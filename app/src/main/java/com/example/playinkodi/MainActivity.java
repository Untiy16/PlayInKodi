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
import androidx.preference.PreferenceManager;
import com.example.playinkodi.api.KodiApiHelper;
import com.example.playinkodi.database.MyDatabaseHelper;

import androidx.appcompat.widget.PopupMenu;
import com.example.playinkodi.webview.MainActivityWebViewClient;

import java.lang.reflect.Array;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public EditText urlInput;
    public WebView webView;
    public ProgressBar progressBar;
    public View playKodiBtn;
    public View menuMainIcon;

    public PopupMenu popup;
    String playlist, subtitles, kodiApiUrl;
    int updatableBookmarkId = 0;
    SharedPreferences sharedPreferences;
    KodiApiHelper kodiApiHelper = new KodiApiHelper(MainActivity.this);
    public MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this);
    private static final String HOME_URL = "https://google.com";
    private static final String WEBVIEW_PREFS = "webViewPrefs";


    //log and toast examples
    //Toast.makeText(MainActivity.this, kodiApiUrl, Toast.LENGTH_SHORT).show();
    //Log.d("kodilog", playlist);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Map<String, ?> preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getAll();
                kodiApiUrl = String.valueOf(preferences.get("pref_apiUrl"));
                if (kodiApiUrl == "null" || kodiApiUrl.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Kodi api url is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String requestUrl = "http://" + kodiApiUrl + "/jsonrpc";

                kodiApiHelper.playlistRequest(requestUrl, playlist);
                kodiApiHelper.subtitlesRequest(requestUrl, subtitles);
            }
        });

        popup = new PopupMenu(this, menuMainIcon);
        popup.setForceShowIcon(true);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        //menu main listener
        menuMainIcon.setOnClickListener(v -> {
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.refresh) {
                        refreshPage();
                        return true;
                    } else if (id == R.id.home) {
                        loadMyUrl(HOME_URL);
                        return true;
                    }  else if (id == R.id.bookmarks) {
                        Intent intent = new Intent(getApplicationContext(), BookmarksActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (id == R.id.add_bookmark) {
                        dbHelper.addBookmark(urlInput.getText().toString());
                        return true;
                    } else if (id == R.id.remove_bookmark) {
                        dbHelper.addBookmark(urlInput.getText().toString());
                        return true;
                    } else if (id == R.id.update_bookmark) {
                        dbHelper.updateBookmark(updatableBookmarkId, webView.getUrl());
                        return true;
                    } else if (id == R.id.settings) {
                        Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        });


        //Check if we have a URL to load
        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            loadMyUrl(url);
        } else if (!getLastVisitedUrl().isEmpty()) {
            loadMyUrl(getLastVisitedUrl());
        } else {
            loadMyUrl(HOME_URL);
            //loadMyUrl("https://american-horror-story.net/238-subtitles/1-season/2-episode");
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
        String preparedUrl = "";
        if (matchUrl) {
            preparedUrl = url;
        } else {
            preparedUrl = "https://google.com/search?q=" + url;
        }
        webView.loadUrl(preparedUrl);
    }

    private void refreshPage() {
        webView.reload();
    }
    public String getPlaylist() { return playlist; }
    public void setPlaylist(String playlist) { this.playlist = playlist; }
    public void setSubtitles(String subtitles) { this.subtitles = subtitles;}
    public void setUpdatableBookmarkId(int updatableBookmarkId) {
        this.updatableBookmarkId = updatableBookmarkId;
    }

    public String getLastVisitedUrl() {
        SharedPreferences readHistory = this.getSharedPreferences(MainActivity.WEBVIEW_PREFS, MODE_PRIVATE);
        return readHistory.getString("lastVisitedUrl", "");
    }

    public void setLastVisitedUrl(String url) {
        SharedPreferences readHistory = this.getSharedPreferences(MainActivity.WEBVIEW_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = readHistory.edit();
        editor.putString("lastVisitedUrl", url);
        editor.apply();
    }
}