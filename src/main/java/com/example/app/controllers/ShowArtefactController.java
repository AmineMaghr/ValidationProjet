package com.example.app.controllers;

import com.example.app.entities.Artefact;
import com.example.app.entities.Commentaire;
import com.example.app.services.ArtefactService;
import com.example.app.services.CommentaireService;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ShowArtefactController extends BaseController {

    @FXML
    private ImageView imageView;
    @FXML
    private Text nameText;
    @FXML
    private Text typeText;
    @FXML
    private Text universeText;
    @FXML
    private Text originsText;
    @FXML
    private Text powersText;
    @FXML
    private Text rarityText;
    @FXML
    private Text dateText;
    @FXML
    private Button modifyBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private TextArea commentArea;
    @FXML
    private Button addCommentBtn;
    @FXML
    private ListView<Commentaire> commentList;

    private Artefact artefact;
    private ArtefactService artefactService = new ArtefactService();
    private CommentaireService commentaireService = new CommentaireService();
    private File selectedImageFile;

    @FXML
    public void initialize() {
        artefact = ArtefactController.getSelectedArtefactForShow();
        if (artefact != null) {
            displayArtefactDetails();
            loadComments();
            commentList.setCellFactory(param -> new CommentCell());

            if (UserSession.isLoggedIn()) {
                boolean isOwner = artefact.getCreatedBy() != null &&
                        artefact.getCreatedBy().getId() == UserSession.getCurrentUser().getId();
                boolean isAdmin = UserSession.getCurrentUser().isAdmin();
                modifyBtn.setVisible(isOwner || isAdmin);
                deleteBtn.setVisible(isOwner || isAdmin);
            } else {
                modifyBtn.setVisible(false);
                deleteBtn.setVisible(false);
            }
        } else {
            showAlert("Erreur", "Aucun artefact sélectionné");
            handleBack();
        }
    }

    private void displayArtefactDetails() {
        nameText.setText(artefact.getName());
        typeText.setText("🏷️ " + artefact.getType());
        universeText.setText("🌌 " + artefact.getUniverse());
        originsText.setText(artefact.getOrigins());
        powersText.setText(artefact.getPowers());
        rarityText.setText("⭐ " + artefact.getRarity());
        dateText.setText(
                "📅 " + (artefact.getCreatedAt() != null ? artefact.getCreatedAt().toString() : "Date inconnue"));

        if (artefact.getImageUrl() != null && !artefact.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File(artefact.getImageUrl());
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
                return commentaireService.findByArtefact(artefact.getId());
            }
        };
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                commentList.getItems().setAll(task.getValue());
            });
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

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(content.trim());
        commentaire.setUserId(UserSession.getCurrentUser().getId());
        commentaire.setArtefactId(artefact.getId());
        // ⭐ CORRECTION: Supprimer setCreatedById

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
            showAlert("Succès", "Commentaire ajouté. Le propriétaire sera notifié par email.");
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Erreur lors de l'ajout du commentaire: " + (ex != null ? ex.getMessage() : "Unknown"));
            ex.printStackTrace();
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

        boolean isOwner = artefact.getCreatedBy() != null &&
                artefact.getCreatedBy().getId() == UserSession.getCurrentUser().getId();
        boolean isAdmin = UserSession.getCurrentUser().isAdmin();

        if (!isOwner && !isAdmin) {
            showAlert("Permission refusée", "Vous n'êtes pas le propriétaire de cet artefact");
            return;
        }

        // Créer une boîte de dialogue personnalisée pour la modification
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'artefact");
        dialog.setHeaderText("Modifier: " + artefact.getName());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #11161c;");

        TextField nameField = new TextField(artefact.getName());
        nameField.setStyle(
                "-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("armure", "arme", "bijoux", "relique", "outil magique");
        typeCombo.setValue(artefact.getType());
        typeCombo.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 5;");

        TextField universeField = new TextField(artefact.getUniverse());
        universeField.setStyle(
                "-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");

        TextArea originsArea = new TextArea(artefact.getOrigins());
        originsArea.setPrefHeight(100);
        originsArea.setWrapText(true);
        originsArea.setStyle(
                "-fx-background-color: #fff; -fx-text-fill: #000; -fx-padding: 10; -fx-background-radius: 8;");

        TextArea powersArea = new TextArea(artefact.getPowers());
        powersArea.setPrefHeight(100);
        powersArea.setWrapText(true);
        powersArea.setStyle(
                "-fx-background-color: #fff; -fx-text-fill: #000; -fx-padding: 10; -fx-background-radius: 8;");

        ComboBox<String> rarityCombo = new ComboBox<>();
        rarityCombo.getItems().addAll("Rare", "Épique", "Légendaire");
        rarityCombo.setValue(artefact.getRarity());
        rarityCombo.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 5;");

        Button imageBtn = new Button("Changer l'image");
        imageBtn.setStyle(
                "-fx-background-color: #2a3540; -fx-text-fill: #fff; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;");
        Label imageStatus = new Label("Image actuelle conservée");
        imageStatus.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 11px;");

        imageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif"));
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedImageFile = file;
                imageStatus.setText("✓ Nouvelle image: " + file.getName());
                imageStatus.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px;");
            }
        });

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Univers:"), 0, 2);
        grid.add(universeField, 1, 2);
        grid.add(new Label("Origines:"), 0, 3);
        grid.add(originsArea, 1, 3);
        grid.add(new Label("Pouvoirs:"), 0, 4);
        grid.add(powersArea, 1, 4);
        grid.add(new Label("Rareté:"), 0, 5);
        grid.add(rarityCombo, 1, 5);
        grid.add(new Label("Image:"), 0, 6);

        VBox imageBox = new VBox(5, imageBtn, imageStatus);
        grid.add(imageBox, 1, 6);

        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: #b0b9b6; -fx-font-weight: bold;");
            }
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #0a0c10;");

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            String newName = nameField.getText().trim();
            String newType = typeCombo.getValue();
            String newUniverse = universeField.getText().trim();
            String newOrigins = originsArea.getText().trim();
            String newPowers = powersArea.getText().trim();
            String newRarity = rarityCombo.getValue();

            StringBuilder errors = new StringBuilder();
            if (newName.isEmpty())
                errors.append("- Le nom est requis\n");
            if (newType == null)
                errors.append("- Le type est requis\n");
            if (newUniverse.isEmpty())
                errors.append("- L'univers est requis\n");
            if (newOrigins.isEmpty())
                errors.append("- Les origines sont requises\n");
            if (newPowers.isEmpty()) {
                errors.append("- Les pouvoirs sont requis\n");
            } else if (newPowers.length() < 10) {
                errors.append("- Les pouvoirs doivent contenir au moins 10 caractères\n");
            }
            if (newRarity == null)
                errors.append("- La rareté est requise\n");

            if (errors.length() > 0) {
                showAlert("Erreur de validation", errors.toString());
                return;
            }

            artefact.setName(newName);
            artefact.setType(newType);
            artefact.setUniverse(newUniverse);
            artefact.setOrigins(newOrigins);
            artefact.setPowers(newPowers);
            artefact.setRarity(newRarity);

            try {
                artefactService.update(artefact);

                if (selectedImageFile != null) {
                    artefactService.saveImage(artefact.getId(), selectedImageFile);
                }

                showAlert("Succès", "Artefact modifié avec succès !");
                displayArtefactDetails();

            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la sauvegarde de l'image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour supprimer");
            navigateTo("/login");
            return;
        }

        boolean isOwner = artefact.getCreatedBy() != null &&
                artefact.getCreatedBy().getId() == UserSession.getCurrentUser().getId();
        boolean isAdmin = UserSession.getCurrentUser().isAdmin();

        if (!isOwner && !isAdmin) {
            showAlert("Permission refusée", "Vous n'êtes pas le propriétaire de cet artefact");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer l'artefact");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement \"" + artefact.getName()
                + "\" ?\n\nCette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (artefact.getImageUrl() != null && !artefact.getImageUrl().isEmpty()) {
                    File imageFile = new File(artefact.getImageUrl());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }

                artefactService.delete(artefact.getId());
                showAlert("Succès", "Artefact supprimé avec succès !");
                handleBack();
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBack() {
        navigateTo("/artefact");
    }

    private void deleteComment(Commentaire commentaire) {
        if (!UserSession.isLoggedIn())
            return;

        boolean isOwner = artefact.getCreatedBy() != null &&
                artefact.getCreatedBy().getId() == UserSession.getCurrentUser().getId();
        boolean isAdmin = UserSession.getCurrentUser().isAdmin();

        if (!isOwner && !isAdmin) {
            showAlert("Permission refusée", "Vous ne pouvez pas supprimer ce commentaire");
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

    private class CommentCell extends ListCell<Commentaire> {
        private VBox content;
        private Label userLabel;
        private Label dateLabel;
        private Text commentText;
        private Button deleteBtn;

        public CommentCell() {
            content = new VBox(5);
            content.setStyle("-fx-padding: 10; -fx-background-color: #1a2530; -fx-background-radius: 8;");

            userLabel = new Label();
            userLabel.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 12px; -fx-font-weight: bold;");

            dateLabel = new Label();
            dateLabel.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 10px;");

            commentText = new Text();
            commentText.setStyle("-fx-fill: #000000; -fx-font-size: 12px;");
            commentText.setWrappingWidth(400);

            deleteBtn = new Button("🗑 Supprimer");
            deleteBtn.setStyle(
                    "-fx-background-color: #c62828; -fx-text-fill: #fff; -fx-background-radius: 15; -fx-padding: 5 15; -fx-cursor: hand;");

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

            boolean isOwner = UserSession.isLoggedIn() &&
                    (artefact.getCreatedBy() != null
                            && artefact.getCreatedBy().getId() == UserSession.getCurrentUser().getId() ||
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