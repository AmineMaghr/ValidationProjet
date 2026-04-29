package com.example.app.views;

import com.example.app.entities.Universe;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;
import com.example.app.services.UniverseService;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

public class UniverseListView extends BorderPane {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String BG_DARK = "#0D0F0F";
    private static final String FORM_PANEL_BG = "#1A1F1EB3";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private static final String TEXT_SECONDARY = "#B0B9B6";

    private FlowPane flowPane;
    private TextField searchField;
    private ComboBox<String> sortCombo;
    private ComboBox<String> genreCombo;
    private UniverseService universeService = new UniverseService();

    public UniverseListView() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Header View at the very top (NavBar)
        this.setTop(new HeaderView());

        // Setup Internal Layout
        setupHeader();
        setupLeft();
        setupCenter();

        // Load data
        loadUniverses();
    }

    private void setupHeader() {
        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        headerContainer.setSpacing(20);
        headerContainer.setPadding(new Insets(20));
        headerContainer.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label title = new Label("Tous les Univers");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 28px; -fx-font-weight: bold;");

        Button btnCreate = new Button("+ Créer un Univers");
        btnCreate.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10 20;");
        btnCreate.setOnAction(e -> com.example.app.utils.SceneManager.getInstance().loadScene("/universes/create"));
        applyMagicEffect(btnCreate);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        headerContainer.getChildren().addAll(title, spacer, btnCreate);

        VBox topRegion = new VBox(headerContainer);
        BorderPane.setAlignment(topRegion, Pos.TOP_CENTER);
    }

    private void setupLeft() {
        VBox leftBox = new VBox();
        leftBox.setSpacing(15);
        leftBox.setPadding(new Insets(20));
        leftBox.setPrefWidth(250);
        leftBox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        Label lblSearch = new Label("Recherche");
        lblSearch.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");

        searchField = new TextField();
        searchField.setPromptText("Rechercher un univers...");
        searchField.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 8;");
        searchField.textProperty().addListener((obs, oldV, newV) -> loadUniverses());

        Label lblGenre = new Label("Genre");
        lblGenre.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");
        genreCombo = new ComboBox<>(FXCollections.observableArrayList("Tout", "Fantasy", "Science-Fiction", "Horreur", "Steampunk", "Médiéval"));
        genreCombo.setValue("Tout");
        genreCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + ";");
        genreCombo.setOnAction(e -> loadUniverses());

        Label lblSort = new Label("Trier par");
        lblSort.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");
        sortCombo = new ComboBox<>(FXCollections.observableArrayList("Récents", "A-Z", "Z-A"));
        sortCombo.setValue("Récents");
        sortCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + ";");
        sortCombo.setOnAction(e -> loadUniverses());

        leftBox.getChildren().addAll(lblSearch, searchField, lblGenre, genreCombo, lblSort, sortCombo);
        this.setLeft(leftBox);
    }

    private void setupCenter() {
        VBox centerBox = new VBox();
        centerBox.setSpacing(0);
        centerBox.setStyle("-fx-background-color: " + BG_DARK + ";");

        // The inner header
        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        headerContainer.setSpacing(20);
        headerContainer.setPadding(new Insets(20));

        Label title = new Label("Tous les Univers");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 28px; -fx-font-weight: bold;");

        Button btnCreate = new Button("+ Créer un Univers");
        btnCreate.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10 20;");
        btnCreate.setOnAction(e -> com.example.app.utils.SceneManager.getInstance().loadScene("/universes/create"));
        applyMagicEffect(btnCreate);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        headerContainer.getChildren().addAll(title, spacer, btnCreate);
        centerBox.getChildren().add(headerContainer);

        // Core List
        flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG_DARK + "; -fx-border-color: transparent;");
        
        centerBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        this.setCenter(centerBox);
    }

    public void setUniverses(List<Universe> universes) {
        flowPane.getChildren().clear();
        for (Universe u : universes) {
            flowPane.getChildren().add(createUniverseCard(u));
        }
    }

    private void loadUniverses() {
        try {
            String search = searchField != null ? searchField.getText() : "";
            String genre = genreCombo != null ? genreCombo.getValue() : "Tout";
            String sort = sortCombo != null ? sortCombo.getValue() : "Récents";
            setUniverses(universeService.searchUniverses(search, genre, sort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createUniverseCard(Universe universe) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + FORM_PANEL_BG + "; -fx-background-radius: 12px;");
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(250);
        imgView.setFitHeight(140);
        imgView.setPreserveRatio(false);
        // Style clipping logic programmatic equivalent via CSS usually is just setting radius. JavaFX ImageView clipping requires explicit shape.
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(250, 140);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        imgView.setClip(clip);
        
        if (universe.getBannerImage() != null && universe.getBannerImage().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(universe.getBannerImage());
                imgView.setImage(new javafx.scene.image.Image(bis));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Label name = new Label(universe.getName() != null ? universe.getName() : "Unknown");
        name.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 18px;");

        Label genreBadge = new Label(universe.getGenre());
        genreBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "33; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-padding: 3 8; -fx-background-radius: 12px; -fx-font-size: 11px;");

        Label desc = new Label(universe.getShortDescription());
        desc.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");
        desc.setWrapText(true);
        desc.setMaxHeight(60);

        card.getChildren().addAll(imgView, name, genreBadge, desc);

        card.setOnMouseClicked(e -> {
            javafx.scene.Scene currentScene = this.getScene();
            if (currentScene != null) {
                currentScene.setRoot(new UniverseDetailView(universe));
            }
        });

        applyMagicEffect(card);

        return card;
    }

    private void applyMagicEffect(Region node) {
        node.setOnMouseEntered(e -> {
            node.setScaleX(1.05);
            node.setScaleY(1.05);
            DropShadow shadow = new DropShadow(15, Color.web(PRIMARY_COLOR));
            node.setEffect(shadow);
        });

        node.setOnMouseExited(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
            node.setEffect(null);
        });
    }
}








