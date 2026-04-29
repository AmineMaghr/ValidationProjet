package com.example.app.views;
import com.example.app.entities.Personnage;
import com.example.app.entities.Universe;
import com.example.app.services.PersonnageService;
import com.example.app.services.UniverseService;
import com.example.app.services.ChatbotService;
import com.example.app.services.ImageGenerationService;
import com.example.app.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

public class PersonnageCreateView extends VBox {
    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String FORM_PANEL_BG = "#1A1F1EB3";
    private static final String BG_DARK = "#0D0F0F";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private final VBox formPanel;
    private Personnage editingPersonnage;
    private boolean isAdminOrigin = false;

    public PersonnageCreateView() {
        this(null, false);
    }
    public PersonnageCreateView(Personnage personnage) {
        this(personnage, false);
    }
    public PersonnageCreateView(Personnage personnage, boolean isAdminOrigin) {
        this.editingPersonnage = personnage;
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
        Label title = new Label(editingPersonnage == null ? "Créer un Personnage" : "Modifier le Personnage");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        ComboBox<Universe> universeCombo = new ComboBox<>();
        universeCombo.setPromptText("Sélectionnez l'Univers (Requis)");
        universeCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        universeCombo.setPrefWidth(Double.MAX_VALUE);
        try {
            UniverseService us = new UniverseService();
            universeCombo.setItems(FXCollections.observableArrayList(us.select()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        TextField nameField = createTextField("Nom du personnage (Requis)");
        
        ComboBox<String> classRoleCombo = new ComboBox<>(FXCollections.observableArrayList("Guerrier", "Mage", "Voleur", "Prêtre", "Druide", "Paladin", "Chasseur"));
        classRoleCombo.setPromptText("Sélectionnez la Classe/Rôle");
        classRoleCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        classRoleCombo.setPrefWidth(Double.MAX_VALUE);

        TextArea historyContext = createTextArea("Contexte Historique", 80);
        Button btnGenHistory   = createAIButton("✨ Générer");
        Button btnEnhHistory   = createEnhanceButton("🔮 Améliorer");
        btnGenHistory.setOnAction(e -> {
            String name = nameField.getText().trim();
            String role = classRoleCombo.getValue() != null ? classRoleCombo.getValue() : "héros";
            String universe = universeCombo.getValue() != null ? universeCombo.getValue().getName() : "monde fantastique";
            if (name.isEmpty()) { historyContext.setText("Entrez d'abord le nom du personnage."); return; }
            runAIAction(historyContext, btnGenHistory, () -> ChatbotService.generateCharacterHistory(name, role, universe));
        });
        btnEnhHistory.setOnAction(e -> {
            String text = historyContext.getText().trim();
            String name = nameField.getText().trim();
            String role = classRoleCombo.getValue() != null ? classRoleCombo.getValue() : "héros";
            if (text.isEmpty() || text.length() < 10) { historyContext.setText("Écrivez d'abord un texte à améliorer."); return; }
            runAIAction(historyContext, btnEnhHistory, () -> ChatbotService.enhanceCharacterHistory(name, role, text));
        });
        HBox historyBtns = new HBox(8, btnGenHistory, btnEnhHistory);
        VBox historyBox = new VBox(6, historyContext, historyBtns);

        TextArea abilitiesPowers = createTextArea("Capacités et Pouvoirs", 80);
        Button btnGenAbilities   = createAIButton("✨ Générer");
        Button btnEnhAbilities   = createEnhanceButton("🔮 Améliorer");
        btnGenAbilities.setOnAction(e -> {
            String name = nameField.getText().trim();
            String role = classRoleCombo.getValue() != null ? classRoleCombo.getValue() : "héros";
            if (name.isEmpty()) { abilitiesPowers.setText("Entrez d'abord le nom du personnage."); return; }
            runAIAction(abilitiesPowers, btnGenAbilities, () -> ChatbotService.generateCharacterAbilities(name, role));
        });
        btnEnhAbilities.setOnAction(e -> {
            String text = abilitiesPowers.getText().trim();
            String name = nameField.getText().trim();
            String role = classRoleCombo.getValue() != null ? classRoleCombo.getValue() : "héros";
            if (text.isEmpty() || text.length() < 10) { abilitiesPowers.setText("Écrivez d'abord un texte à améliorer."); return; }
            runAIAction(abilitiesPowers, btnEnhAbilities, () -> ChatbotService.enhanceCharacterAbilities(name, role, text));
        });
        HBox abilitiesBtns = new HBox(8, btnGenAbilities, btnEnhAbilities);
        VBox abilitiesBox = new VBox(6, abilitiesPowers, abilitiesBtns);
        
        Label lblStats = new Label("Statistiques (Max: 100)");
        lblStats.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        
        Spinner<Integer> strengthSpin = createSpinner();
        Spinner<Integer> agilitySpin = createSpinner();
        Spinner<Integer> magicSpin = createSpinner();
        Spinner<Integer> defenseSpin = createSpinner();
        
        addStatToGrid(statsGrid, "Force", strengthSpin, 0, 0);
        addStatToGrid(statsGrid, "Agilité", agilitySpin, 0, 1);
        addStatToGrid(statsGrid, "Magie", magicSpin, 1, 0);
        addStatToGrid(statsGrid, "Défense", defenseSpin, 1, 1);
        
        Label imgLabel = new Label("Portrait du Personnage");
        imgLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold;");
        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(180);
        imagePreview.setFitHeight(220);
        imagePreview.setPreserveRatio(false);
        imagePreview.setStyle("-fx-opacity: 0.5;");

        final byte[][] selectedImage = new byte[1][];

        if(editingPersonnage != null) {
            nameField.setText(editingPersonnage.getName());
            classRoleCombo.setValue(editingPersonnage.getClassRole());
            historyContext.setText(editingPersonnage.getHistoryContext());
            abilitiesPowers.setText(editingPersonnage.getAbilitiesPowers());
            strengthSpin.getValueFactory().setValue(editingPersonnage.getStrength());
            agilitySpin.getValueFactory().setValue(editingPersonnage.getAgility());
            magicSpin.getValueFactory().setValue(editingPersonnage.getMagic());
            defenseSpin.getValueFactory().setValue(editingPersonnage.getDefense());
            if (editingPersonnage.getPortraitImage() != null) {
                selectedImage[0] = editingPersonnage.getPortraitImage();
                try {
                    imagePreview.setImage(new Image(new ByteArrayInputStream(selectedImage[0])));
                    imagePreview.setStyle("-fx-opacity: 1;");
                } catch (Exception ignored) {}
            }
            
            if(editingPersonnage.getUniverse() != null) {
                // Match universe by ID
                for(Universe u : universeCombo.getItems()) {
                    if(u.getId() == editingPersonnage.getUniverse().getId()) {
                        universeCombo.setValue(u);
                        break;
                    }
                }
            }
        }
        
        Button btnImage = new Button("📁 Choisir portrait...");
        btnImage.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnImage.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                try {
                    selectedImage[0] = Files.readAllBytes(file.toPath());
                    btnImage.setText("📁 " + file.getName());
                    imagePreview.setImage(new Image(file.toURI().toString()));
                    imagePreview.setStyle("-fx-opacity: 1.0;");
                } catch (Exception ex) {
                    errorLabel.setText("Erreur lors de la lecture de l'image.");
                    errorLabel.setManaged(true); errorLabel.setVisible(true);
                }
            }
        });

        Button btnAIPortrait = new Button("🖼️ Générer via IA");
        btnAIPortrait.setStyle("-fx-background-color: #e67e2222; -fx-text-fill: #e67e22; -fx-border-color: #e67e22; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnAIPortrait.setOnAction(e -> {
            String name = nameField.getText().trim();
            String role = classRoleCombo.getValue() != null ? classRoleCombo.getValue() : "héros";
            if (name.isEmpty()) {
                errorLabel.setText("Entrez d'abord le nom du personnage.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            btnAIPortrait.setText("⏳ Génération...");
            btnAIPortrait.setDisable(true);
            imagePreview.setStyle("-fx-opacity: 0.4;");
            String url = ImageGenerationService.generateCharacterImageURL(name, role);
            new Thread(() -> {
                try {
                    byte[] bytes = ImageGenerationService.downloadImageBytes(url);
                    javafx.application.Platform.runLater(() -> {
                        selectedImage[0] = bytes;
                        imagePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
                        imagePreview.setStyle("-fx-opacity: 1.0;");
                        btnAIPortrait.setText("🖼️ Générer via IA");
                        btnAIPortrait.setDisable(false);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        btnAIPortrait.setText("❌ Réessayer");
                        btnAIPortrait.setDisable(false);
                        imagePreview.setStyle("-fx-opacity: 0.5;");
                    });
                    ex.printStackTrace();
                }
            }).start();
        });

        HBox imageBtns = new HBox(10, btnImage, btnAIPortrait);
        VBox imageBox = new VBox(10, imgLabel, imagePreview, imageBtns);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        Button btnSubmit = new Button(editingPersonnage == null ? "Créer le Personnage" : "Sauvegarder");
        btnSubmit.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 15 20; -fx-cursor: hand;");
        btnSubmit.setPrefWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> {
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
            
            String nameVal = nameField.getText() == null ? "" : nameField.getText().trim();
            if (nameVal.isEmpty()) {
                errorLabel.setText("Le nom est requis.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            if (nameVal.length() > 255) {
                errorLabel.setText("Le nom est trop long (Max: 255).");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            if (nameVal.length() < 3) {
                errorLabel.setText("Le nom doit comporter au moins 3 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            
            if (universeCombo.getValue() == null) {
                errorLabel.setText("Veuillez sélectionner un univers.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            
            if (classRoleCombo.getValue() == null) {
                errorLabel.setText("Veuillez sélectionner une classe/rôle.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            String roleVal = classRoleCombo.getValue();
            
            String historyVal = historyContext.getText() == null ? "" : historyContext.getText().trim();
            if (historyVal.isEmpty() || historyVal.length() < 10) {
                errorLabel.setText("Le contexte historique doit comporter au moins 10 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }
            
            String abilitiesVal = abilitiesPowers.getText() == null ? "" : abilitiesPowers.getText().trim();
            if (abilitiesVal.isEmpty() || abilitiesVal.length() < 10) {
                errorLabel.setText("Les capacités et pouvoirs doivent comporter au moins 10 caractères.");
                errorLabel.setManaged(true); errorLabel.setVisible(true);
                return;
            }

            try {
                Personnage p = editingPersonnage == null ? new Personnage() : editingPersonnage;
                p.setName(nameVal);
                p.setUniverse(universeCombo.getValue());
                p.setClassRole(roleVal);
                p.setHistoryContext(historyVal);
                p.setAbilitiesPowers(abilitiesVal);
                p.setStrength(strengthSpin.getValue());
                p.setAgility(agilitySpin.getValue());
                p.setMagic(magicSpin.getValue());
                p.setDefense(defenseSpin.getValue());
                p.setPortraitImage(selectedImage[0]);
                PersonnageService service = new PersonnageService();
                if(editingPersonnage == null) {
                    service.add(p);
                } else {
                    service.update(p);
                }
                SceneManager.getInstance().loadScene(isAdminOrigin ? "/admin/personnages" : "/personnages");
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Erreur serveur : " + ex.getMessage());
                errorLabel.setManaged(true); errorLabel.setVisible(true);
            }
        });
        applyMagicEffect(btnSubmit);
        formPanel.getChildren().addAll(title, errorLabel, universeCombo, nameField, classRoleCombo, historyBox, abilitiesBox, lblStats, statsGrid, imageBox, btnSubmit);
    }
    private void addStatToGrid(GridPane grid, String labelText, Spinner<Integer> spinner, int col, int row) {
        VBox v = new VBox(5);
        Label l = new Label(labelText);
        l.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        v.getChildren().addAll(l, spinner);
        grid.add(v, col, row);
    }
    private Spinner<Integer> createSpinner() {
        Spinner<Integer> sp = new Spinner<>(0, 100, 50);
        sp.setStyle("-fx-background-color: " + BG_DARK + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        return sp;
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

    /** Runs a blocking AI call on a background thread, disables the button, shows spinner text, then restores. */
    private void runAIAction(TextArea target, Button triggerBtn, java.util.concurrent.Callable<String> action) {
        String originalText  = target.getText();
        String originalLabel = triggerBtn.getText();
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
