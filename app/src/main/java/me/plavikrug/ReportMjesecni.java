package me.plavikrug;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Locale;

import me.plavikrug.db.DataBaseSource;

/**
 * Created by Vuk on 21.1.2018..
 */

public class ReportMjesecni extends Fragment {

    private GraphView graph;
    private DataBaseSource dbSource;
    private int godina, mjesec, dan;
    private Calendar begginingOfMonth = Calendar.getInstance();
    private String datumFragment;

    ImageView btnPrevMonth;
    ImageView btnNextMonth;
    TextView lblDisplayedMonth;

    private LineGraphSeries<DataPoint> avgLine;
    private LineGraphSeries<DataPoint> minLine;
    private LineGraphSeries<DataPoint> maxLine;

    TextView txtProsjek;
    TextView txtMin;
    TextView txtMaks;
    TextView txtInsulinUkupno;
    TextView txtInsulinPoDanu;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_mjesecni, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graph = (GraphView) view.findViewById(R.id.grafikMjesecni);
        btnPrevMonth = (ImageView) view.findViewById(R.id.btn_prev_month);
        btnNextMonth = (ImageView) view.findViewById(R.id.btn_next_month);
        lblDisplayedMonth = (TextView) view.findViewById(R.id.lblDisplayedMonth);

        txtProsjek = (TextView) view.findViewById(R.id.txtProsjek);
        txtMin = (TextView) view.findViewById(R.id.txtMin);
        txtMaks = (TextView) view.findViewById(R.id.txtMaks);
        txtInsulinUkupno = (TextView) view.findViewById(R.id.txtInsulinUkupno);
        txtInsulinPoDanu = (TextView) view.findViewById(R.id.txtInsulinPoDanu);

        dbSource = new DataBaseSource(getActivity());

        avgLine = new LineGraphSeries<>();
        minLine = new LineGraphSeries<>();
        maxLine = new LineGraphSeries<>();

        Calendar datum = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        godina = datum.get(Calendar.YEAR);
        mjesec = datum.get(Calendar.MONTH)+1;
        dan = datum.get(Calendar.DAY_OF_MONTH);


        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(R.color.colorPrimary));
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));

        avgLine.setDrawAsPath(true);
        avgLine.setCustomPaint(paint);
        avgLine.setDataPointsRadius(10);
        minLine.setColor(getResources().getColor(R.color.hypoColor));
        maxLine.setColor(getResources().getColor(R.color.colorRed));

        avgLine.setTitle(getResources().getString(R.string.lblProsjek));
        minLine.setTitle(getResources().getString(R.string.lblMin));
        maxLine.setTitle(getResources().getString(R.string.lblMaks));

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
        changeDisplayedMonth();
        getDataFromMonth();
        iscrtajGrafik();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            RotateAnimation ra = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            lblDisplayedMonth.setAnimation(ra);
        }
        //Dodavanje funkcionalnosti dugmetu za nedelju unazad
        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonth();
            }
        });

        //Dodavanje funkcionalnosti dugmetu ya nedelju unaprijed
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonth();
            }
        });

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
    }

    private void iscrtajGrafik(){
        graph.removeAllSeries();

        ispuniLiniju("avg");
        ispuniLiniju("min");
        ispuniLiniju("max");

        graph.addSeries(avgLine);
        graph.addSeries(minLine);
        graph.addSeries(maxLine);
    }

    private void ispuniLiniju(String lineType){
        DataPoint[] tacke = new DataPoint[6];
        int a = 0;
        int sat = 0;
        while(a<6){
            switch(lineType){
                case "avg":
                    tacke[a] = new DataPoint(sat + 2, calcAverage(segmentacijaPodatakaPoVremenu(sat)));
                    break;
                case "min":
                    tacke[a] = new DataPoint(sat + 2, findMin(segmentacijaPodatakaPoVremenu(sat)));
                    break;
                case "max":
                    tacke[a] = new DataPoint(sat + 2, findMax(segmentacijaPodatakaPoVremenu(sat)));
                    break;
            }
            a++;
            sat+=4;
        }

        switch (lineType){
            case "avg":
                avgLine.resetData(tacke);
                break;
            case "min":
                minLine.resetData(tacke);
                break;
            case "max":
                maxLine.resetData(tacke);
                break;

        }
    }
    private double[] segmentacijaPodatakaPoVremenu(int startSat){
        double podaci[];
        String mjStr = mjesec + "";
        if(mjesec<10){
            mjStr = "0" + mjStr;
        }
        String monthToSearchIn = mjStr + "." + godina;
        dbSource.open();
        Cursor cursor = dbSource.getMonthData(monthToSearchIn, startSat);
        cursor.moveToFirst();
        podaci = new double[cursor.getCount()];
        int i = 0;
        while(!cursor.isAfterLast()){
            podaci[i] = cursor.getDouble(0);
            i++;
            cursor.moveToNext();
        }

        dbSource.close();
        return podaci;
    }

    private void previousMonth(){
        begginingOfMonth.add(Calendar.MONTH, -1);
        mjesec = begginingOfMonth.get(Calendar.MONTH) + 1;
        godina = begginingOfMonth.get(Calendar.YEAR);
        changeDisplayedMonth();
        getDataFromMonth();
        iscrtajGrafik();
    }

    private void nextMonth(){
        begginingOfMonth.add(Calendar.MONTH, 1);
        mjesec = begginingOfMonth.get(Calendar.MONTH) + 1;
        godina = begginingOfMonth.get(Calendar.YEAR);
        changeDisplayedMonth();
        getDataFromMonth();
        iscrtajGrafik();
    }

    private void changeDisplayedMonth(){
        lblDisplayedMonth.setText(formatMonYear(begginingOfMonth));
    }

    private void getDataFromMonth(){
        dbSource.open();

        String mjStr = mjesec + "";
        if(mjesec<10){
            mjStr = "0" + mjStr;
        }
        String monthToSearchIn = mjStr + "." + godina;

        txtProsjek.setText(dbSource.getFromMonth("AVG", "REZULTATI", "izmjereno", "vrijeme", monthToSearchIn)+"");
        txtMin.setText(dbSource.getFromMonth("MIN", "REZULTATI", "izmjereno", "vrijeme", monthToSearchIn)+"");
        txtMaks.setText(dbSource.getFromMonth("MAX", "REZULTATI", "izmjereno", "vrijeme",monthToSearchIn)+"");
        txtInsulinUkupno.setText(dbSource.getFromMonth("SUM", "TERAPIJA", "jed_insl+jed_kor", "dan", monthToSearchIn)+"");
        txtInsulinPoDanu.setText(dbSource.getFromMonth("AVG", "TERAPIJA", "jed_insl+jed_kor", "dan",monthToSearchIn)+"");
        dbSource.close();
    }

    private double calcAverage(double[] array){
        double avg = 0;
        double sum = 0;
        for(int i = 0; i < array.length; i++){
            sum += array[i];
        }
        avg = sum / array.length;
        return avg;
    }

    private double findMax(double[] array){
        double maks = 0;
        for(int j = 0; j < array.length; j++){
            if(array[j]> maks){
                maks = array[j];
            }
        }
        return maks;
    }

    private double findMin(double[] array){
        double min = findMax(array);
        for(int j = 0; j < array.length; j++){
            if(array[j]< min){
                min = array[j];
            }
        }
        return min;
    }

    private String formatMonYear(Calendar date){
        String format = "";
        format = fullMonthName(date.get(Calendar.MONTH)+1) + " " + date.get(Calendar.YEAR);
        return format;
    }

    private String fullMonthName(int m){
        String month = "";
        switch(m){
            case 1:
                month =getString(R.string.Januar);
                break;
            case 2:
                month =getString(R.string.Februar);
                break;
            case 3:
                month =getString(R.string.Mart);
                break;
            case 4:
                month =getString(R.string.April);
                break;
            case 5:
                month =getString(R.string.Maj);
                break;
            case 6:
                month =getString(R.string.Jun);
                break;
            case 7:
                month =getString(R.string.Jul);
                break;
            case 8:
                month =getString(R.string.Avgust);
                break;
            case 9:
                month =getString(R.string.Septembar);
                break;
            case 10:
                month =getString(R.string.Oktobar);
                break;
            case 11:
                month =getString(R.string.Novembar);
                break;
             case 12:
                month =getString(R.string.Decembar);
                break;

        }
        return month;
    }
}
