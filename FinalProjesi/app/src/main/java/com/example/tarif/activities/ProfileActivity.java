package com.example.tarif.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarif.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtNameSurname;
    private ImageView imageProfile;

    private Button btnLogout, btnEditProfile, btnChangePassword, btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBottomNavigation();

        txtNameSurname = findViewById(R.id.txtNameSurname);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        imageProfile = findViewById(R.id.imageProfile);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Giriş yapmış kullanıcı
            loadUserData(user.getUid());

            btnLogout.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);

            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            });

            btnEditProfile.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            });

            btnChangePassword.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            });

        } else {
            // Misafir kullanıcı
            txtNameSurname.setText("Misafir");

            btnLogout.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);

            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            });

            btnRegister.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, RegisterActivity.class));
                finish();

                Log.d("NAVIGATION_TEST", "Register button clicked in ProfileActivity");

            });
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) return true;

            Intent intent = null;
            if (id == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_categories) {
                intent = new Intent(this, KategoriActivity.class);
            } else if (id == R.id.nav_saved) {
                intent = new Intent(this, SavedRecipesActivity.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return true;
        });

    }


    private void loadUserData(String userId) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    String name = document.getString("name");
                    String surname = document.getString("surname");
                    String avatarIcon = document.getString("avatarIcon");

                    txtNameSurname.setText(name + " " + surname);

                    if (avatarIcon != null && !avatarIcon.isEmpty()) {
                        int resId = getResources().getIdentifier(avatarIcon, "drawable", getPackageName());
                        if (resId != 0) {
                            imageProfile.setImageResource(resId);
                        } else {
                            imageProfile.setImageResource(R.drawable.ic_profile_default);
                        }
                    } else {
                        imageProfile.setImageResource(R.drawable.ic_profile_default);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Bilgiler yüklenemedi", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadUserData(user.getUid());
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }

}
