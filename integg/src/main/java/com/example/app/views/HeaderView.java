package com.example.app.views;

import com.example.app.utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HeaderView extends HBox {
 
    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    
    private String currentRoute;
 
    public HeaderView() {
        this("/");
    }
 
    public HeaderView(String activeRoute) {
        this.currentRoute = activeRoute;
        
        // Use the CSS class from accueil.css
        this.getStyleClass().add("header");
        this.getStyleClass().add("header-content");
        
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);
        this.setPadding(new Insets(15, 30, 15, 30));
 
        // Left: Logo
        HBox logoBox = new HBox(10);
        logoBox.getStyleClass().add("logo");
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setOnMouseClicked(e -> SceneManager.getInstance().loadScene("/"));
        
        Label icon = new Label("⚔️");
        icon.setStyle("-fx-font-size: 24px;");
        
        Label logo = new Label("Midgar");
        logo.setFont(Font.font("System", FontWeight.BOLD, 20));
        logo.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        
        logoBox.getChildren().addAll(icon, logo);
 
        // Center: Magic Buttons Navigation
        HBox navBox = new HBox(5);
        navBox.setAlignment(Pos.CENTER_LEFT);
        
        navBox.getChildren().addAll(
            createMagicButton("Accueil", "/"),
            createMagicButton("Découvrir", "/discover"),
            createMagicButton("Univers", "/universes"),
            createMagicButton("Personnages", "/personnages"),
            createMagicButton("Œuvre", "/oeuvre"),
            createMagicButton("Artefacts", "/artefact"),
            createMagicButton("Boutique", "/shop"),
            createMagicButton("Défis", "/challenges")
        );
 
        // Spacer pushes Auth Buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(navBox, Priority.ALWAYS);
 
        // Right: Auth Buttons (session-aware)
        HBox authBox = new HBox(10);
        authBox.setAlignment(Pos.CENTER_RIGHT);
 
        if (com.example.app.utils.UserSession.isLoggedIn()) {
            if (com.example.app.utils.UserSession.isAdmin()) {
                Button btnAdmin = createMagicButton("Back-Office", "/admin");
                btnAdmin.getStyleClass().setAll("btn-secondary"); // Use secondary style for admin
                btnAdmin.setStyle("-fx-padding: 8 20; -fx-font-size: 14px;");
                authBox.getChildren().add(btnAdmin);
            }
            Button btnProfile = createMagicButton("👤 " + com.example.app.utils.UserSession.getUsername(), "/profile");
            btnProfile.getStyleClass().setAll("btn-primary");
            btnProfile.setStyle("-fx-padding: 8 20; -fx-font-size: 14px;");
            authBox.getChildren().add(btnProfile);
        } else {
            Button btnLogin = createMagicButton("Connexion", "/login");
            Button btnRegister = createMagicButton("S'inscrire", "/register");
            btnRegister.getStyleClass().setAll("btn-primary");
            btnRegister.setStyle("-fx-padding: 8 20; -fx-font-size: 14px;");
            authBox.getChildren().addAll(btnLogin, btnRegister);
        }
 
        this.getChildren().addAll(logoBox, navBox, spacer, authBox);
    }
 
    private Button createMagicButton(String text, String route) {
        Button btn = new Button(text);
        btn.getStyleClass().add("magic-btn");
        
        // Highlight if active
        if (route.equals(currentRoute)) {
            btn.getStyleClass().add("active");
        }
        
        btn.setOnAction(e -> SceneManager.getInstance().loadScene(route));
 
        return btn;
    }
}

