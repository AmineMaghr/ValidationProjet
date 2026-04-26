package com.example.app.views;

import com.example.app.entities.Artefact;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.File;

public class ArtefactCard extends VBox {

    private Artefact artefact;
    private Runnable onClick;

    public ArtefactCard(Artefact artefact, Runnable onClick) {
        this.artefact = artefact;
        this.onClick = onClick;
        initialize();
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

        getChildren().addAll(nameLabel, typeLabel, universeLabel, rarityLabel, originsText);

        // Effet hover
        setOnMouseEntered(e -> {
            setStyle("-fx-background-color: #16202a; -fx-background-radius: 12; -fx-border-color: #18E3A4; -fx-border-radius: 12; -fx-cursor: hand;");
            setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.valueOf("#18E3A4").deriveColor(0, 1, 1, 0.3)));
        });
        setOnMouseExited(e -> {
            setStyle("-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;");
            setEffect(null);
        });

        setOnMouseClicked(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });
    }

    public Artefact getArtefact() {
        return artefact;
    }
}