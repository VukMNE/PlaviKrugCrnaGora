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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Srdjan on 26.7.2017..
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblpol;
    private TextView lbltip;
    private TextView lblHelp;
    private EditText txtemail;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private Spinner selectTypeDiabetes;
    private TextView lblWhyRegData;
    private Button btnConf;
    private Button btnSkip;
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
        setContentView(R.layout.activity_register);

        godina_r = 1990;
        mjesec_r = 1;
        dan_r = 1;
        Calendar calendar = Calendar.getInstance();
        godina_d = calendar.get(Calendar.YEAR);
        mjesec_d = calendar.get(Calendar.MONTH) + 1;
        dan_d = calendar.get(Calendar.DAY_OF_MONTH);


        checkIme = 0;
        checkPrez = 0;
        checkDate1 = 0;
        checkDate2 = 0;


        txtemail = (EditText) findViewById(R.id.txt_reg_email);
        lblpol = (TextView) findViewById(R.id.lbl_reg_gender);
        lblHelp = (TextView) findViewById(R.id.lbl_reg_help);
        radioMale = (RadioButton) findViewById(R.id.gender_male);
        radioFemale = (RadioButton) findViewById(R.id.gender_female);
        lbltip = (TextView) findViewById(R.id.lbl_tip_diabetes);
        selectTypeDiabetes = (Spinner) findViewById(R.id.spinner_tip_diabetes);
        lblDateOfBirth = (TextView) findViewById(R.id.lbl_date_of_birth);
        lblDateDiabetes = (TextView) findViewById(R.id.lbl_date_of_birth);

        btnDateOfBirth = (Button) findViewById(R.id.btn_date_of_birth);
        btnDateDiabetes = (Button) findViewById(R.id.btn_date_diabetes);

        String danStr1, mjesecStr1;
        String danStr2, mjesecStr2;

        if(dan_r < 10) {
            danStr1 = "0" + dan_r;
        } else {
            danStr1 = dan_r + "";
        }

        if(dan_d < 10) {
            danStr2 = "0" + dan_d;
        } else {
            danStr2 = dan_d + "";
        }

        if(mjesec_r < 10) {
            mjesecStr1 = "0" + mjesec_r;
        } else {
            mjesecStr1 = mjesec_r + "";
        }

        if(mjesec_d < 10) {
            mjesecStr2 = "0" + mjesec_d;
        } else {
            mjesecStr2 = mjesec_d + "";
        }

        btnDateOfBirth.setText(danStr1+"."+mjesecStr1+"."+godina_r);
        btnDateDiabetes.setText(danStr2+"."+mjesecStr2+"."+godina_d);

        btnConf = (Button) findViewById(R.id.btn_reg_confirm);
        btnSkip = (Button) findViewById(R.id.btn_reg_skip);

        /*Setting up the adapter for spiner*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_diabetes_types, android.R.layout.simple_spinner_dropdown_item);
        //Setting up the drop-down view
        selectTypeDiabetes.setAdapter(adapter);

        btnConf.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dateBirthDialog = new DatePickerDialog(RegisterActivity.this,
                        R.style.Theme_DelegateWindow,
                        dateBirthListener,
                        godina_r,mjesec_r-1,dan_r);
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
                String danStr, mjesecStr;
                danStr = dan_r + "";
                mjesecStr = mjesec_r + "";

                if(dan_r < 10) {
                    danStr = "0" + dan_r;
                }
                if(mjesec_r < 10) {
                    mjesecStr = "0" + mjesec_r;
                }

                btnDateOfBirth.setText(danStr+"."+mjesecStr+"."+year);
                checkDate1 = 1;

            }
        };

        btnDateDiabetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDiabetesDialog = new DatePickerDialog(RegisterActivity.this,
                        R.style.Theme_DelegateWindow,
                        dateDiabetesListener,
                        godina_d,mjesec_d-1,dan_d);
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
                String danStr, mjesecStr;
                danStr = dan_d + "";
                mjesecStr = mjesec_d + "";

                if(dan_d < 10) {
                    danStr = "0" + dan_d;
                }
                if(mjesec_d < 10) {
                    mjesecStr = "0" + mjesec_d;
                }

                btnDateDiabetes.setText(danStr+"."+mjesecStr+"."+year);
                checkDate2 = 1;
            }
        };

        lblHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent popUpIntent = new Intent(RegisterActivity.this, RegHelpPopUpActivity.class);
                startActivity(popUpIntent);
            }
        });



    }

    @Override
    public void onClick(View v) {
        SharedPreferences podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
        SharedPreferences.Editor editor = podaci.edit();
        switch(v.getId()) {
            case R.id.btn_reg_confirm:

                String pol = "";
                String email = txtemail.getText().toString();

                int tip = 0;

                if(!radioMale.isChecked() && !radioFemale.isChecked()) {
                    Toast.makeText(this, getString(R.string.gender_not_chosen), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (radioFemale.isChecked()) {
                    pol = "Ženski";
                } else {
                    pol = "Muški";
                }

                tip = selectTypeDiabetes.getSelectedItemPosition();
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
                    Intent int2 = new Intent(RegisterActivity.this, GlavnaAktivnost.class);
                    startActivity(int2);
                    finish();
                }
                else{
                    Toast.makeText(this, "Email nije validan, ili ostavite prazno polje ili unesite pravilnu email adresu", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.btn_reg_skip:
                editor.putBoolean("isFirstRun", false).commit();
                Intent int2 = new Intent(RegisterActivity.this, GlavnaAktivnost.class);
                startActivity(int2);
                finish();
                break;
        }



    }

}
