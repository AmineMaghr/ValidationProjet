package com.example.app.dao;

import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IDAO<User> {

    protected Connection connection;

    public UserDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO user (prenom, nom, username, email, password, role, avatar, bio, is_blocked, is_verified, phone_number, auth_provider, face_enabled, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getPrenom());
            ps.setString(2, user.getNom());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole() != null ? user.getRole() : "user");
            ps.setString(7, user.getAvatar());
            ps.setString(8, user.getBio());
            ps.setBoolean(9, user.isBlocked());
            ps.setBoolean(10, user.isVerified());
            ps.setString(11, user.getPhoneNumber());
            ps.setString(12, user.getAuthProvider() != null ? user.getAuthProvider() : "local");
            ps.setBoolean(13, user.isFaceEnabled());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET prenom = ?, nom = ?, username = ?, email = ?, password = ?, role = ?, avatar = ?, is_blocked = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getPrenom());
            ps.setString(2, user.getNom());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getAvatar());
            ps.setBoolean(8, user.isBlocked());
            ps.setInt(9, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public User findActiveByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ? AND is_blocked = false";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<User> findAdmins() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = 'admin'";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<User> searchPublicUsers(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE username LIKE ? AND is_blocked = false LIMIT 10";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + query + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<User> searchUsersApi(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, prenom, nom, avatar, created_at FROM user WHERE (username LIKE ? OR prenom LIKE ? OR nom LIKE ?) AND is_blocked = false ORDER BY username ASC LIMIT 10";
        PreparedStatement ps = connection.prepareStatement(sql);
        String searchPattern = "%" + query + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        ps.setString(3, searchPattern);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPrenom(rs.getString("prenom"));
            user.setNom(rs.getString("nom"));
            user.setAvatar(rs.getString("avatar"));
            list.add(user);
        }
        return list;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public java.util.List<String> generateUsernameSuggestions(String baseUsername) throws SQLException {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        String[] suffixes = {"123", "42", "007", "2024", "2025", "2026", "_official", "_fan", "_legend", "_master"};

        for (String suffix : suffixes) {
            String suggestion = baseUsername + suffix;
            if (!isUsernameTaken(suggestion)) {
                suggestions.add(suggestion);
                if (suggestions.size() >= 5) break;
            }
        }

        // Si pas assez de suggestions, ajouter des numéros aléatoires
        for (int i = 1; i <= 10 && suggestions.size() < 5; i++) {
            String suggestion = baseUsername + i;
            if (!isUsernameTaken(suggestion) && !suggestions.contains(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    protected User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setPrenom(rs.getString("prenom"));
        user.setNom(rs.getString("nom"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setAvatar(rs.getString("avatar"));
        user.setBio(rs.getString("bio"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAuthProvider(rs.getString("auth_provider"));
        user.setFaceEnabled(rs.getBoolean("face_enabled"));

        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        try {
            java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                user.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (java.sql.SQLException e) {
            // Column may not exist, set to createdAt
            user.setUpdatedAt(user.getCreatedAt());
        }

        return user;
    }

    public void updateUserBlockStatus(int userId, boolean blocked) throws SQLException {
        String sql = "UPDATE user SET is_blocked = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, blocked);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}