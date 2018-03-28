package me.plavikrug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DoBackUp extends AppCompatActivity implements AsyncResponse {

    private SharedPreferences podaci;
    private DataBaseSource dbSource;
    private SamoupravniWebProgram swp = new SamoupravniWebProgram();
    private TextView lblBackUpProgress;
    private ProgressBar progressBar;
    private Handler mHandler = new Handler();

    private int progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_back_up);
        lblBackUpProgress = (TextView) findViewById(R.id.lblBackUpInProgress);
        progressBar = (ProgressBar) findViewById(R.id.backUpProgress);
        progressBar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
        swp.delegate = this;
        progress = 0;

            try {
                swp.execute(podaci.getInt("id", 0)+"==="+podaci.getString("email", ""),kopirajSvePodatke().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


    }

    @Override
    public void processFinish(String[] output) {
        if(output.length>1){
            SharedPreferences.Editor editor = podaci.edit();
            editor.putInt("id", Integer.parseInt(output[0]));
            editor.commit();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progress < 100){
                    progress++;
                    android.os.SystemClock.sleep(10);
                    mHandler.post( new Runnable(){
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                }

                mHandler.post( new Runnable(){
                    @Override
                    public void run() {
                        lblBackUpProgress.setText("Pravljenje siguronosne kopije je uspješno završeno!");
                        android.os.SystemClock.sleep(500);
                        onBackPressed();
                    }
                });
            }
        }).start();

    }

    @Override
    public void processFinish(Uri uri) throws IOException {
        Log.d("OK", "nije OK");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, BackUpActivity.class);
        startActivity(i);
        finish();
    }

    private JSONObject kopirajSvePodatke() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray jsonMjerenja = new JSONArray();
        JSONArray jsonTerapija = new JSONArray();
        JSONArray jsonBiljeske = new JSONArray();



        dbSource = new DataBaseSource(this);
        dbSource.open();
        Cursor mjerenjaCursor = dbSource.getSvaMjerenja();
        mjerenjaCursor.moveToFirst();

        int ind = 0;
        while(!mjerenjaCursor.isAfterLast()){
            JSONObject currMjer = new JSONObject();
            currMjer.put("vrijeme", mjerenjaCursor.getString(0));
            currMjer.put("atr_id_atr", mjerenjaCursor.getInt(2));
            currMjer.put("tip_mj", mjerenjaCursor.getInt(3));
            currMjer.put("izmjereno", mjerenjaCursor.getDouble(1));

            jsonMjerenja.put(ind, currMjer);
            ind++;
            mjerenjaCursor.moveToNext();
        }

        Cursor terapijaCursor = dbSource.getSvaTerapija();
        terapijaCursor.moveToFirst();

        int tind = 0;
        while(!terapijaCursor.isAfterLast()){
            JSONObject currTer = new JSONObject();
            currTer.put("dat_vrijem", terapijaCursor.getString(3));
            currTer.put("jed_insl", terapijaCursor.getInt(0));
            currTer.put("jed_kor", terapijaCursor.getInt(1));
            currTer.put("poz_prijem_ter", terapijaCursor.getInt(2));

            jsonTerapija.put(tind, currTer);
            tind++;
            terapijaCursor.moveToNext();
        }

        Cursor biljeskeCursor = dbSource.getSveBiljeske();
        biljeskeCursor.moveToFirst();
        int bInd = 0;
        while(!biljeskeCursor.isAfterLast()){
            JSONObject currBiljeska = new JSONObject();
            currBiljeska.put("biljeska", biljeskeCursor.getString(0));
            currBiljeska.put("datum", biljeskeCursor.getString(1));

            jsonBiljeske.put(bInd, currBiljeska);
            bInd++;
            biljeskeCursor.moveToNext();

        }

        dbSource.markAllBackedUp();

        json.put("mjerenja", jsonMjerenja);
        json.put("terapija", jsonTerapija);
        json.put("biljeske", jsonBiljeske);
        dbSource.close();
        return json;
    }
}
