package com.example.app.utils;

import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    
    public static String post(String url, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonBody
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}