package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.services.OeuvreService;
import com.example.app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateOeuvreController extends BaseController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea descriptionArea;
    @FXML private TextField authorField;
    @FXML private Button selectImageBtn;
    @FXML private ImageView imagePreview;
    @FXML private Label imageStatus;
    @FXML private Label dateLabel;
    @FXML private Label errorLabel;

    private OeuvreService oeuvreService = new OeuvreService();
    private File selectedImageFile;
    private Oeuvre currentOeuvre;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(
            "roman", "nouvelle", "poesie", "artwork", "musique", "autre"
        ));
        dateLabel.setText(LocalDate.now().toString());
        
        // Vérifier si on est en mode édition
        currentOeuvre = OeuvreController.getSelectedOeuvreForShow();
        if (currentOeuvre != null) {
            loadOeuvreForEdit();
        }
    }

    private void loadOeuvreForEdit() {
        titleField.setText(currentOeuvre.getTitle());
        typeCombo.setValue(currentOeuvre.getType());
        descriptionArea.setText(currentOeuvre.getDescription());
        authorField.setText(currentOeuvre.getAuthor());
        
        // Charger l'image si elle existe
        if (currentOeuvre.hasImage()) {
            imagePreview.setImage(currentOeuvre.getImage());
            imageStatus.setText("✓ Image existante");
            imageStatus.setStyle("-fx-text-fill: #18E3A4;");
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            imagePreview.setImage(new Image(file.toURI().toString()));
            imageStatus.setText("✓ " + file.getName());
            imageStatus.setStyle("-fx-text-fill: #18E3A4;");
            errorLabel.setText("");
        }
    }

    @FXML
    private void handleCreate() {
        if (!validateForm()) {
            return;
        }

        Oeuvre oeuvre = (currentOeuvre != null) ? currentOeuvre : new Oeuvre();
        oeuvre.setTitle(titleField.getText().trim());
        oeuvre.setType(typeCombo.getValue());
        oeuvre.setDescription(descriptionArea.getText().trim());
        oeuvre.setAuthor(authorField.getText().trim());
        oeuvre.setDatePublication(LocalDate.now());

        if (UserSession.isLoggedIn()) {
            oeuvre.setCreateurId(UserSession.getCurrentUser().getId());
        }

        try {
            if (currentOeuvre == null) {
                // Création
                oeuvreService.add(oeuvre);
                if (selectedImageFile != null) {
                    // Sauvegarde avec les 3 chemins
                    String fileName = oeuvre.saveImage(selectedImageFile);
                    oeuvre.setImageUrl(fileName);
                    oeuvreService.update(oeuvre);
                }
                showAlert("Succès", "Œuvre créée avec succès !");
            } else {
                // Modification
                if (selectedImageFile != null) {
                    oeuvre.saveImage(selectedImageFile);
                }
                oeuvreService.update(oeuvre);
                showAlert("Succès", "Œuvre modifiée avec succès !");
            }
            navigateTo("/oeuvre");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errors.append("- Le titre est requis\n");
            titleField.setStyle("-fx-border-color: #ff6b6b;");
        } else {
            titleField.setStyle("");
        }

        if (typeCombo.getValue() == null) {
            errors.append("- Le type est requis\n");
            typeCombo.setStyle("-fx-border-color: #ff6b6b;");
        } else {
            typeCombo.setStyle("");
        }

        if (authorField.getText() == null || authorField.getText().trim().isEmpty()) {
            errors.append("- L'auteur est requis\n");
            authorField.setStyle("-fx-border-color: #ff6b6b;");
        } else {
            authorField.setStyle("");
        }

        String description = descriptionArea.getText();
        if (description == null || description.trim().isEmpty()) {
            errors.append("- La description est requise\n");
            descriptionArea.setStyle("-fx-border-color: #ff6b6b;");
        } else if (description.length() < 10) {
            errors.append("- La description doit contenir au moins 10 caractères\n");
            descriptionArea.setStyle("-fx-border-color: #ff6b6b;");
        } else {
            descriptionArea.setStyle("");
        }

        // L'image n'est obligatoire que pour une nouvelle création
        if (currentOeuvre == null && selectedImageFile == null) {
            errors.append("- L'image est requise\n");
        }

        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        navigateTo("/oeuvre");
    }
}