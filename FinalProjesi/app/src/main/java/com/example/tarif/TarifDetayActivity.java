package com.example.tarif;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class TarifDetayActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;
    private ImageButton btnBookmark;
    private boolean isFavorite = false;
    private String tarifId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_detay);



        // Firebase bağlantıları
        databaseReference = FirebaseDatabase.getInstance().getReference("tarifler");
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Giriş yapmalısınız", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = user.getUid();

        // View'ları bağla
        ImageView ivRecipeImage = findViewById(R.id.iv_recipe_image);
        TextView tvRecipeName = findViewById(R.id.tv_recipe_name);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvIngredients = findViewById(R.id.tv_ingredients);
        TextView tvSteps = findViewById(R.id.tv_steps);
        btnBookmark = findViewById(R.id.btnBookmark);

        // Bookmark butonunu görünür yap
        btnBookmark.setVisibility(View.VISIBLE);

        // Intent'ten tarif ID'sini al
        tarifId = getIntent().getStringExtra("tarifId");
        if (tarifId == null || tarifId.isEmpty()) {
            Toast.makeText(this, "Tarif bilgisi yüklenemedi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Favori durumunu kontrol et
        checkFavoriteStatus();

        // Bookmark butonuna tıklama olayı
        btnBookmark.setOnClickListener(v -> {
            Log.d("BOOKMARK", "Tıklandı"); // 🔥 Tıklama kontrolü
            if (isFavorite) {
                removeFromFavorites();
            } else {
                addToFavorites();
            }
        });

        // Firebase'den tarif detaylarını çek
        databaseReference.child(tarifId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tarif tarif = snapshot.getValue(Tarif.class);
                if (tarif != null) {
                    // Verileri view'lara yerleştir
                    tvRecipeName.setText(tarif.getAd() != null ? tarif.getAd() : "");
                    tvCategory.setText(tarif.getKategori() != null ? tarif.getKategori() : "");
                    tvIngredients.setText(tarif.getMalzemeler() != null ? tarif.getMalzemeler() : "");
                    tvSteps.setText(tarif.getYapilisAdimlari() != null ? tarif.getYapilisAdimlari() : "");

                    // Resim yükleme (resimId'ye göre)
                    try {
                        int resId = getResources().getIdentifier(
                                tarif.getResimId(), "drawable", getPackageName());
                        ivRecipeImage.setImageResource(resId);
                    } catch (Exception e) {
                        ivRecipeImage.setImageResource(R.drawable.default_resim);
                    }
                } else {
                    Toast.makeText(TarifDetayActivity.this,
                            "Tarif bulunamadı",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TarifDetayActivity.this,
                        "Tarif yüklenirken hata: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void checkFavoriteStatus() {
        firestore.collection("users")
                .document(userId)
                .collection("favoriler")
                .whereEqualTo("tarifId", tarifId)
                .get(Source.SERVER) // 🔥 Server'dan oku
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isFavorite = !task.getResult().isEmpty();
                        Log.d("FAVORI_STATUS", "Favori bulundu mu: " + isFavorite); // 🔥 Favori durumu
                        updateBookmarkIcon();
                    } else {
                        Log.e("FAVORI_CHECK", "Hata: " + task.getException().getMessage());
                    }
                });


    }


    private void toggleFavorite() {
        if (isFavorite) {
            removeFromFavorites();
        } else {
            addToFavorites();
        }
    }
    private void removeOldFavorites() {
        FirebaseFirestore.getInstance().collection("favoriler").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }
                });
    }

    private void addToFavorites() {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("tarifId", tarifId);
        favorite.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("favoriler")
                .add(favorite)
                .addOnSuccessListener(documentReference -> {
                    isFavorite = true;
                    updateBookmarkIcon();
                    Toast.makeText(this, "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FAVORI_ERROR", "Hata: " + e.getMessage());
                });

    }

    private void removeFromFavorites() {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("favoriler")
                .whereEqualTo("tarifId", tarifId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        isFavorite = false;
                                        updateBookmarkIcon();
                                        Toast.makeText(this, "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FAVORI_ERROR", "Silme hatası: " + e.getMessage());
                                    });
                        }
                    }
                });

    }

    private void updateBookmarkIcon() {
        btnBookmark.setImageResource(isFavorite ?
                R.drawable.ic_bookmark_filled :
                R.drawable.ic_bookmark_border);
    }
}