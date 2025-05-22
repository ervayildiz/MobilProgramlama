package com.example.tarif;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.tarif.models.Tarif;

public class RecipeRepository {
    private static final String COLLECTION_NAME = "tarifler";

    public interface DataLoadListener {
        void onDataLoaded(List<Tarif> tarifList);

        void onError(Exception e);
    }

    public static void getRecipesFromSource(Context context, @NonNull DataLoadListener listener) {
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Tarif> tarifList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tarif tarif = document.toObject(Tarif.class);
                            tarif.setTarifId(document.getId()); // Firestore ID'sini kaydet
                            tarifList.add(tarif);
                        }
                        listener.onDataLoaded(tarifList);
                    } else {
                        // Firebase'den veri alınamazsa JSON'a dön
                        try {
                            List<Tarif> jsonTarifler = JsonReader.readTariflerFromJson(context);
                            listener.onDataLoaded(jsonTarifler);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }
                });
    }

    public static void getRecipesByCategory(Context context, String category, @NonNull DataLoadListener listener) {
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .whereEqualTo("kategori", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Tarif> filteredList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tarif tarif = document.toObject(Tarif.class);
                            tarif.setTarifId(document.getId());
                            filteredList.add(tarif);
                        }
                        listener.onDataLoaded(filteredList);
                    } else {
                        // Fallback to JSON filtering
                        try {
                            List<Tarif> allRecipes = JsonReader.readTariflerFromJson(context);
                            List<Tarif> filteredList = new ArrayList<>();
                            for (Tarif tarif : allRecipes) {
                                if (category.equals(tarif.getKategori())) {
                                    filteredList.add(tarif);
                                }
                            }
                            listener.onDataLoaded(filteredList);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }
                });
    }

    public static void getSavedRecipes(String userId, DataLoadListener listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("savedRecipes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Tarif> savedRecipes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tarif tarif = document.toObject(Tarif.class);
                            tarif.setTarifId(document.getId());
                            savedRecipes.add(tarif);
                        }
                        listener.onDataLoaded(savedRecipes);
                    } else {
                        listener.onError(task.getException() != null ?
                                task.getException() :
                                new Exception("Bilinmeyen hata"));
                    }
                });
    }
}