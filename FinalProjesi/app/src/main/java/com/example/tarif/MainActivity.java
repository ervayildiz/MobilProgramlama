package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Zaten ana sayfadayız
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, KategoriActivity.class));
                finish();
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

        initializeUI();
    }

    private void initializeUI() {
        // RecyclerView'ları tanımla
        RecyclerView rvRecommended = findViewById(R.id.rvDenemelisin);
        RecyclerView rvBookmarked = findViewById(R.id.rvBookmarks);

        // LayoutManager ayarla
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvBookmarked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Verileri yükle
        loadRecipes(rvRecommended, rvBookmarked);
    }

    private void loadRecipes(RecyclerView rvRecommended, RecyclerView rvBookmarked) {
        // Önerilen tarifler
        RecipeRepository.getRecipesFromSource(this, new RecipeRepository.DataLoadListener() {
            @Override
            public void onDataLoaded(List<Tarif> recipes) {
                setupAdapter(rvRecommended, recipes);
            }
            @Override
            public void onError(Exception e) {
                showError(R.string.error_load_recommended);
            }
        });

        // Kayıtlı tarifler (null kontrolü ile)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            RecipeRepository.getSavedRecipes(user.getUid(), new RecipeRepository.DataLoadListener() {
                @Override
                public void onDataLoaded(List<Tarif> recipes) {
                    setupAdapter(rvBookmarked, recipes);
                }
                @Override
                public void onError(Exception e) {
                    showError(R.string.error_load_bookmarked);
                }
            });
        }
    }

    private void setupAdapter(RecyclerView recyclerView, List<Tarif> recipes) {
        if (recipes != null && !recipes.isEmpty()) {
            TarifAdapter adapter = new TarifAdapter(recipes, tarif -> {
                Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
                intent.putExtra("tarifId", tarif.getId()); // Sadece ID gönder
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void showError(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }
}