package me.plavikrug.model;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;

/**
 * Created by Vuk on 24.3.2018..
 */

public class PDFParameters {
    private PdfDocument doc;
    private Resources resursi;
    private Cursor podaci;
    private String danOd;
    private String danDo;

    public PDFParameters(PdfDocument dok, Resources res, Cursor data, String dan1, String dan2){
        doc = dok;
        resursi = res;
        podaci = data;
        danOd = dan1;
        danDo = dan2;
    }

    public PdfDocument getDoc() {
        return doc;
    }

    public void setDoc(PdfDocument doc) {
        this.doc = doc;
    }

    public Resources getResursi() {
        return resursi;
    }

    public void setResursi(Resources resursi) {
        this.resursi = resursi;
    }

    public Cursor getPodaci() {
        return podaci;
    }

    public void setPodaci(Cursor podaci) {
        this.podaci = podaci;
    }

    public String getDanOd() {
        return danOd;
    }

    public void setDanOd(String danOd) {
        this.danOd = danOd;
    }

    public String getDanDo() {
        return danDo;
    }

    public void setDanDo(String danDo) {
        this.danDo = danDo;
    }
}
