package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView ve Adapter ayarları
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Tarif> tarifList = new ArrayList<>();
        tarifList.add(new Tarif("Pizza", "Ana Yemek", "Lezzetli bir pizza tarifi.", R.drawable.pizza, "Malzemeler: Un, su, maya, domates sosu, peynir", "Yapılış: Hamuru yoğur, sosu sür, peynir ekle ve fırında pişir."));
        tarifList.add(new Tarif("Makarna", "Ana Yemek", "Kolay makarna tarifi.", R.drawable.makarna, "Malzemeler: Makarna, su, tuz, sos", "Yapılış: Makarnayı haşla, sosu ekle ve karıştır."));
        tarifList.add(new Tarif("Çikolatalı Kek", "Tatlı", "Çikolatalı kek tarifi.", R.drawable.kek, "Malzemeler: Un, şeker, kakao, yumurta, süt", "Yapılış: Malzemeleri karıştır, fırında pişir."));

        TarifAdapter tarifAdapter = new TarifAdapter(this, tarifList, tarif -> {
            // TarifDetayActivity'yi başlat ve tarif bilgilerini gönder
            Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
            intent.putExtra("tarif", tarif); // Tarif nesnesini Serializable olarak gönder
            startActivity(intent);
        });
        recyclerView.setAdapter(tarifAdapter);

        // Bottom Navigation ayarları
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_ana_sayfa) {
                // Ana sayfada kal
            } else if (itemId == R.id.nav_kategoriler) {
                // Kategoriler sayfasına geçiş
                Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                intent.putExtra("kategori", "Ana Yemek"); // Örnek olarak "Ana Yemek" kategorisini gönder
                startActivity(intent);
            } else if (itemId == R.id.nav_kaydedilenler) {
                // Kaydedilenler sayfasına geçiş
            } else if (itemId == R.id.nav_arama) {
                // Arama sayfasına geçiş
            } else if (itemId == R.id.nav_profile) {
                // Profil sayfasına geçiş
            }
            return true; // Önemli: true döndürün, aksi takdirde seçim işlemi gerçekleşmez.
        });
    }
}