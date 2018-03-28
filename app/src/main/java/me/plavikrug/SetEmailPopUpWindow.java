package me.plavikrug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetEmailPopUpWindow extends AppCompatActivity {
    TextView lblCouldntFindEmail;
    EditText txtEmailSet;
    Button btnSubmitSetEmail;
    SharedPreferences.Editor editor;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    Intent prijemIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_email_pop_up_window);
        lblCouldntFindEmail = (TextView) findViewById(R.id.lblCouldntFindEmail);
        txtEmailSet = (EditText) findViewById(R.id.txtEmailAdresaSet);
        btnSubmitSetEmail = (Button) findViewById(R.id.btnSubmitSetEmail);
        prijemIntent = getIntent();

        txtEmailSet.addTextChangedListener( new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validate(s.toString())){
                    btnSubmitSetEmail.setEnabled(true);
                    btnSubmitSetEmail.setBackgroundColor(Color.parseColor("#1976d2"));
                    btnSubmitSetEmail.setTextColor(Color.parseColor("#FFFFFF"));
                }
                else{
                    btnSubmitSetEmail.setEnabled(false);
                }
            }
        });

        btnSubmitSetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = SetEmailPopUpWindow.this.getSharedPreferences("PODACI", MODE_PRIVATE).edit();
                editor.putString("email",txtEmailSet.getText().toString());
                editor.commit();
                if(prijemIntent.getIntExtra("opcija",0)==1) {
                    Intent backUpIntent = new Intent(SetEmailPopUpWindow.this, VerifyEmail.class);
                    backUpIntent.putExtra("opcija",1);
                    startActivity(backUpIntent);
                    finish();
                }
                else{
                    Intent backUpIntent = new Intent(SetEmailPopUpWindow.this, VerifyEmail.class);
                    backUpIntent.putExtra("opcija",1);
                    startActivity(backUpIntent);
                    finish();
                }
            }
        });

    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
