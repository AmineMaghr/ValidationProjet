package com.example.app.views;

import com.example.app.entities.Oeuvre;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class OeuvreCard extends VBox {

    private Oeuvre oeuvre;
    private Runnable onClick;
    private Label favoriteLabel;
    private boolean isFavorite = false;
    private FavorisService favorisService;

    public OeuvreCard(Oeuvre oeuvre, Runnable onClick) {
        this.oeuvre = oeuvre;
        this.onClick = onClick;
        this.favorisService = null;

        if (UserSession.isLoggedIn()) {
            int currentUserId = UserSession.getCurrentUser().getId();
            if (oeuvre.getCreateurId() != currentUserId) {
                try {
                    this.favorisService = new FavorisService();
                } catch (Exception e) {
                    System.err.println("Erreur création FavorisService: " + e.getMessage());
                }
            }
        }
        initialize();

        if (favorisService != null) {
            loadFavoriteStatus();
        }
    }

    private void initialize() {
        setStyle(
                "-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;");
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

        // Chargement de l'image
        loadImage(imageView);

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

        // Titre
        Label titleLabel = new Label(oeuvre.getTitle());
        titleLabel.setStyle("-fx-text-fill: #fff; -fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(250);

        // Type
        Label typeLabel = new Label(oeuvre.getType());
        typeLabel.setStyle(
                "-fx-text-fill: #18E3A4; -fx-font-size: 11px; -fx-background-color: rgba(24,227,164,0.1); -fx-padding: 4 12; -fx-background-radius: 20;");

        // Auteur
        Label authorLabel = new Label("✍️ " + oeuvre.getAuthor());
        authorLabel.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 12px;");

        // Description (tronquée)
        Text descText = new Text();
        String desc = oeuvre.getDescription();
        if (desc != null && desc.length() > 80) {
            desc = desc.substring(0, 80) + "...";
        }
        descText.setText(desc != null ? desc : "");
        descText.setStyle("-fx-fill: #b0b9b6; -fx-font-size: 11px;");
        descText.setWrappingWidth(250);

        // Cœur des favoris
        HBox heartBox = new HBox(10);
        heartBox.setAlignment(Pos.CENTER_LEFT);
        favoriteLabel = new Label();
        favoriteLabel.setStyle("-fx-font-size: 20px; -fx-cursor: hand;");
        updateFavoriteIcon();
        heartBox.getChildren().add(favoriteLabel);

        getChildren().addAll(titleLabel, typeLabel, authorLabel, descText, heartBox);

        // Effet hover
        setOnMouseEntered(e -> {
            setStyle(
                    "-fx-background-color: #16202a; -fx-background-radius: 12; -fx-border-color: #18E3A4; -fx-border-radius: 12; -fx-cursor: hand;");
        });
        setOnMouseExited(e -> {
            setStyle(
                    "-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;");
        });

        setOnMouseClicked(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });

        if (favorisService != null) {
            Tooltip.install(favoriteLabel, new Tooltip("Ajouter aux favoris"));
            favoriteLabel.setOnMouseClicked(e -> {
                e.consume();
                toggleFavorite();
            });
        }
    }

    private void loadImage(ImageView imageView) {
        if (oeuvre.getImageUrl() != null && !oeuvre.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File(oeuvre.getImageUrl());
                if (imageFile.exists()) {
                    Image img = new Image(imageFile.toURI().toString(), 250, 160, false, true);
                    imageView.setImage(img);
                }
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }
    }



    private void loadFavoriteStatus() {
        if (favorisService == null) {
            if (favoriteLabel != null)
                favoriteLabel.setVisible(false);
            return;
        }
        try {
            isFavorite = favorisService.isOeuvreFavorite(oeuvre.getId());
            updateFavoriteIcon();
            System.out.println("❤️ Statut favori pour " + oeuvre.getTitle() + ": " + isFavorite);
        } catch (Exception e) {
            System.err.println("Erreur chargement statut favori: " + e.getMessage());
            isFavorite = false;
        }
    }

    private void updateFavoriteIcon() {
        if (favoriteLabel != null) {
            String symbol = isFavorite ? "\u2764" : "\u2661";
            String color = isFavorite ? "#EF5350" : "#666";
            favoriteLabel.setText(symbol);
            favoriteLabel.setStyle("-fx-font-size: 22px; -fx-cursor: hand; -fx-text-fill: " + color + ";");
            Tooltip tooltip = new Tooltip(isFavorite ? "Retirer des favoris" : "Ajouter aux favoris");
            Tooltip.install(favoriteLabel, tooltip);
        }
    }

    private void toggleFavorite() {
        if (favorisService == null)
            return;
        try {
            if (isFavorite) {
                favorisService.removeFavoriteOeuvre(oeuvre.getId());
                isFavorite = false;
                System.out.println("❤️ Œuvre retirée des favoris: " + oeuvre.getTitle());
                showAlert("Succès", "Œuvre retirée des favoris");
            } else {
                favorisService.addFavoriteOeuvre(oeuvre.getId());
                isFavorite = true;
                System.out.println("❤️ Œuvre ajoutée aux favoris: " + oeuvre.getTitle());
                showAlert("Succès", "Œuvre ajoutée aux favoris");
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

    public Oeuvre getOeuvre() {
        return oeuvre;
    }
}