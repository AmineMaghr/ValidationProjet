package com.example.app.utils;

import com.example.app.exceptions.AppException;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utilitaire de validation pour les formulaires
 * Fournit des méthodes statiques pour valider les données utilisateur
 */
public class ValidationUtil {
    
    // Patterns regex
    private static final Pattern EMAIL_PATTERN =
        // On accepte les emails classiques + les domaines sans forcément imposer un TLD (ex: xxx@local.mydomain)
        // mais on exige un '@' et au moins un '.' dans la partie domaine.
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.)+[A-Za-z0-9-]+$");

    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");
    
    /**
     * Valide qu'une chaîne n'est pas vide
     */
    public static void validateNotEmpty(String value, String fieldName) throws AppException {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException.ValidationError(fieldName, "Ce champ est obligatoire");
        }
    }
    
    /**
     * Valide qu'une chaîne a une longueur minimum
     */
    public static void validateMinLength(String value, int minLength, String fieldName) throws AppException {
        validateNotEmpty(value, fieldName);
        if (value.length() < minLength) {
            throw new AppException.ValidationError(fieldName, 
                "Minimum " + minLength + " caractères requis");
        }
    }
    
    /**
     * Valide qu'une chaîne a une longueur maximum
     */
    public static void validateMaxLength(String value, int maxLength, String fieldName) throws AppException {
        if (value != null && value.length() > maxLength) {
            throw new AppException.ValidationError(fieldName, 
                "Maximum " + maxLength + " caractères autorisés");
        }
    }
    
    /**
     * Valide un email
     */
    public static void validateEmail(String email) throws AppException {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new AppException.ValidationError("Email", "Format d'email invalide");
        }
    }
    
    /**
     * Valide un nom d'utilisateur
     */
    public static void validateUsername(String username) throws AppException {
        validateNotEmpty(username, "Nom d'utilisateur");
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new AppException.ValidationError("Nom d'utilisateur", 
                "Doit contenir 3-20 caractères alphanumériques, tirets ou underscores");
        }
    }
    
    /**
     * Valide un mot de passe
     * Minimum 8 caractères, au moins 1 lettre et 1 chiffre
     */
    public static void validatePassword(String password) throws AppException {
        validateNotEmpty(password, "Mot de passe");
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new AppException.ValidationError("Mot de passe", 
                "Minimum 8 caractères, 1 lettre et 1 chiffre requis");
        }
    }
    
    /**
     * Valide que deux chaînes sont identiques
     */
    public static void validateMatch(String value1, String value2, String fieldName) throws AppException {
        if (!value1.equals(value2)) {
            throw new AppException.ValidationError(fieldName, "Les valeurs ne correspondent pas");
        }
    }
    
    /**
     * Valide les dates d'un défi
     */
    public static void validateDefiDates(LocalDate dateDebut, LocalDate dateFin) throws AppException {
        if (dateDebut == null) {
            throw new AppException.ValidationError("Date de début", "La date de début est requise");
        }
        if (dateFin == null) {
            throw new AppException.ValidationError("Date de fin", "La date de fin est requise");
        }
        
        if (dateDebut.isAfter(dateFin)) {
            throw new AppException.ValidationError("Dates", 
                "La date de début doit être antérieure à la date de fin");
        }
        
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new AppException.ValidationError("Date de début", 
                "La date de début ne peut pas être dans le passé");
        }
    }
    
    /**
     * Valide un titre de défi
     */
    public static void validateDefiTitre(String titre) throws AppException {
        validateNotEmpty(titre, "Titre");
        validateMinLength(titre, 5, "Titre");
        validateMaxLength(titre, 255, "Titre");
    }
    
    /**
     * Valide une description de défi
     */
    public static void validateDefiDescription(String description) throws AppException {
        if (description != null && description.length() > 2000) {
            throw new AppException.ValidationError("Description", 
                "La description ne peut pas dépasser 2000 caractères");
        }
    }
    
    /**
     * Valide un thème de défi
     */
    public static void validateDefiTheme(String theme) throws AppException {
        validateNotEmpty(theme, "Thème");
        String[] validThemes = {"Environnement", "Santé", "Art", "Programmation", "Sport", "Autre"};
        for (String valid : validThemes) {
            if (valid.equals(theme)) return;
        }
        throw new AppException.ValidationError("Thème", "Thème invalide");
    }
    
    /**
     * Valide une difficulté
     */
    public static void validateDifficulty(String difficulty) throws AppException {
        validateNotEmpty(difficulty, "Difficulté");
        if (!difficulty.matches("FACILE|MOYEN|DIFFICILE")) {
            throw new AppException.ValidationError("Difficulté", "Difficulté invalide");
        }
    }
    
    /**
     * Valide un statut
     */
    public static void validateStatut(String statut) throws AppException {
        validateNotEmpty(statut, "Statut");
        if (!statut.matches("OUVERT|FERME|TERMINE|PLANIFIE")) {
            throw new AppException.ValidationError("Statut", "Statut invalide");
        }
    }
    
    /**
     * Valide un défi complet
     */
    public static void validateDefi(String titre, String description, String theme, 
                                    LocalDate dateDebut, LocalDate dateFin, 
                                    String difficulty, String statut) throws AppException {
        validateDefiTitre(titre);
        validateDefiDescription(description);
        validateDefiTheme(theme);
        validateDefiDates(dateDebut, dateFin);
        validateDifficulty(difficulty);
        validateStatut(statut);
    }
}

