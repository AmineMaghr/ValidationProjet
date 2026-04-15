package com.example.app.controllers;

import com.example.app.services.UniverseService;
import com.example.app.entities.Universe;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UniversesController {
    @FXML
    private ListView<Universe> universesList;
    
    @FXML
    private TextField searchField;

    private UniverseService universeService;
    private List<Universe> allUniverses;

    @FXML
    public void initialize() {
        universeService = new UniverseService();
        loadUniverses();
        setupCellFactory();
    }

    private void loadUniverses() {
        try {
            allUniverses = universeService.select();
            if (allUniverses != null) {
                universesList.getItems().addAll(allUniverses);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des univers: " + e.getMessage());
        }
    }

    private void setupCellFactory() {
        // Universe cells
        universesList.setCellFactory(new Callback<ListView<Universe>, ListCell<Universe>>() {
            @Override
            public ListCell<Universe> call(ListView<Universe> param) {
                return new ListCell<Universe>() {
                    @Override
                    protected void updateItem(Universe item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            StringBuilder text = new StringBuilder();
                            text.append(item.getName());
                            
                            if (item.getShortDescription() != null && !item.getShortDescription().isEmpty()) {
                                text.append(" - ").append(item.getShortDescription());
                            } else if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                                // Prendre les premiers 50 caractères de la description complète
                                String desc = item.getDescription().length() > 50 
                                    ? item.getDescription().substring(0, 50) + "..." 
                                    : item.getDescription();
                                text.append(" - ").append(desc);
                            }
                            
                            if (item.getThemes() != null && !item.getThemes().isEmpty()) {
                                text.append("\nThèmes: ").append(item.getThemesAsString());
                            }
                            
                            setText(text.toString());
                            setWrapText(true);
                            setPrefHeight(60);
                        }
                    }
                };
            }
        });

        // Double-click pour voir les détails
        universesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Universe selected = universesList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    // TODO: Implémenter la page de détails
                    System.out.println("Universes sélectionné: " + selected.getName());
                }
            }
        });
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (allUniverses != null) {
            List<Universe> filtered = allUniverses.stream()
                    .filter(u -> u.getName().toLowerCase().contains(query) ||
                            u.getShortDescription().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            universesList.getItems().clear();
            universesList.getItems().addAll(filtered);
        }
    }

    @FXML
    public void handleCreate() {
        SceneManager.showScene("universes/create_universe", "Midgar - Créer un Univers");
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}
