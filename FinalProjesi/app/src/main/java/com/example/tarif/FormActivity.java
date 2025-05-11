package com.example.tarif;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tarif.ValidationUtil;

public abstract class FormActivity extends AppCompatActivity {

    protected boolean isValidEmail(String email) {
        return ValidationUtil.isValidEmail(email);
    }

    protected boolean isValidPassword(String password) {
        return ValidationUtil.isValidPassword(password);
    }

    protected void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void handleFirebaseError(Exception e) {
        showError("Firebase hatası: " + e.getMessage());
    }
}
