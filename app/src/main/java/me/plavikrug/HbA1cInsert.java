package me.plavikrug;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Vuk on 27.8.2017..
 */

public class HbA1cInsert extends AppCompatActivity {

    Calendar c;
    int godina, mjesec, dan;
    String mjesecStr, danStr;
    Button btnHba1cDate;
    EditText txtRez;
    private DatePickerDialog.OnDateSetListener datumListener;
    DataBaseSource dataBaseSource;
    int check;
    int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hba1c);
        setTitle("Dodaj HbA1c rezultat");
        btnHba1cDate = (Button) findViewById(R.id.btn_hba1c_date);
        txtRez = (EditText) findViewById(R.id.txt_rez_hba1c);

        Intent intent = getIntent();

        if(intent.getStringExtra("datum") != null){
            btnHba1cDate.setText(intent.getStringExtra("datum"));
            txtRez.setText(intent.getFloatExtra("rez", 0)+"");
            check = 1;
            id = intent.getIntExtra("id",0);
        }

        c = Calendar.getInstance();
        final Locale locale = Locale.getDefault();
        godina = c.get(Calendar.YEAR);
        mjesec = c.get(Calendar.MONTH)+1;
        dan = c.get(Calendar.DAY_OF_MONTH);

        mjesecStr = mjesec + "";
        danStr = dan + "";
        if(mjesec<10){
            mjesecStr = "0" + mjesec;
        }
        if(dan<10){
            danStr = "0" + dan;
        }


        btnHba1cDate.setText(danStr+"."+mjesecStr+"." + godina);

        btnHba1cDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datumDialog = new DatePickerDialog(HbA1cInsert.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datumListener,
                        godina,mjesec-1,dan);
                datumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datumDialog.setTitle("Datum");
                datumDialog.setButton(datumDialog.BUTTON_POSITIVE,"U redu",datumDialog);
                datumDialog.setButton(datumDialog.BUTTON_NEGATIVE,"Otkaži",datumDialog);
                datumDialog.show();
            }
        });

        datumListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                godina= year;
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
                btnHba1cDate.setText(danStr+"."+mjesecStr+"."+year);
            }
        };


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_dodaj_mjeru, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_mjer) {
            if (txtRez.getText().toString().length() > 0) {
                if (check == 0) {
                    dataBaseSource = new DataBaseSource(HbA1cInsert.this);
                    dataBaseSource.open();
                    dataBaseSource.insertHbA1c(btnHba1cDate.getText().toString(), Float.parseFloat(txtRez.getText().toString()));
                } else if (check == 1) {
                    dataBaseSource = new DataBaseSource(HbA1cInsert.this);
                    dataBaseSource.open();
                    dataBaseSource.updateHbA1c(btnHba1cDate.getText().toString(), Float.parseFloat(txtRez.getText().toString()), id);
                }
                Toast.makeText(this, "Uspješno sačuvani podaci", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(HbA1cInsert.this, "Nijeste unijeli rezultat HbA1c testa", Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent retIntent = new Intent(HbA1cInsert.this, GlavnaAktivnost.class);
        startActivity(retIntent);
        finish();
    }
}
