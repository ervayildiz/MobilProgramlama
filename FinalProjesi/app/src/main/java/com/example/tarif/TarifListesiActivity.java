package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TarifListesiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TarifAdapter tarifAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_listesi);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Intent'ten kategori bilgisini al
        Intent intent = getIntent();
        String kategori = intent.getStringExtra("kategori");

        // Kategoriye ait tarifleri filtrele
        List<Tarif> filteredTarifList = filterTariflerByKategori(kategori);

        // RecyclerView için adapter ayarla
        tarifAdapter = new TarifAdapter(this, filteredTarifList, tarif -> {
            // TarifDetayActivity'yi başlat ve tarif bilgilerini gönder
            Intent detailIntent = new Intent(TarifListesiActivity.this, TarifDetayActivity.class);
            detailIntent.putExtra("tarif", tarif);
            startActivity(detailIntent);
        });
        recyclerView.setAdapter(tarifAdapter);
    }

    // Kategoriye göre tarifleri filtrele
    private List<Tarif> filterTariflerByKategori(String kategori) {
        List<Tarif> allTarifler = getSampleTarifList(); // Tüm tarifleri al
        List<Tarif> filteredTarifler = new ArrayList<>();

        for (Tarif tarif : allTarifler) {
            if (tarif.getKategori().equals(kategori)) {
                filteredTarifler.add(tarif);
            }
        }

        return filteredTarifler;
    }

    // Örnek tarif listesi (Bu kısmı kendi veri kaynağınıza göre güncelleyin)
    private List<Tarif> getSampleTarifList() {
        List<Tarif> tarifList = new ArrayList<>();
        tarifList.add(new Tarif("Pizza", "Ana Yemekler", "Lezzetli bir pizza tarifi.", R.drawable.pizza, "Malzemeler: Un, su, maya, domates sosu, peynir", "Yapılış: Hamuru yoğur, sosu sür, peynir ekle ve fırında pişir."));
        tarifList.add(new Tarif("Makarna", "Ana Yemekler", "Kolay makarna tarifi.", R.drawable.makarna, "Malzemeler: Makarna, su, tuz, sos", "Yapılış: Makarnayı haşla, sosu ekle ve karıştır."));
        tarifList.add(new Tarif("Çikolatalı Kek", "Tatlılar", "Çikolatalı kek tarifi.", R.drawable.kek, "Malzemeler: Un, şeker, kakao, yumurta, süt", "Yapılış: Malzemeleri karıştır, fırında pişir."));
        tarifList.add(new Tarif("Salata", "Salatalar", "Sağlıklı Salata tarifi.", R.drawable.salata, "Malzemeler: Marul, domates, salatalık, roka", "Yapılış: Sebzeleri doğra, servis et."));
        return tarifList;
    }
}