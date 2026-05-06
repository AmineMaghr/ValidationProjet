package com.example.app.controllers;

import com.example.app.entities.Universe;
import com.example.app.services.UniverseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.sql.SQLException;
import java.util.List;

public class UniversesController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ListView<Universe> universeList;
    @FXML private Label detailName;
    @FXML private Label detailGenre;
    @FXML private Label detailDescription;
    @FXML private Text detailStoryContext;
    @FXML private HBox themesContainer;

    private UniverseService universeService = new UniverseService();
    private ObservableList<Universe> universes = FXCollections.observableArrayList();
    private String currentGenre = null;  // Pour suivre le filtre actuel

    @FXML
    public void initialize() {
        universeList.setItems(universes);

        universeList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                // showUniverseDetails est déjà dans UniverseListView ou autre s'il existe. 
                // Dans le code FXML : ListView universeList, puis click sur un item
                // wait, if we click, we want to go back to UniverseDetailView.
                try {
                    universeList.getScene().setRoot(new com.example.app.views.UniverseDetailView(selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loadUniverses();
    }

    private void loadUniverses() {
        loadUniversesWithFilters(searchField.getText(), currentGenre);
    }

    private void loadUniversesWithFilters(String search, String genre) {
        Task<List<Universe>> task = new Task<>() {
            @Override
            protected List<Universe> call() throws SQLException {
                return universeService.searchUniverses(search, genre, "newest");
            }
        };
        task.setOnSucceeded(e -> {
            universes.setAll(task.getValue());
            updateFilterButtonsStyle();
        });
        task.setOnFailed(e -> {
            Throwable exception = e.getSource().getException();
            System.err.println("Erreur chargement: " + (exception != null ? exception.getMessage() : "Unknown error"));
            showAlert("Erreur", "Impossible de charger les univers");
        });
        new Thread(task).start();
    }

    private void showUniverseDetails(Universe universe) {
        if (universe == null) return;

        detailName.setText(universe.getName() != null ? universe.getName() : "");
        detailGenre.setText(universe.getGenre() != null ? universe.getGenre() : "");
        detailDescription.setText(universe.getShortDescription() != null ? universe.getShortDescription() : "");
        detailStoryContext.setText(universe.getStoryContext() != null ? universe.getStoryContext() : "");

        themesContainer.getChildren().clear();
        if (universe.getThemes() != null) {
            for (String theme : universe.getThemes()) {
                if (theme != null && !theme.isEmpty()) {
                    Label tag = new Label(theme);
                    tag.getStyleClass().add("tag");
                    themesContainer.getChildren().add(tag);
                }
            }
        }
    }

    // ===== MÉTHODES DE FILTRAGE =====

    @FXML
    private void filterAll() {
        currentGenre = null;
        loadUniversesWithFilters(searchField.getText(), null);
    }

    @FXML
    private void filterFantasy() {
        currentGenre = "Fantasy";
        loadUniversesWithFilters(searchField.getText(), "Fantasy");
    }

    @FXML
    private void filterSciFi() {
        currentGenre = "Science-Fiction";
        loadUniversesWithFilters(searchField.getText(), "Science-Fiction");
    }

    @FXML
    private void filterHorror() {
        currentGenre = "Horreur";
        loadUniversesWithFilters(searchField.getText(), "Horreur");
    }

    @FXML
    private void filterSteampunk() {
        currentGenre = "Steampunk";
        loadUniversesWithFilters(searchField.getText(), "Steampunk");
    }

    @FXML
    private void filterMedieval() {
        currentGenre = "Médiéval";
        loadUniversesWithFilters(searchField.getText(), "Médiéval");
    }

    // ===== MÉTHODES DE RECHERCHE =====

    @FXML
    private void search() {
        loadUniversesWithFilters(searchField.getText(), currentGenre);
    }

    @FXML
    private void handleSearch() {
        loadUniversesWithFilters(searchField.getText(), currentGenre);
    }

    // ===== MÉTHODES DE CRÉATION =====

    @FXML
    private void createUniverse() {
        navigateTo("/universes/create");
    }

    @FXML
    private void handleCreate() {
        navigateTo("/universes/create");
    }

    // ===== RÉINITIALISATION =====

    @FXML
    private void resetFilters() {
        searchField.clear();
        currentGenre = null;
        loadUniversesWithFilters("", null);
    }

    // Met à jour le style des boutons de filtre
    private void updateFilterButtonsStyle() {
        // Cette méthode peut être utilisée pour changer le style des boutons actifs
        // Tu peux l'implémenter si tu veux un effet visuel sur le filtre actif
    }
}