package com.example.tarif;

import java.io.Serializable;

public class Tarif implements Serializable {
    private String isim;
    private String kategori;
    private String aciklama;
    private int resimId;
    private String malzemeler;
    private String yapilis;

    public Tarif(String isim, String kategori, String aciklama, int resimId, String malzemeler, String yapilis) {
        this.isim = isim;
        this.kategori = kategori;
        this.aciklama = aciklama;
        this.resimId = resimId;
        this.malzemeler = malzemeler;
        this.yapilis = yapilis;
    }

    // Getter metodları
    public String getIsim() {
        return isim;
    }

    public String getKategori() {
        return kategori;
    }

    public String getAciklama() {
        return aciklama;
    }

    public int getResimId() {
        return resimId;
    }

    public String getMalzemeler() {
        return malzemeler;
    }

    public String getYapilis() {
        return yapilis;
    }
}