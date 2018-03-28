package me.plavikrug;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Vuk on 14.1.2018..
 */

public class ReportNedeljni extends Fragment {

    private GraphView graph;
    private DataBaseSource dbSource;
    private int godina,mjesec, dan;
    private Calendar begginingOfWeek = Calendar.getInstance();
    private String datumFragment; // Potreban je kako bi se mogla pretrazivati baza

    ImageView btnPrevWeek;
    ImageView btnNextWeek;
    TextView lblDisplayedWeek;

    private LineGraphSeries<DataPoint> ponLine;
    private LineGraphSeries<DataPoint> utoLine;
    private LineGraphSeries<DataPoint> sriLine;
    private LineGraphSeries<DataPoint> cetLine;
    private LineGraphSeries<DataPoint> petLine;
    private LineGraphSeries<DataPoint> subLine;
    private LineGraphSeries<DataPoint> nedLine;

    CheckBox chbPon;
    CheckBox chbUto;
    CheckBox chbSri;
    CheckBox chbCet;
    CheckBox chbPet;
    CheckBox chbSub;
    CheckBox chbNed;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graph = (GraphView) view.findViewById(R.id.grafikNedeljni);
        btnPrevWeek = (ImageView) view.findViewById(R.id.btn_prev_week);
        btnNextWeek = (ImageView) view.findViewById(R.id.btn_next_week);
        lblDisplayedWeek = (TextView) view.findViewById(R.id.lblDisplayedWeek);
        dbSource = new DataBaseSource(getActivity());

        //Inicijalizacija linija
        ponLine = new LineGraphSeries<>();
        utoLine = new LineGraphSeries<>();
        sriLine = new LineGraphSeries<>();
        cetLine = new LineGraphSeries<>();
        petLine = new LineGraphSeries<>();
        subLine = new LineGraphSeries<>();
        nedLine = new LineGraphSeries<>();

        //DOdjeljivanje boja linijama
        ponLine.setColor(getResources().getColor(R.color.colorPrimaryDark));
        utoLine.setColor(getResources().getColor(R.color.colorPrimary));
        sriLine.setColor(getResources().getColor(R.color.colorPrimarySvjetlija));
        cetLine.setColor(getResources().getColor(R.color.colorTabBackground));
        petLine.setColor(getResources().getColor(R.color.colorGreenTransform));
        subLine.setColor(getResources().getColor(R.color.colorGreenMiddle));
        nedLine.setColor(getResources().getColor(R.color.colorGreenFinal));

        chbPon = (CheckBox) view.findViewById(R.id.chbPon);
        chbUto = (CheckBox) view.findViewById(R.id.chbUto);
        chbSri = (CheckBox) view.findViewById(R.id.chbSri);
        chbCet = (CheckBox) view.findViewById(R.id.chbCet);
        chbPet = (CheckBox) view.findViewById(R.id.chbPet);
        chbSub = (CheckBox) view.findViewById(R.id.chbSub);
        chbNed = (CheckBox) view.findViewById(R.id.chbNeđ);

        Calendar datum = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        godina = datum.get(Calendar.YEAR);
        mjesec = datum.get(Calendar.MONTH)+1;
        dan = datum.get(Calendar.DAY_OF_MONTH);
        datumFragment = formatDDdotMMdotYYYY(datum);
        getMondayDate(datum);
        changeDisplayedWeek();

        graph.addSeries(ponLine);
        graph.addSeries(utoLine);
        graph.addSeries(sriLine);
        graph.addSeries(cetLine);
        graph.addSeries(petLine);
        graph.addSeries(subLine);
        graph.addSeries(nedLine);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxX(24);
        graph.getViewport().setMaxY(25);

        //Da grafik može da se pomjera lijevo desno, i gore i dolje
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);

        //Doradjivanje labela
        graph.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                //Ukoliko je u pitanju natpis sa x ose
                if(isValueX){
                    double prBroj = (double) ((int) value);
                    double minuti = (double)((int)((value - prBroj) * 60));
                    String minStr = (int) minuti+"";
                    if(minuti<10){
                        minStr = "0" + minStr;
                    }
                    return super.formatLabel(prBroj, isValueX) + ":" + minStr;
                }
                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        iscrtajGrafik();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            RotateAnimation ra = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            lblDisplayedWeek.setAnimation(ra);
        }
        //Dodavanje funkcionalnosti dugmetu za nedelju unazad
        btnPrevWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWeek();
            }
        });

        //Dodavanje funkcionalnosti dugmetu ya nedelju unaprijed
        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWeek();
            }
        });

        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch(compoundButton.getId()){
                    case R.id.chbPon:
                        if(b){
                            //if checked
                            graph.addSeries(ponLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(ponLine);
                        }
                        break;
                    case R.id.chbUto:
                        if(b){
                            //if checked
                            graph.addSeries(utoLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(utoLine);
                        }
                        break;
                    case R.id.chbSri:
                        if(b){
                            //if checked
                            graph.addSeries(sriLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(sriLine);
                        }
                        break;
                    case R.id.chbCet:
                        if(b){
                            //if checked
                            graph.addSeries(cetLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(cetLine);
                        }
                        break;
                    case R.id.chbPet:
                        if(b){
                            //if checked
                            graph.addSeries(petLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(petLine);
                        }
                        break;
                    case R.id.chbSub:
                        if(b){
                            //if checked
                            graph.addSeries(subLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(subLine);
                        }
                        break;
                    case R.id.chbNeđ:
                        if(b){
                            //if checked
                            graph.addSeries(nedLine);
                        }
                        else{
                            //if removed
                            graph.removeSeries(nedLine);
                        }
                        break;

                }
            }
        };

        chbPon.setOnCheckedChangeListener(checkBoxListener);
        chbUto.setOnCheckedChangeListener(checkBoxListener);
        chbSri.setOnCheckedChangeListener(checkBoxListener);
        chbCet.setOnCheckedChangeListener(checkBoxListener);
        chbPet.setOnCheckedChangeListener(checkBoxListener);
        chbSub.setOnCheckedChangeListener(checkBoxListener);
        chbNed.setOnCheckedChangeListener(checkBoxListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_nedeljni, container, false);
    }

    private void getMondayDate(Calendar date){
        begginingOfWeek.setTime(date.getTime());
        switch(date.get(Calendar.DAY_OF_WEEK)){
            case 1:
                begginingOfWeek.add(Calendar.DAY_OF_MONTH, -6);
                break;
            case 2:
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                begginingOfWeek.add(Calendar.DAY_OF_MONTH, (date.get(Calendar.DAY_OF_WEEK)-2)*(-1));
                break;
        }
    }


    private void changeDisplayedWeek(){
        Calendar endOfWeek = Calendar.getInstance();
        endOfWeek.setTime(begginingOfWeek.getTime());
        endOfWeek.add(Calendar.DAY_OF_MONTH, 6);
        lblDisplayedWeek.setText(formatDdMon(begginingOfWeek)+ " - " + formatDdMon(endOfWeek));
    }

    private void previousWeek(){
        begginingOfWeek.add(Calendar.DAY_OF_MONTH, -7);
        changeDisplayedWeek();
        iscrtajGrafik();
    }

    private void nextWeek(){
        begginingOfWeek.add(Calendar.DAY_OF_MONTH, 7);
        changeDisplayedWeek();
        iscrtajGrafik();
    }

    private void iscrtajGrafik(){
        Calendar dayInkrementor = Calendar.getInstance();
        dayInkrementor.setTime(begginingOfWeek.getTime());
        for(int j = 0; j <= 6; j++){
            if(j > 0) {
                dayInkrementor.add(Calendar.DAY_OF_MONTH, 1);
            }
            switch(j){
                case 0:
                    ispuniLiniju(ponLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
                case 1:
                    ispuniLiniju(utoLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
                case 2:
                    ispuniLiniju(sriLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
                case 3:
                    ispuniLiniju(cetLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
                case 4:
                    ispuniLiniju(petLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
                case 5:
                    ispuniLiniju(subLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
                case 6:
                    ispuniLiniju(nedLine, formatDDdotMMdotYYYY(dayInkrementor));
                    break;
            }
        }
    }

    private void ispuniLiniju(LineGraphSeries<DataPoint> linija, String dan){
        dbSource.open();
        Cursor mjerenjaPoDanu = dbSource.getGlMjerenja(dan);
        int brojac = mjerenjaPoDanu.getCount();
        DataPoint[] kord = new DataPoint[brojac];
        mjerenjaPoDanu.moveToFirst();
        int i = 0;

        while(!mjerenjaPoDanu.isAfterLast()){
            double x1 = mjerenjaPoDanu.getDouble(1);
            double y1 = mjerenjaPoDanu.getDouble(2);
            kord[i] = new DataPoint(x1,y1);

            i++;
            mjerenjaPoDanu.moveToNext();
        }

        dbSource.close();
        linija.resetData(kord);
    }

    private String formatDDdotMMdotYYYY(Calendar date){
        String datStr = "";
        String danStr = date.get(Calendar.DAY_OF_MONTH)+"";
        String mjesecStr = (date.get(Calendar.MONTH)+1)+"";
        if(date.get(Calendar.DAY_OF_MONTH) < 10){
            danStr = "0" + danStr;
        }
        if(date.get(Calendar.MONTH)< 10){
            mjesecStr = "0" + mjesecStr;
        }
        datStr = danStr + "." + mjesecStr + "." + date.get(Calendar.YEAR);
        return datStr;
    }
    private String formatDdMon(Calendar date){
        String datStr = "";
        String danStr = date.get(Calendar.DAY_OF_MONTH)+"";
        if(date.get(Calendar.DAY_OF_MONTH) < 10){
            danStr = "0" + danStr;
        }
        String mjesecStr = shortenedMonth(date.get(Calendar.MONTH)+1);
        datStr = danStr + " " + mjesecStr;
        return datStr;
    }

    private String shortenedMonth(int month){
        String mjStr = "";
        switch(month){
            case 1:
                mjStr = getString(R.string.Jan);
                break;
            case 2:
                mjStr = getString(R.string.Feb);
                break;
            case 3:
                mjStr = getString(R.string.Mar);
                break;
            case 4:
                mjStr = getString(R.string.Apr);
                break;
            case 5:
                mjStr =  getString(R.string.Maj);
                break;
            case 6:
                mjStr =  getString(R.string.Jun);
                break;
            case 7:
                mjStr =  getString(R.string.Jul);
                break;
            case 8:
                mjStr =  getString(R.string.Avg);
                break;
            case 9:
                mjStr =  getString(R.string.Sep);
                break;
            case 10:
                mjStr =  getString(R.string.Okt);
                break;
            case 11:
                mjStr =  getString(R.string.Nov);
                break;
            case 12:
                mjStr =  getString(R.string.Dec);
                break;
        }
        return mjStr;
    }

    public void checkBoxClicked(View view){
        switch (view.getId()){
            case R.id.chbPon:
                if(chbPon.isChecked()){
                    graph.addSeries(ponLine);
                }
                else{
                    graph.removeSeries(ponLine);
                }
                break;
        }
    }
}
