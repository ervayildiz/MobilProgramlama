package com.example.tarif.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.R;
import com.example.tarif.adapters.TarifAdapter;
import com.example.tarif.models.Tarif;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TumTariflerActivity extends AppCompatActivity
        implements TarifAdapter.OnItemClickListener, TarifAdapter.FavoriToggleListener {

    private RecyclerView recyclerView;
    private TarifAdapter adapter;
    private List<Tarif> tarifListesi;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tum_tarifler);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Giriş yapmalısınız", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = currentUser.getUid();

        recyclerView = findViewById(R.id.rvTumTarifler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tarifListesi = new ArrayList<>();
        adapter = new TarifAdapter(tarifListesi, this, this);
        recyclerView.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tarifleriYukle();
    }

    private void tarifleriYukle() {
        firestore.collection("tarifler")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tarifListesi.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tarif tarif = doc.toObject(Tarif.class);
                        tarifListesi.add(tarif);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Tarifler yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(Tarif tarif) {
        Intent intent = new Intent(this, TarifDetayActivity.class);
        intent.putExtra("tarifId", tarif.getTarifId());
        startActivity(intent);
    }

    @Override
    public void onFavoriToggled(Tarif tarif, boolean yeniDurum) {
        if (yeniDurum) {
            Map<String, Object> favorite = new HashMap<>();
            favorite.put("tarifId", tarif.getTarifId());
            favorite.put("timestamp", FieldValue.serverTimestamp());

            firestore.collection("users")
                    .document(userId)
                    .collection("favoriler")
                    .add(favorite)
                    .addOnSuccessListener(docRef -> {
                        // Favori eklendi
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Favori eklenemedi", Toast.LENGTH_SHORT).show());
        } else {
            firestore.collection("users")
                    .document(userId)
                    .collection("favoriler")
                    .whereEqualTo("tarifId", tarif.getTarifId())
                    .get()
                    .addOnSuccessListener(query -> {
                        for (QueryDocumentSnapshot doc : query) {
                            doc.getReference().delete();
                        }
                    });
        }
    }
}
