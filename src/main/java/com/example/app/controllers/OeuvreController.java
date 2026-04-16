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
import java.util.stream.Collectors;

public class OeuvreController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private CheckBox myItemsCheckbox;
    @FXML private TilePane oeuvreGrid;
    @FXML private Label totalOeuvresLabel;
    @FXML private Label totalAuteursLabel;
    @FXML private Label totalTypesLabel;
    @FXML private Label resultCountLabel;

    private OeuvreDAO oeuvreDAO = new OeuvreDAO();
    private ObservableList<Oeuvre> oeuvreList = FXCollections.observableArrayList();
    private Oeuvre currentOeuvre;

    @FXML
    public void initialize() {
        loadOeuvres();
        loadTypes();
    }

    private void loadOeuvres() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                String search = searchField != null ? searchField.getText() : null;
                String type = typeFilter != null ? typeFilter.getValue() : null;
                boolean myItems = myItemsCheckbox != null && myItemsCheckbox.isSelected();
                Integer userId = myItems && UserSession.isLoggedIn() ? UserSession.getCurrentUser().getId() : null;
                
                if ((search != null && !search.isEmpty()) || (type != null && !type.equals("Tous"))) {
                    return oeuvreDAO.searchOeuvres(search, type, userId);
                } else if (myItems) {
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
        if (oeuvreGrid == null) return;
        
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
        if (searchField != null) searchField.clear();
        if (typeFilter != null) typeFilter.setValue("Tous");
        if (myItemsCheckbox != null) myItemsCheckbox.setSelected(false);
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
    
    public static Oeuvre getSelectedOeuvreForShow() {
        return selectedOeuvreForShow;
    }
    
    @Override
    public void goAccueil() {
        navigateTo("/");
    }
}
