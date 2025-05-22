// Path: FinalProjesi/app/src/main/java/com/example/tarif/SavedRecipesActivity.java

package com.example.tarif.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.R;
import com.example.tarif.models.Tarif;
import com.example.tarif.adapters.TarifAdapter;
import com.example.tarif.data.Callback;
import com.example.tarif.data.TarifManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SavedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvKayitli;
    private TextView txtKayitliBos;
    private final TarifManager tarifManager = new TarifManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        rvKayitli = findViewById(R.id.rv_saved_recipes);
        txtKayitliBos = findViewById(R.id.txtEmptyMessage);
        rvKayitli.setLayoutManager(new LinearLayoutManager(this));

        loadSavedRecipes();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_saved);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_saved) return true;

            Intent intent = null;
            if (id == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_categories) {
                intent = new Intent(this, KategoriActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return true;
        });

    }

        private void loadSavedRecipes() {
        tarifManager.getFavorites(new Callback<List<Tarif>>() {
            @Override
            public void onSuccess(List<Tarif> savedTarifList) {
                if (savedTarifList.isEmpty()) {
                    txtKayitliBos.setVisibility(View.VISIBLE);
                    rvKayitli.setVisibility(View.GONE);
                } else {
                    txtKayitliBos.setVisibility(View.GONE);
                    rvKayitli.setVisibility(View.VISIBLE);

                    TarifAdapter adapter = new TarifAdapter(savedTarifList,
                            tarif -> {
                                Intent intent = new Intent(SavedRecipesActivity.this, TarifDetayActivity.class);
                                intent.putExtra("tarifId", tarif.getTarifId());
                                startActivity(intent);
                            },
                            (tarif, yeniDurum) -> {
                                if (yeniDurum) {
                                    tarifManager.addToFavorites(tarif, new Callback<Void>() {
                                        @Override public void onSuccess(Void result) {}
                                        @Override public void onFailure(Exception e) {}
                                    });
                                } else {
                                    tarifManager.removeFromFavorites(tarif, new Callback<Void>() {
                                        @Override public void onSuccess(Void result) {}
                                        @Override public void onFailure(Exception e) {}
                                    });
                                }
                            });

                    rvKayitli.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                txtKayitliBos.setVisibility(View.VISIBLE);
                rvKayitli.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_saved);
        loadSavedRecipes();
    }

} 
