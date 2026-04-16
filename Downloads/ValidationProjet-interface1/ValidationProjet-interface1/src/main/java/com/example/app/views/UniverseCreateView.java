package com.example.app.views;
import com.example.app.entities.Universe;
import com.example.app.services.UniverseService;
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
    public UniverseCreateView() {
        this(null);
    }
    public UniverseCreateView(Universe universe) {
        this.editingUniverse = universe;
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
        TextArea storyContext = createTextArea("Contexte narratif", 120);
        TextField tagsField = createTextField("Tags (séparés par virgules)");
        
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
            
            String shortDescVal = shortDescription.getText() == null ? "" : shortDescription.getText().trim();
            if (shortDescVal.isEmpty() || shortDescVal.length() < 10) {
                errorLabel.setText("La courte description doit comporter au moins 10 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }

            String storyContextVal = storyContext.getText() == null ? "" : storyContext.getText().trim();
            if (storyContextVal.isEmpty() || storyContextVal.length() < 10) {
                errorLabel.setText("Le contexte narratif doit comporter au moins 10 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            
            try {
                Universe u = editingUniverse == null ? new Universe() : editingUniverse;
                u.setName(nameVal);
                u.setGenre(genreCombo.getValue());
                u.setShortDescription(shortDescVal);
                u.setStoryContext(storyContextVal);
                u.setThemesFromString(tagsField.getText() == null ? "" : tagsField.getText().trim());
                u.setBannerImage(selectedImage[0]);
                UniverseService service = new UniverseService();
                if (editingUniverse == null) {
                    service.add(u);
                } else {
                    service.update(u);
                }
                SceneManager.getInstance().loadScene("/universes");
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Erreur serveur : " + ex.getMessage());
                errorLabel.setManaged(true); errorLabel.setVisible(true);
            }
        });
        applyMagicEffect(btnSubmit);
        formPanel.getChildren().addAll(title, errorLabel, nameField, genreCombo, shortDescription, storyContext, tagsField, imageBox, btnSubmit);
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
