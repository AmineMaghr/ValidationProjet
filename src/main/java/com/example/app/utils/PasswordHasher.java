package com.example.app.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public String hash(String password) {
        try {
            // Générer un sel aléatoire
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            // Hasher le mot de passe
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Concaténer sel + hash et encoder en Base64
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hashage", e);
        }
    }

    public boolean verify(String plainPassword, String hashedPassword) {
        try {
            // Décoder le hash stocké
            byte[] combined = Base64.getDecoder().decode(hashedPassword);

            if (combined.length < SALT_LENGTH + 32) { // Minimum pour SHA-256 hash
                return false; // Pas un hash valide
            }

            // Extraire le sel et le hash original
            byte[] salt = new byte[SALT_LENGTH];
            byte[] originalHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, originalHash, 0, originalHash.length);

            // Hasher le mot de passe fourni avec le même sel
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] newHash = md.digest(plainPassword.getBytes());

            // Comparer les hash
            return MessageDigest.isEqual(originalHash, newHash);
        } catch (Exception e) {
            // Si le hash n'est pas au format attendu (ex: mot de passe en clair), retourner false
            return false;
        }
    }
}