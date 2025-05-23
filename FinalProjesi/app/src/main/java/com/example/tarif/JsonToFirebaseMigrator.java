package com.example.tarif;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import com.example.tarif.models.Tarif;

public class JsonToFirebaseMigrator {
    public static void migrate(Context context) {
        // JSON'dan tarifleri oku
        List<Tarif> tarifler = JsonReader.readTariflerFromJson(context);

        if (tarifler == null || tarifler.isEmpty()) {
            Log.e("Migration", "JSON'dan tarif okunamadı veya liste boş");
            return;
        }

        FirebaseRepository repository = new FirebaseRepository();

        for (Tarif tarif : tarifler) {
            // Rastgele ID oluştur
            tarif.setTarifId(UUID.randomUUID().toString());

            // Firebase'e ekle
            repository.tarifEkle(tarif, task -> {
                if (task.isSuccessful()) {
                    Log.d("Migration", "Tarif başarıyla eklendi: " + tarif.getAd());
                } else {
                    Log.e("Migration", "Hata: " + task.getException().getMessage());
                }
            });
        }
    }
}