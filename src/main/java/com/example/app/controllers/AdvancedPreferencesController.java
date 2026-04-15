package com.example.app.controllers;

import com.example.app.entities.AdvancedPreference;
import com.example.app.utils.SceneManager;
import com.example.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdvancedPreferencesController {

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField genreField;

    @FXML
    private Slider affinitySlider;

    @FXML
    private Label affinityLabel;

    @FXML
    private TextField themesField;

    @FXML
    private TextField tagsField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private AdvancedPreferenceController preferenceController;

    @FXML
    public void initialize() {
        preferenceController = new AdvancedPreferenceController();

        // Configurer le slider
        affinitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            affinityLabel.setText(String.valueOf(newVal.intValue()));
        });

        // Charger les préférences existantes
        loadPreferences();
    }

    private void loadPreferences() {
        if (SessionManager.isLoggedIn()) {
            AdvancedPreference prefs = preferenceController.getCurrentUserPreferences();
            if (prefs != null) {
                descriptionField.setText(prefs.getFreeDescription());
                genreField.setText(prefs.getFavoriteGenre());
                affinitySlider.setValue(prefs.getAffinityLevel());
                themesField.setText(prefs.getFavoriteThemes());
                tagsField.setText(prefs.getCustomTags());
            }
        }
    }

    @FXML
    public void handleSave() {
        if (!SessionManager.isLoggedIn()) {
            errorLabel.setText("Vous devez être connecté pour sauvegarder vos préférences");
            return;
        }

        try {
            String desc = descriptionField.getText();
            String genre = genreField.getText();
            int affinity = (int) affinitySlider.getValue();
            String themes = themesField.getText();
            String tags = tagsField.getText();

            preferenceController.saveCurrentUserPreferences(desc, genre, affinity, themes, tags);

            // Afficher le message de succès
            successLabel.setText("Préférences sauvegardées avec succès !");
            errorLabel.setText("");

            // Masquer le message après 3 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    successLabel.setText("");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            errorLabel.setText("Erreur lors de la sauvegarde: " + e.getMessage());
            successLabel.setText("");
        }
    }

    @FXML
    public void handleReset() {
        descriptionField.clear();
        genreField.clear();
        affinitySlider.setValue(5);
        themesField.clear();
        tagsField.clear();
        errorLabel.setText("");
        successLabel.setText("");
    }

    @FXML
    public void handleDelete() {
        if (!SessionManager.isLoggedIn()) {
            errorLabel.setText("Vous devez être connecté pour supprimer vos préférences");
            return;
        }

        try {
            preferenceController.deletePreferences(SessionManager.getCurrentUser().getId());
            handleReset();
            successLabel.setText("Préférences supprimées avec succès !");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}
