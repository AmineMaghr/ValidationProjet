package com.example.app.controllers;

import com.example.app.entities.Universe;
import com.example.app.services.UniverseService;
import com.example.app.services.VideoPlayerService;
import com.example.app.services.EmailNotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.media.MediaView;
import java.sql.SQLException;
import java.util.List;

public class UniversesController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ListView<Universe> universeList;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Label detailName;
    @FXML private Label detailGenre;
    @FXML private Label detailDescription;
    @FXML private Text detailStoryContext;
    @FXML private HBox themesContainer;
    @FXML private TabPane tabPane;

    private UniverseService universeService = new UniverseService();
    private ObservableList<Universe> universes = FXCollections.observableArrayList();
    private String currentGenre = null;  // Pour suivre le filtre actuel
    private String currentSort = "Récents";
    private ChatbotPanelController chatbotController;
    private Universe currentUniverse;

    @FXML
    public void initialize() {
        initializeChatbot();

        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList("Récents", "A-Z", "Z-A"));
            sortCombo.setValue("Récents");
        }

        universeList.setItems(universes);
        
        // Amélioration de l'aspect visuel de la liste
        universeList.setCellFactory(param -> new ListCell<Universe>() {
            @Override
            protected void updateItem(Universe u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null || u.getName() == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-background-color: #1a1f1e; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #2a3139; -fx-border-radius: 10;");
                    
                    Label nameLbl = new Label(u.getName());
                    nameLbl.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 18px; -fx-font-weight: bold;");
                    
                    Label genreLbl = new Label(u.getGenre() != null ? u.getGenre() : "");
                    genreLbl.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
                    
                    Label descLbl = new Label(u.getShortDescription() != null ? u.getShortDescription() : "");
                    descLbl.setStyle("-fx-text-fill: #fff; -fx-font-size: 14px;");
                    descLbl.setWrapText(true);
                    
                    card.getChildren().addAll(nameLbl, genreLbl, descLbl);
                    setGraphic(card);
                    setStyle("-fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        universeList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                showUniverseDetails(selected);
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
                String sortParam = "newest";
                if ("A-Z".equals(currentSort)) sortParam = "a-z";
                if ("Z-A".equals(currentSort)) sortParam = "z-a";
                return universeService.searchUniverses(search, genre, sortParam);
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

        currentUniverse = universe;
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

        updateChatbotUniverse(universe);
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

    @FXML
    private void handleSort() {
        if (sortCombo != null && sortCombo.getValue() != null) {
            currentSort = sortCombo.getValue();
            loadUniversesWithFilters(searchField.getText(), currentGenre);
        }
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

    @FXML
    private void playUniverseLoreVideo() {
        Universe selected = universeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un univers");
            return;
        }

        String videoPath = "src/main/resources/videos/" + selected.getName().replaceAll("\\s+", "_") + ".mp4";
        MediaView mediaView = new MediaView();
        try {
            VideoPlayerService.playVideo(videoPath, mediaView);
            showAlert("Succès", "Lecture vidéo démarrée\nVidéo: " + selected.getName());
        } catch (Exception e) {
            System.out.println("Vidéo non trouvée pour: " + selected.getName());
        }
    }

    @FXML
    private void notifyUniverseUpdate() {
        Universe selected = universeList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            EmailNotificationService.notifyUniverseUpdate(
                "admin@example.com",
                selected.getName(),
                "Univers " + selected.getName() + " has been updated with new content!"
            );
        }
    }

    private void initializeChatbot() {
        try {
            if (tabPane != null) {
                Tab chatTab = tabPane.getTabs().stream()
                    .filter(tab -> tab.getText() != null && tab.getText().contains("Ask"))
                    .findFirst()
                    .orElse(null);

                if (chatTab != null && chatTab.getContent() != null) {
                    chatbotController = (ChatbotPanelController) chatTab.getContent().getUserData();
                    if (chatbotController == null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/view/ChatbotPanel.fxml"));
                            chatTab.setContent(loader.load());
                            chatbotController = loader.getController();
                        } catch (Exception e) {
                            System.err.println("Failed to load chatbot: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Chatbot initialization error: " + e.getMessage());
        }
    }

    private void updateChatbotUniverse(Universe universe) {
        if (chatbotController != null && universe != null) {
            chatbotController.setUniverse(
                universe.getName(),
                universe.getStoryContext() != null ? universe.getStoryContext() : universe.getShortDescription()
            );
        }
    }
}