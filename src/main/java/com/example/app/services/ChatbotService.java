package com.example.app.services;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;

public class ChatbotService {
    private static final String MODEL = "gemini-2.5-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
        + MODEL + ":generateContent?key=";

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();
    private static final Gson gson = new Gson();

    private static String apiKey = loadApiKey();

    private static String loadApiKey() {
        // 1. Environment variable
        String key = System.getenv("GOOGLE_API_KEY");
        if (key != null && !key.isBlank()) return key.trim();

        // 2. src/main/resources/anthropic.key file
        try {
            java.io.InputStream is = ChatbotService.class.getResourceAsStream("/anthropic.key");
            if (is != null) {
                String content = new String(is.readAllBytes());
                for (String line : content.split("\\r?\\n")) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        return line;
                    }
                }
            }
        } catch (Exception ignored) {}

        return "";
    }

    public static void setAPIKey(String key) {
        apiKey = key;
    }

    public static boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public static String askAboutCharacter(String characterName, String characterDescription, String question) {
        String prompt = "You are " + characterName + ", a fictional character. Background: " + characterDescription +
            ". Stay in character. Answer concisely in 2-4 sentences.\n\nQuestion: " + question;
        return callGemini(prompt, characterName);
    }

    public static String askAboutUniverse(String universeName, String universeContext, String question) {
        String prompt = "You are an expert lorekeeper for the fictional universe '" + universeName + "'. " +
            "Context: " + universeContext + ". Answer concisely in 2-4 sentences.\n\nQuestion: " + question;
        return callGemini(prompt, universeName);
    }

    public static String narrateFullTournament(
            java.util.List<String> participants,
            java.util.List<String> eliminationOrder,
            java.util.List<String> roundEvents,
            String winner) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Tu es le narrateur d'un tournoi mortel style Culling Games / Hunger Games. ")
              .append("Participants: ").append(String.join(", ", participants)).append(".\n")
              .append("Ordre d'élimination (premier éliminé au dernier): ")
              .append(String.join(" → ", eliminationOrder)).append(".\n")
              .append("Événements aléatoires par round: ").append(String.join(" | ", roundEvents)).append(".\n")
              .append("Gagnant final: ").append(winner).append(".\n")
              .append("Narre ce tournoi round par round de façon épique et brutale. ")
              .append("Pour chaque round, montre comment les personnages ont été éliminés et l'impact des événements. ")
              .append("Termine avec une proclamation de victoire pour ").append(winner).append(". ")
              .append("Utilise des séparateurs comme '⚔️ ROUND 1 ⚔️'. Réponds en français. Sois dramatique et intense!");
        return callGemini(prompt.toString(), "Tournament");
    }

    public static String enhanceCharacterHistory(String name, String classRole, String existingText) {
        return callGemini(
            "Tu es un écrivain créatif expert en fantasy. Améliore et enrichis le texte suivant pour le personnage "
            + name + " (" + classRole + "). Garde le sens général mais rends-le plus épique, dramatique et immersif. "
            + "Réponds uniquement avec le texte amélioré, sans commentaires. En français.\n\nTexte:\n" + existingText,
            name
        );
    }

    public static String enhanceCharacterAbilities(String name, String classRole, String existingText) {
        return callGemini(
            "Tu es un game designer expert en fantasy. Améliore et enrichis les capacités suivantes pour le personnage "
            + name + " (" + classRole + "). Rends chaque capacité plus unique, puissante et cinématique. "
            + "Garde le format original. Réponds uniquement avec le texte amélioré, en français.\n\nCapacités:\n" + existingText,
            name
        );
    }

    public static String enhanceUniverseDescription(String name, String genre, String existingText) {
        return callGemini(
            "Tu es un auteur expert en world-building " + genre + ". Améliore la description suivante de l'univers '"
            + name + "'. Rends-la plus captivante et mystérieuse pour attirer le lecteur. "
            + "Réponds uniquement avec le texte amélioré, en français.\n\nDescription:\n" + existingText,
            name
        );
    }

    public static String enhanceUniverseStory(String name, String genre, String existingText) {
        return callGemini(
            "Tu es un auteur expert en world-building " + genre + ". Améliore le contexte narratif suivant de l'univers '"
            + name + "'. Enrichis la lore, les factions et l'histoire. Garde la même longueur approximative. "
            + "Réponds uniquement avec le texte amélioré, en français.\n\nContexte:\n" + existingText,
            name
        );
    }

    public static String generateCharacterHistory(String name, String classRole, String universeName) {
        return callGemini(
            "Write a compelling backstory in 3-4 sentences for a fictional " + classRole +
            " character named " + name + " in the " + universeName + " universe. Be epic and dramatic. Answer in the same language as the universe name.",
            name
        );
    }

    public static String generateCharacterAbilities(String name, String classRole) {
        return callGemini(
            "List exactly 4 unique abilities for a fictional " + classRole + " named " + name +
            ". Format each as: [Ability Name]: [one sentence description]. Be creative and genre-appropriate. Answer in French.",
            name
        );
    }

    public static String generateUniverseDescription(String name, String genre) {
        return callGemini(
            "Write a captivating 2-3 sentence short description for a fictional " + genre +
            " universe called '" + name + "'. Make it sound epic and draw the reader in. Answer in French.",
            name
        );
    }

    public static String generateUniverseStory(String name, String genre, String shortDesc) {
        return callGemini(
            "Write a rich narrative context (4-5 sentences) for a fictional " + genre +
            " universe called '" + name + "'. Short description: " + shortDesc +
            ". Expand on history, lore, factions, and world-building. Answer in French.",
            name
        );
    }

    public static String suggestCharacterName(String universeTheme) {
        return callGemini("Give 5 creative character name suggestions for a " + universeTheme +
            " universe, comma-separated, names only.", "Generator");
    }

    public static String suggestUniverseName(String genre) {
        return callGemini("Give 5 creative universe names for the " + genre +
            " genre, comma-separated, names only.", "Generator");
    }

    private static String callGemini(String prompt, String fallbackName) {
        if (!isConfigured()) {
            return getFallbackResponse(fallbackName, prompt);
        }

        try {
            // Build Gemini request body
            JsonObject part = new JsonObject();
            part.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(part);

            JsonObject content = new JsonObject();
            content.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contents);

            // Safety + generation config
            JsonObject genConfig = new JsonObject();
            genConfig.addProperty("maxOutputTokens", 512);
            genConfig.addProperty("temperature", 0.8);
            requestBody.add("generationConfig", genConfig);

            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(BASE_URL + apiKey)
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() == null) return "Pas de réponse reçue.";
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    System.err.println("Gemini API error " + response.code() + ": " + responseBody);
                    return "Erreur API (" + response.code() + ") — vérifiez votre clé Google.";
                }
                return parseGeminiResponse(responseBody);
            }
        } catch (IOException e) {
            System.err.println("Chatbot network error: " + e.getMessage());
            return "Connexion impossible — vérifiez votre connexion internet.";
        }
    }

    private static String parseGeminiResponse(String json) {
        try {
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            return obj.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString().trim();
        } catch (Exception e) {
            System.err.println("Gemini parse error: " + e.getMessage());
            return "Réponse inattendue du serveur.";
        }
    }

    private static String getFallbackResponse(String name, String question) {
        String[] responses = {
            "Dans l'univers de " + name + ", cette question mérite une réflexion profonde...",
            name + " répondrait : les mystères de ce monde ne se révèlent qu'à ceux qui cherchent vraiment.",
            "L'histoire de " + name + " est complexe — chaque réponse ouvre de nouvelles questions.",
            "Configurez une clé Google API pour activer le chatbot IA."
        };
        return responses[(int) (Math.random() * responses.length)];
    }
}
