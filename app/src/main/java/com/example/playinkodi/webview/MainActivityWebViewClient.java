package com.example.playinkodi.webview;

import android.graphics.Bitmap;
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
        context.playKodiBtn.setVisibility(View.INVISIBLE);
        context.setPlaylist("");
        context.setSubtitles("");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        context.progressBar.setVisibility(View.INVISIBLE);
        if (!this.context.getPlaylist().isEmpty()) {
            context.playKodiBtn.setVisibility(View.VISIBLE);
        }

        if (context.dbHelper.isBookmarkExist(context.webView.getUrl())) {
            context.popup.getMenu().findItem(R.id.add_bookmark).setVisible(false);
            context.popup.getMenu().findItem(R.id.remove_bookmark).setVisible(true);
        } else {
            context.popup.getMenu().findItem(R.id.add_bookmark).setVisible(true);
            context.popup.getMenu().findItem(R.id.remove_bookmark).setVisible(false);
        }

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