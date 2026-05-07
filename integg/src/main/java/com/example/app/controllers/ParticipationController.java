package com.example.app.controllers;

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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParticipationController extends BaseController {

    @FXML private ImageView defiImageView;
    @FXML private Label defiTitreLabel;
    @FXML private Label defiThemeLabel;
    @FXML private Label defiDescriptionLabel;
    @FXML private Label defiDatesLabel;
    @FXML private VBox uploadZone;
    @FXML private ImageView imagePreview;
    @FXML private Label fileNameLabel;
    @FXML private TextArea descriptionArea;
    @FXML private TextField artworkIdField;
    @FXML private Label errorLabel;

    private static int pendingDefiId = -1;

    public static void setPendingDefiId(int id) {
        pendingDefiId = id;
    }

    private DefiService defiService = new DefiService();
    private ParticipationService participationService = new ParticipationService();
    private Defi currentDefi;
    private File selectedImageFile;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        if (pendingDefiId != -1) {
            setDefiId(pendingDefiId);
            pendingDefiId = -1;
        }
    }


    public void setDefi(Defi defi) {
        this.currentDefi = defi;
        defiTitreLabel.setText(defi.getTitre());
        defiThemeLabel.setText(defi.getTheme());
        defiDescriptionLabel.setText(defi.getDescription());

        if (defi.getDateDebut() != null && defi.getDateFin() != null) {
            defiDatesLabel.setText("📅 Du " + defi.getDateDebut().format(dateFormatter) + " au " + defi.getDateFin().format(dateFormatter));
        }

        if (defi.getImageCover() != null && !defi.getImageCover().isEmpty()) {
            try {
                Image image = new Image("file:" + defi.getImageCover());
                defiImageView.setImage(image);
                defiImageView.setVisible(true);
                defiImageView.setManaged(true);
            } catch (Exception e) {
                System.out.println("Image non trouvée");
            }
        }
    }

    public void setDefiId(int defiId) {
        Task<Defi> task = new Task<>() {
            @Override
            protected Defi call() throws SQLException {
                return defiService.select().stream()
                        .filter(d -> d.getId() == defiId)
                        .findFirst()
                        .orElse(null);
            }
        };
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                setDefi(task.getValue());
            } else {
                showError("Défi non trouvé");
            }
        });
        task.setOnFailed(e -> showError("Erreur lors du chargement du défi"));
        new Thread(task).start();
    }

    @FXML
    private void openImageModal() {
        if (currentDefi != null && currentDefi.getImageCover() != null) {
            String imageUrl = "file:" + currentDefi.getImageCover();
            showImageModal(imageUrl, currentDefi.getTitre());
        }
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                imagePreview.setImage(image);
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);
                fileNameLabel.setText(selectedImageFile.getName());
                fileNameLabel.setVisible(true);
                fileNameLabel.setManaged(true);
                uploadZone.setVisible(false);
                uploadZone.setManaged(false);
            } catch (Exception ex) {
                showError("Impossible de charger l'image: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void soumettre() {
        String description = descriptionArea.getText();

        if (description.isEmpty()) {
            showError("Veuillez ajouter une description de votre création");
            return;
        }

        if (selectedImageFile == null && artworkIdField.getText().isEmpty()) {
            showError("Veuillez ajouter une image ou un ID d'œuvre");
            return;
        }

        if (!UserSession.isLoggedIn()) {
            showError("Veuillez vous connecter pour participer");
            navigateTo("/login");
            return;
        }

        Participation participation = new Participation();
        participation.setDefi(currentDefi);
        participation.setDescription(description);
        participation.setUserId(UserSession.getCurrentUser().getId());
        participation.setStatut("EN_ATTENTE");
        participation.setDateSoumission(LocalDateTime.now());

        if (!artworkIdField.getText().isEmpty()) {
            try {
                int artworkId = Integer.parseInt(artworkIdField.getText());
                participation.setArtworkId(artworkId);
            } catch (NumberFormatException e) {
                showError("L'ID de l'œuvre doit être un nombre");
                return;
            }
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                participationService.add(participation);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Votre participation a été enregistrée !");
            navigateTo("/challenges");
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showError("Erreur lors de l'enregistrement: " + (ex != null ? ex.getMessage() : "Unknown"));
        });
        new Thread(task).start();
    }

    @FXML
    private void openPainter() {
        if (currentDefi != null) {
            navigateTo("/challenges/peindre/" + currentDefi.getId());
        }
    }

    @FXML
    private void goBack() {
        navigateTo("/challenges");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showImageModal(String imageUrl, String title) {
        // Implémentez l'affichage de la modale d'image
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Image: " + imageUrl);
        alert.showAndWait();
    }
}