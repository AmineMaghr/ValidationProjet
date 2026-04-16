package com.example.app.views;
import com.example.app.entities.Personnage;
import com.example.app.entities.Universe;
import com.example.app.services.PersonnageService;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
    public PersonnageCreateView() {
        this(null);
    }
    public PersonnageCreateView(Personnage personnage) {
        this.editingPersonnage = personnage;
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
        Label title = new Label(editingPersonnage == null ? "Créer un Personnage" : "Modifier le Personnage");
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        ComboBox<Universe> universeCombo = new ComboBox<>();
        universeCombo.setPromptText("Sélectionnez l'Univers");
        universeCombo.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        universeCombo.setPrefWidth(Double.MAX_VALUE);
        try {
            UniverseService us = new UniverseService();
            universeCombo.setItems(FXCollections.observableArrayList(us.select()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextField nameField = createTextField("Nom du personnage");
        TextField classRoleField = createTextField("Classe ou Rôle (ex: Guerrier)");
        TextArea historyContext = createTextArea("Contexte Historique", 80);
        TextArea abilitiesPowers = createTextArea("Capacités et Pouvoirs", 80);
        Label lblStats = new Label("Statistiques");
        lblStats.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");
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
        
        final byte[][] selectedImage = new byte[1][];

        if(editingPersonnage != null) {
            nameField.setText(editingPersonnage.getName());
            classRoleField.setText(editingPersonnage.getClassRole());
            historyContext.setText(editingPersonnage.getHistoryContext());
            abilitiesPowers.setText(editingPersonnage.getAbilitiesPowers());
            strengthSpin.getValueFactory().setValue(editingPersonnage.getStrength());
            agilitySpin.getValueFactory().setValue(editingPersonnage.getAgility());
            magicSpin.getValueFactory().setValue(editingPersonnage.getMagic());
            defenseSpin.getValueFactory().setValue(editingPersonnage.getDefense());
            selectedImage[0] = editingPersonnage.getPortraitImage();
            
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
        Button btnImage = new Button("Choisir portrait...");
        btnImage.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        btnImage.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                try {
                    selectedImage[0] = Files.readAllBytes(file.toPath());
                    btnImage.setText("Portrait: " + file.getName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert a = new Alert(Alert.AlertType.ERROR, "Erreur lors de la lecture de l'image.");
                    a.show();
                }
            }
        });
        Button btnSubmit = new Button(editingPersonnage == null ? "Créer le Personnage" : "Sauvegarder");
        btnSubmit.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10 20;");
        btnSubmit.setPrefWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Le nom est requis.");
                a.show();
                return;
            }
            try {
                Personnage p = editingPersonnage == null ? new Personnage() : editingPersonnage;
                p.setName(nameField.getText());
                p.setUniverse(universeCombo.getValue());
                p.setClassRole(classRoleField.getText());
                p.setHistoryContext(historyContext.getText());
                p.setAbilitiesPowers(abilitiesPowers.getText());
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
                Alert a = new Alert(Alert.AlertType.INFORMATION, editingPersonnage == null ? "Personnage créé !" : "Personnage modifié !");
                a.showAndWait();
                SceneManager.getInstance().loadScene("/personnages");
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage());
                a.show();
            }
        });
        applyMagicEffect(btnSubmit);
        formPanel.getChildren().addAll(title, universeCombo, nameField, classRoleField, historyContext, abilitiesPowers, lblStats, statsGrid, btnImage, btnSubmit);
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
