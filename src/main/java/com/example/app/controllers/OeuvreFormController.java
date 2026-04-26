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

    // Champs du formulaire (seuls les champs existants dans l'entité)
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField authorField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField imageUrlField;
    @FXML
    private ComboBox<String> universeComboBox;

    // Labels d'erreur
    @FXML
    private Label titleError;
    @FXML
    private Label descriptionError;
    @FXML
    private Label authorError;
    @FXML
    private Label imageUrlError;

    // Patterns de validation
    private static final Pattern TITLE_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\p{Punct}]{2,200}$");
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("^[a-zA-Z\\s.-]{2,100}$");
    private static final Pattern URL_PATTERN = Pattern
            .compile("^(https?://)?([\\da-z.-]+\\.([a-z.]{2,6})[/\\w .-]*/?)$");

    private OeuvreDAO oeuvreDAO = new OeuvreDAO();
    private Oeuvre editingOeuvre;

    @FXML
    public void initialize() {
        setupValidationListeners();
        loadComboBoxData();
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

    // ============= VALIDATIONS INDIVIDUELLES =============

    private boolean validateTitle() {
        if (titleField == null || titleError == null)
            return true;

        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) {
            titleError.setText("Le titre est requis");
            titleError.setVisible(true);
            titleField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (!TITLE_PATTERN.matcher(title.trim()).matches()) {
            titleError.setText("Le titre doit contenir 2-200 caractères (lettres, chiffres, ponctuation)");
            titleError.setVisible(true);
            titleField.setStyle("-fx-border-color: red;");
            return false;
        }

        titleError.setVisible(false);
        titleField.setStyle("");
        return true;
    }

    private boolean validateAuthor() {
        if (authorField == null || authorError == null)
            return true;

        String author = authorField.getText();
        if (author == null || author.trim().isEmpty()) {
            authorError.setText("L'auteur est requis");
            authorError.setVisible(true);
            authorField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (!AUTHOR_PATTERN.matcher(author.trim()).matches()) {
            authorError.setText("L'auteur doit contenir 2-100 caractères (lettres, espaces, points, tirets)");
            authorError.setVisible(true);
            authorField.setStyle("-fx-border-color: red;");
            return false;
        }

        authorError.setVisible(false);
        authorField.setStyle("");
        return true;
    }

    private boolean validateDescription() {
        if (descriptionArea == null || descriptionError == null)
            return true;

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

        if (description.length() > 5000) {
            descriptionError.setText("La description ne peut pas dépasser 5000 caractères");
            descriptionError.setVisible(true);
            descriptionArea.setStyle("-fx-border-color: red;");
            return false;
        }

        descriptionError.setVisible(false);
        descriptionArea.setStyle("");
        return true;
    }

    private boolean validateImageUrl() {
        if (imageUrlField == null || imageUrlError == null)
            return true;

        String url = imageUrlField.getText();
        if (url == null || url.trim().isEmpty()) {
            imageUrlError.setText("L'URL de l'image est requise");
            imageUrlError.setVisible(true);
            imageUrlField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (!URL_PATTERN.matcher(url.trim()).matches()) {
            imageUrlError.setText("URL invalide");
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
            try {
                List<com.example.app.entities.Universe> universes = new com.example.app.dao.UniverseDAO().select();
                universeComboBox.getItems().add("Autre");
                for (com.example.app.entities.Universe u : universes) {
                    universeComboBox.getItems().add(u.getName());
                }
            } catch (SQLException e) {
                universeComboBox.getItems().addAll("Marvel", "DC", "Harry Potter", "Star Wars", "Seigneur des Anneaux",
                        "Autre");
            }
        }
    }

    @FXML
    private void handleSave() {
        // Validation avant sauvegarde
        if (!validateAllFields()) {
            showAlert("Erreur de validation", "Veuillez corriger les erreurs dans le formulaire");
            return;
        }

        // Vérifier si l'utilisateur est connecté
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour créer/modifier une œuvre");
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
            oeuvre.setCreatedBy(UserSession.getCurrentUser());

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

    public void setEditingOeuvre(Oeuvre oeuvre) {
        this.editingOeuvre = oeuvre;
        if (oeuvre != null) {
            titleField.setText(oeuvre.getTitle());
            authorField.setText(oeuvre.getAuthor());
            descriptionArea.setText(oeuvre.getDescription());
            typeComboBox.setValue(oeuvre.getType());
            imageUrlField.setText(oeuvre.getImageUrl());
        }
    }

    @FXML
    private void handleCancel() {
        navigateTo("/oeuvre");
    }
}