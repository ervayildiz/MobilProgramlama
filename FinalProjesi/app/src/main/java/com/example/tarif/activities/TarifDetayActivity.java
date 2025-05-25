package com.example.tarif.activities;

import static android.content.Intent.getIntent;

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

import com.bumptech.glide.Glide;
import com.example.tarif.R;
import com.example.tarif.models.Tarif;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.List;
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
        TextView tvHazirlikSure = findViewById(R.id.tvHazirlikSure);
        TextView tvPisirmeSure = findViewById(R.id.tvPisirmeSure);
        TextView tvServisSayisi = findViewById(R.id.tvServisSayisi);
        btnBookmark = findViewById(R.id.btnBookmark);
        btnBookmark.setVisibility(View.VISIBLE);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        Uri data = getIntent().getData();
        final boolean deepLinktenGeldik;

        if (data != null) {
            List<String> segments = data.getPathSegments();
            if (segments.size() >= 2 && segments.get(0).equals("tarif")) {
                tarifId = segments.get(1);
                deepLinktenGeldik = true;
            } else {
                deepLinktenGeldik = false;
            }
        } else {
            deepLinktenGeldik = false;
        }


        if (tarifId == null) {
            tarifId = getIntent().getStringExtra("tarifId");
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
            String tarifBaslik = tvRecipeName.getText().toString();
            String shareLink = "https://finalprojesi-43f89.web.app/tarif/" + tarifId;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, tarifBaslik + "\n" + shareLink);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Tarifi paylaş"));
        });

        // ✅ Firestore’dan veriyi çek
        firestore.collection("tarifler").document(tarifId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Tarif tarif = documentSnapshot.toObject(Tarif.class);
                        if (tarif != null) {
                            tvRecipeName.setText(tarif.getAd());
                            tvCategory.setText(tarif.getKategori());
                            tvIngredients.setText(tarif.getMalzemeler());
                            tvSteps.setText(tarif.getYapilisAdimlari());
                            tvHazirlikSure.setText(tarif.getHazirlikSuresi() + " dk");
                            tvPisirmeSure.setText(tarif.getPisirmeSuresi() + " dk");
                            tvServisSayisi.setText(tarif.getServisSayisi());

                            if (deepLinktenGeldik) {
                                // Paylaşım veya tarayıcıdan geldiyse → URL'den yükle
                                String imageUrl = tarif.getResimUrl();
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.default_resim)
                                            .error(R.drawable.default_resim)
                                            .into(ivRecipeImage);
                                } else {
                                    ivRecipeImage.setImageResource(R.drawable.default_resim);
                                }
                            } else {
                                // Uygulama içindeysek → drawable’dan yükle
                                try {
                                    int resId = getResources().getIdentifier(
                                            tarif.getResimId(), "drawable", getPackageName());
                                    ivRecipeImage.setImageResource(resId);
                                } catch (Exception e) {
                                    ivRecipeImage.setImageResource(R.drawable.default_resim);
                                }
                            }

                        }
                    } else {
                        Toast.makeText(this, "Tarif bulunamadı", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Veri alınırken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
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
