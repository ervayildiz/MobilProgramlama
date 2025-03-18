package com.example.tarif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TarifDetayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_detay);

        TextView textViewIsim = findViewById(R.id.textViewIsim);
        TextView textViewMalzemeler = findViewById(R.id.textViewMalzemeler);
        TextView textViewYapilis = findViewById(R.id.textViewYapilis);

        // Intent'ten tarif bilgilerini al
        Intent intent = getIntent();
        if (intent != null) {
            Tarif tarif = (Tarif) intent.getSerializableExtra("tarif");
            if (tarif != null) {
                textViewIsim.setText(tarif.getIsim());
                textViewMalzemeler.setText(tarif.getMalzemeler());
                textViewYapilis.setText(tarif.getYapilis());
            }
        }
    }
}