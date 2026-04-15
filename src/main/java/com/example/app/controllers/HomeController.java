package com.example.app.controllers;

import com.example.app.services.*;
import com.example.app.entities.*;
import com.example.app.utils.SessionManager;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;

public class HomeController {
    @FXML
    private ListView<Universe> universesList;
    
    @FXML
    private ListView<Oeuvre> creationsList;
    
    @FXML
    private ListView<Defi> defisList;
    
    @FXML
    private Label userLabel;
    
    @FXML
    private VBox mainContent;

    private UniverseService universeService;
    private OeuvreService oeuvreService;
    private DefiService defiService;

    @FXML
    public void initialize() {
        universeService = new UniverseService();
        oeuvreService = new OeuvreService();
        defiService = new DefiService();

        loadData();
        setupCellFactories();
        updateUserLabel();
    }

    private void loadData() {
        try {
            // Charger les univers
            List<Universe> universes = universeService.select();
            if (universes != null && universes.size() > 10) {
                universesList.getItems().addAll(universes.subList(0, 10));
            } else if (universes != null) {
                universesList.getItems().addAll(universes);
            }

            // Charger les oeuvres
            List<Oeuvre> oeuvres = oeuvreService.select();
            if (oeuvres != null && oeuvres.size() > 10) {
                creationsList.getItems().addAll(oeuvres.subList(0, 10));
            } else if (oeuvres != null) {
                creationsList.getItems().addAll(oeuvres);
            }

            // Charger les défis
            List<Defi> defis = defiService.select();
            if (defis != null && defis.size() > 8) {
                defisList.getItems().addAll(defis.subList(0, 8));
            } else if (defis != null) {
                defisList.getItems().addAll(defis);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void setupCellFactories() {
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
                            setText(item.getName() + " - " + item.getDescription());
                        }
                    }
                };
            }
        });

        // Oeuvre cells
        creationsList.setCellFactory(new Callback<ListView<Oeuvre>, ListCell<Oeuvre>>() {
            @Override
            public ListCell<Oeuvre> call(ListView<Oeuvre> param) {
                return new ListCell<Oeuvre>() {
                    @Override
                    protected void updateItem(Oeuvre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getTitre() + " - " + item.getCreateurId());
                        }
                    }
                };
            }
        });

        // Defi cells
        defisList.setCellFactory(new Callback<ListView<Defi>, ListCell<Defi>>() {
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
                        }
                    }
                };
            }
        });
    }

    private void updateUserLabel() {
        userLabel.setText("Connecté: " + SessionManager.getUsername());
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }

    @FXML
    public void goUniverses() {
        SceneManager.showScene("universes/universes", "Midgar - Univers");
    }

    @FXML
    public void goOeuvres() {
        SceneManager.showScene("oeuvres/oeuvres", "Midgar - Oeuvres");
    }

    @FXML
    public void goPersonnages() {
        SceneManager.showScene("personnages/personnages", "Midgar - Personnages");
    }

    @FXML
    public void goQuiz() {
        // TODO: Implémenter la page du quiz
        System.out.println("Navigation vers Quiz");
    }

    @FXML
    public void goChallenges() {
        SceneManager.showScene("challenges/challenges", "Midgar - Défis");
    }

    @FXML
    public void handleLogout() {
        SessionManager.logout();
        SceneManager.showScene("common/login", "Midgar - Connexion");
    }

    @FXML
    public void goPreferences() {
        SceneManager.showScene("preferences/advanced_preferences", "Midgar - Préférences Avancées");
    }
}
