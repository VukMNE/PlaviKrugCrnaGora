package me.plavikrug.model;

/**
 * Created by Vuk on 10.8.2017..
 */

public class DanDnevnik {

    String mjesecStr;
    int dan;
    int mjesec;
    int godina;
    String vremenaMjerenja[];
    float rezultatiMjerenja[];
    String datum;
    String biljeska;

    public DanDnevnik( String mjesecStr, int dan, int mjesec, int godina, String[] vremenaMjerenja, float[] rezultatiMjerenja,String biljeska){
        this.mjesecStr = mjesecStr;
        this.dan = dan;
        this.mjesec = mjesec;
        this.godina = godina;
        this.vremenaMjerenja = vremenaMjerenja;
        this.rezultatiMjerenja = rezultatiMjerenja;
        String danS = dan+ "";
        String mS = mjesec + "";
        if(dan < 10){
            danS = "0" + dan;
        }
        if(mjesec<10){
            mS = "0" + mjesec;
        }
        this.datum = danS+"."+mS+"."+godina;
        this.biljeska = biljeska;
    }

    public String getMjesec() {
        return mjesecStr;
    }

    public void setMjesecStr(String mjesecStr) {
        this.mjesecStr = mjesecStr;
    }

    public int getDan() {
        return dan;
    }

    public void setDan(int dan) {
        this.dan = dan;
    }

    public void setMjesec(int mjesec) {
        this.mjesec = mjesec;
    }

    public int getGodina() {
        return godina;
    }

    public void setGodina(int godina) {
        this.godina = godina;
    }

    public String[] getVremenaMjerenja() {
        return vremenaMjerenja;
    }

    public void setVremenaMjerenja(String[] vremenaMjerenja) {
        this.vremenaMjerenja = vremenaMjerenja;
    }

    public float[] getRezultatiMjerenja() {
        return rezultatiMjerenja;
    }

    public void setRezultatiMjerenja(float[] rezultatiMjerenja) {
        this.rezultatiMjerenja = rezultatiMjerenja;
    }

    public String getDatum() {
        return datum;
    }

    public String getBiljeska() {
        return biljeska;
    }

    public void setBiljeska(String biljeska) {
        this.biljeska = biljeska;
    }
}
