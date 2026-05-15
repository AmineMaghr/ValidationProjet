package com.example.app.services;

import com.example.app.entities.User;
import com.example.app.dao.UserDAO;
import com.example.app.utils.PasswordHasher;
import com.example.app.utils.LegacyPasswordHasher;
import com.example.app.utils.LogUtil;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserService implements IService<User> {

    private UserDAO userDAO;
    private PasswordHasher passwordHasher;      // BCrypt (compatible Symfony)
    private LegacyPasswordHasher legacyHasher;  // SHA-256 (pour migration)

    public UserService() {
        this.userDAO = new UserDAO();
        this.passwordHasher = new PasswordHasher();     // BCrypt
        this.legacyHasher = new LegacyPasswordHasher(); // SHA-256 legacy
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

        // Hash avec BCrypt (compatible Symfony)
        if ("local".equals(user.getAuthProvider()) && user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hashedPassword = passwordHasher.hash(user.getPassword());
            user.setPassword(hashedPassword);
            LogUtil.debug("Mot de passe hashé avec BCrypt: " + hashedPassword.substring(0, Math.min(20, hashedPassword.length())) + "...");
        }

        userDAO.add(user);
        LogUtil.success("Utilisateur ajouté: " + user.getUsername());
    }

    @Override
    public void update(User user) throws SQLException {
        userDAO.update(user);
        LogUtil.info("Utilisateur mis à jour: " + user.getUsername());
    }

    @Override
    public void delete(int id) throws SQLException {
        userDAO.delete(id);
        LogUtil.info("Utilisateur supprimé (ID: " + id + ")");
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

    /**
     * Authentification compatible Symfony (BCrypt) + Migration des anciens hashs
     */
    public boolean authenticate(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            user = userDAO.findByEmail(username);
        }

        if (user == null) {
            LogUtil.warning("Utilisateur non trouvé: " + username);
            return false;
        }

        if (user.isBlocked()) {
            LogUtil.warning("Tentative de connexion sur compte bloqué: " + username);
            return false;
        }

        String storedHash = user.getPassword();
        if (storedHash == null || storedHash.isEmpty()) {
            LogUtil.warning("Aucun mot de passe défini pour: " + username);
            return false;
        }

        // 1. Essayer avec BCrypt (Symfony / nouveau format)
        if (passwordHasher.verify(password, storedHash)) {
            LogUtil.success("Authentification BCrypt réussie pour: " + username);
            return true;
        }

        // 2. Essayer avec l'ancien hash SHA-256 (migration)
        if (legacyHasher.verify(password, storedHash)) {
            LogUtil.info("🔄 Migration du mot de passe de SHA-256 vers BCrypt pour: " + username);
            
            // Migrer vers BCrypt
            String newBcryptHash = passwordHasher.hash(password);
            userDAO.updatePassword(user.getId(), newBcryptHash);
            LogUtil.success("✅ Mot de passe migré avec succès pour: " + username);
            return true;
        }

        // 3. Vérification pour les mots de passe en clair (cas très rare, sécurité faible)
        if (storedHash.equals(password)) {
            LogUtil.warning("⚠️ Mot de passe en clair détecté pour: " + username + " - Migration vers BCrypt");
            String newBcryptHash = passwordHasher.hash(password);
            userDAO.updatePassword(user.getId(), newBcryptHash);
            return true;
        }

        LogUtil.warning("Mot de passe incorrect pour: " + username);
        return false;
    }

    /**
     * Connexion avec récupération de l'utilisateur (compatible Symfony)
     */
    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            user = userDAO.findByEmail(username);
        }

        if (user == null) {
            LogUtil.warning("Utilisateur non trouvé: " + username);
            return null;
        }

        if (user.isBlocked()) {
            LogUtil.warning("Tentative de connexion sur compte bloqué: " + username);
            return null;
        }

        String storedHash = user.getPassword();
        if (storedHash == null || storedHash.isEmpty()) {
            return null;
        }

        // 1. Vérification BCrypt
        if (passwordHasher.verify(password, storedHash)) {
            LogUtil.success("Connexion BCrypt réussie: " + user.getUsername());
            return user;
        }

        // 2. Migration SHA-256 → BCrypt
        if (legacyHasher.verify(password, storedHash)) {
            LogUtil.info("🔄 Migration SHA-256 → BCrypt pour: " + username);
            String newBcryptHash = passwordHasher.hash(password);
            userDAO.updatePassword(user.getId(), newBcryptHash);
            LogUtil.success("✅ Migration réussie, connexion établie");
            return user;
        }

        // 3. Mots de passe en clair (legacy)
        if (storedHash.equals(password)) {
            LogUtil.warning("⚠️ Migration mot de passe en clair → BCrypt pour: " + username);
            String newBcryptHash = passwordHasher.hash(password);
            userDAO.updatePassword(user.getId(), newBcryptHash);
            return user;
        }

        return null;
    }
    
    public void updateGoogleId(int userId, String googleId, String provider) throws SQLException {
        userDAO.updateGoogleId(userId, googleId, provider);
        LogUtil.info("Google ID mis à jour pour l'utilisateur ID: " + userId);
    }

    /**
     * Mise à jour du mot de passe avec hash BCrypt
     */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        
        LogUtil.info("📝 Mise à jour du mot de passe pour l'utilisateur ID: " + userId);
        String hashedPassword = passwordHasher.hash(newPassword);
        LogUtil.debug("Hash BCrypt généré: " + hashedPassword.substring(0, Math.min(20, hashedPassword.length())) + "...");
        userDAO.updatePassword(userId, hashedPassword);
        LogUtil.success("✅ Mot de passe mis à jour avec succès");
    }

    /**
     * Changement de mot de passe avec vérification de l'ancien
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        User user = findById(userId);
        if (user == null) {
            LogUtil.warning("Utilisateur non trouvé pour changement de mot de passe");
            return false;
        }
        
        // Vérifier l'ancien mot de passe (supporte BCrypt et legacy)
        boolean oldPasswordValid = passwordHasher.verify(oldPassword, user.getPassword()) ||
                                   legacyHasher.verify(oldPassword, user.getPassword()) ||
                                   user.getPassword().equals(oldPassword);
        
        if (oldPasswordValid) {
            updatePassword(userId, newPassword);
            LogUtil.success("Mot de passe changé avec succès pour: " + user.getUsername());
            return true;
        }
        
        LogUtil.warning("Ancien mot de passe incorrect pour: " + user.getUsername());
        return false;
    }

    /**
     * Vérifie si un hash est au format BCrypt (Symfony)
     */
    public boolean isBcryptHash(String hash) {
        return hash != null && hash.matches("^\\$2[aby]\\$\\d{2}\\$[.\\/A-Za-z0-9]{53}$");
    }

    /**
     * Vérifie si un hash est au format SHA-256 legacy
     */
    public boolean isLegacyHash(String hash) {
        // Les hashs legacy sont en Base64 et ne commencent pas par $2
        return hash != null && !hash.startsWith("$2") && hash.length() > 30;
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
        LogUtil.info("Utilisateur ID " + userId + " " + (blocked ? "bloqué" : "débloqué"));
    }

    // ==================== RESET PASSWORD METHODS ====================
    
    public void saveResetToken(int userId, String token, LocalDateTime expiry) throws SQLException {
        userDAO.saveResetToken(userId, token, expiry);
        LogUtil.debug("Token de réinitialisation sauvegardé pour ID: " + userId);
    }

    public void saveResetCode(int userId, String code, LocalDateTime expiry) throws SQLException {
        userDAO.saveResetCode(userId, code, expiry);
        LogUtil.debug("Code de réinitialisation sauvegardé pour ID: " + userId);
    }

    public User findByResetToken(String token) throws SQLException {
        return userDAO.findByResetToken(token);
    }

    public User findByResetCode(String code) throws SQLException {
        return userDAO.findByResetCode(code);
    }

    public void clearResetToken(int userId) throws SQLException {
        userDAO.clearResetToken(userId);
        LogUtil.debug("Token de réinitialisation effacé pour ID: " + userId);
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
            LogUtil.info("Chargement de " + users.size() + " utilisateurs avec descripteurs faciaux");
        } catch (SQLException e) {
            LogUtil.error("Erreur lors du chargement des descripteurs faciaux", e);
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
            LogUtil.success("✓ Descripteur facial sauvegardé pour l'utilisateur " + userId);
        } catch (SQLException e) {
            LogUtil.error("Erreur lors de la sauvegarde du descripteur facial", e);
        }
    }

    /**
     * Supprimer un utilisateur avec vérification du mot de passe
     */
    public boolean deleteUserById(int id, String password) throws SQLException {
        User user = findById(id);
        if (user == null) {
            LogUtil.warning("Utilisateur non trouvé pour suppression: ID " + id);
            return false;
        }
        
        // Vérifier le mot de passe avant suppression
        boolean passwordValid = passwordHasher.verify(password, user.getPassword()) ||
                               legacyHasher.verify(password, user.getPassword()) ||
                               user.getPassword().equals(password);
        
        if (passwordValid) {
            delete(id);
            LogUtil.success("Utilisateur supprimé: " + user.getUsername());
            return true;
        }
        
        LogUtil.warning("Tentative de suppression avec mot de passe incorrect pour: " + user.getUsername());
        return false;
    }

    /**
     * Migration massive de tous les anciens hashs SHA-256 vers BCrypt
     * À exécuter ponctuellement pour migrer tous les utilisateurs d'un coup
     */
    public int migrateAllLegacyPasswords() throws SQLException {
        List<User> allUsers = select();
        int migratedCount = 0;
        
        for (User user : allUsers) {
            String hash = user.getPassword();
            if (hash != null && !hash.isEmpty() && !isBcryptHash(hash)) {
                // Impossible de migrer sans connaître le mot de passe en clair
                // On ne peut que marquer ces comptes pour migration au prochain login
                LogUtil.warning("Compte à migrer au prochain login: " + user.getUsername());
            }
        }
        
        LogUtil.info("Migration massive terminée. " + migratedCount + " comptes pré-marqués.");
        return migratedCount;
    }
}