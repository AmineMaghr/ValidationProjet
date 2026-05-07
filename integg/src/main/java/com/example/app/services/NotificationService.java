package com.example.app.services;

import com.example.app.dao.FavorisDAO;
import com.example.app.dao.UserDAO;
import com.example.app.entities.User;
import java.sql.SQLException;
import java.util.*;

public class NotificationService {

    private FavorisDAO favorisDAO;
    private UserDAO userDAO;
    private EmailService emailService;

    public NotificationService() {
        this.favorisDAO = new FavorisDAO();
        this.userDAO = new UserDAO();
        this.emailService = new EmailService();
    }

    /**
     * Notifie tous les utilisateurs qui ont un type d'œuvre en favoris
     * quand une nouvelle œuvre du même type est ajoutée
     */
    public void notifierNouvelleOeuvreParType(String titreOeuvre, String typeOeuvre, int createurId) {
        try {
            System.out.println("=== [NOTIFICATION] Vérification pour nouvelle œuvre: " + titreOeuvre + " (type: " + typeOeuvre + ")");
            
            // Récupérer tous les utilisateurs (sauf le créateur)
            List<User> allUsers = userDAO.select();
            
            for (User user : allUsers) {
                if (user.getId() == createurId) continue;
                
                try {
                    List<String> favoriteTypes = favorisDAO.findFavoriteOeuvreTypesByUser(user.getId());
                    
                    if (favoriteTypes.contains(typeOeuvre)) {
                        System.out.println("  - Utilisateur " + user.getUsername() + " a ce type en favori!");
                        
                        boolean dejaNotifie = favorisDAO.isNotificationAlreadySent(user.getId(), titreOeuvre, "oeuvre", typeOeuvre);
                        
                        if (!dejaNotifie) {
                            emailService.envoyerNotificationNouvelElementSimilaire(
                                user.getEmail(),
                                user.getPrenom() + " " + user.getNom(),
                                titreOeuvre,
                                typeOeuvre,
                                "œuvre"
                            );
                            
                            favorisDAO.enregistrerNotificationEnvoyee(user.getId(), titreOeuvre, "oeuvre", typeOeuvre);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur pour utilisateur " + user.getId() + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur notification œuvre: " + e.getMessage());
        }
    }

    /**
     * Notifie tous les utilisateurs qui ont un type d'artefact en favoris
     * quand un nouvel artefact du même type est ajouté
     */
    public void notifierNouvelArtefactParType(String nomArtefact, String typeArtefact, int createurId) {
        try {
            System.out.println("=== [NOTIFICATION] Vérification pour nouvel artefact: " + nomArtefact + " (type: " + typeArtefact + ")");
            
            List<User> allUsers = userDAO.select();
            
            for (User user : allUsers) {
                if (user.getId() == createurId) continue;
                
                try {
                    List<String> favoriteTypes = favorisDAO.findFavoriteArtefactTypesByUser(user.getId());
                    
                    if (favoriteTypes.contains(typeArtefact)) {
                        System.out.println("  - Utilisateur " + user.getUsername() + " a ce type en favori!");
                        
                        boolean dejaNotifie = favorisDAO.isNotificationAlreadySent(user.getId(), nomArtefact, "artefact", typeArtefact);
                        
                        if (!dejaNotifie) {
                            emailService.envoyerNotificationNouvelElementSimilaire(
                                user.getEmail(),
                                user.getPrenom() + " " + user.getNom(),
                                nomArtefact,
                                typeArtefact,
                                "artefact"
                            );
                            
                            favorisDAO.enregistrerNotificationEnvoyee(user.getId(), nomArtefact, "artefact", typeArtefact);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur pour utilisateur " + user.getId() + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur notification artefact: " + e.getMessage());
        }
    }
}