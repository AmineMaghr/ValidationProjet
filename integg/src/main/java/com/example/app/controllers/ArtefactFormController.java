package com.example.app.controllers;

import com.example.app.entities.Artefact;
import com.example.app.services.ArtefactService;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class ArtefactFormController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextArea powersArea;
    @FXML private TextArea originsArea;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> universeComboBox;
    @FXML private ComboBox<String> rarityComboBox;
    @FXML private TextField imageUrlField;

    @FXML private Label nameError;
    @FXML private Label powersError;
    @FXML private Label originsError;
    @FXML private Label rarityError;
    @FXML private Label imageUrlError;

    private ArtefactService artefactService = new ArtefactService();
    private Artefact editingArtefact;

    @FXML
    public void initialize() {
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("armure", "arme", "bijoux", "relique", "outil magique", "Autre");
        }
        if (rarityComboBox != null) {
            rarityComboBox.getItems().addAll("Commun", "Inhabituel", "Rare", "Épique", "Légendaire", "Unique");
        }
        if (universeComboBox != null) {
            universeComboBox.getItems().addAll("Marvel", "DC", "Harry Potter", "Star Wars", "Seigneur des Anneaux", "Autre");
        }
    }

    @FXML
    private void handleSave() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté");
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
        }
    }

    @FXML
    private void handleCancel() {
        navigateTo("/artefact");
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

    // ⭐ CHANGÉ de private à protected pour correspondre à BaseController
    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}