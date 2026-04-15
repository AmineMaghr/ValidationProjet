package com.example.app.controllers;

import com.example.app.services.ProduitService;
import com.example.app.entities.Produit;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;

public class ShopController {
    @FXML
    private ListView<Produit> productsList;

    private ProduitService produitService;

    @FXML
    public void initialize() {
        produitService = new ProduitService();
        loadProducts();
        setupCellFactory();
    }

    private void loadProducts() {
        try {
            List<Produit> produits = produitService.select();
            if (produits != null) {
                productsList.getItems().addAll(produits);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    private void setupCellFactory() {
        productsList.setCellFactory(new Callback<ListView<Produit>, ListCell<Produit>>() {
            @Override
            public ListCell<Produit> call(ListView<Produit> param) {
                return new ListCell<Produit>() {
                    @Override
                    protected void updateItem(Produit item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s - %.2f€\n%s", 
                                item.getNom(),
                                item.getPrix(),
                                item.getDescription()));
                            setWrapText(true);
                            setPrefHeight(70);
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

