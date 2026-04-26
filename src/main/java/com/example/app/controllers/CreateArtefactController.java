package com.example.app.controllers;

import com.example.app.entities.Artefact;
import com.example.app.services.ArtefactService;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class CreateArtefactController extends BaseController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField universeField;
    @FXML private TextArea originsArea;
    @FXML private TextArea powersArea;
    @FXML private ComboBox<String> rarityCombo;
    @FXML private ImageView imagePreview;
    @FXML private Label imageStatus;
    @FXML private Label errorLabel;
    @FXML private Button createButton;

    private ArtefactService artefactService = new ArtefactService();
    private File selectedImageFile;

    @FXML
    public void initialize() {
        typeCombo.setItems(javafx.collections.FXCollections.observableArrayList("armure", "arme", "bijoux", "relique", "outil magique"));
        rarityCombo.setItems(javafx.collections.FXCollections.observableArrayList("Rare", "Épique", "Légendaire"));
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

        Artefact artefact = new Artefact();
        artefact.setName(nameField.getText().trim());
        artefact.setType(typeCombo.getValue());
        artefact.setUniverse(universeField.getText().trim());
        artefact.setOrigins(originsArea.getText().trim());
        artefact.setPowers(powersArea.getText().trim());
        artefact.setRarity(rarityCombo.getValue());

        if (selectedImageFile != null) {
            artefact.setImageUrl(selectedImageFile.getAbsolutePath());
        }

        if (UserSession.isLoggedIn()) {
            artefact.setCreatedBy(UserSession.getCurrentUser());
        }

        try {
            artefactService.add(artefact);
            if (selectedImageFile != null) {
                artefactService.saveImage(artefact.getId(), selectedImageFile);
            }
            showAlert("Succès", "Artefact créé avec succès !");
            navigateTo("/artefact");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la création: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("- Le nom est requis\n");
            nameField.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            nameField.setStyle("");
        }

        if (typeCombo.getValue() == null) {
            errors.append("- Le type est requis\n");
            typeCombo.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            typeCombo.setStyle("");
        }

        if (universeField.getText() == null || universeField.getText().trim().isEmpty()) {
            errors.append("- L'univers est requis\n");
            universeField.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            universeField.setStyle("");
        }

        String origins = originsArea.getText();
        if (origins == null || origins.trim().isEmpty()) {
            errors.append("- Les origines sont requises\n");
            originsArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            originsArea.setStyle("");
        }

        String powers = powersArea.getText();
        if (powers == null || powers.trim().isEmpty()) {
            errors.append("- Les pouvoirs sont requis\n");
            powersArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else if (powers.length() < 10) {
            errors.append("- Les pouvoirs doivent contenir au moins 10 caractères\n");
            powersArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else if (!powers.matches("^[a-zA-Z].*$")) {
            errors.append("- Les pouvoirs doivent commencer par une lettre\n");
            powersArea.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            powersArea.setStyle("");
        }

        if (rarityCombo.getValue() == null) {
            errors.append("- La rareté est requise\n");
            rarityCombo.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            rarityCombo.setStyle("");
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
        navigateTo("/artefact");
    }

    @FXML
    private void handleBack() {
        navigateTo("/artefact");
    }

    @FXML
    public void goAccueil() {
        navigateTo("/");
    }
}