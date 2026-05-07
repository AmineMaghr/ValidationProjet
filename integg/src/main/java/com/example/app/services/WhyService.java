package com.example.app.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Java equivalent of AiExplanationService.php.
 *
 * Generates a human-readable explanation for why a given post was recommended
 * to a user, using the Groq LLM API (llama-3.1-8b-instant).
 *
 * When the API is disabled or unavailable the constant FALLBACK_EXPLANATION
 * is returned — the caller (e.g. WhyController) must render a sensible default.
 *
 * SETUP:
 *   Replace GROQ_API_KEY with your key from https://console.groq.com
 *   Set AI_ENABLED = false to skip the API call entirely during development.
 */
public class WhyService {

    private static final Logger LOG = Logger.getLogger(WhyService.class.getName());

    // ── Constants (mirrors PHP class constants) ──────────────────────────
    public  static final String FALLBACK_EXPLANATION = "FALLBACK_TRIGGERED";
    private static final String GROQ_ENDPOINT        = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODEL           = "llama-3.1-8b-instant";

    // ── Configuration ────────────────────────────────────────────────────
    private static String  GROQ_API_KEY = "";
    private static final boolean AI_ENABLED   = true;   // set false to always use fallback

    // ── Dependencies ─────────────────────────────────────────────────────
    private final Connection  connection;
    private final HttpClient  httpClient;

    static {
        try {
            java.util.Properties props = new java.util.Properties();
            try (java.io.InputStream is = WhyService.class.getResourceAsStream("/config.properties")) {
                if (is != null) props.load(is);
            }
            GROQ_API_KEY = props.getProperty("groq.api.key", "");
        } catch (Exception ignored) {}
    }

    public WhyService(Connection connection) {
        this.connection = connection;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Public API
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Generates an explanation for why {@code post} was shown to {@code userId}.
     *
     * Mirrors PHP: generateExplanation(User $user, string $postTag, array $scoreBreakdown)
     *
     * @param post   the ranked post (carries tag + score breakdown)
     * @param userId the user to explain for (pass 1 for the simulated user)
     * @return a {@link WhyResult} containing the explanation and score map
     */
    public WhyResult explain(RankedPost post, int userId) {
        Map<String, Double> scores = post.getScoreBreakdown();

        // ── Guard: AI disabled ───────────────────────────────────────────
        if (!AI_ENABLED) {
            return new WhyResult(FALLBACK_EXPLANATION, scores);
        }

        // ── Guard: missing API key ───────────────────────────────────────
        if (GROQ_API_KEY == null || GROQ_API_KEY.isBlank() || GROQ_API_KEY.equals("your_groq_api_key_here") || GROQ_API_KEY.equals("PASTE_KEY_HERE") || GROQ_API_KEY.equals("PASTE_YOUR_GROQ_API_KEY_HERE")) {
            LOG.warning("AI explanation enabled but GROQ_API_KEY is missing.");
            return new WhyResult(FALLBACK_EXPLANATION, scores);
        }

        // ── Build context ────────────────────────────────────────────────
        List<String> preferenceTags = getUserPreferenceTags(userId);
        List<String> behaviorTags   = getUserBehaviorTags(userId);
        String       normalizedTag  = normalizeTag(post.getTag());

        // ── Build prompt ─────────────────────────────────────────────────
        String prompt = buildPrompt(preferenceTags, behaviorTags, normalizedTag, scores, "short");

        // ── Call Groq ────────────────────────────────────────────────────
        String explanation = callGroq(prompt, userId, post.getTag());
        return new WhyResult(explanation, scores);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Prompt builder  (mirrors buildPrompt in PHP exactly)
    // ═══════════════════════════════════════════════════════════════════════

    private String buildPrompt(List<String> userPreferenceTags,
                               List<String> userBehaviorTags,
                               String postTag,
                               Map<String, Double> scoreBreakdown,
                               String mode) {

        double tagSimilarity      = scoreBreakdown.getOrDefault("tag_similarity",      0.0);
        double behaviorSimilarity = scoreBreakdown.getOrDefault("behavior_similarity", 0.0);
        double engagementScore    = scoreBreakdown.getOrDefault("engagement_score",    0.0);
        double recencyDecay       = scoreBreakdown.getOrDefault("recency_decay",       0.0);

        // Mirrors PHP ternary flags
        String interactionFlag = behaviorSimilarity > 0.4 ? "oui" : "non";
        String preferenceFlag  = tagSimilarity      > 0.4 ? "oui" : "non";
        String trendFlag       = (recencyDecay > 0.5 || engagementScore > 0.5) ? "oui" : "non";

        return String.format(
                "Tu es un moteur d'explication de recommandation.\n\n" +
                        "Contexte :\n\n" +
                        "Interaction passée avec ce thème : %s\n" +
                        "Présent dans vos préférences : %s\n" +
                        "Contenu récent ou populaire : %s\n\n" +
                        "Consignes strictes :\n\n" +
                        "- Explique pourquoi ce contenu est recommandé.\n" +
                        "- Utilise uniquement les signaux marqués \"oui\".\n" +
                        "- Si INTERACTION_FLAG = non, ne parle jamais d'interaction.\n" +
                        "- Si PREFERENCE_FLAG = non, ne parle jamais de préférence.\n" +
                        "- Si TREND_FLAG = non, ne parle jamais de popularité ou récence.\n" +
                        "- Si TREND_FLAG = oui et INTERACTION_FLAG = non et PREFERENCE_FLAG = non, " +
                        "base l'explication uniquement sur la récence ou la popularité.\n" +
                        "- Dans ce cas, ne mentionne jamais interaction ni préférence.\n" +
                        "- Maximum 3 phrases.\n" +
                        "- Ton professionnel et naturel.\n" +
                        "- Ne mentionne jamais de hashtag.\n" +
                        "- Ne mentionne jamais d'autre thème.\n" +
                        "- Ne rajoute aucune information.\n\n" +
                        "Répond uniquement par l'explication finale.",
                interactionFlag, preferenceFlag, trendFlag
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Groq HTTP call  (mirrors the httpClient->request() block in PHP)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Sends the prompt to Groq and returns the model's reply.
     * Returns FALLBACK_EXPLANATION on any error — never throws.
     */
    private String callGroq(String prompt, int userId, String postTag) {
        // Build JSON payload manually (no external JSON library required)
        String jsonPayload = buildJsonPayload(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_ENDPOINT))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            LOG.info(String.format("Groq response received | status=%d | model=%s",
                    response.statusCode(), GROQ_MODEL));

            return extractContent(response.body());

        } catch (IOException | InterruptedException e) {
            LOG.log(Level.SEVERE,
                    String.format("Failed to generate AI explanation via Groq | user_id=%d | tag=%s",
                            userId, postTag), e);
            return FALLBACK_EXPLANATION;
        }
    }

    /**
     * Builds the Groq request JSON payload without an external library.
     * Mirrors the PHP $payload array passed as 'json' to the HTTP client.
     */
    private String buildJsonPayload(String prompt) {
        // Escape the prompt for safe JSON embedding
        String escaped = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return "{"
                + "\"model\":\"" + GROQ_MODEL + "\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escaped + "\"}],"
                + "\"temperature\":0.5,"
                + "\"max_tokens\":80,"
                + "\"stream\":false"
                + "}";
    }

    /**
     * Extracts choices[0].message.content from the Groq JSON response.
     * Uses simple string search so no JSON library is needed.
     * Mirrors: $data['choices'][0]['message']['content'] ?? null
     */
    private String extractContent(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return FALLBACK_EXPLANATION;
        }

        // Find "content":"<value>" — robust enough for Groq's stable response format
        Pattern pattern = Pattern.compile("\"content\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        java.util.regex.Matcher matcher = pattern.matcher(responseBody);

        if (matcher.find()) {
            String raw = matcher.group(1);
            // Unescape JSON string sequences
            String unescaped = raw
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
            String trimmed = unescaped.trim();
            return trimmed.isEmpty() ? FALLBACK_EXPLANATION : trimmed;
        }

        LOG.warning("Could not extract 'content' from Groq response: " + responseBody);
        return FALLBACK_EXPLANATION;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DB helpers  (mirrors getUserPreferenceTags / getUserBehaviorTags in PHP)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Reads all preference tags for the user from user_preferences.
     * Mirrors PHP getUserPreferenceTags().
     *
     * SQL: SELECT tags FROM user_preferences WHERE user_id = ?
     */
    private List<String> getUserPreferenceTags(int userId) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT tags FROM user_preferences WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    for (String raw : splitTags(rs.getString("tags"))) {
                        String normalized = normalizeTag(raw);
                        if (!normalized.isEmpty()) {
                            tags.add(normalized);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Failed to load preference tags for user_id=" + userId, e);
        }
        return tags;
    }

    /**
     * Reads behavior tags (liked oeuvres + artefacts) for the user.
     * Returns entries like "#fantasy(x3)" — mirrors PHP getUserBehaviorTags().
     *
     * SQL: SELECT o.tag, COUNT(f.id) AS c
     *      FROM favoris f INNER JOIN oeuvres o ON o.id = f.oeuvre_id
     *      WHERE f.user_id = ? AND f.oeuvre_id IS NOT NULL
     *      GROUP BY o.tag
     */
    private List<String> getUserBehaviorTags(int userId) {
        List<String> tags = new ArrayList<>();

        // oeuvres
        String oeuvresSql =
                "SELECT o.tag AS tag, COUNT(f.id) AS c " +
                        "FROM favoris f " +
                        "INNER JOIN oeuvres o ON o.id = f.oeuvre_id " +
                        "WHERE f.user_id = ? AND f.oeuvre_id IS NOT NULL " +
                        "GROUP BY o.tag";
        fetchBehaviorRows(tags, userId, oeuvresSql);

        // artefacts
        String artefactsSql =
                "SELECT a.tag AS tag, COUNT(f.id) AS c " +
                        "FROM favoris f " +
                        "INNER JOIN artefacts a ON a.id = f.artefact_id " +
                        "WHERE f.user_id = ? AND f.artefact_id IS NOT NULL " +
                        "GROUP BY a.tag";
        fetchBehaviorRows(tags, userId, artefactsSql);

        return tags;
    }

    /** Shared row-fetch logic for behavior queries. */
    private void fetchBehaviorRows(List<String> tags, int userId, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String normalized = normalizeTag(rs.getString("tag"));
                    if (!normalized.isEmpty()) {
                        // e.g. "#fantasy(x3)"  — mirrors PHP sprintf('%s(x%d)', ...)
                        tags.add(String.format("%s(x%d)", normalized, rs.getInt("c")));
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Failed to load behavior tags for user_id=" + userId, e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Tag utilities  (identical to DiscoverService / PostNotificationService)
    // ═══════════════════════════════════════════════════════════════════════

    /** Lower-cases, trims, prepends '#' if missing. Returns "" for blank input. */
    private String normalizeTag(String tag) {
        if (tag == null) return "";
        String normalized = tag.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) return "";
        if (!normalized.startsWith("#")) normalized = "#" + normalized;
        return normalized;
    }

    /** Splits a comma-separated tag string; trims each part; drops blanks. */
    private List<String> splitTags(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.isBlank()) return result;
        for (String part : raw.split(",")) {
            String t = part.trim();
            if (!t.isEmpty()) result.add(t);
        }
        return result;
    }

    /**
     * Strips the "(xN)" suffix from a behavior tag before normalising.
     * Mirrors PHP normalizeBehaviorTag().
     * e.g. "#fantasy(x3)" → "#fantasy"
     */
    private String normalizeBehaviorTag(String tag) {
        if (tag == null) return "";
        String base = tag.trim().replaceAll("\\(x\\d+\\)$", "");
        return normalizeTag(base);
    }
}