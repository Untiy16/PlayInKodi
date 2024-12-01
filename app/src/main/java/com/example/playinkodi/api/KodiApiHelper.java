package com.example.playinkodi.api;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class KodiApiHelper {
    private Context context;
    MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    JSONObject jsonParams;

    public KodiApiHelper(Context context) {
        this.context = context;
    }

    public void playlistRequest(String requestUrl, String playlist) {
        if (!playlist.isEmpty()) {
            try {
                //Log.d("kodilog", requestUrl);
                post(
                    requestUrl,
                    getKodiJson("Player.Open", playlist).toString()
                );
            } catch (Throwable e) {
                Toast.makeText(context, "Playlist request error!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void subtitlesRequest(String requestUrl, String subtitles) {
        if (!subtitles.isEmpty()) {
            try {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    try {
                        post(
                            requestUrl,
                            getKodiJson("Player.AddSubtitle", subtitles).toString()
                        );
                    } catch (IOException e) {
                        Toast.makeText(context, "Subtitles request error!", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }

                }, 5000);

            } catch (Throwable e) {
                Toast.makeText(context, "Subtitles request error!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
          .url(url)
          .post(body)
          .build();
        //try (Response response = client.newCall(request).execute()) {
        //    return response.body().string();
        //}

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

    public JSONObject getKodiJson(String method, String param) {
        try {
            jsonParams = new JSONObject();
            jsonParams.put("jsonrpc", "2.0");
            jsonParams.put("id", "1");
            jsonParams.put("method", method);
            if (method.equals("Player.AddSubtitle")) {
                jsonParams.put("params", new JSONObject().put("playerid", 1).put("subtitle", param));
            } else if (method.equals("Player.Open")) {
                jsonParams.put("params", new JSONObject().put("item", new JSONObject().put("file", param)));
            }
            return jsonParams;
        } catch (JSONException e) {
            Toast.makeText(this.context, "Json init fail", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }
}
