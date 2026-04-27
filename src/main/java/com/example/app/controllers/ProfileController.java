package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.entities.Oeuvre;
import com.example.app.entities.Artefact;
import com.example.app.services.UserService;
import com.example.app.services.OeuvreService;
import com.example.app.services.ArtefactService;
import com.example.app.utils.UserSession;
import com.example.app.views.OeuvreCard;
import com.example.app.views.ArtefactCard;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class ProfileController extends BaseController {
    
    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label bioLabel;
    @FXML private ImageView avatarImage;
    @FXML private Button adminBtn;
    @FXML private TabPane contentTabPane;
    @FXML private TilePane oeuvresGrid;
    @FXML private TilePane artefactsGrid;
    @FXML private TilePane favoriteOeuvresGrid;
    @FXML private TilePane favoriteArtefactsGrid;
    @FXML private Label oeuvresCountLabel;
    @FXML private Label artefactsCountLabel;
    @FXML private Label favoritesCountLabel;
    
    // Pour les modales
    @FXML private VBox editModal;
    @FXML private TextField editUsername;
    @FXML private TextField editEmail;
    @FXML private TextArea editBio;
    @FXML private ImageView previewAvatar;  // AJOUTER CETTE LIGNE !
    @FXML private VBox favorisSection;
    
    private UserService userService;
    private OeuvreService oeuvreService;
    private ArtefactService artefactService;
    private User currentUser;
    
    // Variables pour l'avatar temporaire
    private File selectedAvatarFile = null;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        oeuvreService = new OeuvreService();
        artefactService = new ArtefactService();
        
        currentUser = UserSession.getCurrentUser();
        
        loadProfileData();
        
        if (adminBtn != null) {
            adminBtn.setVisible(UserSession.isAdmin());
        }
    }
    
    private void loadProfileData() {
        if (currentUser == null) {
            showAlert("Erreur", "Utilisateur non connecté");
            navigateTo("/login");
            return;
        }
        
        usernameLabel.setText("@" + currentUser.getUsername());
        fullNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
        emailLabel.setText(currentUser.getEmail());
        memberSinceLabel.setText("Membre depuis " + 
            (currentUser.getCreatedAt() != null ? 
             currentUser.getCreatedAt().toLocalDate().toString() : "récemment"));
        
        String bio = currentUser.getBio();
        if (bio != null && !bio.isEmpty()) {
            bioLabel.setText(bio);
        } else {
            bioLabel.setText("Aucune bio pour le moment");
        }
        
        loadAvatar();
        loadUserCreations();
        loadUserFavorites();
    }
    
    private void loadAvatar() {
        String avatarPath = currentUser.getAvatar();
        
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = findAvatarFile(avatarPath);
            
            if (avatarFile != null && avatarFile.exists()) {
                try {
                    Image avatar = new Image(avatarFile.toURI().toString(), 130, 130, true, true);
                    avatarImage.setImage(avatar);
                    return;
                } catch (Exception e) {
                    System.err.println("Erreur chargement avatar: " + e.getMessage());
                }
            }
        }
        setPlaceholderAvatar();
    }
    
    private File findAvatarFile(String avatarPath) {
        String userDir = System.getProperty("user.dir");
        String fileName = new File(avatarPath).getName();
        
        String[] possiblePaths = {
            avatarPath,
            userDir + "/" + avatarPath,
            "uploads/avatars/" + fileName,
            userDir + "/uploads/avatars/" + fileName,
            "src/main/resources/uploads/avatars/" + fileName,
            userDir + "/src/main/resources/uploads/avatars/" + fileName
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
    
    private void setPlaceholderAvatar() {
        String initials = getInitials();
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(130, 130);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.setFill(javafx.scene.paint.Color.web("#18E3A4"));
        gc.fillOval(0, 0, 130, 130);
        gc.setStroke(javafx.scene.paint.Color.web("#0a0c10"));
        gc.setLineWidth(3);
        gc.strokeOval(2, 2, 126, 126);
        gc.setFill(javafx.scene.paint.Color.web("#0a0c10"));
        gc.setFont(javafx.scene.text.Font.font("Arial", 45));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(initials, 65, 85);
        
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(130, 130);
        canvas.snapshot(null, writableImage);
        avatarImage.setImage(writableImage);
    }
    
    private String getInitials() {
        StringBuilder initials = new StringBuilder();
        if (currentUser.getPrenom() != null && !currentUser.getPrenom().isEmpty()) {
            initials.append(currentUser.getPrenom().charAt(0));
        }
        if (currentUser.getNom() != null && !currentUser.getNom().isEmpty()) {
            initials.append(currentUser.getNom().charAt(0));
        }
        return initials.toString().toUpperCase();
    }
    
    private String saveAvatarImage(File sourceFile) throws Exception {
        File uploadsDir = new File("uploads/avatars");
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
        
        String extension = getFileExtension(sourceFile);
        String fileName = "avatar_" + currentUser.getId() + "_" + System.currentTimeMillis() + "." + extension;
        File destFile = new File(uploadsDir, fileName);
        
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return destFile.getAbsolutePath();
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return lastIndex > 0 ? name.substring(lastIndex + 1) : "png";
    }
    
    private void loadUserCreations() {
        Task<List<Oeuvre>> oeuvreTask = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                return oeuvreService.searchOeuvres(null, null, currentUser.getId());
            }
        };
        oeuvreTask.setOnSucceeded(e -> {
            List<Oeuvre> oeuvres = oeuvreTask.getValue();
            if (oeuvresCountLabel != null) {
                oeuvresCountLabel.setText(String.valueOf(oeuvres.size()));
            }
            displayOeuvresAsCards(oeuvres, oeuvresGrid);
        });
        oeuvreTask.setOnFailed(e -> System.err.println("Erreur chargement œuvres: " + e.getSource().getException()));
        new Thread(oeuvreTask).start();
        
        Task<List<Artefact>> artefactTask = new Task<>() {
            @Override
            protected List<Artefact> call() throws Exception {
                return artefactService.searchArtefacts(null, null, currentUser.getId());
            }
        };
        artefactTask.setOnSucceeded(e -> {
            List<Artefact> artefacts = artefactTask.getValue();
            if (artefactsCountLabel != null) {
                artefactsCountLabel.setText(String.valueOf(artefacts.size()));
            }
            displayArtefactsAsCards(artefacts, artefactsGrid);
        });
        artefactTask.setOnFailed(e -> System.err.println("Erreur chargement artefacts: " + e.getSource().getException()));
        new Thread(artefactTask).start();
    }
    
    private void loadUserFavorites() {
        favoritesCountLabel.setText("0");
        
        if (favorisSection != null) {
            favorisSection.setVisible(true);
            favorisSection.setManaged(true);
        }
    }
    
    private void displayOeuvresAsCards(List<Oeuvre> oeuvres, TilePane grid) {
        Platform.runLater(() -> {
            grid.getChildren().clear();
            for (Oeuvre oeuvre : oeuvres) {
                OeuvreCard card = new OeuvreCard(oeuvre, () -> {
                    OeuvreController.setSelectedOeuvreForShow(oeuvre);
                    navigateTo("/oeuvre/show");
                });
                grid.getChildren().add(card);
            }
        });
    }
    
    private void displayArtefactsAsCards(List<Artefact> artefacts, TilePane grid) {
        Platform.runLater(() -> {
            grid.getChildren().clear();
            for (Artefact artefact : artefacts) {
                ArtefactCard card = new ArtefactCard(artefact, () -> {
                    ArtefactController.setSelectedArtefactForShow(artefact);
                    navigateTo("/artefact/show");
                });
                grid.getChildren().add(card);
            }
        });
    }
    
    // ===== MODALE ÉDITION AVEC AVATAR =====
    
    @FXML
    private void openAvatarModal() {
        if (editModal != null) {
            editUsername.setText(currentUser.getUsername());
            editEmail.setText(currentUser.getEmail());
            editBio.setText(currentUser.getBio());
            
            // Afficher l'avatar actuel dans la preview
            String avatarPath = currentUser.getAvatar();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = findAvatarFile(avatarPath);
                if (avatarFile != null && avatarFile.exists()) {
                    Image preview = new Image(avatarFile.toURI().toString(), 90, 90, true, true);
                    previewAvatar.setImage(preview);
                } else {
                    setPreviewPlaceholder();
                }
            } else {
                setPreviewPlaceholder();
            }
            
            selectedAvatarFile = null;
            editModal.setVisible(true);
            editModal.setManaged(true);
        } else {
            showEditProfileDialog();
        }
    }
    
    @FXML
    private void closeEditModal() {
        if (editModal != null) {
            editModal.setVisible(false);
            editModal.setManaged(false);
        }
    }
    
    @FXML
    private void chooseAvatarImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(editModal.getScene().getWindow());
        if (selectedFile != null) {
            selectedAvatarFile = selectedFile;
            try {
                Image preview = new Image(selectedFile.toURI().toString(), 90, 90, true, true);
                previewAvatar.setImage(preview);
            } catch (Exception e) {
                System.err.println("Erreur preview: " + e.getMessage());
            }
        }
    }
    
    private void setPreviewPlaceholder() {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(90, 90);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.web("#18E3A4"));
        gc.fillOval(0, 0, 90, 90);
        gc.setFill(javafx.scene.paint.Color.web("#0a0c10"));
        gc.setFont(javafx.scene.text.Font.font("Arial", 36));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(getInitials(), 45, 55);
        
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(90, 90);
        canvas.snapshot(null, writableImage);
        previewAvatar.setImage(writableImage);
    }
    
    @FXML
    private void saveProfile() {
        try {
            if (editUsername != null && editUsername.getText() != null && !editUsername.getText().isEmpty()) {
                currentUser.setUsername(editUsername.getText());
            }
            if (editEmail != null && editEmail.getText() != null && !editEmail.getText().isEmpty()) {
                currentUser.setEmail(editEmail.getText());
            }
            if (editBio != null && editBio.getText() != null) {
                currentUser.setBio(editBio.getText());
            }
            
            if (selectedAvatarFile != null) {
                String avatarPath = saveAvatarImage(selectedAvatarFile);
                currentUser.setAvatar(avatarPath);
                selectedAvatarFile = null;
            }
            
            userService.update(currentUser);
            showAlert("Succès", "Profil mis à jour avec succès !");
            loadProfileData();
            closeEditModal();
            
        } catch (SQLException ex) {
            showAlert("Erreur", "Erreur lors de la mise à jour: " + ex.getMessage());
        } catch (Exception ex) {
            showAlert("Erreur", "Erreur lors de la sauvegarde de l'avatar: " + ex.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateAvatar() {
        showAlert("Info", "Fonctionnalité à venir");
    }
    
    @FXML
    private void handleChangePassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Changer le mot de passe");
        dialog.setHeaderText("Modifier votre mot de passe");
        
        ButtonType saveButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        PasswordField oldPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        
        grid.add(new Label("Mot de passe actuel:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("Nouveau mot de passe:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirmer:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                String oldPassword = oldPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                if (!newPassword.equals(confirmPassword)) {
                    showAlert("Erreur", "Les mots de passe ne correspondent pas");
                    return;
                }
                
                if (newPassword.length() < 6) {
                    showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères");
                    return;
                }
                
                try {
                    if (userService.authenticate(currentUser.getUsername(), oldPassword)) {
                        userService.updatePassword(currentUser.getId(), newPassword);
                        showAlert("Succès", "Mot de passe modifié avec succès !");
                    } else {
                        showAlert("Erreur", "Mot de passe actuel incorrect");
                    }
                } catch (SQLException ex) {
                    showAlert("Erreur", "Erreur: " + ex.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Se déconnecter");
        confirm.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserSession.logout();
                navigateTo("/login");
            }
        });
    }
    
    @FXML
    private void goToAdmin() {
        navigateTo("/admin/users");
    }
    
    @FXML
    public void goToHome() {
        navigateTo("/");
    }
    
    // Navigation
    @FXML public void goAccueil() { navigateTo("/"); }
    @FXML public void goDiscover() { navigateTo("/discover"); }
    @FXML public void goUniverses() { navigateTo("/universes"); }
    @FXML public void goPersonnages() { navigateTo("/personnages"); }
    @FXML public void goOeuvres() { navigateTo("/oeuvre"); }
    @FXML public void goArtefacts() { navigateTo("/artefact"); }
    @FXML public void goShop() { navigateTo("/shop"); }
    @FXML public void goChallenges() { navigateTo("/challenges"); }
    
    @FXML private void toggleSearchBar() {}
    @FXML private void showOeuvresTab() { if (contentTabPane != null) contentTabPane.getSelectionModel().select(0); }
    @FXML private void showArtefactsTab() { if (contentTabPane != null) contentTabPane.getSelectionModel().select(1); }
    @FXML private void showFavorisTab() { if (contentTabPane != null) contentTabPane.getSelectionModel().select(2); }
    private void styleActiveTab(String activeTab) {}
    
    private void showEditProfileDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Éditer le profil");
        dialog.setHeaderText("Modifier vos informations");
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        TextField usernameField = new TextField(currentUser.getUsername());
        TextField emailField = new TextField(currentUser.getEmail());
        
        grid.add(new Label("Nom d'utilisateur:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                currentUser.setUsername(usernameField.getText());
                currentUser.setEmail(emailField.getText());
                
                try {
                    userService.update(currentUser);
                    showAlert("Succès", "Profil mis à jour avec succès !");
                    loadProfileData();
                } catch (SQLException ex) {
                    showAlert("Erreur", "Erreur lors de la mise à jour: " + ex.getMessage());
                }
            }
        });
    }
    
    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}