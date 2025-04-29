package com.example.tarif;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseRepository {
    private final FirebaseFirestore db;
    private final CollectionReference tariflerRef;

    public FirebaseRepository() {
        db = FirebaseFirestore.getInstance();
        tariflerRef = db.collection("tarifler");
    }

    // Tüm tarifleri getir
    public void getTumTarifler(OnCompleteListener<QuerySnapshot> listener) {
        tariflerRef.get().addOnCompleteListener(listener);
    }

    // Kategoriye göre filtrele
    public void getTariflerByKategori(String kategori, OnCompleteListener<QuerySnapshot> listener) {
        tariflerRef.whereEqualTo("kategori", kategori).get().addOnCompleteListener(listener);
    }

    // Yeni tarif ekle
    public void tarifEkle(Tarif tarif, OnCompleteListener<Void> listener) {
        tariflerRef.document(tarif.getTarifId()).set(tarif).addOnCompleteListener(listener);
    }

    public void getFavoriTarifler(String userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("favoriler")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(listener);
    }
}
