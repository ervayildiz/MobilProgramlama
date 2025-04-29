package com.example.tarif;

import android.content.Intent;
import android.graphics.Paint;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtArama;
    private RecyclerView rvSearchResults;
    private TarifAdapter searchAdapter;

    private List<Tarif> allTarifList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtHosgeldin = findViewById(R.id.txtHosgeldin);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String name = documentSnapshot.getString("name");
                        txtHosgeldin.setText("Merhaba, " + name + "!");
                    });
        }

        edtArama = findViewById(R.id.edtArama);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new TarifAdapter(new ArrayList<>(), tarif -> {
            Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
            intent.putExtra("tarifId", tarif.getTarifId());
            startActivity(intent);
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
        btnGozAt.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SavedRecipesActivity.class));
        });
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
            else if (id == R.id.nav_categories) {
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
                allTarifList.addAll(tarifList); // <-- BURAYA EKLE

                List<Tarif> gosterilecek = tarifList.subList(0, Math.min(6, tarifList.size()));
                TarifAdapter adapter = new TarifAdapter(gosterilecek, tarif -> {
                    Intent intent = new Intent(MainActivity.this, TarifDetayActivity.class);
                    intent.putExtra("tarifId", tarif.getTarifId());
                    startActivity(intent);
                });
                rvOnerilen.setAdapter(adapter);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadSavedRecipes(FirebaseUser user, RecyclerView rvKayitli, TextView txtKayitliBos, TextView btnGozAt) {
        if (user == null) {
            txtKayitliBos.setVisibility(View.VISIBLE);
            rvKayitli.setVisibility(View.GONE);
            btnGozAt.setVisibility(View.GONE);
            return;
        }
        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .get()
                .addOnSuccessListener(favSnapshot -> {
                    List<String> favoriteIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : favSnapshot) {
                        favoriteIds.add(doc.getString("tarifId"));
                    }
                    DatabaseReference tarifRef = FirebaseDatabase.getInstance().getReference("tarifler");
                    tarifRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Tarif> kayitliList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Tarif tarif = dataSnapshot.getValue(Tarif.class);
                                tarif.setTarifId(dataSnapshot.getKey());
                                if (favoriteIds.contains(tarif.getTarifId())) {
                                    kayitliList.add(tarif);
                                }
                            }
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
                                });
                                rvKayitli.setAdapter(kayitliAdapter);
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                });
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
