// Path: FinalProjesi/app/src/main/java/com/example/tarif/MainActivity.java

package com.example.tarif;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.TarifAdapter;
import com.example.tarif.data.Callback;
import com.example.tarif.data.TarifManager;
import com.example.tarif.Tarif;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtArama;
    private RecyclerView rvSearchResults;
    private TarifAdapter searchAdapter;
    private final List<Tarif> allTarifList = new ArrayList<>();
    private static List<Tarif> cachedOnerilenTarifler = null;
    private final TarifManager tarifManager = new TarifManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtHosgeldin = findViewById(R.id.txtHosgeldin);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            txtHosgeldin.setText("Merhaba!");
        }

        edtArama = findViewById(R.id.edtArama);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new TarifAdapter(new ArrayList<>(), tarif -> {
            Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
            intent.putExtra("tarifId", tarif.getTarifId());
            startActivity(intent);
        }, (tarif, yeniDurum) -> {
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
        rvSearchResults.setAdapter(searchAdapter);

        RecyclerView rvOnerilen = findViewById(R.id.rvOnerilenTarifler);
        rvOnerilen.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadRecipes(rvOnerilen);

        RecyclerView rvKayitli = findViewById(R.id.rvKayitliTarifler);
        rvKayitli.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        TextView txtKayitliBos = findViewById(R.id.txtKayitliBos);
        TextView btnGozAt = findViewById(R.id.btnGozAt);
        btnGozAt.setPaintFlags(btnGozAt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnGozAt.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SavedRecipesActivity.class)));

        loadSavedRecipes(user, rvKayitli, txtKayitliBos, btnGozAt);

        edtArama.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString().trim());
                rvSearchResults.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;

            Intent intent = null;
            if (id == R.id.nav_categories) {
                intent = new Intent(this, KategoriActivity.class);
            } else if (id == R.id.nav_saved) {
                intent = new Intent(this, SavedRecipesActivity.class);
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




        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        Uri deepLink = null;
                        if (data != null) {
                            deepLink = data.getLink();
                            if (deepLink != null && deepLink.getQueryParameter("id") != null) {
                                String tarifId = deepLink.getQueryParameter("id");
                                Intent i = new Intent(MainActivity.this, TarifDetayActivity.class);
                                i.putExtra("tarifId", tarifId);
                                startActivity(i);
                            }
                        }
                    }
                });
    }

    private void loadRecipes(RecyclerView rvOnerilen) {
        DatabaseReference tarifRef = FirebaseDatabase.getInstance().getReference("tarifler");
        tarifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Tarif> tarifList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tarif tarif = dataSnapshot.getValue(Tarif.class);
                    tarif.setTarifId(dataSnapshot.getKey());
                    tarifList.add(tarif);
                }
                allTarifList.clear();
                allTarifList.addAll(tarifList);

                if (cachedOnerilenTarifler == null) {
                    Collections.shuffle(tarifList);
                    cachedOnerilenTarifler = tarifList.subList(0, Math.min(6, tarifList.size()));
                }

                TarifAdapter adapter = new TarifAdapter(cachedOnerilenTarifler, tarif -> {
                    Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
                    intent.putExtra("tarifId", tarif.getTarifId());
                    startActivity(intent);
                }, (tarif, yeniDurum) -> {
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
                rvOnerilen.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadSavedRecipes(FirebaseUser user, RecyclerView rvKayitli, TextView txtKayitliBos, TextView btnGozAt) {
        if (user == null) {
            txtKayitliBos.setVisibility(View.VISIBLE);
            rvKayitli.setVisibility(View.GONE);
            btnGozAt.setVisibility(View.GONE);
            return;
        }
        tarifManager.getFavorites(new Callback<List<Tarif>>() {
            @Override
            public void onSuccess(List<Tarif> kayitliList) {
                if (kayitliList.isEmpty()) {
                    txtKayitliBos.setVisibility(View.VISIBLE);
                    rvKayitli.setVisibility(View.GONE);
                    btnGozAt.setVisibility(View.GONE);
                } else {
                    txtKayitliBos.setVisibility(View.GONE);
                    rvKayitli.setVisibility(View.VISIBLE);
                    btnGozAt.setVisibility(View.VISIBLE);
                    List<Tarif> gosterilecek = kayitliList.subList(0, Math.min(6, kayitliList.size()));
                    TarifAdapter kayitliAdapter = new TarifAdapter(gosterilecek, tarif -> {
                        Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
                        intent.putExtra("tarifId", tarif.getTarifId());
                        startActivity(intent);
                    }, (tarif, yeniDurum) -> {
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
                    rvKayitli.setAdapter(kayitliAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                txtKayitliBos.setVisibility(View.VISIBLE);
                rvKayitli.setVisibility(View.GONE);
                btnGozAt.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home); // Bu sayfaya özel
    }

    private void filterRecipes(String query) {
        List<Tarif> filteredList = new ArrayList<>();
        for (Tarif tarif : allTarifList) {
            if (tarif.getAd().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(tarif);
            }
        }
        searchAdapter.updateList(filteredList);
    }
}
