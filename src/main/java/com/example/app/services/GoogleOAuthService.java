package com.example.app.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.IOException;

public class GoogleOAuthService {
    
    private static final String CLIENT_ID = "865930201188-hdctotfkb4082qkr6rhepd0bjltm7ecn.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    
    private final OkHttpClient httpClient = new OkHttpClient();
    
    public String getAuthorizationUrl() {
        return AUTH_URL + "?" +
            "client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI +
            "&response_type=code" +
            "&scope=email%20profile" +
            "&access_type=offline";
    }
    
    public GoogleUserInfo exchangeCodeForUserInfo(String code) throws IOException {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }
private String getAccessToken(String code) throws IOException {
    // Décoder le code URL (convertir %2F en / etc.)
    String decodedCode = java.net.URLDecoder.decode(code, "UTF-8");
    System.out.println("🔑 Code original: " + code);
    System.out.println("🔑 Code décodé: " + decodedCode);
    
    RequestBody formBody = new FormBody.Builder()
        .add("code", decodedCode)
        .add("client_id", CLIENT_ID)
        .add("client_secret", CLIENT_SECRET)
        .add("redirect_uri", REDIRECT_URI)
        .add("grant_type", "authorization_code")
        .build();
    
    Request request = new Request.Builder()
        .url(TOKEN_URL)
        .post(formBody)
        .build();
    
    try (Response response = httpClient.newCall(request).execute()) {
        String responseBody = response.body().string();
        System.out.println("📥 Réponse code: " + response.code());
        System.out.println("📥 Réponse body: " + responseBody);
        
        if (!response.isSuccessful()) {
            throw new IOException("Erreur token: " + response.code() + " - " + responseBody);
        }
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        return json.get("access_token").getAsString();
    }
}
    private GoogleUserInfo getUserInfo(String accessToken) throws IOException {
        Request request = new Request.Builder()
            .url(USERINFO_URL)
            .header("Authorization", "Bearer " + accessToken)
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur userinfo: " + response.code());
            }
            String jsonResponse = response.body().string();
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            
            GoogleUserInfo userInfo = new GoogleUserInfo();
            userInfo.setId(json.get("sub").getAsString());
            userInfo.setEmail(json.get("email").getAsString());
            userInfo.setName(json.has("name") ? json.get("name").getAsString() : "");
            userInfo.setGivenName(json.has("given_name") ? json.get("given_name").getAsString() : "");
            userInfo.setFamilyName(json.has("family_name") ? json.get("family_name").getAsString() : "");
            userInfo.setPicture(json.has("picture") ? json.get("picture").getAsString() : "");
            userInfo.setEmailVerified(json.has("email_verified") && json.get("email_verified").getAsBoolean());
            
            return userInfo;
        }
    }
    
    public static class GoogleUserInfo {
        private String id;
        private String email;
        private String name;
        private String givenName;
        private String familyName;
        private String picture;
        private boolean emailVerified;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGivenName() { return givenName; }
        public void setGivenName(String givenName) { this.givenName = givenName; }
        public String getFamilyName() { return familyName; }
        public void setFamilyName(String familyName) { this.familyName = familyName; }
        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    }
}