package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.dao.OeuvreDAO;
import com.example.app.utils.UserSession;
import com.example.app.services.MoondreamService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

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
    
    @FXML private Button generateAIBtn;
    @FXML private ProgressIndicator loadingIndicator;

    private OeuvreDAO oeuvreDAO = new OeuvreDAO();
    private Oeuvre editingOeuvre;
    private MoondreamService moondreamService;
    
    private static Oeuvre oeuvreToEdit;

    public static void setEditingOeuvre(Oeuvre oeuvre) {
        oeuvreToEdit = oeuvre;
    }

    @FXML
    public void initialize() {
        appliquerStyleTexteVisible();
        
        moondreamService = new MoondreamService();
        
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Livre", "Film", "Série", "Jeu Vidéo", "Bande Dessinée", "Manga", "Autre");
        }
        if (universeComboBox != null) {
            universeComboBox.getItems().addAll("Marvel", "DC", "Harry Potter", "Star Wars", "Seigneur des Anneaux", "Autre");
        }
        
        if (oeuvreToEdit != null) {
            editingOeuvre = oeuvreToEdit;
            titleField.setText(editingOeuvre.getTitle());
            authorField.setText(editingOeuvre.getAuthor());
            descriptionArea.setText(editingOeuvre.getDescription());
            typeComboBox.setValue(editingOeuvre.getType());
            imageUrlField.setText(editingOeuvre.getImageUrl());
            oeuvreToEdit = null;
        }
        
        updateAIBtnStatus();
    }
    
    private void updateAIBtnStatus() {
        if (moondreamService.isMoondreamAvailable()) {
            generateAIBtn.setText("🤖 Générer description avec IA");
            generateAIBtn.setStyle("-fx-background-color: #18E3A4; -fx-text-fill: #0a0c10; -fx-font-weight: bold; -fx-background-radius: 20;");
        } else {
            generateAIBtn.setText("🎨 Générer description (simulation)");
            generateAIBtn.setStyle("-fx-background-color: #6a7a8a; -fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 20;");
        }
    }
    
    private void appliquerStyleTexteVisible() {
        if (titleField != null) {
            titleField.setStyle("-fx-text-fill: #000000; -fx-background-color: #ffffff;");
            titleField.setPromptText("Ex: Le Secret des Anciens...");
        }
        
        if (authorField != null) {
            authorField.setStyle("-fx-text-fill: #000000; -fx-background-color: #ffffff;");
            authorField.setPromptText("Votre nom ou pseudonyme");
        }
        
        if (descriptionArea != null) {
            descriptionArea.setStyle("-fx-text-fill: #000000; -fx-background-color: #ffffff;");
            descriptionArea.setPromptText("Décrivez votre œuvre, son contexte, ses inspirations...");
        }
        
        if (imageUrlField != null) {
            imageUrlField.setStyle("-fx-text-fill: #000000; -fx-background-color: #ffffff;");
            imageUrlField.setPromptText("Chemin de l'image ou URL");
        }
    }
    
    @FXML
    private void genererAvecAI() {
        String imagePath = imageUrlField.getText().trim();
        
        if (imagePath.isEmpty()) {
            showAlert("Erreur", "Veuillez d'abord sélectionner ou entrer le chemin d'une image.");
            return;
        }
        
        String localPath = extraireCheminLocal(imagePath);
        if (localPath == null || !new File(localPath).exists()) {
            showAlert("Erreur", "L'image n'existe pas ou le chemin est invalide.");
            return;
        }
        
        generateAIBtn.setDisable(true);
        loadingIndicator.setVisible(true);
        
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return moondreamService.genererDescriptionArtistique(localPath);
            }
        };
        
        task.setOnSucceeded(e -> {
            String description = task.getValue();
            
            Platform.runLater(() -> {
                descriptionArea.clear();
                descriptionArea.setText(description);
                descriptionArea.positionCaret(description.length());
                
                System.out.println("✅ Description insérée avec succès (" + description.length() + " caractères)");
            });
            
            generateAIBtn.setDisable(false);
            loadingIndicator.setVisible(false);
            showAlert("Succès", "Description générée par l'IA !");
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable ex = task.getException();
                System.err.println("❌ Erreur génération IA : " + ex.getMessage());
                ex.printStackTrace();
                showAlert("Erreur", "Impossible de générer la description :\n" + ex.getMessage());
            });
            
            generateAIBtn.setDisable(false);
            loadingIndicator.setVisible(false);
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void parcourirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageUrlField.setText(selectedFile.getAbsolutePath());
            System.out.println("📁 Image sélectionnée : " + selectedFile.getAbsolutePath());
        }
    }
    
    private String extraireCheminLocal(String path) {
        if (path == null || path.trim().isEmpty()) return null;
        
        String cleanPath = path.trim();
        
        try {
            cleanPath = URLDecoder.decode(cleanPath, StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
        
        if (cleanPath.startsWith("file:///")) {
            cleanPath = cleanPath.substring(8);
        } else if (cleanPath.startsWith("file:/")) {
            cleanPath = cleanPath.substring(6);
        }
        
        if (cleanPath.matches("^/[A-Za-z]:.*")) {
            cleanPath = cleanPath.substring(1);
        }
        
        return cleanPath;
    }

    @FXML
    private void handleSave() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Vous devez être connecté pour sauvegarder.");
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
                showAlert("Succès", "L'œuvre a été mise à jour avec succès.");
            } else {
                oeuvreDAO.add(oeuvre);
                showAlert("Succès", "L'œuvre a été créée avec succès.");
            }

            navigateTo("/oeuvre");

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        navigateTo("/oeuvre");
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}