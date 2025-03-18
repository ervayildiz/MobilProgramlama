package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KategoriAdapter kategoriAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        recyclerView = findViewById(R.id.recyclerViewKategoriler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Ana kategorileri oluştur
        List<Kategori> kategoriList = new ArrayList<>();
        kategoriList.add(new Kategori("Ana Yemekler"));
        kategoriList.add(new Kategori("Tatlılar"));
        kategoriList.add(new Kategori("Salatalar"));
        kategoriList.add(new Kategori("İçecekler"));

        // RecyclerView için adapter ayarla
        kategoriAdapter = new KategoriAdapter(kategoriList, kategori -> {
            // Kategoriye tıklandığında, o kategoriye ait tarifleri gösteren yeni bir aktivite başlat
            Intent intent = new Intent(KategoriActivity.this, TarifListesiActivity.class);
            intent.putExtra("kategori", kategori.getIsim());
            startActivity(intent);
        });
        recyclerView.setAdapter(kategoriAdapter);
    }
}