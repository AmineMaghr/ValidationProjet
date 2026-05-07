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

    public HeaderView() {
        this.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-padding: 15 30;");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);

        // Left: Logo
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setStyle("-fx-cursor: hand;");
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
        
        Button btnAccueil = createMagicButton("Accueil", "/");
        Button btnDiscover = createMagicButton("Découvrir", "/discover");
        Button btnUnivers = createMagicButton("Univers", "/universes");
        Button btnPersonnages = createMagicButton("Personnages", "/personnages");
        Button btnOeuvre = createMagicButton("Œuvre", "/oeuvre");
        Button btnArtefacts = createMagicButton("Artefacts", "/artefact");
        Button btnBoutique = createMagicButton("Boutique", "/shop");
        Button btnDefis = createMagicButton("Défis", "/challenges");
        
        navBox.getChildren().addAll(btnAccueil, btnDiscover, btnUnivers, btnPersonnages, btnOeuvre, btnArtefacts, btnBoutique, btnDefis);

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
                btnAdmin.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-padding: 8 20; -fx-font-weight: bold;");
                authBox.getChildren().add(btnAdmin);
            }
            Button btnProfile = createMagicButton("👤 " + com.example.app.utils.UserSession.getUsername(), "/profile");
            btnProfile.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_MAIN + "; -fx-background-radius: 12px; -fx-padding: 8 20; -fx-font-weight: bold;");
            authBox.getChildren().add(btnProfile);
        } else {
            Button btnLogin = createMagicButton("Connexion", "/login");
            Button btnRegister = createMagicButton("S'inscrire", "/register");
            btnRegister.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_MAIN + "; -fx-background-radius: 12px; -fx-padding: 8 20; -fx-font-weight: bold;");
            authBox.getChildren().addAll(btnLogin, btnRegister);
        }

        this.getChildren().addAll(logoBox, navBox, spacer, authBox);
    }

    private Button createMagicButton(String text, String route) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-cursor: hand;");
        
        btn.setOnAction(e -> SceneManager.getInstance().loadScene(route));

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 14px; -fx-cursor: hand;");
            DropShadow shadow = new DropShadow(15, Color.web(PRIMARY_COLOR));
            btn.setEffect(shadow);
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-cursor: hand;");
            btn.setEffect(null);
        });

        return btn;
    }
}
