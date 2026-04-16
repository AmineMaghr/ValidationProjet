package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.services.OeuvreService;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ShowOeuvreController extends BaseController {

    @FXML private ImageView imageView;
    @FXML private Text titleText;
    @FXML private Text authorText;
    @FXML private Text typeText;
    @FXML private Text dateText;
    @FXML private Text descriptionText;
    @FXML private Button modifyBtn;
    @FXML private Button deleteBtn;

    private Oeuvre oeuvre;
    private OeuvreService oeuvreService = new OeuvreService();
    private File selectedImageFile;

    @FXML
    public void initialize() {
        oeuvre = OeuvreController.getSelectedOeuvreForShow();
        if (oeuvre != null) {
            displayOeuvreDetails();
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

        // Charger l'image
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

    @FXML
    private void handleModify() {
        // Créer une boîte de dialogue personnalisée pour la modification
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'œuvre");
        dialog.setHeaderText("Modifier: " + oeuvre.getTitle());

        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Créer le contenu du formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #11161c;");

        // Champs du formulaire
        TextField titleField = new TextField(oeuvre.getTitle());
        titleField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("roman", "nouvelle", "poesie", "artwork", "musique", "autre");
        typeCombo.setValue(oeuvre.getType());
        typeCombo.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 5;");

        TextField authorField = new TextField(oeuvre.getAuthor());
        authorField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");

        TextArea descriptionArea = new TextArea(oeuvre.getDescription());
        descriptionArea.setPrefHeight(100);
        descriptionArea.setWrapText(true);
        descriptionArea.setStyle("-fx-background-color: #fff; -fx-text-fill: #000; -fx-padding: 10; -fx-background-radius: 8;");

        DatePicker datePicker = new DatePicker();
        if (oeuvre.getDatePublication() != null) {
            datePicker.setValue(oeuvre.getDatePublication());
        } else {
            datePicker.setValue(LocalDate.now());
        }
        datePicker.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff;");

        // Bouton pour changer l'image
        Button imageBtn = new Button("Changer l'image");
        imageBtn.setStyle("-fx-background-color: #2a3540; -fx-text-fill: #fff; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;");
        Label imageStatus = new Label("Image actuelle conservée");
        imageStatus.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 11px;");

        imageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif")
            );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedImageFile = file;
                imageStatus.setText("✓ Nouvelle image: " + file.getName());
                imageStatus.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px;");
            }
        });

        // Ajouter les champs au grid
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Auteur:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Image:"), 0, 5);

        VBox imageBox = new VBox(5, imageBtn, imageStatus);
        grid.add(imageBox, 1, 5);

        // Styliser les labels
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: #b0b9b6; -fx-font-weight: bold;");
            }
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #0a0c10;");

        // Afficher la boîte de dialogue
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            // Valider les champs
            String newTitle = titleField.getText().trim();
            String newType = typeCombo.getValue();
            String newAuthor = authorField.getText().trim();
            String newDescription = descriptionArea.getText().trim();
            LocalDate newDate = datePicker.getValue();

            // Validation
            StringBuilder errors = new StringBuilder();
            if (newTitle.isEmpty()) errors.append("- Le titre est requis\n");
            if (newType == null) errors.append("- Le type est requis\n");
            if (newAuthor.isEmpty()) errors.append("- L'auteur est requis\n");
            if (newDescription.isEmpty()) {
                errors.append("- La description est requise\n");
            } else if (newDescription.length() < 10) {
                errors.append("- La description doit contenir au moins 10 caractères\n");
            } else if (!newDescription.matches("^[a-zA-Z].*$")) {
                errors.append("- La description doit commencer par une lettre\n");
            }

            if (errors.length() > 0) {
                showAlert("Erreur de validation", errors.toString());
                return;
            }

            // Mettre à jour l'œuvre
            oeuvre.setTitle(newTitle);
            oeuvre.setType(newType);
            oeuvre.setAuthor(newAuthor);
            oeuvre.setDescription(newDescription);
            oeuvre.setDatePublication(newDate != null ? newDate : LocalDate.now());
            oeuvre.setUpdatedAt(java.time.LocalDateTime.now());

            try {
                oeuvreService.update(oeuvre);

                // Sauvegarder la nouvelle image si sélectionnée
                if (selectedImageFile != null) {
                    oeuvreService.saveImage(oeuvre.getId(), selectedImageFile);
                }

                showAlert("Succès", "Œuvre modifiée avec succès !");
                displayOeuvreDetails(); // Rafraîchir l'affichage

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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer l'œuvre");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement \"" + oeuvre.getTitle() + "\" ?\n\nCette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'image associée si elle existe
                if (oeuvre.getImageUrl() != null && !oeuvre.getImageUrl().isEmpty()) {
                    File imageFile = new File(oeuvre.getImageUrl());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }

                oeuvreService.delete(oeuvre.getId());
                showAlert("Succès", "Œuvre supprimée avec succès !");
                handleBack();
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBack() {
        navigateTo("/oeuvre");
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}