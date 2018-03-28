package me.plavikrug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BackUpActivity extends AppCompatActivity{
    Button btnBackUpPomoc;
    TextView txtBackUpPercentage;
    TextView txtBackUpSyncedText;
    Button btnMakeBackUp;
    Button btnDoBackUp;
    DataBaseSource dbSource;
    private SharedPreferences podaci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //btnBackUpPomoc = (Button) findViewById(R.id.btnBackUpPomoc);
        txtBackUpPercentage = (TextView) findViewById(R.id.txtBackUpPercentage);
        txtBackUpSyncedText = (TextView) findViewById(R.id.txtBackUpSyncedText);
        btnMakeBackUp = (Button) findViewById(R.id.btnMakeBackUp);
        btnDoBackUp = (Button) findViewById(R.id.btnDoBackUp);
        dbSource = new DataBaseSource(this);
        dbSource.open();

        txtBackUpPercentage.setText(dbSource.countBackedUpData()+"%");

        btnMakeBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
                String email = podaci.getString("email", "");
                int status = podaci.getInt("status", 0);
                if (isNetworkAvailable()) {
                if (email == null) {
                    Intent emailIntent = new Intent(BackUpActivity.this, SetEmailPopUpWindow.class);
                    emailIntent.putExtra("opcija", 1);
                    startActivity(emailIntent);
                } else if (email.length() == 0) {
                    Intent emailIntent = new Intent(BackUpActivity.this, SetEmailPopUpWindow.class);
                    emailIntent.putExtra("opcija", 1);
                    startActivity(emailIntent);
                } else {
                    if (status == 1) {
                        Intent backUpIntent = new Intent(BackUpActivity.this, DoBackUp.class);
                        startActivity(backUpIntent);
                    } else {
                        // mail nije verifikovan
                        Intent backUpIntent = new Intent(BackUpActivity.this, VerifyEmail.class);
                        backUpIntent.putExtra("opcija", 1);
                        startActivity(backUpIntent);
                    }
                }
            }
            else{
                //Nema interneta
                AlertDialog.Builder builder = new AlertDialog.Builder(BackUpActivity.this);
                builder.setTitle("Nema interneta");
                builder.setMessage(R.string.msgNoInternet);
                builder.setIcon(R.mipmap.ic_offline);
                AlertDialog alert1 = builder.create();
                alert1.show();
            }
            }
        });

        btnDoBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
                String email = podaci.getString("email", "");
                int status = podaci.getInt("status",0);
                int id = podaci.getInt("id",0);
                if (isNetworkAvailable()) {
                    if (email == null || email.length() == 0) {
                        //Korisnik je registrovan
                        Intent emailIntent = new Intent(BackUpActivity.this, SetEmailPopUpWindow.class);
                        emailIntent.putExtra("opcija", 2);
                        startActivity(emailIntent);
                    } else {
                        if (status == 1) {
                            //Ukucaj siguronosni kod
                            Intent backUpCode = new Intent(BackUpActivity.this, VerifyEmail.class);
                            backUpCode.putExtra("opcija", 2);
                            startActivity(backUpCode);

                        } else {
                            Intent backUpIntent = new Intent(BackUpActivity.this, VerifyEmail.class);
                            backUpIntent.putExtra("opcija", 2);
                            startActivity(backUpIntent);
                        }
                    }
                }
                else {
                    //Nema interneta
                    AlertDialog.Builder builder = new AlertDialog.Builder(BackUpActivity.this);
                    builder.setTitle("Nema interneta");
                    builder.setMessage(R.string.msgNoInternet);
                    builder.setIcon(R.mipmap.ic_offline);
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
                }


        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent getBack = new Intent(this, GlavnaAktivnost.class);
        startActivity(getBack);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
