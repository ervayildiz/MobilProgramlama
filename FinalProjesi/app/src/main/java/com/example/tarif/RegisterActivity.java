package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etName, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // View binding
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        // Register button click
        btnRegister.setOnClickListener(v -> registerUser());

        // Login redirect
        btnLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation checks
        if (TextUtils.isEmpty(name)) {
            etName.setError("İsim boş olamaz");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email boş olamaz");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Şifre boş olamaz");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Şifre en az 6 karakter olmalı");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Şifreler eşleşmiyor");
            return;
        }

        // Firebase Authentication registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Send verification email
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Doğrulama emaili gönderildi",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Save user data to Firestore
                            saveUserToFirestore(user.getUid(), name, email);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Kayıt başarısız: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("createdAt", com.google.firebase.Timestamp.now());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this,
                            "Kayıt başarılı! Giriş yapabilirsiniz",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this,
                            "Firestore hatası: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}