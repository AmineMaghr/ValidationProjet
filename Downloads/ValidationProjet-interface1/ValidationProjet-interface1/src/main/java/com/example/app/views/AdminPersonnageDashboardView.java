package com.example.app.views;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.example.app.utils.SceneManager;
import com.example.app.services.PersonnageService;
import com.example.app.entities.Personnage;

import java.util.List;

public class AdminPersonnageDashboardView extends BorderPane {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String BG_DARK = "#0B0F0E";
    private static final String SIDEBAR_HOVER = "#1A1F1E";
    private static final String BORDER_COLOR = "#2A3139";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private static final String TEXT_SECONDARY = "#B0B9B6";

    private Label lblPersoTotals;
    private Label lblHerosStats;
    private Label lblUniversConn;

    public AdminPersonnageDashboardView() {
        this.setStyle("-fx-background-color: " + BG_MAIN + ";");
        setupSidebar();
        setupMainContent();
        refreshStats();
    }

    private void refreshStats() {
        try {
            PersonnageService ps = new PersonnageService();
            List<Personnage> all = ps.select();
            lblPersoTotals.setText(String.valueOf(all.size()));
            
            long herosCount = all.stream().filter(p -> (p.getStrength() + p.getAgility() + p.getMagic() + p.getDefense()) > 200).count();
            lblHerosStats.setText(String.valueOf(herosCount));

            long universAttachCount = all.stream().filter(p -> p.getUniverse() != null).count();
            lblUniversConn.setText(String.valueOf(universAttachCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: " + BG_DARK + "; -fx-border-color: transparent " + BORDER_COLOR + " transparent transparent; -fx-border-width: 1px;");
        
        // Brand Header
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(30, 20, 40, 20));
        
        Label icon = new Label("⚡");
        icon.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 24px;");
        
        Label title = new Label("Midgar Admin");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        brand.getChildren().addAll(icon, title);

        // Sidebar Links
        VBox navLinks = new VBox(10);
        navLinks.setPadding(new Insets(0, 15, 0, 15));
        
        navLinks.getChildren().addAll(
            createSidebarLink("🏠 Tableau de Bord", "/admin/dashboard"),
            createSidebarLink("🌌 Gestion des Univers", "/admin/universes"),
            createSidebarLink("👤 Gestion des Personnages", "/admin/personnages"),
            createSidebarLink("↩ Retour au Site", "/")
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
            btn.setScaleX(1.02);
            btn.setScaleY(1.02);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 16px; -fx-alignment: CENTER-LEFT; -fx-padding: 15; -fx-background-radius: 8px; -fx-cursor: hand;");
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        btn.setOnAction(e -> SceneManager.getInstance().loadScene(route));

        return btn;
    }

    private void setupMainContent() {
        VBox mainBox = new VBox();
        mainBox.setPadding(new Insets(40));
        mainBox.setSpacing(20);
        mainBox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Section Header
        BorderPane sectionHeader = new BorderPane();
        Label heading = new Label("Gestion des Personnages");
        heading.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        lblPersoTotals = new Label("0");
        lblPersoTotals.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 32px; -fx-font-weight: bold;");

        lblHerosStats = new Label("0");
        lblHerosStats.setStyle("-fx-text-fill: #F39C12; -fx-font-size: 32px; -fx-font-weight: bold;");

        lblUniversConn = new Label("0");
        lblUniversConn.setStyle("-fx-text-fill: #3498DB; -fx-font-size: 32px; -fx-font-weight: bold;");

        // Stat Cards HBox
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
            createStatCard("Personnages Créés", lblPersoTotals, "#18E3A4"),
            createStatCard("Héros de la semaine", lblHerosStats, "#F39C12"),
            createStatCard("Univers Attachés", lblUniversConn, "#3498DB")
        );
        statsBox.setPadding(new Insets(10, 0, 30, 0));

        // Search and Actions HBox
        HBox toolsBar = new HBox(15);
        toolsBar.setAlignment(Pos.CENTER_RIGHT);

        // Data Table Reference created early for search attachment
        PersonnageTableView tableView = new PersonnageTableView();

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un personnage...");
        searchField.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 15;");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldV, newV) -> tableView.search(newV));

        Button mainBtn = new Button("+ Ajouter Personnage");
        mainBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        mainBtn.setOnMouseEntered(e -> {
            mainBtn.setScaleX(1.05);
            mainBtn.setScaleY(1.05);
            mainBtn.setEffect(new DropShadow(15, Color.web(PRIMARY_COLOR)));
        });

        mainBtn.setOnMouseExited(e -> {
            mainBtn.setScaleX(1.0);
            mainBtn.setScaleY(1.0);
            mainBtn.setEffect(null);
        });
        
        mainBtn.setOnAction(e -> getScene().setRoot(new PersonnageCreateView(null, true)));

        HBox rightControls = new HBox(15, searchField, mainBtn);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        sectionHeader.setLeft(heading);
        sectionHeader.setRight(rightControls);
        
        // Wrap header and stats together
        VBox topArea = new VBox(5, sectionHeader, statsBox);
        
        // Setup table growth
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);
        
        VBox wrapper = new VBox(20, topArea, tableView);
        wrapper.setPadding(new Insets(40));
        VBox.setVgrow(wrapper, javafx.scene.layout.Priority.ALWAYS);
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
        
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #252A29; -fx-background-radius: 12px; -fx-border-color: " + color + "; -fx-border-radius: 12px;");
            card.setEffect(new DropShadow(15, Color.web(color, 0.3)));
            card.setTranslateY(-5);
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: " + SIDEBAR_HOVER + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
            card.setEffect(null);
            card.setTranslateY(0);
        });
        return card;
    }
}
