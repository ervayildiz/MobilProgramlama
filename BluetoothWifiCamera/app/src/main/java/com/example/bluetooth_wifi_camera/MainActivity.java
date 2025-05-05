package com.example.bluetooth_wifi_camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth_wifi_camera.BluetoothActivity;
import com.example.bluetooth_wifi_camera.CameraActivity;
import com.example.bluetooth_wifi_camera.R;
import com.example.bluetooth_wifi_camera.WifiActivity;

public class MainActivity extends AppCompatActivity {

    Button buttonB, buttonW, buttonC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonB = findViewById(R.id.buttonB);
        buttonW = findViewById(R.id.buttonW);
        buttonC = findViewById(R.id.buttonC);

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });

        buttonW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifiActivity.class);
                startActivity(intent);
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }
}