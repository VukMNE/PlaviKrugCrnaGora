package me.plavikrug.model;

/**
 * Created by Vuk on 27.8.2017..
 */

public class HbA1c {

    String datum;
    float rez;
    int id;

    public HbA1c(String datum, float rez, int id) {
        this.datum = datum;
        this.rez = rez;
        this.id = id;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public float getRez() {
        return rez;
    }

    public void setRez(float rez) {
        this.rez = rez;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
