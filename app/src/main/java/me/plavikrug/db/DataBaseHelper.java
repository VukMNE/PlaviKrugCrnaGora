package me.plavikrug.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vuk on 28.7.2017..
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "PlaviKrug.db";
    private static final int DB_VERSION = 6;
    private String CREATE_REZULTATI = "CREATE TABLE REZULTATI (id_rez INTEGER PRIMARY KEY AUTOINCREMENT," +
            "vrijeme TEXT," +
            "id_attr_fk INTEGER," +
            "izmjereno REAL," +
            "tip_mj INTEGER," +
            "BACKED_UP INTEGER DEFAULT 0,"+
            "id_p INTEGER)";
    private String CREATE_ATTR = "CREATE TABLE ATTR (id_attr INTEGER PRIMARY KEY AUTOINCREMENT," +
            "naziv TEXT," +
            "mjera TEXT)";
    private String CREATE_TERAPIJA = "CREATE TABLE TERAPIJA (id_ter INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_pac INTEGER," +
            "jed_insl INTEGER," +
            "jed_kor INTEGER," +
            "poz_prijem_ter INTEGER," +
            "dan TEXT," +
            "BACKED_UP INTEGER DEFAULT 0,"+
            "vrijeme TEXT)";
    private String CREATE_DANI_COMMENT = "CREATE TABLE DANI_BIL (id_db INTEGER PRIMARY KEY AUTOINCREMENT," +
            "datum TEXT," +
            "BACKED_UP INTEGER DEFAULT 0,"+
            "biljeska TEXT)";

    private String CREATE_PODSJETNIK = "CREATE TABLE PODSJETNIK (id_pod INTEGER PRIMARY KEY AUTOINCREMENT," +
            "datum TEXT," +
            "vrijeme TEXT," +
            "naslov TEXT)";

    private String TABLE_REZULTATI = "REZULTATI";
    private String TABLE_ATTR = "ATTR";
    private String TABLE_TERAPIJA = "TERAPIJA";
    private String TABLE_DANI_BIL = "DANI_BIL";
    private String TABLE_PODSJETNIK = "PODSJETNIK";

//REZULTATI
    private String COLUMN_ID_REZ = "id_rez";
    private String COLUMN_VRIJEME = "vrijeme";
    private String COLUMN_ID_ATTR_FK = "id_attr_fk";
    private String COLUMN_IZMJERENO = "izmjereno";
    private String COLUMN_TIP_MJ = "tip_mj";
    private String COLUMN_ID_P = "id_p";
//ATTR
    private String COLUMN_ID_ATTR = "id_attr";
    private String COLUMN_NAZIV = "naziv";
    private String COLUMN_MJERA = "mjera";
//TERAPIJA
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


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REZULTATI);
        db.execSQL(CREATE_ATTR);
        db.execSQL(CREATE_TERAPIJA);
        db.execSQL(CREATE_DANI_COMMENT);
        db.execSQL(CREATE_PODSJETNIK);

        generateData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //DB version 6
        db.execSQL("ALTER TABLE REZULTATI ADD COLUMN BACKED_UP INTEGER DEFAULT 0");
        db.execSQL("ALTER TABLE TERAPIJA ADD COLUMN BACKED_UP INTEGER DEFAULT 0");
        db.execSQL("ALTER TABLE DANI_BIL ADD COLUMN BACKED_UP INTEGER DEFAULT 0");


    }

    public void generateData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID_ATTR,1);
        values.put(COLUMN_NAZIV,"glukoza");
        values.put(COLUMN_MJERA,"mmol/L");
        db.insert(TABLE_ATTR,null,values);

        values.clear();
        values.put(COLUMN_ID_ATTR,2);
        values.put(COLUMN_NAZIV,"HbA1c");
        values.put(COLUMN_MJERA,"%");
        db.insert(TABLE_ATTR,null,values);

    }
}
