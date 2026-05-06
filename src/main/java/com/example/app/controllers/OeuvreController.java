package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.dao.OeuvreDAO;
import com.example.app.utils.UserSession;
import com.example.app.views.OeuvreCard;
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
class OeuvreValidationConstants {
    public static final int MAX_SEARCH_LENGTH = 100;
    public static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\sÀ-ÿ_-]*$");
}

public class OeuvreController extends BaseController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private CheckBox myItemsCheckbox;
    @FXML
    private TilePane oeuvreGrid;
    @FXML
    private Label totalOeuvresLabel;
    @FXML
    private Label totalAuteursLabel;
    @FXML
    private Label totalTypesLabel;
    @FXML
    private Label resultCountLabel;

    // Header search elements
    @FXML
    private TextField searchInput;
    @FXML
    private ListView<String> searchResults;

    private OeuvreDAO oeuvreDAO = new OeuvreDAO();
    private ObservableList<Oeuvre> oeuvreList = FXCollections.observableArrayList();
    private Oeuvre currentOeuvre;

    @FXML
    public void initialize() {
        loadOeuvres();
        loadTypes();

        // ⭐ Contrôles de saisie - Limiter la longueur du champ de recherche
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.length() > OeuvreValidationConstants.MAX_SEARCH_LENGTH) {
                    searchField.setText(oldVal);
                    showValidationError("La recherche ne peut pas dépasser "
                            + OeuvreValidationConstants.MAX_SEARCH_LENGTH + " caractères");
                }
            });
        }

        // ⭐ Ajouter des listeners pour les filtres
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> loadOeuvres());
        }
        if (typeFilter != null) {
            typeFilter.valueProperty().addListener((obs, old, newVal) -> loadOeuvres());
        }
        if (myItemsCheckbox != null) {
            myItemsCheckbox.selectedProperty().addListener((obs, old, newVal) -> loadOeuvres());
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
        if (searchText.length() > OeuvreValidationConstants.MAX_SEARCH_LENGTH) {
            showValidationError(
                    "La recherche ne peut pas dépasser " + OeuvreValidationConstants.MAX_SEARCH_LENGTH + " caractères");
            return false;
        }

        // Vérifier les caractères autorisés
        if (!OeuvreValidationConstants.SAFE_INPUT_PATTERN.matcher(searchText).matches()) {
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

    private void loadOeuvres() {
        // ⭐ Valider la saisie avant de charger
        String search = searchField != null ? searchField.getText() : null;
        if (!validateSearchInput(search)) {
            return; // Ne pas charger si la validation échoue
        }

        // ⭐ Nettoyer la recherche
        String cleanSearch = sanitizeInput(search);

        // ⭐ Créer des variables effectively final pour le Task
        final String finalSearch = cleanSearch;
        final String finalType = typeFilter != null ? typeFilter.getValue() : null;
        final boolean finalMyItems = myItemsCheckbox != null && myItemsCheckbox.isSelected();

        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                Integer userId = finalMyItems && UserSession.isLoggedIn() ? UserSession.getCurrentUser().getId() : null;

                if ((finalSearch != null && !finalSearch.isEmpty())
                        || (finalType != null && !finalType.equals("Tous"))) {
                    return oeuvreDAO.searchOeuvres(finalSearch, finalType, userId);
                } else if (finalMyItems) {
                    return oeuvreDAO.searchOeuvres(null, null, userId);
                } else {
                    return oeuvreDAO.select();
                }
            }
        };
        task.setOnSucceeded(e -> {
            List<Oeuvre> oeuvres = task.getValue();
            oeuvreList.setAll(oeuvres);
            displayOeuvresAsCards(oeuvres);
            updateStats(oeuvres);
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Erreur lors du chargement: " + (ex != null ? ex.getMessage() : "Unknown"));
            ex.printStackTrace();
        });
        new Thread(task).start();
    }

    private void displayOeuvresAsCards(List<Oeuvre> oeuvres) {
        if (oeuvreGrid == null)
            return;

        javafx.application.Platform.runLater(() -> {
            oeuvreGrid.getChildren().clear();
            for (Oeuvre oeuvre : oeuvres) {
                OeuvreCard card = new OeuvreCard(oeuvre, () -> {
                    currentOeuvre = oeuvre;
                    goToShowOeuvre();
                });
                oeuvreGrid.getChildren().add(card);
            }
            if (resultCountLabel != null) {
                resultCountLabel.setText(oeuvres.size() + " œuvre(s)");
            }
        });
    }

    private void updateStats(List<Oeuvre> oeuvres) {
        if (totalOeuvresLabel != null) {
            totalOeuvresLabel.setText(String.valueOf(oeuvres.size()));
        }

        if (totalAuteursLabel != null) {
            long auteursCount = oeuvres.stream()
                    .map(Oeuvre::getAuthor)
                    .filter(a -> a != null && !a.isEmpty())
                    .distinct()
                    .count();
            totalAuteursLabel.setText(String.valueOf(auteursCount));
        }

        if (totalTypesLabel != null) {
            long typesCount = oeuvres.stream()
                    .map(Oeuvre::getType)
                    .filter(t -> t != null && !t.isEmpty())
                    .distinct()
                    .count();
            totalTypesLabel.setText(String.valueOf(typesCount));
        }
    }

    private void loadTypes() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return oeuvreDAO.getAvailableTypes();
            }
        };
        task.setOnSucceeded(e -> {
            List<String> types = task.getValue();
            if (typeFilter != null) {
                typeFilter.setItems(FXCollections.observableArrayList(types));
                typeFilter.getItems().add(0, "Tous");
                typeFilter.setValue("Tous");
            }
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            System.err.println("Erreur chargement types: " + ex.getMessage());
        });
        new Thread(task).start();
    }

    @FXML
    private void search() {
        loadOeuvres();
    }

    @FXML
    private void resetFilters() {
        if (searchField != null)
            searchField.clear();
        if (typeFilter != null)
            typeFilter.setValue("Tous");
        if (myItemsCheckbox != null)
            myItemsCheckbox.setSelected(false);
        loadOeuvres();
    }

    @FXML
    private void toggleMyItems() {
        loadOeuvres();
    }

    private static Oeuvre selectedOeuvreForShow;

    @FXML
    public void goToCreateOeuvre() {
        navigateTo("/oeuvre/create");
    }

    @FXML
    public void goToShowOeuvre() {
        if (currentOeuvre != null) {
            selectedOeuvreForShow = currentOeuvre;
            navigateTo("/oeuvre/show");
        } else {
            showAlert("Erreur", "Veuillez sélectionner une œuvre");
        }
    }

    public static void setSelectedOeuvreForShow(Oeuvre oeuvre) {
        selectedOeuvreForShow = oeuvre;
    }

    public static Oeuvre getSelectedOeuvreForShow() {
        return selectedOeuvreForShow;
    }

    @Override
    public void goAccueil() {
        navigateTo("/");
    }

    @FXML
    public void toggleSearchBar() {
        // Implement search bar toggle if needed
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
