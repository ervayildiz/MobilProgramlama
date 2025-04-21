package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // RecyclerView ayarları
        RecyclerView recyclerView = findViewById(R.id.rv_saved_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TarifAdapter(new ArrayList<>(), tarif -> {
            Intent intent = new Intent(this, TarifDetayActivity.class);
            intent.putExtra("tarifId", tarif.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadSavedRecipes();
    }

    private void loadSavedRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Firestore'dan favori tarif ID'lerini çek
            FirebaseFirestore.getInstance().collection("favoriler")
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            List<String> favoriteIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                favoriteIds.add(document.getString("tarifId"));
                            }

                            // Realtime Database'den tüm tarifleri çek
                            FirebaseDatabase.getInstance().getReference("tarifler")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            List<Tarif> savedRecipes = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Tarif tarif = dataSnapshot.getValue(Tarif.class);
                                                if (tarif != null && favoriteIds.contains(tarif.getId())) {
                                                    savedRecipes.add(tarif);
                                                }
                                            }

                                            // Adapter'i güncelle
                                            if (savedRecipes.isEmpty()) {
                                                Toast.makeText(SavedRecipesActivity.this,
                                                        "Henüz favori tarifiniz yok",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            adapter.updateList(savedRecipes);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SavedRecipesActivity.this,
                                                    "Tarifler yüklenirken hata: " + error.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(SavedRecipesActivity.this,
                                    "Favori tarif bulunamadı",
                                    Toast.LENGTH_SHORT).show();
                            adapter.updateList(new ArrayList<>());
                        }
                    });
        } else {
            Toast.makeText(this, "Giriş yapmalısınız", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sayfa tekrar açıldığında listeyi yenile
        loadSavedRecipes();
    }
}