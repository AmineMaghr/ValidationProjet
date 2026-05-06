package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.app.utils.MyDatabase;

public class AdvancedPreferencesController extends BaseController {

    @FXML private TextArea freeDescriptionArea;
    @FXML private ToggleButton genreEpic;
    @FXML private ToggleButton genreDark;
    @FXML private ToggleButton genreHeroic;
    @FXML private ToggleButton genreMedieval;
    @FXML private ToggleButton genreScifi;
    @FXML private Spinner<Integer> affinitySpinner;
    @FXML private ToggleButton themeMagie;
    @FXML private ToggleButton themeGuerre;
    @FXML private ToggleButton themePolitique;
    @FXML private ToggleButton themeNature;
    @FXML private TextField customTagsField;
    @FXML private Label statusLabel;
    @FXML private javafx.scene.layout.VBox successBox;
    @FXML private javafx.scene.control.ScrollPane mainScrollPane;

    // Auth UI Elements defined in FXML
    @FXML private javafx.scene.layout.HBox authButtons;
    @FXML private javafx.scene.layout.HBox profileBox;
    @FXML private javafx.scene.image.ImageView avatarImage;
    @FXML private Label usernameLabel;
    @FXML private javafx.scene.control.Button logoutBtn;
    @FXML private javafx.scene.control.Button loginBtn;
    @FXML private javafx.scene.control.Button registerBtn;

    // Error UI Elements defined in FXML
    @FXML private javafx.scene.layout.VBox errorsBox;
    @FXML private javafx.scene.layout.VBox errorsList;
    @FXML private javafx.scene.layout.GridPane genreGrid;
    @FXML private javafx.scene.control.Button submitBtn;

    private final ToggleGroup genreGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        if (genreEpic != null) genreEpic.setToggleGroup(genreGroup);
        if (genreDark != null) genreDark.setToggleGroup(genreGroup);
        if (genreHeroic != null) genreHeroic.setToggleGroup(genreGroup);
        if (genreMedieval != null) genreMedieval.setToggleGroup(genreGroup);
        if (genreScifi != null) genreScifi.setToggleGroup(genreGroup);

        if (affinitySpinner != null) {
            affinitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5));
        }
        if (genreEpic != null) genreEpic.setSelected(true);
    }

    @FXML
    private void savePreferences() {
        System.out.println("--- [DEBUG] Début de l'enregistrement des préférences ---");
        
        // Validation process
        boolean hasErrors = false;
        if (errorsList != null) {
            errorsList.getChildren().clear();
        }
        
        String desc = freeDescriptionArea != null ? freeDescriptionArea.getText() : "";
        if (desc == null || desc.trim().isEmpty()) {
            addError("La description libre est requise.");
            hasErrors = true;
        } else if (desc.trim().length() < 10) {
            addError("La description libre doit faire au moins 10 caractères.");
            hasErrors = true;
        }

        if (genreGroup.getSelectedToggle() == null) {
            addError("Veuillez sélectionner un genre favori.");
            hasErrors = true;
        }
        
        String tags = customTagsField != null ? customTagsField.getText().trim() : "";
        if (!tags.isEmpty() && !tags.startsWith("#")) {
            tags = "#" + tags;
            if (customTagsField != null) {
                customTagsField.setText(tags); // Update the UI field too
            }
        }

        if (tags.isEmpty()) {
            addError("Les tags personnalisés sont requis.");
            hasErrors = true;
        }

        List<String> themes = new ArrayList<>();
        if (themeMagie != null && themeMagie.isSelected()) themes.add("Magie");
        if (themeGuerre != null && themeGuerre.isSelected()) themes.add("Guerre");
        if (themePolitique != null && themePolitique.isSelected()) themes.add("Politique");
        if (themeNature != null && themeNature.isSelected()) themes.add("Nature");
        
        if (themes.isEmpty()) {
            addError("Veuillez sélectionner au moins un thème favori.");
            hasErrors = true;
        }

        if (hasErrors) {
            System.out.println("[DEBUG] Erreurs de validation trouvées. Annulation de l'insertion.");
            showErrors();
            if (mainScrollPane != null) {
                mainScrollPane.setVvalue(0.0);
            }
            return;
        } else {
            hideErrors();
        }

        // Logic using UserSession
        int simulatedUserId = com.example.app.utils.UserSession.getCurrentUserId();
        String genre = ((ToggleButton)genreGroup.getSelectedToggle()).getText();
        int affinity = affinitySpinner != null ? affinitySpinner.getValue() : 5;
        String joinedThemes = String.join(", ", themes);

        System.out.println("[DEBUG] Valeurs lues : ");
        System.out.println("  User ID : " + simulatedUserId);
        System.out.println("  Desc : " + desc);
        System.out.println("  Genre : " + genre);
        System.out.println("  Affinity : " + affinity);
        System.out.println("  Themes : " + joinedThemes);
        System.out.println("  Tags : " + tags);

        // SQL Insertion
        String sql = "INSERT INTO advanced_preferences " +
                "(free_description, favorite_genre, affinity_level, favorite_themes, custom_tags, user_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
        try {
            Connection conn = MyDatabase.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                System.out.println("[DEBUG] Connexion DB obtenue. Exécution de la requête SQL...");
                ps.setString(1, desc);
                ps.setString(2, genre);
                ps.setInt(3, affinity);
                ps.setString(4, joinedThemes);
                ps.setString(5, tags);
                ps.setInt(6, simulatedUserId);
                
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                ps.setTimestamp(7, now);
                ps.setTimestamp(8, now);
                
                int rowsAffected = ps.executeUpdate();
                System.out.println("[DEBUG] Insertion réussie. Lignes affectées : " + rowsAffected);

                // Succès UI -> redirection vers Accueil avec message
                AccueilController.showSuccessFromPreferences = true;
                navigateTo("/");
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Erreur critique lors de l'insertion DB ! " + e.getMessage());
            e.printStackTrace();
            addError("Erreur interne lors de l'enregistrement de vos préférences : " + e.getMessage());
            showErrors();
        }
    }

    private void addError(String message) {
        if (errorsList != null) {
            Label errLabel = new Label("• " + message);
            errLabel.setStyle("-fx-text-fill: #ff6b6b;");
            errorsList.getChildren().add(errLabel);
        }
    }

    private void showErrors() {
        if (errorsBox != null) {
            errorsBox.setVisible(true);
            errorsBox.setManaged(true);
        }
    }

    private void hideErrors() {
        if (errorsBox != null) {
            errorsBox.setVisible(false);
            errorsBox.setManaged(false);
        }
    }

    @FXML
    private void goBackToQuiz() {
        navigateTo("/quiz");
    }
    
    @FXML
    private void logout() {
        com.example.app.utils.UserSession.logout();
        goAccueil();
    }
}
