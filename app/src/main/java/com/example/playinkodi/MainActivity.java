package com.example.playinkodi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    EditText urlInput;
    WebView webView;
    ProgressBar progressBar;
    ImageView settings;
    View playKodiBtn;

    String playlist, subtitles, kodiApiUrl;
    SharedPreferences sharedPreferences;

    MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    JSONObject jsonParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        urlInput = findViewById(R.id.url_input);
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_bar);
        settings = findViewById(R.id.settings_icon);
        playKodiBtn = findViewById(R.id.play_kodi_btn);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

//        loadMyUrl("https://google.com");
        loadMyUrl("https://american-horror-story.net/238-subtitles/1-season/2-episode");

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

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        playKodiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
                kodiApiUrl = sharedPreferences.getString(SettingsActivity.KODI_API_URL, "");
//                Toast.makeText(MainActivity.this, kodiApiUrl, Toast.LENGTH_SHORT).show();
//                Log.d("kodilog", playlist);
//                Log.d("kodilog", subtitles);
                if (kodiApiUrl.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Kodi api url is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String requestUrl = "http://" + kodiApiUrl + "/jsonrpc";

                if (!playlist.isEmpty()) {
                    try {
                        jsonParams = new JSONObject();
                        jsonParams.put("jsonrpc", "2.0");
                        jsonParams.put("id", "1");
                        jsonParams.put("method", "Player.Open");
                        jsonParams.put("params", new JSONObject().put("item", new JSONObject().put("file", playlist)));
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Playlist JSON creation error!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        post(requestUrl, jsonParams.toString());
                    } catch (Throwable e) {
                        Toast.makeText(MainActivity.this, "Playlist request error!", Toast.LENGTH_SHORT).show();
//                        Log.d("kodilog", requestUrl);
//                        Log.d("kodilog", jsonParams.toString());
//                        Log.e("kodilog", e.getMessage(), e);
                        return;
                    }
                }

                if (!subtitles.isEmpty()) {
                    try {
                        jsonParams = new JSONObject();
                        jsonParams.put("jsonrpc", "2.0");
                        jsonParams.put("id", "1");
                        jsonParams.put("method", "Player.AddSubtitle");
                        jsonParams.put("params", new JSONObject().put("playerid", 1).put("subtitle", subtitles));
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Subtitles JSON creation error!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            try {
                                post(requestUrl, jsonParams.toString());
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this, "Subtitles request error!", Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }

                        }, 5000);

                    } catch (Throwable e) {
                        Toast.makeText(MainActivity.this, "Subtitles request error!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });


    }

    void loadMyUrl(String url) {
        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if (matchUrl) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("https://google.com/search?q=" + url);
        }
    }

    String post(String url, String json) throws IOException {
      RequestBody body = RequestBody.create(json, JSON);
      Request request = new Request.Builder()
          .url(url)
          .post(body)
          .build();
//      try (Response response = client.newCall(request).execute()) {
//        return response.body().string();
//      }

        client.newCall(request).enqueue(new Callback() {
          @Override public void onFailure(Call call, IOException e) {
            e.printStackTrace();
          }

          @Override public void onResponse(Call call, Response response) throws IOException {
            try (ResponseBody responseBody = response.body()) {
              if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

              Headers responseHeaders = response.headers();
              for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
              }

              System.out.println(responseBody.string());
            }
          }
        });

        return "";
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            urlInput.setText(webView.getUrl());
            progressBar.setVisibility(View.VISIBLE);
            playKodiBtn.setVisibility(View.INVISIBLE);
            playlist = subtitles = "";
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
            if (!playlist.isEmpty()) {
                playKodiBtn.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.contains(".m3u")) {
                playlist = url;
            }

            if (url.contains(".vtt") || url.contains(".srt") || url.contains(".ass")) {
                subtitles = url;
            }

            super.onLoadResource(view, url);
        }


    }
}