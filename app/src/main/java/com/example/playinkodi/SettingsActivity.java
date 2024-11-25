package com.example.playinkodi;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    public static String PREFS_NAME = "AppSettings";
    public static String KODI_API_URL = "KodiApiUrl";

    EditText editTextKodiApiUrl;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        editTextKodiApiUrl = findViewById(R.id.edit_text_kodi_api_url);
        buttonSave = findViewById(R.id.button_save);

        // Load saved value
        loadKodiApiUrl();

        // Save button listener
        buttonSave.setOnClickListener(v -> saveKodiApiUrl());
    }

    private void saveKodiApiUrl() {
        String kodiApiUrl = editTextKodiApiUrl.getText().toString();

        if (kodiApiUrl.isEmpty()) {
            Toast.makeText(this, "Api url cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save api url in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KODI_API_URL, kodiApiUrl);
        editor.apply();

        Toast.makeText(this, "Api url saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void loadKodiApiUrl() {
        // Load api url from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String kodiApiUrl = sharedPreferences.getString(KODI_API_URL, "");

        // Set the saved api url in the EditText
        if (!kodiApiUrl.isEmpty()) {
            editTextKodiApiUrl.setText(kodiApiUrl);
        }
    }
}