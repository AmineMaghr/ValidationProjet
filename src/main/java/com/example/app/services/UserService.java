package com.example.app.services;

import com.example.app.entities.User;
import com.example.app.dao.UserDAO;
import com.example.app.utils.PasswordHasher;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserService implements IService<User> {

    private UserDAO userDAO;
    private PasswordHasher passwordHasher;

    public UserService() {
        this.userDAO = new UserDAO();
        this.passwordHasher = new PasswordHasher();
    }

    @Override
    public void add(User user) throws SQLException {
        if (!user.isValid()) {
            throw new IllegalArgumentException("Données utilisateur invalides: " + user.getValidationErrorsAsString());
        }

        if (userDAO.isUsernameTaken(user.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
        }
        if (userDAO.isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée");
        }

        if ("local".equals(user.getAuthProvider()) && user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordHasher.hash(user.getPassword()));
        }

        userDAO.add(user);
    }

    @Override
    public void update(User user) throws SQLException {
        userDAO.update(user);
    }

    @Override
    public void delete(int id) throws SQLException {
        userDAO.delete(id);
    }

    @Override
    public List<User> select() throws SQLException {
        return userDAO.select();
    }

    public User findById(int id) throws SQLException {
        return userDAO.findById(id);
    }
    
    public User findByUsername(String username) throws SQLException {
        return userDAO.findByUsername(username);
    }

    public User findByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }
    
    public User findByGoogleId(String googleId) throws SQLException {
        return userDAO.findByGoogleId(googleId);
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        return userDAO.isUsernameTaken(username);
    }

    public boolean isEmailTaken(String email) throws SQLException {
        return userDAO.isEmailTaken(email);
    }

    public List<String> generateUsernameSuggestions(String baseUsername) throws SQLException {
        return userDAO.generateUsernameSuggestions(baseUsername);
    }

    public boolean authenticate(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            user = userDAO.findByEmail(username);
        }

        if (user != null && !user.isBlocked()) {
            return passwordHasher.verify(password, user.getPassword()) || user.getPassword().equals(password);
        }

        return false;
    }

    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            user = userDAO.findByEmail(username);
        }

        if (user != null && !user.isBlocked()) {
            if (user.getPassword() != null && (passwordHasher.verify(password, user.getPassword()) || user.getPassword().equals(password))) {
                return user;
            }
        }

        return null;
    }
    
    public void updateGoogleId(int userId, String googleId, String provider) throws SQLException {
        userDAO.updateGoogleId(userId, googleId, provider);
    }

    // ✅ MÉTHODE updatePassword CORRIGÉE
    public void updatePassword(int userId, String newPassword) throws SQLException {
        System.out.println("📝 UserService.updatePassword - Mot de passe reçu: " + newPassword);
        String hashedPassword = passwordHasher.hash(newPassword);
        System.out.println("📝 Hash généré: " + hashedPassword);
        userDAO.updatePassword(userId, hashedPassword);
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        User user = findById(userId);
        if (user != null && passwordHasher.verify(oldPassword, user.getPassword())) {
            updatePassword(userId, newPassword);
            return true;
        }
        return false;
    }

    public List<User> searchUsers(String query) throws SQLException {
        return userDAO.select().stream()
            .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        u.getPrenom().toLowerCase().contains(query.toLowerCase()) ||
                        u.getNom().toLowerCase().contains(query.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<User> searchUsersAdmin(String search, java.time.LocalDate start, java.time.LocalDate end, String sort, String direction) throws SQLException {
        return userDAO.select().stream()
            .filter(u -> {
                if (search != null && !search.isEmpty()) {
                    return u.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                           u.getEmail().toLowerCase().contains(search.toLowerCase());
                }
                return true;
            })
            .sorted((u1, u2) -> {
                int cmp = 0;
                switch (sort) {
                    case "username" -> cmp = u1.getUsername().compareTo(u2.getUsername());
                    case "lastName" -> cmp = u1.getNom().compareTo(u2.getNom());
                    case "firstName" -> cmp = u1.getPrenom().compareTo(u2.getPrenom());
                    default -> cmp = u1.getCreatedAt().compareTo(u2.getCreatedAt());
                }
                return "desc".equals(direction) ? -cmp : cmp;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    public void toggleUserBlock(int userId, boolean blocked) throws SQLException {
        userDAO.updateUserBlockStatus(userId, blocked);
    }

    // ==================== RESET PASSWORD METHODS ====================
    
    public void saveResetToken(int userId, String token, LocalDateTime expiry) throws SQLException {
        userDAO.saveResetToken(userId, token, expiry);
    }

    public void saveResetCode(int userId, String code, LocalDateTime expiry) throws SQLException {
        userDAO.saveResetCode(userId, code, expiry);
    }

    public User findByResetToken(String token) throws SQLException {
        return userDAO.findByResetToken(token);
    }

    public User findByResetCode(String code) throws SQLException {
        return userDAO.findByResetCode(code);
    }

    public void clearResetToken(int userId) throws SQLException {
        userDAO.clearResetToken(userId);
    }

    public User findByPhoneNumber(String phone) throws SQLException {
        return userDAO.findByPhoneNumber(phone);
    }
    // ==================== FACE RECOGNITION METHODS ====================

public List<User> getUsersWithFaceDescriptors() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT id, username, email, face_descriptor, face_enabled FROM user WHERE face_descriptor IS NOT NULL AND face_descriptor != ''";
    
    try (java.sql.Statement stmt = userDAO.getConnection().createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setFaceDescriptor(rs.getString("face_descriptor"));
            user.setFaceEnabled(rs.getBoolean("face_enabled"));
            users.add(user);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return users;
}
    // ==================== SAUVEGARDE DESCRIPTEUR FACIAL ====================
    
    public void saveFaceDescriptor(int userId, String descriptor) {
        String sql = "UPDATE user SET face_descriptor = ?, face_enabled = 1 WHERE id = ?";
        
        try (java.sql.PreparedStatement pstmt = userDAO.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, descriptor);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("✓ Descripteur facial sauvegardé pour l'utilisateur " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteUserById(int id, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserById'");
    }
}