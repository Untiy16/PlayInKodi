package com.example.playinkodi.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.playinkodi.MainActivity;
import com.example.playinkodi.R;

public class MainActivityWebViewClient extends WebViewClient {
    MainActivity context;
    public MainActivityWebViewClient(MainActivity context) {
        this.context = context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        context.urlInput.setText(context.webView.getUrl());
        context.progressBar.setVisibility(View.VISIBLE);
        context.playKodiBtnWrapper.setVisibility(View.INVISIBLE);
        context.setPlaylist("");
        context.setSubtitles("");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
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
        if (url.contains(".m3u")) {
            this.context.setPlaylist(url);
        }

        if (url.contains(".vtt") || url.contains(".srt") || url.contains(".ass")) {
            this.context.setSubtitles(url);
        }

        super.onLoadResource(view, url);
    }
}