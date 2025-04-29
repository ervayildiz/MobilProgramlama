package com.example.tarif;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextName, editTextSurname;
    private Button btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        loadUserData();

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
                                .update("email", email, "name", name, "surname", surname)
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
}
