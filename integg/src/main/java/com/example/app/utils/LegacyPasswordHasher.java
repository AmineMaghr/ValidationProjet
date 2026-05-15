package com.example.app.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Ancien PasswordHasher (SHA-256) - UNIQUEMENT pour la migration
 * Ne pas utiliser pour les nouveaux mots de passe
 * 
 * @deprecated Utiliser PasswordHasher (BCrypt) pour les nouveaux mots de passe
 */
@Deprecated
public class LegacyPasswordHasher {
    
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hash un mot de passe avec SHA-256 (ancienne méthode)
     * @deprecated Utiliser PasswordHasher.hash() à la place
     */
    @Deprecated
    public String hash(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de hashage legacy", e);
        }
    }
    
    /**
     * Vérifie un mot de passe contre un hash SHA-256 (ancienne méthode)
     * @deprecated Utiliser PasswordHasher.verify() à la place
     */
    @Deprecated
    public boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            
            if (combined.length < SALT_LENGTH + 32) {
                return false;
            }
            
            byte[] salt = new byte[SALT_LENGTH];
            byte[] originalHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, originalHash, 0, originalHash.length);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] newHash = md.digest(plainPassword.getBytes());
            
            return MessageDigest.isEqual(originalHash, newHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Vérifie si un hash semble être au format legacy
     */
    public boolean isLegacyFormat(String hash) {
        return hash != null && !hash.startsWith("$2") && hash.length() > 30;
    }
}