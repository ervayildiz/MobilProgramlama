package com.example.tarif;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {
    private TextInputLayout tilName, tilEmail;
    private com.google.android.material.textfield.TextInputEditText etNewName, etNewEmail;
    private FirebaseFirestore db;
    private com.google.android.material.button.MaterialButton btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        db = FirebaseFirestore.getInstance();

        // View binding with new IDs
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        etNewName = findViewById(R.id.etNewName);
        etNewEmail = findViewById(R.id.etNewEmail);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            etNewEmail.setText(user.getEmail());

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            etNewName.setText(document.getString("name"));
                        }
                    });
        }

        btnSaveChanges.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String newName = etNewName.getText().toString().trim();
        String newEmail = etNewEmail.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        if (!validateInputs(newName, newEmail) || user == null) {
            return;
        }

        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Güncelleniyor...");

        if (!user.getEmail().equals(newEmail)) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateFirestoreProfile(user.getUid(), newName);
                        } else {
                            handleError(task.getException().getMessage());
                        }
                    });
        } else {
            updateFirestoreProfile(user.getUid(), newName);
        }
    }

    private boolean validateInputs(String name, String email) {
        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError("İsim boş olamaz");
            valid = false;
        } else {
            tilName.setError(null);
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email boş olamaz");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Geçerli email girin");
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        return valid;
    }

    private void updateFirestoreProfile(String userId, String newName) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);

        db.collection("users").document(userId)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        handleError(task.getException().getMessage());
                    }
                });
    }

    private void handleError(String error) {
        btnSaveChanges.setEnabled(true);
        btnSaveChanges.setText("Kaydet");
        Toast.makeText(this, "Hata: " + error, Toast.LENGTH_SHORT).show();
    }
}