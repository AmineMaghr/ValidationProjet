package com.example.app.controllers;

import com.example.app.entities.Defi;
import com.example.app.entities.Oeuvre;
import com.example.app.entities.Universe;
import com.example.app.entities.User;
import com.example.app.services.DefiService;
import com.example.app.services.OeuvreService;
import com.example.app.services.UniverseService;
import com.example.app.services.UserService;
import com.example.app.utils.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AccueilController extends BaseController {

    public static boolean showSuccessFromPreferences;
    @FXML private MediaView mediaView;
    @FXML private HBox authContainer;
    @FXML private VBox searchBarContainer;
    @FXML private TextField searchInput;
    @FXML private ListView<String> searchResults;
    @FXML private HBox universSlider;
    @FXML private HBox creationsSlider;
    @FXML private TilePane defisGrid;
    @FXML private StackPane defiModal;
    @FXML private Label modalTitre;
    @FXML private Label modalTheme;
    @FXML private Label modalDescription;
    @FXML private Label modalDebut;
    @FXML private Label modalFin;
    @FXML private Label modalStatut;

    private UniverseService universService = new UniverseService();
    private OeuvreService oeuvreService = new OeuvreService();
    private DefiService defiService = new DefiService();
    private UserService userService = new UserService();

    private int universIndex = 0;
    private int creationsIndex = 0;
    private ObservableList<Universe> universList = FXCollections.observableArrayList();
    private ObservableList<Oeuvre> creationsList = FXCollections.observableArrayList();
    private List<Defi> defisList = new ArrayList<>();

    private Timeline searchTimeline;
    private MediaPlayer mediaPlayer;
    private Defi currentDefi;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupVideoBackground();
        setupAuthButtons();
        loadUniversPopulaires();
        loadCreationsRecentes();
        loadDefis();
        setupSearchBar();

        System.out.println("=== ACCUEIL INITIALISÉ ===");
        System.out.println("UserSession.isLoggedIn(): " + UserSession.isLoggedIn());
        if (UserSession.isLoggedIn()) {
            User user = UserSession.getCurrentUser();
            System.out.println("Utilisateur connecté: " + user.getUsername() + " (ID: " + user.getId() + ")");
        } else {
            System.out.println("Aucun utilisateur connecté");
        }
    }

    private void setupVideoBackground() {
    try {
        // Essayer différents chemins possibles
        String videoPath = null;
        
        // Chemin 1: resources/videos/dragon.mp4
        if (getClass().getResource("/videos/dragon.mp4") != null) {
            videoPath = getClass().getResource("/videos/dragon.mp4").toExternalForm();
            System.out.println("✅ Vidéo trouvée dans resources/videos/");
        }
        // Chemin 2: resources/com/monapp/view/videos/dragon.mp4
        else if (getClass().getResource("/com/monapp/view/videos/dragon.mp4") != null) {
            videoPath = getClass().getResource("/com/monapp/view/videos/dragon.mp4").toExternalForm();
            System.out.println("✅ Vidéo trouvée dans com/monapp/view/videos/");
        }
        // Chemin 3: fichier local dans uploads
        else {
            File videoFile = new File("uploads/dragon.mp4");
            if (videoFile.exists()) {
                videoPath = videoFile.toURI().toString();
                System.out.println("✅ Vidéo trouvée dans uploads/");
            } else {
                // Chemin 4: dossier partagé
                videoFile = new File("C:/midgar_shared/uploads/dragon.mp4");
                if (videoFile.exists()) {
                    videoPath = videoFile.toURI().toString();
                    System.out.println("✅ Vidéo trouvée dans midgar_shared/uploads/");
                }
            }
        }
        
        if (videoPath != null) {
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);

            // Make video full screen
            mediaView.fitWidthProperty().bind(((AnchorPane) mediaView.getParent()).widthProperty());
            mediaView.fitHeightProperty().bind(((AnchorPane) mediaView.getParent()).heightProperty());
            mediaView.setPreserveRatio(false);

            System.out.println("✅ Vidéo chargée avec succès!");
        } else {
            System.out.println("❌ Vidéo non trouvée, fallback sur fond sombre");
            mediaView.setVisible(false);
        }
        
    } catch (Exception e) {
        System.err.println("Erreur chargement vidéo: " + e.getMessage());
        mediaView.setVisible(false);
    }
}
    private void setupAuthButtons() {
        authContainer.getChildren().clear();

        if (UserSession.isLoggedIn()) {
            User user = UserSession.getCurrentUser();

            Button searchToggle = new Button("🔍");
            searchToggle.getStyleClass().addAll("magic-btn", "search-toggle");
            searchToggle.setOnAction(e -> toggleSearchBar());

            HBox profileBox = new HBox(8);
            profileBox.setAlignment(Pos.CENTER);

            ImageView avatar = new ImageView();
            avatar.setFitWidth(32);
            avatar.setFitHeight(32);
            avatar.setStyle("-fx-border-radius: 50%; -fx-border-color: #18E3A4; -fx-border-width: 2;");
            try {
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    File imgFile = new File("uploads/avatars/" + user.getAvatar());
                    if (imgFile.exists()) {
                        avatar.setImage(new Image(imgFile.toURI().toString()));
                    }
                }
            } catch (Exception e) {
                // No image loaded
            }

            Button profileBtn = new Button(user.getUsername());
            profileBtn.setGraphic(avatar);
            profileBtn.getStyleClass().addAll("magic-btn", "profile-link");
            profileBtn.setOnAction(e -> navigateTo("/profile"));

            Button logoutBtn = new Button("Déconnexion");
            logoutBtn.getStyleClass().addAll("btn", "btn-secondary");
            logoutBtn.setOnAction(e -> logout());

            authContainer.getChildren().addAll(searchToggle, profileBtn, logoutBtn);
        } else {
            Button loginBtn = new Button("Connexion");
            loginBtn.getStyleClass().addAll("btn", "btn-secondary", "btn-glow");
            loginBtn.setOnAction(e -> navigateTo("/login"));

            Button registerBtn = new Button("S'inscrire");
            registerBtn.getStyleClass().addAll("btn", "btn-primary", "btn-glow");
            registerBtn.setOnAction(e -> navigateTo("/register"));

            authContainer.getChildren().addAll(loginBtn, registerBtn);
        }
    }

    public void toggleSearchBar() {
        boolean isVisible = searchBarContainer.isVisible();
        searchBarContainer.setVisible(!isVisible);
        searchBarContainer.setManaged(!isVisible);
        if (!isVisible) {
            searchInput.requestFocus();
        } else {
            searchResults.setVisible(false);
            searchInput.clear();
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
            protected List<User> call() throws Exception {
                return userService.searchUsers(query);
            }
        };
        task.setOnSucceeded(e -> {
            List<User> users = task.getValue();
            if (users == null || users.isEmpty()) {
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

    private void loadUniversPopulaires() {
        Task<List<Universe>> task = new Task<>() {
            @Override
            protected List<Universe> call() throws Exception {
                return universService.getUniversPopulaires();
            }
        };
        task.setOnSucceeded(e -> {
            universList.setAll(task.getValue());
            updateUniversSlider();
        });
        task.setOnFailed(e -> e.getSource().getException().printStackTrace());
        new Thread(task).start();
    }

    private void updateUniversSlider() {
        Platform.runLater(() -> {
            universSlider.getChildren().clear();
            int start = universIndex;
            int end = Math.min(start + 3, universList.size());
            for (int i = start; i < end; i++) {
                universSlider.getChildren().add(createUniversCard(universList.get(i)));
            }
        });
    }

    private VBox createUniversCard(Universe univers) {
        VBox card = new VBox(5);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setOnMouseClicked(e -> navigateTo("/universe/" + univers.getId()));

        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("card-image");
        imageContainer.setPrefHeight(160);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(false);

        if (univers.getBannerBase64() != null && !univers.getBannerBase64().isEmpty()) {
            try {
                Image img = new Image(univers.getBannerBase64());
                imageView.setImage(img);
            } catch (Exception ex) {
                setDefaultImagePlaceholder(imageView, "🌍");
            }
        } else {
            setDefaultImagePlaceholder(imageView, "🌍");
        }

        imageContainer.getChildren().add(imageView);

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");
        body.setPadding(new Insets(10));

        Label title = new Label(univers.getName());
        title.getStyleClass().add("card-title");

        Text description = new Text(univers.getShortDescription());
        description.setWrappingWidth(260);
        description.getStyleClass().add("card-description");

        HBox tags = new HBox(5);
        tags.getStyleClass().add("card-tags");
        if (univers.getThemes() != null && !univers.getThemes().isEmpty()) {
            for (int i = 0; i < Math.min(3, univers.getThemes().size()); i++) {
                Label tag = new Label(univers.getThemes().get(i));
                tag.getStyleClass().add("tag");
                tags.getChildren().add(tag);
            }
        }

        body.getChildren().addAll(title, description, tags);
        card.getChildren().addAll(imageContainer, body);

        return card;
    }

    private void loadCreationsRecentes() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                return oeuvreService.getCreationsRecentes();
            }
        };
        task.setOnSucceeded(e -> {
            creationsList.setAll(task.getValue());
            updateCreationsSlider();
        });
        task.setOnFailed(e -> e.getSource().getException().printStackTrace());
        new Thread(task).start();
    }

    private void updateCreationsSlider() {
        Platform.runLater(() -> {
            creationsSlider.getChildren().clear();
            int start = creationsIndex;
            int end = Math.min(start + 3, creationsList.size());
            for (int i = start; i < end; i++) {
                creationsSlider.getChildren().add(createCreationCard(creationsList.get(i)));
            }
        });
    }

    private VBox createCreationCard(Oeuvre oeuvre) {
        VBox card = new VBox(5);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setOnMouseClicked(e -> navigateTo("/oeuvre/show/" + oeuvre.getId()));

        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("card-image");
        imageContainer.setPrefHeight(160);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(false);

        if (oeuvre.hasImage()) {
            imageView.setImage(oeuvre.getImage());
        } else {
            setDefaultImagePlaceholder(imageView, "📖");
        }

        imageContainer.getChildren().add(imageView);

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");
        body.setPadding(new Insets(10));

        Label title = new Label(oeuvre.getTitle());
        title.getStyleClass().add("card-title");

        Text description = new Text(oeuvre.getDescription());
        description.setWrappingWidth(260);
        description.getStyleClass().add("card-description");

        Label author = new Label("Par " + oeuvre.getAuthor());
        author.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 11px;");

        HBox tags = new HBox(5);
        tags.getStyleClass().add("card-tags");
        Label typeTag = new Label(oeuvre.getType());
        typeTag.getStyleClass().add("tag");
        tags.getChildren().add(typeTag);

        body.getChildren().addAll(title, description, author, tags);
        card.getChildren().addAll(imageContainer, body);

        return card;
    }

    private void loadDefis() {
        Task<List<Defi>> task = new Task<>() {
            @Override
            protected List<Defi> call() throws Exception {
                return defiService.getAllDefis();
            }
        };
        task.setOnSucceeded(e -> {
            defisList = task.getValue();
            Platform.runLater(() -> displayDefis());
        });
        task.setOnFailed(e -> e.getSource().getException().printStackTrace());
        new Thread(task).start();
    }

    private void displayDefis() {
        defisGrid.getChildren().clear();
        for (Defi defi : defisList) {
            defisGrid.getChildren().add(createDefiCard(defi));
        }
    }

    private VBox createDefiCard(Defi defi) {
        VBox card = new VBox(5);
        card.getStyleClass().addAll("card", "defi-card");
        card.setPrefWidth(250);
        card.setOnMouseClicked(e -> showDefiModal(defi));

        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("card-image");
        imageContainer.setPrefHeight(150);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);

        if (defi.getImageCover() != null && !defi.getImageCover().isEmpty()) {
            try {
                File imgFile = new File("uploads/defis/" + defi.getImageCover());
                if (imgFile.exists()) {
                    imageView.setImage(new Image(imgFile.toURI().toString()));
                } else {
                    setDefaultImagePlaceholder(imageView, "🏆");
                }
            } catch (Exception ex) {
                setDefaultImagePlaceholder(imageView, "🏆");
            }
        } else {
            setDefaultImagePlaceholder(imageView, "🏆");
        }

        imageContainer.getChildren().add(imageView);

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");
        body.setPadding(new Insets(10));

        Label title = new Label(defi.getTitre());
        title.getStyleClass().add("card-title");

        Label theme = new Label("Thème : " + defi.getTheme());
        theme.setStyle("-fx-text-fill: #B0B9B6; -fx-font-size: 11px;");

        String endDateStr = defi.getDateFin() != null ? defi.getDateFin().format(DATE_FORMATTER) : "Non spécifiée";
        Label endDate = new Label("Fin : " + endDateStr);
        endDate.setStyle("-fx-text-fill: #B0B9B6; -fx-font-size: 10px;");

        Button viewBtn = new Button("Voir le défi");
        viewBtn.getStyleClass().addAll("btn", "btn-primary", "btn-sm", "view-defi");
        viewBtn.setOnAction(e -> showDefiModal(defi));

        body.getChildren().addAll(title, theme, endDate, viewBtn);
        card.getChildren().addAll(imageContainer, body);

        return card;
    }

    private void setDefaultImagePlaceholder(ImageView imageView, String emoji) {
        Label placeholder = new Label(emoji);
        placeholder.setStyle("-fx-font-size: 48px; -fx-text-fill: #18E3A4;");
        imageView.setImage(null);
    }

    private void showDefiModal(Defi defi) {
        currentDefi = defi;
        modalTitre.setText(defi.getTitre());
        modalTheme.setText(defi.getTheme());
        modalDescription.setText(defi.getDescription());

        String debutStr = defi.getDateDebut() != null ? defi.getDateDebut().format(DATE_FORMATTER) : "Non spécifiée";
        String finStr = defi.getDateFin() != null ? defi.getDateFin().format(DATE_FORMATTER) : "Non spécifiée";

        modalDebut.setText(debutStr);
        modalFin.setText(finStr);
        modalStatut.setText(defi.getStatut());

        defiModal.setVisible(true);
        defiModal.setManaged(true);
    }

    @FXML
    private void closeDefiModal() {
        defiModal.setVisible(false);
        defiModal.setManaged(false);
    }

    @FXML
    private void participerDefi() {
        if (currentDefi != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Participation");
            alert.setHeaderText(null);
            alert.setContentText("Participation au défi \"" + currentDefi.getTitre() + "\" enregistrée !");
            alert.showAndWait();
            closeDefiModal();
        }
    }

    @FXML
    private void prevUnivers() {
        if (universIndex > 0) {
            universIndex--;
            updateUniversSlider();
        }
    }

    @FXML
    private void nextUnivers() {
        if (universIndex + 3 < universList.size()) {
            universIndex++;
            updateUniversSlider();
        }
    }

    @FXML
    private void prevCreation() {
        if (creationsIndex > 0) {
            creationsIndex--;
            updateCreationsSlider();
        }
    }

    @FXML
    private void nextCreation() {
        if (creationsIndex + 3 < creationsList.size()) {
            creationsIndex++;
            updateCreationsSlider();
        }
    }

    @FXML
    public void lancerQuiz() {
        navigateTo("/quiz");
    }

    @FXML
    public void goAccueil() {
        navigateTo("/");
    }

    @FXML
    public void goDiscover() {
        navigateTo("/discover");
    }

    @FXML
    public void goUniverses() {
        navigateTo("/universes");
    }

    @FXML
    public void goPersonnages() {
        navigateTo("/personnages");
    }

    @FXML
    public void goOeuvres() {
        navigateTo("/oeuvre");
    }

    @FXML
    public void goArtefacts() {
        navigateTo("/artefact");
    }

    @FXML
    public void goShop() {
        navigateTo("/shop");
    }

    @FXML
    public void goChallenges() {
        navigateTo("/challenges");
    }

    private void logout() {
        UserSession.logout();
        navigateTo("/");
    }
}