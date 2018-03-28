package me.plavikrug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

public class EditPopUpWindow extends Activity {
    String fullDatum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pop_up_window);
        Button editButton = (Button) findViewById(R.id.editButton);
        Intent getter = getIntent();
        fullDatum = getter.getStringExtra("full_datum");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*0.2));
        getWindow().setGravity(Gravity.BOTTOM);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToEdit = new Intent(EditPopUpWindow.this, DodajMjerenje.class);
                goToEdit.putExtra("full_datum", fullDatum);
                startActivity(goToEdit);
            }
        });


    }
}
