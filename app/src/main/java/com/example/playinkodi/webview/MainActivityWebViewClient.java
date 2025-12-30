package com.example.playinkodi.webview;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.preference.PreferenceManager;
import com.example.playinkodi.MainActivity;
import com.example.playinkodi.R;

import java.net.URI;
import java.util.Map;

public class MainActivityWebViewClient extends WebViewClient {
    MainActivity context;
    public MainActivityWebViewClient(MainActivity context) {
        this.context = context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        context.setRefreshing(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean parseDesktopUserAgentState = prefs.getBoolean("pref_desktopUserAgent", false);
        if (parseDesktopUserAgentState) {
            view.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");
        } else {
            view.getSettings().setUserAgentString(null);
        }

        context.urlInput.setText(context.webView.getUrl());
        context.progressBar.setVisibility(View.VISIBLE);
        context.playKodiBtnWrapper.setVisibility(View.INVISIBLE);
        context.setPlaylist("");
        context.setSubtitles("");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        context.setRefreshing(false);

        context.progressBar.setVisibility(View.INVISIBLE);
        if (!this.context.getPlaylist().isEmpty()) {
            context.playKodiBtnWrapper.setVisibility(View.VISIBLE);
        }

        MenuItem updateBookmark = context.popup.getMenu().findItem(R.id.update_bookmark);
        updateBookmark.setVisible(false);
        if (context.dbHelper.isBookmarkExist(context.webView.getUrl())) {
            context.popup.getMenu().findItem(R.id.add_bookmark).setVisible(false);
            context.popup.getMenu().findItem(R.id.remove_bookmark).setVisible(true);
        } else {
            context.popup.getMenu().findItem(R.id.add_bookmark).setVisible(true);
            context.popup.getMenu().findItem(R.id.remove_bookmark).setVisible(false);
            int updatableBookmarkId = context.dbHelper.getUpdatableBookmark(Uri.parse(url).getHost());
            if (updatableBookmarkId != 0) {
                updateBookmark.setVisible(true);
                context.setUpdatableBookmarkId(updatableBookmarkId);
            }

        }

        context.setLastVisitedUrl(context.webView.getUrl());



        Log.d("kodilog", Uri.parse(url).getHost());
        Log.d("kodilog", String.valueOf(context.dbHelper.getUpdatableBookmark(Uri.parse(url).getHost())));
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean parseDirectVideoUrl = prefs.getBoolean("pref_parseDirectVideoUrl", true);

        String urlExt = getUrlExtension(url);

        if (
            urlExt.equals(".m3u")
            || urlExt.equals(".m3u8")
            || (
                parseDirectVideoUrl
                && (
                    urlExt.equals(".mp4")
                    || urlExt.equals(".mov")
                    || urlExt.equals(".avi")
                    || urlExt.equals(".wmv")
                    || urlExt.equals(".mkv")
                    || urlExt.equals(".webm")
                    || urlExt.equals(".flv")
                )
            )
        ) {
            this.context.setPlaylist(url);
            context.playKodiBtnWrapper.setVisibility(View.VISIBLE);
            context.playKodiBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2feba6")));
        }

        if (urlExt.equals(".vtt") || urlExt.equals(".srt") || urlExt.equals(".ass")) {
            this.context.setSubtitles(url);
        }

        super.onLoadResource(view, url);
    }

    public static String getUrlExtension(String urlString) {
        try {
            URI url = new URI(urlString);
            String path = url.getPath();
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex != -1) {
                return "." + path.substring(dotIndex + 1).toLowerCase();
            }
        } catch (Exception e) {
            // malformed URL, just return empty
        }
        return "";
    }
}