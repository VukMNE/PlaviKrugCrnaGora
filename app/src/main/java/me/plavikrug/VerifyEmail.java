package me.plavikrug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Vuk on 26.11.2017..
 */

public class VerifyEmail extends AppCompatActivity implements AsyncResponse {
    private SamoupravniWebProgram swp = new SamoupravniWebProgram();
    private SamoupravniWebProgram swp2 = new SamoupravniWebProgram();
    private SharedPreferences podaci;
    private DataBaseSource dbSource;
    private EditText txtBackUpCode;
    private Button btnSubmitCode;
    private TextView lblUcitavnjeBackup, lblBackUpCodeText;
    private ProgressBar backUpRecieveProgress;
    private Intent recIntent;
    ColorStateList oldColors;
    String vCode;
    boolean codeTrue;
    int progress;
    int status;
    String json;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        dbSource = new DataBaseSource(this);
        recIntent = getIntent();
        swp.delegate = this;
        swp2.delegate = this;
        txtBackUpCode = (EditText) findViewById(R.id.txtBackUpCode);
        btnSubmitCode = (Button) findViewById(R.id.btnSubmitCode);
        lblUcitavnjeBackup = (TextView) findViewById(R.id.lblUcitavnjeBackup);
        lblBackUpCodeText = (TextView) findViewById(R.id.lblBackUpCodeText);
        backUpRecieveProgress = (ProgressBar) findViewById(R.id.backUpRecieveProgress);
        backUpRecieveProgress.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        oldColors =  lblUcitavnjeBackup.getTextColors();
        podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
        editor = podaci.edit();
        vCode = podaci.getString("vCode","");
        status = podaci.getInt("status",0);
        //lblBackUpCodeText.setText(vCode);
        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    if(status == 1 || (txtBackUpCode.getText().toString().length()> 0 && vCode.equals(txtBackUpCode.getText().toString()))) {
                        if(txtBackUpCode.getText().toString().length()> 0 && vCode.equals(txtBackUpCode.getText().toString())){
                            editor.putInt("status",1);
                            editor.commit();
                        }
                            if(recIntent.getIntExtra("opcija",0)==1){
                            Intent backUpIntent = new Intent(VerifyEmail.this, DoBackUp.class);
                            startActivity(backUpIntent);
                        }
                        else if(recIntent.getIntExtra("opcija",0)==2) {
                            lblUcitavnjeBackup.setTextColor(oldColors);
                            lblUcitavnjeBackup.setText(R.string.lblUcitavnjeBackup);
                            lblUcitavnjeBackup.setVisibility(View.VISIBLE);
                            swp2.execute(podaci.getInt("id", 0) + "===" + podaci.getString("email", "") + "===" + vCode);
                            backUpRecieveProgress.setVisibility(View.VISIBLE);
                            while (progress < 30) {
                                progress++;
                                android.os.SystemClock.sleep(30);
                                backUpRecieveProgress.setProgress(progress);
                            }

                        }

                    } else {
                        lblUcitavnjeBackup.setVisibility(View.VISIBLE);
                        lblUcitavnjeBackup.setTextColor(Color.RED);
                        lblUcitavnjeBackup.setText("Netačan kod, pokušajte ponovo");
                    }
                }
                else{
                    //Nema interneta
                    AlertDialog.Builder builder = new AlertDialog.Builder(VerifyEmail.this);
                    builder.setTitle("Nema interneta");
                    builder.setMessage(R.string.msgNoInternet);
                    builder.setIcon(R.mipmap.ic_offline);
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            }
        });

        if(podaci.getString("vCode","a").length() < 7){
                swp.execute(podaci.getInt("id", 0)+"==="+podaci.getString("email", ""),
                       "register",
                        podaci.getString("email", "")+"==="+podaci.getString("pol", "")+"==="+podaci.getInt("tip", 0)
                                +"==="+podaci.getString("datum_rodj", "")+"==="+podaci.getString("datum_d", ""));

        }
        else{
            if(status == 1) {
                btnSubmitCode.setVisibility(View.GONE);
                lblBackUpCodeText.setVisibility(View.GONE);
                txtBackUpCode.setVisibility(View.GONE);
                btnSubmitCode.performClick();
            }
        }
        // ovo mo\da budem koristio za novo slanje swp.execute(podaci.getInt("id", 0)+"==="+podaci.getString("email", "")+"==="+"000");




    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, BackUpActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void processFinish(String[] output)  throws JSONException {
        lblUcitavnjeBackup.setText(output[0]);
        if(output.length> 1) {
            //dobili smo kod
                vCode = output[1];
                editor.putInt("id", Integer.parseInt(output[0]));
                editor.putString("vCode", output[1]);
                editor.commit();
        }
        else {
            //dobili smo json back-up

                while (progress < 60) {
                    progress++;
                    android.os.SystemClock.sleep(30);
                    backUpRecieveProgress.setProgress(progress);
                }
                json = output[0];
                dbSource.open();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dbSource.parseJsonIntoDb(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dbSource.close();
                        while (progress < 100)

                        {
                            progress++;
                            android.os.SystemClock.sleep(30);
                            backUpRecieveProgress.setProgress(progress);
                        }
                        android.os.SystemClock.sleep(500);
                        onBackPressed();
                    }


                }).start();
            lblUcitavnjeBackup.setText("Podaci uspješno učitani");
                /*android.os.SystemClock.sleep(500);
                onBackPressed();*/

        }
    }

    @Override
    public void processFinish(Uri uri) throws IOException {
        Log.d("OK", "nije OK");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
