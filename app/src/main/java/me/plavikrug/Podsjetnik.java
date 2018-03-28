package me.plavikrug;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import me.plavikrug.utils.UniversalTextWatcher;

public class Podsjetnik extends AppCompatActivity {


    int dan, mjesec, godina, sat ,minuti, check, id;
    String mjesecStr, danStr;
    DataBaseSource dbSource;

    private Button btnDatePicker;
    private Button btnSetPodsjetnik;
    private DatePickerDialog.OnDateSetListener datumListener;
    private EditText txtSat;
    private EditText txtMin;
    private EditText podNaslov;
    private ImageView btnGoreSat;
    private ImageView btnDoljeSat;
    private ImageView btnGoreMin;
    private ImageView btnDoljeMin;
    Calendar c;
    NotificationCompat.Builder notification;
    private int keyboardPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podsjetnik);
        btnDatePicker = (Button) findViewById(R.id.pod_date_picker);
        btnSetPodsjetnik = (Button) findViewById(R.id.btn_set_podsjetnik);
        podNaslov = (EditText) findViewById(R.id.txt_pod_naslov);

        txtSat = (EditText) findViewById(R.id.txt_sati);
        txtMin = (EditText) findViewById(R.id.txt_minuti);
        txtSat.setText("08");
        txtMin.setText("00");


        Intent intent = getIntent();



        btnGoreSat = (ImageView) findViewById(R.id.btn_gore_sat);
        btnDoljeSat = (ImageView) findViewById(R.id.btn_dolje_sat);
        btnGoreMin = (ImageView) findViewById(R.id.btn_gore_minut);
        btnDoljeMin = (ImageView) findViewById(R.id.btn_dolje_minut);

        c = Calendar.getInstance();
        final Locale locale = Locale.getDefault();
        godina = c.get(Calendar.YEAR);
        mjesec = c.get(Calendar.MONTH)+1;
        dan = c.get(Calendar.DAY_OF_MONTH);
        sat = 8;
        minuti = 0;

        mjesecStr = mjesec + "";
        danStr = dan + "";
        if(mjesec<10){
            mjesecStr = "0" + mjesec;
        }
        if(dan<10){
            danStr = "0" + dan;
        }

        btnDatePicker.setText(danStr+"."+mjesecStr+"." + godina);

        if(intent.getStringExtra("datum") != null){
            btnDatePicker.setText(intent.getStringExtra("datum"));
            txtSat.setText(intent.getStringExtra("vrijeme").substring(0,2));
            txtMin.setText(intent.getStringExtra("vrijeme").substring(3));
            podNaslov.setText(intent.getStringExtra("naslov"));
            check = 1;
            id = intent.getIntExtra("id",0);
        }

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datumDialog = new DatePickerDialog(Podsjetnik.this,
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
                btnDatePicker.setText(danStr+"."+mjesecStr+"."+year);
            }
        };

        View.OnClickListener upDownLeftRight = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setSat = Integer.parseInt(txtSat.getText().toString());
                int setMin = Integer.parseInt(txtMin.getText().toString());
                String setSatStr = "";
                String setMinStr = "";
                switch (v.getId()){
                    case R.id.btn_gore_sat:
                        setSat--;
                        setSatStr = setSat + "";
                        if(setSat<10){
                            setSatStr = "0" + setSat;
                            if(setSat<0){
                                setSat = 23;
                                setSatStr = setSat + "";
                            }

                        }
                        txtSat.setText(setSatStr);
                        break;
                    case R.id.btn_dolje_sat:
                        setSat++;
                        setSatStr = setSat + "";
                        if(setSat<10){
                            setSatStr = "0" + setSat;
                        }
                        else if(setSat>23){
                            setSat = 0;
                            setSatStr = "0" + setSat;
                        }
                        txtSat.setText(setSatStr);
                        break;
                    case R.id.btn_gore_minut:
                        setMin--;
                        setMinStr = setMin + "";
                        if(setMin<10){
                            setMinStr = "0" + setMin;
                            if(setMin<0){
                                setMin = 59;
                                setMinStr = setMin + "";
                            }
                        }
                        txtMin.setText(setMinStr);
                        break;
                    case R.id.btn_dolje_minut:
                        setMin++;
                        setMinStr = setMin + "";
                        if(setMin<10){
                            setMinStr = "0" + setMin;
                        }
                        else if(setMin>59){
                            setMin = 0;
                            setMinStr = "0" + setMin;
                        }
                        txtMin.setText(setMinStr);
                        break;
                }
            }
        };

        btnGoreSat.setOnClickListener(upDownLeftRight);
        btnDoljeSat.setOnClickListener(upDownLeftRight);
        btnGoreMin.setOnClickListener(upDownLeftRight);
        btnDoljeMin.setOnClickListener(upDownLeftRight);

        txtSat.addTextChangedListener( new UniversalTextWatcher(txtSat, null, 3));
        txtMin.addTextChangedListener( new UniversalTextWatcher(txtMin, null, 4));
        btnSetPodsjetnik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbSource = new DataBaseSource(Podsjetnik.this);
                dbSource.open();
                if(check != 1) {
                    dbSource.insertPodsjetnik(btnDatePicker.getText().toString(),
                            txtSat.getText().toString() + ":" + txtMin.getText().toString()
                            , podNaslov.getText().toString());
                }
                else{
                    dbSource.updatePodsjetnik(btnDatePicker.getText().toString(),
                            txtSat.getText().toString() + ":" + txtMin.getText().toString()
                            , podNaslov.getText().toString(),id);
                }

                Calendar alarmVrijeme = Calendar.getInstance();
                alarmVrijeme.set(Calendar.DAY_OF_MONTH, Integer.parseInt(danStr));
                alarmVrijeme.set(Calendar.MONTH, Integer.parseInt(mjesecStr)-1);
                alarmVrijeme.set(Calendar.YEAR, godina);
                alarmVrijeme.set(Calendar.HOUR_OF_DAY, Integer.parseInt(txtSat.getText().toString()));
                alarmVrijeme.set(Calendar.MINUTE, Integer.parseInt(txtMin.getText().toString()));
                alarmVrijeme.set(Calendar.SECOND, 1);



                Driver driver = new GooglePlayDriver(Podsjetnik.this);
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
                Calendar currentTime = Calendar.getInstance();
                int timeInterval = (int) TimeUnit.MILLISECONDS.toSeconds(Math.abs(alarmVrijeme.getTimeInMillis() - currentTime.getTimeInMillis()));//(int) (((-1) * alarmVrijeme.getTimeInMillis())-((-1) *currentTime.getTimeInMillis()))/1000;
                Bundle jobParameters = new Bundle();

                SharedPreferences podaci = getSharedPreferences("PODACI",MODE_PRIVATE);

                jobParameters.putString("podnaslov", podNaslov.getText().toString());
                jobParameters.putLong("kada?", alarmVrijeme.getTimeInMillis());
                jobParameters.putInt("notifikacija_id",podaci.getInt("notifikacija_id",0));

                Job myJob = dispatcher.newJobBuilder()
                        // the JobService that will be called
                        .setService(NotificationPrijemnik.class)
                        // uniquely identifies the job
                        .setTag("complex-job")
                        // one-off job
                        .setRecurring(true)
                        // don't persist past a device reboot
                        .setLifetime(Lifetime.FOREVER)
                        // start between 0 and 15 minutes (900 seconds)
                        .setTrigger(Trigger.executionWindow(0,timeInterval))
                        // overwrite an existing job with the same tag
                        .setReplaceCurrent(true)
                        //Proslijedi parametre
                        .setExtras(jobParameters)
                        // retry with exponential backoff
                        .build();
                dispatcher.schedule(myJob);
                dbSource.close();



                Intent intent = new Intent(Podsjetnik.this, GlavnaAktivnost.class);
                intent.putExtra("usp_poruka", "PP");
                intent.putExtra("dlt_poruka", "Podsjetnik podešen za: " + danStr + "." + mjesecStr + "." + godina + " " + txtSat.getText().toString() + ":" + txtMin.getText().toString());
                startActivity(intent);
            }
        });


    }

    public String setD(int a){
        String ar = a + "";
        if(a < 10){
            ar = "0" + ar;
        }
        return  ar;
    }

    @Override
    public void onBackPressed() {

        Intent retIntent = new Intent(Podsjetnik.this, PodsjetniciLista.class);
        startActivity(retIntent);
        finish();
    }
}
