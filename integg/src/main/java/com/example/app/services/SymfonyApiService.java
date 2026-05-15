package com.example.app.services;

import com.example.app.entities.Oeuvre;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SymfonyApiService {
    private static final String SYMFONY_URL = "http://127.0.0.1:8000";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    /**
     * Récupère toutes les œuvres depuis Symfony
     */
    public Future<List<Oeuvre>> fetchOeuvres() {
        Callable<List<Oeuvre>> task = () -> {
            String apiUrl = SYMFONY_URL + "/api/oeuvres";
            String json = httpGet(apiUrl);
            return objectMapper.readValue(json, new TypeReference<List<Oeuvre>>() {});
        };
        return executor.submit(task);
    }
    
    /**
     * Crée une œuvre via l'API Symfony
     */
    public Future<Oeuvre> createOeuvre(Oeuvre oeuvre, byte[] imageBytes) {
        Callable<Oeuvre> task = () -> {
            String apiUrl = SYMFONY_URL + "/api/oeuvres/create";
            String boundary = "---" + System.currentTimeMillis();
            String body = buildMultipartBody(oeuvre, imageBytes, boundary);
            
            HttpURLConnection conn = (HttpURLConnection) URI.create(apiUrl).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                String response = readResponse(conn);
                return objectMapper.readValue(response, Oeuvre.class);
            }
            throw new RuntimeException("Erreur: " + responseCode);
        };
        return executor.submit(task);
    }
    
    private String buildMultipartBody(Oeuvre oeuvre, byte[] imageBytes, String boundary) {
        StringBuilder body = new StringBuilder();
        
        addField(body, boundary, "title", oeuvre.getTitle());
        addField(body, boundary, "type", oeuvre.getType());
        addField(body, boundary, "description", oeuvre.getDescription());
        addField(body, boundary, "author", oeuvre.getAuthor());
        
        if (imageBytes != null && imageBytes.length > 0) {
            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n");
            body.append("Content-Type: image/jpeg\r\n\r\n");
            body.append(Base64.getEncoder().encodeToString(imageBytes)).append("\r\n");
        }
        
        body.append("--").append(boundary).append("--\r\n");
        return body.toString();
    }
    
    private void addField(StringBuilder body, String boundary, String name, String value) {
        if (value != null && !value.isEmpty()) {
            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"").append(name).append("\"\r\n\r\n");
            body.append(value).append("\r\n");
        }
    }
    
    private String httpGet(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return readResponse(conn);
    }
    
    private String readResponse(HttpURLConnection conn) throws Exception {
        try (InputStream in = conn.getInputStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }
    }
}