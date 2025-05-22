package com.example.tarif;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("tarifler");
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Giriş yapmalısınız", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = user.getUid();

        ImageView ivRecipeImage = findViewById(R.id.iv_recipe_image);
        TextView tvRecipeName = findViewById(R.id.tv_recipe_name);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvIngredients = findViewById(R.id.tv_ingredients);
        TextView tvSteps = findViewById(R.id.tv_steps);
        btnBookmark = findViewById(R.id.btnBookmark);
        btnBookmark.setVisibility(View.VISIBLE);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        Uri data = getIntent().getData();
        if (data != null && data.getQueryParameter("id") != null) {
            tarifId = data.getQueryParameter("id");
        }

        if (tarifId == null) {
            tarifId = getIntent().getStringExtra("tarifId");
        }

        if (tarifId == null || tarifId.isEmpty()) {
            Toast.makeText(this, "Tarif bilgisi yüklenemedi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        checkFavoriteStatus();

        btnBookmark.setOnClickListener(v -> {
            if (isFavorite) {
                removeFromFavorites();
            } else {
                addToFavorites();
            }
        });

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Log.d("SHARE", "Paylaş butonuna tıklandı");

            String tarifBaslik = tvRecipeName.getText().toString();
            String shareLink = "https://finalprojesi-43f89.web.app/tarif/" + tarifId;

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, tarifBaslik + "\n" + shareLink);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Tarifi paylaş"));
        });




        databaseReference.child(tarifId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tarif tarif = snapshot.getValue(Tarif.class);
                if (tarif != null) {
                    tvRecipeName.setText(tarif.getAd() != null ? tarif.getAd() : "");
                    tvCategory.setText(tarif.getKategori() != null ? tarif.getKategori() : "");
                    tvIngredients.setText(tarif.getMalzemeler() != null ? tarif.getMalzemeler() : "");
                    tvSteps.setText(tarif.getYapilisAdimlari() != null ? tarif.getYapilisAdimlari() : "");

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
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isFavorite = !task.getResult().isEmpty();
                        updateBookmarkIcon();
                    } else {
                        Log.e("FAVORI_CHECK", "Hata: " + task.getException().getMessage());
                    }
                });
    }

    private void addToFavorites() {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("tarifId", tarifId);
        favorite.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("users")
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
        firestore.collection("users")
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
