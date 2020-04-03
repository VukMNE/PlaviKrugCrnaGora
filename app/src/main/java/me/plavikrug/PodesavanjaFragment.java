package me.plavikrug;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;

import static android.content.Context.MODE_PRIVATE;
import static me.plavikrug.SetEmailPopUpWindow.VALID_EMAIL_ADDRESS_REGEX;

/**
 * Created by Vuk on 10.8.2017..
 */

public class PodesavanjaFragment extends Fragment {

    SharedPreferences podaci;
    SharedPreferences.Editor editor;

    TextView lblEmail;
    TextView lblSetPol;
    TextView lblSetTip;
    TextView lblSetDatumRodj;
    TextView lblSetDatumDia;

    Button btnConf;
    Button btnCancel;


    EditText txtSetEmail;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private Spinner selectTypeDiabetes;

    Button btnSetDatumRodj;
    Button btnSetDatumDia;
    Button btnGoToUpdateSettings;
    ImageView imgEdit;

    DatePickerDialog.OnDateSetListener datumRodjListener;
    DatePickerDialog.OnDateSetListener dateDiabetesListener;
    int godina_r, mjesec_r, dan_r, godina_d, mjesec_d, dan_d;

    Animation fadeOutAnimation, fadeInAnimation;

    private String TAG = "AJMO_PLAVI";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podesavanja,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Poveyivanje layouta sa kontroloma na ekranu
        podaci = this.getActivity().getSharedPreferences("PODACI", MODE_PRIVATE);
        editor = podaci.edit();

        btnGoToUpdateSettings = (Button) view.findViewById(R.id.btnUpdateSettings);
        imgEdit = (ImageView) view.findViewById(R.id.li_set_edit_img);
        lblEmail = (TextView) view.findViewById(R.id.lbl_set_email);
        txtSetEmail = (EditText) view.findViewById(R.id.txt_set_email);
        lblSetPol = (TextView) view.findViewById(R.id.lbl_set_gender);
        lblSetTip = (TextView) view.findViewById(R.id.lbl_tip_diabetes);
        lblSetDatumRodj = (TextView) view.findViewById(R.id.lbl_date_of_birth);
        lblSetDatumDia = (TextView) view.findViewById(R.id.lbl_date_diabetes);

        radioMale = (RadioButton) view.findViewById(R.id.gender_male);
        radioFemale = (RadioButton) view.findViewById(R.id.gender_female);

        btnConf = (Button) view.findViewById(R.id.btn_set_confirm);
        btnCancel = (Button) view.findViewById(R.id.btn_set_skip);

        selectTypeDiabetes = (Spinner) view.findViewById(R.id.spinner_tip_diabetes);

        /*Setting up the adapter for spiner*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.arr_diabetes_types, android.R.layout.simple_spinner_dropdown_item);
        //Setting up the drop-down view
        selectTypeDiabetes.setAdapter(adapter);


        btnSetDatumRodj = (Button) view.findViewById(R.id.btn_date_of_birth);
        btnSetDatumDia = (Button) view.findViewById(R.id.btn_date_diabetes);

        //Upisivanje ranije sačuvanih osnovnih podataka o korisniku

        /*txtSetIme.setText(podaci.getString("ime",""));
        txtSetPrezime.setText(podaci.getString("prezime",""));*/
        txtSetEmail.setText(podaci.getString("email",""));
        boolean pol,tip;

        if(podaci.getString("pol","").equals("Muški")){
            radioMale.setChecked(true);
            radioFemale.setChecked(false);
        }
        else{
            radioMale.setChecked(false);
            radioFemale.setChecked(true);
        }

        selectTypeDiabetes.setSelection(podaci.getInt("tip", 0));

        btnSetDatumRodj.setText(podaci.getString("datum_rodj","01.01.1990"));
        btnSetDatumDia.setText(podaci.getString("datum_d","01.09.2016"));

        Log.d(TAG, "Datum rodjenja: " +  podaci.getString("datum_rodj","01.01.1990"));
        Log.d(TAG, "Datum dijabetes: " +  podaci.getString("datum_d","01.09.2016"));


        dan_r = Integer.parseInt(podaci.getString("datum_rodj","01.01.1990").substring(0,2));
        mjesec_r = Integer.parseInt(podaci.getString("datum_rodj","01.01.1990").substring(3,5));
        godina_r = Integer.parseInt(podaci.getString("datum_rodj","01.01.1990").substring(6));

        dan_d = Integer.parseInt(podaci.getString("datum_d","01.01.1990").substring(0,2));
        mjesec_d = Integer.parseInt(podaci.getString("datum_d","01.01.1990").substring(3,5));
        godina_d = Integer.parseInt(podaci.getString("datum_d","01.01.1990").substring(6));


        //Omogucavanja odabira datuma rodjenja
        btnSetDatumRodj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dateBirthDialog = new DatePickerDialog(getContext(),
                        R.style.Theme_DelegateWindow,
                        datumRodjListener,
                        godina_r,mjesec_r-1,dan_r);
                dateBirthDialog.setTitle("Datum rođenja");
                dateBirthDialog.setButton(dateBirthDialog.BUTTON_POSITIVE,"U redu",dateBirthDialog);
                dateBirthDialog.setButton(dateBirthDialog.BUTTON_NEGATIVE,"Otkaži",dateBirthDialog);
                dateBirthDialog.show();
            }
        });

        datumRodjListener = new DatePickerDialog.OnDateSetListener() {
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

                btnSetDatumRodj.setText(danStr+"."+mjesecStr+"."+year);

            }
        };

        btnSetDatumDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDiabetesDialog = new DatePickerDialog(getContext(),
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

                btnSetDatumDia.setText(danStr+"."+mjesecStr+"."+year);
            }
        };


        fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        fadeInAnimation.setDuration(500);
        fadeOutAnimation.setDuration(500);



        btnGoToUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioMale.setEnabled(true);
                radioFemale.setEnabled(true);
                selectTypeDiabetes.setEnabled(true);
                if(podaci.getInt("status",0) == 0) {
                    txtSetEmail.setEnabled(true);
                }
                btnSetDatumRodj.setEnabled(true);
                btnSetDatumDia.setEnabled(true);

                btnConf.setEnabled(true);
                btnCancel.setEnabled(true);

                btnConf.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                btnCancel.setBackgroundColor(getResources().getColor(R.color.colorDanger));


                btnGoToUpdateSettings.setVisibility(View.INVISIBLE);
                imgEdit.setVisibility(View.INVISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableForm();
            }
        });

        btnConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData();
            }
        });
    }

    public static boolean validate(String emailStr) {
        if(emailStr.length()==0){
            return true;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private void disableForm() {
        txtSetEmail.setEnabled(false);
        radioMale.setEnabled(false);
        radioFemale.setEnabled(false);
        selectTypeDiabetes.setEnabled(false);
        btnSetDatumRodj.setEnabled(false);
        btnSetDatumDia.setEnabled(false);
    }

    private void updateUserData() {
        if(validate(txtSetEmail.getText().toString())) {
            disableForm();
            String polP;
            int tipP;
            if(radioFemale.isChecked()){
                polP = "Ženski";
            }
            else{
                polP = "Muški";
            }
            tipP = selectTypeDiabetes.getSelectedItemPosition();

            editor.putString("pol", polP);
            editor.putInt("tip", tipP);
            editor.putString("datum_rodj", btnSetDatumRodj.getText().toString());
            editor.putString("datum_d", btnSetDatumDia.getText().toString());
            if(podaci.getInt("status",0)==0) {
                editor.putString("email", txtSetEmail.getText().toString());
            }
            editor.commit();
            btnGoToUpdateSettings.setVisibility(View.VISIBLE);
            imgEdit.setVisibility(View.VISIBLE);

        }
        else{
            Toast.makeText(getContext(), "Email nije validan, ili ostavite prazno polje ili unesite pravilnu email adresu", Toast.LENGTH_LONG).show();
        }
    }
}
