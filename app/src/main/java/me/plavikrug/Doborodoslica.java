package me.plavikrug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Doborodoslica extends Activity {

    private ImageView plaviKrug;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doborodoslica);
        Thread mojThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(),GlavnaAktivnost.class);
                startActivity(intent);
                finish();
            }
        };

        mojThread.start();
        plaviKrug = (ImageView) findViewById(R.id.plavikrug);
    }
}
