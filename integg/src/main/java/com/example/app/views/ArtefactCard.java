package com.example.app.views;

import com.example.app.entities.Artefact;
import com.example.app.services.FavorisService;
import com.example.app.utils.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.File;
import java.sql.SQLException;

public class ArtefactCard extends VBox {

    private Artefact artefact;
    private Runnable onClick;
    private Label favoriteLabel;
    private boolean isFavorite = false;
    private FavorisService favorisService;

    public ArtefactCard(Artefact artefact, Runnable onClick) {
        this.artefact = artefact;
        this.onClick = onClick;
        this.favorisService = null;
        
        // Initialiser le service de favoris seulement si l'utilisateur est connecté
        if (UserSession.isLoggedIn()) {
            try {
                this.favorisService = new FavorisService();
            } catch (Exception e) {
                System.err.println("Erreur création FavorisService: " + e.getMessage());
            }
        }
        initialize();
        
        // Charger le statut du favori si l'utilisateur est connecté
        if (favorisService != null) {
            loadFavoriteStatus();
        }
    }

    private void initialize() {
        setStyle("-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;");
        setPadding(new Insets(15));
        setSpacing(12);
        setPrefWidth(280);
        setMaxWidth(280);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-background-color: #1a2530; -fx-background-radius: 8;");

        if (artefact.getImageUrl() != null && !artefact.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File(artefact.getImageUrl());
                if (imageFile.exists()) {
                    Image img = new Image(imageFile.toURI().toString(), 250, 160, false, true);
                    imageView.setImage(img);
                }
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }

        // Si pas d'image, afficher un placeholder
        if (imageView.getImage() == null) {
            VBox placeholder = new VBox();
            placeholder.setStyle("-fx-background-color: #1a2530; -fx-background-radius: 8;");
            placeholder.setPrefWidth(250);
            placeholder.setPrefHeight(160);
            placeholder.setAlignment(Pos.CENTER);
            Label iconLabel = new Label("⚡");
            iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: #6a7a8a;");
            placeholder.getChildren().add(iconLabel);
            getChildren().add(placeholder);
        } else {
            getChildren().add(imageView);
        }

        // Nom
        Label nameLabel = new Label(artefact.getName());
        nameLabel.setStyle("-fx-text-fill: #fff; -fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(250);

        // Type
        Label typeLabel = new Label(artefact.getType());
        typeLabel.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px; -fx-background-color: rgba(24,227,164,0.1); -fx-padding: 4 12; -fx-background-radius: 20;");

        // Univers
        Label universeLabel = new Label("🌌 " + artefact.getUniverse());
        universeLabel.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 12px;");

        // Rareté
        Label rarityLabel = new Label("⭐ " + artefact.getRarity());
        rarityLabel.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 12px;");
        
        // Origines (tronqué)
        Text originsText = new Text();
        String origins = artefact.getOrigins();
        if (origins != null) {
            if (origins.length() > 80) {
                origins = origins.substring(0, 80) + "...";
            }
            originsText.setText(origins);
        } else {
            originsText.setText("Aucune origine");
        }
        originsText.setStyle("-fx-fill: #b0b9b6; -fx-font-size: 11px;");
        originsText.setWrappingWidth(250);
        
        // Cœur des favoris
        HBox heartBox = new HBox(10);
        heartBox.setAlignment(Pos.CENTER_LEFT);
        favoriteLabel = new Label();
        favoriteLabel.setStyle("-fx-font-size: 20px; -fx-cursor: hand;");
        updateFavoriteIcon();
        heartBox.getChildren().add(favoriteLabel);
        
        getChildren().addAll(nameLabel, typeLabel, universeLabel, rarityLabel, originsText, heartBox);

        // Effet hover
        setOnMouseEntered(e -> {
            setStyle("-fx-background-color: #16202a; -fx-background-radius: 12; -fx-border-color: #18E3A4; -fx-border-radius: 12; -fx-cursor: hand;");
        });
        setOnMouseExited(e -> {
            setStyle("-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;");
        });

        setOnMouseClicked(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });
        
        // Ajouter l'événement de clic sur le cœur
        if (favorisService != null) {
            Tooltip.install(favoriteLabel, new Tooltip("Ajouter aux favoris"));
            favoriteLabel.setOnMouseClicked(e -> {
                e.consume();
                toggleFavorite();
            });
        }
    }
    
    private void loadFavoriteStatus() {
        if (favorisService == null) {
            if (favoriteLabel != null) favoriteLabel.setVisible(false);
            return;
        }
        try {
            isFavorite = favorisService.isArtefactFavorite(artefact.getId());
            updateFavoriteIcon();
        } catch (Exception e) {
            System.err.println("Erreur chargement statut favori: " + e.getMessage());
            isFavorite = false;
        }
    }
    
    private void updateFavoriteIcon() {
        if (favoriteLabel != null) {
            // ♡ = cœur vide, ♥ = cœur plein
            String symbol = isFavorite ? "\u2764" : "\u2661";
            String color = isFavorite ? "#EF5350" : "#666";
            favoriteLabel.setText(symbol);
            favoriteLabel.setStyle("-fx-font-size: 22px; -fx-cursor: hand; -fx-text-fill: " + color + ";");
            Tooltip tooltip = new Tooltip(isFavorite ? "Retirer des favoris" : "Ajouter aux favoris");
            Tooltip.install(favoriteLabel, tooltip);
        }
    }
    
    private void toggleFavorite() {
        if (favorisService == null) return;
        try {
            if (isFavorite) {
                favorisService.removeFavoriteArtefact(artefact.getId());
                isFavorite = false;
                showAlert("Succès", "Artefact retiré des favoris");
            } else {
                favorisService.addFavoriteArtefact(artefact.getId());
                isFavorite = true;
                showAlert("Succès", "Artefact ajouté aux favoris");
            }
            updateFavoriteIcon();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la gestion des favoris: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Artefact getArtefact() {
        return artefact;
    }
}