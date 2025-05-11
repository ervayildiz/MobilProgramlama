package com.example.tarif.data;

import com.example.tarif.Tarif;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class TarifManager extends CollectionManager<Tarif> {

    @Override
    public CollectionReference getCollection() {
        return FirebaseFirestore.getInstance().collection("tarifler");
    }

    @Override
    public Tarif fromSnapshot(DocumentSnapshot doc) {
        return doc.toObject(Tarif.class);
    }

    @Override
    public void getAll(Callback<List<Tarif>> callback) {
        getCollection().get()
                .addOnSuccessListener(query -> {
                    List<Tarif> liste = new ArrayList<>();
                    for (DocumentSnapshot d : query.getDocuments()) {
                        liste.add(fromSnapshot(d));
                    }
                    callback.onSuccess(liste);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void save(Tarif tarif, Callback<Void> callback) {
        getCollection().add(tarif)
                .addOnSuccessListener(ref -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}
