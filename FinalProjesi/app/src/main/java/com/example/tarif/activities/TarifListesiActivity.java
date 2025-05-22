// Path: FinalProjesi/app/src/main/java/com/example/tarif/TarifListesiActivity.java

package com.example.tarif.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.R;
import com.example.tarif.adapters.TarifAdapter;
import com.example.tarif.data.Callback;
import com.example.tarif.data.TarifManager;
import com.example.tarif.models.Tarif;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

public class TarifListesiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TarifAdapter adapter;
    private List<Tarif> tarifList;
    private TarifManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_listesi);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        manager = new TarifManager();

        // Intent’ten kategori bilgisi alınır (null olabilir)
        String kategori = getIntent().getStringExtra("kategori");

        manager.getAll(new Callback<List<Tarif>>() {
            @Override
            public void onSuccess(List<Tarif> tarifler) {
                List<Tarif> filtrelenmisTarifler;

                if (kategori != null && !kategori.isEmpty()) {
                    filtrelenmisTarifler = new ArrayList<>();
                    for (Tarif tarif : tarifler) {
                        if (kategori.equalsIgnoreCase(tarif.getKategori())) {
                            filtrelenmisTarifler.add(tarif);
                        }
                    }
                } else {
                    filtrelenmisTarifler = tarifler;
                }

                adapter = new TarifAdapter(filtrelenmisTarifler, tarif -> {
                    Intent intent = new Intent(TarifListesiActivity.this, TarifDetayActivity.class);
                    intent.putExtra("tarifId", tarif.getTarifId());
                    startActivity(intent);
                }, (tarif, yeniDurum) -> {
                    if (yeniDurum) {
                        manager.addToFavorites(tarif, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                // İşlem başarılı, gerekirse log yazılabilir
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Hata yönetimi yapılabilir
                                Log.e("TarifFavori", "Favoriye eklenemedi", e);
                            }
                        });

                    } else {
                        manager.removeFromFavorites(tarif, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                // Başarı durumu
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("TarifFavori", "Favoriden silinemedi", e);
                            }
                        });
                    }
                });

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TarifListesiActivity.this, "Tarifler yüklenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}