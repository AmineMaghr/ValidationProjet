package com.example.app.controllers;

import com.example.app.entities.Defi;
import com.example.app.entities.Oeuvre;
import com.example.app.entities.Universe;
import com.example.app.entities.User;
import com.example.app.dao.DefiDAO;
import com.example.app.services.OeuvreService;
import com.example.app.services.UniverseService;
import com.example.app.services.UserService;
import com.example.app.utils.SceneManager;
import com.example.app.utils.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.example.app.services.DefiService;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AccueilController extends BaseController {

    // FXML Injections
    @FXML private MediaView mediaView;
    @FXML private HBox authContainer;
    @FXML private VBox searchBarContainer;
    @FXML private TextField searchInput;
    @FXML private ListView<String> searchResults;
    @FXML private HBox universSlider;
    @FXML private HBox creationsSlider;
    @FXML private TilePane defisGrid;
    @FXML private StackPane defiModal;
    @FXML private StackPane faceModal;
    @FXML private StackPane globalSuccessBanner;
    @FXML private Label modalTitre, modalTheme, modalDescription, modalDebut, modalFin, modalStatut;

    // Services
    private UniverseService universService = new UniverseService();
    private OeuvreService oeuvreService = new OeuvreService();
    private DefiService defiService = new DefiService();
    private UserService userService = new UserService();

    // Sliders
    private int universIndex = 0;
    private int creationsIndex = 0;
    private ObservableList<Universe> universList = FXCollections.observableArrayList();
    private ObservableList<Oeuvre> creationsList = FXCollections.observableArrayList();

    private Timeline searchTimeline;
    private MediaPlayer mediaPlayer;
    private Defi currentDefi;

    public static boolean showSuccessFromPreferences = false;

    @FXML
    public void initialize() {
        setupVideoBackground();
        setupAuthButtons();
        loadUniversPopulaires();
        loadCreationsRecentes();
        loadDefis();
        setupSearchBar();

        if (showSuccessFromPreferences) {
            showSuccessBanner();
            showSuccessFromPreferences = false; // Reset
        }
    }

    private void showSuccessBanner() {
        if (globalSuccessBanner != null) {
            globalSuccessBanner.setVisible(true);
            globalSuccessBanner.setManaged(true);
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            delay.setOnFinished(e -> {
                globalSuccessBanner.setVisible(false);
                globalSuccessBanner.setManaged(false);
            });
            delay.play();
        }
    }

    private void setupVideoBackground() {
        try {
            String path = getClass().getResource("/com/monapp/videos/dragon.mp4").toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);
        } catch (Exception e) {
            System.out.println("Vidéo non trouvée, fallback sur fond sombre");
        }
    }

    private void setupAuthButtons() {
        authContainer.getChildren().clear();
        if (UserSession.isLoggedIn()) {
            User user = UserSession.getCurrentUser();
            HBox profileBox = new HBox(8);
            profileBox.setAlignment(javafx.geometry.Pos.CENTER);

            ImageView avatar = new ImageView();
            avatar.setFitHeight(40);
            avatar.setFitWidth(40);
            avatar.setStyle("-fx-border-radius: 50%; -fx-border-color: #18E3A4; -fx-border-width: 2;");
            try {
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    avatar.setImage(new javafx.scene.image.Image("file:uploads/avatars/" + user.getAvatar()));
                } else {
                    java.net.URL url = getClass().getResource("/com/monapp/images/default-avatar.png");
                    if (url != null) {
                        avatar.setImage(new javafx.scene.image.Image(url.toExternalForm()));
                    }
                }
            } catch (Exception e) {
                java.net.URL url = getClass().getResource("/com/monapp/images/default-avatar.png");
                if (url != null) {
                    avatar.setImage(new javafx.scene.image.Image(url.toExternalForm()));
                }
            }

            javafx.scene.control.Button profileBtn = new javafx.scene.control.Button(user.getUsername(), avatar);
            profileBtn.getStyleClass().addAll("magic-btn");
            profileBtn.setOnAction(e -> navigateTo("/profile"));

            Button logoutBtn = new Button("Déconnexion");
            logoutBtn.getStyleClass().addAll("btn-secondary");
            logoutBtn.setOnAction(e -> logout());

            authContainer.getChildren().addAll(profileBtn, logoutBtn);
        } else {
            Button loginBtn = new Button("Connexion");
            loginBtn.getStyleClass().addAll("btn-secondary", "btn-glow");
            loginBtn.setOnAction(e -> navigateTo("/login"));

            Button registerBtn = new Button("S'inscrire");
            registerBtn.getStyleClass().addAll("btn-primary", "btn-glow");
            registerBtn.setOnAction(e -> navigateTo("/register"));

            authContainer.getChildren().addAll(loginBtn, registerBtn);
        }
    }

    private void loadUniversPopulaires() {
        Task<List<Universe>> task = new Task<>() {
            @Override
            protected List<Universe> call() {
                try {
                    return universService.getUniversPopulaires();
                } catch (SQLException e) {
                    System.err.println("Erreur chargement univers: " + e.getMessage());
                    return new ArrayList<>();
                }
            }
        };
        task.setOnSucceeded(e -> {
            universList.setAll(task.getValue());
            updateUniversSlider();
        });
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private void updateUniversSlider() {
        universSlider.getChildren().clear();
        int start = universIndex;
        int end = Math.min(universIndex + 4, universList.size());
        for (int i = start; i < end; i++) {
            universSlider.getChildren().add(createUniversCard(universList.get(i)));
        }
    }

    private VBox createUniversCard(Universe univers) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Region imageRegion = new Region();
        imageRegion.getStyleClass().add("card-image");
        if (univers.getBannerBase64() != null) {
            imageRegion.setStyle("-fx-background-image: url('" + univers.getBannerBase64() + "'); -fx-background-size: cover;");
        }

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");

        Label title = new Label(univers.getName());
        title.getStyleClass().add("card-title");

        Text description = new Text(univers.getShortDescription());
        description.getStyleClass().add("card-text");
        description.wrappingWidthProperty().bind(body.widthProperty().subtract(10));

        HBox tags = new HBox(5);
        for (String theme : univers.getThemes()) {
            Label tag = new Label(theme);
            tag.getStyleClass().add("tag");
            tags.getChildren().add(tag);
        }

        body.getChildren().addAll(title, description, tags);
        card.getChildren().addAll(imageRegion, body);
        card.setOnMouseClicked(e -> navigateTo("/univers/" + univers.getId()));
        return card;
    }

    @FXML private void prevUnivers() {
        if (universIndex > 0) {
            universIndex = Math.max(0, universIndex - 1);
            updateUniversSlider();
        }
    }

    @FXML private void nextUnivers() {
        if (universIndex + 4 < universList.size()) {
            universIndex++;
            updateUniversSlider();
        }
    }

    private void loadCreationsRecentes() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() {
                try {
                    return oeuvreService.getCreationsRecentes();
                } catch (SQLException e) {
                    System.err.println("Erreur chargement créations: " + e.getMessage());
                    return new ArrayList<>();
                }
            }
        };
        task.setOnSucceeded(e -> {
            creationsList.setAll(task.getValue());
            updateCreationsSlider();
        });
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private void updateCreationsSlider() {
        creationsSlider.getChildren().clear();
        int start = creationsIndex;
        int end = Math.min(creationsIndex + 4, creationsList.size());
        for (int i = start; i < end; i++) {
            creationsSlider.getChildren().add(createCreationCard(creationsList.get(i)));
        }
    }

    private VBox createCreationCard(Oeuvre oeuvre) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Region imageRegion = new Region();
        imageRegion.getStyleClass().add("card-image");
        if (oeuvre.getImageUrl() != null) {
            imageRegion.setStyle("-fx-background-image: url('" + oeuvre.getImageUrl() + "'); -fx-background-size: cover;");
        }

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");

        Label title = new Label(oeuvre.getTitle());
        title.getStyleClass().add("card-title");

        Text description = new Text(oeuvre.getDescription());
        description.getStyleClass().add("card-text");
        description.wrappingWidthProperty().bind(body.widthProperty().subtract(10));

        Label author = new Label("Par " + oeuvre.getAuthor());
        author.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px;");

        Label type = new Label(oeuvre.getType());
        type.getStyleClass().add("tag");

        body.getChildren().addAll(title, description, author, type);
        card.getChildren().addAll(imageRegion, body);
        card.setOnMouseClicked(e -> navigateTo("/oeuvre/" + oeuvre.getId()));
        return card;
    }

    @FXML private void prevCreation() {
        if (creationsIndex > 0) {
            creationsIndex--;
            updateCreationsSlider();
        }
    }

    @FXML private void nextCreation() {
        if (creationsIndex + 4 < creationsList.size()) {
            creationsIndex++;
            updateCreationsSlider();
        }
    }

    private void loadDefis() {
        Task<List<Defi>> task = new Task<>() {
            @Override
            protected List<Defi> call() {
                try {
                    return defiService.getAllDefis();
                } catch (SQLException e) {
                    System.err.println("Erreur chargement défis: " + e.getMessage());
                    return new ArrayList<>();
                }
            }
        };
        task.setOnSucceeded(e -> {
            defisGrid.getChildren().clear();
            for (Defi defi : task.getValue()) {
                defisGrid.getChildren().add(createDefiCard(defi));
            }
        });
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private VBox createDefiCard(Defi defi) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("card", "defi-card");

        Region imageRegion = new Region();
        imageRegion.getStyleClass().add("card-image");
       /* if (defi.getImageCover() != null) {
            imageRegion.setStyle("-fx-background-image: url('file:uploads/defis/" + defi.getImageCover() + "'); -fx-background-size: cover;");
        }*/

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");

        Label title = new Label(defi.getTitre());
        title.getStyleClass().add("card-title");

        Label theme = new Label("Thème : " + defi.getTheme());
        theme.setStyle("-fx-text-fill: #B0B9B6; -fx-font-size: 12px;");

        Label dateFin = new Label("Fin : " + (defi.getDateFin() != null ? defi.getDateFin() : "Non spécifiée"));
        dateFin.setStyle("-fx-text-fill: #B0B9B6; -fx-font-size: 11px;");

        Button voirBtn = new Button("Voir le défi");
        voirBtn.getStyleClass().addAll("btn-primary", "btn-sm", "magic-btn");
        voirBtn.setOnAction(e -> showDefiModal(defi));

        body.getChildren().addAll(title, theme, dateFin, voirBtn);
        card.getChildren().addAll(imageRegion, body);
        return card;
    }

    private void showDefiModal(Defi defi) {
        currentDefi = defi;
        modalTitre.setText(defi.getTitre());
        modalTheme.setText(defi.getTheme());
        modalDescription.setText(defi.getDescription());
        modalDebut.setText(defi.getDateDebut() != null ? defi.getDateDebut().toString() : "Non spécifiée");
        modalFin.setText(defi.getDateFin() != null ? defi.getDateFin().toString() : "Non spécifiée");
        modalStatut.setText(defi.getStatut());
        defiModal.setVisible(true);
        defiModal.setManaged(true);
    }

    @FXML private void closeDefiModal() {
        defiModal.setVisible(false);
        defiModal.setManaged(false);
    }

    @FXML private void participerDefi() {
        if (currentDefi != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Participation");
            alert.setHeaderText(null);
            alert.setContentText("Participation au défi \"" + currentDefi.getTitre() + "\" enregistrée !");
            alert.showAndWait();
            closeDefiModal();
        }
    }

    private void setupSearchBar() {
        searchInput.textProperty().addListener((obs, old, newVal) -> {
            if (searchTimeline != null) searchTimeline.stop();
            searchTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> performSearch(newVal)));
            searchTimeline.play();
        });
    }

    private void performSearch(String query) {
        if (query == null || query.length() < 2) {
            searchResults.setVisible(false);
            return;
        }
        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() {
                try {
                    return userService.searchUsers(query);
                } catch (SQLException e) {
                    System.err.println("Erreur recherche: " + e.getMessage());
                    return new ArrayList<>();
                }
            }
        };
        task.setOnSucceeded(e -> {
            List<User> users = task.getValue();
            if (users.isEmpty()) {
                searchResults.setVisible(false);
            } else {
                searchResults.getItems().clear();
                for (User user : users) {
                    searchResults.getItems().add(user.getUsername() + " - " + user.getPrenom() + " " + user.getNom());
                }
                searchResults.setVisible(true);
            }
        });
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML private void toggleSearchBar() {
        boolean isActive = searchBarContainer.isVisible();
        searchBarContainer.setVisible(!isActive);
        if (!isActive) {
            searchInput.requestFocus();
        } else {
            searchResults.setVisible(false);
            searchInput.clear();
        }
    }

    // Navigation
    @FXML public void goAccueil() { navigateTo("/"); }
    @FXML public void goDiscover() { navigateTo("/discover"); }
    @FXML public void goUniverses() { navigateTo("/universes"); }
    @FXML public void goPersonnages() { navigateTo("/personnages"); }
    @FXML public void goOeuvres() { navigateTo("/oeuvre"); }
    @FXML public void goArtefacts() { navigateTo("/artefact"); }
    @FXML public void goShop() { navigateTo("/shop"); }
    @FXML public void goChallenges() { navigateTo("/challenges"); }

    @FXML
    public void lancerQuiz(javafx.event.ActionEvent event) {
        navigateTo("/quiz");
    }

    @Override protected void navigateTo(String path) {
        SceneManager.getInstance().loadScene(path);
    }

    private void logout() {
        UserSession.logout();
        navigateTo("/");
    }
}
