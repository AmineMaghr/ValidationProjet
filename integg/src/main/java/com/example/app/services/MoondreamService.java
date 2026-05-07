package com.example.app.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class MoondreamService {

    private static final String OLLAMA_URL = "http://127.0.0.1:11434/api/chat";
    private static final String MODEL = "llava:7b"; // Change en "moondream" si tu veux tester
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String genererDescriptionArtistique(String imagePath) {
        System.out.println("🎨 Génération description pour : " + imagePath);

        try {
            Path path = Path.of(imagePath);
            if (!Files.exists(path)) {
                return "Erreur : L'image n'existe pas sur le chemin spécifié.";
            }

            byte[] imageBytes = Files.readAllBytes(path);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Prompt en une seule ligne pour éviter les problèmes de JSON
            String prompt = "RÉPONDS UNIQUEMENT EN FRANÇAIS. TOUTE TA RÉPONSE DOIT ÊTRE EN FRANÇAIS. AUCUN MOT EN ANGLAIS SAUF NOMS PROPRES. COMMENCE DIRECTEMENT PAR LA DESCRIPTION. donne-moi une description très détaillée de cette photo en français. Décris le sujet principal, les couleurs dominantes, les détails visuels, l'ambiance, l'atmosphère générale et les éléments marquants. Sois descriptif, créatif et naturel.";

            String jsonRequest = String.format(
                    "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\",\"images\":[\"%s\"]}],\"stream\":false,\"options\":{\"temperature\":0.7,\"num_predict\":700}}",
                    MODEL,
                    prompt.replace("\"", "\\\""),
                    base64Image);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📡 HTTP Response Code: " + response.statusCode());

            if (response.statusCode() != 200) {
                System.err.println("❌ Réponse Ollama : " + response.body());
                return getFallbackDescription();
            }

            String jsonResponse = response.body();

            // Extraction propre de la réponse
            int start = jsonResponse.indexOf("\"content\":\"");
            if (start == -1) {
                return getFallbackDescription();
            }

            start += 11;
            StringBuilder result = new StringBuilder();
            boolean escaping = false;

            for (int i = start; i < jsonResponse.length(); i++) {
                char c = jsonResponse.charAt(i);
                if (escaping) {
                    result.append(c);
                    escaping = false;
                    continue;
                }
                if (c == '\\') {
                    escaping = true;
                    result.append(c);
                    continue;
                }
                if (c == '"')
                    break;
                result.append(c);
            }

            String description = result.toString()
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                    .trim();

            System.out.println("✅ Description générée (" + description.length() + " caractères)");
            return description;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération : " + e.getMessage());
            e.printStackTrace();
            return getFallbackDescription();
        }
    }

    private String getFallbackDescription() {
        return "Cette image montre une scène visuellement intéressante avec de beaux éléments et une composition harmonieuse.";
    }

    public boolean isMoondreamAvailable() {
        try {
            URI uri = new URI("http://127.0.0.1:11434/api/tags");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public String decrireImage(String imagePath) {
        return genererDescriptionArtistique(imagePath);
    }
}