package me.plavikrug;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import me.plavikrug.db.DataBaseSource;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DodajMjerenje extends AppCompatActivity {
    private Button btnVrijeme;
    private Button btnDatum;
    private Button btnDelete;
    private TextView txtSec;
    private EditText txtGl;
    private Spinner spin;
    private TextView txtTip;
    private DatePickerDialog.OnDateSetListener datumListener;
    private TimePickerDialog.OnTimeSetListener vrijemeListener;
    private int godina,mjesec,dan,sat,minut;
    protected DataBaseSource dataBaseSource;
    private TextView lblInsulin;
    private EditText txtInsulin;
    private TextView lblKorekcija;
    private EditText txtKorekcija;
    private ImageView imgPozicija;
    private Button btnSubmit;
    private int pozPrijema;
    private String danStr;
    private String mjesecStr;
    private String minStr;
    private String satStr;
    private String fullDatum;
    private float bgnMjerenje;
    private int bgnInsl;
    private int bgnKor;
    int nacinRada, bgnSpin;
    String datum, vrijeme;
    MenuItem menuItem;
    int visinaSlike, sirinaSlike;
    int idTer, idRez;
    int timeChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_mjerenje);
        setTitle("Unos mjerenja");
        txtSec = (TextView) findViewById(R.id.txtSecer);
        txtGl = (EditText) findViewById(R.id.gl_measure);
        btnDatum =(Button) findViewById(R.id.btn_datum);
        btnVrijeme = (Button) findViewById(R.id.btn_vrijeme);
        btnDelete = (Button) findViewById(R.id.deleteMjerenje);
        txtTip = (TextView) findViewById(R.id.lbl_tip_mjer);
        spin = (Spinner) findViewById(R.id.spinner);
        lblInsulin = (TextView) findViewById(R.id.lbl_insulin);
        txtInsulin = (EditText) findViewById(R.id.txt_insulin);
        lblKorekcija = (TextView) findViewById(R.id.lbl_korekcija);
        txtKorekcija = (EditText) findViewById(R.id.txt_korekcija);
        imgPozicija = (ImageView) findViewById(R.id.img_pozicija);
        visinaSlike = imgPozicija.getHeight();
        sirinaSlike = imgPozicija.getWidth();
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        Intent getter = getIntent();
        fullDatum = getter.getStringExtra("full_datum");
        dataBaseSource = new DataBaseSource(DodajMjerenje.this);
        nacinRada = 0;
        timeChanged = 0;

         /*Pravljenje adaptera za spiner*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipovi_mjerenja, android.R.layout.simple_spinner_item);
        //Setovanje drop-down izgleda
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        pozPrijema = 0;

        if(fullDatum!=null){
            nacinRada = 1;
            btnDelete.setVisibility(View.VISIBLE);
            String[] datumVrijeme = fullDatum.split(" ");
            dan = Integer.parseInt(datumVrijeme[0].substring(0,2));
            mjesec = getMjesecFromString(datumVrijeme[0].substring(3,6));
            godina = Integer.parseInt(datumVrijeme[0].substring(7));
            sat = Integer.parseInt(datumVrijeme[1].substring(0,2));
            minut = Integer.parseInt(datumVrijeme[1].substring(3,5));
            setDatumAndVrijeme();
            dataBaseSource.open();
            Cursor getterCursor = dataBaseSource.fetchExactMjerenjeAndTerapija(datum, vrijeme);
            getterCursor.moveToFirst();
            idTer = getterCursor.getInt(5);
            idRez = getterCursor.getInt(6);
            txtGl.setText(getterCursor.getFloat(0)+"");

            if(getterCursor.getInt(1)>0){
                txtInsulin.setText(getterCursor.getInt(1)+"");
            }
            if(getterCursor.getInt(2)>0){
                txtKorekcija.setText(getterCursor.getInt(2)+"");
            }
            pozPrijema = getterCursor.getInt(3);
            spin.setSelection(getterCursor.getInt(4));
            switch(pozPrijema){
                case 1:
                    pozPrijema = 1;
                    imgPozicija.setImageResource(R.drawable.poz1);
                    break;
                case 2:
                    pozPrijema = 2;
                    imgPozicija.setImageResource(R.drawable.poz2);
                    break;
                case 3:
                    pozPrijema = 3;
                    imgPozicija.setImageResource(R.drawable.poz3);
                    break;
                case 0:
                    break;
            }
            dataBaseSource.close();
            bgnMjerenje = Float.parseFloat(txtGl.getText().toString());
            bgnInsl = 0;
            bgnKor = 0;
            if(txtInsulin.getText().toString().length()>0) {
                bgnInsl = Integer.parseInt(txtInsulin.getText().toString());
            }
            if(txtKorekcija.getText().toString().length()>0) {
                bgnKor = Integer.parseInt(txtKorekcija.getText().toString());
            }
            bgnSpin = spin.getSelectedItemPosition();
        }
        else {
            Calendar c = Calendar.getInstance();
            Locale locale = Locale.getDefault();
            godina = c.get(Calendar.YEAR);
            mjesec = c.get(Calendar.MONTH) + 1;
            dan = c.get(Calendar.DAY_OF_MONTH);
            sat = c.get(Calendar.HOUR_OF_DAY);
            minut = c.get(Calendar.MINUTE);
            setDatumAndVrijeme();
        }


        btnDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datumDialog = new DatePickerDialog(DodajMjerenje.this,
                        R.style.Theme_DelegateWindow,
                        datumListener,
                        godina,mjesec-1,dan);
                datumDialog.setTitle("Datum");
                datumDialog.setButton(datumDialog.BUTTON_POSITIVE,"U redu",datumDialog);
                datumDialog.setButton(datumDialog.BUTTON_NEGATIVE,"Otkaži",datumDialog);
                datumDialog.show();
            }
        });

        datumListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                timeChanged = 1;
                godina = year;
                mjesec = month+1;
                dan = dayOfMonth;

                danStr = dan + "";
                mjesecStr = mjesec + "";

                if(dan < 10){
                    danStr = "0" + dan;
                }
                if(mjesec < 10){
                    mjesecStr = "0" + mjesec;
                }

                btnDatum.setText(danStr+"."+mjesecStr+"."+year);
            }
        };
        btnVrijeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog vrijemeDialog = new TimePickerDialog(DodajMjerenje.this,
                        R.style.Theme_DelegateWindow,
                        vrijemeListener,sat,minut,true);
                vrijemeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                vrijemeDialog.setTitle("Vrijeme");
                vrijemeDialog.setButton(vrijemeDialog.BUTTON_POSITIVE,"U redu",vrijemeDialog);
                vrijemeDialog.setButton(vrijemeDialog.BUTTON_NEGATIVE,"Otkaži",vrijemeDialog);
                vrijemeDialog.show();
            }
        });

        vrijemeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeChanged = 1;
                sat = hourOfDay;
                minut = minute;

                satStr = sat+"";
                minStr = minut + "";
                if(sat<10){
                    satStr = "0" + satStr;
                }
                if(minut< 10){
                    minStr = "0" + minStr;
                }

                btnVrijeme.setText(satStr + ":" + minStr);
            }
        };
        //Na klik na određeni dio slike, označava se taj dio tijela
        imgPozicija.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Toast.makeText(DodajMjerenje.this, "Širina: " + imgPozicija.getWidth() + ", Visina: " + imgPozicija.getHeight(), Toast.LENGTH_SHORT).show();
                switch(getPozicijaNakonDodira(event.getX(),event.getY())){
                    case 1:
                        if(pozPrijema == 1){
                            pozPrijema = 0;
                            imgPozicija.setImageResource(R.drawable.pozicije);
                        }
                        else {
                            pozPrijema = 1;
                            imgPozicija.setImageResource(R.drawable.poz1);
                        }
                        break;
                    case 2:
                        if(pozPrijema == 2){
                            pozPrijema = 0;
                            imgPozicija.setImageResource(R.drawable.pozicije);
                        }
                        else {
                            pozPrijema = 2;
                            imgPozicija.setImageResource(R.drawable.poz2);
                        }
                        break;
                    case 3:
                        if(pozPrijema == 3){
                            pozPrijema = 0;
                            imgPozicija.setImageResource(R.drawable.pozicije);
                        }
                        else {
                            pozPrijema = 3;
                            imgPozicija.setImageResource(R.drawable.poz3);
                        }
                        break;
                    case 0:
                        imgPozicija.setImageResource(R.drawable.pozicije);
                        break;
                }
                return false;
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dltIntent = new Intent(DodajMjerenje.this, DeletePopUpWindow.class);
                dltIntent.putExtra("tabela", "Mjerenje");
                dltIntent.putExtra("id", datum + " " + vrijeme);
                dltIntent.putExtra("activity", "GlavnaAktivnost");
                dltIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(dltIntent);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_dodaj_mjeru, menu);
        menuItem = menu.findItem(R.id.add_mjer);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int insulin;
        int korekcija;
        float glukoza;
        String msg = "";
        int tip = spin.getSelectedItemPosition();
        if(item.getItemId()==R.id.add_mjer) {
            if (txtGl.getText().toString().length() == 0 && txtInsulin.getText().toString().length() == 0 && txtKorekcija.getText().toString().length() == 0) {
                msg = "Nijeste unijeli nivo šećera u krvi, ili količinu insulina.";
                Toast.makeText(DodajMjerenje.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                dataBaseSource.open();
                String dan = btnDatum.getText().toString();
                String vrijeme = btnVrijeme.getText().toString();
                if (nacinRada == 0) {
                    if (txtGl.getText().toString().length() > 0) {
                        dataBaseSource.insertGlukoza(dan + " " + vrijeme, Float.parseFloat(txtGl.getText().toString()), tip);
                        msg = "Uspješno zabilježen rezultat";
                    }

                    if (txtInsulin.getText().toString().length() > 0 || txtKorekcija.getText().toString().length() > 0) {
                        if (txtInsulin.getText().toString().length() > 0) {
                            insulin = Integer.parseInt(txtInsulin.getText().toString());
                        } else {
                            insulin = 0;
                        }
                        if (txtKorekcija.getText().toString().length() > 0) {
                            korekcija = Integer.parseInt(txtKorekcija.getText().toString());
                        } else {
                            korekcija = 0;
                        }
                        dataBaseSource.insertTerapija(insulin, korekcija, dan, vrijeme, pozPrijema);
                        msg = "Uspješno zabilježen rezultat";
                    }

                } else {
                    if (Float.parseFloat(txtGl.getText().toString()) > bgnMjerenje ||
                            Float.parseFloat(txtGl.getText().toString()) < bgnMjerenje ||
                            bgnInsl >= 0 ||
                            bgnKor >= 0 ||
                            spin.getSelectedItemPosition() > 0 ||
                            spin.getSelectedItemPosition() < 0 ||
                            timeChanged > 0
                            ) {
                        if (txtGl.getText().toString().length() > 0) {
                            glukoza = Float.parseFloat(txtGl.getText().toString());
                            if (txtInsulin.getText().toString().length() > 0) {
                                insulin = Integer.parseInt(txtInsulin.getText().toString());
                            } else {
                                insulin = 0;
                            }
                            if (txtKorekcija.getText().toString().length() > 0) {
                                korekcija = Integer.parseInt(txtKorekcija.getText().toString());
                            } else {
                                korekcija = 0;
                            }
                            dataBaseSource.updateMjerenje(glukoza, insulin, korekcija, pozPrijema, tip, btnDatum.getText().toString() + " " + btnVrijeme.getText().toString(), idRez, idTer);
                            if (idTer == 0) {
                                if (txtInsulin.getText().toString().length() > 0 || txtKorekcija.getText().toString().length() > 0) {
                                    if (txtInsulin.getText().toString().length() > 0) {
                                        insulin = Integer.parseInt(txtInsulin.getText().toString());
                                    } else {
                                        insulin = 0;
                                    }
                                    if (txtKorekcija.getText().toString().length() > 0) {
                                        korekcija = Integer.parseInt(txtKorekcija.getText().toString());
                                    } else {
                                        korekcija = 0;
                                    }
                                    dataBaseSource.insertTerapija(insulin, korekcija, btnDatum.getText().toString(), btnVrijeme.getText().toString(), pozPrijema);
                                }
                            }
                            msg = "Podatak uspješno ažuriran";
                        }

                    }
                }

                dataBaseSource.close();
                Intent goFirst = new Intent(DodajMjerenje.this, GlavnaAktivnost.class);
                goFirst.putExtra("usp_poruka", msg);
                startActivity(goFirst);
                finish();
                return true;
            }
        }
		else if(item.getItemId()==android.R.id.home){
			onBackPressed();
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent goFirst = new Intent(DodajMjerenje.this, GlavnaAktivnost.class);
        startActivity(goFirst);
        finish();
    }

    private int getPozicijaNakonDodira(float x, float y){
        int poz = 0;
        Rect stomak = new Rect(sirina(100),visina(220),sirina(250),visina(340));
        Rect noga1 = new Rect(sirina(80),visina(360),sirina(130),visina(514));
        Rect noga2 = new Rect(sirina(200),visina(360),sirina(250),visina(514));
        Rect ruka1 = new Rect(sirina(300),visina(150),sirina(380),visina(250));
        Rect ruka2 = new Rect(sirina(600),visina(150),sirina(680),visina(250));
        Rect noga3 = new Rect(sirina(350),visina(380),sirina(450),visina(514));
        Rect noga4 = new Rect(sirina(500),visina(380),sirina(600),visina(514));
        if(stomak.contains((int)x,(int)y)){
            poz = 1;
        }
        else if(noga1.contains((int)x,(int)y) || noga2.contains((int)x,(int)y)){
            poz = 2;
        }
        else if(noga3.contains((int)x,(int)y) || noga4.contains((int)x,(int)y)){
            poz = 2;
        }
        else if(ruka1.contains((int)x,(int)y) || ruka2.contains((int)x,(int)y)){
            poz = 3;
        }

        return poz;
    }

    private int sirina(int broj){
        float num = ((float)broj / (float)680)*(float)imgPozicija.getWidth();
        //Toast.makeText(DodajMjerenje.this, "X: " +  imgPozicija.getWidth(), Toast.LENGTH_SHORT).show();
        return (int)num;
    }

    private int visina(int broj){
        float num = ((float)broj / (float)514)*(float)imgPozicija.getHeight();
        return (int)num;
    }

    private int getMjesecFromString(String mjStr){
        int mjesec = 0;
        if(mjStr.equals("Jan")){
            mjesec = 1;
        }
        if(mjStr.equals("Feb")){
            mjesec = 2;
        }
        if(mjStr.equals("Mar")){
            mjesec = 3;
        }
        if(mjStr.equals("Apr")){
            mjesec = 4;
        }
        if(mjStr.equals("Maj")){
            mjesec = 5;
        }
        if(mjStr.equals("Jun")){
            mjesec = 6;
        }
        if(mjStr.equals("Jul")){
            mjesec = 7;
        }
        if(mjStr.equals("Avg")){
            mjesec = 8;
        }
        if(mjStr.equals("Sep")){
            mjesec = 9;
        }
        if(mjStr.equals("Okt")){
            mjesec = 10;
        }
        if(mjStr.equals("Nov")){
            mjesec = 11;
        }
        if(mjStr.equals("Dec")){
            mjesec = 12;
        }
        return mjesec;
    }

    private void setDatumAndVrijeme(){
        danStr = dan + "";
        mjesecStr = mjesec + "";

        if (dan < 10) {
            danStr = "0" + dan;
        }
        if (mjesec < 10) {
            mjesecStr = "0" + mjesec;
        }
        datum = danStr + "." + mjesecStr + "." + godina;
        btnDatum.setText(datum);

        satStr = sat + "";
        minStr = minut + "";

        if (sat < 10) {
            satStr = "0" + satStr;
        }
        if (minut < 10) {
            minStr = "0" + minStr;
        }
        vrijeme = satStr + ":" + minStr;

        btnVrijeme.setText(vrijeme);
    }
}
