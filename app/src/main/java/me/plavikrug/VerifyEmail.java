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
    RequestQueue mRequestQueue;
    ColorStateList oldColors;
    String vCode;
    boolean codeTrue;
    int progress;
    int status;
    JSONObject json;
    SharedPreferences.Editor editor;
    String apiBaseUrl = "http://80.211.160.9/backend/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        dbSource = new DataBaseSource(this);
        mRequestQueue = Volley.newRequestQueue(this);
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

        setSubmitButtonListener();

        if(status == 0 || (vCode != null && vCode.length() < 6)){
            // registracija korisnika (unos podataka u bazu
//            swp.execute(podaci.getInt("id", 0)+"==="+podaci.getString("email", ""),
//                    "register",
//                    podaci.getString("email", "")+"==="+podaci.getString("pol", "")+"==="+podaci.getInt("tip", 0)
//                            +"==="+podaci.getString("datum_rodj", "")+"==="+podaci.getString("datum_d", ""));
            try {
                mRequestQueue.add(sendUserDataToServerForVerification());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            btnSubmitCode.setVisibility(View.GONE);
            lblBackUpCodeText.setVisibility(View.GONE);
            txtBackUpCode.setVisibility(View.GONE);
            btnSubmitCode.performClick();
        }


        //lblBackUpCodeText.setText(vCode);

        // ovo mo\da budem koristio za novo slanje swp.execute(podaci.getInt("id", 0)+"==="+podaci.getString("email", "")+"==="+"000");

    }

    private JsonObjectRequest sendUserDataToServerForVerification() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", podaci.getString("email", ""));
        jsonObject.put("gender", podaci.getString("pol", ""));
        jsonObject.put("typeDiabetes", podaci.getInt("tip", 0));
        jsonObject.put("dateOfBirth", podaci.getString("datum_rodj", ""));
        jsonObject.put("dateOfDiabetes", podaci.getString("datum_d", ""));
        jsonObject.put("measurements", new JSONArray());
        jsonObject.put("therapy", new JSONArray());
        jsonObject.put("notes", new JSONArray());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiBaseUrl + "register-user",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            vCode = response.getInt("verificationCode") + "";
                            editor.putInt("id", response.getJSONObject("userData").getInt("id"));
                            editor.putString("vCode", vCode);
                            editor.commit();
                            Toast.makeText(VerifyEmail.this, "Email za verifikaciju poslat!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VerifyEmail.this, "Dogodila se greška!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AJMO_PLAVI", "Greska: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(VerifyEmail.this, "Dogodila se greška!", Toast.LENGTH_SHORT).show();
            }
        }

        );

        return jsonObjectRequest;
    }


    private void setSubmitButtonListener() {
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
                            // Ide se na backup
                            Intent backUpIntent = new Intent(VerifyEmail.this, DoBackUp.class);
                            startActivity(backUpIntent);
                        }
                        else if(recIntent.getIntExtra("opcija",0)==2) {
                            // Ucitava se backup sa clouda
                            try {
                                loadBackupFromClud();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(VerifyEmail.this,"Dogodila se greška prilikom učitavanja podataka!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }

                    } else {
                        lblUcitavnjeBackup.setVisibility(View.VISIBLE);
                        lblUcitavnjeBackup.setTextColor(Color.RED);
                        lblUcitavnjeBackup.setText("Netačan kod, pokušajte ponovo!");
                        // lblUcitavnjeBackup.setText("vCode: " + vCode + " | textInput: " + txtBackUpCode.getText().toString());
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
    }

    private void loadBackupFromClud() throws JSONException {
        btnSubmitCode.setVisibility(View.GONE);
        lblBackUpCodeText.setVisibility(View.GONE);
        txtBackUpCode.setVisibility(View.GONE);
        lblUcitavnjeBackup.setTextColor(oldColors);
        lblUcitavnjeBackup.setText(R.string.lblUcitavnjeBackup);
        lblUcitavnjeBackup.setVisibility(View.VISIBLE);
        // swp2.execute(podaci.getInt("id", 0) + "===" + podaci.getString("email", "") + "===" + vCode);
        backUpRecieveProgress.setVisibility(View.VISIBLE);
        while (progress < 30) {
            progress++;
            android.os.SystemClock.sleep(30);
            backUpRecieveProgress.setProgress(progress);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", podaci.getString("email", ""));
        jsonObject.put("id", podaci.getInt("id", 0));
        jsonObject.put("verificationCode", Integer.parseInt(podaci.getString("vCode", "")));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiBaseUrl + "user-data-backup",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        while (progress < 60) {
                            progress++;
                            android.os.SystemClock.sleep(30);
                            backUpRecieveProgress.setProgress(progress);
                        }
                        json = response;
                        try {
//                            Log.d("PLAVI_KRUG", "pol: " + response.getString("gender"));
//                            Log.d("PLAVI_KRUG", "tip dijabetesa: " + response.getInt("typeDiabetes"));
//                            Log.d("PLAVI_KRUG", "datum_rodj: " + response.getString("dateOfBirth"));
//                            Log.d("PLAVI_KRUG", "datum_d: " + response.getString("dateOfDiabetes"));
//                            Log.d("PLAVI_KRUG", "mjerenja: " + response.getJSONArray("measurements").toString());

                            editor.putString("pol", response.getString("gender"));
                            editor.putInt("tip", response.getInt("typeDiabetes"));
                            editor.putString("datum_rodj", response.getString("dateOfBirth"));
                            editor.putString("datum_d", response.getString("dateOfDiabetes"));
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AJMO_PLAVI", "Greska: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(VerifyEmail.this, "Dogodila se greška prilikom učitavanja podataka!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        );

        mRequestQueue.add(jsonObjectRequest);


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
                // json = output[0];
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
