package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.entities.Commentaire;
import com.example.app.services.OeuvreService;
import com.example.app.services.CommentaireService;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.File;
import java.util.List;

public class ShowOeuvreController extends BaseController {

    @FXML private ImageView imageView;
    @FXML private Text titleText;
    @FXML private Text authorText;
    @FXML private Text typeText;
    @FXML private Text dateText;
    @FXML private Text descriptionText;
    @FXML private Button modifyBtn;
    @FXML private Button deleteBtn;
    
    @FXML private TextArea commentArea;
    @FXML private Button addCommentBtn;
    @FXML private ListView<Commentaire> commentList;

    private Oeuvre oeuvre;
    private OeuvreService oeuvreService = new OeuvreService();
    private CommentaireService commentaireService = new CommentaireService();

    @FXML
    public void initialize() {
        oeuvre = OeuvreController.getSelectedOeuvreForShow();
        if (oeuvre != null) {
            displayOeuvreDetails();
            loadComments();
            commentList.setCellFactory(param -> new CommentCell());
            
            // Afficher les boutons seulement pour le propriétaire
            if (UserSession.isLoggedIn()) {
                boolean isOwner = oeuvre.getCreateurId() == UserSession.getCurrentUser().getId();
                boolean isAdmin = UserSession.getCurrentUser().isAdmin();
                modifyBtn.setVisible(isOwner || isAdmin);
                deleteBtn.setVisible(isOwner || isAdmin);
            } else {
                modifyBtn.setVisible(false);
                deleteBtn.setVisible(false);
            }
            
            System.out.println("=== [COMMENTAIRES] Page œuvre chargée: " + oeuvre.getTitle());
        } else {
            showAlert("Erreur", "Aucune œuvre sélectionnée");
            handleBack();
        }
    }

    private void displayOeuvreDetails() {
        titleText.setText(oeuvre.getTitle());
        authorText.setText("✍️ " + oeuvre.getAuthor());
        typeText.setText("🏷️ " + oeuvre.getType());
        dateText.setText("📅 " + (oeuvre.getDatePublication() != null ? oeuvre.getDatePublication().toString() : "Date inconnue"));
        descriptionText.setText(oeuvre.getDescription() != null ? oeuvre.getDescription() : "Aucune description");

        if (oeuvre.getImageUrl() != null && !oeuvre.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File(oeuvre.getImageUrl());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), 400, 400, true, true);
                    imageView.setImage(image);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement image: " + e.getMessage());
            }
        }
    }

    private void loadComments() {
        Task<List<Commentaire>> task = new Task<>() {
            @Override
            protected List<Commentaire> call() throws Exception {
                List<Commentaire> comments = commentaireService.findByOeuvre(oeuvre.getId());
                System.out.println("=== [COMMENTAIRES] " + comments.size() + " commentaires chargés pour l'œuvre ID " + oeuvre.getId());
                for (Commentaire c : comments) {
                    System.out.println("  - @" + c.getUsername() + ": " + c.getContenu());
                }
                return comments;
            }
        };
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                commentList.getItems().setAll(task.getValue());
            });
        });
        task.setOnFailed(e -> {
            System.err.println("=== [COMMENTAIRES] Erreur chargement: " + e.getSource().getException().getMessage());
        });
        new Thread(task).start();
    }

    @FXML
    private void addComment() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour commenter");
            navigateTo("/login");
            return;
        }

        String content = commentArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez écrire un commentaire");
            return;
        }

        System.out.println("=== [COMMENTAIRE] Ajout d'un commentaire par " + UserSession.getCurrentUser().getUsername());
        System.out.println("  - Contenu: " + content);
        System.out.println("  - Œuvre ID: " + oeuvre.getId());
        System.out.println("  - Propriétaire ID: " + oeuvre.getCreateurId());

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(content.trim());
        commentaire.setUserId(UserSession.getCurrentUser().getId());
        commentaire.setOeuvreId(oeuvre.getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                commentaireService.add(commentaire);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            commentArea.clear();
            loadComments();
            System.out.println("=== [COMMENTAIRE] Commentaire ajouté avec succès !");
            showAlert("Succès", "Commentaire ajouté. Le propriétaire sera notifié par email.");
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            System.err.println("=== [COMMENTAIRE] ERREUR: " + ex.getMessage());
            ex.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du commentaire: " + ex.getMessage());
        });
        new Thread(task).start();
    }

    @FXML
    private void handleModify() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour modifier");
            navigateTo("/login");
            return;
        }
        
        if (oeuvre.getCreateurId() != UserSession.getCurrentUser().getId() && !UserSession.getCurrentUser().isAdmin()) {
            showAlert("Permission refusée", "Vous n'êtes pas le propriétaire de cette œuvre");
            return;
        }
        
        OeuvreFormController.setEditingOeuvre(oeuvre);
        navigateTo("/oeuvre/create");
    }

    @FXML
    private void handleDelete() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour supprimer");
            navigateTo("/login");
            return;
        }
        
        if (oeuvre.getCreateurId() != UserSession.getCurrentUser().getId() && !UserSession.getCurrentUser().isAdmin()) {
            showAlert("Permission refusée", "Vous n'êtes pas le propriétaire de cette œuvre");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'œuvre");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement \"" + oeuvre.getTitle() + "\" ?\n\nCette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        oeuvreService.delete(oeuvre.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    showAlert("Succès", "Œuvre supprimée avec succès");
                    handleBack();
                });
                task.setOnFailed(e -> {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getSource().getException().getMessage());
                });
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void handleBack() {
        navigateTo("/oeuvre");
    }

    // ⭐ SUPPRESSION COMMENTAIRE - Uniquement le propriétaire de l'œuvre
    private void deleteComment(Commentaire commentaire) {
        if (!UserSession.isLoggedIn()) return;
        
        boolean isOwner = oeuvre.getCreateurId() == UserSession.getCurrentUser().getId();
        boolean isAdmin = UserSession.getCurrentUser().isAdmin();
        
        if (!isOwner && !isAdmin) {
            showAlert("Permission refusée", "Seul le propriétaire peut supprimer les commentaires");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer ce commentaire ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        commentaireService.delete(commentaire.getId());
                        System.out.println("=== [COMMENTAIRE] Commentaire ID " + commentaire.getId() + " supprimé par le propriétaire");
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    loadComments();
                    showAlert("Succès", "Commentaire supprimé");
                });
                new Thread(task).start();
            }
        });
    }

    // ⭐ CELLULE DES COMMENTAIRES
    private class CommentCell extends ListCell<Commentaire> {
        private VBox content;
        private Label userLabel;
        private Label dateLabel;
        private Label commentText;
        private Button deleteBtn;

        public CommentCell() {
            content = new VBox(8);
            content.setStyle("-fx-padding: 12; -fx-background-color: #1a2530; -fx-background-radius: 10; -fx-border-color: #2a3540; -fx-border-radius: 10;");
            
            userLabel = new Label();
            userLabel.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 13px; -fx-font-weight: bold;");
            
            dateLabel = new Label();
            dateLabel.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 10px;");
            
            commentText = new Label();
            commentText.setStyle("-fx-text-fill: #b0b9b6; -fx-font-size: 13px;");
            commentText.setWrapText(true);
            commentText.setMaxWidth(450);
            
            deleteBtn = new Button("🗑 Supprimer");
            deleteBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: #fff; -fx-background-radius: 15; -fx-padding: 5 15; -fx-cursor: hand; -fx-font-size: 11px;");
            
            HBox header = new HBox(10, userLabel, dateLabel);
            header.setAlignment(Pos.CENTER_LEFT);
            
            HBox footer = new HBox(deleteBtn);
            footer.setAlignment(Pos.CENTER_RIGHT);
            
            content.getChildren().addAll(header, commentText, footer);
        }

        @Override
        protected void updateItem(Commentaire commentaire, boolean empty) {
            super.updateItem(commentaire, empty);
            if (empty || commentaire == null) {
                setGraphic(null);
                return;
            }
            
            userLabel.setText("@" + (commentaire.getUsername() != null ? commentaire.getUsername() : "Utilisateur"));
            dateLabel.setText(commentaire.getFormattedDate());
            commentText.setText(commentaire.getContenu());
            
            // ⭐ Seul le PROPRIÉTAIRE peut supprimer les commentaires
            boolean isOwner = UserSession.isLoggedIn() && 
                (oeuvre.getCreateurId() == UserSession.getCurrentUser().getId() || 
                 UserSession.getCurrentUser().isAdmin());
            deleteBtn.setVisible(isOwner);
            deleteBtn.setOnAction(e -> deleteComment(commentaire));
            
            setGraphic(content);
        }
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}