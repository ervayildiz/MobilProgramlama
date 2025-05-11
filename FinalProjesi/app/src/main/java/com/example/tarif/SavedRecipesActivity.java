package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.data.Callback;
import com.example.tarif.data.TarifManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipesActivity extends AppCompatActivity {
    private TarifAdapter adapter;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private RecyclerView rvKayitli;
    private TextView txtBos;
    private List<Tarif> savedTarifList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        // Firebase bağlantıları
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tarifler");

        // Bottom Navigation ayarları
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_saved);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, KategoriActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_saved) {
                // Zaten bu sayfadayız
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });


        rvKayitli = findViewById(R.id.rv_saved_recipes);
        txtBos = findViewById(R.id.txtEmptyMessage);
        rvKayitli.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TarifAdapter(savedTarifList, tarif -> {
            Intent intent = new Intent(SavedRecipesActivity.this, TarifDetayActivity.class);
            intent.putExtra("tarifId", tarif.getTarifId());
            startActivity(intent);
        });
        rvKayitli.setAdapter(adapter);

        loadSavedRecipes();
    }

    private void loadSavedRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            txtBos.setVisibility(View.VISIBLE);
            rvKayitli.setVisibility(View.GONE);
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .get()
                .addOnSuccessListener(favSnapshot -> {
                    List<String> favoriIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : favSnapshot) {
                        favoriIds.add(doc.getString("tarifId"));
                    }

                    TarifManager tarifManager = new TarifManager();
                    tarifManager.getAll(new Callback<List<Tarif>>() {
                        @Override
                        public void onSuccess(List<Tarif> result) {
                            List<Tarif> kayitliList = new ArrayList<>();
                            for (Tarif tarif : result) {
                                if (favoriIds.contains(tarif.getTarifId())) {
                                    kayitliList.add(tarif);
                                }
                            }

                            if (kayitliList.isEmpty()) {
                                txtBos.setVisibility(View.VISIBLE);
                                rvKayitli.setVisibility(View.GONE);
                            } else {
                                txtBos.setVisibility(View.GONE);
                                rvKayitli.setVisibility(View.VISIBLE);
                                savedTarifList.clear();
                                savedTarifList.addAll(kayitliList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(SavedRecipesActivity.this, "Kayıtlı tarifler yüklenemedi", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Sayfa tekrar açıldığında listeyi yenile
        loadSavedRecipes();
    }
}