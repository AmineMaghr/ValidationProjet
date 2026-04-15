package com.example.app.controllers;

import com.example.app.services.UniverseService;
import com.example.app.entities.Universe;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.SQLException;

public class CreateUniverseController {
    @FXML
    private TextField nameField;
    
    @FXML
    private TextArea shortDescField;
    
    @FXML
    private TextArea descriptionField;
    
    @FXML
    private TextField themesField;
    
    @FXML
    private Label errorLabel;

    private UniverseService universeService;

    @FXML
    public void initialize() {
        universeService = new UniverseService();
    }

    @FXML
    public void handleCreate() {
        String name = nameField.getText().trim();
        String shortDesc = shortDescField.getText().trim();
        String description = descriptionField.getText().trim();
        String themesStr = themesField.getText().trim();

        // Validation
        if (name.isEmpty() || description.isEmpty()) {
            errorLabel.setText("Veuillez remplir au moins le nom et la description");
            return;
        }

        try {
            // Créer l'univers
            Universe universe = new Universe();
            universe.setName(name);
            universe.setShortDescription(shortDesc);
            universe.setDescription(description);

            // Gérer les thèmes
            if (!themesStr.isEmpty()) {
                universe.setThemesFromString(themesStr);
            }

            // TODO: Associer l'utilisateur connecté comme créateur
            // Pour l'instant, on laisse creator à null

            // Ajouter à la base de données
            universeService.add(universe);

            // Retour à la liste
            SceneManager.showScene("universes/universes", "Midgar - Univers");
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de la création: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showScene("universes/universes", "Midgar - Univers");
    }
}
