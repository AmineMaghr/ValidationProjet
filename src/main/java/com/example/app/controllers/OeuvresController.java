package com.example.app.controllers;

import com.example.app.services.OeuvreService;
import com.example.app.entities.Oeuvre;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OeuvresController {
    @FXML
    private ListView<Oeuvre> oeuvresList;
    
    @FXML
    private TextField searchField;

    private OeuvreService oeuvreService;
    private List<Oeuvre> allOeuvres;

    @FXML
    public void initialize() {
        oeuvreService = new OeuvreService();
        loadOeuvres();
        setupCellFactory();
    }

    private void loadOeuvres() {
        try {
            allOeuvres = oeuvreService.select();
            if (allOeuvres != null) {
                oeuvresList.getItems().addAll(allOeuvres);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des oeuvres: " + e.getMessage());
        }
    }

    private void setupCellFactory() {
        oeuvresList.setCellFactory(new Callback<ListView<Oeuvre>, ListCell<Oeuvre>>() {
            @Override
            public ListCell<Oeuvre> call(ListView<Oeuvre> param) {
                return new ListCell<Oeuvre>() {
                    @Override
                    protected void updateItem(Oeuvre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s\nPar %s\n%s", 
                                item.getTitre(),
                                item.getCreateurId(),
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
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}

