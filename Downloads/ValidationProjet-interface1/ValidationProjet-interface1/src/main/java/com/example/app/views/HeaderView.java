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
        Label logo = new Label("Midgar");
        logo.setFont(Font.font("System", FontWeight.BOLD, 24));
        logo.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        // Spacer
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Center: Magic Buttons Navigation
        HBox navBox = new HBox(15);
        navBox.setAlignment(Pos.CENTER);
        Button btnAccueil = createMagicButton("Accueil", "/");
        Button btnUnivers = createMagicButton("Univers", "/universes");
        Button btnPersonnages = createMagicButton("Personnages", "/personnages");
        Button btnBoutique = createMagicButton("Boutique", "/shop");
        navBox.getChildren().addAll(btnAccueil, btnUnivers, btnPersonnages, btnBoutique);

        // Spacer
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Right: Auth Buttons
        HBox authBox = new HBox(10);
        authBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnLogin = createMagicButton("Connexion", "/login");
        Button btnRegister = createMagicButton("S'inscrire", "/register");
        btnRegister.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_MAIN + "; -fx-background-radius: 12px; -fx-padding: 8 20; -fx-font-weight: bold;");
        authBox.getChildren().addAll(btnLogin, btnRegister);

        this.getChildren().addAll(logo, spacer1, navBox, spacer2, authBox);
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



