package com.example.tarif.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarif.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextView tvMessage;
    private Button btnResetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.et_email);
        tvMessage = findViewById(R.id.tv_message);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tvMessage.setText("Lütfen e-posta girin.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvMessage.setText("Geçerli bir e-posta adresi girin.");
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tvMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark)); // Yeşil renk
                        tvMessage.setText("Şifre sıfırlama e-postası gönderildi.");
                    } else {
                        tvMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Hata için kırmızı
                        tvMessage.setText("Hata: " + task.getException().getMessage());
                    }
                });
    }
}
