package com.example.app.controllers;

import com.example.app.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.example.app.services.PostDetails;
import com.example.app.services.RankedPost;
import com.example.app.services.PostNotificationService;
import javafx.stage.Stage;

/**
 * Controller for discover.fxml — mirrors the JS logic of the original
 * Midgar "Découvrir" page.
 *
 * Data is supplied via {@link #setItems(List)} (called by the parent
 * application after loading the ranked posts from the server / API).
 */
public class DiscoverController extends BaseController implements Initializable {

    // ── Header ──────────────────────────────────────────────────────────────
    @FXML private Button  btnLogin;
    @FXML private Button  btnRegister;
    @FXML private HBox    profileBox;
    @FXML private ImageView avatarImg;
    @FXML private Label   userNameLabel;

    // ── Filters ─────────────────────────────────────────────────────────────
    @FXML private TextField searchInput;

    @FXML private CheckBox genreHighFantasy;
    @FXML private CheckBox genreDarkFantasy;
    @FXML private CheckBox genreScifi;
    @FXML private CheckBox genreUrban;

    @FXML private CheckBox themeMagie;
    @FXML private CheckBox themeGuerre;
    @FXML private CheckBox themePolitique;
    @FXML private CheckBox themeNature;
    @FXML private CheckBox themeMystere;
    @FXML private CheckBox themeAventure;

    @FXML private ToggleGroup sortGroup;
    @FXML private RadioButton sortNewest;
    @FXML private RadioButton sortPopular;
    @FXML private RadioButton sortTrending;

    // ── Feed ────────────────────────────────────────────────────────────────
    @FXML private FlowPane feedGrid;
    @FXML private Label    resultsCount;
    @FXML private VBox     emptyState;
    @FXML private HBox     loadMoreContainer;

    // ── State ────────────────────────────────────────────────────────────────
    private List<ContentItem> allItems   = new ArrayList<>();
    private List<ContentItem> filtered   = new ArrayList<>();
    private int displayedItems = 12;

    // Placeholder image path (classpath resource)
    private static final String PLACEHOLDER = "/images/dragon-poster.jpg";

    // ─────────────────────────────────────────────────────────────────────────
    //  Initializable
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Automatically set logged in user based on simulation
        if (com.example.app.utils.UserSession.isLoggedIn()) {
            setLoggedInUser(com.example.app.utils.UserSession.getUsername(), null);
        } else {
            setLoggedInUser(null, null);
        }
        // Always load existing items dynamically if backend service could provide it.. but for now
        // Simulate reading items depending on user tags
        loadItemsBasedOnUserTags();
    }

    private void loadItemsBasedOnUserTags() {
        int userId = com.example.app.utils.UserSession.getCurrentUserId();
        String userTags = "";
        try {
            java.sql.Connection conn = com.example.app.utils.MyDatabase.getConnection();
            String sql = "SELECT tags FROM user_preferences WHERE user_id = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userTags = rs.getString("tags");
                    }
                }
            } catch (Exception ignored) { }
            
            // Si pas de tags dans user_preferences, on cherche dans advanced_preferences
            if (userTags == null || userTags.isEmpty()) {
                String sqlAdv = "SELECT custom_tags FROM advanced_preferences WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAdv)) {
                    ps.setInt(1, userId);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            userTags = rs.getString("custom_tags");
                        }
                    }
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        com.example.app.dao.ContentRepository repository = new com.example.app.dao.ContentRepository();
        List<ContentItem> dbItems = repository.getAllContent();
        
        System.out.println("Tags utilisateur récupérés : " + userTags);
        List<ContentItem> recommendedItems = new ArrayList<>();
        
        if (userTags != null && !userTags.isEmpty()) {
            final String[] finalTagsList = userTags.toLowerCase().split(",");
            for (ContentItem item : dbItems) {
                boolean match = false;
                for (String t : finalTagsList) {
                    if (item.tagText().toLowerCase().contains(t.trim())) {
                        match = true;
                        break;
                    }
                }
                if (match) recommendedItems.add(item);
            }
            if (recommendedItems.isEmpty()) {
                // Return all if no direct match
                recommendedItems.addAll(dbItems);
                if(searchInput != null) {
                    searchInput.setPromptText("Aucun post tagué " + userTags + "...");
                }
            } else if (searchInput != null) {
                searchInput.setPromptText("Recommandé pour vous : " + userTags);
            }
        } else {
            recommendedItems.addAll(dbItems);
        }

        setItems(recommendedItems);
    }


    // ─────────────────────────────────────────────────────────────────────────
    //  Public API called by the Application layer
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Provide the ranked content items (from server / repository).
     * Call this after {@code FXMLLoader.load()}.
     */
    public void setItems(List<ContentItem> items) {
        this.allItems  = new ArrayList<>(items);
        this.filtered  = new ArrayList<>(items);
        this.displayedItems = 12;
        renderFeed();
    }

    /**
     * Show a logged-in user in the header.
     * Pass {@code null} to return to the login/register buttons.
     */
    public void setLoggedInUser(String username, String avatarUrl) {
        boolean loggedIn = username != null;

        btnLogin.setVisible(!loggedIn);
        btnLogin.setManaged(!loggedIn);
        btnRegister.setVisible(!loggedIn);
        btnRegister.setManaged(!loggedIn);

        profileBox.setVisible(loggedIn);
        profileBox.setManaged(loggedIn);

        if (loggedIn) {
            userNameLabel.setText(username);
            if (avatarUrl != null && !avatarUrl.isBlank()) {
                avatarImg.setImage(new Image(avatarUrl, true));
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  FXML handlers – Navigation
    // ─────────────────────────────────────────────────────────────────────────
    @FXML private void onLogout()          { navigateTo("logout"); }
    @FXML private void onEditPrefs()       { navigateTo("preferences/advanced"); }

    @FXML
    private void handleCreateOeuvre() {
        try {
            System.out.println("[DEBUG] 1. Button 'Créer Œuvre' CLICKED - handleCreateOeuvre() triggered");

            java.sql.Connection conn = com.example.app.utils.MyDatabase.getConnection();
            
            com.example.app.services.EmailService emailService = new com.example.app.services.EmailService();

            System.out.println("[DEBUG] 2. Calling notifyContentCreated...");
            new PostNotificationService(conn, emailService).notifyContentCreated(
                    "Œuvre",
                    "Test Post",
                    "#dark"
            );

        } catch (Exception e) {
            System.err.println("[DEBUG] ERROR in handleCreateOeuvre()");
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  FXML handlers – Feed controls
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void onSearch() {
        applyFilters();
    }

    @FXML
    private void onFilterChanged() {
        applyFilters();
    }

    @FXML
    private void onResetFilters() {
        clearAllFilters();
        filtered = new ArrayList<>(allItems);
        displayedItems = 12;
        renderFeed();
    }

    @FXML
    private void onResetPrefs() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirmer la réinitialisation des préférences ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                clearAllFilters();
                filtered = new ArrayList<>(allItems);
                displayedItems = 12;
                renderFeed();
                new Alert(Alert.AlertType.INFORMATION, "Préférences réinitialisées.")
                        .showAndWait();
            }
        });
    }

    @FXML
    private void onLoadMore() {
        displayedItems += 12;
        renderFeed();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Filter logic
    // ─────────────────────────────────────────────────────────────────────────
    private void applyFilters() {
        Set<String> genres = checkedValues(
                genreHighFantasy, genreDarkFantasy, genreScifi, genreUrban);
        Set<String> themes = checkedValues(
                themeMagie, themeGuerre, themePolitique,
                themeNature, themeMystere, themeAventure);
        String search = searchInput.getText().toLowerCase().trim();

        filtered = allItems.stream()
                .filter(item -> genres.isEmpty() ||
                        item.genres().stream().anyMatch(genres::contains))
                .filter(item -> themes.isEmpty() ||
                        item.themes().stream().anyMatch(themes::contains))
                .filter(item -> search.isEmpty() ||
                        item.name().toLowerCase().contains(search))
                .collect(Collectors.toList());

        // Sort
        RadioButton selected = (RadioButton) sortGroup.getSelectedToggle();
        String sortVal = selected != null ? selected.getId() : "sortNewest";
        switch (sortVal) {
            case "sortPopular"  -> filtered.sort(
                    Comparator.comparingInt(ContentItem::likes).reversed());
            case "sortTrending" -> filtered.sort(
                    Comparator.comparingInt(ContentItem::likes).reversed());
            default             -> filtered.sort(
                    Comparator.comparingLong(ContentItem::date).reversed());
        }

        displayedItems = 12;
        renderFeed();
    }

    private Set<String> checkedValues(CheckBox... boxes) {
        Set<String> result = new HashSet<>();
        for (CheckBox cb : boxes) {
            if (cb.isSelected()) result.add(cb.getId()
                    .replaceFirst("^genre", "")
                    .replaceFirst("^theme", "")
                    .toLowerCase());
        }
        return result;
    }

    private void clearAllFilters() {
        for (CheckBox cb : Arrays.asList(
                genreHighFantasy, genreDarkFantasy, genreScifi, genreUrban,
                themeMagie, themeGuerre, themePolitique,
                themeNature, themeMystere, themeAventure)) {
            cb.setSelected(false);
        }
        sortNewest.setSelected(true);
        searchInput.clear();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Render
    // ─────────────────────────────────────────────────────────────────────────
    private void renderFeed() {
        feedGrid.getChildren().clear();

        if (filtered.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            loadMoreContainer.setVisible(false);
            loadMoreContainer.setManaged(false);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);

            int limit = Math.min(displayedItems, filtered.size());
            for (int i = 0; i < limit; i++) {
                feedGrid.getChildren().add(buildCard(filtered.get(i)));
            }

            boolean hasMore = filtered.size() > displayedItems;
            loadMoreContainer.setVisible(hasMore);
            loadMoreContainer.setManaged(hasMore);
        }

        int shown = Math.min(displayedItems, filtered.size());
        resultsCount.setText(shown + " / " + filtered.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Card builder
    // ─────────────────────────────────────────────────────────────────────────
    private VBox buildCard(ContentItem item) {
        VBox card = new VBox(0);
        card.getStyleClass().add("universe-card");
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        // Banner
        StackPane banner = buildBanner(item);

        // Content area
        VBox content = new VBox(8);
        content.setPadding(new Insets(16));
        content.getStyleClass().add("card-content");

        Label typeLabel = new Label(typeLabel(item.type()));
        typeLabel.getStyleClass().add("card-type-label");

        Label titleLabel = new Label(item.name());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        // Genre tags
        HBox tagsBox = new HBox(6);
        for (String g : item.genres()) {
            Label tag = new Label(g);
            tag.getStyleClass().add("genre-tag");
            tagsBox.getChildren().add(tag);
        }

        Label desc = new Label(item.description());
        desc.getStyleClass().add("card-description");
        desc.setWrapText(true);
        VBox.setVgrow(desc, Priority.ALWAYS);

        Separator sep = new Separator();
        sep.getStyleClass().add("card-separator");

        // Footer
        HBox footer = new HBox();
        footer.setSpacing(8);

        HBox stats = buildStats(item);
        HBox.setHgrow(stats, Priority.ALWAYS);

        HBox creator = new HBox(6);
        Label avatarEmoji = new Label(item.avatarEmoji());
        Label creatorName = new Label(item.creator());
        creatorName.getStyleClass().add("creator-label");
        creator.getChildren().addAll(avatarEmoji, creatorName);

        footer.getChildren().addAll(stats, creator);

        content.getChildren().addAll(typeLabel, titleLabel, tagsBox, desc, sep, footer);
        card.getChildren().addAll(banner, content);

        // Why button (overlay) – shown via StackPane wrapper
        StackPane wrapper = new StackPane(card);
        Button whyBtn = new Button("?");
        whyBtn.getStyleClass().add("why-btn");
        StackPane.setAlignment(whyBtn, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(whyBtn, new Insets(8, 8, 0, 0));
        
        // Prevent click from bubbling to the card wrapper
        whyBtn.setOnMouseClicked(javafx.event.Event::consume);
        
        whyBtn.setOnAction(e -> {
            e.consume();
            Stage stage = (Stage) whyBtn.getScene().getWindow();

            // Reconstruct a RankedPost from ContentItem properties 
            // In a real scenario, the ranked breakdown comes from a recommendation service backend 
            Map<String, Double> scores = new HashMap<>();
            scores.put("tag_similarity", 0.85);
            scores.put("engagement_score", 0.70);
            scores.put("recency_decay", 0.60);
            scores.put("behavior_similarity", 0.45);

            RankedPost post = new RankedPost(
                    item.type(), 
                    (int) item.id(), 
                    item.tagText(), 
                    item.likes() / 100.0, 
                    scores
            );

            PostDetails details = new PostDetails(
                post.getType(),
                item.name(), // use real name instead of generic ID "Post #id"
                item.description()
            );

            new Thread(() -> {
                try {
                    java.sql.Connection conn = com.example.app.utils.MyDatabase.getConnection();
                    com.example.app.services.WhyService whyService = new com.example.app.services.WhyService(conn);
                    int userId = com.example.app.utils.UserSession.getCurrentUserId();
                    if (userId <= 0) userId = 1; // Fallback to simulated user 1
                    
                    com.example.app.services.WhyResult whyResult = whyService.explain(post, userId);

                    // Extract what user tags we have conceptually in this simplistic layer:
                    List<String> prefTags = Arrays.asList(post.getTag()); 
                    List<String> behaviorTags = new ArrayList<>();

                    javafx.application.Platform.runLater(() -> {
                        WhyController.navigateTo(
                            stage,
                            post,
                            details,
                            whyResult,
                            prefTags,
                            behaviorTags
                        );
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        WhyController.navigateTo(
                            stage,
                            post,
                            details,
                            new com.example.app.services.WhyResult("Une erreur locale est survenue : " + ex.getMessage(), new HashMap<>()),
                            Arrays.asList(post.getTag()),
                            new ArrayList<>()
                        );
                    });
                }
            }).start();
        });
        wrapper.getChildren().add(whyBtn);

        // Click anywhere on card → detail page
        wrapper.setOnMouseClicked(e -> navigateTo(detailPath(item)));
        wrapper.setCursor(javafx.scene.Cursor.HAND);

        // We need to return a VBox but StackPane is our root – wrap it:
        VBox outer = new VBox(wrapper);
        return outer;
    }

    private StackPane buildBanner(ContentItem item) {
        StackPane banner = new StackPane();
        banner.getStyleClass().add("card-banner");
        banner.setMinHeight(160);
        banner.setMaxHeight(160);

        if (item.imagePath() != null && !item.imagePath().isBlank()) {
            try {
                ImageView iv = new ImageView(new Image(item.imagePath(), 280, 160, true, true, true));
                iv.setFitWidth(280);
                iv.setFitHeight(160);
                iv.setPreserveRatio(false);
                banner.getChildren().add(iv);
                return banner;
            } catch (Exception ignored) { /* fall through to emoji */ }
        }

        // Fallback emoji
        Label emoji = new Label(typeBannerEmoji(item.type()));
        emoji.setStyle("-fx-font-size: 48;");
        banner.getChildren().add(emoji);
        return banner;
    }

    private HBox buildStats(ContentItem item) {
        HBox stats = new HBox(12);
        String type = item.type().toLowerCase();
        switch (type) {
            case "personnage" -> stats.getChildren().add(stat("Univers: —"));
            case "oeuvre"     -> stats.getChildren().add(stat("Type: Œuvre"));
            case "artefact"   -> stats.getChildren().add(stat("Type: " + item.tagText()));
            default           -> stats.getChildren().addAll(
                    stat("Personnages " + item.personnages()),
                    stat("Œuvres " + item.oeuvres()),
                    stat("Artefacts " + item.artefacts())
            );
        }
        return stats;
    }

    private Label stat(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("card-stat-label");
        return l;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private String typeLabel(String type) {
        return switch (type.toLowerCase()) {
            case "universe", "univers" -> "UNIVERSE";
            case "personnage"          -> "PERSONNAGE";
            case "oeuvre"              -> "ŒUVRE";
            case "artefact"            -> "ARTEFACT";
            default                    -> "UNIVERSE";
        };
    }

    private String typeBannerEmoji(String type) {
        return switch (type.toLowerCase()) {
            case "personnage" -> "🧍";
            case "oeuvre"     -> "📖";
            case "artefact"   -> "⚔️";
            default           -> "🌌";
        };
    }

    private String detailPath(ContentItem item) {
        String t = item.type().toLowerCase().equals("univers") ? "universe" : item.type().toLowerCase();
        return t + "/" + item.id();
    }


    // ─────────────────────────────────────────────────────────────────────────
    //  Inner data model
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Immutable content item – mirrors the JS {@code mockContent} objects.
     * Build instances from your JSON/server response in your repository layer.
     */
    public record ContentItem(
            long   id,
            String type,        // "universe" | "personnage" | "oeuvre" | "artefact"
            String name,
            String description,
            String tagText,
            List<String> genres,
            List<String> themes,
            long   date,        // epoch millis
            int    likes,
            int    personnages,
            int    oeuvres,
            int    artefacts,
            String avatarEmoji,
            String creator,
            String imagePath
    ) {
    }
}
