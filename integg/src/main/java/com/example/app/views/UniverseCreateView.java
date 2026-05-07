package com.example.app.views;
import com.example.app.entities.Universe;
import com.example.app.services.UniverseService;
import com.example.app.services.ChatbotService;
import com.example.app.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

public class UniverseCreateView extends VBox {
    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String FORM_PANEL_BG = "#1A1F1EB3";
    private static final String BG_DARK = "#0D0F0F";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private final VBox formPanel;
    private Universe editingUniverse;
    private boolean isAdminOrigin = false;

    public UniverseCreateView() {
        this(null, false);
    }
    public UniverseCreateView(Universe universe) {
        this(universe, false);
    }
    public UniverseCreateView(Universe universe, boolean isAdminOrigin) {
        this.editingUniverse = universe;
        this.isAdminOrigin = isAdminOrigin;
        this.setStyle("-fx-background-color: " + BG_MAIN + ";");
        this.getChildren().add(new HeaderView());
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        container.setStyle("-fx-background-color: " + BG_MAIN + ";");
        formPanel = new VBox(20);
        formPanel.setMaxWidth(600);
        formPanel.setPadding(new Insets(40));
        formPanel.setStyle("-fx-background-color: " + FORM_PANEL_BG + "; -fx-background-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);");
        setupForm();
        container.getChildren().add(formPanel);
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setStyle("-fx-background: " + BG_MAIN + "; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        this.getChildren().add(scrollPane);
    }
    private void setupForm() {
        Label title = new Label(editingUniverse == null ? "Créer un Univers" : "Modifier l'Univers");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        TextField nameField = createTextField("Nom de l'univers (Requis)");
        
        ComboBox<String> genreCombo = new ComboBox<>(FXCollections.observableArrayList("Fantasy", "Sci-Fi", "Medieval", "Cyberpunk", "Horror"));
        genreCombo.setPromptText("Sélectionnez le Genre (Requis)");
        genreCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        genreCombo.setPrefWidth(Double.MAX_VALUE);
        
        TextArea shortDescription = createTextArea("Courte description", 80);
        Button btnGenShort = createAIButton("✨ Générer");
        Button btnEnhShort = createEnhanceButton("🔮 Améliorer");
        btnGenShort.setOnAction(e -> {
            String name = nameField.getText().trim();
            String genre = genreCombo.getValue() != null ? genreCombo.getValue() : "Fantasy";
            if (name.isEmpty()) { shortDescription.setText("Entrez d'abord le nom de l'univers."); return; }
            runAIAction(shortDescription, btnGenShort, () -> ChatbotService.generateUniverseDescription(name, genre));
        });
        btnEnhShort.setOnAction(e -> {
            String text = shortDescription.getText().trim();
            String name = nameField.getText().trim();
            String genre = genreCombo.getValue() != null ? genreCombo.getValue() : "Fantasy";
            if (text.length() < 10) { shortDescription.setText("Écrivez d'abord une description à améliorer."); return; }
            runAIAction(shortDescription, btnEnhShort, () -> ChatbotService.enhanceUniverseDescription(name, genre, text));
        });
        HBox shortBtns = new HBox(8, btnGenShort, btnEnhShort);
        VBox shortDescBox = new VBox(6, shortDescription, shortBtns);

        TextArea storyContext = createTextArea("Contexte narratif", 120);
        Button btnGenStory = createAIButton("✨ Générer");
        Button btnEnhStory = createEnhanceButton("🔮 Améliorer");
        btnGenStory.setOnAction(e -> {
            String name = nameField.getText().trim();
            String genre = genreCombo.getValue() != null ? genreCombo.getValue() : "Fantasy";
            String shortDesc = shortDescription.getText().trim();
            if (name.isEmpty()) { storyContext.setText("Entrez d'abord le nom de l'univers."); return; }
            runAIAction(storyContext, btnGenStory, () -> ChatbotService.generateUniverseStory(name, genre, shortDesc.isEmpty() ? "Unknown setting" : shortDesc));
        });
        btnEnhStory.setOnAction(e -> {
            String text = storyContext.getText().trim();
            String name = nameField.getText().trim();
            String genre = genreCombo.getValue() != null ? genreCombo.getValue() : "Fantasy";
            if (text.length() < 10) { storyContext.setText("Écrivez d'abord un contexte à améliorer."); return; }
            runAIAction(storyContext, btnEnhStory, () -> ChatbotService.enhanceUniverseStory(name, genre, text));
        });
        HBox storyBtns = new HBox(8, btnGenStory, btnEnhStory);
        VBox storyBox = new VBox(6, storyContext, storyBtns);
        TextField tagsField = createTextField("Tags (séparés par virgules)");

        TextField videoUrlField = createTextField("🎬 Lien YouTube (optionnel — ex: https://youtube.com/watch?v=...)");

        Label imgLabel = new Label("Image de la Bannière");
        imgLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold;");
        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(520);
        imagePreview.setFitHeight(150);
        imagePreview.setPreserveRatio(false);
        imagePreview.setStyle("-fx-opacity: 0.5;"); // Default empty look
        
        final byte[][] selectedImage = new byte[1][];

        if (editingUniverse != null) {
            nameField.setText(editingUniverse.getName());
            genreCombo.setValue(editingUniverse.getGenre());
            shortDescription.setText(editingUniverse.getShortDescription());
            storyContext.setText(editingUniverse.getStoryContext());
            tagsField.setText(editingUniverse.getThemesAsString());
            if (editingUniverse.getVideoUrl() != null)
                videoUrlField.setText(editingUniverse.getVideoUrl());
            if (editingUniverse.getBannerImage() != null) {
                selectedImage[0] = editingUniverse.getBannerImage();
                try {
                    imagePreview.setImage(new Image(new ByteArrayInputStream(selectedImage[0])));
                    imagePreview.setStyle("-fx-opacity: 1;");
                } catch (Exception ignored) {}
            }
        }
        
        Button btnImage = new Button("Choisir une image...");
        btnImage.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnImage.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                try {
                    selectedImage[0] = Files.readAllBytes(file.toPath());
                    btnImage.setText("Bannière: " + file.getName());
                    imagePreview.setImage(new Image(file.toURI().toString()));
                    imagePreview.setStyle("-fx-opacity: 1.0;");
                } catch (Exception ex) {
                    errorLabel.setText("Erreur lors de la lecture de l'image.");
                    errorLabel.setManaged(true);
                    errorLabel.setVisible(true);
                }
            }
        });
        
        VBox imageBox = new VBox(10, imgLabel, imagePreview, btnImage);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        Button btnSubmit = new Button(editingUniverse == null ? "Créer l'Univers" : "Mettre à jour");
        btnSubmit.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 15 20; -fx-cursor: hand;");
        btnSubmit.setPrefWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> {
            // Validation (Contrôle de saisie)
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
            
            String nameVal = nameField.getText() == null ? "" : nameField.getText().trim();
            if (nameVal.isEmpty()) {
                errorLabel.setText("Le nom est requis.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            if (nameVal.length() < 3 || nameVal.length() > 255) {
                errorLabel.setText("Le nom doit comporter entre 3 et 255 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            if (genreCombo.getValue() == null || genreCombo.getValue().toString().trim().isEmpty()) {
                errorLabel.setText("Veuillez sélectionner un genre.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            
            String shortVal = shortDescription.getText() == null ? "" : shortDescription.getText().trim();
            if (shortVal.isEmpty() || shortVal.length() < 10) {
                errorLabel.setText("La courte description doit comporter au moins 10 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }

            String storyVal = storyContext.getText() == null ? "" : storyContext.getText().trim();
            if (storyVal.isEmpty() || storyVal.length() < 10) {
                errorLabel.setText("Le contexte narratif doit comporter au moins 10 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            
            try {
                Universe u = editingUniverse == null ? new Universe() : editingUniverse;
                u.setName(nameVal);
                u.setGenre(genreCombo.getValue());
                u.setShortDescription(shortVal);
                u.setStoryContext(storyVal);
                u.setThemesFromString(tagsField.getText() == null ? "" : tagsField.getText().trim());
                String videoVal = videoUrlField.getText() == null ? "" : videoUrlField.getText().trim();
                u.setVideoUrl(videoVal.isEmpty() ? null : videoVal);
                u.setBannerImage(selectedImage[0]);
                
                if (com.example.app.utils.UserSession.isLoggedIn()) {
                    u.setCreatorId(com.example.app.utils.UserSession.getCurrentUserId());
                }

                UniverseService service = new UniverseService();
                if(editingUniverse == null) {
                    service.add(u);
                } else {
                    service.update(u);
                }
                SceneManager.getInstance().loadScene(isAdminOrigin ? "/admin/universes" : "/universes");
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Erreur serveur : " + ex.getMessage());
                errorLabel.setManaged(true); errorLabel.setVisible(true);
            }
        });
        applyMagicEffect(btnSubmit);
        formPanel.getChildren().addAll(title, errorLabel, nameField, genreCombo, shortDescBox, storyBox, tagsField, videoUrlField, imageBox, btnSubmit);
    }
    private TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-prompt-text-fill: gray; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 10;");
        return tf;
    }
    private TextArea createTextArea(String prompt, double height) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefHeight(height);
        ta.setStyle("-fx-control-inner-background: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-prompt-text-fill: gray; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        return ta;
    }
    private Button createAIButton(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: #8e44ad22; -fx-text-fill: #c39bd3; -fx-border-color: #8e44ad; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 12px; -fx-padding: 5 12; -fx-cursor: hand;");
        return btn;
    }

    private Button createEnhanceButton(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: #1a6b4a22; -fx-text-fill: #18E3A4; -fx-border-color: #18E3A4; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 12px; -fx-padding: 5 12; -fx-cursor: hand;");
        return btn;
    }

    private void runAIAction(javafx.scene.control.TextArea target, Button triggerBtn, java.util.concurrent.Callable<String> action) {
        String originalLabel = triggerBtn.getText();
        String originalText  = target.getText();
        target.setText("⏳ IA en cours...");
        triggerBtn.setDisable(true);
        new Thread(() -> {
            String result;
            try { result = action.call(); }
            catch (Exception ex) { result = originalText; ex.printStackTrace(); }
            final String r = result;
            javafx.application.Platform.runLater(() -> {
                target.setText(r);
                triggerBtn.setText(originalLabel);
                triggerBtn.setDisable(false);
            });
        }).start();
    }

    private void applyMagicEffect(Button btn) {
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.web(PRIMARY_COLOR));
            btn.setEffect(shadow);
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setEffect(null);
        });
    }
}
