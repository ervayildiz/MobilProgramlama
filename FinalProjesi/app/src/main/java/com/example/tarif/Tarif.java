package com.example.tarif;

import java.io.Serializable;

public class Tarif {
    private String TarifId;
    private String ad;
    private String kategori;
    private String malzemeler;
    private String yapilisAdimlari;
    private int hazirlikSuresi;
    private int pisirmeSuresi;
    private String servisSayisi;
    private String resimId;

    private String resimUrl;
    private boolean favori;


    public Tarif() {
        // Firebase için boş constructor gerekli
    }
    public boolean isFavori() {
        return favori;
    }

    public void setFavori(boolean favori) {
        this.favori = favori;
    }

    // Getter ve Setter metodları
    public String getTarifId() { return TarifId; }
    public void setTarifId(String TarifId) { this.TarifId = TarifId; }
    public String getAd() { return ad; }
    public String getKategori() { return kategori; }
    public String getMalzemeler() { return malzemeler; }
    public String getYapilisAdimlari() { return yapilisAdimlari; }
    public int getHazirlikSuresi() { return hazirlikSuresi; }
    public int getPisirmeSuresi() { return pisirmeSuresi; }
    public String getServisSayisi() { return servisSayisi; }
    public String getResimId() { return resimId; }

    public String getResimUrl() { return resimUrl; }

    public void setAd(String ad) { this.ad = ad; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setMalzemeler(String malzemeler) { this.malzemeler = malzemeler; }
    public void setYapilisAdimlari(String yapilisAdimlari) { this.yapilisAdimlari = yapilisAdimlari; }
    public void setHazirlikSuresi(int hazirlikSuresi) { this.hazirlikSuresi = hazirlikSuresi; }
    public void setPisirmeSuresi(int pisirmeSuresi) { this.pisirmeSuresi = pisirmeSuresi; }
    public void setServisSayisi(String servisSayisi) { this.servisSayisi = servisSayisi; }
    public void setResimId(String resimId) { this.resimId = resimId; }
    public void setResimUrl(String resimUrl) { this.resimUrl = resimUrl; }

}