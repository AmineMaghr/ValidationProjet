package com.example.app.services;


import com.example.app.entities.AdvancedPreference;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancedPreferenceService implements Iservice<AdvancedPreference> {

    private Connection connection;

    public AdvancedPreferenceService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(AdvancedPreference p) throws SQLException {

        String sql = "INSERT INTO advanced_preference " +
                "(free_description, favorite_genre, affinity_level, favorite_themes, custom_tags, user_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, p.getFreeDescription());
        ps.setString(2, p.getFavoriteGenre());
        ps.setInt(3, p.getAffinityLevel());
        ps.setString(4, p.getFavoriteThemes());
        ps.setString(5, p.getCustomTags());
        ps.setInt(6, p.getUserId());
        ps.setTimestamp(7, Timestamp.valueOf(p.getCreatedAt()));
        ps.setTimestamp(8, Timestamp.valueOf(p.getUpdatedAt()));

        ps.executeUpdate();

        System.out.println("AdvancedPreference ajoutée");
    }

    @Override
    public void update(AdvancedPreference p) throws SQLException {

        String sql = "UPDATE advanced_preference SET " +
                "free_description=?, favorite_genre=?, affinity_level=?, favorite_themes=?, custom_tags=?, user_id=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, p.getFreeDescription());
        ps.setString(2, p.getFavoriteGenre());
        ps.setInt(3, p.getAffinityLevel());
        ps.setString(4, p.getFavoriteThemes());
        ps.setString(5, p.getCustomTags());
        ps.setInt(6, p.getUserId());
        ps.setTimestamp(7, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(8, p.getId());

        ps.executeUpdate();

        System.out.println("AdvancedPreference modifiée");
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM advanced_preference WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();

        System.out.println("AdvancedPreference supprimée");
    }

    @Override
    public List<AdvancedPreference> select() throws SQLException {

        List<AdvancedPreference> list = new ArrayList<>();

        String sql = "SELECT * FROM advanced_preference";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            AdvancedPreference p = new AdvancedPreference();

            p.setId(rs.getInt("id"));
            p.setFreeDescription(rs.getString("free_description"));
            p.setFavoriteGenre(rs.getString("favorite_genre"));
            p.setAffinityLevel(rs.getInt("affinity_level"));
            p.setFavoriteThemes(rs.getString("favorite_themes"));
            p.setCustomTags(rs.getString("custom_tags"));
            p.setUserId(rs.getInt("user_id"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                p.setCreatedAt(created.toLocalDateTime());
            }

            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                p.setUpdatedAt(updated.toLocalDateTime());
            }

            list.add(p);
        }

        return list;
    }

    // Méthodes spécifiques pour AdvancedPreference

    /**
     * Trouve les préférences par ID utilisateur
     */
    public AdvancedPreference findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM advanced_preference WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AdvancedPreference ap = new AdvancedPreference();
            ap.setId(rs.getInt("id"));
            ap.setFreeDescription(rs.getString("free_description"));
            ap.setFavoriteGenre(rs.getString("favorite_genre"));
            ap.setAffinityLevel(rs.getInt("affinity_level"));
            ap.setFavoriteThemes(rs.getString("favorite_themes"));
            ap.setCustomTags(rs.getString("custom_tags"));
            ap.setUserId(rs.getInt("user_id"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                ap.setCreatedAt(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                ap.setUpdatedAt(updated.toLocalDateTime());
            }
            return ap;
        }
        return null;
    }

    /**
     * Met à jour les préférences d'un utilisateur
     */
    public void updatePreference(int id, String desc, String genre, int affinity, String themes, String tags) throws SQLException {
        String sql = "UPDATE advanced_preference SET " +
                "free_description=?, favorite_genre=?, affinity_level=?, favorite_themes=?, custom_tags=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, desc);
        ps.setString(2, genre);
        ps.setInt(3, affinity);
        ps.setString(4, themes);
        ps.setString(5, tags);
        ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));
        ps.setInt(7, id);
        ps.executeUpdate();
        System.out.println("Préférences mises à jour");
    }

    /**
     * Crée de nouvelles préférences pour un utilisateur
     */
    public void createPreference(int userId, String desc, String genre, int affinity, String themes, String tags) throws SQLException {
        AdvancedPreference ap = new AdvancedPreference();
        ap.setUserId(userId);
        ap.setFreeDescription(desc);
        ap.setFavoriteGenre(genre);
        ap.setAffinityLevel(affinity);
        ap.setFavoriteThemes(themes);
        ap.setCustomTags(tags);
        add(ap);
    }

    /**
     * Supprime les préférences d'un utilisateur
     */
    public void deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM advanced_preference WHERE user_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.executeUpdate();
        System.out.println("Préférences supprimées pour l'utilisateur " + userId);
    }

    /**
     * Vérifie si un utilisateur a des préférences
     */
    public boolean hasPreferences(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM advanced_preference WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}
