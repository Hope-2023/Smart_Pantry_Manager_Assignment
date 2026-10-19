package com.example.smart_pantry_manager_assignment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch alertSwitch = findViewById(R.id.switchExpiryAlerts);
        Button saveBtn = findViewById(R.id.saveSettingsButton);

        saveBtn.setOnClickListener(v -> {
            boolean enabled = alertSwitch.isChecked();
            Toast.makeText(this, "Settings saved! Expiry alerts: " + (enabled ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}