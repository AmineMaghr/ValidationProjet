package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.dao.OeuvreDAO;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class OeuvreFormController extends BaseController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField imageUrlField;
    @FXML private ComboBox<String> universeComboBox;

    @FXML private Label titleError;
    @FXML private Label descriptionError;
    @FXML private Label authorError;
    @FXML private Label imageUrlError;

    private static final Pattern TITLE_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\p{Punct}]{2,200}$");
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("^[a-zA-Z\\s.-]{2,100}$");

    private OeuvreDAO oeuvreDAO = new OeuvreDAO();
    private Oeuvre editingOeuvre;

    // ⭐ MÉTHODE STATIQUE POUR PASSER L'OEUVRE À MODIFIER
    private static Oeuvre oeuvreToEdit;

    public static void setEditingOeuvre(Oeuvre oeuvre) {
        oeuvreToEdit = oeuvre;
    }

    public static Oeuvre getEditingOeuvre() {
        return oeuvreToEdit;
    }

    @FXML
    public void initialize() {
        setupValidationListeners();
        loadComboBoxData();
        
        // ⭐ Charger l'œuvre à modifier si elle existe
        if (oeuvreToEdit != null) {
            editingOeuvre = oeuvreToEdit;
            titleField.setText(editingOeuvre.getTitle());
            authorField.setText(editingOeuvre.getAuthor());
            descriptionArea.setText(editingOeuvre.getDescription());
            typeComboBox.setValue(editingOeuvre.getType());
            imageUrlField.setText(editingOeuvre.getImageUrl());
            oeuvreToEdit = null;
        }
    }

    private void setupValidationListeners() {
        if (titleField != null) {
            titleField.textProperty().addListener((obs, old, val) -> validateTitle());
        }
        if (authorField != null) {
            authorField.textProperty().addListener((obs, old, val) -> validateAuthor());
        }
        if (descriptionArea != null) {
            descriptionArea.textProperty().addListener((obs, old, val) -> validateDescription());
        }
        if (imageUrlField != null) {
            imageUrlField.textProperty().addListener((obs, old, val) -> validateImageUrl());
        }
    }

    private boolean validateTitle() {
        if (titleField == null || titleError == null) return true;
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) {
            titleError.setText("Le titre est requis");
            titleError.setVisible(true);
            titleField.setStyle("-fx-border-color: red;");
            return false;
        }
        if (!TITLE_PATTERN.matcher(title.trim()).matches()) {
            titleError.setText("Le titre doit contenir 2-200 caractères");
            titleError.setVisible(true);
            titleField.setStyle("-fx-border-color: red;");
            return false;
        }
        titleError.setVisible(false);
        titleField.setStyle("");
        return true;
    }

    private boolean validateAuthor() {
        if (authorField == null || authorError == null) return true;
        String author = authorField.getText();
        if (author == null || author.trim().isEmpty()) {
            authorError.setText("L'auteur est requis");
            authorError.setVisible(true);
            authorField.setStyle("-fx-border-color: red;");
            return false;
        }
        if (!AUTHOR_PATTERN.matcher(author.trim()).matches()) {
            authorError.setText("L'auteur doit contenir 2-100 caractères");
            authorError.setVisible(true);
            authorField.setStyle("-fx-border-color: red;");
            return false;
        }
        authorError.setVisible(false);
        authorField.setStyle("");
        return true;
    }

    private boolean validateDescription() {
        if (descriptionArea == null || descriptionError == null) return true;
        String description = descriptionArea.getText();
        if (description == null || description.trim().isEmpty()) {
            descriptionError.setText("La description est requise");
            descriptionError.setVisible(true);
            descriptionArea.setStyle("-fx-border-color: red;");
            return false;
        }
        if (description.length() < 20) {
            descriptionError.setText("La description doit contenir au moins 20 caractères");
            descriptionError.setVisible(true);
            descriptionArea.setStyle("-fx-border-color: red;");
            return false;
        }
        descriptionError.setVisible(false);
        descriptionArea.setStyle("");
        return true;
    }

    private boolean validateImageUrl() {
        if (imageUrlField == null || imageUrlError == null) return true;
        String url = imageUrlField.getText();
        if (url == null || url.trim().isEmpty()) {
            imageUrlError.setText("L'URL de l'image est requise");
            imageUrlError.setVisible(true);
            imageUrlField.setStyle("-fx-border-color: red;");
            return false;
        }
        imageUrlError.setVisible(false);
        imageUrlField.setStyle("");
        return true;
    }

    private boolean validateAllFields() {
        return validateTitle() && validateAuthor() && validateDescription() && validateImageUrl();
    }

    private void loadComboBoxData() {
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Livre", "Film", "Série", "Jeu Vidéo", "Bande Dessinée", "Manga", "Autre");
        }
        if (universeComboBox != null) {
            universeComboBox.getItems().addAll("Marvel", "DC", "Harry Potter", "Star Wars", "Seigneur des Anneaux", "Autre");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateAllFields()) {
            showAlert("Erreur de validation", "Veuillez corriger les erreurs dans le formulaire");
            return;
        }

        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté");
            navigateTo("/login");
            return;
        }

        try {
            Oeuvre oeuvre = new Oeuvre();
            oeuvre.setTitle(titleField.getText().trim());
            oeuvre.setAuthor(authorField.getText().trim());
            oeuvre.setDescription(descriptionArea.getText().trim());
            oeuvre.setType(typeComboBox.getValue());
            oeuvre.setImageUrl(imageUrlField.getText().trim());
            oeuvre.setCreateurId(UserSession.getCurrentUser().getId());

            if (editingOeuvre != null) {
                oeuvre.setId(editingOeuvre.getId());
                oeuvreDAO.update(oeuvre);
                showAlert("Succès", "L'œuvre a été mise à jour");
            } else {
                oeuvreDAO.add(oeuvre);
                showAlert("Succès", "L'œuvre a été créée");
            }

            navigateTo("/oeuvre");

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur base de données: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        navigateTo("/oeuvre");
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}