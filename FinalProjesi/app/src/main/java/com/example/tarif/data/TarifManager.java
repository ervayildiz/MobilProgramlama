// Path: FinalProjesi/app/src/main/java/com/example/tarif/data/TarifManager.java

package com.example.tarif.data;

import com.example.tarif.models.Tarif;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TarifManager extends CollectionManager<Tarif> {

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public CollectionReference getCollection() {
        return firestore.collection("tarifler");
    }

    @Override
    public Tarif fromSnapshot(DocumentSnapshot doc) {
        Tarif tarif = doc.toObject(Tarif.class);
        if (tarif != null) tarif.setTarifId(doc.getId());
        return tarif;
    }

    @Override
    public void getAll(Callback<List<Tarif>> callback) {
        getCollection().get()
                .addOnSuccessListener(snapshot -> {
                    List<Tarif> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        list.add(fromSnapshot(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void save(Tarif tarif, Callback<Void> callback) {
        getCollection().document(tarif.getTarifId())
                .set(tarif)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void addToFavorites(Tarif tarif, Callback<Void> callback) {
        if (user == null) {
            callback.onFailure(new Exception("Kullanıcı oturumu açık değil."));
            return;
        }
        firestore.collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .document(tarif.getTarifId())
                .set(tarif)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void removeFromFavorites(Tarif tarif, Callback<Void> callback) {
        if (user == null) {
            callback.onFailure(new Exception("Kullanıcı oturumu açık değil."));
            return;
        }
        firestore.collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .document(tarif.getTarifId())
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void getFavorites(Callback<List<Tarif>> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("Kullanıcı oturumu yok"));
            return;
        }

        firestore.collection("users")
                .document(user.getUid())
                .collection("favoriler")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    List<Tarif> favoriList = new ArrayList<>();
                    List<String> tarifIdList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        String tarifId = doc.getString("tarifId");
                        if (tarifId != null) {
                            tarifIdList.add(tarifId);
                        }
                    }

                    if (tarifIdList.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    final int[] counter = {0};
                    for (String id : tarifIdList) {
                        firestore.collection("tarifler")
                                .document(id)
                                .get()
                                .addOnSuccessListener(tarifDoc -> {
                                    if (tarifDoc.exists()) {
                                        Tarif tarif = tarifDoc.toObject(Tarif.class);
                                        if (tarif != null) {
                                            tarif.setTarifId(id); // kritik!
                                            favoriList.add(tarif);
                                        }
                                    }
                                    counter[0]++;
                                    if (counter[0] == tarifIdList.size()) {
                                        callback.onSuccess(favoriList);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    counter[0]++;
                                    if (counter[0] == tarifIdList.size()) {
                                        callback.onSuccess(favoriList);
                                    }
                                });
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

}
