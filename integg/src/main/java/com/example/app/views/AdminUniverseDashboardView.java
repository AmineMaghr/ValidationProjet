package com.example.app.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.example.app.services.UniverseService;
import com.example.app.entities.Universe;
import com.example.app.utils.SceneManager;

import java.util.List;

public class AdminUniverseDashboardView extends BorderPane {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN       = "#1A1F1E";
    private static final String BG_DARK       = "#0B0F0E";
    private static final String SIDEBAR_HOVER = "#1A1F1E";
    private static final String BORDER_COLOR  = "#2A3139";
    private static final String TEXT_PRIMARY  = "#E6FFF6";
    private static final String TEXT_SECONDARY= "#B0B9B6";

    private Label lblTotalUniverses;
    private Label lblGenresCount;
    private Label lblWithPersonnages;

    public AdminUniverseDashboardView() {
        this.setStyle("-fx-background-color: " + BG_MAIN + ";");

        if (!com.example.app.utils.UserSession.isAdmin()) {
            Platform.runLater(() -> SceneManager.getInstance().loadScene("/"));
            return;
        }

        setupSidebar();
        setupMainContent();
        refreshStats();
    }

    private void refreshStats() {
        try {
            UniverseService us = new UniverseService();
            List<Universe> all = us.select();
            lblTotalUniverses.setText(String.valueOf(all.size()));

            long genres = all.stream()
                .map(Universe::getGenre)
                .filter(g -> g != null && !g.isBlank())
                .distinct().count();
            lblGenresCount.setText(String.valueOf(genres));

            long withPerso = all.stream()
                .filter(u -> u.getPersonnages() != null && !u.getPersonnages().isEmpty())
                .count();
            lblWithPersonnages.setText(String.valueOf(withPerso));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: " + BG_DARK + "; -fx-border-color: transparent " + BORDER_COLOR + " transparent transparent; -fx-border-width: 1px;");

        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(30, 20, 40, 20));
        Label icon  = new Label("⚡");
        icon.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 24px;");
        Label title = new Label("Midgar Admin");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        brand.getChildren().addAll(icon, title);

        VBox navLinks = new VBox(10);
        navLinks.setPadding(new Insets(0, 15, 0, 15));
        navLinks.getChildren().addAll(
            createSidebarLink("🏠 Tableau de Bord",          "/admin/dashboard"),
            createSidebarLink("🌌 Gestion des Univers",      "/admin/universes"),
            createSidebarLink("👤 Gestion des Personnages",  "/admin/personnages"),
            createSidebarLink("↩ Retour au Site",            "/")
        );

        sidebar.getChildren().addAll(brand, navLinks);
        this.setLeft(sidebar);
    }

    private Button createSidebarLink(String text, String route) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 16px; -fx-alignment: CENTER-LEFT; -fx-padding: 15; -fx-background-radius: 8px; -fx-cursor: hand;");
        btn.setPrefWidth(250);
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + SIDEBAR_HOVER + "; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 16px; -fx-alignment: CENTER-LEFT; -fx-padding: 15; -fx-background-radius: 8px; -fx-cursor: hand;");
            btn.setScaleX(1.02); btn.setScaleY(1.02);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 16px; -fx-alignment: CENTER-LEFT; -fx-padding: 15; -fx-background-radius: 8px; -fx-cursor: hand;");
            btn.setScaleX(1.0); btn.setScaleY(1.0);
        });
        btn.setOnAction(e -> SceneManager.getInstance().loadScene(route));
        return btn;
    }

    private void setupMainContent() {
        lblTotalUniverses  = new Label("0");
        lblTotalUniverses.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 32px; -fx-font-weight: bold;");
        lblGenresCount     = new Label("0");
        lblGenresCount.setStyle("-fx-text-fill: #F39C12; -fx-font-size: 32px; -fx-font-weight: bold;");
        lblWithPersonnages = new Label("0");
        lblWithPersonnages.setStyle("-fx-text-fill: #3498DB; -fx-font-size: 32px; -fx-font-weight: bold;");

        HBox statsBox = new HBox(20,
            createStatCard("Univers Créés",       lblTotalUniverses,  "#18E3A4"),
            createStatCard("Genres distincts",    lblGenresCount,     "#F39C12"),
            createStatCard("Avec Personnages",    lblWithPersonnages, "#3498DB")
        );
        statsBox.setPadding(new Insets(10, 0, 30, 0));

        UniverseTableView tableView = new UniverseTableView();
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);

        Label heading = new Label("Gestion des Univers");
        heading.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un univers...");
        searchField.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 15;");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, o, n) -> tableView.search(n));

        Button addBtn = new Button("+ Ajouter Univers");
        addBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8px; -fx-cursor: hand;");
        addBtn.setOnMouseEntered(e -> { addBtn.setScaleX(1.05); addBtn.setScaleY(1.05); addBtn.setEffect(new DropShadow(15, Color.web(PRIMARY_COLOR))); });
        addBtn.setOnMouseExited(e  -> { addBtn.setScaleX(1.0);  addBtn.setScaleY(1.0);  addBtn.setEffect(null); });
        addBtn.setOnAction(e -> getScene().setRoot(new UniverseCreateView(null, true)));

        HBox rightControls = new HBox(15, searchField, addBtn);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        BorderPane sectionHeader = new BorderPane();
        sectionHeader.setLeft(heading);
        sectionHeader.setRight(rightControls);

        VBox topArea = new VBox(5, sectionHeader, statsBox);

        VBox wrapper = new VBox(20, topArea, tableView);
        wrapper.setPadding(new Insets(40));
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);

        this.setCenter(wrapper);
    }

    private VBox createStatCard(String title, Label lblValue, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + SIDEBAR_HOVER + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        card.setPrefWidth(220);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 14px;");
        card.getChildren().addAll(lblTitle, lblValue);
        card.setOnMouseEntered(e -> { card.setStyle("-fx-background-color: #252A29; -fx-background-radius: 12px; -fx-border-color: " + color + "; -fx-border-radius: 12px;"); card.setEffect(new DropShadow(15, Color.web(color, 0.3))); card.setTranslateY(-5); });
        card.setOnMouseExited(e  -> { card.setStyle("-fx-background-color: " + SIDEBAR_HOVER + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;"); card.setEffect(null); card.setTranslateY(0); });
        return card;
    }
}
