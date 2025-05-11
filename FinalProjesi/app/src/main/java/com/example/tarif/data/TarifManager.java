// Path: FinalProjesi/app/src/main/java/com/example/tarif/data/TarifManager.java

package com.example.tarif.data;

import androidx.annotation.NonNull;
import com.example.tarif.Tarif;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TarifManager {

    public void addToFavorites(Tarif tarif, Callback<Void> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("Kullanıcı giriş yapmamış."));
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("tarifId", tarif.getTarifId());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .document(tarif.getTarifId())
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void removeFromFavorites(Tarif tarif, Callback<Void> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("Kullanıcı giriş yapmamış."));
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .document(tarif.getTarifId())
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void getAll(Callback<List<Tarif>> callback) {
        FirebaseDatabase.getInstance().getReference("tarifler")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Tarif> tarifList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Tarif tarif = data.getValue(Tarif.class);
                            tarif.setTarifId(data.getKey());
                            tarifList.add(tarif);
                        }
                        callback.onSuccess(tarifList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.toException());
                    }
                });
    }

    public void getFavorites(Callback<List<Tarif>> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("Kullanıcı giriş yapmamış."));
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> favoriIdler = new ArrayList<>();
                    for (var doc : snapshot) {
                        favoriIdler.add(doc.getString("tarifId"));
                    }

                    FirebaseDatabase.getInstance().getReference("tarifler")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<Tarif> sonuc = new ArrayList<>();
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Tarif tarif = data.getValue(Tarif.class);
                                        if (favoriIdler.contains(data.getKey())) {
                                            tarif.setTarifId(data.getKey());
                                            sonuc.add(tarif);
                                        }
                                    }
                                    callback.onSuccess(sonuc);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    callback.onFailure(error.toException());
                                }
                            });
                })
                .addOnFailureListener(callback::onFailure);
    }
}
