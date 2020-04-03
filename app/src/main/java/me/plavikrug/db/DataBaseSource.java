package me.plavikrug.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Vuk on 30.7.2017..
 */

public class DataBaseSource {

    private SQLiteDatabase db;
    private DataBaseHelper db_helper;
    private Context context;

    private String TABLE_REZULTATI = "REZULTATI";
    private String TABLE_ATTR = "ATTR";
    private String TABLE_TER = "TERAPIJA";
    private String TABLE_DANI_BIL = "DANI_BIL";
    private String TABLE_PODSJETNIK = "PODSJETNIK";

    //Tabela rezultati
    private String COLUMN_ID_REZ = "id_rez";
    private String COLUMN_VRIJEME = "vrijeme";
    private String COLUMN_ID_ATTR_FK = "id_attr_fk";
    private String COLUMN_IZMJERENO = "izmjereno";
    private String COLUMN_TIP_MJ = "tip_mj";
    private String COLUMN_ID_P = "id_p";

    //Tabela ATTR
    private String COLUMN_ID_ATTR = "id_attr";
    private String COLUMN_NAZIV = "naziv";
    private String COLUMN_MJERA = "mjera";

    //Tabela Terapija
    private String COLUMN_ID_TER = "id_ter";
    private String COLUMN_ID_PAC = "id_pac";
    private String COLUMN_JED_INSL = "jed_insl";
    private String COLUMN_JED_KOR = "jed_kor";
    private String COLUMN_POZ_PRIJEM_TER = "poz_prijem_ter"; //mjesto gdje je pacijent primio insulin, na pr: ruka, stomak..
    private String COLUMN_DAN = "dan";
    //već postoji COLUMN_VRIJEME pa je zato nisam stavljao ovđen

    private String COLUMN_ID_DB = "id_db";
    private String COLUMN_DATUM = "datum";
    private String COLUMN_BILJESKA = "biljeska";
    private String COLUMN_BACKED_UP = "BACKED_UP";


    private String COLUMN_NASLOV = "naslov";



    public DataBaseSource(Context context) {
        this.context = context;
        db_helper = new DataBaseHelper(context);
    }

    //open
    public void open() throws SQLException {
        db = db_helper.getWritableDatabase();
    }

    //close
    public void close(){
        db.close();
    }

    public void insertGlukoza(String datum,float nivo, int tip){
        ContentValues values = new ContentValues();
        values.put(COLUMN_VRIJEME,datum);
        values.put(COLUMN_ID_ATTR_FK,1);
        values.put(COLUMN_IZMJERENO,nivo);
        values.put(COLUMN_ID_P,1);
        values.put(COLUMN_TIP_MJ,tip);
        db.insert(TABLE_REZULTATI,null,values);
    }


    public void insertTerapija(int jedInsulin, int jedKorekcija, String dan, String vrijeme, int pozPrijema){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_PAC,1);
        values.put(COLUMN_JED_INSL,jedInsulin);
        values.put(COLUMN_JED_KOR,jedKorekcija);
        values.put(COLUMN_POZ_PRIJEM_TER, pozPrijema);
        values.put(COLUMN_DAN,dan);
        values.put(COLUMN_VRIJEME,vrijeme);
        db.insert(TABLE_TER, null, values);
    }

    //Možda će trebati da se doda argument dan kao String
    public Cursor getGlMjerenja(String danStr){
        Calendar c = Calendar.getInstance();
        int godina = c.get(Calendar.YEAR);
        int mjesec = c.get(Calendar.MONTH)+1;
        int dan = c.get(Calendar.DAY_OF_MONTH);
        String datum = dan+".0"+mjesec+"."+godina;
        String upit =  "Select id_rez,CAST(substr(vrijeme,instr(vrijeme,' ')+1,2) as real) + CAST(substr(vrijeme,instr(vrijeme,':')+1,2) as real)/60 as vrijeme," +
                    "izmjereno from REZULTATI WHERE id_attr_fk = 1 and substr(vrijeme,1,10) = '" + danStr + "' order by 2;";

        //Da bi se prikazivali rezultati od danas
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;
    }

    public Cursor getLastMjerenje() {
        String upit = "SELECT id_rez, vrijeme, id_attr_fk, izmjereno, tip_mj, backed_up, id_p from rezultati " +
                "WHERE id_attr_fk = 1 and " +
                "CAST(substr(vrijeme,7,4)||substr(vrijeme,4,2)||substr(vrijeme,1,2)||substr(vrijeme,12,2)||substr(vrijeme,15,2) AS integer) = " +
                "(SELECT MAX(CAST(substr(vrijeme,7,4)||substr(vrijeme,4,2)||substr(vrijeme,1,2)||substr(vrijeme,12,2)||substr(vrijeme,15,2) AS integer)) from rezultati)";
        Cursor cursor = db.rawQuery(upit, null);
        return cursor;
    }

    public Cursor getPDFReportMjerenja(String danOd, String danDo){
        /*
        danOd i danDo će biti uključeni
         */

        Cursor mjerenja;
        String whereMjerenja = " and substr(r.vrijeme,7,4)||substr(r.vrijeme,4,2)||substr(r.vrijeme,1,2) between '" + danOd + "' and '" + danDo + "'";
        String whereMjerenja2 = "id_attr_fk = 1";
        String orderByMjerenje = " order by substr(r.vrijeme,7,4)||substr(r.vrijeme,4,2)||substr(r.vrijeme,1,2)||substr(r.vrijeme,12,2)||substr(r.vrijeme,15,2) asc";
        String whereTerapija = "substr(dan,7)||substr(dan,4,2)||substr(dan,1,2) between '" + danOd + "' and '" + danDo + "'";
        String from = "REZULTATI r LEFT OUTER JOIN TERAPIJA t on r.vrijeme=t.dan||' '||t.vrijeme";
        String upitMjerenja = "SELECT r.vrijeme, izmjereno, id_attr_fk, tip_mj, jed_insl + jed_kor FROM " + from +" WHERE " + whereMjerenja2 + whereMjerenja + orderByMjerenje;
        mjerenja = db.rawQuery(upitMjerenja, null);
        return mjerenja;
    }

    public Cursor getDnevnik(String param){
        Cursor cursor;
        Calendar c = Calendar.getInstance();
        int godina = c.get(Calendar.YEAR);
        int mjesec = c.get(Calendar.MONTH)+1;
        int dan = c.get(Calendar.DAY_OF_MONTH);
        if(param.length()==0){
            String upit = "SELECT distinct substr(vrijeme,1,10) as dan from REZULTATI where id_attr_fk = 1 order by substr(vrijeme,7,4)||substr(vrijeme,4,2)||substr(vrijeme,1,2) desc";
            cursor = db.rawQuery(upit,null);
            return cursor;
        }
        else if(param.length()==1){
            int p = Integer.parseInt(param);
            String whereUslov = "";
            String orderBy = "order by substr(vrijeme,7,4)||substr(vrijeme,4,2)||substr(vrijeme,1,2) desc";
            String upit = "SELECT distinct substr(vrijeme,1,10) as dan from REZULTATI where id_attr_fk = 1 AND ";
            switch(p){
                case 1:
                    //date BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime');
                    whereUslov = "date(substr(vrijeme,7,4)||'-'||substr(vrijeme,4,2)||'-'||substr(vrijeme,1,2)) BETWEEN datetime('now', '-7 days') AND datetime('now', 'localtime') ";
                    break;
                case 2:
                    whereUslov = "date(substr(vrijeme,7,4)||'-'||substr(vrijeme,4,2)||'-'||substr(vrijeme,1,2)) BETWEEN datetime('now', '-14 days') AND datetime('now', 'localtime') ";
                    break;
                case 3:
                    whereUslov = "date(substr(vrijeme,7,4)||'-'||substr(vrijeme,4,2)||'-'||substr(vrijeme,1,2)) BETWEEN datetime('now', '-30 days') AND datetime('now', 'localtime') ";
                    break;
            }
            cursor = db.rawQuery(upit+whereUslov+orderBy,null);
            return cursor;
        }

        else {
            String upit = "SELECT distinct substr(vrijeme,1,10) as dan from REZULTATI where substr(vrijeme,4,7) = '" + param +"' order by substr(vrijeme,7,4)||substr(vrijeme,4,2)||substr(vrijeme,1,2) desc";
            cursor = db.rawQuery(upit,null);
            return cursor;
        }

    }

    //Daje mjerenja za određeni dan
    public Cursor getMjerenja(String dan){
        String upit = "SELECT substr(vrijeme,12,5)||'h' as vrijeme, izmjereno from REZULTATI where substr(vrijeme,1,10) = '" + dan + "' and id_attr_fk = 1 order by 1";
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;

        //substr(vrijeme,12,2)||substr(vrijeme,14,2)
    }

    public Cursor getSvaMjerenja(){
        String upit = "SELECT id_rez, vrijeme, izmjereno, id_attr_fk, tip_mj from REZULTATI order by 1";
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;
    }

    public Cursor getSvaTerapija(){
        String upit = "SELECT id_ter, jed_insl, jed_kor, poz_prijem_ter, dan, vrijeme from TERAPIJA order by 4";
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;
    }

    public Cursor getLastTerapija() {
        Calendar dan1 = Calendar.getInstance();
        Calendar dan2 = Calendar.getInstance();
        dan2.add(Calendar.DATE, -1);

        String datum1 = dan1.get(Calendar.YEAR) + "||" +
                (dan1.get(Calendar.MONTH) + 1 >= 10 ? (dan1.get(Calendar.MONTH) + 1) : "0||" + (dan1.get(Calendar.MONTH) + 1)) + "||" +
                dan1.get(Calendar.DAY_OF_MONTH) + "||" + dan1.get(Calendar.HOUR_OF_DAY) + "||" + dan1.get(Calendar.MINUTE) + "";
        String datum2 = dan2.get(Calendar.YEAR) + "||" +
                (dan2.get(Calendar.MONTH) + 1 >= 10 ? (dan2.get(Calendar.MONTH) + 1) : "0||" + (dan2.get(Calendar.MONTH) + 1)) + "||" +
                dan2.get(Calendar.DAY_OF_MONTH) + "||" + dan2.get(Calendar.HOUR_OF_DAY) + "||" + dan2.get(Calendar.MINUTE) + "";

        String vremenskiUslov = "CAST(substr(dan,7,4)||substr(dan,4,2)||substr(dan,1,2)||substr(vrijeme,1,2)||substr(vrijeme,4,2) AS integer)";

        String upit = "SELECT SUM(jed_insl), SUM(jed_kor) from TERAPIJA " +
                "WHERE " + vremenskiUslov + " >= " + datum2 + " and " + vremenskiUslov + " < " + datum1;

        Log.d(TABLE_REZULTATI, upit);
        Cursor cursor = db.rawQuery(upit, null);
        return cursor;
    }

    public Cursor getSveBiljeske(){
        String upit = "SELECT id_db, biljeska, datum from DANI_BIL";
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;
    }

    public Cursor getBiljeskeZaDan(String dan){
        String upit = "SELECT biljeska from DANI_BIL where datum = '" + dan + "'";
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;

    }

    //Možda će trebati da se doda argument dan kao String
    public Cursor getTerapija(String dan){
        String datum = dan;
        String vrijemeDec = "CAST(substr(t.vrijeme,1,2) as real) + round(CAST(substr(t.vrijeme,4,2) as real)/60,2) as pvrijeme";
        String upit = "SELECT distinct jed_insl, jed_kor, poz_prijem_ter," +vrijemeDec + ",ifnull(izmjereno,5)+0.5 from TERAPIJA t LEFT OUTER JOIN REZULTATI r on t.vrijeme=substr(r.vrijeme,12,5) where dan ='" + dan+ "' and jed_insl is not null and jed_insl + jed_kor > 0 order by 4";
        Cursor cursor = db.rawQuery(upit,null);
        return cursor;
    }

    public void insertBiljeska(String datum, String biljeska){
        ContentValues val = new ContentValues();
        val.put(COLUMN_DATUM,datum);
        val.put(COLUMN_BILJESKA,biljeska);
        db.insert(TABLE_DANI_BIL,null,val);
    }

    public String findBiljeskaOnDay(String datum){
        String biljeska = "";
        String countUpit = "Select count(*) from DANI_BIL where datum='" + datum + "'";
        String upit = "Select biljeska from DANI_BIL where datum='" + datum + "'";
        Cursor cursor = db.rawQuery(countUpit,null);
        cursor.moveToFirst();
        int brojSlogova = cursor.getInt(0);
        if(brojSlogova==1){
            cursor = db.rawQuery(upit,null);
            cursor.moveToFirst();
            biljeska = cursor.getString(0);
        }
        return biljeska;
    }
    public void delete(String table, String id){
        String idName = "";
        if(table.equals("Podsjetnik")){
            idName = "id_pod";
            table = "PODSJETNIK";
            String upit = "DELETE FROM PODSJETNIK where  id_pod = " + id;
            db.execSQL(upit);
        }
        if(table.equals("HbA1c")){
            idName = "id_rez";
            table = "REZULTATI";
            String upit = "DELETE FROM REZULTATI where  id_rez = " + id;
            db.execSQL(upit);
        }
        if(table.equals("Mjerenje")){
            String upit1 = "DELETE FROM REZULTATI where vrijeme = '" + id + "'";
            String upit2 = "DELETE FROM TERAPIJA where dan||' '||vrijeme = '" + id + "'";
            db.execSQL(upit1);
            db.execSQL(upit2);
        }

    }
    public void updateBiljeske(String datum, String biljeska){
        ContentValues updateVal = new ContentValues();
        updateVal.put(COLUMN_BILJESKA, biljeska);
        db.update(TABLE_DANI_BIL,updateVal,"datum='" + datum + "'",null);
    }

    public void updateMjerenje(float glukoza, int insulin, int korekcija, int poz, int tip, String datumVrijeme, int idrez, int idter){
        ContentValues val = new ContentValues();
        val.put(COLUMN_IZMJERENO, glukoza);
        val.put(COLUMN_TIP_MJ, tip);
        val.put(COLUMN_VRIJEME, datumVrijeme);
        db.update(TABLE_REZULTATI,val,"id_rez = " + idrez ,null);
        val.clear();
        val.put(COLUMN_JED_INSL, insulin);
        val.put(COLUMN_JED_KOR, korekcija);
        val.put(COLUMN_POZ_PRIJEM_TER, poz);
        String[] vv = datumVrijeme.split(" ");
        val.put(COLUMN_DAN, vv[0]);
        val.put(COLUMN_VRIJEME, vv[1]);
        db.update(TABLE_TER,val,"id_ter = " + idter,null);

    }

    public void insertPodsjetnik(String datum, String vrijeme, String naslov){
        ContentValues val = new ContentValues();
        val.put(COLUMN_DATUM, datum);
        val.put(COLUMN_VRIJEME, vrijeme);
        val.put(COLUMN_NASLOV, naslov);
        db.insert(TABLE_PODSJETNIK,null,val);
    }

    public void updatePodsjetnik(String datum, String vrijeme, String naslov, int id){
        ContentValues val = new ContentValues();
        val.put(COLUMN_DATUM, datum);
        val.put(COLUMN_VRIJEME, vrijeme);
        val.put(COLUMN_NASLOV, naslov);
        db.update(TABLE_PODSJETNIK,val,"id_pod = " + id,null);
    }

    public Cursor getPodsjetnik(){
        String orderBy = "substr(datum,7,4)||substr(datum,4,2)||substr(datum,1,2)||substr(vrijeme,1,2)||substr(vrijeme,4,2) asc";
        String upit = "SELECT datum, vrijeme, naslov, id_pod from PODSJETNIK order by " + orderBy;
        Cursor cursor = db.rawQuery(upit, null);
        return cursor;
    }

    public String[] prviPodsjetnik(String dan, String vrijeme){
        String[] rez = new String[4];
        String upitBr;
        upitBr = dan.substring(6)+ dan.substring(3,5) + dan.substring(0,2) + vrijeme.substring(0,2) + vrijeme.substring(3);
        String upit = "SELECT datum, vrijeme, naslov, id_pod from PODSJETNIK " +
                "WHERE CAST( substr(datum,7,4)||substr(datum,4,2)||substr(datum,1,2)||substr(vrijeme,1,2)||substr(vrijeme,4,2) as decimal) = " +
                "( SELECT min(CAST( substr(datum,7,4)||substr(datum,4,2)||substr(datum,1,2)||substr(vrijeme,1,2)||substr(vrijeme,4,2) as decimal)) from PODSJETNIK " +
                "WHERE CAST( substr(datum,7,4)||substr(datum,4,2)||substr(datum,1,2)||substr(vrijeme,1,2)||substr(vrijeme,4,2) as decimal) > " + upitBr + ")";
        Cursor cursor = db.rawQuery(upit,null);
        if(cursor.getCount()> 0) {
            cursor.moveToFirst();
            rez[0] = cursor.getString(0);
            rez[1] = cursor.getString(1);
            rez[2] = cursor.getString(2);
            rez[3] = cursor.getInt(3) + "";
        }
        return rez;
    }

    public void deletePodsjetnici(String dan, String vrijeme){
        String upitBr = dan.substring(6)+ dan.substring(3,5) + dan.substring(0,2) + vrijeme.substring(0,2) + vrijeme.substring(3);
        String upit = "DELETE FROM PODSJETNIK where " +
                "CAST( substr(datum,7,4)||substr(datum,4,2)||substr(datum,1,2)||substr(vrijeme,1,2)||substr(vrijeme,4,2) as decimal) < " +upitBr;
        db.execSQL(upit);
    }


    public Cursor getHbA1c(){
        String orderBy = "substr(vrijeme,7,4)||substr(vrijeme,4,2)||substr(vrijeme,1,2)||substr(vrijeme,12,2)||substr(vrijeme,15,2)";
        String upit = "Select vrijeme, izmjereno, id_rez from REZULTATI where id_attr_fk= 2 order by "+ orderBy +" desc";
        Cursor cursor = db.rawQuery(upit, null);
        return cursor;
    }

    public void insertHbA1c(String datum,float nivo){
        ContentValues values = new ContentValues();
        values.put(COLUMN_VRIJEME,datum);
        values.put(COLUMN_ID_ATTR_FK,2);
        values.put(COLUMN_IZMJERENO,nivo);
        values.put(COLUMN_ID_P,1);
        db.insert(TABLE_REZULTATI,null,values);
    }

    public void updateHbA1c(String datum,float nivo, int id){
        ContentValues values = new ContentValues();
        values.put(COLUMN_VRIJEME,datum);
        values.put(COLUMN_IZMJERENO,nivo);
        db.update(TABLE_REZULTATI,values,"id_rez = " + id ,null);
    }

    public Cursor fetchExactMjerenjeAndTerapija(String dan, String vrijeme){
        String checkTherapyUpit = "Select count(*) from terapija where dan = '" + dan + "' and vrijeme = '" + vrijeme + "'";
        Cursor checkIfRowsExist = db.rawQuery(checkTherapyUpit, null);
        int numOfRows = 0;
        checkIfRowsExist.moveToFirst();
        numOfRows = checkIfRowsExist.getInt(0);
        String jedInsl = ", 0";
        String jedKor = ", 0";
        String pozPrijem = ", 0";
        String idTer = ", 0";
        if(numOfRows > 0){
            String getTerapija = "select jed_insl, jed_kor, poz_prijem_ter, id_ter from TERAPIJA where dan = '" + dan + "' and vrijeme = '" + vrijeme + "'";
            Cursor getterCursor = db.rawQuery(getTerapija, null);
            getterCursor.moveToFirst();
            jedInsl = ", " + getterCursor.getInt(0) + "";
            jedKor = ", " + getterCursor.getInt(1) + "";
            pozPrijem = ", " + getterCursor.getInt(2) + "";
            idTer = ", " + getterCursor.getInt(3) + "";
            if(idTer.length()==2){
                idTer = ", 0";
            }

        }
        String upit = "Select r.izmjereno" + jedInsl + jedKor + pozPrijem +", tip_mj " + idTer + ",id_rez from REZULTATI r where vrijeme = '" +dan+ " " + vrijeme + "'";
        Cursor cursor = db.rawQuery(upit, null);
        return cursor;
    }

    public int countBackedUpData(){
        int backedUpRows = 0;
        int allRows = 0;
        int percentage = 0;
        float percentageFl = 0;

        String countBackedUpResults = "Select count(1) from " + TABLE_REZULTATI + " where backed_up = 1";
        String countAllResults = "Select count(1) from " + TABLE_REZULTATI;
        Cursor cResults = db.rawQuery(countBackedUpResults, null);
        Cursor allResults = db.rawQuery(countAllResults, null);
        cResults.moveToFirst();
        allResults.moveToFirst();
        backedUpRows += cResults.getInt(0);
        allRows += allResults.getInt(0);

        String countBackedUpTerapija = "Select count(1) from " + TABLE_TER + " where backed_up = 1";
        String countAllTerapija = "Select count(1) from " + TABLE_TER;
        Cursor cTerapija = db.rawQuery(countBackedUpTerapija, null);
        Cursor allTerapija = db.rawQuery(countAllTerapija, null);
        cTerapija.moveToFirst();
        allTerapija.moveToFirst();
        backedUpRows += cTerapija.getInt(0);
        allRows += allTerapija.getInt(0);

        String countBackedUpBiljeske = "Select count(1) from " + TABLE_DANI_BIL + " where backed_up = 1";
        String countAllBiljeske = "Select count(1) from " + TABLE_DANI_BIL;
        Cursor cBiljeske = db.rawQuery(countBackedUpBiljeske, null);
        Cursor allBiljeske = db.rawQuery(countAllBiljeske, null);
        cBiljeske.moveToFirst();
        allBiljeske.moveToFirst();
        backedUpRows += cBiljeske.getInt(0);
        allRows += allBiljeske.getInt(0);

        percentageFl = (float)backedUpRows / (float) allRows;
        percentage = (int)(percentageFl*100);
        return percentage;
    }

    public void markAllBackedUp(){
        ContentValues updateVal = new ContentValues();
        updateVal.put(COLUMN_BACKED_UP, 1);
        db.update(TABLE_DANI_BIL,updateVal,"BACKED_UP=" + 0, null);
        updateVal.clear();
        updateVal.put(COLUMN_BACKED_UP, 1);
        db.update(TABLE_REZULTATI,updateVal,"BACKED_UP=" + 0, null);
        updateVal.put(COLUMN_BACKED_UP, 1);
        db.update(TABLE_TER,updateVal,"BACKED_UP=" + 0, null);
    }

    public void parseJsonIntoDb(JSONObject jsonObject) throws JSONException {

        Log.d("PLAVI_KRUG", "pol: " + jsonObject.getString("gender"));
        Log.d("PLAVI_KRUG", "tip dijabetesa: " + jsonObject.getInt("typeDiabetes"));
        Log.d("PLAVI_KRUG", "datum_rodj: " + jsonObject.getString("dateOfBirth"));
        Log.d("PLAVI_KRUG", "datum_d: " + jsonObject.getString("dateOfDiabetes"));
        Log.d("PLAVI_KRUG", "mjerenja: " + jsonObject.getJSONArray("measurements").toString());

        JSONArray jsonMjerenja;
        JSONArray jsonTerapija;
        JSONArray jsonBiljeske;
        ContentValues insertVal = new ContentValues();

        jsonMjerenja = jsonObject.getJSONArray("measurements");
        jsonTerapija = jsonObject.getJSONArray("therapy");
        jsonBiljeske = jsonObject.getJSONArray("notes");

        for(int m = 0; m < jsonMjerenja.length(); m++){
            JSONObject currMea = jsonMjerenja.getJSONObject(m);
            insertVal.put(COLUMN_ID_REZ, currMea.getInt("id"));
            insertVal.put(COLUMN_VRIJEME, currMea.getString("datetime"));
            insertVal.put(COLUMN_ID_ATTR_FK, currMea.getInt("attributeId"));
            insertVal.put(COLUMN_IZMJERENO, currMea.getDouble("measuredValue"));
            insertVal.put(COLUMN_TIP_MJ, currMea.getInt("typeOfMeasure"));
            insertVal.put(COLUMN_BACKED_UP, 1);
            if(alreadyExists(TABLE_REZULTATI, COLUMN_VRIJEME, currMea.getString("datetime")) == false) {
                db.insert(TABLE_REZULTATI, null, insertVal);
            }
            insertVal.clear();
        }

        for(int b = 0; b < jsonBiljeske.length(); b++){
            JSONObject currBiljeska = jsonBiljeske.getJSONObject(b);
            insertVal.put(COLUMN_ID_DB,  currBiljeska.getString("id"));
            insertVal.put(COLUMN_BILJESKA,  currBiljeska.getString("noteText"));
            insertVal.put(COLUMN_DATUM, currBiljeska.getString("date"));
            insertVal.put(COLUMN_BACKED_UP, 1);
            if(alreadyExists(TABLE_DANI_BIL, COLUMN_DATUM, currBiljeska.getString("date")) == false) {
                db.insert(TABLE_DANI_BIL, null, insertVal);
            }
            insertVal.clear();
        }

        for(int t = 0; t < jsonTerapija.length(); t++){
            JSONObject currTer = jsonTerapija.getJSONObject(t);
            insertVal.put(COLUMN_ID_TER, currTer.getInt("id"));
            insertVal.put(COLUMN_JED_INSL, currTer.getInt("insulinUnits"));
            insertVal.put(COLUMN_JED_KOR, currTer.getInt("correctionUnits"));
            insertVal.put(COLUMN_POZ_PRIJEM_TER, currTer.getInt("bodyEntryPosition"));
            insertVal.put(COLUMN_DAN, currTer.getString("date"));
            insertVal.put(COLUMN_VRIJEME, currTer.getString("time"));
            insertVal.put(COLUMN_BACKED_UP, 1);
            if(alreadyExists(TABLE_TER, COLUMN_DAN+"||' '||"+COLUMN_VRIJEME,
                    currTer.getString("date") + "'||' '||'" + currTer.getString("time")) == false) {
                db.insert(TABLE_TER, null, insertVal);
            }
            insertVal.clear();
        }




    }

    public boolean alreadyExists(String table, String column, String value){
        boolean val = false;
        Cursor rs =  db.rawQuery("SELECT count(1) from " + table + " where " + column + "= '" + value + "'", null);
        rs.moveToFirst();
        if(rs.getInt(0) == 0){
            val = false;
        }
        else{
            val = true;
        }
        return val;
    }

    public Cursor getMonthData(String month, int sat){
        Cursor cursor;
        int endSat = sat + 4;
        String uslov1 = " substr(vrijeme,4,7) = '" + month + "'";
        String uslov2 = " CAST(substr(vrijeme,12,2) as INTEGER) >= " + sat + " and CAST(substr(vrijeme,12,2) as INTEGER) < " + endSat;
        String uslov3 = " id_attr_fk = 1";
        String upit = "SELECT izmjereno, vrijeme FROM REZULTATI WHERE" + uslov1 + " and " + uslov2 + " and " + uslov3;
        cursor = db.rawQuery(upit, null);
        return cursor;
    }

    public double getFromMonth(String function, String tabela, String kolona, String whereKolona, String month){
        double vrijednost = 0;
        String uslov1 = " WHERE substr(" + whereKolona + ",4,7) = '" + month + "'";
        String upit = "Select round(" + function + "(" + kolona + "),2) from " + tabela + uslov1;
        Cursor cursor = db.rawQuery(upit, null);
        cursor.moveToFirst();
        vrijednost = cursor.getDouble(0);
        return vrijednost;
    }
}
