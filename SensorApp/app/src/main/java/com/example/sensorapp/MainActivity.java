package com.example.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private TextView[] sensorTextViews = new TextView[9];
    private boolean isUpsideDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView'leri bağlama
        int[] textViewIds = {
                R.id.accelerometer_value, R.id.compass_value, R.id.gyroscope_value,
                R.id.humidity_value, R.id.light_value, R.id.magnetometer_value,
                R.id.pressure_value, R.id.proximity_value, R.id.temperature_value
        };

        for (int i = 0; i < textViewIds.length; i++) {
            sensorTextViews[i] = findViewById(textViewIds[i]);
            sensorTextViews[i].setText(getString(R.string.sensor_initializing));
        }

        // Sensör yöneticisini ve vibratörü başlatma
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mevcut sensörleri kaydet
        registerAvailableSensors();
    }

    private void registerAvailableSensors() {
        // Sensör tipleri ve indeksleri
        int[] sensorTypes = {
                Sensor.TYPE_ACCELEROMETER,       // 0
                Sensor.TYPE_MAGNETIC_FIELD,      // 1
                Sensor.TYPE_GYROSCOPE,           // 2
                Sensor.TYPE_RELATIVE_HUMIDITY,   // 3
                Sensor.TYPE_LIGHT,               // 4
                Sensor.TYPE_MAGNETIC_FIELD,      // 5 (Magnetometer)
                Sensor.TYPE_PRESSURE,            // 6
                Sensor.TYPE_PROXIMITY,           // 7
                Sensor.TYPE_AMBIENT_TEMPERATURE  // 8
        };

        for (int i = 0; i < sensorTypes.length; i++) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorTypes[i]);
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                sensorTextViews[i].setText(getString(R.string.sensor_not_available));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                updateSensorUI(0, event.values, "X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²");
                checkUpsideDown(event.values[2]);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                if (event.sensor.getName().contains("Magnetometer")) {
                    updateSensorUI(5, event.values, "X: %.2f μT\nY: %.2f μT\nZ: %.2f μT");
                } else {
                    updateSensorUI(1, event.values, "X: %.2f μT\nY: %.2f μT\nZ: %.2f μT");
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                updateSensorUI(2, event.values, "X: %.2f rad/s\nY: %.2f rad/s\nZ: %.2f rad/s");
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorTextViews[3].setText(String.format(Locale.getDefault(), "Nem: %.2f%%", event.values[0]));
                break;

            case Sensor.TYPE_LIGHT:
                sensorTextViews[4].setText(String.format(Locale.getDefault(), "Işık: %.2f lx", event.values[0]));
                break;

            case Sensor.TYPE_PRESSURE:
                sensorTextViews[6].setText(String.format(Locale.getDefault(), "Basınç: %.2f hPa", event.values[0]));
                break;

            case Sensor.TYPE_PROXIMITY:
                sensorTextViews[7].setText(String.format(Locale.getDefault(), "Yakınlık: %.2f cm", event.values[0]));
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorTextViews[8].setText(String.format(Locale.getDefault(), "Sıcaklık: %.2f °C", event.values[0]));
                break;
        }
    }

    private void checkUpsideDown(float zValue) {
        if (zValue < -5 && !isUpsideDown) {
            isUpsideDown = true;
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(500);
                } else {
                    vibrator.vibrate(500);
                }
            }
        } else if (zValue > 5 && isUpsideDown) {
            isUpsideDown = false;
        }
    }

    private void updateSensorUI(int index, float[] values, String format) {
        String text = String.format(Locale.getDefault(), format, values[0], values[1], values.length > 2 ? values[2] : 0);
        sensorTextViews[index].setText(text);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}