package com.example.app.views;

import com.example.app.entities.Universe;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class UniverseDetailView extends VBox {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String BG_DARK = "#141615";
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
        mainBox.setSpacing(0);
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
        StackPane heroContainer = new StackPane();
        heroContainer.setPrefHeight(400);
        heroContainer.setMinHeight(400);
        heroContainer.setStyle("-fx-background-color: " + BG_DARK + ";");

        ImageView hero = new ImageView();
        hero.setFitWidth(1200);
        hero.setFitHeight(400);
        hero.setPreserveRatio(false); // standard scaling for banner look
        
        if (universe.getBannerImage() != null && universe.getBannerImage().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(universe.getBannerImage());
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

        Label nameTitle = new Label(universe.getName());
        nameTitle.setFont(Font.font("System", FontWeight.BOLD, 48));
        nameTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0, 0, 2);");

        Label genreBadge = new Label(universe.getGenre() != null ? universe.getGenre() : "Unknown Genre");
        genreBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-padding: 8 20; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-font-size: 14px;");

        javafx.scene.control.Button btnEdit = new javafx.scene.control.Button("Modifier");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 8px; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        applyButtonHoverEffect(btnEdit, PRIMARY_COLOR, "transparent");
        btnEdit.setOnAction(e -> {
            try {
                this.getScene().setRoot(new UniverseCreateView(universe));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-border-color: #e74c3c; -fx-border-radius: 8px; -fx-padding: 8 20; -fx-cursor: hand;");
        applyButtonHoverEffect(btnDelete, "#E6FFF6", "#e74c3c");
        btnDelete.setOnAction(e -> {
            try {
                new com.example.app.services.UniverseService().delete(universe.getId());
                com.example.app.utils.SceneManager.getInstance().loadScene("/universes");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        HBox actionsBox = new HBox(15, genreBadge, btnEdit, btnDelete);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        headerText.getChildren().addAll(nameTitle, actionsBox);

        heroContainer.getChildren().addAll(hero, gradient, headerText);
        mainBox.getChildren().add(heroContainer);
    }

    private void applyButtonHoverEffect(javafx.scene.control.Button btn, String hoverTextUrl, String hoverBgUrl) {
        String originalStyle = btn.getStyle();
        btn.setOnMouseEntered(e -> btn.setStyle(originalStyle + " -fx-opacity: 0.8;"));
        btn.setOnMouseExited(e -> btn.setStyle(originalStyle));
    }

    private void setupBody() {
        VBox body = new VBox(30);
        body.setPadding(new Insets(40));
        body.setAlignment(Pos.TOP_CENTER);
        
        VBox contentCard = new VBox(25);
        contentCard.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 40; -fx-background-radius: 16px;");
        contentCard.setMaxWidth(900);

        Label shortDescTitle = new Label("Description Courte");
        shortDescTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 22px;");

        Text shortDescText = new Text(universe.getShortDescription());
        shortDescText.setFill(Color.web(TEXT_SECONDARY));
        shortDescText.setWrappingWidth(820);
        shortDescText.setStyle("-fx-font-size: 15px; -fx-line-spacing: 1.5em;");

        Label storyTitle = new Label("Contexte Narratif");
        storyTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 22px;");

        Text storyContextText = new Text(universe.getStoryContext());
        storyContextText.setFill(Color.web(TEXT_SECONDARY));
        storyContextText.setWrappingWidth(820);
        storyContextText.setStyle("-fx-font-size: 15px; -fx-line-spacing: 1.5em;");

        contentCard.getChildren().addAll(shortDescTitle, shortDescText, storyTitle, storyContextText);
        body.getChildren().add(contentCard);

        mainBox.getChildren().add(body);
    }
}

