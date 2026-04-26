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

// Constantes de validation
class ValidationConstants {
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

    // Header search elements
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

        // Ajouter des listeners pour les filtres
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> filterArtefacts());
        }
        if (typeFilter != null) {
            typeFilter.valueProperty().addListener((obs, old, newVal) -> filterArtefacts());
        }
        if (myItemsCheckbox != null) {
            myItemsCheckbox.selectedProperty().addListener((obs, old, newVal) -> filterArtefacts());
        }

        // ⭐ Contrôles de saisie - Limiter la longueur du champ de recherche
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.length() > ValidationConstants.MAX_SEARCH_LENGTH) {
                    searchField.setText(oldVal);
                    showValidationError("La recherche ne peut pas dépasser " + ValidationConstants.MAX_SEARCH_LENGTH
                            + " caractères");
                }
            });
        }
    }

    // ⭐ MÉTHODES DE VALIDATION DE SAISIE ⭐

    /**
     * Valide le texte de recherche
     * 
     * @return true si valide, false sinon
     */
    private boolean validateSearchInput(String searchText) {
        if (searchText == null) {
            return true; // Null est acceptable (pas de filtre)
        }

        // Vérifier la longueur
        if (searchText.length() > ValidationConstants.MAX_SEARCH_LENGTH) {
            showValidationError(
                    "La recherche ne peut pas dépasser " + ValidationConstants.MAX_SEARCH_LENGTH + " caractères");
            return false;
        }

        // Vérifier les caractères autorisés
        if (!ValidationConstants.SAFE_INPUT_PATTERN.matcher(searchText).matches()) {
            showValidationError("Caractères spéciaux non autorisés dans la recherche");
            return false;
        }

        return true;
    }

    /**
     * Nettoie et valide l'entrée utilisateur
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Supprimer les espaces multiples et trim
        String sanitized = input.trim().replaceAll("\\s+", " ");
        return sanitized;
    }

    /**
     * Affiche une erreur de validation
     */
    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ⭐ FIN DES MÉTHODES DE VALIDATION ⭐

    private void loadArtefacts() {
        Task<List<Artefact>> task = new Task<List<Artefact>>() {
            @Override
            protected List<Artefact> call() throws Exception {
                return artefactService.select();
            }
        };
        task.setOnSucceeded(e -> {
            List<Artefact> artefacts = task.getValue();
            artefactList.setAll(artefacts);
            filterArtefacts(); // ⭐ Appliquer les filtres après chargement
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Erreur lors du chargement: " + (ex != null ? ex.getMessage() : "Unknown"));
            ex.printStackTrace();
        });
        new Thread(task).start();
    }

    // ⭐ NOUVELLE MÉTHODE POUR FILTRER LES ARTEFACTS ⭐
    private void filterArtefacts() {
        String search = (searchField != null && searchField.getText() != null) ? searchField.getText().toLowerCase()
                : "";
        String type = (typeFilter != null && typeFilter.getValue() != null) ? typeFilter.getValue() : "Tous";
        boolean myItems = (myItemsCheckbox != null) ? myItemsCheckbox.isSelected() : false;

        // ⭐ Valider la saisie avant de filtrer
        if (!validateSearchInput(searchField != null ? searchField.getText() : null)) {
            return; // Ne pas filtrer si la validation échoue
        }

        // ⭐ Nettoyer la recherche
        String cleanSearch = sanitizeInput(search);

        // ⭐ Créer des variables effectively final pour le lambda
        final String finalSearch = cleanSearch;
        final String finalType = type;
        final boolean finalMyItems = myItems;

        List<Artefact> filtered = artefactList.stream()
                .filter(artefact -> {
                    // Filtre par recherche (nom ou univers)
                    if (!finalSearch.isEmpty()) {
                        if (!artefact.getName().toLowerCase().contains(finalSearch) &&
                                !artefact.getUniverse().toLowerCase().contains(finalSearch) &&
                                !artefact.getOrigins().toLowerCase().contains(finalSearch)) {
                            return false;
                        }
                    }

                    // Filtre par type
                    if (!finalType.equals("Tous") && !finalType.equals(artefact.getType())) {
                        return false;
                    }

                    // Filtre "Mes artefacts"
                    if (finalMyItems && UserSession.isLoggedIn()) {
                        if (artefact.getCreatedBy() != null &&
                                artefact.getCreatedBy().getId() != UserSession.getCurrentUserId()) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        displayArtefactsAsCards(filtered);
        updateStats(filtered);
        if (resultCountLabel != null) {
            resultCountLabel.setText(filtered.size() + " artefact(s) trouvé(s)");
        }
    }

    private void displayArtefactsAsCards(List<Artefact> artefacts) {
        if (artefactGrid == null)
            return;

        Platform.runLater(() -> {
            artefactGrid.getChildren().clear();
            for (Artefact artefact : artefacts) {
                ArtefactCard card = new ArtefactCard(artefact, () -> {
                    currentArtefact = artefact;
                    goToShowArtefact();
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
        filterArtefacts(); // ⭐ Appliquer les filtres
    }

    @FXML
    private void resetFilters() {
        if (searchField != null)
            searchField.clear();
        if (typeFilter != null)
            typeFilter.setValue("Tous");
        if (myItemsCheckbox != null)
            myItemsCheckbox.setSelected(false);
        filterArtefacts(); // ⭐ Rafraîchir après réinitialisation
    }

    @FXML
    private void toggleMyItems() {
        filterArtefacts(); // ⭐ Appliquer le filtre
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
    public void goToShowArtefact() {
        if (currentArtefact != null) {
            selectedArtefactForShow = currentArtefact;
            navigateTo("/artefact/show");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un artefact");
        }
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshList() {
        loadArtefacts();
    }

    @FXML
    public void toggleSearchBar() {
        // Implement search bar toggle if needed
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