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
            intent.putExtra("tarifId", tarif.getTarifId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadSavedRecipes();
    }

    private void loadSavedRecipes() {
        TextView txtEmptyMessage = findViewById(R.id.txtEmptyMessage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            adapter.updateList(new ArrayList<>());
            txtEmptyMessage.setVisibility(View.VISIBLE);
            txtEmptyMessage.setText("Favori tariflerinizi görmek için giriş yapın.");
        } else {
            txtEmptyMessage.setVisibility(View.GONE);
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .collection("favoriler")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            List<String> favoriteIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                favoriteIds.add(document.getString("tarifId"));
                            }

                            FirebaseDatabase.getInstance().getReference("tarifler")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            List<Tarif> savedRecipes = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Tarif tarif = dataSnapshot.getValue(Tarif.class);
                                                if (tarif != null) {
                                                    tarif.setTarifId(dataSnapshot.getKey());
                                                    if (favoriteIds.contains(tarif.getTarifId())) {
                                                        savedRecipes.add(tarif);
                                                    }
                                                }
                                            }

                                            if (savedRecipes.isEmpty()) {
                                                txtEmptyMessage.setVisibility(View.VISIBLE);
                                                txtEmptyMessage.setText("Henüz favori tarifiniz yok.");
                                            } else {
                                                txtEmptyMessage.setVisibility(View.GONE);
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
                            txtEmptyMessage.setVisibility(View.VISIBLE);
                            txtEmptyMessage.setText("Henüz favori tarifiniz yok.");
                            adapter.updateList(new ArrayList<>());
                        }
                    });
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // Sayfa tekrar açıldığında listeyi yenile
        loadSavedRecipes();
    }
}