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
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

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

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(
            "roman", "nouvelle", "poesie", "artwork", "musique", "autre"
        ));
        dateLabel.setText(LocalDate.now().toString());
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

        Oeuvre oeuvre = new Oeuvre();
        oeuvre.setTitle(titleField.getText().trim());
        oeuvre.setType(typeCombo.getValue());
        oeuvre.setDescription(descriptionArea.getText().trim());
        oeuvre.setAuthor(authorField.getText().trim());
        oeuvre.setDatePublication(LocalDate.now());
        oeuvre.setCreatedAt(LocalDateTime.now());
        oeuvre.setUpdatedAt(LocalDateTime.now());

        if (selectedImageFile != null) {
            oeuvre.setImageUrl(selectedImageFile.getAbsolutePath());
        }

        if (UserSession.isLoggedIn()) {
            oeuvre.setCreateurId(UserSession.getCurrentUser().getId());
        }

        try {
            oeuvreService.add(oeuvre);
            if (selectedImageFile != null) {
                oeuvreService.saveImage(oeuvre.getId(), selectedImageFile);
            }
            showAlert("Succès", "Œuvre créée avec succès !");
            navigateTo("/oeuvre");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la création: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errors.append("- Le titre est requis\n");
            titleField.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            titleField.setStyle("");
        }

        if (typeCombo.getValue() == null) {
            errors.append("- Le type est requis\n");
            typeCombo.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            typeCombo.setStyle("");
        }

        if (authorField.getText() == null || authorField.getText().trim().isEmpty()) {
            errors.append("- L'auteur est requis\n");
            authorField.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            authorField.setStyle("");
        }

        String description = descriptionArea.getText();
        if (description == null || description.trim().isEmpty()) {
            errors.append("- La description est requise\n");
            descriptionArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else if (description.length() < 10) {
            errors.append("- La description doit contenir au moins 10 caractères\n");
            descriptionArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else if (!Pattern.compile("^[a-zA-Z]").matcher(description).find()) {
            errors.append("- La description doit commencer par une lettre\n");
            descriptionArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            descriptionArea.setStyle("");
        }

        if (selectedImageFile == null) {
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

    @FXML
    private void handleBack() {
        navigateTo("/oeuvre");
    }
}
