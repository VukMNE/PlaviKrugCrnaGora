package me.plavikrug;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static me.plavikrug.SetEmailPopUpWindow.VALID_EMAIL_ADDRESS_REGEX;

/**
 * Created by Vuk on 10.8.2017..
 */

public class PodesavanjaFragment extends Fragment {

    SharedPreferences podaci;
    SharedPreferences.Editor editor;

    CircleImageView imgJa;
    TextView lblEmail;
    TextView lblSetIme;
    TextView lblSetPrezime;
    TextView lblSetPol;
    TextView lblSetTip;
    TextView lblSetDatumRodj;
    TextView lblSetDatumDia;

    EditText txtSetIme;
    EditText txtSetPrezime;
    EditText txtSetEmail;

    Switch swSetPol;
    Switch swSetTip;

    Button btnSetDatumRodj;
    Button btnSetDatumDia;
    Button btnUpdateSettings;

    DatePickerDialog.OnDateSetListener datumRodjListener;
    DatePickerDialog.OnDateSetListener datumDiabListener;



    private DatePickerDialog.OnDateSetListener dateBirthListener;
    private DatePickerDialog.OnDateSetListener dateDiabetesListener;
    int godina_r, mjesec_r, dan_r, godina_d, mjesec_d, dan_d;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podesavanja,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Poveyivanje layouta sa kontroloma na ekranu
        getActivity().setTitle("Podešavanja");
        podaci = this.getActivity().getSharedPreferences("PODACI", MODE_PRIVATE);
        editor = podaci.edit();

        imgJa = (CircleImageView) view.findViewById(R.id.imgJa);
        btnUpdateSettings = (Button) view.findViewById(R.id.btnUpdateSettings);
        lblEmail = (TextView) view.findViewById(R.id.lbl_set_email);
        txtSetEmail = (EditText) view.findViewById(R.id.txt_set_email);
        /*lblSetIme = (TextView) view.findViewById(R.id.lbl_set_ime);
        lblSetPrezime = (TextView) view.findViewById(R.id.lbl_set_prezime);*/
        lblSetPol = (TextView) view.findViewById(R.id.lbl_set_pol);
        lblSetTip = (TextView) view.findViewById(R.id.lbl_set_tip);
        lblSetDatumRodj = (TextView) view.findViewById(R.id.lbl_set_datum_rodjenja);
        lblSetDatumDia = (TextView) view.findViewById(R.id.lbl_set_datum_dijabetisa);

        /*txtSetIme = (EditText) view.findViewById(R.id.txt_set_ime);
        txtSetPrezime = (EditText) view.findViewById(R.id.txt_set_prezime);*/

        swSetPol = (Switch) view.findViewById(R.id.switch_set_pol);
        swSetTip = (Switch) view.findViewById(R.id.switch_set_tip);

        btnSetDatumRodj = (Button) view.findViewById(R.id.date_set_birth);
        btnSetDatumDia = (Button) view.findViewById(R.id.date_set_diabetes);

        //Upisivanje ranije sačuvanih osnovnih podataka o korisniku

        /*txtSetIme.setText(podaci.getString("ime",""));
        txtSetPrezime.setText(podaci.getString("prezime",""));*/
        txtSetEmail.setText(podaci.getString("email",""));
        boolean pol,tip;

        if(podaci.getString("pol","").equals("Muški")){
            pol = false;
        }
        else{
            pol = true;
        }
        if(podaci.getInt("tip",0)==1){
            tip = true;
        }
        else{
            tip = false;
        }


        swSetPol.setChecked(pol);
        swSetTip.setChecked(tip);

        btnSetDatumRodj.setText(podaci.getString("datum_rodj","1.1.1990"));
        btnSetDatumDia.setText(podaci.getString("datum_d","1.9.2016"));

        //Omogucavanja odabira datuma rodjenja
        btnSetDatumRodj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dateBirthDialog = new DatePickerDialog(getContext(),
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

        datumRodjListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                godina_r = year;
                mjesec_r = month+1;
                dan_r = dayOfMonth;
                btnSetDatumRodj.setText(dayOfMonth+"."+(month+1)+"."+year);

            }
        };

        btnSetDatumDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDiabetesDialog = new DatePickerDialog(getContext(),
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
                btnSetDatumDia.setText(dayOfMonth+"."+(month+1)+"."+year);
            }
        };





        btnUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labela = btnUpdateSettings.getText().toString();

                switch(labela.toUpperCase()){
                    case "IZMIJENI":
                        /*txtSetIme.setEnabled(true);
                        txtSetPrezime.setEnabled(true);*/
                        swSetPol.setEnabled(true);
                        swSetTip.setEnabled(true);
                        if(podaci.getInt("status",0) == 0) {
                            txtSetEmail.setEnabled(true);
                        }
                        btnSetDatumRodj.setEnabled(true);
                        btnSetDatumDia.setEnabled(true);
                        btnUpdateSettings.setText("SAČUVAJ");
                        break;
                    case "SAČUVAJ":
                        if(validate(txtSetEmail.getText().toString())) {
                        /*txtSetIme.setEnabled(false);
                        txtSetPrezime.setEnabled(false);*/
                        txtSetEmail.setEnabled(false);
                        swSetPol.setEnabled(false);
                        swSetTip.setEnabled(false);
                        btnSetDatumRodj.setEnabled(false);
                        btnSetDatumDia.setEnabled(false);
                        btnUpdateSettings.setText("IZMIJENI");

                        //Upisivanje novih vrijednosti u podešavanja
                        /*editor.putString("ime",txtSetIme.getText().toString());
                        editor.putString("prezime",txtSetPrezime.getText().toString());*/
                        String polP;
                        int tipP;
                        if(swSetPol.isChecked()){
                             polP = "Ženski";
                        }
                        else{
                             polP = "Muški";
                        }
                        if(swSetTip.isChecked()){
                            tipP = 1;
                        }
                        else{
                            tipP = 2;
                        }

                            editor.putString("pol", polP);
                            editor.putInt("tip", tipP);
                            editor.putString("datum_rodj", btnSetDatumRodj.getText().toString());
                            editor.putString("datum_d", btnSetDatumDia.getText().toString());
                            if(podaci.getInt("status",0)==0) {
                                editor.putString("email", txtSetEmail.getText().toString());
                            }
                            editor.commit();
                        }
                        else{
                            Toast.makeText(getContext(), "Email nije validan, ili ostavite prazno polje ili unesite pravilnu email adresu", Toast.LENGTH_LONG).show();
                        }

                        break;
                }

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
}
