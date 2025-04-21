// App.java
package com.example.tarif;

import android.app.Application;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Firestore çevrimdışı desteği ve ayarları
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }
}