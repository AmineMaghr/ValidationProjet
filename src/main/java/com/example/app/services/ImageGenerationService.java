package com.example.app.services;

import com.example.app.entities.Personnage;
import com.example.app.entities.Universe;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Image generation using Pollinations.ai — completely free, no API key required.
 * Just builds a URL and JavaFX loads it as a regular image.
 */
public class ImageGenerationService {

    private static final String BASE = "https://image.pollinations.ai/prompt/";

    public static String generateCharacterImageURL(Personnage p) {
        return generateCharacterImageURL(p.getName(), p.getClassRole());
    }

    public static String generateCharacterImageURL(String name, String classRole) {
        String prompt = "epic fantasy character portrait of " + name
            + ", " + (classRole != null ? classRole : "hero")
            + ", dark fantasy art, detailed, cinematic lighting, dramatic, high quality, solo, full face";
        return buildUrl(prompt, 512, 768);
    }

    /** Downloads image from a Pollinations URL and returns the raw bytes for DB storage */
    public static byte[] downloadImageBytes(String url) throws Exception {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.connect();
        try (java.io.InputStream is = conn.getInputStream()) {
            return is.readAllBytes();
        }
    }

    public static String generateUniverseImageURL(Universe u) {
        String prompt = "epic " + (u.getGenre() != null ? u.getGenre() : "fantasy")
            + " landscape, " + u.getName()
            + ", wide cinematic shot, detailed world, dramatic sky, high quality concept art";
        return buildUrl(prompt, 900, 400);
    }

    public static String generateBattleSceneURL(String fighter1, String fighter2) {
        String prompt = "two epic fantasy warriors in an intense battle, " + fighter1
            + " vs " + fighter2
            + ", dynamic action, sparks, magic, dramatic lighting, cinematic, dark fantasy";
        return buildUrl(prompt, 700, 400);
    }

    public static String generateWinnerPortraitURL(String name, String classRole) {
        String prompt = "triumphant " + classRole + " warrior " + name
            + " standing victorious, golden light, epic pose, detailed, fantasy art, winner, glory";
        return buildUrl(prompt, 512, 512);
    }

    private static String buildUrl(String prompt, int width, int height) {
        try {
            String encoded = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
            return BASE + encoded + "?width=" + width + "&height=" + height + "&nologo=true&seed=" + Math.abs(prompt.hashCode() % 9999);
        } catch (Exception e) {
            return "";
        }
    }
}
