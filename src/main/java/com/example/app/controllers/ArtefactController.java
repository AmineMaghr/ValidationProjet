package com.example.app.controllers;

import com.example.app.entities.Artefact;
import com.example.app.services.ArtefactService;
import com.example.app.utils.UserSession;
import com.example.app.views.ArtefactCard;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import java.sql.SQLException;
import java.util.List;

public class ArtefactController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private CheckBox myItemsCheckbox;
    @FXML private TilePane artefactGrid;
    @FXML private Label totalArtefactsLabel;
    @FXML private Label totalUniversLabel;
    @FXML private Label totalTypesLabel;
    @FXML private Label resultCountLabel;

    private ArtefactService artefactService = new ArtefactService();
    private ObservableList<Artefact> artefactList = FXCollections.observableArrayList();
    private Artefact currentArtefact;

    @FXML
    public void initialize() {
        loadArtefacts();
        loadTypes();
    }

    private void loadArtefacts() {
        Task<List<Artefact>> task = new Task<List<Artefact>>() {
            @Override
            protected List<Artefact> call() throws Exception {
                return artefactService.select();
            }
        };
        task.setOnSucceeded(e -> {
            List<Artefact> artefacts = task.getValue();
            artefactList.setAll(artefacts);
            displayArtefactsAsCards(artefacts);
            updateStats(artefacts);
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Erreur lors du chargement: " + (ex != null ? ex.getMessage() : "Unknown"));
            ex.printStackTrace();
        });
        new Thread(task).start();
    }

    private void displayArtefactsAsCards(List<Artefact> artefacts) {
        if (artefactGrid == null) return;

        Platform.runLater(() -> {
            artefactGrid.getChildren().clear();
            for (Artefact artefact : artefacts) {
                ArtefactCard card = new ArtefactCard(artefact, () -> {
                    currentArtefact = artefact;
                    goToShowArtefact();
                });
                artefactGrid.getChildren().add(card);
            }
            if (resultCountLabel != null) {
                resultCountLabel.setText(artefacts.size() + " artefact(s)");
            }
        });
    }

    private void updateStats(List<Artefact> artefacts) {
        if (totalArtefactsLabel != null) {
            totalArtefactsLabel.setText(String.valueOf(artefacts.size()));
        }

        if (totalUniversLabel != null) {
            long universCount = artefacts.stream()
                .map(Artefact::getUniverse)
                .filter(u -> u != null && !u.isEmpty())
                .distinct()
                .count();
            totalUniversLabel.setText(String.valueOf(universCount));
        }

        if (totalTypesLabel != null) {
            long typesCount = artefacts.stream()
                .map(Artefact::getType)
                .filter(t -> t != null && !t.isEmpty())
                .distinct()
                .count();
            totalTypesLabel.setText(String.valueOf(typesCount));
        }
    }

    private void loadTypes() {
        try {
            List<Artefact> allArtefacts = artefactService.select();
            List<String> types = allArtefacts.stream()
                .map(Artefact::getType)
                .filter(t -> t != null && !t.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

            if (typeFilter != null) {
                typeFilter.setItems(FXCollections.observableArrayList(types));
                typeFilter.getItems().add(0, "Tous");
                typeFilter.setValue("Tous");
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement types: " + e.getMessage());
        }
    }

    @FXML
    private void search() {
        loadArtefacts();
    }

    @FXML
    private void resetFilters() {
        if (searchField != null) searchField.clear();
        if (typeFilter != null) typeFilter.setValue("Tous");
        if (myItemsCheckbox != null) myItemsCheckbox.setSelected(false);
        loadArtefacts();
    }

    @FXML
    private void toggleMyItems() {
        loadArtefacts();
    }

    private static Artefact selectedArtefactForShow;

    @FXML
    public void goToCreateArtefact() {
        navigateTo("/artefact/create");
    }

    @FXML
    public void goToShowArtefact() {
        if (currentArtefact != null) {
            selectedArtefactForShow = currentArtefact;
            navigateTo("/artefact/show");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un artefact");
        }
    }

    public static Artefact getSelectedArtefactForShow() {
        return selectedArtefactForShow;
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshList() {
        loadArtefacts();
    }
}