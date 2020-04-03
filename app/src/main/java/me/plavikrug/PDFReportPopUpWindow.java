package me.plavikrug;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;

import me.plavikrug.db.DataBaseSource;
import me.plavikrug.model.PDFParameters;
import me.plavikrug.pdf.PDFReportGenerator;
import me.plavikrug.utils.UniversalTextWatcher;

public class PDFReportPopUpWindow extends Activity implements AsyncResponse {
    public static Context context;
    String fullDatum;
    EditText txtOdDan;
    EditText txtOdMjesec;
    EditText txtOdGodina;
    EditText txtDoDan;
    EditText txtDoMjesec;
    EditText txtDoGodina;
    Button btnPokreniIzvjestaj;
    DataBaseSource dbSource;
    Calendar kalOd;
    Calendar kalDo;
    private int keyboardPressed;
    private PDFReportGenerator pdfGen = new PDFReportGenerator();


    int danOd, mjesecOd, godinaOd, danDo, mjesecDo, godinaDo;
    int REQUEST_STORAGE = 2028;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_report_pop_up_window);
        context = PDFReportPopUpWindow.this;
        //Podesavanje velicine prozora
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.8), (int) (height*0.5));

        dbSource = new DataBaseSource(PDFReportPopUpWindow.this);
        pdfGen.delegate = this;

        txtOdDan = (EditText) findViewById(R.id.txtOdDan);
        txtOdMjesec = (EditText) findViewById(R.id.txtOdMjesec);
        txtOdGodina = (EditText) findViewById(R.id.txtOdGodina);
        txtDoDan = (EditText) findViewById(R.id.txtDoDan);
        txtDoMjesec = (EditText) findViewById(R.id.txtDoMjesec);
        txtDoGodina = (EditText) findViewById(R.id.txtDoGodina);
        btnPokreniIzvjestaj = (Button) findViewById(R.id.btnPokreniIzvjestaj);

        Calendar kal = Calendar.getInstance();
        danOd = 1;
        mjesecOd = kal.get(Calendar.MONTH) + 1;
        godinaOd = kal.get(Calendar.YEAR);

        danDo = kal.getActualMaximum(Calendar.DAY_OF_MONTH);
        mjesecDo = kal.get(Calendar.MONTH) + 1;
        godinaDo = kal.get(Calendar.YEAR);

        kalOd = Calendar.getInstance();
        kalOd.set(Calendar.DAY_OF_MONTH, danOd);

        kalDo = Calendar.getInstance();
        kalDo.set(Calendar.DAY_OF_MONTH, danDo);

        txtOdDan.setText(finesaDatum(danOd));
        txtOdMjesec.setText(finesaDatum(mjesecOd));
        txtOdGodina.setText(godinaOd+"");
        txtDoDan.setText(danDo+"");
        txtDoMjesec.setText(finesaDatum(mjesecDo));
        txtDoGodina.setText(godinaDo+"");

        btnPokreniIzvjestaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbSource.open();
                String danOdParam = txtOdGodina.getText().toString() + txtOdMjesec.getText().toString() + txtOdDan.getText().toString();
                String danDoParam = txtDoGodina.getText().toString() + txtDoMjesec.getText().toString() + txtDoDan.getText().toString();

                Cursor podaci = dbSource.getPDFReportMjerenja(danOdParam,danDoParam);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    if (ContextCompat.checkSelfPermission(PDFReportPopUpWindow.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(PDFReportPopUpWindow.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        ActivityCompat.requestPermissions(PDFReportPopUpWindow.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_STORAGE);
                    }
                    else {
                        PdfDocument doc = new PdfDocument();

                        Resources resursi = getResources();
                        //SharedPreferences korisnickiPodaci = getActivity().getSharedPreferences("PODACI", Context.MODE_PRIVATE);
                        PDFParameters pdfParameters = new PDFParameters(doc, resursi, podaci, danOdParam, danDoParam);
                        pdfGen.execute(pdfParameters);
                    }
                }

                else{
                    Toast.makeText(getApplicationContext(), "Žao nam je, ali izvještaj nije moguće pokrenuti na ovom uređaju.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        setTextWatchers();
        setFocusChangers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pdfGen = new PDFReportGenerator();
        pdfGen.delegate = this;

    }

    private String finesaDatum(int vrijednost){
        String povrat = "";
        if(vrijednost < 10){
            povrat = "0" + vrijednost;
        }
        else{
            povrat = vrijednost + "";
        }

        return povrat;
    }

    private void setTextWatchers(){
        txtOdDan.addTextChangedListener(new UniversalTextWatcher(txtOdDan, kalOd, 1));
        txtDoDan.addTextChangedListener(new UniversalTextWatcher(txtDoDan, kalDo, 1));
        txtOdMjesec.addTextChangedListener(new UniversalTextWatcher(txtOdMjesec, kalOd, 2));
        txtDoMjesec.addTextChangedListener(new UniversalTextWatcher(txtDoMjesec, kalDo, 2));
    }

    private void setFocusChangers(){
        txtOdDan.setOnFocusChangeListener(focusChanger(txtOdDan));
        txtOdMjesec.setOnFocusChangeListener(focusChanger(txtOdMjesec));
        txtOdGodina.setOnFocusChangeListener(focusChanger(txtOdGodina));
        txtDoDan.setOnFocusChangeListener(focusChanger(txtDoDan));
        txtDoMjesec.setOnFocusChangeListener(focusChanger(txtDoMjesec));
        txtOdGodina.setOnFocusChangeListener(focusChanger(txtOdGodina));
    }

    private View.OnFocusChangeListener focusChanger(final EditText editText){
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(editText.getId() == R.id.txtOdDan || editText.getId() == R.id.txtOdMjesec || editText.getId() == R.id.txtOdGodina){
                        if(view.getId() == R.id.txtOdDan) {
                            if(editText.getText().toString().length() == 0){
                                editText.setText(finesaDatum(kalOd.get(Calendar.DAY_OF_MONTH)));
                            }
                            else {
                                kalOd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(editText.getText().toString()));
                            }
                        }
                        if(view.getId() == R.id.txtOdMjesec) {
                            if(editText.getText().toString().length() == 0){
                                editText.setText(finesaDatum(kalOd.get(Calendar.MONTH)+1));
                            }
                            else {
                                kalOd.set(Calendar.MONTH, Integer.parseInt(editText.getText().toString())-1);
                            }
                        }
                        if(view.getId() == R.id.txtOdGodina) {
                            if(editText.getText().toString().length() == 0){
                                editText.setText(finesaDatum(kalOd.get(Calendar.YEAR)));
                            }
                            else {
                                kalOd.set(Calendar.YEAR, Integer.parseInt(editText.getText().toString()));
                            }
                        }
                        if(kalOd.getTimeInMillis() > kalDo.getTimeInMillis()){
                            kalDo.set(Calendar.YEAR, kalOd.get(Calendar.YEAR));
                            kalDo.set(Calendar.MONTH, kalOd.get(Calendar.MONTH));
                            kalDo.add(Calendar.MONTH, -1);
                            kalDo.set(Calendar.DAY_OF_MONTH, kalDo.getActualMaximum(Calendar.DAY_OF_MONTH));
                            txtDoDan.setText(finesaDatum(kalDo.get(Calendar.DAY_OF_MONTH)));
                            txtDoMjesec.setText(finesaDatum(kalDo.get(Calendar.MONTH)+1));
                            txtDoGodina.setText(kalDo.get(Calendar.YEAR)+"");
                        }
                    }
                    else {
                        // odustalo se od nekog od Do-polja
                        if(view.getId() == R.id.txtDoDan) {
                            kalDo.set(Calendar.DAY_OF_MONTH, Integer.parseInt(editText.getText().toString()));
                        }
                        if(view.getId() == R.id.txtDoMjesec) {
                            kalDo.set(Calendar.MONTH, Integer.parseInt(editText.getText().toString())-1);
                            if(kalDo.get(Calendar.DAY_OF_MONTH) > kalDo.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                kalDo.set(Calendar.DAY_OF_MONTH, kalDo.getActualMaximum(Calendar.DAY_OF_MONTH));
                                txtDoDan.setText(kalDo.get(Calendar.DAY_OF_MONTH));
                            }
                        }
                        if(view.getId() == R.id.txtDoGodina) {
                            kalDo.set(Calendar.YEAR, Integer.parseInt(editText.getText().toString()));
                        }
                        if(kalOd.getTimeInMillis() > kalDo.getTimeInMillis()){
                            kalOd.set(Calendar.YEAR, kalDo.get(Calendar.YEAR));
                            kalOd.set(Calendar.MONTH, kalDo.get(Calendar.MONTH));
                            kalOd.set(Calendar.DAY_OF_MONTH, kalDo.getActualMinimum(Calendar.DAY_OF_MONTH));
                            txtOdDan.setText(finesaDatum(kalOd.get(Calendar.DAY_OF_MONTH)));
                            txtOdMjesec.setText(finesaDatum(kalOd.get(Calendar.MONTH)+1));
                            txtOdGodina.setText(kalOd.get(Calendar.YEAR)+"");
                        }
                    }
                }
            }
        };

        return focusChangeListener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("VUK", "Kod: " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void processFinish(String[] output) throws JSONException {
        Log.d("OK","Nije OK");
    }

    @Override
    public void processFinish(Uri uri) throws IOException {
        if(uri != null){
            Intent viewPdf = new Intent(Intent.ACTION_VIEW);
            viewPdf.setDataAndType(uri, "application/pdf");
            viewPdf.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            viewPdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            viewPdf.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            if(viewPdf.resolveActivity(getPackageManager()) != null) {
                startActivity(viewPdf);
            }
        }
    }
}
