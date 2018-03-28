package me.plavikrug.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Vuk on 15.3.2018..
 */

public class UniversalTextWatcher implements TextWatcher {
    private EditText editTekst;
    private int keyboardPressed;
    private Calendar kal;
    private int oblik; // 1 znaci da je u pitanju dan, 2 - mjesec, 3 - sat, 4 - minuti
    public UniversalTextWatcher(EditText view, Calendar kal, int oblik) {
        this.editTekst = view;
        this.kal = kal;
        this.oblik = oblik;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    /* charSequence - novi tekst
     * start - mjesto u rijeci na kojem se promjena desava
     * before - duzina teksta koji je zamijenjen
     * count - duzina novog teksta
     */
        switch(oblik){
            case 1:
                // U slucaju da se mijenja tekst za dan
                String dan = charSequence.toString();
                if(dan.length()==1&& count == 0){
                    editTekst.setText("");
                }
                else if(count == 1 && before ==0 && start ==0){
                    int nextDigit = 0;
                    try {
                        nextDigit  = Integer.parseInt(dan.charAt(0)+"");
                    }
                    catch(NumberFormatException e){
                        keyboardPressed = 1;
                    }

                    int prvaCifra = kal.getActualMaximum(Calendar.DAY_OF_MONTH) / 10;
                    if(dan.length()< 1) {
                        if(nextDigit>prvaCifra){
                            editTekst.setText("0" + nextDigit);
                        }
                    }
                    else{ //ovaj dio mi izgleda kao da mu treba promjena
                        if(nextDigit > prvaCifra){
                            editTekst.setText(charSequence.subSequence(1,1));
                        }
                        else if(Integer.parseInt(charSequence+"") > kal.getActualMaximum(Calendar.DAY_OF_MONTH)){
                            editTekst.setText(charSequence.subSequence(0,1));
                        }
                    }

                }
                else if(count >= 1 && start == 1){
                    if(Integer.parseInt(charSequence+"") > kal.getActualMaximum(Calendar.DAY_OF_MONTH)){
                        editTekst.setText(charSequence.subSequence(0,1));
                    }
                }
                break;
            case 2:
                // U slucaju da se mijenja tekst za mjesec
                String mjesec = charSequence.toString();
                if(mjesec.length()==1&& count == 0){
                    editTekst.setText("");
                }
                else if(count == 1 && before ==0 && start ==0){
                    int nextDigit = 0;
                    try {
                        nextDigit  = Integer.parseInt(mjesec);
                    }
                    catch(NumberFormatException e){
                        keyboardPressed = 1;
                    }
                    Log.d("TEXT_WATCHER", "NEXT_DIGIT :" + nextDigit);
                    if(nextDigit>1){
                        editTekst.setText("0"+nextDigit);
                    }


                }
                else if(count == 1 && start == 0 && before != 0){
                    if(Integer.parseInt(charSequence+"") > 2){
                        editTekst.setText(charSequence.subSequence(0,1));
                    }
                }
                else if(count == 1 && start == 1 && before == 0){
                    if(Integer.parseInt(charSequence.charAt(0)+"") >= 2){
                        editTekst.setText(charSequence.subSequence(0,1));
                    }
                }
                break;
            case 3:
                // U slucaju da se mijenja tekst za sat
                String sat = charSequence.toString();
                if(sat.length()==1&& count == 0){
                    editTekst.setText("");
                }
                else if(count == 1 && before ==0 && start ==0){
                    int nextDigit = 0;
                    try {
                        nextDigit  = Integer.parseInt(sat);
                    }
                    catch(NumberFormatException e){
                        keyboardPressed = 1;
                    }

                    if(nextDigit>2){
                        editTekst.setText("0"+nextDigit);
                    }

                }
                else if(count == 1 && start == 1){
                    if(Integer.parseInt(charSequence+"") >= 24){
                        editTekst.setText(charSequence.subSequence(0,1));
                    }
                }
                break;
            case 4:
                // U slucaju da se mijenja tekst za minut
                String minuti = charSequence.toString();
                if(minuti.length()==1&& count == 0){
                    editTekst.setText("");
                }
                else if(count == 1 && before ==0 && start ==0){
                    int nextDigit = 0;
                    try {
                        nextDigit  = Integer.parseInt(minuti);
                    }
                    catch(NumberFormatException e){
                        keyboardPressed = 1;
                    }

                    if(nextDigit>5){
                        editTekst.setText("0"+nextDigit);
                    }

                }
                else if(count == 1 && start == 1){
                    if(Integer.parseInt(charSequence+"") > 59){
                        editTekst.setText(charSequence.subSequence(0,1));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
