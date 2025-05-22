package com.example.tarif.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarif.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextName, editTextSurname;
    private Button btnSaveProfile;
    private String selectedAvatar = "ic_profile_default"; // Varsayılan


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        loadUserData();

        // Avatarları tanımla
        ImageView avatarPink = findViewById(R.id.avatar_pink);
        ImageView avatarGreen = findViewById(R.id.avatar_green);
        ImageView avatarBlue = findViewById(R.id.avatar_blue);
        ImageView avatarYellow = findViewById(R.id.avatar_yellow);
        ImageView avatarPurple = findViewById(R.id.avatar_purple);
        ImageView avatarRed = findViewById(R.id.avatar_red);

        // Hepsini listeye al
        List<ImageView> avatarViews = Arrays.asList(
                avatarPink, avatarGreen, avatarBlue,
                avatarYellow, avatarPurple, avatarRed
        );

        // Tıklanınca seçilen büyüsün, diğerleri küçülsün
        avatarPink.setOnClickListener(v -> {
            selectedAvatar = "ic_profile_pink";
            updateAvatarSelection(avatarPink, avatarViews);
        });

        avatarGreen.setOnClickListener(v -> {
            selectedAvatar = "ic_profile_green";
            updateAvatarSelection(avatarGreen, avatarViews);
        });

        avatarBlue.setOnClickListener(v -> {
            selectedAvatar = "ic_profile_blue";
            updateAvatarSelection(avatarBlue, avatarViews);
        });

        avatarYellow.setOnClickListener(v -> {
            selectedAvatar = "ic_profile_yellow";
            updateAvatarSelection(avatarYellow, avatarViews);
        });

        avatarPurple.setOnClickListener(v -> {
            selectedAvatar = "ic_profile_purple";
            updateAvatarSelection(avatarPurple, avatarViews);
        });

        avatarRed.setOnClickListener(v -> {
            selectedAvatar = "ic_profile_red";
            updateAvatarSelection(avatarRed, avatarViews);
        });

        btnSaveProfile.setOnClickListener(v -> saveUserData());
    }


    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            editTextEmail.setText(user.getEmail());

            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        editTextName.setText(doc.getString("name"));
                        editTextSurname.setText(doc.getString("surname"));

                        // avatarIcon'u Firestore'dan çek
                        String avatarIcon = doc.getString("avatarIcon");
                        if (avatarIcon != null && !avatarIcon.isEmpty()) {
                            selectedAvatar = avatarIcon;

                            // ilgili avatar view'u bul
                            ImageView selectedView = getAvatarViewByIconName(avatarIcon);

                            if (selectedView != null) {
                                List<ImageView> avatarViews = Arrays.asList(
                                        findViewById(R.id.avatar_pink),
                                        findViewById(R.id.avatar_green),
                                        findViewById(R.id.avatar_blue),
                                        findViewById(R.id.avatar_yellow),
                                        findViewById(R.id.avatar_purple),
                                        findViewById(R.id.avatar_red)
                                );

                                updateAvatarSelection(selectedView, avatarViews);
                            }
                        }
                    });
        }
    }



    private void saveUserData() {
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            Toast.makeText(this, "Alanlar boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Authentication email güncelle
            user.updateEmail(email)
                    .addOnSuccessListener(aVoid -> {
                        // Firestore bilgileri güncelle
                        FirebaseFirestore.getInstance().collection("users")
                                .document(user.getUid())
                                .update("email", email, "name", name, "surname", surname, "avatarIcon", selectedAvatar)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Firestore hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Email güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void updateAvatarSelection(ImageView selectedView, List<ImageView> allViews) {
        for (ImageView view : allViews) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = dpToPx(60); // küçük boy
            params.height = dpToPx(60);
            view.setLayoutParams(params);
        }

        // Seçilen avatara büyük boy
        ViewGroup.LayoutParams selectedParams = selectedView.getLayoutParams();
        selectedParams.width = dpToPx(72); // bir tık büyük
        selectedParams.height = dpToPx(72);
        selectedView.setLayoutParams(selectedParams);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private ImageView getAvatarViewByIconName(String iconName) {
        switch (iconName) {
            case "ic_profile_pink": return findViewById(R.id.avatar_pink);
            case "ic_profile_green": return findViewById(R.id.avatar_green);
            case "ic_profile_blue": return findViewById(R.id.avatar_blue);
            case "ic_profile_yellow": return findViewById(R.id.avatar_yellow);
            case "ic_profile_purple": return findViewById(R.id.avatar_purple);
            case "ic_profile_red": return findViewById(R.id.avatar_red);
            default: return null;
        }
    }



}
