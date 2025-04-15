package com.example.tarif;

import android.content.Context;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class JsonReader {
    public static List<Tarif> readTariflerFromJson(Context context) {
        try {
            InputStream is = context.getAssets().open("tarifler.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Tarif>>(){}.getType();
            return gson.fromJson(sb.toString(), listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}