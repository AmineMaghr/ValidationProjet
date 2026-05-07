package com.example.app.services;

import com.example.app.utils.EnvLoader;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.Map;

public class ChatbotService {
    // ── Gemini (primary) ──────────────────────────────────────────────────────
    private static final String GEMINI_MODEL = "gemini-2.5-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
        + GEMINI_MODEL + ":generateContent?key=";

    // ── OpenRouter (fallback) ─────────────────────────────────────────────────
    private static final String OR_URL   = "https://openrouter.ai/api/v1/chat/completions";
    private static final String OR_MODEL = "nvidia/nemotron-3-nano-omni-30b-a3b-reasoning:free";

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .build();
    private static final Gson gson = new Gson();

    private static String apiKey   = loadApiKey();
    private static String orApiKey = loadOpenRouterApiKey();

    private static String loadApiKey() {
        // 1. Central EnvLoader (System env or .env file)
        String key = EnvLoader.get("GOOGLE_API_KEY");
        if (key == null || key.isBlank()) {
            key = EnvLoader.get("GEMINI_API_KEY");
        }
        if (key != null && !key.isBlank()) return key.trim();

        // 2. Local fallback file (anthropic.key — naming kept for historical compatibility)
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

    private static String loadOpenRouterApiKey() {
        // 1. Central EnvLoader
        String key = EnvLoader.get("OPENROUTER_API_KEY");
        if (key != null && !key.isBlank()) return key.trim();

        // 2. Local fallback file
        try {
            java.io.InputStream is = ChatbotService.class.getResourceAsStream("/openrouter.key");
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
        return (apiKey != null && !apiKey.isBlank())
            || (orApiKey != null && !orApiKey.isBlank());
    }

    public String healthCheck() {
        StringBuilder sb = new StringBuilder();
        if (apiKey == null || apiKey.isBlank()) {
            sb.append("- Google/Gemini API key manquante (Cherché: GOOGLE_API_KEY, GEMINI_API_KEY dans .env ou Env Vars)\n");
        }
        if (orApiKey == null || orApiKey.isBlank()) {
            sb.append("- OpenRouter API key manquante (Cherché: OPENROUTER_API_KEY)\n");
        }
        
        if (sb.length() == 0) {
            return "✅ Chatbot configuré et prêt (AI Active).";
        }
        
        return "⚠️ Le chatbot utilise des réponses par défaut car :\n" + sb.toString() + 
               "\nNote: Assurez-vous que vos variables sont dans le fichier .env ou vos variables d'environnement système.";
    }



    public String chat(String message, Map<String, String> customerInfo) {
        if (message == null || message.isBlank()) {
            return "Posez une question pour obtenir de l'aide.";
        }

        if (!isConfigured()) {
            return buildLocalSupportReply(message, customerInfo);
        }

        String prompt = buildSupportPrompt(message, customerInfo);
        return callGemini(prompt, "Support", 1024);
    }

    private static boolean isORConfigured() {
        return orApiKey != null && !orApiKey.isBlank();
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
        return callGemini(prompt.toString(), "Tournament", 8192);
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

    /** Calls Gemini with default token budget (2048) — used for all creative writing tasks. */
    private static String callGemini(String prompt, String fallbackName) {
        return callGemini(prompt, fallbackName, 2048);
    }

    /**
     * Core Gemini caller.
     * Thinking is explicitly disabled (thinkingBudget=0) for all calls:
     * - Gemini 2.5-flash enables thinking by default, and thinking tokens
     *   consume from the same maxOutputTokens budget, starving the actual reply.
     * - We don't need reasoning for creative writing / storytelling.
     *
     * Falls back to OpenRouter automatically on:
     *   - 503 (Gemini overloaded)
     *   - 429 quota exhausted after one retry
     *   - IOException (network failure)
     */
    private static String callGemini(String prompt, String fallbackName, int maxTokens) {
        // If Gemini key missing but OpenRouter is available, skip straight to OpenRouter
        if (apiKey == null || apiKey.isBlank()) {
            if (isORConfigured()) {
                System.out.println("Gemini key absent — using OpenRouter directly.");
                return callOpenRouter(prompt, maxTokens);
            }
            return getFallbackResponse(fallbackName, prompt);
        }

        // Build request body once — reused across retries
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        JsonArray parts = new JsonArray();
        parts.add(part);
        JsonObject content = new JsonObject();
        content.add("parts", parts);
        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject thinkingConfig = new JsonObject();
        thinkingConfig.addProperty("thinkingBudget", 0);
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("maxOutputTokens", maxTokens);
        genConfig.addProperty("temperature", 0.9);
        genConfig.add("thinkingConfig", thinkingConfig);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contents);
        requestBody.add("generationConfig", genConfig);

        // Two attempts max: first try, then one retry only after a 429 rate-limit wait.
        // We do NOT retry 503s — each retry burns one of the 5 req/min free-tier quota.
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
                );
                Request request = new Request.Builder()
                    .url(BASE_URL + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.body() == null) return "Pas de réponse reçue.";
                    String responseBody = response.body().string();

                    // 503 — server overload: fall back to OpenRouter immediately (saves Gemini quota)
                    if (response.code() == 503) {
                        System.err.println("Gemini 503 — serveur surchargé. Bascule sur OpenRouter…");
                        if (isORConfigured()) return callOpenRouter(prompt, maxTokens);
                        return "⏳ Gemini est surchargé — réessayez dans quelques secondes.";
                    }

                    // 429 — rate limit: parse the exact retry delay from Google's response, wait, then retry once
                    if (response.code() == 429) {
                        if (attempt == 2) {
                            // Already retried once — fall back to OpenRouter
                            System.err.println("Gemini 429 — quota épuisé. Bascule sur OpenRouter…");
                            if (isORConfigured()) return callOpenRouter(prompt, maxTokens);
                            return "⚠️ Quota Gemini atteint (5 req/min). Attendez ~60s et réessayez.";
                        }
                        int waitSecs = parseRetryDelay(responseBody);
                        System.err.println("Gemini 429 — quota atteint. Attente de " + waitSecs + "s avant réessai…");
                        Thread.sleep(waitSecs * 1000L);
                        continue; // one clean retry after the wait
                    }

                    if (!response.isSuccessful()) {
                        System.err.println("Gemini API error " + response.code() + ": " + responseBody);
                        if (isORConfigured()) {
                            System.err.println("Bascule sur OpenRouter suite à l'erreur " + response.code() + "…");
                            return callOpenRouter(prompt, maxTokens);
                        }
                        return "Erreur API (" + response.code() + ") — vérifiez votre clé Google.";
                    }

                    return parseGeminiResponse(responseBody);
                }
            } catch (IOException e) {
                System.err.println("Chatbot network error: " + e.getMessage());
                if (isORConfigured()) {
                    System.err.println("Bascule sur OpenRouter suite à l'erreur réseau…");
                    return callOpenRouter(prompt, maxTokens);
                }
                return "Connexion impossible — vérifiez votre connexion internet.";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Requête interrompue.";
            }
        }
        // Exhausted retries — try OpenRouter one last time
        if (isORConfigured()) {
            System.err.println("Gemini quota épuisé après réessai — bascule sur OpenRouter…");
            return callOpenRouter(prompt, maxTokens);
        }
        return "⚠️ Quota Gemini atteint — réessayez dans ~60 secondes.";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OpenRouter fallback (OpenAI-compatible API)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calls OpenRouter (mistral-7b-instruct:free) as fallback when Gemini is
     * unavailable. OpenRouter uses the standard OpenAI chat completions format.
     */
    private static String callOpenRouter(String prompt, int maxTokens) {
        try {
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);
            JsonArray messages = new JsonArray();
            messages.add(message);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", OR_MODEL);
            requestBody.add("messages", messages);
            requestBody.addProperty("max_tokens", maxTokens);
            requestBody.addProperty("temperature", 0.9);

            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
            );
            Request request = new Request.Builder()
                .url(OR_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + orApiKey)
                .addHeader("HTTP-Referer", "https://localhost")
                .addHeader("X-Title", "Fantasy Universe App")
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() == null) return "OpenRouter : pas de réponse reçue.";
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    System.err.println("OpenRouter API error " + response.code() + ": " + responseBody);
                    return "Erreur OpenRouter (" + response.code() + ") — vérifiez votre clé.";
                }

                return parseOpenRouterResponse(responseBody);
            }
        } catch (IOException e) {
            System.err.println("OpenRouter network error: " + e.getMessage());
            return "Connexion impossible — vérifiez votre connexion internet.";
        }
    }

    private static String parseOpenRouterResponse(String json) {
        try {
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            return obj.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString().trim();
        } catch (Exception e) {
            System.err.println("OpenRouter parse error: " + e.getMessage());
            return "Réponse inattendue d'OpenRouter.";
        }
    }

    /** Extracts the retryDelay (in seconds) from a Gemini 429 response body, defaulting to 62s. */
    private static int parseRetryDelay(String responseBody) {
        try {
            JsonObject obj     = gson.fromJson(responseBody, JsonObject.class);
            JsonArray  details = obj.getAsJsonObject("error").getAsJsonArray("details");
            for (int i = 0; i < details.size(); i++) {
                JsonObject detail = details.get(i).getAsJsonObject();
                if (detail.has("retryDelay")) {
                    // Value looks like "59s" or "59.407s"
                    String raw = detail.get("retryDelay").getAsString();
                    return (int) Math.ceil(Double.parseDouble(raw.replace("s", "").trim())) + 2;
                }
            }
        } catch (Exception ignored) {}
        return 62; // safe default if parsing fails
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

    private String buildLocalSupportReply(String message, Map<String, String> customerInfo) {
        String normalized = message.toLowerCase();
        String customerName = customerInfo != null ? customerInfo.getOrDefault("name", "client") : "client";

        if (normalized.contains("commande") || normalized.contains("order")) {
            return "Bonjour " + customerName + ", je peux vous aider avec votre commande.";
        }
        if (normalized.contains("livraison") || normalized.contains("shipping") || normalized.contains("expédition")) {
            return "Je peux vous aider pour la livraison. Indiquez votre numéro de commande.";
        }
        if (normalized.contains("retour") || normalized.contains("rembourse") || normalized.contains("refund")) {
            return "Pour un retour ou un remboursement, gardez votre preuve d'achat et contactez le support.";
        }
        if (normalized.contains("stock") || normalized.contains("disponible") || normalized.contains("disponibilité")) {
            return "Je peux vérifier les disponibilités des produits. Dites-moi le nom du produit qui vous intéresse.";
        }
        if (normalized.contains("prix") || normalized.contains("tarif")) {
            return "Dites-moi le produit qui vous intéresse et je vous aiderai à trouver son prix.";
        }
        return "Bonjour " + customerName + ", je suis le support boutique. Je peux aider pour les produits, les commandes, la livraison et les retours.";
    }

    private String buildSupportPrompt(String message, Map<String, String> customerInfo) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Tu es l'assistant boutique Midgar. Réponds en français, de façon courte, utile et polie. ");
        prompt.append("Tu aides pour les produits, commandes et le support. Si tu ne sais pas, recommande de contacter un humain.\n\n");

        if (customerInfo != null && !customerInfo.isEmpty()) {
            prompt.append("Contexte client:\n");
            customerInfo.forEach((key, value) -> prompt.append("- ").append(key).append(": ").append(value).append('\n'));
            prompt.append('\n');
        }

        prompt.append("Message utilisateur: ").append(message.trim());
        return prompt.toString();
    }

    public static java.util.Map<String, String> buildDefaultCustomerInfo(String username, String orderNumber, String issueType) {
        java.util.Map<String, String> info = new java.util.LinkedHashMap<>();
        if (username != null && !username.isBlank()) {
            info.put("name", username.trim());
        }
        if (orderNumber != null && !orderNumber.isBlank()) {
            info.put("order_number", orderNumber.trim());
        }
        if (issueType != null && !issueType.isBlank()) {
            info.put("issue_type", issueType.trim());
        }
        return info;
    }
}
