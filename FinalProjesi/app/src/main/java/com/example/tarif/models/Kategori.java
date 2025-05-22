package com.example.tarif.models;

public class Kategori {
    private final String isim;
    private final int ikonResmi;

    public Kategori(String isim, int ikonResmi) {
        this.isim = isim;
        this.ikonResmi = ikonResmi;
    }

    public String getIsim() {
        return isim;
    }

    public int getIkonResmi() {
        return ikonResmi;
    }
}