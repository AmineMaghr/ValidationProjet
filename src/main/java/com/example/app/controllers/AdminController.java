package com.example.app.controllers;

import com.example.app.services.*;
import com.example.app.entities.*;
import com.example.app.utils.SceneManager;
import com.example.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {
    @FXML
    private ListView<User> usersList;
    
    @FXML
    private ListView<Universe> universesList;
    
    @FXML
    private ListView<Oeuvre> oeuvresList;
    
    @FXML
    private ListView<Defi> defisList;
    
    @FXML
    private TextField userSearchField;
    
    @FXML
    private TextField universeSearchField;

    private UserService userService;
    private UniverseService universeService;
    private OeuvreService oeuvreService;
    private DefiService defiService;
    
    private List<User> allUsers;
    private List<Universe> allUniverses;
    private List<Oeuvre> allOeuvres;
    private List<Defi> allDefis;

    @FXML
    public void initialize() {
        // Check if admin
        if (!SessionManager.isAdmin()) {
            System.err.println("Accès refusé - Admin uniquement");
            SceneManager.showScene("common/home", "Midgar - Accueil");
            return;
        }

        userService = new UserService();
        universeService = new UniverseService();
        oeuvreService = new OeuvreService();
        defiService = new DefiService();

        loadAllData();
        setupCellFactories();
    }

    private void loadAllData() {
        try {
            allUsers = userService.select();
            if (allUsers != null) {
                usersList.getItems().addAll(allUsers);
            }

            allUniverses = universeService.select();
            if (allUniverses != null) {
                universesList.getItems().addAll(allUniverses);
            }

            allOeuvres = oeuvreService.select();
            if (allOeuvres != null) {
                oeuvresList.getItems().addAll(allOeuvres);
            }

            allDefis = defiService.select();
            if (allDefis != null) {
                defisList.getItems().addAll(allDefis);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void setupCellFactories() {
        // Users
        usersList.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) setText(null);
                        else setText(item.getUsername() + " (" + item.getRole() + ") - " + item.getEmail());
                    }
                };
            }
        });

        // Universes
        universesList.setCellFactory(new Callback<ListView<Universe>, ListCell<Universe>>() {
            @Override
            public ListCell<Universe> call(ListView<Universe> param) {
                return new ListCell<Universe>() {
                    @Override
                    protected void updateItem(Universe item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) setText(null);
                        else setText(item.getName() + " - " + item.getDescription());
                    }
                };
            }
        });

        // Oeuvres
        oeuvresList.setCellFactory(new Callback<ListView<Oeuvre>, ListCell<Oeuvre>>() {
            @Override
            public ListCell<Oeuvre> call(ListView<Oeuvre> param) {
                return new ListCell<Oeuvre>() {
                    @Override
                    protected void updateItem(Oeuvre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) setText(null);
                        else setText(item.getTitre() + " - " + item.getCreateurId());
                    }
                };
            }
        });

        // Defis
        defisList.setCellFactory(new Callback<ListView<Defi>, ListCell<Defi>>() {
            @Override
            public ListCell<Defi> call(ListView<Defi> param) {
                return new ListCell<Defi>() {
                    @Override
                    protected void updateItem(Defi item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) setText(null);
                        else setText(item.getTitre() + " - " + item.getTheme());
                    }
                };
            }
        });
    }

    @FXML
    public void handleUserSearch() {
        String query = userSearchField.getText().toLowerCase();
        if (allUsers != null) {
            List<User> filtered = allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(query) ||
                            u.getEmail().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            usersList.getItems().clear();
            usersList.getItems().addAll(filtered);
        }
    }

    @FXML
    public void handleUniverseSearch() {
        String query = universeSearchField.getText().toLowerCase();
        if (allUniverses != null) {
            List<Universe> filtered = allUniverses.stream()
                    .filter(u -> u.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            universesList.getItems().clear();
            universesList.getItems().addAll(filtered);
        }
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
    
    @FXML
    public void goShopAdmin() {
        SceneManager.showScene("admin/shop_admin", "Midgar - Administration Boutique");
    }
}
