package com.example.app.views;

import com.example.app.entities.Personnage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PersonnageDetailView extends VBox {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String BG_DARK = "#0D0F0F";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private static final String TEXT_SECONDARY = "#B0B9B6";

    private final VBox mainBox;
    private final Personnage personnage;

    public PersonnageDetailView(Personnage personnage) {
        this.personnage = personnage;
        this.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Top Navigation
        this.getChildren().add(new HeaderView());

        mainBox = new VBox();
        mainBox.setSpacing(0);
        mainBox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        setupHeader();
        setupDashboard();

        ScrollPane scrollPane = new ScrollPane(mainBox);
        scrollPane.setStyle("-fx-background: " + BG_MAIN + "; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().add(scrollPane);
    }

    private void setupHeader() {
        StackPane heroContainer = new StackPane();
        heroContainer.setPrefHeight(400);
        heroContainer.setMinHeight(400);
        heroContainer.setStyle("-fx-background-color: " + BG_DARK + ";");

        ImageView hero = new ImageView();
        hero.setFitWidth(1200);
        hero.setFitHeight(400);
        hero.setPreserveRatio(false); // Make it a banner style using standard scaling
        
        if (personnage.getPortraitImage() != null && personnage.getPortraitImage().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(personnage.getPortraitImage());
                hero.setImage(new javafx.scene.image.Image(bis));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        Region gradient = new Region();
        gradient.setStyle("-fx-background-color: linear-gradient(to top, " + BG_MAIN + " 0%, transparent 80%);");

        VBox headerText = new VBox(15);
        headerText.setAlignment(Pos.BOTTOM_LEFT);
        headerText.setPadding(new Insets(40));

        Label nameTitle = new Label(personnage.getName() != null ? personnage.getName().toUpperCase() : "UNKNOWN");
        nameTitle.setFont(Font.font("System", FontWeight.BOLD, 48));
        nameTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0, 0, 2);");

        Label classBadge = new Label(personnage.getClassRole() != null ? personnage.getClassRole() : "Unknown");
        classBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-padding: 8 20; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-font-size: 14px;");

        javafx.scene.control.Button btnEdit = new javafx.scene.control.Button("Modifier");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 8px; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        applyButtonHoverEffect(btnEdit, PRIMARY_COLOR, "transparent");
        btnEdit.setOnAction(e -> {
            try {
                this.getScene().setRoot(new PersonnageCreateView(personnage));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 20; -fx-border-color: #e74c3c; -fx-border-radius: 8px; -fx-cursor: hand;");
        applyButtonHoverEffect(btnDelete, "#E6FFF6", "#e74c3c");
        btnDelete.setOnAction(e -> {
            try {
                new com.example.app.services.PersonnageService().delete(personnage.getId());
                com.example.app.utils.SceneManager.getInstance().loadScene("/personnages");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        HBox actionsBox = new HBox(15, classBadge, btnEdit, btnDelete);
        actionsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        headerText.getChildren().addAll(nameTitle, actionsBox);

        heroContainer.getChildren().addAll(hero, gradient, headerText);
        mainBox.getChildren().add(heroContainer);
    }

    private void applyButtonHoverEffect(javafx.scene.control.Button btn, String hoverTextUrl, String hoverBgUrl) {
        String originalStyle = btn.getStyle();
        btn.setOnMouseEntered(e -> btn.setStyle(originalStyle + " -fx-opacity: 0.8;"));
        btn.setOnMouseExited(e -> btn.setStyle(originalStyle));
    }

    private void setupDashboard() {
        HBox dashboard = new HBox(40);
        dashboard.setPadding(new Insets(40));
        dashboard.setAlignment(Pos.TOP_CENTER);

        // LEFT: LORE
        VBox loreBox = new VBox(20);
        loreBox.setPrefWidth(600);
        loreBox.setPadding(new Insets(30));
        loreBox.setStyle("-fx-background-color: " + BG_DARK + "; -fx-background-radius: 16px;");

        Label lblHistory = new Label("Contexte Historique");
        lblHistory.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 22px;");
        
        Text txtHistory = new Text(personnage.getHistoryContext());
        txtHistory.setFill(Color.web(TEXT_SECONDARY));
        txtHistory.setWrappingWidth(540);
        txtHistory.setStyle("-fx-font-size: 15px; -fx-line-spacing: 1.5em;");

        Label lblAbilities = new Label("Capacités & Pouvoirs");
        lblAbilities.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 22px;");
        
        Text txtAbilities = new Text(personnage.getAbilitiesPowers());
        txtAbilities.setFill(Color.web(TEXT_SECONDARY));
        txtAbilities.setWrappingWidth(540);
        txtAbilities.setStyle("-fx-font-size: 15px; -fx-line-spacing: 1.5em;");

        loreBox.getChildren().addAll(lblHistory, txtHistory, lblAbilities, txtAbilities);

        // RIGHT: STATS
        VBox statsBox = new VBox(25);
        statsBox.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 30; -fx-background-radius: 16px;");
        statsBox.setPrefWidth(350);

        Label lblStats = new Label("Statistiques");
        lblStats.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 22px;");
        statsBox.getChildren().add(lblStats);

        statsBox.getChildren().add(createStatBar("Force", personnage.getStrength()));
        statsBox.getChildren().add(createStatBar("Agilité", personnage.getAgility()));
        statsBox.getChildren().add(createStatBar("Magie", personnage.getMagic()));
        statsBox.getChildren().add(createStatBar("Défense", personnage.getDefense()));

        dashboard.getChildren().addAll(loreBox, statsBox);
        mainBox.getChildren().add(dashboard);
    }

    private VBox createStatBar(String statName, int value) {
        VBox box = new VBox(5);
        
        HBox labels = new HBox();
        Label name = new Label(statName);
        name.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        Label valLbl = new Label(String.valueOf(value));
        valLbl.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        labels.getChildren().addAll(name, spacer, valLbl);

        ProgressBar pb = new ProgressBar(value / 100.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_COLOR + "; -fx-control-inner-background: " + BG_MAIN + ";");

        box.getChildren().addAll(labels, pb);
        return box;
    }
}
