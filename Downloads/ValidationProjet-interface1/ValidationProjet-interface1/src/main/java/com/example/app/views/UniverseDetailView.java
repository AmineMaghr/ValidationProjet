package com.example.app.views;

import com.example.app.entities.Universe;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class UniverseDetailView extends VBox {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private static final String TEXT_SECONDARY = "#B0B9B6";

    private final VBox mainBox;
    private final Universe universe;

    public UniverseDetailView(Universe universe) {
        this.universe = universe;
        this.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Top Navigation
        this.getChildren().add(new HeaderView());

        mainBox = new VBox();
        mainBox.setSpacing(20);
        mainBox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        setupHeader();
        setupBody();

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
        hero.setPreserveRatio(true);
        // Load image here from universe.getBannerImage() if it exists

        Label nameTitle = new Label(universe.getName());
        nameTitle.setFont(Font.font("System", FontWeight.BOLD, 36));
        nameTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        Label genreBadge = new Label(universe.getGenre());
        genreBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "33; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-padding: 5 12; -fx-background-radius: 12px; -fx-font-weight: bold;");

        javafx.scene.control.Button btnEdit = new javafx.scene.control.Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: #1A1F1E; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 5 15;");
        btnEdit.setOnAction(e -> {
            try {
                this.getScene().setRoot(new UniverseCreateView(universe));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 5 15;");
        btnDelete.setOnAction(e -> {
            try {
                new com.example.app.services.UniverseService().delete(universe.getId());
                com.example.app.utils.SceneManager.getInstance().loadScene("/universes");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.layout.HBox actionsBox = new javafx.scene.layout.HBox(10, genreBadge, btnEdit, btnDelete);
        actionsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox headerText = new VBox(10);
        headerText.setPadding(new Insets(20, 40, 20, 40));
        headerText.getChildren().addAll(nameTitle, actionsBox);

        mainBox.getChildren().addAll(hero, headerText);
    }

    private void setupBody() {
        VBox body = new VBox(15);
        body.setPadding(new Insets(0, 40, 40, 40));

        Label shortDescTitle = new Label("Description Courte");
        shortDescTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 18px;");

        Text shortDescText = new Text(universe.getShortDescription());
        shortDescText.setFill(Color.web(TEXT_SECONDARY));
        shortDescText.setWrappingWidth(700);

        Label storyTitle = new Label("Contexte Narratif");
        storyTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 18px;");

        Text storyContextText = new Text(universe.getStoryContext());
        storyContextText.setFill(Color.web(TEXT_SECONDARY));
        storyContextText.setWrappingWidth(700);

        body.getChildren().addAll(shortDescTitle, shortDescText, storyTitle, storyContextText);

        mainBox.getChildren().add(body);
    }
}

