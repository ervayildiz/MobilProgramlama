package com.example.tarif;

import java.io.Serializable;

public class Tarif {
    private String id;
    private String ad;
    private String kategori;
    private String malzemeler;
    private String yapilisAdimlari;
    private int hazirlikSuresi;
    private int pisirmeSuresi;
    private String servisSayisi;
    private String resimId;

    public Tarif() {
        // Firebase için boş constructor gerekli
    }

    // Getter ve Setter metodları
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAd() { return ad; }
    public String getKategori() { return kategori; }
    public String getMalzemeler() { return malzemeler; }
    public String getYapilisAdimlari() { return yapilisAdimlari; }
    public int getHazirlikSuresi() { return hazirlikSuresi; }
    public int getPisirmeSuresi() { return pisirmeSuresi; }
    public String getServisSayisi() { return servisSayisi; }
    public String getResimId() { return resimId; }
}