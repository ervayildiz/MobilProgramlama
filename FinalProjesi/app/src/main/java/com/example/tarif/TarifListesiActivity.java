// Path: FinalProjesi/app/src/main/java/com/example/tarif/TarifListesiActivity.java

package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tarif.TarifAdapter;
import com.example.tarif.data.Callback;
import com.example.tarif.data.TarifManager;
import com.example.tarif.Tarif;
import java.util.List;

public class TarifListesiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TarifAdapter adapter;
    private List<Tarif> tarifList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_listesi);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // tarifList doldurulmuş olmalı (intent ile gelen ya da önceki sorgudan)

        TarifManager manager = new TarifManager();
        adapter = new TarifAdapter(tarifList, tarif -> {
            Intent intent = new Intent(this, TarifDetayActivity.class);
            intent.putExtra("tarifId", tarif.getTarifId());
            startActivity(intent);
        }, (tarif, yeniDurum) -> {
            if (yeniDurum) {
                manager.addToFavorites(tarif, new Callback<Void>() {
                    @Override public void onSuccess(Void result) {}
                    @Override public void onFailure(Exception e) {}
                });
            } else {
                manager.removeFromFavorites(tarif, new Callback<Void>() {
                    @Override public void onSuccess(Void result) {}
                    @Override public void onFailure(Exception e) {}
                });
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
