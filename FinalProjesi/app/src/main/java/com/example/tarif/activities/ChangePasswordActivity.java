package com.example.tarif.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tarif.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends FormActivity {

    private EditText editTextOldPassword, editTextNewPassword, editTextConfirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);


        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = editTextOldPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (!isValidPassword(oldPassword) || !isValidPassword(newPassword)) {
            showError("Geçerli şifreler girin");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Yeni şifreler uyuşmuyor!");
            return;
        }

        if (oldPassword.equals(newPassword)) {
            showError("Yeni şifre, eski şifreyle aynı olamaz!");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                showSuccess("Şifre başarıyla değiştirildi.");
                                finish();
                            })
                            .addOnFailureListener(this::handleFirebaseError))
                    .addOnFailureListener(this::handleFirebaseError);
        }
    }

}
