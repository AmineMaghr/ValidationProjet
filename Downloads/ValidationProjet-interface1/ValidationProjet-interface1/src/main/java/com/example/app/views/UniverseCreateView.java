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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
        formPanel.setPadding(new Insets(30));
        formPanel.setStyle("-fx-background-color: " + FORM_PANEL_BG + "; -fx-background-radius: 12px;");
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
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        TextField nameField = createTextField("Nom de l'univers");
        ComboBox<String> genreCombo = new ComboBox<>(FXCollections.observableArrayList("Fantasy", "Sci-Fi", "Medieval", "Cyberpunk", "Horror"));
        genreCombo.setPromptText("Genre");
        genreCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        genreCombo.setPrefWidth(Double.MAX_VALUE);
        TextArea shortDescription = createTextArea("Courte description", 60);
        TextArea storyContext = createTextArea("Contexte narratif", 120);
        TextField tagsField = createTextField("Tags (séparés par virgules)");
        if (editingUniverse != null) {
            nameField.setText(editingUniverse.getName());
            genreCombo.setValue(editingUniverse.getGenre());
            shortDescription.setText(editingUniverse.getShortDescription());
            storyContext.setText(editingUniverse.getStoryContext());
            tagsField.setText(editingUniverse.getThemesAsString());
        }
        Button btnImage = new Button("Choisir une image de bannière");
        btnImage.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        btnImage.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.showOpenDialog(null);
        });
        Button btnSubmit = new Button(editingUniverse == null ? "Créer l'Univers" : "Mettre à jour");
        btnSubmit.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10 20;");
        btnSubmit.setPrefWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Le nom est requis.");
                a.show();
                return;
            }
            try {
                Universe u = editingUniverse == null ? new Universe() : editingUniverse;
                u.setName(nameField.getText());
                u.setGenre(genreCombo.getValue());
                u.setShortDescription(shortDescription.getText());
                u.setStoryContext(storyContext.getText());
                u.setThemesFromString(tagsField.getText());
                UniverseService service = new UniverseService();
                if (editingUniverse == null) {
                    service.add(u);
                } else {
                    service.update(u);
                }
                Alert a = new Alert(Alert.AlertType.INFORMATION, editingUniverse == null ? "Univers créé !" : "Univers modifié !");
                a.showAndWait();
                SceneManager.getInstance().loadScene("/universes");
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage());
                a.show();
            }
        });
        applyMagicEffect(btnSubmit);
        formPanel.getChildren().addAll(title, nameField, genreCombo, shortDescription, storyContext, tagsField, btnImage, btnSubmit);
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
