package com.example.app.dao;

import com.example.app.entities.AdvancedPreference;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancedPreferenceDAO implements IDAO<AdvancedPreference> {

    private Connection connection;

    public AdvancedPreferenceDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(AdvancedPreference preference) throws SQLException {
        String sql = "INSERT INTO advanced_preferences (free_description, favorite_genre, affinity_level, favorite_themes, custom_tags, user_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, preference.getFreeDescription());
        ps.setString(2, preference.getFavoriteGenre());
        ps.setInt(3, preference.getAffinityLevel());
        ps.setString(4, preference.getFavoriteThemes());
        ps.setString(5, preference.getCustomTags());
        ps.setString(6, preference.getUserId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            preference.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(AdvancedPreference preference) throws SQLException {
        String sql = "UPDATE advanced_preferences SET free_description = ?, favorite_genre = ?, affinity_level = ?, favorite_themes = ?, custom_tags = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, preference.getFreeDescription());
        ps.setString(2, preference.getFavoriteGenre());
        ps.setInt(3, preference.getAffinityLevel());
        ps.setString(4, preference.getFavoriteThemes());
        ps.setString(5, preference.getCustomTags());
        ps.setInt(6, preference.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM advanced_preferences WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<AdvancedPreference> select() throws SQLException {
        List<AdvancedPreference> list = new ArrayList<>();
        String sql = "SELECT * FROM advanced_preferences ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public AdvancedPreference findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM advanced_preferences WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<AdvancedPreference> findAllOrderedByDate() throws SQLException {
        String sql = "SELECT * FROM advanced_preferences ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<AdvancedPreference> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private AdvancedPreference mapResultSet(ResultSet rs) throws SQLException {
        AdvancedPreference preference = new AdvancedPreference();
        preference.setId(rs.getInt("id"));
        preference.setFreeDescription(rs.getString("free_description"));
        preference.setFavoriteGenre(rs.getString("favorite_genre"));
        preference.setAffinityLevel(rs.getInt("affinity_level"));
        preference.setFavoriteThemes(rs.getString("favorite_themes"));
        preference.setCustomTags(rs.getString("custom_tags"));
        preference.setUserId(rs.getString("user_id"));
        return preference;
    }
}