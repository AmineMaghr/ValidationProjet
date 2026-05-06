package com.example.app.services;

import com.example.app.dao.CommentaireDAO;
import com.example.app.dao.OeuvreDAO;
import com.example.app.dao.ArtefactDAO;
import com.example.app.dao.UserDAO;
import com.example.app.entities.Commentaire;
import com.example.app.entities.Oeuvre;
import com.example.app.entities.Artefact;
import com.example.app.entities.User;
import java.sql.SQLException;
import java.util.List;

public class CommentaireService implements IService<Commentaire> {

    private CommentaireDAO commentaireDAO;
    private OeuvreDAO oeuvreDAO;
    private ArtefactDAO artefactDAO;
    private UserDAO userDAO;
    private EmailService emailService;

    public CommentaireService() {
        this.commentaireDAO = new CommentaireDAO();
        this.oeuvreDAO = new OeuvreDAO();
        this.artefactDAO = new ArtefactDAO();
        this.userDAO = new UserDAO();
        this.emailService = new EmailService();
    }

    @Override
    public void add(Commentaire commentaire) throws SQLException {
        System.out.println("=========================================");
        System.out.println("💬 [COMMENTAIRE SERVICE] Ajout d'un commentaire");
        System.out.println("=========================================");
        System.out.println("  - Contenu: " + commentaire.getContenu());
        System.out.println("  - UserId: " + commentaire.getUserId());
        System.out.println("  - OeuvreId: " + commentaire.getOeuvreId());
        System.out.println("  - ArtefactId: " + commentaire.getArtefactId());
        
        commentaireDAO.add(commentaire);
        System.out.println("✅ [COMMENTAIRE SERVICE] Commentaire enregistré en base (ID: " + commentaire.getId() + ")");
        
        // Envoyer un email au propriétaire
        if (commentaire.getOeuvreId() > 0) {
            try {
                Oeuvre oeuvre = oeuvreDAO.findById(commentaire.getOeuvreId());
                if (oeuvre != null && oeuvre.getCreateurId() > 0) {
                    User proprietaire = userDAO.findById(oeuvre.getCreateurId());
                    User auteur = userDAO.findById(commentaire.getUserId());
                    
                    System.out.println("📧 [EMAIL] Tentative d'envoi au propriétaire de l'œuvre");
                    System.out.println("  - Propriétaire ID: " + oeuvre.getCreateurId());
                    System.out.println("  - Email propriétaire: " + (proprietaire != null ? proprietaire.getEmail() : "null"));
                    System.out.println("  - Auteur: " + (auteur != null ? auteur.getUsername() : "Inconnu"));
                    
                    if (proprietaire != null && proprietaire.getEmail() != null && !proprietaire.getEmail().isEmpty()) {
                        emailService.envoyerNotificationCommentaire(
                            proprietaire.getEmail(),
                            proprietaire.getPrenom() + " " + proprietaire.getNom(),
                            commentaire.getContenu(),
                            oeuvre.getTitle(),
                            "œuvre",
                            auteur != null ? auteur.getUsername() : "Un utilisateur"
                        );
                        System.out.println("✅ [EMAIL] Demande d'envoi effectuée pour " + proprietaire.getEmail());
                    } else {
                        System.err.println("❌ [EMAIL] Impossible d'envoyer - Email du propriétaire invalide");
                    }
                } else {
                    System.err.println("❌ [EMAIL] Œuvre non trouvée ou sans créateur");
                }
            } catch (Exception e) {
                System.err.println("❌ [EMAIL] Erreur lors de la préparation: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (commentaire.getArtefactId() > 0) {
            try {
                Artefact artefact = artefactDAO.findById(commentaire.getArtefactId());
                if (artefact != null && artefact.getCreatedBy() != null && artefact.getCreatedBy().getId() > 0) {
                    User proprietaire = userDAO.findById(artefact.getCreatedBy().getId());
                    User auteur = userDAO.findById(commentaire.getUserId());
                    
                    System.out.println("📧 [EMAIL] Tentative d'envoi au propriétaire de l'artefact");
                    System.out.println("  - Propriétaire ID: " + artefact.getCreatedBy().getId());
                    System.out.println("  - Email propriétaire: " + (proprietaire != null ? proprietaire.getEmail() : "null"));
                    
                    if (proprietaire != null && proprietaire.getEmail() != null && !proprietaire.getEmail().isEmpty()) {
                        emailService.envoyerNotificationCommentaire(
                            proprietaire.getEmail(),
                            proprietaire.getPrenom() + " " + proprietaire.getNom(),
                            commentaire.getContenu(),
                            artefact.getName(),
                            "artefact",
                            auteur != null ? auteur.getUsername() : "Un utilisateur"
                        );
                        System.out.println("✅ [EMAIL] Demande d'envoi effectuée pour " + proprietaire.getEmail());
                    } else {
                        System.err.println("❌ [EMAIL] Impossible d'envoyer - Email du propriétaire invalide");
                    }
                } else {
                    System.err.println("❌ [EMAIL] Artefact non trouvé ou sans créateur");
                }
            } catch (Exception e) {
                System.err.println("❌ [EMAIL] Erreur lors de la préparation: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=========================================");
        System.out.println("✅ [COMMENTAIRE SERVICE] Traitement terminé");
        System.out.println("=========================================");
    }

    @Override
    public void update(Commentaire commentaire) throws SQLException {
        commentaireDAO.update(commentaire);
    }

    @Override
    public void delete(int id) throws SQLException {
        System.out.println("🗑️ [COMMENTAIRE SERVICE] Suppression du commentaire ID: " + id);
        commentaireDAO.delete(id);
    }

    @Override
    public List<Commentaire> select() throws SQLException {
        return commentaireDAO.select();
    }

    public List<Commentaire> findByOeuvre(int oeuvreId) throws SQLException {
        List<Commentaire> comments = commentaireDAO.findByOeuvre(oeuvreId);
        System.out.println("🔍 [COMMENTAIRE SERVICE] " + comments.size() + " commentaires trouvés pour œuvre ID: " + oeuvreId);
        return comments;
    }

    public List<Commentaire> findByArtefact(int artefactId) throws SQLException {
        List<Commentaire> comments = commentaireDAO.findByArtefact(artefactId);
        System.out.println("🔍 [COMMENTAIRE SERVICE] " + comments.size() + " commentaires trouvés pour artefact ID: " + artefactId);
        return comments;
    }
    
    public boolean peutSupprimerCommentaire(int userId, int commentaireId) throws SQLException {
        return commentaireDAO.isProprietaireDeLElement(userId, commentaireId);
    }
}