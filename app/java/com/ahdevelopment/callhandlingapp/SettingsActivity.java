package com.ahdevelopment.callhandlingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private EditText businessKeywordsEditText;
    private Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessKeywordsEditText = findViewById(R.id.businessKeywordsEditText);
        languageSpinner = findViewById(R.id.languageSpinner);

        businessKeywordsEditText.setText(preferences.getString("business_keywords", ""));
        languageSpinner.setSelection(preferences.getString("language", "en").equals("da") ? 1 : 0);

        findViewById(R.id.saveSettingsButton).setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        preferences.edit()
                .putString("business_keywords", businessKeywordsEditText.getText().toString())
                .putString("language", languageSpinner.getSelectedItem().toString())
                .apply();
        finish();
    }
}
