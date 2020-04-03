package me.plavikrug;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Calendar;
import java.util.Locale;

import me.plavikrug.db.DataBaseSource;

/**
 * Created by Vuk on 31.7.2017..
 */

@SuppressLint("ValidFragment")
public class ReportDnevni extends Fragment {


    private LineGraphSeries<DataPoint> serije;
    private PointsGraphSeries<DataPoint> tacke;
    private PointsGraphSeries<DataPoint> hipo;
    private PointsGraphSeries<DataPoint> hiper;
    private PointsGraphSeries<DataPoint> insulin;
    private GraphView graph;
    private DataBaseSource dbSource;
    private DatePickerDialog.OnDateSetListener datumListener;
    private Button btnDan;
    private Button btnSendComment;
    int godina;
    int mjesec;
    int dan;
    int edit; // edit = 0 znaci da nije unosen komentar ranije i da fragment treba biti u fazi inserta
              // edit = 1 fragment je spreman za update
    String mjesecStr, danStr;
    EditText txtAddComment;

    String datumFragment;
    String biljeska;
    String[] lblDataInsulin;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Izvještaj");
        graph = (GraphView) view.findViewById(R.id.grafik);
        btnDan = (Button) view.findViewById(R.id.rep_date);
        txtAddComment = (EditText) view.findViewById(R.id.txt_add_comment);
        btnSendComment = (Button) view.findViewById(R.id.btn_send_comment);
        edit = 0;
        double x,y = 0;

        serije = new LineGraphSeries<>();
        tacke = new PointsGraphSeries<>();
        hipo = new PointsGraphSeries<>();
        hiper = new PointsGraphSeries<>();
        insulin = new PointsGraphSeries<>();

        tacke.setTitle("U redu");
        hipo.setTitle("Hipo");
        hiper.setTitle("Hiper");
        insulin.setTitle("Insulin");

        tacke.setSize(10);
        hipo.setSize(10);
        hiper.setSize(10);
        insulin.setSize(10);

        serije.setColor(Color.BLACK);
        hipo.setColor(Color.rgb(255,165,0));
        hiper.setColor(Color.RED);
        insulin.setColor(Color.rgb(204,255,144));

        insulin.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                String dtTekst = dataPoint.getX() + "";
                int[] iik = getInsulinAndKorekcijaFromLabels(dtTekst); // iik je insulin i korekcija
                int zbir = iik[0]+ iik[1];
                paint.setTextSize(40);
                paint.setColor(Color.rgb(64,119,77));
                canvas.drawText(zbir+"x",x-40,y,paint);

                paint.setColor(Color.rgb(150,204,106));
                canvas.drawOval(myOval(20,40,x+13,y - 12),paint);

                paint.setColor(Color.rgb(204,255,144));
                canvas.drawOval(myOval(16,36,x+13,y - 12),paint);
            }
        });

        Calendar c = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        godina = c.get(Calendar.YEAR);
        mjesec = c.get(Calendar.MONTH)+1;
        dan = c.get(Calendar.DAY_OF_MONTH);

        mjesecStr = mjesec + "";
        danStr = dan + "";
         if(mjesec<10){
             mjesecStr = "0" + mjesec;
         }
        if(dan<10){
            danStr = "0" + dan;
        }
        if(getArguments().containsKey("datumFragment")){
            String datum = getArguments().getString("datumFragment");
            if(datum.length()==0){
                btnDan.setText(danStr + "." + mjesecStr + "." + godina);
                datumFragment= danStr + "." + mjesecStr + "." + godina;
            }
            else{
                datumFragment = getArguments().getString("datumFragment");
                btnDan.setText(datumFragment);
            }
        }
        btnDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datumDialog = new DatePickerDialog(getContext(),
                        R.style.Theme_DelegateWindow,
                        datumListener,
                        godina,mjesec-1,dan);
                datumDialog.setTitle("Datum");
                datumDialog.setButton(datumDialog.BUTTON_POSITIVE,"U redu",datumDialog);
                datumDialog.setButton(datumDialog.BUTTON_NEGATIVE,"Otkaži",datumDialog);
                datumDialog.show();
            }
        });

        //Na promjenu datima, potrebno je ponovo iscrtati grafik
        datumListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                godina = year;
                mjesec = month+1;
                dan = dayOfMonth;

                danStr = dan + "";
                mjesecStr = mjesec + "";

                if(dan < 10){
                    danStr = "0" + dan;
                }
                if(mjesec < 10){
                    mjesecStr = "0" + mjesec;
                }
                datumFragment = danStr+"."+mjesecStr+"."+year;
                btnDan.setText(datumFragment);
                iscrtajGrafik();


                //Za sad sam samo unio nove linije, trebaju mi i tačke za novi dan
            }
        };


        dbSource = new DataBaseSource(getActivity());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxX(24);
        graph.getViewport().setMaxY(25);

        //Da grafik može da se pomjera lijevo desno
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);

        //Da je moguće uveličati grafik
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

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


        //ponovno iscrtavanje grafika
        iscrtajGrafik();
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);


        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtAddComment.getText().toString().length()>0){
                    switch(btnSendComment.getText().toString().length()){
                        case 6:  //Ako na dugmetu piše 'napiši' (čitaj: 6 slova) to znači da se vrši unos komentara
                            String tekst = txtAddComment.getText().toString();
                            txtAddComment.setText(tekst);
                            txtAddComment.setEnabled(false);
                            txtAddComment.setBackground(null);
                            btnSendComment.setText("Sredi");
                            dbSource.open();
                            if(edit==0) {
                                dbSource.insertBiljeska(btnDan.getText().toString(), tekst);
                            }
                            else{
                                dbSource.updateBiljeske(datumFragment,tekst);
                            }
                            dbSource.close();
                            break;

                        case 5: //Ako na dugmetu piše 'sredi' (čitaj: 6 slova) to znači da se vrši ažuriranje komentara
                            txtAddComment.setEnabled(true);
                            txtAddComment.setBackgroundResource(R.drawable.komentar);
                            btnSendComment.setText("Napiši");
                        break;


                    }

                }
            }
        });


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_dnevni, container, false);
    }

    @SuppressLint("ValidFragment")
    public static final ReportDnevni newInstance(String datumPar){
        ReportDnevni rd = new ReportDnevni();
        Bundle bundle = new Bundle(1);
        bundle.putString("datumFragment",datumPar);
        rd.setArguments(bundle);
        return rd;
    }

    public int[] getInsulinAndKorekcijaFromLabels(String vrijeme){
        int[] inslAndKor = new int[2];
        for(int j = 0; j < lblDataInsulin.length; j++){
            String[] st = lblDataInsulin[j].split(";");
            if(vrijeme.equals(st[2])){
                inslAndKor[0] = Integer.parseInt(st[0]);
                inslAndKor[1] = Integer.parseInt(st[1]);
                break;
            }
        }
        return inslAndKor;
    }

    public RectF myOval(float width, float height, float x, float y){
        float halfW = width/2;
        float halfH = height/2;
        return new RectF(x-halfW, y-halfH, x+halfW, y+halfH);
    }

    private void iscrtajGrafik(){
        graph.removeAllSeries();
        dbSource.open();
        Cursor noviPodaci = dbSource.getGlMjerenja(datumFragment);
        int brojacc = noviPodaci.getCount();
        noviPodaci.moveToFirst();
        DataPoint[] noveTacke = new DataPoint[brojacc];
        int nt = 0;
        DataPoint[] noveHipo = new DataPoint[brojacc];
        int nho = 0;
        DataPoint[] noveHiper = new DataPoint[brojacc];
        int nhi = 0;
        DataPoint[] kord = new DataPoint[brojacc];
        int ink = 0;
        while(!noviPodaci.isAfterLast()){
            double x1 = noviPodaci.getDouble(1);
            double y1 = noviPodaci.getDouble(2);
            kord[ink] = new DataPoint(x1,y1);

            if(y1<5){
                noveHipo[nho] = new DataPoint(x1,y1);
                nho++;
               // hipo.appendData(new DataPoint(x1,y1),false,brojacc+1);
            }
            else if(y1>=5 && y1<= 10){
                noveTacke[nt] = new DataPoint(x1,y1);
                nt++;
                //tacke.appendData(new DataPoint(x1,y1),false,brojacc+1);
            }
            else{
                noveHiper[nhi] = new DataPoint(x1,y1);
                nhi++;
                //hiper.appendData(new DataPoint(x1,y1),false,brojacc+1);
            }

            ink++;
            noviPodaci.moveToNext();
        }
        serije.resetData(kord);

        Cursor nTerapija = dbSource.getTerapija(datumFragment);
        nTerapija.moveToFirst();
        int brojT = nTerapija.getCount();
        lblDataInsulin = new String[brojT];
        int it = 0;
        double xt = 0;
        double yt = 0;
        DataPoint[] noveInsulin = new DataPoint[brojT];
        while(!nTerapija.isAfterLast()){
            xt = nTerapija.getDouble(3);
            yt = nTerapija.getDouble(4);
            lblDataInsulin[it] = nTerapija.getInt(0) + ";" + nTerapija.getInt(1) + ";" + nTerapija.getDouble(3);
            noveInsulin[it] = new DataPoint(xt,yt);
            //insulin.appendData(new DataPoint(xt,yt),false,brojT+1);
            nTerapija.moveToNext();
            it++;
        }

        biljeska = dbSource.findBiljeskaOnDay(datumFragment);
        if(biljeska.length()>0){
            txtAddComment.setText(biljeska);
            txtAddComment.setEnabled(false);
            txtAddComment.setBackground(null);
            edit = 1;
            btnSendComment.setText("Sredi");
        }
        else{
            txtAddComment.setText(biljeska);
            txtAddComment.setEnabled(true);
            txtAddComment.setBackgroundResource(R.drawable.komentar);
            edit = 0;
            btnSendComment.setText("Napiši");

        }

        tacke.resetData(removeNullsFromArray(noveTacke));
        hipo.resetData(removeNullsFromArray(noveHipo));
        hiper.resetData(removeNullsFromArray(noveHiper));
        insulin.resetData(removeNullsFromArray(noveInsulin));
        dbSource.close();
        graph.addSeries(serije);
        graph.addSeries(tacke);
        graph.addSeries(hipo);
        graph.addSeries(hiper);
        graph.addSeries(insulin);
    }


    private DataPoint[] removeNullsFromArray(DataPoint[] niz){
        DataPoint[] noviNiz;
        int notNulls = 0;
        for(int n = 0; n < niz.length; n++){
            if(niz[n] != null){
               notNulls++;
            }
        }

        noviNiz = new DataPoint[notNulls];
        for(int nn = 0; nn < notNulls; nn++){
            if(niz[nn] != null){
                noviNiz[nn] = niz[nn];
            }
        }

        return noviNiz;
    }
}
