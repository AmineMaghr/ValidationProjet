package com.example.app.controllers;

import com.example.app.services.PersonnageService;
import com.example.app.entities.Personnage;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PersonnagesController {
    @FXML
    private ListView<Personnage> personnagesList;
    
    @FXML
    private TextField searchField;

    private PersonnageService personnageService;
    private List<Personnage> allPersonnages;

    @FXML
    public void initialize() {
        personnageService = new PersonnageService();
        loadPersonnages();
        setupCellFactory();
    }

    private void loadPersonnages() {
        try {
            allPersonnages = personnageService.select();
            if (allPersonnages != null) {
                personnagesList.getItems().addAll(allPersonnages);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des personnages: " + e.getMessage());
        }
    }

    private void setupCellFactory() {
        personnagesList.setCellFactory(new Callback<ListView<Personnage>, ListCell<Personnage>>() {
            @Override
            public ListCell<Personnage> call(ListView<Personnage> param) {
                return new ListCell<Personnage>() {
                    @Override
                    protected void updateItem(Personnage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s\nType: %s\nDescription: %s", 
                                item.getNom(),
                                item.getUnivers(),
                                item.getDescription()));
                            setWrapText(true);
                            setPrefHeight(80);
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (allPersonnages != null) {
            List<Personnage> filtered = allPersonnages.stream()
                    .filter(p -> p.getNom().toLowerCase().contains(query) ||
                            p.getDescription().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            personnagesList.getItems().clear();
            personnagesList.getItems().addAll(filtered);
        }
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}

