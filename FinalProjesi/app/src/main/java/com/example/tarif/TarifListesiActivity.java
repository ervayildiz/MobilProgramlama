package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TarifListesiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TarifAdapter tarifAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_listesi);

        // Firebase Database referansı
        databaseReference = FirebaseDatabase.getInstance().getReference("tarifler");

        // RecyclerView ayarları
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Kategoriye göre filtreleme
        String kategoriAdi = getIntent().getStringExtra("kategori");
        if (kategoriAdi != null) {
            loadTariflerByKategori(kategoriAdi);
        }
    }

    private void loadTariflerByKategori(String kategori) {
        Query query = databaseReference.orderByChild("kategori").equalTo(kategori);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Tarif> tarifList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tarif tarif = snapshot.getValue(Tarif.class);
                    if (tarif != null) {
                        tarif.setId(snapshot.getKey()); // Firebase ID'sini kaydet
                        tarifList.add(tarif);
                    }
                }

                tarifAdapter = new TarifAdapter(tarifList, tarif -> {
                    Intent intent = new Intent(TarifListesiActivity.this, TarifDetayActivity.class);
                    intent.putExtra("tarifId", tarif.getId()); // Sadece ID gönder
                    startActivity(intent);
                });
                recyclerView.setAdapter(tarifAdapter);

                if (tarifList.isEmpty()) {
                    Toast.makeText(TarifListesiActivity.this,
                            "Bu kategoride tarif bulunamadı",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TarifListesiActivity.this,
                        "Veri çekilirken hata: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}