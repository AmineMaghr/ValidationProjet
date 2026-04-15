package com.example.app.controllers;

import com.example.app.services.CommandeService;
import com.example.app.entities.Commande;
import com.example.app.utils.SceneManager;
import com.example.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import com.example.app.services.StockPredictionService;

public class AdminShopController {

    @FXML
    private ListView<Commande> commandesList;

    @FXML
    private TextField searchField;

    @FXML
    private VBox detailPanel;

    @FXML
    private Label commandeRef;

    @FXML
    private Label commandeDate;

    @FXML
    private Label commandeMontant;

    @FXML
    private Label commandeStatut;

    private CommandeService commandeService;
    private List<Commande> allCommandes;
    private Commande selectedCommande;

    @FXML
    public void initialize() {
        // Vérifier si admin
        if (!SessionManager.isAdmin()) {
            System.err.println("Accès refusé - Admin uniquement");
            SceneManager.showScene("common/home", "Midgar - Accueil");
            return;
        }

        commandeService = new CommandeService();
        loadCommandes();
        setupCellFactory();
        hideDetailPanel();
    }

    private void loadCommandes() {
        try {
            allCommandes = commandeService.select();
            if (allCommandes != null) {
                commandesList.getItems().addAll(allCommandes);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des commandes: " + e.getMessage());
        }
    }

    private void setupCellFactory() {
        commandesList.setCellFactory(new Callback<ListView<Commande>, ListCell<Commande>>() {
            @Override
            public ListCell<Commande> call(ListView<Commande> param) {
                return new ListCell<Commande>() {
                    @Override
                    protected void updateItem(Commande item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("Commande %s - %.2f€ - %s",
                                item.getReferenceCommande(),
                                item.getMontantTotal(),
                                item.getDateCommande().toLocalDate()));
                        }
                    }
                };
            }
        });

        // Double-clic pour voir les détails
        commandesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Commande selected = commandesList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showCommandeDetails(selected);
                }
            }
        });
    }

    private void showCommandeDetails(Commande commande) {
        selectedCommande = commande;
        commandeRef.setText(commande.getReferenceCommande());
        commandeDate.setText(commande.getDateCommande().toLocalDate().toString());
        commandeMontant.setText(String.format("%.2f€", commande.getMontantTotal()));
        commandeStatut.setText(commande.getStatutCommande());
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
    }

    private void hideDetailPanel() {
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (allCommandes != null) {
            List<Commande> filtered = allCommandes.stream()
                    .filter(c -> c.getReferenceCommande().toLowerCase().contains(query) ||
                            c.getStatutCommande().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            commandesList.getItems().clear();
            commandesList.getItems().addAll(filtered);
        }
    }

    @FXML
    public void handleRefresh() {
        commandesList.getItems().clear();
        loadCommandes();
        hideDetailPanel();
    }

    @FXML
    public void handleEditCommande() {
        if (selectedCommande != null) {
            // TODO: Implémenter l'édition de commande
            System.out.println("Édition de la commande: " + selectedCommande.getReferenceCommande());
        }
    }

    @FXML
    public void handleDeleteCommande() {
        if (selectedCommande != null) {
            try {
                commandeService.delete(selectedCommande.getId());
                handleRefresh();
                System.out.println("Commande supprimée: " + selectedCommande.getReferenceCommande());
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleAnalytics() {
        // Ouvrir les analytics de la boutique
        System.out.println("Ouverture des analytics de boutique");
        // TODO: Implémenter une vraie page d'analytics
    }

    @FXML
    public void handleAutomationHistory() {
        // Ouvrir l'historique d'automatisation
        System.out.println("Ouverture de l'historique d'automatisation");
        // TODO: Implémenter une vraie page d'historique
    }

    @FXML
    public void handleStockPredictions() {
        // Calculer les prédictions de stock
        StockPredictionService predictionService = new StockPredictionService();
        predictionService.predictAllProducts();
        System.out.println("Prédictions de stock calculées");
        // TODO: Implémenter une vraie page de prédictions
    }

    @FXML
    public void goBack() {
        SceneManager.showScene("admin/dashboard", "Midgar - Admin Dashboard");
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}
