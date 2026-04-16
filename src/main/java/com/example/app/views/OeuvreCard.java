package com.example.app.views;

import com.example.app.entities.Oeuvre;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.File;

public class OeuvreCard extends VBox {
    
    private Oeuvre oeuvre;
    private Runnable onClick;
    
    public OeuvreCard(Oeuvre oeuvre, Runnable onClick) {
        this.oeuvre = oeuvre;
        this.onClick = onClick;
        initialize();
    }
    
    private void initialize() {
        setStyle("-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;");
        setPadding(new Insets(15));
        setSpacing(10);
        setPrefWidth(280);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-color: #1a2530; -fx-background-radius: 8;");
        
        if (oeuvre.getImageUrl() != null && !oeuvre.getImageUrl().isEmpty()) {
            try {
                File imgFile = new File(oeuvre.getImageUrl());
                if (imgFile.exists()) {
                    Image img = new Image(imgFile.toURI().toString(), 250, 160, true, true);
                    imageView.setImage(img);
                }
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }
        
        Label titleLabel = new Label(oeuvre.getTitle());
        titleLabel.setStyle("-fx-text-fill: #fff; -fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        
        Label typeLabel = new Label(oeuvre.getType());
        typeLabel.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px; -fx-background-color: rgba(24,227,164,0.1); -fx-padding: 4 12; -fx-background-radius: 20;");
        
        Label authorLabel = new Label("✍️ " + oeuvre.getAuthor());
        authorLabel.setStyle("-fx-text-fill: #6a7a8a; -fx-font-size: 12px;");
        
        Text descText = new Text();
        String desc = oeuvre.getDescription();
        if (desc != null && desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        descText.setText(desc != null ? desc : "");
        descText.setStyle("-fx-fill: #b0b9b6; -fx-font-size: 12px;");
        descText.setWrappingWidth(250);
        
        getChildren().addAll(imageView, titleLabel, typeLabel, authorLabel, descText);
        
        setOnMouseClicked(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });
        
        setOnMouseEntered(e -> setStyle("-fx-background-color: #16202a; -fx-background-radius: 12; -fx-border-color: #18E3A4; -fx-border-radius: 12; -fx-cursor: hand;"));
        setOnMouseExited(e -> setStyle("-fx-background-color: #11161c; -fx-background-radius: 12; -fx-border-color: #1a2530; -fx-border-radius: 12; -fx-cursor: hand;"));
    }
    
    public Oeuvre getOeuvre() {
        return oeuvre;
    }
}
