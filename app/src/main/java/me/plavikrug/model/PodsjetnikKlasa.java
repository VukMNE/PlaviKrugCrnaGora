package me.plavikrug.model;

/**
 * Created by Vuk on 26.8.2017..
 */

public class PodsjetnikKlasa {

    String datum, vrijeme, naslov;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PodsjetnikKlasa(String datum, String vrijeme, String naslov, int id) {
        this.datum = datum;
        this.vrijeme = vrijeme;
        this.naslov = naslov;
        this.id = id;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }
}
