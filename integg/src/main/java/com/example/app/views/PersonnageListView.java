package com.example.app.views;

import com.example.app.entities.Personnage;
import com.example.app.services.PortraitGenerationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.ArrayList;
import com.example.app.services.PersonnageService;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

public class PersonnageListView extends BorderPane {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String BG_DARK = "#0D0F0F";
    private static final String FORM_PANEL_BG = "#1A1F1EB3";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private static final String TEXT_SECONDARY = "#B0B9B6";

    private FlowPane flowPane;
    private TextField searchField;
    private ComboBox<String> sortCombo;
    private ComboBox<String> roleCombo;
    private PersonnageService personnageService = new PersonnageService();

    public PersonnageListView() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setTop(new HeaderView("/personnages"));
        setupHeader();
        setupLeft();
        setupCenter();

        loadPersonnages();
    }

    private void setupHeader() {
        // Handled inside center to appear exactly under top
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
        searchField.setPromptText("Rechercher un personnage...");
        searchField.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 8;");
        searchField.textProperty().addListener((obs, oldV, newV) -> loadPersonnages());

        Label lblRole = new Label("Classe / Rôle");
        lblRole.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");
        roleCombo = new ComboBox<>(FXCollections.observableArrayList("Tout", "Guerrier", "Mage", "Voleur", "Prêtre", "Druide", "Paladin", "Chasseur"));
        roleCombo.setValue("Tout");
        roleCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + ";");
        roleCombo.setOnAction(e -> loadPersonnages());

        Label lblSort = new Label("Trier par");
        lblSort.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");
        sortCombo = new ComboBox<>(FXCollections.observableArrayList("Récents", "A-Z", "Z-A", "Niveau Max (Stats)", "Plus de Force", "Plus de Magie"));
        sortCombo.setValue("Récents");
        sortCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + ";");
        sortCombo.setOnAction(e -> loadPersonnages());

        leftBox.getChildren().addAll(lblSearch, searchField, lblRole, roleCombo, lblSort, sortCombo);
        this.setLeft(leftBox);
    }

    private void setupCenter() {
        VBox centerBox = new VBox();
        centerBox.setSpacing(0);
        centerBox.setStyle("-fx-background-color: " + BG_DARK + ";");

        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        headerContainer.setSpacing(20);
        headerContainer.setPadding(new Insets(20));

        Label title = new Label("Tous les Personnages");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 28px; -fx-font-weight: bold;");

        Button btnBattle = new Button("⚔️ Culling Games");
        btnBattle.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10 20;");
        btnBattle.setOnAction(e -> com.example.app.utils.SceneManager.getInstance().loadScene("/battle"));
        applyMagicEffect(btnBattle);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        headerContainer.getChildren().addAll(title, spacer, btnBattle);

        if (com.example.app.utils.UserSession.isLoggedIn()) {
            Button btnCreate = new Button("+ Créer un Personnage");
            btnCreate.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10 20;");
            btnCreate.setOnAction(e -> com.example.app.utils.SceneManager.getInstance().loadScene("/personnages/create"));
            applyMagicEffect(btnCreate);
            headerContainer.getChildren().add(btnCreate);
        }
        centerBox.getChildren().add(headerContainer);

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

    public void setPersonnages(List<Personnage> personnages) {
        flowPane.getChildren().clear();
        for (Personnage p : personnages) {
            flowPane.getChildren().add(createCard(p));
        }
    }

    private void loadPersonnages() {
        try {
            String search = searchField != null ? searchField.getText() : "";
            String role = roleCombo != null ? roleCombo.getValue() : "Tout";
            String sort = sortCombo != null ? sortCombo.getValue() : "Récents";
            
            List<String> roles = new ArrayList<>();
            if (!"Tout".equals(role)) {
                roles.add(role);
            }
            
            setPersonnages(personnageService.searchPersonnages(search, roles, null, sort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(Personnage personnage) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + FORM_PANEL_BG + "; -fx-background-radius: 12px;");
        card.setPrefWidth(220);
        card.setMaxWidth(220);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(190);
        imgView.setFitHeight(190);
        imgView.setPreserveRatio(false);
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(190, 190);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        imgView.setClip(clip);

        if (personnage.getPortraitImage() != null && personnage.getPortraitImage().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(personnage.getPortraitImage());
                imgView.setImage(new javafx.scene.image.Image(bis));
            } catch (Exception ex) {
                ex.printStackTrace();
                loadDicebearPortrait(imgView, personnage.getName());
            }
        } else {
            loadDicebearPortrait(imgView, personnage.getName());
        }

        Label name = new Label(personnage.getName() != null ? personnage.getName() : "Unknown");
        name.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label classBadge = new Label(personnage.getClassRole() != null ? personnage.getClassRole() : "Unknown-Class");
        classBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "33; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-padding: 3 8; -fx-background-radius: 12px; -fx-font-size: 11px;");

        card.getChildren().addAll(imgView, name, classBadge);

        card.setOnMouseClicked(e -> {
            javafx.scene.Scene currentScene = this.getScene();
            if (currentScene != null) {
                currentScene.setRoot(new PersonnageDetailView(personnage));
            }
        });

        applyMagicEffect(card);

        return card;
    }

    private void applyMagicEffect(javafx.scene.layout.Region node) {
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

    private void loadDicebearPortrait(ImageView imgView, String characterName) {
        new Thread(() -> {
            try {
                String url = PortraitGenerationService.generatePortraitURL(characterName);
                Image image = new Image(url, true);
                javafx.application.Platform.runLater(() -> imgView.setImage(image));
            } catch (Exception ex) {
                System.err.println("Dicebear portrait failed: " + ex.getMessage());
            }
        }).start();
    }
}
