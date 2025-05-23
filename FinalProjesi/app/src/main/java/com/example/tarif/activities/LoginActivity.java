package com.example.tarif.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tarif.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends FormActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtRegister, txtExplore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtRegister = findViewById(R.id.txtRegister);
        txtExplore = findViewById(R.id.txtExplore);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());

        txtForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
        txtRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        txtExplore.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (!isValidEmail(email)) {
            showError("Geçersiz email");
            return;
        }

        if (password.isEmpty()) {
            showError("Şifre boş olamaz");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    showSuccess("Giriş başarılı: " + user.getEmail());
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(this::handleFirebaseError);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
