package me.plavikrug;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Srdjan on 26.7.2017..
 */

public class PrviPut extends AppCompatActivity implements View.OnClickListener {

    private TextView lblpol;
    private TextView lbltip;
    private EditText txtemail;
    private EditText txtime;
    private EditText txtprezime;
    private Switch swPol;
    private Switch swTip;
    private Button btnConf;
    private TextView lblDateOfBirth;
    private TextView lblDateDiabetes;
    private  Button btnDateOfBirth;
    private Button btnDateDiabetes;
    private DatePickerDialog.OnDateSetListener dateBirthListener;
    private DatePickerDialog.OnDateSetListener dateDiabetesListener;
    int godina_r, mjesec_r, dan_r, godina_d, mjesec_d, dan_d;
    int checkIme;
    int checkPrez;
    int checkDate1;
    int checkDate2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prviput);

        godina_r = 1990;
        mjesec_r = 1;
        dan_r = 1;
        godina_d = 2016;
        mjesec_d = 9;
        dan_d = 1;

        checkIme = 0;
        checkPrez = 0;
        checkDate1 = 0;
        checkDate2 = 0;


        txtemail = (EditText) findViewById(R.id.txt_email);
        lblpol = (TextView) findViewById(R.id.lbl_pol);
        lbltip = (TextView) findViewById(R.id.lbl_tip);
        lblDateOfBirth = (TextView) findViewById(R.id.lbl_datum_rodjenja);
        lblDateDiabetes = (TextView) findViewById(R.id.lbl_datum_dijabetisa);

        btnDateOfBirth = (Button) findViewById(R.id.date_birth);
        btnDateDiabetes = (Button) findViewById(R.id.date_diabetes);

        btnDateOfBirth.setText(dan_r+"."+mjesec_r+"."+godina_r);
        btnDateDiabetes.setText(dan_r+"."+mjesec_d+"."+godina_d);

        /*txtime = (EditText) findViewById(R.id.txt_ime);
        txtprezime = (EditText) findViewById(R.id.txt_prezime);*/

        swPol = (Switch) findViewById(R.id.switch_pol);
        swTip = (Switch) findViewById(R.id.switch_tip);



        btnConf = (Button) findViewById(R.id.btn_conf_firstTimer);

        btnConf.setOnClickListener(this);
        btnDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dateBirthDialog = new DatePickerDialog(PrviPut.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateBirthListener,
                        godina_r,mjesec_r-1,dan_r);
                dateBirthDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateBirthDialog.setTitle("Datum rođenja");
                dateBirthDialog.setButton(dateBirthDialog.BUTTON_POSITIVE,"U redu",dateBirthDialog);
                dateBirthDialog.setButton(dateBirthDialog.BUTTON_NEGATIVE,"Otkaži",dateBirthDialog);
                dateBirthDialog.show();
            }
        });


        dateBirthListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                godina_r = year;
                mjesec_r = month+1;
                dan_r = dayOfMonth;
                btnDateOfBirth.setText(dayOfMonth+"."+(month+1)+"."+year);
                checkDate1 = 1;

            }
        };

        btnDateDiabetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDiabetesDialog = new DatePickerDialog(PrviPut.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateDiabetesListener,
                        godina_d,mjesec_d-1,dan_d);
                dateDiabetesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDiabetesDialog.setTitle("Od kojeg datuma imate dijabetis?");
                dateDiabetesDialog.setButton(dateDiabetesDialog.BUTTON_POSITIVE,"U redu",dateDiabetesDialog);
                dateDiabetesDialog.setButton(dateDiabetesDialog.BUTTON_NEGATIVE,"Otkaži",dateDiabetesDialog);
                dateDiabetesDialog.show();
            }
        });

        dateDiabetesListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                godina_d = year;
                mjesec_d = month+1;
                dan_d = dayOfMonth;
                btnDateDiabetes.setText(dayOfMonth+"."+(month+1)+"."+year);
                checkDate2 = 1;
            }
        };



    }

    @Override
    public void onClick(View v) {
        SharedPreferences podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
        SharedPreferences.Editor editor = podaci.edit();
        String pol = "";
        String email = txtemail.getText().toString();

        int tip = 0;

        if (swPol.isChecked()) {
            pol = "Ženski";
        } else {
            pol = "Muški";
        }
        if (swTip.isChecked()) {
            tip = 1;
        } else {
            tip = 2;
        }
        /*editor.putString("ime",txtime.getText().toString());
        editor.putString("prezime",txtprezime.getText().toString());*/

            editor.putString("pol", pol);
            editor.putInt("tip", tip);
            editor.putInt("notifikacija_id", 27);
            editor.putInt("status", 0);
            editor.putString("datum_rodj", btnDateOfBirth.getText().toString());
            editor.putString("datum_d", btnDateDiabetes.getText().toString());


        if(SetEmailPopUpWindow.validate(email)|| email.length()==0) {
            if(email.length()>0) {
                editor.putString("email", txtemail.getText().toString());
            }
            editor.putBoolean("isFirstRun", false).commit();
            Intent int2 = new Intent(PrviPut.this, GlavnaAktivnost.class);
            startActivity(int2);
            finish();
        }
        else{
            Toast.makeText(this, "Email nije validan, ili ostavite prazno polje ili unesite pravilnu email adresu", Toast.LENGTH_LONG).show();
        }

    }

}
