package com.example.tarif.data;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public abstract class CollectionManager<T> {
    public abstract CollectionReference getCollection();
    public abstract T fromSnapshot(DocumentSnapshot doc);
    public abstract void getAll(Callback<List<T>> callback);
    public abstract void save(T obj, Callback<Void> callback);
}
