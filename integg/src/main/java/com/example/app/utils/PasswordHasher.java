package com.example.app.utils;

import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;

/**
 * Hashage compatible avec Symfony (bcrypt)
 * Symfony utilise l'algorithme "auto" qui est bcrypt par défaut
 */
public class PasswordHasher {
    
    private static final int BCRYPT_COST = 13; // Même coût que Symfony par défaut
    
    /**
     * Hash un mot de passe avec bcrypt (compatible Symfony)
     * @param plainPassword Mot de passe en clair
     * @return Hash bcrypt (ex: $2y$13$...)
     */
    public String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }
    
    /**
     * Vérifie un mot de passe contre un hash bcrypt
     * Compatible avec les hashs générés par Symfony
     * @param plainPassword Mot de passe en clair
     * @param hashedPassword Hash bcrypt (peut venir de Symfony ou JavaFX)
     * @return true si le mot de passe correspond
     */
    public boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        
        try {
            // Vérifier si c'est un hash bcrypt valide
            if (!isBcryptHash(hashedPassword)) {
                // Si c'est un ancien hash SHA-256 (migration), on peut essayer de le convertir
                return false;
            }
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            LogUtil.error("Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }
    
    /**
     * Vérifie si un hash est au format bcrypt
     * Format bcrypt: $2a$, $2b$, $2y$ + coût (2 chiffres) + $ + 53 caractères
     */
    private boolean isBcryptHash(String hash) {
        Pattern bcryptPattern = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[.\\/A-Za-z0-9]{53}$");
        return bcryptPattern.matcher(hash).matches();
    }
    
    /**
     * Méthode utilitaire pour vérifier la force du mot de passe
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // Au moins une majuscule, une minuscule, un chiffre
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    }
}