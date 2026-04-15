package com.example.app.controllers;

import com.example.app.services.DefiService;
import com.example.app.services.ParticipationService;
import com.example.app.entities.Defi;
import com.example.app.utils.SceneManager;
import com.example.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;

public class ChallengesController {
    @FXML
    private ListView<Defi> challengesList;
    
    @FXML
    private VBox detailsPanel;
    
    @FXML
    private Label challengeTitle;
    
    @FXML
    private Label challengeTheme;
    
    @FXML
    private Label challengeStart;
    
    @FXML
    private Label challengeEnd;
    
    @FXML
    private TextArea challengeDescription;

    private DefiService defiService;
    private ParticipationService participationService;
    private Defi selectedChallenge;

    @FXML
    public void initialize() {
        defiService = new DefiService();
        participationService = new ParticipationService();
        loadChallenges();
        setupCellFactory();
    }

    private void loadChallenges() {
        try {
            List<Defi> defis = defiService.select();
            if (defis != null) {
                challengesList.getItems().addAll(defis);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des défis: " + e.getMessage());
        }
    }

    private void setupCellFactory() {
        challengesList.setCellFactory(new Callback<ListView<Defi>, ListCell<Defi>>() {
            @Override
            public ListCell<Defi> call(ListView<Defi> param) {
                return new ListCell<Defi>() {
                    @Override
                    protected void updateItem(Defi item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getTitre() + " - Thème: " + item.getTheme());
                            setWrapText(true);
                            setPrefHeight(50);
                        }
                    }
                };
            }
        });

        // Double-click pour voir les détails
        challengesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Defi selected = challengesList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showChallengeDetails(selected);
                }
            }
        });
    }

    private void showChallengeDetails(Defi defi) {
        selectedChallenge = defi;
        challengeTitle.setText(defi.getTitre());
        challengeTheme.setText(defi.getTheme());
        challengeStart.setText(defi.getDateDebut() != null ? defi.getDateDebut().toString() : "Non spécifié");
        challengeEnd.setText(defi.getDateFin() != null ? defi.getDateFin().toString() : "Non spécifié");
        challengeDescription.setText(defi.getDescription());
        detailsPanel.setVisible(true);
        detailsPanel.setManaged(true);
    }

    @FXML
    public void handleParticipate() {
        if (selectedChallenge != null && SessionManager.isLoggedIn()) {
            System.out.println("Participation au défi: " + selectedChallenge.getTitre());
            // TODO: Implémenter la logique de participation
        }
    }

    @FXML
    public void handleCloseDetails() {
        detailsPanel.setVisible(false);
        detailsPanel.setManaged(false);
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}

