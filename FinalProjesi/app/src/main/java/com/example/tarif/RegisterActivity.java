package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tarif.FormActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends FormActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etName, etSurname, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        btnRegister.setOnClickListener(v -> registerUser());
        btnLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Tüm alanları doldurun");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Geçersiz email");
            return;
        }

        if (!isValidPassword(password)) {
            showError("Şifre en az 8 karakter ve harf+sayı içermeli");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Şifreler eşleşmiyor");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("surname", surname);
                    userMap.put("email", email);

                    db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                showSuccess("Kayıt başarılı! Giriş yapabilirsiniz.");
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(this::handleFirebaseError);
                })
                .addOnFailureListener(this::handleFirebaseError);
    }
}