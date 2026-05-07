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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ArtefactValidationConstants {
    public static final int MAX_SEARCH_LENGTH = 100;
    public static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\sÀ-ÿ_-]*$");
}

public class ArtefactController extends BaseController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private CheckBox myItemsCheckbox;
    @FXML
    private TilePane artefactGrid;
    @FXML
    private Label totalArtefactsLabel;
    @FXML
    private Label totalUniversLabel;
    @FXML
    private Label totalTypesLabel;
    @FXML
    private Label resultCountLabel;

    @FXML
    private TextField searchInput;
    @FXML
    private ListView<String> searchResults;

    private ArtefactService artefactService = new ArtefactService();
    private ObservableList<Artefact> artefactList = FXCollections.observableArrayList();
    private Artefact currentArtefact;

    @FXML
    public void initialize() {
        loadArtefacts();
        loadTypes();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> filterArtefacts());
        }
        if (typeFilter != null) {
            typeFilter.valueProperty().addListener((obs, old, newVal) -> filterArtefacts());
        }
        if (myItemsCheckbox != null) {
            myItemsCheckbox.selectedProperty().addListener((obs, old, newVal) -> filterArtefacts());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.length() > ArtefactValidationConstants.MAX_SEARCH_LENGTH) {
                    searchField.setText(oldVal);
                    showValidationError("La recherche ne peut pas dépasser " 
                        + ArtefactValidationConstants.MAX_SEARCH_LENGTH + " caractères");
                }
            });
        }
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadArtefacts() {
        System.out.println("=== [ARTEFACT] loadArtefacts() appelée ===");
        Task<List<Artefact>> task = new Task<List<Artefact>>() {
            @Override
            protected List<Artefact> call() throws Exception {
                return artefactService.select();
            }
        };
        task.setOnSucceeded(e -> {
            List<Artefact> artefacts = task.getValue();
            artefactList.setAll(artefacts);
            System.out.println("=== [ARTEFACT] " + artefacts.size() + " artefacts chargés");
            filterArtefacts();
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            System.err.println("=== [ARTEFACT] Erreur: " + (ex != null ? ex.getMessage() : "Unknown"));
            ex.printStackTrace();
        });
        new Thread(task).start();
    }

    private void filterArtefacts() {
        String search = (searchField != null && searchField.getText() != null) ? searchField.getText().toLowerCase() : "";
        String type = (typeFilter != null && typeFilter.getValue() != null) ? typeFilter.getValue() : "Tous";
        boolean myItems = (myItemsCheckbox != null) ? myItemsCheckbox.isSelected() : false;

        System.out.println("=== [ARTEFACT] filterArtefacts - myItems=" + myItems + 
                           ", userId=" + (UserSession.isLoggedIn() ? UserSession.getCurrentUserId() : "non connecté"));

        List<Artefact> filtered = artefactList.stream()
                .filter(artefact -> {
                    if (!search.isEmpty()) {
                        if (!artefact.getName().toLowerCase().contains(search) &&
                            !artefact.getUniverse().toLowerCase().contains(search) &&
                            !artefact.getOrigins().toLowerCase().contains(search)) {
                            return false;
                        }
                    }

                    if (!type.equals("Tous") && !type.equals(artefact.getType())) {
                        return false;
                    }

                    if (myItems && UserSession.isLoggedIn()) {
                        int currentUserId = UserSession.getCurrentUserId();
                        int artefactCreatorId = artefact.getCreatedBy() != null ? artefact.getCreatedBy().getId() : -1;
                        System.out.println("  - Artefact: " + artefact.getName() + 
                                           ", creatorId=" + artefactCreatorId + 
                                           ", currentUserId=" + currentUserId);
                        if (artefactCreatorId != currentUserId) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        System.out.println("=== [ARTEFACT] " + filtered.size() + " artefacts après filtrage");
        displayArtefactsAsCards(filtered);
        updateStats(filtered);
        if (resultCountLabel != null) {
            resultCountLabel.setText(filtered.size() + " artefact(s)");
        }
    }

    private void displayArtefactsAsCards(List<Artefact> artefacts) {
        if (artefactGrid == null) return;

        Platform.runLater(() -> {
            artefactGrid.getChildren().clear();
            for (Artefact artefact : artefacts) {
                ArtefactCard card = new ArtefactCard(artefact, () -> {
                    currentArtefact = artefact;
                    setSelectedArtefactForShow(artefact);
                    navigateTo("/artefact/show");
                });
                artefactGrid.getChildren().add(card);
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
        filterArtefacts();
    }

    @FXML
    private void resetFilters() {
        if (searchField != null) searchField.clear();
        if (typeFilter != null) typeFilter.setValue("Tous");
        if (myItemsCheckbox != null) myItemsCheckbox.setSelected(false);
        filterArtefacts();
    }

    @FXML
    private void toggleMyItems() {
        filterArtefacts();
    }

    private static Artefact selectedArtefactForShow;

    public static void setSelectedArtefactForShow(Artefact artefact) {
        selectedArtefactForShow = artefact;
    }

    public static Artefact getSelectedArtefactForShow() {
        return selectedArtefactForShow;
    }

    @FXML
    public void goToCreateArtefact() {
        navigateTo("/artefact/create");
    }

    @FXML
    public void toggleSearchBar() {
    }

    @FXML
    public void goAccueil() {
        navigateTo("/");
    }

    @FXML
    public void goDiscover() {
        navigateTo("/discover");
    }

    @FXML
    public void goUniverses() {
        navigateTo("/universes");
    }

    @FXML
    public void goPersonnages() {
        navigateTo("/personnages");
    }

    @FXML
    public void goOeuvres() {
        navigateTo("/oeuvre");
    }

    @FXML
    public void goArtefacts() {
        navigateTo("/artefact");
    }

    @FXML
    public void goShop() {
        navigateTo("/shop");
    }

    @FXML
    public void goChallenges() {
        navigateTo("/challenges");
    }
}