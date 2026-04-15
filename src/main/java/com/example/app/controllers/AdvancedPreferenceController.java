package com.example.app.controllers;

import com.example.app.services.AdvancedPreferenceService;
import com.example.app.entities.AdvancedPreference;
import com.example.app.utils.SessionManager;
import java.sql.SQLException;

public class AdvancedPreferenceController {

    private final AdvancedPreferenceService service = new AdvancedPreferenceService();

    // GET - Retourne un objet AdvancedPreference au lieu de ResultSet
    public AdvancedPreference getPreferences(int userId) {
        try {
            return service.findByUserId(userId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des préférences: " + e.getMessage());
            return null;
        }
    }

    // CREATE or UPDATE
    public void savePreferences(
            int userId,
            String desc,
            String genre,
            int affinity,
            String themes,
            String tags
    ) {
        try {
            AdvancedPreference existing = service.findByUserId(userId);

            if (existing != null) {
                // UPDATE
                service.updatePreference(existing.getId(), desc, genre, affinity, themes, tags);
            } else {
                // CREATE
                service.createPreference(userId, desc, genre, affinity, themes, tags);
            }

            System.out.println("Préférences sauvegardées avec succès");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde des préférences: " + e.getMessage());
        }
    }

    // DELETE
    public void deletePreferences(int userId) {
        try {
            service.deleteByUserId(userId);
            System.out.println("Préférences supprimées");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des préférences: " + e.getMessage());
        }
    }

    // Vérifier si l'utilisateur a des préférences
    public boolean hasPreferences(int userId) {
        try {
            return service.hasPreferences(userId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des préférences: " + e.getMessage());
            return false;
        }
    }

    // Créer des préférences par défaut pour un utilisateur
    public void createDefaultPreferences(int userId) {
        savePreferences(userId, "", "", 1, "", "");
    }

    // Obtenir les préférences de l'utilisateur connecté
    public AdvancedPreference getCurrentUserPreferences() {
        if (SessionManager.isLoggedIn()) {
            return getPreferences(SessionManager.getCurrentUser().getId());
        }
        return null;
    }

    // Sauvegarder les préférences de l'utilisateur connecté
    public void saveCurrentUserPreferences(String desc, String genre, int affinity, String themes, String tags) {
        if (SessionManager.isLoggedIn()) {
            savePreferences(SessionManager.getCurrentUser().getId(), desc, genre, affinity, themes, tags);
        }
    }
}
