package com.midgar.controller;

import com.example.app.controllers.BaseController;
import com.example.app.entities.Defi;
import com.example.app.entities.Participation;
import com.example.app.services.DefiService;
import com.example.app.services.ParticipationService;
import com.example.app.utils.UserSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for the participer.fxml screen
 */
public class ParticiperController extends BaseController {

    @FXML private ImageView challengeImageView;
    @FXML private Label challengeTitleLabel;
    @FXML private Label challengeThemeLabel;
    @FXML private Label challengeDescriptionLabel;
    @FXML private Label challengeDatesLabel;
    @FXML private Label challengeStatusLabel;
    @FXML private TextArea descriptionField;
    @FXML private TextField artworkIdField;
    @FXML private Button submitBtn;
@FXML private HBox authContainer;
    @FXML private Button peindreButton;

    // Static variable to hold défi ID during navigation
    private static int pendingDefiId = -1;

    private DefiService defiService = new DefiService();
    private ParticipationService participationService = new ParticipationService();
    private int currentDefiId;
    private Defi currentDefi;
    
    /**
     * Sets the défi ID to be used by the next instance of ParticiperController
     * This is a workaround for passing parameters between FXML controllers
     * @param defiId The ID of the défi to participate in
     */
    public static void setPendingDefiId(int defiId) {
        pendingDefiId = defiId;
    }

    /**
     * Sets the défi ID and loads the défi information
     * @param defiId The ID of the défi to participate in
     */
    public void setDefiId(int defiId) {
        this.currentDefiId = defiId;
        loadDefiDetails();
        setupAuthButtons();
    }
    
    /**
     * Initializes the controller and loads the défi information
     */
    @FXML
    public void initialize() {
        // Initialize form validation
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
        artworkIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
        
        // Load défi details using the pending ID if available
        System.out.println("Pending défi ID: " + pendingDefiId);
        if (pendingDefiId != -1) {
            this.currentDefiId = pendingDefiId;
            System.out.println("Setting currentDefiId to: " + currentDefiId);
            pendingDefiId = -1; // Reset for next use
            loadDefiDetails();
            setupAuthButtons();
        } else {
            System.out.println("No pending défi ID");
        }
    }

     /**
      * Loads and displays the défi details
      */
     private void loadDefiDetails() {
         Task<Defi> task = new Task<>() {
             @Override
             protected Defi call() throws Exception {
                 // Get the défi by ID directly
                 return defiService.selectById(currentDefiId);
             }
         };
        
        task.setOnSucceeded(e -> {
            Defi defi = task.getValue();
            if (defi != null) {
                currentDefi = defi;
                displayDefiDetails(defi);
            } else {
                showAlert("Erreur", "Défi non trouvé");
            }
        });
        
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Impossible de charger les détails du défi: " + (ex != null ? ex.getMessage() : "Erreur inconnue"));
        });
        
        new Thread(task).start();
    }

    /**
     * Displays the défi details in the UI
     * @param defi The défi to display
     */
    private void displayDefiDetails(Defi defi) {
        challengeTitleLabel.setText(defi.getTitre());
        challengeThemeLabel.setText(defi.getTheme());
        challengeDescriptionLabel.setText(defi.getDescription());
        
        if (defi.getDateDebut() != null && defi.getDateFin() != null) {
            challengeDatesLabel.setText("📅 " + defi.getDateDebut().toString() + " → " + defi.getDateFin().toString());
        }
        
        challengeStatusLabel.setText("OUVERT".equals(defi.getStatut()) ? "Actif" : "Terminé");
        challengeStatusLabel.setStyle("-fx-text-fill: " + 
                ("OUVERT".equals(defi.getStatut()) ? "#18E3A4" : "#F85149") + 
                "; -fx-font-weight: 600;");
        
        // Load image if available
        if (defi.getImageCover() != null && !defi.getImageCover().isEmpty()) {
            try {
                Image image = new Image("file:" + defi.getImageCover());
                challengeImageView.setImage(image);
                challengeImageView.setVisible(true);
                challengeImageView.setManaged(true);
            } catch (Exception ex) {
                // Image not found, keep placeholder or hide
                challengeImageView.setVisible(false);
                challengeImageView.setManaged(false);
            }
        } else {
            challengeImageView.setVisible(false);
            challengeImageView.setManaged(false);
        }
    }

    /**
     * Sets up authentication buttons in the header
     */
    private void setupAuthButtons() {
        // Static user1@gmail.tn (ID 1)
        if (!UserSession.isLoggedIn()) {
        // Static user1@gmail.tn (ID 1) - no User instance needed
        UserSession.setCurrentUser(null); // Force static logic in soumettre()
        // Participation soumettre() uses ID 1 by default
        }
        
        authContainer.getChildren().clear();
        
        var user = UserSession.getCurrentUser();
        Button profileBtn = new Button(user != null && user.getUsername() != null ? user.getUsername() : "user1@gmail.tn");
        profileBtn.getStyleClass().addAll("magic-btn");
        profileBtn.setOnAction(e -> navigateTo("/profile"));
        
        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.getStyleClass().addAll("btn-secondary");
        logoutBtn.setOnAction(e -> {
            UserSession.logout();
            navigateTo("/defis");
        });
        authContainer.getChildren().addAll(profileBtn, logoutBtn);
    }



    /**
     * Validates the form and updates the submit button state
     */
    private void validateForm() {
        boolean descriptionFilled = descriptionField.getText() != null && !descriptionField.getText().trim().isEmpty();
        boolean artworkIdValid = true;
        
        String artworkIdText = artworkIdField.getText();
        if (artworkIdText != null && !artworkIdText.trim().isEmpty()) {
            try {
                Integer.parseInt(artworkIdText.trim());
            } catch (NumberFormatException e) {
                artworkIdValid = false;
            }
        }
        
        submitBtn.setDisable(!(descriptionFilled && artworkIdValid));
    }

    @FXML
    private void soumettre() {
        String description = descriptionField.getText();
        String artworkIdText = artworkIdField.getText();

        if (description == null || description.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez ajouter une description de votre participation");
            return;
        }

        int artworkId = 0;
        if (artworkIdText != null && !artworkIdText.trim().isEmpty()) {
            try {
                artworkId = Integer.parseInt(artworkIdText.trim());
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID de l'œuvre doit être un nombre valide");
                return;
            }
        }

        if (currentDefi == null) {
            showAlert("Erreur", "Impossible de charger les informations du défi");
            return;
        }

        Participation participation = new Participation();
        participation.setDescription(description.trim());
        participation.setStatut("EN_ATTENTE");
        
        // Use logged in user if available, otherwise use default user (ID 1)
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser() != null) {
            participation.setUserId(UserSession.getCurrentUser().getId());
        } else {
            // Default user ID when not logged in
            participation.setUserId(1);
        }
        
        participation.setDateSoumission(LocalDateTime.now());
        participation.setArtworkId(artworkId);
        participation.setDefi(currentDefi);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                participationService.add(participation);
                return null;
            }
        };
        
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Votre participation a été enregistrée !");
            // Reset form
            descriptionField.clear();
            artworkIdField.clear();
            // Navigate back to challenges
            navigateTo("/challenges");
        });
        
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + (ex != null ? ex.getMessage() : "Erreur inconnue"));
        });
        
        new Thread(task).start();
    }

    @FXML
    private void annuler() {
        // Clear form and go back
        descriptionField.clear();
        artworkIdField.clear();
        navigateTo("/challenges");
    }

    @FXML
    private void goToPaintEditor() {
        System.out.println("Navigating to paint editor - Canvas prêt !");
        navigateTo("/challenges/peindre");
    }
    
    @FXML
    private void handlePeindreButton() {
        goToPaintEditor();
    }
    
    // Navigation methods for static header buttons
    @FXML
    private void goToProfile() {
        navigateTo("/profile");
    }
    
    @FXML
    private void handleLogout() {
        UserSession.logout();
        navigateTo("/defis");
    }



    protected void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void navigateTo(String path) {
        try {
            if (com.example.app.utils.SceneManager.getInstance() == null) {
                System.err.println("SceneManager non initialisé");
                return;
            }
            com.example.app.utils.SceneManager.getInstance().loadScene(path);
        } catch (Exception e) {
            System.err.println("Erreur de navigation vers " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}