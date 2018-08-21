package me.plavikrug.pdf;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import me.plavikrug.AsyncResponse;
import me.plavikrug.BuildConfig;
import me.plavikrug.PDFReportPopUpWindow;
import me.plavikrug.R;
import me.plavikrug.model.PDFParameters;

/**
 * Created by Vuk on 10.2.2018..
 */

public class PDFReportGenerator extends AsyncTask<PDFParameters,Void, Uri>{

    public AsyncResponse delegate = null;
    private static String[][] nizNizova;
    private static int savedRed = 0;

    public static Uri writeHello(PdfDocument doc, Resources resursi, Cursor podaci, String danOd, String danDo) throws IOException {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Calendar calendar = Calendar.getInstance();

            int brojDana = setujKalendare(danOd, danDo);
            int brojStranica = (int) Math.round((double)brojDana / 31.0);
            if(brojStranica == 0){
                brojStranica = 1;
            }
            int ostatak = brojDana;
            for(int s = 1; s <= brojStranica; s++) {

                //Inicijalizacija stranice
                PdfDocument.PageInfo.Builder pageInfoBuilder = new PdfDocument.PageInfo.Builder(494, 700, s);
                PdfDocument.PageInfo pageInfo = pageInfoBuilder.create();
                PdfDocument.Page page = doc.startPage(pageInfo);

                // Početak crtanja zaglavlja
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#3F48CC"));
                paint.setStrokeWidth(3);
                Bitmap logo = BitmapFactory.decodeResource(resursi, R.drawable.plavikrug);
                page.getCanvas().drawBitmap(logo, null, new Rect(1, 1, 51, 61), paint);
                page.getCanvas().drawLine(1, 68, 493, 68, paint);


                if(s == 1) {
                    paint.setColor(Color.BLACK);
                    paint.setStrokeWidth(2);
                    paint.setFakeBoldText(true);
                    paint.setTextSize(10);
                    String naslov = "Dnevnik mjerenja nivoa šećera u krvi i primljenih količina insulina";
                    //String za = "Izvještaj za: " + korData.getString()
                    page.getCanvas().drawText(naslov, 90, 40, paint);
                    String danOdSub = danOd.substring(6) + "." + danOd.substring(4,6) + "." + danOd.substring(0,4);
                    String danDoSub = danDo.substring(6) + "." + danDo.substring(4,6) + "." + danDo.substring(0,4);
                    page.getCanvas().drawText("Izvještaj napravljen za period: " + danOdSub + " - " + danDoSub , 90, 60, paint);
                    //                                          Kraj crtanja zaglavlja//
                }
                //Početak crtanja tabele
                Rect tabela = new Rect(1, 72, 487, 648);
                nacrtajĆeliju(page, tabela, 0, 0, Color.BLACK, null, 1, paint);

                // Kraj crtanja tabele
                nacrtajPrveRedove(page, paint);
                smjestiPodatkeUNiz(podaci, danOd, danDo);
                if(ostatak >= 31) {
                    popuniTabelu(page, new Rect(2, 91, 56, 109), (s-1)*31,31, paint);
                }
                else{
                    Log.d("OSTATAK", "OSTATAK:" + ostatak);
                    popuniTabelu(page, new Rect(2, 91, 56, 109), brojDana-ostatak,ostatak, paint);
                }
                nacrtajLegendu(page, paint);
                doc.finishPage(page);
                ostatak -= 31;
            }

            String datumVrijeme = calendar.get(Calendar.DATE) + "" + (calendar.get(Calendar.MONTH) +1)+ "" +
                    calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.HOUR_OF_DAY) + "" +
                    calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND) + "";
            String pdfName = "Plavi_Krug_Izvjestaj" + datumVrijeme+ ".pdf";
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "PlaviKrug");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            File outputFile = new File(folder, pdfName);

            try {
                outputFile.createNewFile();
                OutputStream out = new FileOutputStream(outputFile);
                doc.writeTo(out);
                doc.close();
                out.close();
                return FileProvider.getUriForFile(PDFReportPopUpWindow.context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        return null;
    }

    @SuppressLint("NewApi")
    private static void nacrtajĆeliju(PdfDocument.Page stranica, Rect granicnik, int bojaSlova, int bojaPoz, int bojaGranice, String tekst, int tip,Paint crtac){
        Rect unutrasnji = new Rect(granicnik.left + 1, granicnik.top + 1, granicnik.right - 1, granicnik.bottom - 1);

        //crtanje granica
        if(bojaGranice != 0) {
            crtac.setStrokeWidth(1);
            crtac.setStyle(Paint.Style.STROKE);
            crtac.setColor(bojaGranice);
            stranica.getCanvas().drawRect(granicnik, crtac);
        }

       //crtanje pozadine
        if(bojaPoz != 0) {
            crtac.setColor(bojaPoz);
            crtac.setStyle(Paint.Style.FILL);
            stranica.getCanvas().drawRect(unutrasnji, crtac);
        }

        //crtanje teksta
        if(tekst != null) {
            crtac.setColor(bojaSlova);
            crtac.setTextSize(8);
            crtac.setFakeBoldText(false);
            if(tip == 1) {
                stranica.getCanvas().drawText(tekst, unutrasnji.left + 2, unutrasnji.top + 10, crtac);
            }
            else if( tip == 2){
                //Raznobojan tekst
                unutrasnji.left += 2;
                StringTokenizer tokenizer = new StringTokenizer(tekst,";");
                int delim = 0;
                while(tokenizer.hasMoreTokens()){
                    if(delim > 0){
                        crtac.setColor(Color.BLACK);
                        stranica.getCanvas().drawText(";",unutrasnji.left, unutrasnji.top + 10, crtac);
                        float[] duznicar = new float[1];
                        crtac.breakText(";",0,1,true,100,duznicar);
                        unutrasnji.left += duznicar[0];
                    }
                    StringTokenizer razdvajac = new StringTokenizer(tokenizer.nextToken(), "/");
                    int br = 0;
                    while(razdvajac.hasMoreTokens()){
                        if(br==0) {
                            String mjerenje = razdvajac.nextToken();
                            if (Double.parseDouble(mjerenje) >= 4 && Double.parseDouble(mjerenje) <= 10) {
                                crtac.setColor(Color.parseColor("#3F48CC"));
                                stranica.getCanvas().drawText(mjerenje, unutrasnji.left, unutrasnji.top + 10, crtac);
                            } else if (Double.parseDouble(mjerenje) < 4) {
                                crtac.setColor(Color.parseColor("#FFA500"));
                                stranica.getCanvas().drawText(mjerenje, unutrasnji.left, unutrasnji.top + 10, crtac);
                            } else if (Double.parseDouble(mjerenje) > 10) {
                                crtac.setColor(Color.parseColor("#FF0000"));
                                stranica.getCanvas().drawText(mjerenje, unutrasnji.left, unutrasnji.top + 10, crtac);
                            }
                            float[] duznicar = new float[mjerenje.length()];
                            crtac.breakText(mjerenje,0,mjerenje.length(),true,100,duznicar);
                            for(int d = 0; d < duznicar.length; d++){
                                unutrasnji.left += duznicar[d];
                            }
                        }
                        else{
                            String insl = "/" + razdvajac.nextToken();
                            crtac.setColor(Color.parseColor("#AAEE70"));
                            stranica.getCanvas().drawText(insl, unutrasnji.left, unutrasnji.top + 10, crtac);
                            float[] duznicar = new float[insl.length()];
                            crtac.breakText(insl,0,insl.length(),true,100,duznicar);
                            for(int d = 0; d < duznicar.length; d++){
                                unutrasnji.left += duznicar[d];
                            }
                        }
                        br++;

                    }
                    delim++;
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private static void nacrtajPrveRedove(PdfDocument.Page stranica, Paint crtac){
        //Crtanje prvog reda
        Rect ćelija = new Rect(2,73, 56, 91);
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "Datum", 1,crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "Rano ujutru", 1, crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "Doručak", 1,crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "+2h", 1, crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "Ručak", 1, crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "+2h", 1, crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "Večera", 1, crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, "+2h", 1, crtac);
        ćelija.left += 54; ćelija.right += 54;
        nacrtajĆeliju(stranica, ćelija, Color.WHITE, Color.parseColor("#3F48CC"), Color.BLACK, null, 1, crtac);
        ćelija.top += 6;
        crtac.setTextSize(6);
        crtac.setColor(Color.WHITE);
        stranica.getCanvas().drawText("Prije", ćelija.left +2, ćelija.top, crtac);
        stranica.getCanvas().drawText("spavanja", ćelija.left + 2, ćelija.top + 7, crtac);

    }

    private static void popuniTabelu(PdfDocument.Page stranica, Rect ćelija, int poc,int str, Paint crtac){
        for(int i = poc; i < poc + str; i++ ) {
            ćelija.left = 2;
            ćelija.right = 56;
            nacrtajĆeliju(stranica, ćelija, Color.BLACK, Color.WHITE, Color.BLACK, nizNizova[i][0], 1, crtac);
            for (int j = 0; j < 9; j++) {
                if(j>0){
                    nacrtajĆeliju(stranica, ćelija, Color.BLACK, Color.WHITE, Color.BLACK, nizNizova[i][j], 2, crtac);
                }
                ćelija.left += 54;
                ćelija.right += 54;
            }
            ćelija.top += 18;
            ćelija.bottom += 18;
        }
    }

    private static void smjestiPodatkeUNiz(Cursor podaci, String danOd, String danDo){

        int brojRedova = setujKalendare(danOd, danDo);
        Calendar kal = Calendar.getInstance();
        kal.set(Integer.parseInt(danOd.substring(0,4)),Integer.parseInt(danOd.substring(4,6))-1, Integer.parseInt(danOd.substring(6,8)), 0,0,0);
        nizNizova = new String[brojRedova][9];
        for(int i = 0; i < brojRedova; i++){
            String danPom = finesaDatum(kal.get(Calendar.DAY_OF_MONTH));
            String mjesecPom = finesaDatum(kal.get(Calendar.MONTH)+1);
            String godinaPom = finesaDatum(kal.get(Calendar.YEAR));
            nizNizova[i][0] = danPom + "." + mjesecPom + "." + godinaPom;
            kal.set(Calendar.DAY_OF_MONTH, kal.get(Calendar.DAY_OF_MONTH)+1);
        }

        podaci.moveToFirst();
        while(!podaci.isAfterLast()){
            processRow(podaci.getString(0), podaci.getFloat(1), podaci.getInt(3), podaci.getInt(4));
            podaci.moveToNext();
        }

    }

    private static void processRow(String cursorVrijeme, float cursorIzmjereno, int cursorTip, int cursorTerapija){
        String niz[] = new String[2];
        String kolona = "";
        String polje = "";
        String datum = cursorVrijeme.substring(0,10);
        int red = nađiRed(datum);
        int sati = Integer.parseInt(cursorVrijeme.substring(11,13));
        float rounded = cursorIzmjereno;
        polje = rounded + "";
        if (cursorTerapija != 0) {
            polje += "/ " + cursorTerapija;
        }
        if (sati >= 0 && sati < 6) {
            kolona = 1 + "";
        } else if (sati >= 6 && sati <= 8) {
            kolona = 2 + "";
        } else if (sati >= 9 && sati <= 11) {
            kolona = 3 + "";
        } else if (sati >= 12 && sati <= 14) {
            kolona = 4 + "";
        } else if (sati >= 15 && sati <= 16) {
            kolona = 5 + "";
        } else if (sati >= 17 && sati <= 19) {
            kolona = 6 + "";
        } else if (sati >= 20 && sati <= 21) {
            kolona = 7 + "";
        } else if (sati >= 22 && sati <= 23) {
            kolona = 8 + "";
        }
        int brKol = Integer.parseInt(kolona);
        if (nizNizova[red][brKol] == null) {
            nizNizova[red][brKol] = polje;
        } else {
            nizNizova[red][brKol] += "; " + polje;

        }

    }

    private static int nađiRed(String datum){
        int povrat = 0;
        for (int n = savedRed; n < nizNizova.length; n++){
            if(datum.equals(nizNizova[n][0])){
                savedRed = n;
                povrat = n;
                return povrat;
            }
        }
        return povrat;
    }

    private static int setujKalendare(String danOd, String danDo){
        int brojDana = 0;
        DateTime dan1 = new DateTime(Integer.parseInt(danOd.substring(0,4)),Integer.parseInt(danOd.substring(4,6)),Integer.parseInt(danOd.substring(6,8)),0,0);
        DateTime dan2 = new DateTime(Integer.parseInt(danDo.substring(0,4)),Integer.parseInt(danDo.substring(4,6)),Integer.parseInt(danDo.substring(6,8)),0,0);

        brojDana = Days.daysBetween(dan1.withTimeAtStartOfDay(),dan2.withTimeAtStartOfDay()).getDays();
        brojDana++;

        Log.d("BROJ_DANA", "BROJ_DANA: " + brojDana);
        return brojDana;
    }

    private static int getBrojMjeseci(String danOd, String danDo){
        int brojMjeseci = 0;
        DateTime dan1 = new DateTime(Integer.parseInt(danOd.substring(0,4)),Integer.parseInt(danOd.substring(4,6)),Integer.parseInt(danOd.substring(6,8)),0,0);
        DateTime dan2 = new DateTime(Integer.parseInt(danDo.substring(0,4)),Integer.parseInt(danDo.substring(4,6)),Integer.parseInt(danDo.substring(6,8)),0,0);

        brojMjeseci = Months.monthsBetween(dan1.withTimeAtStartOfDay(),dan2.withTimeAtStartOfDay()).getMonths();
        brojMjeseci++;

        Log.d("BROJ_DANA", "BROJ_DANA: " + brojMjeseci);
        return brojMjeseci;
    }

    private static String finesaDatum(int vrijednost){
        String povrat = "";
        if(vrijednost < 10){
            povrat = "0" + vrijednost;
        }
        else{
            povrat = vrijednost + "";
        }

        return povrat;
    }

    @SuppressLint("NewApi")
    private static void nacrtajLegendu(PdfDocument.Page stranica, Paint crtac){
        crtac.setColor(Color.BLACK);
        crtac.setFakeBoldText(false);
        crtac.setTextSize(7);
        crtac.setColor(Color.parseColor("#3F48CC"));
        crtac.setStyle(Paint.Style.FILL);
        stranica.getCanvas().drawRect(new Rect(360, 660, 365, 665),crtac);
        crtac.setColor(Color.BLACK);
        stranica.getCanvas().drawText("U granicama ( > 4 mmol/L i <10 mmol/L)", 370, 665, crtac);

        crtac.setColor(Color.parseColor("#FFA500"));
        stranica.getCanvas().drawRect(new Rect(360, 670, 365, 675),crtac);
        crtac.setColor(Color.BLACK);
        stranica.getCanvas().drawText("Hipoglikemija ( <4 mmol/L)", 370, 675, crtac);

        crtac.setColor(Color.parseColor("#FF0000"));
        stranica.getCanvas().drawRect(new Rect(360, 680, 365, 685),crtac);
        crtac.setColor(Color.BLACK);
        stranica.getCanvas().drawText("Hiperglikemija ( >10 mmol/L)", 370, 685, crtac);

        crtac.setColor(Color.parseColor("#AAEE70"));
        stranica.getCanvas().drawRect(new Rect(360, 690, 365, 695),crtac);
        crtac.setColor(Color.BLACK);
        stranica.getCanvas().drawText("Primljene jedinice insulina", 370, 695, crtac);

    }


    @Override
    protected Uri doInBackground(PDFParameters... pdfParameters) {
        Uri uri = null;
        try {
            uri = writeHello(pdfParameters[0].getDoc(),
                                 pdfParameters[0].getResursi(),
                                 pdfParameters[0].getPodaci(),
                                 pdfParameters[0].getDanOd(),
                                 pdfParameters[0].getDanDo());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        try {
            delegate.processFinish(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
