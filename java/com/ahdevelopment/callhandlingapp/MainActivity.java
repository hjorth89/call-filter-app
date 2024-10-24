package com.ahdevelopment.callhandlingapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean hasSubscription = false;
    private Switch aiToggleSwitch;
    private CallReceiver callReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aiToggleSwitch = findViewById(R.id.aiToggleSwitch);
        aiToggleSwitch.setEnabled(false); // Initially disable it until we verify the subscription

        // Button to view call history
        Button callHistoryButton = findViewById(R.id.callHistoryButton);
        callHistoryButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CallHistoryActivity.class));
        });

        // Button to open settings
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        // Toggle AI Call Handling
        aiToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (hasSubscription) {
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "AI Call Handling Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "AI Call Handling Disabled", Toast.LENGTH_SHORT).show();
                }
            } else {
                aiToggleSwitch.setChecked(false);
                Toast.makeText(MainActivity.this, "Please subscribe to use this feature", Toast.LENGTH_LONG).show();
            }
        });

        // Check and request runtime permissions
        checkAndRequestPermissions();
    }

    // Check and request necessary permissions for reading phone state
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, register the receiver
            registerCallReceiver();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, register the receiver
                registerCallReceiver();
            } else {
                Toast.makeText(this, "Permission denied to read phone state", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Register the CallReceiver to listen for incoming calls
    private void registerCallReceiver() {
        callReceiver = new CallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callReceiver, filter);
    }

    // Unregister the receiver when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
        }
    }
}
