package com.example.app.views;

import com.example.app.entities.Personnage;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        mainBox.setSpacing(20);
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
        ImageView hero = new ImageView();
        hero.setFitWidth(800);
        hero.setFitHeight(300);
        hero.setPreserveRatio(false); // Make it a banner style using standard scaling
        
        Label nameTitle = new Label(personnage.getName() != null ? personnage.getName().toUpperCase() : "UNKNOWN");
        nameTitle.setFont(Font.font("System", FontWeight.BOLD, 36));
        nameTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        Label classBadge = new Label(personnage.getClassRole() != null ? personnage.getClassRole() : "Unknown");
        classBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "33; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-padding: 5 12; -fx-background-radius: 12px; -fx-font-weight: bold;");

        javafx.scene.control.Button btnEdit = new javafx.scene.control.Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: #1A1F1E; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 5 15;");
        btnEdit.setOnAction(e -> {
            try {
                this.getScene().setRoot(new PersonnageCreateView(personnage));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 5 15;");
        btnDelete.setOnAction(e -> {
            try {
                new com.example.app.services.PersonnageService().delete(personnage.getId());
                com.example.app.utils.SceneManager.getInstance().loadScene("/personnages");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        HBox actionsBox = new HBox(10, classBadge, btnEdit, btnDelete);
        actionsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox headerText = new VBox(10);
        headerText.setPadding(new Insets(20, 40, 20, 40));
        headerText.getChildren().addAll(nameTitle, actionsBox);

        mainBox.getChildren().addAll(hero, headerText);
    }

    private void setupDashboard() {
        HBox dashboard = new HBox(40);
        dashboard.setPadding(new Insets(0, 40, 40, 40));

        // LEFT: LORE
        VBox loreBox = new VBox(15);
        loreBox.setPrefWidth(450);

        Label lblHistory = new Label("Contexte Historique");
        lblHistory.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 18px;");
        Text txtHistory = new Text(personnage.getHistoryContext());
        txtHistory.setFill(Color.web(TEXT_SECONDARY));
        txtHistory.setWrappingWidth(450);

        Label lblAbilities = new Label("Capacités & Pouvoirs");
        lblAbilities.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 18px;");
        Text txtAbilities = new Text(personnage.getAbilitiesPowers());
        txtAbilities.setFill(Color.web(TEXT_SECONDARY));
        txtAbilities.setWrappingWidth(450);

        loreBox.getChildren().addAll(lblHistory, txtHistory, lblAbilities, txtAbilities);

        // RIGHT: STATS
        VBox statsBox = new VBox(15);
        statsBox.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 20; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        statsBox.setPrefWidth(250);

        Label lblStats = new Label("Statistiques");
        lblStats.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 18px;");
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

