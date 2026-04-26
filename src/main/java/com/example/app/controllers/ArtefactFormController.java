package com.example.app.controllers;

import com.example.app.entities.Artefact;
import com.example.app.services.ArtefactService;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ArtefactFormController extends BaseController {

    // Champs du formulaire
    @FXML
    private TextField nameField;
    @FXML
    private TextArea powersArea;
    @FXML
    private TextArea originsArea;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> universeComboBox;
    @FXML
    private ComboBox<String> rarityComboBox;
    @FXML
    private TextField imageUrlField;

    // Labels d'erreur
    @FXML
    private Label nameError;
    @FXML
    private Label powersError;
    @FXML
    private Label originsError;
    @FXML
    private Label rarityError;
    @FXML
    private Label imageUrlError;

    // Patterns de validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\p{Punct}]{2,150}$");
    private static final Pattern POWERS_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\p{Punct}]{5,2000}$");
    private static final Pattern ORIGINS_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\p{Punct}]{5,2000}$");
    private static final Pattern URL_PATTERN = Pattern
            .compile("^(https?://)?([\\da-z.-]+\\.([a-z.]{2,6})[/\\w .-]*/?)$");

    private ArtefactService artefactService = new ArtefactService();
    private Artefact editingArtefact;

    @FXML
    public void initialize() {
        setupValidationListeners();
        loadComboBoxData();
    }

    private void setupValidationListeners() {
        if (nameField != null) {
            nameField.textProperty().addListener((obs, old, val) -> validateName());
        }
        if (powersArea != null) {
            powersArea.textProperty().addListener((obs, old, val) -> validatePowers());
        }
        if (originsArea != null) {
            originsArea.textProperty().addListener((obs, old, val) -> validateOrigins());
        }
        if (imageUrlField != null) {
            imageUrlField.textProperty().addListener((obs, old, val) -> validateImageUrl());
        }
    }

    // ============= VALIDATIONS INDIVIDUELLES =============

    private boolean validateName() {
        if (nameField == null || nameError == null)
            return true;

        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            nameError.setText("Le nom est requis");
            nameError.setVisible(true);
            nameField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            nameError.setText("Le nom doit contenir 2-150 caractères (lettres, chiffres, ponctuation)");
            nameError.setVisible(true);
            nameField.setStyle("-fx-border-color: red;");
            return false;
        }

        nameError.setVisible(false);
        nameField.setStyle("");
        return true;
    }

    private boolean validatePowers() {
        if (powersArea == null || powersError == null)
            return true;

        String powers = powersArea.getText();
        if (powers == null || powers.trim().isEmpty()) {
            powersError.setText("Les pouvoirs sont requis");
            powersError.setVisible(true);
            powersArea.setStyle("-fx-border-color: red;");
            return false;
        }

        if (powers.length() < 5) {
            powersError.setText("Les pouvoirs doivent contenir au moins 5 caractères");
            powersError.setVisible(true);
            powersArea.setStyle("-fx-border-color: red;");
            return false;
        }

        if (powers.length() > 2000) {
            powersError.setText("Les pouvoirs ne peuvent pas dépasser 2000 caractères");
            powersError.setVisible(true);
            powersArea.setStyle("-fx-border-color: red;");
            return false;
        }

        powersError.setVisible(false);
        powersArea.setStyle("");
        return true;
    }

    private boolean validateOrigins() {
        if (originsArea == null || originsError == null)
            return true;

        String origins = originsArea.getText();
        if (origins == null || origins.trim().isEmpty()) {
            originsError.setText("Les origines sont requises");
            originsError.setVisible(true);
            originsArea.setStyle("-fx-border-color: red;");
            return false;
        }

        if (origins.length() < 5) {
            originsError.setText("Les origines doivent contenir au moins 5 caractères");
            originsError.setVisible(true);
            originsArea.setStyle("-fx-border-color: red;");
            return false;
        }

        if (origins.length() > 2000) {
            originsError.setText("Les origines ne peuvent pas dépasser 2000 caractères");
            originsError.setVisible(true);
            originsArea.setStyle("-fx-border-color: red;");
            return false;
        }

        originsError.setVisible(false);
        originsArea.setStyle("");
        return true;
    }

    private boolean validateImageUrl() {
        if (imageUrlField == null || imageUrlError == null)
            return true;

        String url = imageUrlField.getText();
        if (url == null || url.trim().isEmpty()) {
            imageUrlError.setText("L'URL de l'image est requise");
            imageUrlError.setVisible(true);
            imageUrlField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (!URL_PATTERN.matcher(url.trim()).matches()) {
            imageUrlError.setText("URL invalide (format URL attendu)");
            imageUrlError.setVisible(true);
            imageUrlField.setStyle("-fx-border-color: red;");
            return false;
        }

        imageUrlError.setVisible(false);
        imageUrlField.setStyle("");
        return true;
    }

    private boolean validateAllFields() {
        return validateName() && validatePowers() && validateOrigins() && validateImageUrl();
    }

    private void loadComboBoxData() {
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Arme", "Armure", "Objet Magique", "Potion", "Artefact", "Relique", "Autre");
        }
        if (rarityComboBox != null) {
            rarityComboBox.getItems().addAll("Commun", "Inhabituel", "Rare", "Épique", "Légendaire", "Unique");
        }
        // Charger les univers existants
        if (universeComboBox != null) {
            try {
                List<com.example.app.entities.Universe> universes = new com.example.app.dao.UniverseDAO().select();
                universeComboBox.getItems().add("Autre");
                for (com.example.app.entities.Universe u : universes) {
                    universeComboBox.getItems().add(u.getName());
                }
            } catch (SQLException e) {
                universeComboBox.getItems().addAll("Marvel", "DC", "Harry Potter", "Star Wars", "Seigneur des Anneaux",
                        "Autre");
            }
        }
    }

    @FXML
    private void handleSave() {
        // Validation avant sauvegarde
        if (!validateAllFields()) {
            showAlert("Erreur de validation", "Veuillez corriger les erreurs dans le formulaire");
            return;
        }

        // Vérifier si l'utilisateur est connecté
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour créer/modifier un artefact");
            navigateTo("/login");
            return;
        }

        try {
            Artefact artefact = new Artefact();
            artefact.setName(nameField.getText().trim());
            artefact.setPowers(powersArea.getText().trim());
            artefact.setOrigins(originsArea.getText().trim());
            artefact.setType(typeComboBox.getValue());
            artefact.setUniverse(universeComboBox.getValue());
            artefact.setRarity(rarityComboBox.getValue());
            artefact.setImageUrl(imageUrlField.getText().trim());
            artefact.setCreatedBy(UserSession.getCurrentUser());

            if (editingArtefact != null) {
                artefact.setId(editingArtefact.getId());
                artefactService.update(artefact);
                showAlert("Succès", "L'artefact a été mis à jour");
            } else {
                artefactService.add(artefact);
                showAlert("Succès", "L'artefact a été créé");
            }

            navigateTo("/artefact");

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur base de données: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage());
        }
    }

    public void setEditingArtefact(Artefact artefact) {
        this.editingArtefact = artefact;
        if (artefact != null) {
            nameField.setText(artefact.getName());
            powersArea.setText(artefact.getPowers());
            originsArea.setText(artefact.getOrigins());
            typeComboBox.setValue(artefact.getType());
            universeComboBox.setValue(artefact.getUniverse());
            rarityComboBox.setValue(artefact.getRarity());
            imageUrlField.setText(artefact.getImageUrl());
        }
    }

    @FXML
    private void handleCancel() {
        navigateTo("/artefact");
    }
}