package me.plavikrug;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import me.plavikrug.model.PodsjetnikKlasa;

public class PodsjetniciLista extends AppCompatActivity {

    Button goToPodsjetnik;
    DataBaseSource dbSource;
    ListView listaPodsjetnika;
    PodsjetnikAdapter podAdapter;
    Intent getterExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podsjetnici_lista);
        goToPodsjetnik = (Button) findViewById(R.id.goToPodsjetnik);

        goToPodsjetnik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PodsjetniciLista.this, Podsjetnik.class);
                startActivity(intent);
                finish();
            }
        });
        //Za prikazivanje Toasta nakon brisanja podsjetnika
        getterExtra = getIntent();
        String msg = getterExtra.getStringExtra("usp_poruka");
        if(msg!=null){
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        podAdapter = new PodsjetnikAdapter(getApplicationContext(), uzmiPodsjetnike());
        listaPodsjetnika = (ListView) findViewById(R.id.pod_lista);
        listaPodsjetnika.setAdapter(podAdapter);
        listaPodsjetnika.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent podIntent = new Intent(PodsjetniciLista.this, Podsjetnik.class);
                podIntent.putExtra("datum", podAdapter.getItem(position).getDatum());
                podIntent.putExtra("vrijeme", podAdapter.getItem(position).getVrijeme());
                podIntent.putExtra("naslov", podAdapter.getItem(position).getNaslov());
                podIntent.putExtra("id", podAdapter.getItem(position).getId());
                startActivity(podIntent);
                finish();

            }
        });

    }

    private ArrayList<PodsjetnikKlasa> uzmiPodsjetnike(){
        ArrayList<PodsjetnikKlasa> podsjetnici = new ArrayList<PodsjetnikKlasa>();
        dbSource = new DataBaseSource(this);
        dbSource.open();
        Calendar c = Calendar.getInstance();
        int godina = c.get(Calendar.YEAR);
        int mjesec = c.get(Calendar.MONTH)+1;
        int dan = c.get(Calendar.DAY_OF_MONTH);
        int sat = c.get(Calendar.HOUR_OF_DAY);
        int minut = c.get(Calendar.MINUTE);
        int id = 0;


        String mjesecStr = mjesec + "";
        String danStr = dan + "";
        String satStr = sat + "";
        String minStr = minut +"";
        if(mjesec<10){
            mjesecStr = "0" + mjesec;
        }
        if(dan<10){
            danStr = "0" + dan;
        }
        if(minut<10){
            minStr = "0" + minut;
        }
        if(sat<10){
            satStr = "0" + sat;
        }
        dbSource.deletePodsjetnici(danStr+"." + mjesecStr+ "." + godina, satStr+":"+minStr);
        Cursor cursor = dbSource.getPodsjetnik();
        cursor.moveToFirst();
        String datum, vrijeme, naslov;

        while(!cursor.isAfterLast()){
            datum = cursor.getString(0);
            vrijeme = cursor.getString(1);
            naslov = cursor.getString(2);
            id = cursor.getInt(3);

            podsjetnici.add(new PodsjetnikKlasa(datum,vrijeme,naslov,id));
            cursor.moveToNext();
        }
        return podsjetnici;
    }

    @Override
    public void onBackPressed() {

        Intent retIntent = new Intent(PodsjetniciLista.this, GlavnaAktivnost.class);
        startActivity(retIntent);
        finish();
    }
}
