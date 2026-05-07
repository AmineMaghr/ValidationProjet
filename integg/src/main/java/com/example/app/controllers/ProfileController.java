package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.entities.Oeuvre;
import com.example.app.entities.Artefact;
import com.example.app.entities.Favoris;
import com.example.app.services.UserService;
import com.example.app.services.OeuvreService;
import com.example.app.services.ArtefactService;
import com.example.app.services.FavorisService;
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
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfileController extends BaseController {
    
    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label bioLabel;
    @FXML private ImageView avatarImage;
    @FXML private Button editProfileBtn;
    @FXML private Button logoutBtn;
    @FXML private Button adminBtn;
    @FXML private TabPane contentTabPane;
    @FXML private TilePane oeuvresGrid;
    @FXML private TilePane artefactsGrid;
    @FXML private TilePane universesGrid;
    @FXML private TilePane personnagesGrid;
    @FXML private TilePane favoriteOeuvresGrid;
    @FXML private TilePane favoriteArtefactsGrid;
    @FXML private Label oeuvresCountLabel;
    @FXML private Label artefactsCountLabel;
    @FXML private Label universesCountLabel;
    @FXML private Label personnagesCountLabel;
    @FXML private Label favoritesCountLabel;
    
    private UserService userService;
    private OeuvreService oeuvreService;
    private ArtefactService artefactService;
    private com.example.app.services.UniverseService universeService;
    private com.example.app.services.PersonnageService personnageService;
    private FavorisService favorisService;
    private User currentUser;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        oeuvreService = new OeuvreService();
        artefactService = new ArtefactService();
        universeService = new com.example.app.services.UniverseService();
        personnageService = new com.example.app.services.PersonnageService();
        favorisService = new FavorisService();
        
        currentUser = UserSession.getCurrentUser();
        
        loadProfileData();
        setupTabs();
        
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
        
        // Afficher la bio
        if (currentUser.getBio() != null && !currentUser.getBio().isEmpty()) {
            bioLabel.setText(currentUser.getBio());
        } else {
            bioLabel.setText("Aucune bio pour le moment");
        }
        
        loadAvatar();
        loadUserCreations();
        loadUserFavorites();
        
        if (adminBtn != null) {
            adminBtn.setVisible(UserSession.isAdmin());
        }
    }
    
    /**
     * Charge la photo de profil de l'utilisateur
     */
    private void loadAvatar() {
        String avatarPath = currentUser.getAvatar();
        System.out.println("🔍 Chargement avatar - Chemin: " + avatarPath);

        if (avatarPath != null && !avatarPath.isEmpty()) {
            String fileName = new File(avatarPath).getName();
            File avatarFile = new File("uploads/avatars/" + fileName);
            
            if (!avatarFile.exists()) {
                avatarFile = new File(avatarPath);
            }
            
            if (avatarFile.exists()) {
                try {
                    Image avatar = new Image(avatarFile.toURI().toString(), 120, 120, true, true);
                    avatarImage.setImage(avatar);
                    
                    // Appliquer le clip cercle
                    Circle clip = new Circle(60);
                    clip.setCenterX(60);
                    clip.setCenterY(60);
                    avatarImage.setClip(clip);
                    
                    System.out.println("✅ Avatar chargé");
                    return;
                } catch (Exception e) {
                    System.err.println("Erreur: " + e.getMessage());
                }
            }
        }
        
        setPlaceholderAvatar();
    }

    /**
     * Crée un placeholder avec les initiales
     */
    private void setPlaceholderAvatar() {
        String initials = getInitials();
        System.out.println("📷 Affichage placeholder pour: " + initials);

        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(120, 120);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(javafx.scene.paint.Color.web("#18E3A4"));
        gc.fillOval(0, 0, 120, 120);

        gc.setStroke(javafx.scene.paint.Color.web("#0a0c10"));
        gc.setLineWidth(3);
        gc.strokeOval(2, 2, 116, 116);

        gc.setFill(javafx.scene.paint.Color.web("#0a0c10"));
        gc.setFont(javafx.scene.text.Font.font("Arial", 40));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(initials, 60, 78);

        javafx.scene.image.WritableImage image = canvas.snapshot(null, null);
        avatarImage.setImage(image);
        
        // Appliquer le clip cercle
        Circle clip = new Circle(60);
        clip.setCenterX(60);
        clip.setCenterY(60);
        avatarImage.setClip(clip);
    }

    /**
     * Extrait les initiales du nom complet
     */
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

    /**
     * Sauvegarde l'image avatar sélectionnée
     */
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

    /**
     * Extrait l'extension du fichier
     */
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return lastIndex > 0 ? name.substring(lastIndex + 1) : "png";
    }

    private void loadUserCreations() {
        // Charger les œuvres
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
            System.out.println("=== [PROFILE] Œuvres chargées: " + oeuvres.size());
        });
        oeuvreTask.setOnFailed(e -> {
            System.err.println("Erreur chargement œuvres: " + e.getSource().getException());
        });
        new Thread(oeuvreTask).start();
        
        // Charger les artefacts
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
            System.out.println("=== [PROFILE] Artefacts chargés: " + artefacts.size());
        });
        artefactTask.setOnFailed(e -> {
            System.err.println("Erreur chargement artefacts: " + e.getSource().getException());
            e.getSource().getException().printStackTrace();
        });
        new Thread(artefactTask).start();
        
        // Charger les univers
        Task<List<com.example.app.entities.Universe>> universeTask = new Task<>() {
            @Override
            protected List<com.example.app.entities.Universe> call() throws Exception {
                return universeService.searchUniverses(null, null, "newest", currentUser.getId());
            }
        };
        universeTask.setOnSucceeded(e -> {
            List<com.example.app.entities.Universe> universes = universeTask.getValue();
            if (universesCountLabel != null) {
                universesCountLabel.setText(String.valueOf(universes.size()));
            }
            displayUniversesAsCards(universes, universesGrid);
        });
        new Thread(universeTask).start();

        // Charger les personnages
        Task<List<com.example.app.entities.Personnage>> personnageTask = new Task<>() {
            @Override
            protected List<com.example.app.entities.Personnage> call() throws Exception {
                return personnageService.searchPersonnages(null, null, null, "newest", currentUser.getId());
            }
        };
        personnageTask.setOnSucceeded(e -> {
            List<com.example.app.entities.Personnage> personnages = personnageTask.getValue();
            if (personnagesCountLabel != null) {
                personnagesCountLabel.setText(String.valueOf(personnages.size()));
            }
            displayPersonnagesAsCards(personnages, personnagesGrid);
        });
        new Thread(personnageTask).start();
    }
    
    private void loadUserFavorites() {
        int userId = currentUser.getId();

        Task<List<Oeuvre>> favOeuvreTask = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                List<Oeuvre> favorites = new ArrayList<>();
                try {
                    List<Favoris> favList = favorisService.getUserFavoriteOeuvres(userId);
                    System.out.println("=== [FAVORIS] Œuvres favorites trouvées: " + favList.size());
                    for (Favoris fav : favList) {
                        System.out.println("  - fav.getOeuvreId() = " + fav.getOeuvreId());
                        Oeuvre oeuvre = oeuvreService.findById(fav.getOeuvreId());
                        if (oeuvre != null) {
                            favorites.add(oeuvre);
                            System.out.println("    → Œuvre ajoutée: " + oeuvre.getTitle());
                        } else {
                            System.out.println("    → Œuvre NON trouvée pour ID: " + fav.getOeuvreId());
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return favorites;
            }
        };
        favOeuvreTask.setOnSucceeded(e -> {
            List<Oeuvre> favOeuvres = favOeuvreTask.getValue();
            if (favoriteOeuvresGrid != null) {
                displayOeuvresAsCards(favOeuvres, favoriteOeuvresGrid);
            }
            updateTotalFavoritesCount();
        });
        new Thread(favOeuvreTask).start();

        Task<List<Artefact>> favArtefactTask = new Task<>() {
            @Override
            protected List<Artefact> call() throws Exception {
                List<Artefact> favorites = new ArrayList<>();
                try {
                    List<Favoris> favList = favorisService.getUserFavoriteArtefacts(userId);
                    System.out.println("=== [FAVORIS] Artefacts favoris trouvés: " + favList.size());
                    for (Favoris fav : favList) {
                        System.out.println("  - fav.getArtefactId() = " + fav.getArtefactId());
                        Artefact artefact = artefactService.findById(fav.getArtefactId());
                        if (artefact != null) {
                            favorites.add(artefact);
                            System.out.println("    → Artefact ajouté: " + artefact.getName());
                        } else {
                            System.out.println("    → Artefact NON trouvé pour ID: " + fav.getArtefactId());
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return favorites;
            }
        };
        favArtefactTask.setOnSucceeded(e -> {
            List<Artefact> favArtefacts = favArtefactTask.getValue();
            if (favoriteArtefactsGrid != null) {
                displayArtefactsAsCards(favArtefacts, favoriteArtefactsGrid);
            }
            updateTotalFavoritesCount();
        });
        new Thread(favArtefactTask).start();
    }
    
    private void updateTotalFavoritesCount() {
        try {
            int total = favorisService.getUserFavoriteOeuvres(currentUser.getId()).size() +
                        favorisService.getUserFavoriteArtefacts(currentUser.getId()).size();
            favoritesCountLabel.setText(String.valueOf(total));
        } catch (SQLException e) {
            favoritesCountLabel.setText("0");
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

    private void displayUniversesAsCards(List<com.example.app.entities.Universe> universes, TilePane grid) {
        Platform.runLater(() -> {
            if (grid == null) return;
            grid.getChildren().clear();
            for (com.example.app.entities.Universe universe : universes) {
                VBox card = new VBox(5);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: #1a2530; -fx-background-radius: 8; -fx-cursor: hand;");
                Label name = new Label(universe.getName());
                name.setStyle("-fx-text-fill: #18E3A4; -fx-font-weight: bold;");
                card.getChildren().add(name);
                card.setOnMouseClicked(e -> {
                    // Navigate to universe detail view or edit
                    navigateTo("/universes");
                });
                grid.getChildren().add(card);
            }
        });
    }

    private void displayPersonnagesAsCards(List<com.example.app.entities.Personnage> personnages, TilePane grid) {
        Platform.runLater(() -> {
            if (grid == null) return;
            grid.getChildren().clear();
            for (com.example.app.entities.Personnage personnage : personnages) {
                VBox card = new VBox(5);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: #1a2530; -fx-background-radius: 8; -fx-cursor: hand;");
                Label name = new Label(personnage.getNom());
                name.setStyle("-fx-text-fill: #18E3A4; -fx-font-weight: bold;");
                card.getChildren().add(name);
                card.setOnMouseClicked(e -> {
                    navigateTo("/personnages");
                });
                grid.getChildren().add(card);
            }
        });
    }
    
    private void setupTabs() {
        if (contentTabPane != null) {
            contentTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    String tabText = newTab.getText();
                    if (tabText.contains("Œuvres")) {
                        refreshTab("oeuvres");
                    } else if (tabText.contains("Artefacts")) {
                        refreshTab("artefacts");
                    } else if (tabText.contains("Favoris")) {
                        refreshTab("favoris");
                    }
                }
            );
        }
    }
    
    private void refreshTab(String tabName) {
        switch (tabName) {
            case "oeuvres":
                loadUserCreations();
                break;
            case "artefacts":
                loadUserCreations();
                break;
            case "favoris":
                loadUserFavorites();
                break;
        }
    }
    
    @FXML
    private void handleEditProfile() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Éditer le profil");
        dialog.setHeaderText("Modifier vos informations");
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #11161c;");
        
        TextField usernameField = new TextField(currentUser.getUsername());
        usernameField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        TextField prenomField = new TextField(currentUser.getPrenom());
        prenomField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        TextField nomField = new TextField(currentUser.getNom());
        nomField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        TextArea bioArea = new TextArea(currentUser.getBio());
        bioArea.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        bioArea.setPrefRowCount(3);
        bioArea.setWrapText(true);

        Button changeAvatarBtn = new Button("Changer l'avatar");
        changeAvatarBtn.setStyle("-fx-background-color: #2a3540; -fx-text-fill: #fff; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;");

        Label avatarStatus = new Label("Image actuelle conservée");
        avatarStatus.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 11px;");

        grid.add(new Label("Nom d'utilisateur:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Nom:"), 0, 2);
        grid.add(nomField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Bio:"), 0, 4);
        grid.add(bioArea, 1, 4);
        grid.add(new Label("Avatar:"), 0, 5);

        VBox avatarBox = new VBox(5, changeAvatarBtn, avatarStatus);
        grid.add(avatarBox, 1, 5);
        
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: #b0b9b6; -fx-font-weight: bold;");
            }
        }
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #0a0c10;");

        final File[] selectedAvatarFile = {null};
        changeAvatarBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp")
            );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedAvatarFile[0] = file;
                avatarStatus.setText("✓ Nouvel avatar: " + file.getName());
                avatarStatus.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px;");
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                currentUser.setUsername(usernameField.getText());
                currentUser.setPrenom(prenomField.getText());
                currentUser.setNom(nomField.getText());
                currentUser.setEmail(emailField.getText());
                currentUser.setBio(bioArea.getText());

                if (selectedAvatarFile[0] != null) {
                    try {
                        String avatarPath = saveAvatarImage(selectedAvatarFile[0]);
                        currentUser.setAvatar(avatarPath);
                    } catch (Exception ex) {
                        showAlert("Erreur", "Erreur lors de la sauvegarde de l'avatar: " + ex.getMessage());
                        return;
                    }
                }

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
        grid.setStyle("-fx-background-color: #11161c;");
        
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle("-fx-background-color: #1a2530; -fx-text-fill: #fff; -fx-padding: 10; -fx-background-radius: 8;");
        
        grid.add(new Label("Mot de passe actuel:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("Nouveau mot de passe:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirmer:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: #b0b9b6; -fx-font-weight: bold;");
            }
        }
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #0a0c10;");
        
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
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer votre compte");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement votre compte ?\n\n" +
                              "Cette action est irréversible et toutes vos données seront perdues.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                TextInputDialog passwordDialog = new TextInputDialog();
                passwordDialog.setTitle("Vérification");
                passwordDialog.setHeaderText("Confirmez votre mot de passe");
                passwordDialog.setContentText("Veuillez saisir votre mot de passe pour confirmer :");
                passwordDialog.getDialogPane().setStyle("-fx-background-color: #11161c;");
                
                passwordDialog.showAndWait().ifPresent(password -> {
                    try {
                        if (userService.authenticate(currentUser.getUsername(), password)) {
                            userService.delete(currentUser.getId());
                            UserSession.logout();
                            showAlert("Compte supprimé", "Votre compte a été supprimé avec succès.");
                            navigateTo("/login");
                        } else {
                            showAlert("Erreur", "Mot de passe incorrect");
                        }
                    } catch (SQLException ex) {
                        showAlert("Erreur", "Erreur lors de la suppression: " + ex.getMessage());
                    }
                });
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
    private void openSettings() {
        // Créer un dialog pour les paramètres
        Dialog<ButtonType> settingsDialog = new Dialog<>();
        settingsDialog.setTitle("Paramètres");
        settingsDialog.setHeaderText("Paramètres du compte");
        
        ButtonType passwordButtonType = new ButtonType("Changer le mot de passe", ButtonBar.ButtonData.OTHER);
        ButtonType deleteButtonType = new ButtonType("Supprimer le compte", ButtonBar.ButtonData.OTHER);
        ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        settingsDialog.getDialogPane().getButtonTypes().addAll(passwordButtonType, deleteButtonType, closeButtonType);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #11161c;");
        
        Label infoLabel = new Label("Options de gestion de compte");
        infoLabel.setStyle("-fx-text-fill: #b0b9b6; -fx-font-size: 14px;");
        
        content.getChildren().add(infoLabel);
        settingsDialog.getDialogPane().setContent(content);
        settingsDialog.getDialogPane().setStyle("-fx-background-color: #0a0c10;");
        
        settingsDialog.showAndWait().ifPresent(response -> {
            if (response == passwordButtonType) {
                handleChangePassword();
            } else if (response == deleteButtonType) {
                handleDeleteAccount();
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
    
    // ================= FACE RECOGNITION METHODS =================
    @FXML
    public void goToFaceRegister() {
        if (currentUser == null) {
            showAlert("Erreur", "Utilisateur non connecté");
            return;
        }

        UserSession.setCurrentUser(currentUser);
        navigateTo("/face-register", UserSession.getCurrentUser());
    }

    @FXML
    public void goToFaceLogin() {
        navigateTo("/face-login");
    }
}

