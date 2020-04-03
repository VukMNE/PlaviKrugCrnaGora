package me.plavikrug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.plavikrug.db.DataBaseSource;

public class DeletePopUpWindow extends Activity {
    TextView dltMessage;
    Button btnDoDelete;
    Button btnCancel;
    String klasa;
    String tabela;
    String vrijeme;
    String id;
    protected DataBaseSource dataBaseSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pop_up_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*0.5));
        dltMessage = (TextView) findViewById(R.id.dltMessage);
        btnDoDelete = (Button) findViewById(R.id.da);
        btnCancel = (Button) findViewById(R.id.cancel);

        Intent gt = getIntent();
        tabela = gt.getStringExtra("tabela");
        id = gt.getStringExtra("id");
        klasa = gt.getStringExtra("activity");

        //btnDoDelete

        btnDoDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseSource = new DataBaseSource(DeletePopUpWindow.this);
                dataBaseSource.open();
                dataBaseSource.delete(tabela, id);
                dataBaseSource.close();
                if(tabela.equals("HbA1c")){
                    Intent newIntent = new Intent(DeletePopUpWindow.this, GlavnaAktivnost.class);
                    newIntent.putExtra("usp_poruka","HbA1c");
                    newIntent.putExtra("dlt_poruka","HbA1c test je obrisan!");
                    startActivity(newIntent);
                }
                if(tabela.equals("Mjerenje")){
                    Intent newIntent = new Intent(DeletePopUpWindow.this, GlavnaAktivnost.class);
                    newIntent.putExtra("usp_poruka","Mjerenje");
                    newIntent.putExtra("dlt_poruka","Mjerenje uspješno obrisano");
                    startActivity(newIntent);
                }
                finish();
            }

        });

                btnCancel.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }


}
