package com.example.app.services;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Auto-generate character portraits using Dicebear API
 * Creates unique avatars based on character names
 */
public class PortraitGenerationService {
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final String DICEBEAR_BASE_URL = "https://api.dicebear.com/7.x/adventurer-neutral/svg";

    /**
     * Generate portrait URL for a character name
     */
    public static String generatePortraitURL(String characterName) {
        return DICEBEAR_BASE_URL + "?seed=" +
               characterName.replaceAll("\\s+", "_") +
               "&scale=80&backgroundColor=random";
    }

    /**
     * Load and display portrait in ImageView (async)
     */
    public static void loadPortraitToImageView(String characterName, ImageView imageView) {
        new Thread(() -> {
            try {
                String imageUrl = generatePortraitURL(characterName);
                Image image = new Image(imageUrl, true);
                javafx.application.Platform.runLater(() -> {
                    imageView.setImage(image);
                });
            } catch (Exception e) {
                System.err.println("Failed to load portrait: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Get portrait as byte array
     */
    public static byte[] getPortraitBytes(String characterName) throws Exception {
        String url = generatePortraitURL(characterName);
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().bytes();
        }
    }
}
