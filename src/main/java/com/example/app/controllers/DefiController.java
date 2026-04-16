package com.example.app.controllers;

import com.example.app.entities.Defi;
import com.example.app.services.DefiService;
import com.example.app.services.ParticipationService;
import com.example.app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DefiController extends BaseController {

    @FXML private HBox authContainer;
    @FXML private TextField searchField;
    @FXML private TilePane activeDefisGrid;
    @FXML private TilePane pastDefisGrid;
    @FXML private Label emptyMessage;
    @FXML private VBox adminSection;
    @FXML private TextField adminTitreField;
    @FXML private TextField adminThemeField;
    @FXML private DatePicker adminDateDebutPicker;
    @FXML private DatePicker adminDateFinPicker;

    private final DefiService defiService = new DefiService();
    private final ParticipationService participationService = new ParticipationService();
    private final ObservableList<Defi> defis = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupAuthButtons();
        setupAdminSection();
        loadDefis();
    }

    private void setupAuthButtons() {
        authContainer.getChildren().clear();
        if (UserSession.isLoggedIn()) {
            var user = UserSession.getCurrentUser();
            Button profileBtn = new Button(user.getUsername());
            profileBtn.getStyleClass().addAll("magic-btn");
            profileBtn.setOnAction(e -> navigateTo("/profile"));

            Button logoutBtn = new Button("Déconnexion");
            logoutBtn.getStyleClass().addAll("btn-secondary");
            logoutBtn.setOnAction(e -> logout());

            authContainer.getChildren().addAll(profileBtn, logoutBtn);
        } else {
            Button loginBtn = new Button("Connexion");
            loginBtn.getStyleClass().addAll("btn-secondary");
            loginBtn.setOnAction(e -> navigateTo("/login"));

            Button registerBtn = new Button("S'inscrire");
            registerBtn.getStyleClass().addAll("btn-primary");
            registerBtn.setOnAction(e -> navigateTo("/register"));

            authContainer.getChildren().addAll(loginBtn, registerBtn);
        }
    }

    private void setupAdminSection() {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            if (adminSection != null) {
                adminSection.setVisible(true);
                adminSection.setManaged(true);
            }
        }
    }

    private void loadDefis() {
        Task<List<Defi>> task = new Task<>() {
            @Override
            protected List<Defi> call() throws SQLException {
                String search = searchField.getText();
                return defiService.searchDefis(search, "recent");
            }
        };
        task.setOnSucceeded(e -> {
            defis.setAll(task.getValue());
            updateGrids();
        });
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            showAlert("Erreur", "Impossible de charger les défis: " + (ex != null ? ex.getMessage() : "Erreur inconnue"));
        });
        new Thread(task).start();
    }

    private void updateGrids() {
        List<Defi> activeDefis = defis.stream()
                .filter(d -> "OUVERT".equals(d.getStatut()))
                .collect(Collectors.toList());

        List<Defi> pastDefis = defis.stream()
                .filter(d -> !"OUVERT".equals(d.getStatut()))
                .collect(Collectors.toList());

        updateActiveGrid(activeDefis);
        updatePastGrid(pastDefis);

        if (activeDefis.isEmpty() && pastDefis.isEmpty()) {
            emptyMessage.setVisible(true);
            emptyMessage.setManaged(true);
        } else {
            emptyMessage.setVisible(false);
            emptyMessage.setManaged(false);
        }
    }

    private void updateActiveGrid(List<Defi> defisList) {
        activeDefisGrid.getChildren().clear();
        for (Defi defi : defisList) {
            activeDefisGrid.getChildren().add(createDefiCard(defi, true));
        }
    }

    private void updatePastGrid(List<Defi> defisList) {
        pastDefisGrid.getChildren().clear();
        for (Defi defi : defisList) {
            pastDefisGrid.getChildren().add(createDefiCard(defi, false));
        }
    }

    private VBox createDefiCard(Defi defi, boolean isActive) {
        VBox card = new VBox(0);
        card.getStyleClass().add("defi-card");
        card.setPrefWidth(350);
        if (!isActive) {
            card.setOpacity(0.8);
        }
        card.setOnMouseClicked(e -> {
            if (isActive) {
                navigateTo("/challenges/participer/" + defi.getId());
            }
        });

        // Image
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("card-image");

        if (defi.getImageCover() != null && !defi.getImageCover().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image("file:" + defi.getImageCover()));
                imageView.setFitWidth(350);
                imageView.setFitHeight(140);
                imageView.setPreserveRatio(false);
                imageContainer.getChildren().add(imageView);
            } catch (Exception ex) {
                Label fallback = new Label(isActive ? "🎨" : "🏆");
                fallback.setStyle("-fx-font-size: 48px;");
                imageContainer.getChildren().add(fallback);
            }
        } else {
            Label fallback = new Label(isActive ? "🎨" : "🏆");
            fallback.setStyle("-fx-font-size: 48px;");
            imageContainer.getChildren().add(fallback);
        }

        // Contenu
        VBox content = new VBox(8);
        content.getStyleClass().add("card-body");

        Label titre = new Label(defi.getTitre());
        titre.getStyleClass().add("card-title");

        Text description = new Text(defi.getDescription());
        description.getStyleClass().add("card-text");
        description.wrappingWidthProperty().bind(card.widthProperty().subtract(30));
        if (description.getText().length() > 150) {
            description.setText(description.getText().substring(0, 150) + "...");
        }

        // Tags
        HBox tags = new HBox(5);
        Label themeTag = new Label(defi.getTheme());
        themeTag.getStyleClass().add("tag");
        Label statusTag = new Label(isActive ? "Actif" : "Terminé");
        statusTag.getStyleClass().add("tag");
        tags.getChildren().addAll(themeTag, statusTag);

        // Dates
        Label dates = new Label();
        if (defi.getDateDebut() != null && defi.getDateFin() != null) {
            dates.setText("📅 " + defi.getDateDebut().format(dateFormatter) + " → " + defi.getDateFin().format(dateFormatter));
            dates.setStyle("-fx-text-fill: #18E3A4; -fx-font-weight: 600; -fx-font-size: 12px;");
        }

        // Bouton participer (uniquement pour les défis actifs)
        if (isActive) {
            Button participerBtn = new Button("Participer");
            participerBtn.getStyleClass().addAll("btn-primary", "btn-sm", "magic-btn");
            participerBtn.setMaxWidth(Double.MAX_VALUE);
            participerBtn.setOnAction(e -> {
                e.consume();
                navigateTo("/challenges/participer/" + defi.getId());
            });
            content.getChildren().addAll(titre, description, tags, dates, participerBtn);
        } else {
            Label voirResultats = new Label("👁️ Voir les résultats");
            voirResultats.setStyle("-fx-text-fill: #B0B9B6; -fx-font-weight: 600; -fx-font-size: 12px; -fx-cursor: hand;");
            content.getChildren().addAll(titre, description, tags, dates, voirResultats);
        }

        card.getChildren().addAll(imageContainer, content);
        return card;
    }

    @FXML
    private void search() {
        loadDefis();
    }

    @FXML
    private void createDefi() {
        if (adminTitreField.getText().isEmpty()) {
            showAlert("Erreur", "Le titre est requis");
            return;
        }

        Defi defi = new Defi();
        defi.setTitre(adminTitreField.getText());
        defi.setTheme(adminThemeField.getText());
        defi.setDateDebut(adminDateDebutPicker.getValue());
        defi.setDateFin(adminDateFinPicker.getValue());
        defi.setStatut("OUVERT");
        if (UserSession.isLoggedIn()) {
            defi.setCreateurId(UserSession.getCurrentUser().getId());
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                defiService.add(defi);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Défi créé !");
            adminTitreField.clear();
            adminThemeField.clear();
            adminDateDebutPicker.setValue(null);
            adminDateFinPicker.setValue(null);
            loadDefis();
        });
        task.setOnFailed(e -> showAlert("Erreur", "Erreur lors de la création"));
        new Thread(task).start();
    }

    private void logout() {
        UserSession.logout();
        navigateTo("/");
    }
}