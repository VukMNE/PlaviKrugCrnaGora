package me.plavikrug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GlavnaAktivnost extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences podaci;
    private Intent getterExtra;
    private String fragmentParam;
    private int idFragment; //govori na kojem je fragmentu aktivnost
    Bundle savedData; // Ovo služi za čuvanje podataka unutar ekrana, prilikom rotacije uređaja

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivnost_glavna);
        setTitle("Plavi Krug");
        podaci = getSharedPreferences("PODACI", MODE_PRIVATE);
        Boolean isFirstRun = podaci.getBoolean("isFirstRun", true); //isFirstRun govori o tome, da li je ovo prvo pokretanje aplikacije na ovom uređaju
        FragmentTransaction ftStart = getSupportFragmentManager().beginTransaction();
        ftStart.replace(R.id.content_glavna_aktivnost,new ContentFragment());
        ftStart.commit();

        if (isFirstRun) {
            //show start activity
            /*
             * Ako jeste prvo pokretanje, onda povedi korisnika na stranicu đe treba da ostavi lične podatke
             */
            startActivity(new Intent(GlavnaAktivnost.this, RegisterActivity.class));
            finish();

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//TODO objasni ovaj dio malo bolje
        getterExtra = getIntent();
        String msg = getterExtra.getStringExtra("usp_poruka");
        if(msg!=null){
            if(msg.equals("HbA1c")) {
                pokaziOdabranuStvar(R.id.men_HbA1c);
                msg = getterExtra.getStringExtra("dlt_poruka");
            }
            if(msg.equals("Mjerenje")){
                pokaziOdabranuStvar(R.id.men_dnevnik);
                msg = getterExtra.getStringExtra("dlt_poruka");
            }
            if(msg.equals("PP")){
                msg = getterExtra.getStringExtra("dlt_poruka");
            }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Ako postoji predjašnje stanje aplikacije
        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey("idFragment")) {
                pokaziOdabranuStvar(savedInstanceState.getInt("idFragment"));
            }
        }

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            boolean version15AdjustmentDone = podaci.getBoolean("version15AdjustmentDone", false);

            if(versionCode >= 15 && !version15AdjustmentDone) {
                SharedPreferences.Editor editor = podaci.edit();
                editor.putString("vCode", "");
                editor.putInt("status", 0);
                editor.putBoolean("version15AdjustmentDone", true);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
                super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //Mapiranje gdje vodi koje dugme sa glavnog menija
    public void pokaziOdabranuStvar(int id){
        Fragment fragment = null;
        String strFragment = "";

        switch(id){
            case R.id.men_home:
                fragment = new ContentFragment();
                strFragment = "content_fragment";
                idFragment = R.id.men_home;
                break;
            case R.id.men_izvjestaj:
                fragment = IzvjestajFragment.newInstance("");
                strFragment = "fragment_izvjestaj";
                idFragment = R.id.men_izvjestaj;
                break;
            case R.id.men_HbA1c:
                fragment = new HbA1cLista();
                strFragment = "fragment_hba1c_lista";
                idFragment = R.id.men_HbA1c;
                break;
            case R.id.men_dnevnik:
                fragment = new DnevnikFragment();
                strFragment = "fragment_dnevnik";
                idFragment = R.id.men_dnevnik;
                break;
            case R.id.men_backup:
                Intent i = new Intent(GlavnaAktivnost.this, BackUpActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.men_podesavanja:
                fragment = new PodesavanjaFragment();
                strFragment = "fragment_podesavanja";
                idFragment = R.id.men_podesavanja;
                break;

        }

        if(fragment!= null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_glavna_aktivnost,fragment).addToBackStack("tag").commit();

        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("idFragment", idFragment);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        pokaziOdabranuStvar(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
