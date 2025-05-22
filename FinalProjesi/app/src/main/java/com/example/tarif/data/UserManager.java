package com.example.tarif.data;

import com.example.tarif.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class UserManager extends CollectionManager<User> {

    @Override
    public CollectionReference getCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    @Override
    public User fromSnapshot(DocumentSnapshot doc) {
        return doc.toObject(User.class);
    }

    @Override
    public void getAll(Callback<List<User>> callback) {
        getCollection().get()
                .addOnSuccessListener(query -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot d : query.getDocuments()) {
                        users.add(fromSnapshot(d));
                    }
                    callback.onSuccess(users);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void save(User user, Callback<Void> callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getCollection().document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

}
