package com.example.playinkodi;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Preferences");

        if (findViewById(R.id.preferences_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_fragment_container, new PreferencesFragment())
                .commit();

//            getFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.preferences_fragment_container, new PreferencesFragment())
//                    .commit();
        }
    }

}