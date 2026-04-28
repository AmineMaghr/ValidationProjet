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
    @FXML private ImageView avatarImage;
    @FXML private Button editProfileBtn;
    @FXML private Button logoutBtn;
    @FXML private Button adminBtn;
    @FXML private TabPane contentTabPane;
    @FXML private TilePane oeuvresGrid;
    @FXML private TilePane artefactsGrid;
    @FXML private TilePane favoriteOeuvresGrid;
    @FXML private TilePane favoriteArtefactsGrid;
    @FXML private Label oeuvresCountLabel;
    @FXML private Label artefactsCountLabel;
    @FXML private Label favoritesCountLabel;
    
    private UserService userService;
    private OeuvreService oeuvreService;
    private ArtefactService artefactService;
    private FavorisService favorisService;
    private User currentUser;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        oeuvreService = new OeuvreService();
        artefactService = new ArtefactService();
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
        
        loadAvatar();
        loadUserCreations();
        loadUserFavorites();
        
        if (adminBtn != null) {
            adminBtn.setVisible(UserSession.isAdmin());
        }
    }
    
    private void loadAvatar() {
        String avatarPath = currentUser.getAvatar();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = new File(avatarPath);
            if (avatarFile.exists()) {
                try {
                    Image avatar = new Image(avatarFile.toURI().toString(), 120, 120, true, true);
                    avatarImage.setImage(avatar);
                    return;
                } catch (Exception e) {
                    System.err.println("Erreur chargement image: " + e.getMessage());
                }
            }
        }
        setPlaceholderAvatar();
    }
    
    private void setPlaceholderAvatar() {
        String initials = getInitials();
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
        gc.fillText(initials, 60, 75);
        
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(120, 120);
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
    
    private void setupTabs() {
        if (contentTabPane != null) {
            contentTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    String tabText = newTab.getText();
                    if (tabText.contains("Œuvres")) {
                        loadUserCreations();
                    } else if (tabText.contains("Artefacts")) {
                        loadUserCreations();
                    } else if (tabText.contains("Favoris")) {
                        loadUserFavorites();
                    }
                }
            );
        }
    }
    
    @FXML
    private void handleEditProfile() {
    }
    
    @FXML
    private void handleChangePassword() {
    }
    
    @FXML
    private void handleDeleteAccount() {
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

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}