    package com.example.app.controllers;

import com.example.app.entities.Universe;
import com.example.app.services.UniverseService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.SQLException;

public class CreateUniverseController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField genreField;
    @FXML private TextField themesField;
    @FXML private TextArea shortDescArea;
    @FXML private TextArea storyContextArea;

    private UniverseService universeService = new UniverseService();

    @FXML
    public void initialize() {
        // Initialization logic if any
    }

    @FXML
    private void saveUniverse() {
        if (!validateForm()) return;

        Universe universe = new Universe();
        universe.setName(nameField.getText());
        universe.setGenre(genreField.getText());
        universe.setThemesFromString(themesField.getText());
        universe.setShortDescription(shortDescArea.getText());
        universe.setStoryContext(storyContextArea.getText());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                universeService.add(universe);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Succès", "Univers créé avec succès !");
            navigateTo("/universes");
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            System.err.println("Erreur: " + ex.getMessage());
            showAlert("Erreur", "Impossible de créer l'univers");
        });

        new Thread(task).start();
    }

    @FXML
    private void cancel() {
        navigateTo("/universes");
    }

    private boolean validateForm() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le nom est requis.");
            return false;
        }
        return true;
    }
}

