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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.plavikrug.db.DataBaseSource;

public class DoBackUp extends AppCompatActivity implements AsyncResponse {

    private SharedPreferences podaci;
    private DataBaseSource dbSource;
    private SamoupravniWebProgram swp = new SamoupravniWebProgram();
    private TextView lblBackUpProgress;
    private ProgressBar progressBar;
    private Handler mHandler = new Handler();
    private RequestQueue mRequestQueue;
    String apiBaseUrl = "http://80.211.160.9/backend/api/";


    private int progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_back_up);
        mRequestQueue = Volley.newRequestQueue(this);
        lblBackUpProgress = (TextView) findViewById(R.id.lblBackUpInProgress);
        progressBar = (ProgressBar) findViewById(R.id.backUpProgress);
        progressBar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
        swp.delegate = this;
        progress = 0;

        try {
            mRequestQueue.add(kopirajSvePodatke());
            //swp.execute(podaci.getInt("id", 0)+"==="+podaci.getString("email", ""),kopirajSvePodatke().toString());
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

    private JsonObjectRequest kopirajSvePodatke() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", podaci.getString("email", ""));
        jsonObject.put("gender", podaci.getString("pol", ""));
        jsonObject.put("typeDiabetes", podaci.getInt("tip", 0));
        jsonObject.put("dateOfBirth", podaci.getString("datum_rodj", ""));
        jsonObject.put("dateOfDiabetes", podaci.getString("datum_d", ""));
        jsonObject.put("id", podaci.getInt("id", 0));
        jsonObject.put("verificationCode", Integer.parseInt(podaci.getString("vCode", "")));


        JSONArray jsonMeasurements = new JSONArray();
        JSONArray jsonTherapy = new JSONArray();
        JSONArray jsonNotes = new JSONArray();


        dbSource = new DataBaseSource(this);
        dbSource.open();
        Cursor mjerenjaCursor = dbSource.getSvaMjerenja();
        mjerenjaCursor.moveToFirst();

        int ind = 0;
        while(!mjerenjaCursor.isAfterLast()){
            JSONObject currMjer = new JSONObject();
            currMjer.put("id", mjerenjaCursor.getInt(0));
            currMjer.put("datetime", mjerenjaCursor.getString(1));
            currMjer.put("measuredValue", mjerenjaCursor.getFloat(2));
            currMjer.put("attributeId", mjerenjaCursor.getInt(3));
            currMjer.put("typeOfMeasure", mjerenjaCursor.getInt(4));
            currMjer.put("backedUp", true);

            jsonMeasurements.put(ind, currMjer);
            ind++;
            mjerenjaCursor.moveToNext();
        }

        Cursor terapijaCursor = dbSource.getSvaTerapija();
        terapijaCursor.moveToFirst();

        int tind = 0;
        while(!terapijaCursor.isAfterLast()){
            JSONObject currTer = new JSONObject();
            currTer.put("id", terapijaCursor.getInt(0));
            currTer.put("insulinUnits", terapijaCursor.getInt(1));
            currTer.put("correctionUnits", terapijaCursor.getInt(2));
            currTer.put("bodyEntryPosition", terapijaCursor.getInt(3));
            currTer.put("date", terapijaCursor.getString(4));
            currTer.put("time", terapijaCursor.getString(5));
            currTer.put("backedUp", true);

            jsonTherapy.put(tind, currTer);
            tind++;
            terapijaCursor.moveToNext();
        }

        Cursor biljeskeCursor = dbSource.getSveBiljeske();
        biljeskeCursor.moveToFirst();
        int bInd = 0;
        while(!biljeskeCursor.isAfterLast()){
            JSONObject currBiljeska = new JSONObject();
            currBiljeska.put("id", biljeskeCursor.getInt(0));
            currBiljeska.put("noteText", biljeskeCursor.getString(1));
            currBiljeska.put("date", biljeskeCursor.getString(2));
            currBiljeska.put("backedUp", true);


            jsonNotes.put(bInd, currBiljeska);
            bInd++;
            biljeskeCursor.moveToNext();

        }

        dbSource.markAllBackedUp();

        jsonObject.put("measurements", jsonMeasurements);
        jsonObject.put("therapy", jsonTherapy);
        jsonObject.put("notes", jsonNotes);
        dbSource.close();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                apiBaseUrl + "user-data-backup",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AJMO_PLAVI", "Greska: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(DoBackUp.this, "Dogodila se greška!", Toast.LENGTH_SHORT).show();
            }
        }

        );

        return jsonObjectRequest;
    }
}
