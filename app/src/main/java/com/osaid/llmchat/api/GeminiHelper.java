package com.osaid.llmchat.api;

import com.osaid.llmchat.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiHelper {

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final OkHttpClient client;
    private final ExecutorService executor;

    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public GeminiHelper() {
        client = new OkHttpClient();
        executor = Executors.newSingleThreadExecutor();
    }

    public void sendMessage(String userMessage, GeminiCallback callback) {
        executor.execute(() -> {
            try {
                JSONObject part = new JSONObject();
                part.put("text", userMessage);

                JSONArray partsArray = new JSONArray();
                partsArray.put(part);

                JSONObject content = new JSONObject();
                content.put("parts", partsArray);

                JSONArray contentsArray = new JSONArray();
                contentsArray.put(content);

                JSONObject body = new JSONObject();
                body.put("contents", contentsArray);

                String url = API_URL + "?key=" + BuildConfig.GEMINI_API_KEY;

                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(body.toString(),
                                MediaType.parse("application/json")))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();

                    if (response.isSuccessful()) {
                        JSONObject json = new JSONObject(responseBody);
                        String text = json.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                        callback.onSuccess(text.trim());
                    } else {
                        callback.onError("API error: " + responseBody);
                    }
                }

            } catch (IOException | org.json.JSONException e) {
                callback.onError(e.getMessage());
            }
        });
    }
}