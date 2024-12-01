package com.example.playinkodi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.playinkodi.api.KodiApiHelper;
import com.example.playinkodi.database.MyDatabaseHelper;

import androidx.appcompat.widget.PopupMenu;
import com.example.playinkodi.webview.MainActivityWebViewClient;

public class MainActivity extends AppCompatActivity {

    public EditText urlInput;
    public WebView webView;
    public ProgressBar progressBar;
    public View playKodiBtn;
    public View menuMainIcon;
    String playlist, subtitles, kodiApiUrl;
    SharedPreferences sharedPreferences;
    KodiApiHelper kodiApiHelper = new KodiApiHelper(MainActivity.this);

    MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this);
    SQLiteDatabase db;


    //log and toast examples
    //Toast.makeText(MainActivity.this, kodiApiUrl, Toast.LENGTH_SHORT).show();
    //Log.d("kodilog", playlist);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //init controls
        urlInput = findViewById(R.id.url_input);
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_bar);
        playKodiBtn = findViewById(R.id.play_kodi_btn);
        menuMainIcon = findViewById(R.id.menu_main_icon);

        //config webView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webView.setWebViewClient(new MainActivityWebViewClient(this));
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
            }
        });

        //url input listener
        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
                    loadMyUrl(urlInput.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //play button listener
        playKodiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
                kodiApiUrl = sharedPreferences.getString(SettingsActivity.KODI_API_URL, "");
                if (kodiApiUrl.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Kodi api url is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String requestUrl = "http://" + kodiApiUrl + "/jsonrpc";

                kodiApiHelper.playlistRequest(requestUrl, playlist);
                kodiApiHelper.subtitlesRequest(requestUrl, subtitles);
            }
        });

        //menu main listener
        menuMainIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setForceShowIcon(true);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bookmarks) {
                        Intent intent = new Intent(getApplicationContext(), BookmarksActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (id == R.id.add_bookmark) {
                        dbHelper.addBookmark(urlInput.getText().toString());
                        return true;
                    } else if (id == R.id.settings) {
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        });

        //code
        //Check if we have a URL to load
        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            loadMyUrl(url);
        } else {
            loadMyUrl("https://google.com");
            //loadMyUrl("https://american-horror-story.net/238-subtitles/1-season/2-episode"); //"https://google.com"
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void loadMyUrl(String url) {
        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if (matchUrl) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("https://google.com/search?q=" + url);
        }
    }
    public String getPlaylist() { return playlist; }
    public void setPlaylist(String playlist) { this.playlist = playlist; }
    public void setSubtitles(String subtitles) { this.subtitles = subtitles;}
}