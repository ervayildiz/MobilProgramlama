package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KategoriAdapter kategoriAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        // Bottom Navigation Ayarları
        setupBottomNavigation();

        // RecyclerView Ayarları
        recyclerView = findViewById(R.id.recyclerViewKategoriLer);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Kategorileri yükle
        loadKategoriler();
    }

    private void loadKategoriler() {
        List<Kategori> kategoriList = getKategoriListesiStatic();

        kategoriAdapter = new KategoriAdapter(kategoriList, kategori -> {
            Intent intent = new Intent(KategoriActivity.this, TarifListesiActivity.class);
            intent.putExtra("kategori", kategori.getIsim());
            startActivity(intent);
        });

        recyclerView.setAdapter(kategoriAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_categories);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_categories) {
                // Zaten bu sayfadayız
                return true;
            } else if (id == R.id.nav_saved) {
                startActivity(new Intent(this, SavedRecipesActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    public static List<Kategori> getKategoriListesiStatic() {
        List<Kategori> kategoriList = new ArrayList<>();
        kategoriList.add(new Kategori("Tatlı", R.drawable.ic_tatli));
        kategoriList.add(new Kategori("Tavuk", R.drawable.ic_tavuk));
        kategoriList.add(new Kategori("Atıştırmalık", R.drawable.ic_atistirmalik));
        kategoriList.add(new Kategori("Balık", R.drawable.ic_balik));
        kategoriList.add(new Kategori("Börek", R.drawable.ic_borek));
        kategoriList.add(new Kategori("Çorba", R.drawable.ic_corba));
        kategoriList.add(new Kategori("Dünya Mutfakları", R.drawable.ic_dunya_mutfaklari));
        kategoriList.add(new Kategori("Et", R.drawable.ic_et));
        kategoriList.add(new Kategori("Glutensiz", R.drawable.ic_glutensiz));
        kategoriList.add(new Kategori("Hamburger", R.drawable.ic_hamburger));
        kategoriList.add(new Kategori("Hamur İşi", R.drawable.ic_hamur_isi));
        kategoriList.add(new Kategori("İçecek", R.drawable.ic_icecek));
        kategoriList.add(new Kategori("Kek", R.drawable.ic_kek));
        kategoriList.add(new Kategori("Köfte", R.drawable.ic_kofte));
        kategoriList.add(new Kategori("Kurabiye", R.drawable.ic_kurabiye));
        kategoriList.add(new Kategori("Makarna", R.drawable.ic_makarna));
        kategoriList.add(new Kategori("Meze", R.drawable.ic_meze));
        kategoriList.add(new Kategori("Pasta", R.drawable.ic_pasta));
        kategoriList.add(new Kategori("Pizza", R.drawable.ic_pizza));
        kategoriList.add(new Kategori("Poğaça", R.drawable.ic_pogaca));
        kategoriList.add(new Kategori("Salata", R.drawable.ic_salata));
        kategoriList.add(new Kategori("Sebze", R.drawable.ic_sebze));

        kategoriList.add(new Kategori("Zeytinyağlı", R.drawable.ic_zeytinyagli));
        return kategoriList;
    }
}