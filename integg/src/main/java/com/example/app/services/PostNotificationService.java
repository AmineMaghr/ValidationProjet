package com.example.app.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java equivalent of PostNotificationService.php.
 *
 * Sends a notification email to the current user when newly created content
 * matches one of their saved preference tags.
 *
 * Simulated user:
 * For now, only user_id = 1 is checked.
 * The static TEST_USER_EMAIL is used as the recipient.
 *
 * TODO (when multi-user email is needed):
 * Replace the TEST_USER_EMAIL constant and the fetchRecipientsForTag() stub
 * with a real query:
 * SELECT u.email
 * FROM users u
 * INNER JOIN user_preferences up ON up.user_id = u.id
 * WHERE up.user_id = 1 -- or loop all matching users
 * and remove the static fallback.
 */
public class PostNotificationService {

    private static final Logger LOG = Logger.getLogger(PostNotificationService.class.getName());

    // ── Simulated recipient ──────────────────────────────────────────────
    // TODO: replace with a real DB lookup when multi-user email is supported.
    private static final String TEST_USER_EMAIL = "medamine52522@gmail.com";

    // ── Simulated user ───────────────────────────────────────────────────
    private static final int SIMULATED_USER_ID = 1;

    private final Connection connection;
    private final EmailService emailService;

    public PostNotificationService(Connection connection, EmailService emailService) {
        this.connection = connection;
        this.emailService = emailService;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Public API (mirrors notifyContentCreated in the PHP service)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Sends a notification email if the created content's tag matches
     * a preference tag of the simulated user (user_id = 1).
     *
     * This method never throws — all failures are caught and logged.
     *
     * @param typeLabel human-readable content type, e.g. "Oeuvre", "Artefact"
     * @param title     content title (or "#id" fallback)
     * @param tag       the raw tag on the content, e.g. "#fantasy"
     */
    public void notifyContentCreated(String typeLabel, String title, String tag) {
        System.out.println(String.format("[DEBUG] 4. notifyContentCreated() called | Type: %s | Title: %s | Tag: %s",
                typeLabel, title, tag));
        try {
            // ── 1. Guard: empty tag ──────────────────────────────────────
            String normalizedTag = normalizeTag(tag);
            if (normalizedTag.isEmpty()) {
                return;
            }

            // ── 2. Check if user_id=1 has a matching preference tag ──────
            boolean tagMatches = userHasMatchingTag(SIMULATED_USER_ID, normalizedTag);
            System.out.println("[DEBUG] 4.1 tagMatches in DB? " + tagMatches);
            if (!tagMatches) {
                System.out.println("[DEBUG] 4.2 User does not have this tag in preferences. Skipping email.");
                return;
            }

            // ── 3. Resolve recipient ─────────────────────────────────────
            // TODO: fetch real email from DB (see class-level Javadoc).
            String recipient = TEST_USER_EMAIL;

            // ── 4. Validate email before sending ─────────────────────────
            if (!emailService.isValidEmail(recipient)) {
                LOG.warning("Recipient email is invalid, skipping: " + recipient);
                return;
            }

            // ── 5. Build subject + body ────────────────────────────────────
            String subject = "Nouveau contenu sur Midgar : Quelque chose qui pourrait vous plaire !";
            String body = String.format(
                    "Bonjour,%n%n" +
                            "Quelque chose qui pourrait vous plaire vient d'être publié sur Midgar !%n" +
                            "Un nouveau contenu correspondant à vos préférences a été ajouté.%n%n" +
                            "--- Détails du contenu ---%n" +
                            "- Type  : %s%n" +
                            "- Titre : %s%n" +
                            "- Tag   : %s%n%n" +
                            "Connectez-vous sur l'application pour le découvrir.%n%n" +
                            "L'équipe Midgar",
                    typeLabel, title, normalizedTag);

            // ── 6. Send Plain Text Email ──────────────────────────────────
            emailService.sendText(recipient, subject, body);

        } catch (Throwable t) {
            // Mirrors the outer catch (\Throwable $e) in PHP — must never crash the app.
            LOG.log(Level.SEVERE, "PostNotificationService crashed (suppressed)", t);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DB helpers
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns true if any of the user's stored preference tags match
     * {@code normalizedTag}.
     *
     * SQL used:
     * SELECT tags FROM user_preferences WHERE user_id = ?
     *
     * Each row's "tags" column may contain comma-separated values like:
     * #dark,#magic,#fantasy
     */
    private boolean userHasMatchingTag(int userId, String normalizedTag) throws SQLException {
        String sql = "SELECT tags FROM user_preferences WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String rawTags = rs.getString("tags");
                    List<String> tags = splitAndNormalizeTags(rawTags);
                    if (tags.contains(normalizedTag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Tag helpers (mirrors normalizeTag / splitTags from DiscoverService)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Lower-cases, trims, and ensures the tag starts with '#'.
     * Returns an empty string if the input is blank.
     */
    private String normalizeTag(String tag) {
        if (tag == null)
            return "";
        String normalized = tag.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty())
            return "";
        if (!normalized.startsWith("#"))
            normalized = "#" + normalized;
        return normalized;
    }

    /**
     * Splits a comma-separated tag string, normalizes each entry, and drops blanks.
     * e.g. "#dark,#magic, fantasy" → ["#dark", "#magic", "#fantasy"]
     */
    private List<String> splitAndNormalizeTags(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.isBlank())
            return result;

        for (String part : raw.split(",")) {
            String normalized = normalizeTag(part);
            if (!normalized.isEmpty()) {
                result.add(normalized);
            }
        }
        return result;
    }
}