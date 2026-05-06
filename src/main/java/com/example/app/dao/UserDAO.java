package com.example.app.dao;

import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class UserDAO implements IDAO<User> {

    protected Connection connection;

    public UserDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    // ==================== CRUD ====================
    
    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO user (prenom, nom, username, email, password, role, avatar, bio, is_blocked, is_verified, phone_number, auth_provider, face_enabled, google_id, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

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
            ps.setString(14, user.getGoogleId());
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
        String sql = "UPDATE user SET prenom = ?, nom = ?, username = ?, email = ?, password = ?, role = ?, avatar = ?, bio = ?, is_blocked = ?, is_verified = ?, phone_number = ?, auth_provider = ?, face_enabled = ?, google_id = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getPrenom());
            ps.setString(2, user.getNom());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getAvatar());
            ps.setString(8, user.getBio());
            ps.setBoolean(9, user.isBlocked());
            ps.setBoolean(10, user.isVerified());
            ps.setString(11, user.getPhoneNumber());
            ps.setString(12, user.getAuthProvider());
            ps.setBoolean(13, user.isFaceEnabled());
            ps.setString(14, user.getGoogleId());
            ps.setInt(15, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY created_at DESC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    // ==================== RECHERCHES ====================
    
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public User findActiveByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ? AND is_blocked = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public User findByGoogleId(String googleId) throws SQLException {
        String sql = "SELECT * FROM user WHERE google_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, googleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public User findByPhoneNumber(String phone) throws SQLException {
        String sql = "SELECT * FROM user WHERE phone_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    // ==================== VERIFICATIONS ====================
    
    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ==================== RESET PASSWORD ====================
    
  public void saveResetToken(int userId, String token, LocalDateTime expiry) throws SQLException {
    System.out.println("📝 SAVE - userId: " + userId);
    System.out.println("📝 SAVE - token: '" + token + "'");
    System.out.println("📝 SAVE - expiry: " + expiry);
    
    String sql = "UPDATE user SET reset_token = ?, reset_token_expires_at = ? WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, token);
        ps.setTimestamp(2, Timestamp.valueOf(expiry));
        ps.setInt(3, userId);
        int rows = ps.executeUpdate();
        System.out.println("📝 SAVE - rows affected: " + rows);
        
        // Vérifier immédiatement
        String verifySql = "SELECT reset_token, reset_token_expires_at FROM user WHERE id = ?";
        try (PreparedStatement ps2 = connection.prepareStatement(verifySql)) {
            ps2.setInt(1, userId);
            ResultSet rs = ps2.executeQuery();
            if (rs.next()) {
                System.out.println("📝 VERIFY - token en base: '" + rs.getString("reset_token") + "'");
                System.out.println("📝 VERIFY - expiry en base: " + rs.getTimestamp("reset_token_expires_at"));
            }
        }
    }
}
   public User findByResetToken(String token) throws SQLException {
    System.out.println("🔍 Recherche token: '" + token + "'");
    
    // D'abord, chercher le token SANS condition d'expiration
    String checkSql = "SELECT * FROM user WHERE reset_token = ?";
    try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
        ps.setString(1, token);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Timestamp expiresAt = rs.getTimestamp("reset_token_expires_at");
            Timestamp now = new Timestamp(System.currentTimeMillis());
            System.out.println("📅 Expiration: " + expiresAt);
            System.out.println("📅 Maintenant: " + now);
            
            if (expiresAt != null && expiresAt.after(now)) {
                System.out.println("✅ Token valide (non expiré)");
                return mapResultSet(rs);
            } else {
                System.out.println("❌ Token trouvé MAIS EXPIRÉ!");
                return null;
            }
        } else {
            System.out.println("❌ Token non trouvé dans la base");
            
            // Afficher tous les tokens existants
            Statement st = connection.createStatement();
            ResultSet rs2 = st.executeQuery("SELECT id, email, reset_token FROM user WHERE reset_token IS NOT NULL");
            while (rs2.next()) {
                System.out.println("   - ID " + rs2.getInt("id") + ": " + rs2.getString("reset_token"));
            }
            return null;
        }
    }
}

    public void saveResetCode(int userId, String code, LocalDateTime expiry) throws SQLException {
        String sql = "UPDATE user SET reset_code = ?, reset_code_expires_at = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setTimestamp(2, Timestamp.valueOf(expiry));
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public User findByResetCode(String code) throws SQLException {
        String sql = "SELECT * FROM user WHERE reset_code = ? AND reset_code_expires_at > NOW()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public void clearResetToken(int userId) throws SQLException {
        String sql = "UPDATE user SET reset_token = NULL, reset_token_expires_at = NULL, reset_code = NULL, reset_code_expires_at = NULL WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ==================== ADMIN ====================
    
    public List<User> findAdmins() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = 'admin'";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public void updateUserBlockStatus(int userId, boolean blocked) throws SQLException {
        String sql = "UPDATE user SET is_blocked = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, blocked);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void updateGoogleId(int userId, String googleId, String provider) throws SQLException {
        String sql = "UPDATE user SET google_id = ?, auth_provider = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, googleId);
            ps.setString(2, provider);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ==================== SUGGESTIONS ====================
    
    public List<String> generateUsernameSuggestions(String baseUsername) throws SQLException {
        List<String> suggestions = new ArrayList<>();
        String[] suffixes = {"123", "42", "007", "2024", "2025", "2026", "_official", "_fan", "_legend", "_master"};

        for (String suffix : suffixes) {
            String suggestion = baseUsername + suffix;
            if (!isUsernameTaken(suggestion)) {
                suggestions.add(suggestion);
                if (suggestions.size() >= 5) break;
            }
        }

        for (int i = 1; i <= 10 && suggestions.size() < 5; i++) {
            String suggestion = baseUsername + i;
            if (!isUsernameTaken(suggestion) && !suggestions.contains(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    // ==================== SEARCH ====================
    
    public List<User> searchPublicUsers(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE username LIKE ? AND is_blocked = false LIMIT 10";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    public List<User> searchUsersApi(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, prenom, nom, avatar, created_at FROM user WHERE (username LIKE ? OR prenom LIKE ? OR nom LIKE ?) AND is_blocked = false ORDER BY username ASC LIMIT 10";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setNom(rs.getString("nom"));
                    user.setAvatar(rs.getString("avatar"));
                    list.add(user);
                }
            }
        }
        return list;
    }

    // ==================== MAP RESULT SET ====================
    
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
        user.setAuthProvider(rs.getString("auth_provider") != null ? rs.getString("auth_provider") : "local");
        user.setFaceEnabled(rs.getBoolean("face_enabled"));
        
        try { String googleId = rs.getString("google_id"); if (googleId != null) user.setGoogleId(googleId); } catch (SQLException e) {}
        try { String resetToken = rs.getString("reset_token"); if (resetToken != null) user.setResetToken(resetToken); } catch (SQLException e) {}
        try { Timestamp resetTokenExpiresAt = rs.getTimestamp("reset_token_expires_at"); if (resetTokenExpiresAt != null) user.setResetTokenExpiresAt(resetTokenExpiresAt.toLocalDateTime()); } catch (SQLException e) {}

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());

        try { Timestamp updatedAt = rs.getTimestamp("updated_at"); if (updatedAt != null) user.setUpdatedAt(updatedAt.toLocalDateTime()); } catch (SQLException e) {}

        return user;
    }
    public Connection getConnection() {
    return connection;
}
// ==================== RECONNAISSANCE FACIALE ====================

/**
 * Sauvegarder le descripteur facial d'un utilisateur
 */
public boolean saveFaceDescriptor(int userId, String faceDescriptor) {
    String sql = "UPDATE user SET face_descriptor = ?, face_enabled = 1 WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, faceDescriptor);
        stmt.setInt(2, userId);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("✓ Face descriptor sauvegardé pour l'utilisateur ID: " + userId);
            return true;
        }
    } catch (SQLException e) {
        System.err.println("❌ Erreur sauvegarde face descriptor: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

/**
 * Récupérer le descripteur facial d'un utilisateur
 */
public String getFaceDescriptor(int userId) {
    String sql = "SELECT face_descriptor FROM user WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("face_descriptor");
        }
    } catch (SQLException e) {
        System.err.println("❌ Erreur récupération face descriptor: " + e.getMessage());
    }
    return null;
}

/**
 * Vérifier si un utilisateur a la reconnaissance faciale activée
 */
public boolean isFaceEnabled(int userId) {
    String sql = "SELECT face_enabled FROM user WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("face_enabled");
        }
    } catch (SQLException e) {
        System.err.println("❌ Erreur vérification face_enabled: " + e.getMessage());
    }
    return false;
}

/**
 * Récupérer tous les utilisateurs qui ont la reconnaissance faciale activée
 * (avec leur descripteur facial)
 */
public List<User> getUsersWithFaceEnabled() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM user WHERE face_enabled = 1 AND face_descriptor IS NOT NULL";
    try (Statement stmt = connection.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            User user = mapResultSet(rs);
            // S'assurer que le face_descriptor est bien chargé
            user.setFaceDescriptor(rs.getString("face_descriptor"));
            users.add(user);
        }
        System.out.println("✓ " + users.size() + " utilisateur(s) avec reconnaissance faciale activée");
    } catch (SQLException e) {
        System.err.println("❌ Erreur récupération utilisateurs avec face: " + e.getMessage());
    }
    return users;
}

/**
 * Désactiver la reconnaissance faciale pour un utilisateur
 */
public boolean disableFace(int userId) {
    String sql = "UPDATE user SET face_enabled = 0, face_descriptor = NULL WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        int affected = stmt.executeUpdate();
        if (affected > 0) {
            System.out.println("✓ Reconnaissance faciale désactivée pour l'utilisateur ID: " + userId);
            return true;
        }
    } catch (SQLException e) {
        System.err.println("❌ Erreur désactivation face: " + e.getMessage());
    }
    return false;
}

/**
 * Mettre à jour le descripteur facial et activer la reconnaissance
 */
public boolean updateFaceDescriptor(int userId, String faceDescriptor) {
    String sql = "UPDATE user SET face_descriptor = ?, face_enabled = 1 WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, faceDescriptor);
        stmt.setInt(2, userId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("❌ Erreur mise à jour face descriptor: " + e.getMessage());
        return false;
    }
}
// ================= FACE LABEL =================

public User findByFaceLabel(int label) {
    String sql = "SELECT * FROM user WHERE face_label = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, label);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return mapResultSet(rs);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
}