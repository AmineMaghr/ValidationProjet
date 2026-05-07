package com.example.app.services;

import com.example.app.services.RankedPost;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * DiscoverService — Java port of the Symfony DiscoverService.php.
 *
 * Dependencies: a JDBC {@link Connection} to the same database.
 * No ORM, no framework — plain JDBC only.
 *
 * Usage:
 *   Connection conn = DriverManager.getConnection(...);
 *   DiscoverService svc = new DiscoverService(conn);
 *   List<RankedPost> feed = svc.getRankedPostsForUser(1, 50);
 */
public class DiscoverService {

    // ── Weights (must match PHP constants exactly) ──────────────────────────
    private static final double WEIGHT_TAG        = 0.40;
    private static final double WEIGHT_ENGAGEMENT = 0.25;
    private static final double WEIGHT_RECENCY    = 0.20;
    private static final double WEIGHT_BEHAVIOR   = 0.15;

    /**
     * Score returned for components whose DB column does not exist for a given
     * post type — keeps the post "neutral" rather than penalising it unfairly.
     */
    private static final double NEUTRAL_UNSUPPORTED_COMPONENT_SCORE = 0.5;

    private final Connection connection;

    public DiscoverService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Public API
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns posts ranked for {@code userId}, optionally capped at {@code limit}
     * entries (pass {@code null} or {@code 0} for no cap).
     */
    public List<RankedPost> getRankedPostsForUser(int userId, Integer limit) throws SQLException {

        List<Map<String, Object>> posts = loadAllPosts();
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        // ── Tag preference map ────────────────────────────────────────────
        Map<String, Integer> userTagFrequencyMap = buildUserTagFrequencyMap(userId);
        int maxUserTagFrequency = maxFrequency(userTagFrequencyMap);

        // ── Engagement stats ──────────────────────────────────────────────
        EngagementStats engagementStats = loadEngagementStats();

        // ── Behaviour (liked-tag) map ─────────────────────────────────────
        BehaviorStats behaviorStats = loadBehaviorTagFrequencyMap(userId);
        int maxBehaviorTagFrequency = maxFrequency(behaviorStats.tagFrequencyMap);

        // ── Pre-compute raw recency for normalisation ─────────────────────
        LocalDateTime now = LocalDateTime.now();
        Map<String, Double> recencyRawByKey = new HashMap<>();
        for (Map<String, Object> post : posts) {
            String key = postKey((String) post.get("type"), (int) post.get("id"));
            recencyRawByKey.put(key, calculateRawRecency((LocalDateTime) post.get("createdAt"), now));
        }

        double minRecencyRaw = recencyRawByKey.isEmpty() ? 0.0
                : Collections.min(recencyRawByKey.values());
        double maxRecencyRaw = recencyRawByKey.isEmpty() ? 1.0
                : Collections.max(recencyRawByKey.values());

        // ── Score every post ──────────────────────────────────────────────
        List<RankedPost> ranked = new ArrayList<>();

        for (Map<String, Object> post : posts) {
            String postTag = (String) post.get("tag");
            String type    = (String) post.get("type");
            int    id      = (int)    post.get("id");
            LocalDateTime createdAt = (LocalDateTime) post.get("createdAt");

            double tagSimilarity = calculateTagSimilarity(postTag, userTagFrequencyMap, maxUserTagFrequency);
            double engagementScore = calculateEngagementScore(
                    type, id,
                    engagementStats.engagementMap,
                    engagementStats.maxEngagementRaw,
                    engagementStats.supportedTypes);
            double recencyDecay = calculateRecencyDecay(createdAt, now, minRecencyRaw, maxRecencyRaw);
            double behaviorSimilarity = calculateBehaviorSimilarity(
                    postTag, type,
                    behaviorStats.tagFrequencyMap,
                    maxBehaviorTagFrequency,
                    behaviorStats.supportedTypes);

            double finalScore =
                    (WEIGHT_TAG        * tagSimilarity)    +
                            (WEIGHT_ENGAGEMENT * engagementScore)  +
                            (WEIGHT_RECENCY    * recencyDecay)     +
                            (WEIGHT_BEHAVIOR   * behaviorSimilarity);

            // Posts created within the last 48 hours get a score floor of 0.25
            long ageSeconds = now.toEpochSecond(ZoneOffset.UTC) - createdAt.toEpochSecond(ZoneOffset.UTC);
            boolean isRecent = ageSeconds < 172_800L;
            if (isRecent) {
                finalScore = Math.max(finalScore, 0.25);
            }

            // Freshness bonus
            if (recencyDecay > 0.7) {
                finalScore += 0.1;
            }

            // Hard floor filter
            if (finalScore <= 0.2) {
                continue;
            }

            // All-zero filter (keeps only posts with at least one signal)
            if (tagSimilarity == 0.0 && behaviorSimilarity == 0.0 && engagementScore == 0.0 && !isRecent) {
                continue;
            }

            Map<String, Double> breakdown = new LinkedHashMap<>();
            breakdown.put("tag_similarity",      round6(tagSimilarity));
            breakdown.put("engagement_score",    round6(engagementScore));
            breakdown.put("recency_decay",       round6(recencyDecay));
            breakdown.put("behavior_similarity", round6(behaviorSimilarity));

            ranked.add(new RankedPost(type, id, postTag, round6(clamp01(finalScore)), breakdown));
        }

        // ── Sort descending by score ──────────────────────────────────────
        ranked.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        // ── Optional limit ────────────────────────────────────────────────
        if (limit != null && limit > 0 && ranked.size() > limit) {
            return ranked.subList(0, limit);
        }
        return ranked;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Data-loading helpers
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Loads every post from the four content tables.
     * Mirrors PHP {@code loadAllPosts()}.
     */
    private List<Map<String, Object>> loadAllPosts() throws SQLException {
        List<Map<String, Object>> posts = new ArrayList<>();
        fetchPostsFromTable(posts, "oeuvres",   "oeuvre");
        fetchPostsFromTable(posts, "artefacts",  "artefact");
        fetchPostsFromTable(posts, "personnage", "personnage");
        fetchPostsFromTable(posts, "universe",   "universe");
        return posts;
    }

    private void fetchPostsFromTable(List<Map<String, Object>> posts,
                                     String tableName,
                                     String typeName) throws SQLException {
        // Guard: skip silently if the table doesn't exist
        if (!tableExists(tableName)) {
            return;
        }

        String sql = "SELECT id, tag, created_at FROM " + tableName;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> post = new HashMap<>();
                post.put("type",      typeName);
                post.put("id",        rs.getInt("id"));
                post.put("tag",       normalizeTag(rs.getString("tag")));
                post.put("createdAt", dateFromDb(rs.getString("created_at")));
                posts.add(post);
            }
        }
    }

    /**
     * Builds tag-frequency map from user_preferences.
     * Mirrors PHP {@code buildUserTagFrequencyMap()}.
     */
    private Map<String, Integer> buildUserTagFrequencyMap(int userId) throws SQLException {
        Map<String, Integer> frequency = new HashMap<>();

        if (!tableExists("user_preferences")) {
            return frequency;
        }

        String sql = "SELECT tags FROM user_preferences WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] tags = splitTags(rs.getString("tags"));
                    for (String tag : tags) {
                        String normalized = normalizeTag(tag);
                        if (!normalized.isEmpty()) {
                            frequency.merge(normalized, 1, Integer::sum);
                        }
                    }
                }
            }
        }
        return frequency;
    }

    // ───────────────────────────────────────────────────────────────────────
    // Engagement stats
    // ───────────────────────────────────────────────────────────────────────

    /** Mirrors PHP {@code loadEngagementStats()}. */
    private EngagementStats loadEngagementStats() throws SQLException {
        Set<String> favorisColumns    = getTableColumns("favoris");
        Set<String> commentairesCols  = getTableColumns("commentaires");

        Map<String, Boolean> supportedTypes = new HashMap<>();
        for (String type : new String[]{"oeuvre", "artefact", "personnage", "universe"}) {
            boolean supported = favorisColumns.contains(type + "_id")
                    || commentairesCols.contains(type + "_id");
            supportedTypes.put(type, supported);
        }

        Map<String, Double> engagementRaw = new HashMap<>();

        // Likes (weight ×1)
        accumulateEngagement(engagementRaw, "favoris",      "oeuvre_id",     "oeuvre",     1.0, favorisColumns);
        accumulateEngagement(engagementRaw, "favoris",      "artefact_id",   "artefact",   1.0, favorisColumns);
        accumulateEngagement(engagementRaw, "favoris",      "personnage_id", "personnage", 1.0, favorisColumns);
        accumulateEngagement(engagementRaw, "favoris",      "universe_id",   "universe",   1.0, favorisColumns);

        // Comments (weight ×2)
        accumulateEngagement(engagementRaw, "commentaires", "oeuvre_id",     "oeuvre",     2.0, commentairesCols);
        accumulateEngagement(engagementRaw, "commentaires", "artefact_id",   "artefact",   2.0, commentairesCols);
        accumulateEngagement(engagementRaw, "commentaires", "personnage_id", "personnage", 2.0, commentairesCols);
        accumulateEngagement(engagementRaw, "commentaires", "universe_id",   "universe",   2.0, commentairesCols);

        double maxRaw = engagementRaw.isEmpty() ? 0.0
                : Collections.max(engagementRaw.values());

        return new EngagementStats(engagementRaw, maxRaw, supportedTypes);
    }

    /**
     * Runs one SELECT against {@code table} for {@code column} and accumulates
     * weighted counts into {@code engagementRaw}.  No-ops if the column is absent.
     */
    private void accumulateEngagement(Map<String, Double> engagementRaw,
                                      String table,
                                      String column,
                                      String typeName,
                                      double weight,
                                      Set<String> existingColumns) throws SQLException {
        if (!existingColumns.contains(column)) {
            return;
        }

        String sql = "SELECT " + column + " AS id, COUNT(id) AS c FROM " + table
                + " WHERE " + column + " IS NOT NULL GROUP BY " + column;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String key = postKey(typeName, rs.getInt("id"));
                double existing = engagementRaw.getOrDefault(key, 0.0);
                engagementRaw.put(key, existing + weight * rs.getDouble("c"));
            }
        }
    }

    // ───────────────────────────────────────────────────────────────────────
    // Behaviour tag frequency
    // ───────────────────────────────────────────────────────────────────────

    /** Mirrors PHP {@code loadBehaviorTagFrequencyMap()}. */
    private BehaviorStats loadBehaviorTagFrequencyMap(int userId) throws SQLException {
        Set<String> favorisColumns = getTableColumns("favoris");

        Map<String, Boolean> supportedTypes = new HashMap<>();
        for (String type : new String[]{"oeuvre", "artefact", "personnage", "universe"}) {
            supportedTypes.put(type, favorisColumns.contains(type + "_id"));
        }

        Map<String, Integer> tagFrequency = new HashMap<>();

        accumulateBehaviorTags(tagFrequency, userId, "favoris", "oeuvre_id",     "oeuvres",   favorisColumns);
        accumulateBehaviorTags(tagFrequency, userId, "favoris", "artefact_id",   "artefacts", favorisColumns);
        accumulateBehaviorTags(tagFrequency, userId, "favoris", "personnage_id", "personnage",favorisColumns);
        accumulateBehaviorTags(tagFrequency, userId, "favoris", "universe_id",   "universe",  favorisColumns);

        return new BehaviorStats(tagFrequency, supportedTypes);
    }

    private void accumulateBehaviorTags(Map<String, Integer> tagFrequency,
                                        int userId,
                                        String favTable,
                                        String fkColumn,
                                        String contentTable,
                                        Set<String> favorisColumns) throws SQLException {
        if (!favorisColumns.contains(fkColumn)) {
            return;
        }

        // Derive the join alias (first letter of content table) to keep SQL readable
        String alias = contentTable.substring(0, 1);
        String sql =
                "SELECT " + alias + ".tag AS tag, COUNT(f.id) AS c " +
                        "FROM " + favTable + " f " +
                        "INNER JOIN " + contentTable + " " + alias + " ON " + alias + ".id = f." + fkColumn + " " +
                        "WHERE f.user_id = ? AND f." + fkColumn + " IS NOT NULL " +
                        "GROUP BY " + alias + ".tag";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String normalized = normalizeTag(rs.getString("tag"));
                    if (!normalized.isEmpty()) {
                        tagFrequency.merge(normalized, rs.getInt("c"), Integer::sum);
                    }
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Scoring helpers  (identical logic to PHP)
    // ═══════════════════════════════════════════════════════════════════════

    private double calculateTagSimilarity(String postTag,
                                          Map<String, Integer> userTagFrequencyMap,
                                          int maxTagFrequency) {
        String normalizedTag = normalizeTag(postTag);
        if (normalizedTag.isEmpty() || maxTagFrequency <= 0) {
            return 0.0;
        }
        int frequency = userTagFrequencyMap.getOrDefault(normalizedTag, 0);
        return clamp01((double) frequency / maxTagFrequency);
    }

    private double calculateEngagementScore(String type,
                                            int id,
                                            Map<String, Double> engagementMap,
                                            double maxEngagementRaw,
                                            Map<String, Boolean> supportedTypes) {
        if (!supportedTypes.getOrDefault(type, false)) {
            return NEUTRAL_UNSUPPORTED_COMPONENT_SCORE;
        }
        if (maxEngagementRaw <= 0.0) {
            return 0.0;
        }
        double raw = engagementMap.getOrDefault(postKey(type, id), 0.0);
        return clamp01(raw / maxEngagementRaw);
    }

    private double calculateRecencyDecay(LocalDateTime createdAt,
                                         LocalDateTime now,
                                         double minRaw,
                                         double maxRaw) {
        double raw = calculateRawRecency(createdAt, now);
        if (maxRaw <= minRaw) {
            return 1.0;
        }
        return clamp01((raw - minRaw) / (maxRaw - minRaw));
    }

    private double calculateBehaviorSimilarity(String postTag,
                                               String type,
                                               Map<String, Integer> behaviorTagFrequencyMap,
                                               int maxBehaviorTagFrequency,
                                               Map<String, Boolean> supportedTypes) {
        if (!supportedTypes.getOrDefault(type, false)) {
            return NEUTRAL_UNSUPPORTED_COMPONENT_SCORE;
        }
        String normalizedTag = normalizeTag(postTag);
        if (normalizedTag.isEmpty() || maxBehaviorTagFrequency <= 0) {
            return 0.0;
        }
        int frequency = behaviorTagFrequencyMap.getOrDefault(normalizedTag, 0);
        return clamp01((double) frequency / maxBehaviorTagFrequency);
    }

    /** Half-life exponential decay — 30-day half-life. */
    private double calculateRawRecency(LocalDateTime createdAt, LocalDateTime now) {
        long ageSeconds = Math.max(0,
                now.toEpochSecond(ZoneOffset.UTC) - createdAt.toEpochSecond(ZoneOffset.UTC));
        double ageInDays  = ageSeconds / 86_400.0;
        double halfLifeDays = 30.0;
        return Math.exp(-ageInDays / halfLifeDays);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DB / schema helpers
    // ═══════════════════════════════════════════════════════════════════════

    /** Returns the set of lower-cased column names for {@code tableName}, or an
     *  empty set if the table doesn't exist (graceful degradation). */
    private Set<String> getTableColumns(String tableName) {
        Set<String> columns = new LinkedHashSet<>();
        try {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                }
            }
        } catch (SQLException e) {
            // table absent or inaccessible — return empty set
        }
        return columns;
    }

    /** Returns {@code true} if {@code tableName} exists in the current schema. */
    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Utility helpers
    // ═══════════════════════════════════════════════════════════════════════

    private String postKey(String type, int id) {
        return type + ":" + id;
    }

    private int maxFrequency(Map<String, Integer> frequencyMap) {
        if (frequencyMap.isEmpty()) return 0;
        return Collections.max(frequencyMap.values());
    }

    private double clamp01(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    private double round6(double value) {
        return Math.round(value * 1_000_000.0) / 1_000_000.0;
    }

    /** Splits a comma-separated tag string, trims whitespace, drops blanks. */
    private String[] splitTags(String tags) {
        if (tags == null || tags.isBlank()) return new String[0];
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toArray(String[]::new);
    }

    /** Lower-cases, trims, and prepends '#' if missing. */
    private String normalizeTag(String tag) {
        if (tag == null) return "";
        String normalized = tag.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) return "";
        if (!normalized.startsWith("#")) normalized = "#" + normalized;
        return normalized;
    }

    /** Parses a DB date string into {@link LocalDateTime}; falls back to epoch on error. */
    private LocalDateTime dateFromDb(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        try {
            // MySQL / MariaDB: "2024-05-01 14:32:00"  — replace space with 'T' for ISO parsing
            return LocalDateTime.parse(value.replace(' ', 'T'));
        } catch (Exception e) {
            return LocalDateTime.of(1970, 1, 1, 0, 0);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Private value-object inner classes (keeps everything in one file)
    // ═══════════════════════════════════════════════════════════════════════

    private static final class EngagementStats {
        final Map<String, Double>  engagementMap;
        final double               maxEngagementRaw;
        final Map<String, Boolean> supportedTypes;

        EngagementStats(Map<String, Double> engagementMap,
                        double maxEngagementRaw,
                        Map<String, Boolean> supportedTypes) {
            this.engagementMap    = engagementMap;
            this.maxEngagementRaw = maxEngagementRaw;
            this.supportedTypes   = supportedTypes;
        }
    }

    private static final class BehaviorStats {
        final Map<String, Integer> tagFrequencyMap;
        final Map<String, Boolean> supportedTypes;

        BehaviorStats(Map<String, Integer> tagFrequencyMap,
                      Map<String, Boolean> supportedTypes) {
            this.tagFrequencyMap = tagFrequencyMap;
            this.supportedTypes  = supportedTypes;
        }
    }
}
