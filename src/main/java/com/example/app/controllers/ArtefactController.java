package com.example.app.controllers;

import com.example.app.entities.Artefact;
import com.example.app.entities.Commentaire;
import com.example.app.services.ArtefactService;
import com.example.app.services.CommentaireService;
import com.example.app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ArtefactController extends BaseController {

    // ==================== SIDEBAR BUTTONS ====================
    @FXML
    private Button homeBtn;
    @FXML
    private Button discoverBtn;
    @FXML
    private Button universesBtn;
    @FXML
    private Button personnagesBtn;
    @FXML
    private Button oeuvresBtn;
    @FXML
    private Button artefactsBtn;
    @FXML
    private Button shopBtn;
    @FXML
    private Button challengesBtn;

    // ==================== ÉLÉMENTS COMMUNS ====================
    @FXML
    private StackPane contentArea;

    // ==================== VUE LISTE (index) ====================
    @FXML
    private VBox listView;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private FlowPane artefactsGrid;
    @FXML
    private Label resultCount;
    @FXML
    private Button createBtn;
    @FXML
    private CheckBox myItemsCheckbox;
    @FXML
    private VBox emptyState;
    @FXML
    private Button allBtn;
    @FXML
    private Button myItemsBtn;

    // ==================== VUE FORMULAIRE (create/edit) ====================
    @FXML
    private VBox formView;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField universeField;
    @FXML
    private TextArea originsArea;
    @FXML
    private TextArea powersArea;
    @FXML
    private ComboBox<String> rarityCombo;
    @FXML
    private ImageView formImageView;
    @FXML
    private Button selectImageBtn;
    @FXML
    private Button submitBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label formTitle;
    @FXML
    private Label formError;

    // ==================== VUE DÉTAIL (show) ====================
    @FXML
    private VBox detailView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label universeLabel;
    @FXML
    private Label rarityLabel;
    @FXML
    private TextArea originsDetailArea;
    @FXML
    private TextArea powersDetailArea;
    @FXML
    private ImageView detailImageView;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button addCommentBtn;
    @FXML
    private ListView<Commentaire> commentList;

    // ==================== SERVICES ====================
    private ArtefactService artefactService = new ArtefactService();
    private CommentaireService commentaireService = new CommentaireService();
    private ObservableList<Artefact> artefacts = FXCollections.observableArrayList();
    private Artefact currentArtefact;
    private File selectedImageFile;
    private boolean isEditMode = false;
    private String currentFilter = "all";

    // ==================== INITIALISATION ====================
    @FXML
    public void initialize() {
        // Configuration des ComboBox
        if (typeFilter != null) {
            typeFilter.setItems(FXCollections.observableArrayList("Tous les types"));
            typeFilter.setValue("Tous les types");
        }
        if (typeCombo != null) {
            typeCombo.setItems(
                    FXCollections.observableArrayList("Arme", "Armure", "Bijoux", "Relique", "Outil Magique"));
        }
        if (rarityCombo != null) {
            rarityCombo
                    .setItems(FXCollections.observableArrayList("Commune", "Rare", "Épique", "Légendaire", "Mythique"));
        }

        // Écouteurs pour la liste
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, val) -> loadArtefacts());
        }
        if (typeFilter != null) {
            typeFilter.valueProperty().addListener((obs, old, val) -> loadArtefacts());
        }
        if (myItemsCheckbox != null) {
            myItemsCheckbox.selectedProperty().addListener((obs, old, val) -> loadArtefacts());
        }

        // Afficher la vue liste par défaut
        showListView();
        loadTypes();
        loadArtefacts();

        if (!UserSession.isLoggedIn()) {
            if (myItemsCheckbox != null)
                myItemsCheckbox.setVisible(false);
            if (myItemsBtn != null)
                myItemsBtn.setVisible(false);
        }
    }

    // ==================== GESTION DES VUES ====================
    private void showListView() {
        if (listView != null) {
            listView.setVisible(true);
            listView.setManaged(true);
        }
        if (formView != null) {
            formView.setVisible(false);
            formView.setManaged(false);
        }
        if (detailView != null) {
            detailView.setVisible(false);
            detailView.setManaged(false);
        }
    }

    private void showFormView() {
        if (listView != null) {
            listView.setVisible(false);
            listView.setManaged(false);
        }
        if (detailView != null) {
            detailView.setVisible(false);
            detailView.setManaged(false);
        }
        if (formView != null) {
            formView.setVisible(true);
            formView.setManaged(true);
        }
    }

    private void showDetailView() {
        if (listView != null) {
            listView.setVisible(false);
            listView.setManaged(false);
        }
        if (formView != null) {
            formView.setVisible(false);
            formView.setManaged(false);
        }
        if (detailView != null) {
            detailView.setVisible(true);
            detailView.setManaged(true);
        }
    }

    // ==================== VUE LISTE (index) ====================
    private void loadTypes() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws SQLException {
                return artefactService.getAvailableTypes();
            }
        };
        task.setOnSucceeded(e -> {
            if (typeFilter != null) {
                typeFilter.getItems().clear();
                typeFilter.getItems().add("Tous les types");
                typeFilter.getItems().addAll(task.getValue());
            }
        });
        new Thread(task).start();
    }

    private void loadArtefacts() {
        Task<List<Artefact>> task = new Task<>() {
            @Override
            protected List<Artefact> call() throws SQLException {
                String search = searchField != null ? searchField.getText() : "";
                String type = (typeFilter != null && "Tous les types".equals(typeFilter.getValue())) ? null
                        : (typeFilter != null ? typeFilter.getValue() : null);
                Integer userId = null;
                if ("my".equals(currentFilter) && UserSession.isLoggedIn()) {
                    userId = UserSession.getCurrentUser().getId();
                }
                return artefactService.searchArtefacts(search, type, userId);
            }
        };
        task.setOnSucceeded(e -> {
            artefacts.setAll(task.getValue());
            updateGrid();
            if (resultCount != null) {
                resultCount.setText(artefacts.size() + " artefact(s) trouvé(s)");
            }
            if (emptyState != null)
                emptyState.setVisible(artefacts.isEmpty());
        });
        new Thread(task).start();
    }

    private void updateGrid() {
        if (artefactsGrid == null)
            return;
        artefactsGrid.getChildren().clear();
        for (Artefact artefact : artefacts) {
            artefactsGrid.getChildren().add(createArtefactCard(artefact));
        }
    }

    private VBox createArtefactCard(Artefact artefact) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: #1a1f1e; -fx-background-radius: 12; -fx-padding: 0; -fx-pref-width: 280; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> showArtefactDetail(artefact.getId()));

        // Bannière
        StackPane banner = new StackPane();
        banner.setStyle(
                "-fx-pref-height: 160; -fx-background-color: linear-gradient(to bottom, #FF6B6B, #FF8E7F); -fx-alignment: center;");

        if (artefact.getImageUrl() != null && !artefact.getImageUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(artefact.getImageUrl()));
                imageView.setFitWidth(280);
                imageView.setFitHeight(160);
                imageView.setPreserveRatio(true);
                banner.getChildren().add(imageView);
            } catch (Exception ex) {
                Label fallback = new Label("✨");
                fallback.setStyle("-fx-font-size: 48px;");
                banner.getChildren().add(fallback);
            }
        } else {
            Label fallback = new Label("✨");
            fallback.setStyle("-fx-font-size: 48px;");
            banner.getChildren().add(fallback);
        }

        // Contenu
        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 15;");

        Label name = new Label(artefact.getName());
        name.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox tags = new HBox(5);
        Label typeTag = new Label(artefact.getType());
        typeTag.setStyle(
                "-fx-background-color: rgba(255,107,107,0.1); -fx-text-fill: #FF6B6B; -fx-border-color: #FF6B6B; -fx-border-radius: 12; -fx-padding: 4 10; -fx-font-size: 11px;");
        Label rarityTag = new Label(artefact.getRarity());
        rarityTag.setStyle(
                "-fx-background-color: rgba(255,107,107,0.1); -fx-text-fill: #FF6B6B; -fx-border-color: #FF6B6B; -fx-border-radius: 12; -fx-padding: 4 10; -fx-font-size: 11px;");
        tags.getChildren().addAll(typeTag, rarityTag);

        Text description = new Text(artefact.getOrigins());
        description.setStyle("-fx-fill: #B0B9B6; -fx-font-size: 12px;");
        description.wrappingWidthProperty().bind(card.widthProperty().subtract(30));
        if (description.getText().length() > 100) {
            description.setText(description.getText().substring(0, 100) + "...");
        }

        HBox footer = new HBox(10);
        footer.setStyle("-fx-padding: 10 0 0 0; -fx-border-color: rgba(255,107,107,0.2); -fx-border-width: 1 0 0 0;");
        Label universe = new Label(artefact.getUniverse());
        universe.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        footer.getChildren().add(universe);

        content.getChildren().addAll(name, tags, description, footer);
        card.getChildren().addAll(banner, content);
        return card;
    }

    // ==================== ACTIONS INDEX ====================

    @FXML
    private void resetFilters() {
        if (searchField != null)
            searchField.clear();
        if (typeFilter != null)
            typeFilter.setValue("Tous les types");
        if (myItemsCheckbox != null)
            myItemsCheckbox.setSelected(false);
        currentFilter = "all";
        loadArtefacts();
    }

    @FXML
    private void showCreateForm() {
        isEditMode = false;
        currentArtefact = null;
        selectedImageFile = null;
        if (formTitle != null)
            formTitle.setText("Créer un Artefact");
        if (submitBtn != null)
            submitBtn.setText("Créer");
        clearForm();
        showFormView();
    }

    @FXML
    private void showAll() {
        currentFilter = "all";
        updateButtonStyles(allBtn);
        loadArtefacts();
    }

    @FXML
    private void showMyItems() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Veuillez vous connecter pour voir vos artefacts");
            navigateTo("/login");
            return;
        }
        currentFilter = "my";
        updateButtonStyles(myItemsBtn);
        loadArtefacts();
    }

    private void updateButtonStyles(Button activeButton) {
        String activeStyle = "-fx-background-color: #18E3A4; -fx-text-fill: #0b0f10; -fx-padding: 8 16; -fx-background-radius: 15;";
        String inactiveStyle = "-fx-background-color: #2a3139; -fx-text-fill: #fff; -fx-padding: 8 16; -fx-background-radius: 15;";

        if (allBtn != null)
            allBtn.setStyle(inactiveStyle);
        if (myItemsBtn != null)
            myItemsBtn.setStyle(inactiveStyle);
        if (activeButton != null)
            activeButton.setStyle(activeStyle);
    }

    // ==================== VUE FORMULAIRE (create/edit) ====================

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null && formImageView != null) {
            formImageView.setImage(new Image(selectedImageFile.toURI().toString()));
            formImageView.setVisible(true);
        }
    }

    @FXML
    private void submitForm() {
        if (nameField == null || nameField.getText().isEmpty()) {
            showFormError("Le nom est requis");
            return;
        }
        if (typeCombo == null || typeCombo.getValue() == null) {
            showFormError("Le type est requis");
            return;
        }
        if (universeField == null || universeField.getText().isEmpty()) {
            showFormError("L'univers est requis");
            return;
        }
        if (originsArea == null || originsArea.getText().isEmpty()) {
            showFormError("Les origines sont requises");
            return;
        }
        if (powersArea == null || powersArea.getText().isEmpty()) {
            showFormError("Les pouvoirs sont requis");
            return;
        }
        if (rarityCombo == null || rarityCombo.getValue() == null) {
            showFormError("La rareté est requise");
            return;
        }
        if (selectedImageFile == null && !isEditMode) {
            showFormError("L'image est requise");
            return;
        }

        Artefact artefact = isEditMode ? currentArtefact : new Artefact();
        artefact.setName(nameField.getText());
        artefact.setType(typeCombo.getValue());
        artefact.setUniverse(universeField.getText());
        artefact.setOrigins(originsArea.getText());
        artefact.setPowers(powersArea.getText());
        artefact.setRarity(rarityCombo.getValue());
        if (UserSession.isLoggedIn()) {
            artefact.setCreatedBy(UserSession.getCurrentUser());
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (isEditMode) {
                    artefactService.update(artefact);
                } else {
                    artefactService.add(artefact);
                }
                if (selectedImageFile != null) {
                    artefactService.saveImage(artefact.getId(), selectedImageFile);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", isEditMode ? "Artefact modifié !" : "Artefact créé !");
            showListView();
            loadArtefacts();
        });
        task.setOnFailed(e -> showFormError("Erreur lors de l'enregistrement"));
        new Thread(task).start();
    }

    @FXML
    private void cancelForm() {
        showListView();
    }

    private void showFormError(String msg) {
        if (formError != null) {
            formError.setText(msg);
            formError.setVisible(true);
        }
    }

    private void clearForm() {
        if (nameField != null)
            nameField.clear();
        if (typeCombo != null)
            typeCombo.setValue(null);
        if (universeField != null)
            universeField.clear();
        if (originsArea != null)
            originsArea.clear();
        if (powersArea != null)
            powersArea.clear();
        if (rarityCombo != null)
            rarityCombo.setValue(null);
        if (formImageView != null) {
            formImageView.setImage(null);
            formImageView.setVisible(false);
        }
        selectedImageFile = null;
        if (formError != null)
            formError.setVisible(false);
    }

    // ==================== VUE DÉTAIL (show) ====================

    public void showArtefactDetail(int artefactId) {
        Task<Artefact> task = new Task<>() {
            @Override
            protected Artefact call() throws SQLException {
                return artefactService.select().stream()
                        .filter(a -> a.getId() == artefactId)
                        .findFirst()
                        .orElse(null);
            }
        };
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                currentArtefact = task.getValue();
                if (nameLabel != null)
                    nameLabel.setText(currentArtefact.getName());
                if (typeLabel != null)
                    typeLabel.setText(currentArtefact.getType());
                if (universeLabel != null)
                    universeLabel.setText(currentArtefact.getUniverse());
                if (rarityLabel != null)
                    rarityLabel.setText(currentArtefact.getRarity());
                if (originsDetailArea != null)
                    originsDetailArea.setText(currentArtefact.getOrigins());
                if (powersDetailArea != null)
                    powersDetailArea.setText(currentArtefact.getPowers());

                if (currentArtefact.getImageUrl() != null && !currentArtefact.getImageUrl().isEmpty()
                        && detailImageView != null) {
                    try {
                        detailImageView.setImage(new Image(currentArtefact.getImageUrl()));
                    } catch (Exception ex) {
                    }
                }

                boolean canEdit = UserSession.isLoggedIn() &&
                        (UserSession.getCurrentUser().isAdmin() ||
                                (currentArtefact.getCreatedBy() != null &&
                                        currentArtefact.getCreatedBy().getId() == UserSession.getCurrentUser()
                                                .getId()));
                if (editBtn != null)
                    editBtn.setVisible(canEdit);
                if (deleteBtn != null)
                    deleteBtn.setVisible(canEdit);

                loadComments();
                showDetailView();
            }
        });
        new Thread(task).start();
    }

    private void loadComments() {
        if (currentArtefact == null || commentList == null)
            return;
        Task<List<Commentaire>> task = new Task<>() {
            @Override
            protected List<Commentaire> call() throws SQLException {
                return commentaireService.findByArtefact(currentArtefact.getId());
            }
        };
        task.setOnSucceeded(e -> commentList.setItems(FXCollections.observableArrayList(task.getValue())));
        new Thread(task).start();
    }

    @FXML
    private void editArtefact() {
        isEditMode = true;
        selectedImageFile = null;
        if (formTitle != null)
            formTitle.setText("Modifier l'Artefact");
        if (submitBtn != null)
            submitBtn.setText("Mettre à jour");
        if (nameField != null)
            nameField.setText(currentArtefact.getName());
        if (typeCombo != null)
            typeCombo.setValue(currentArtefact.getType());
        if (universeField != null)
            universeField.setText(currentArtefact.getUniverse());
        if (originsArea != null)
            originsArea.setText(currentArtefact.getOrigins());
        if (powersArea != null)
            powersArea.setText(currentArtefact.getPowers());
        if (rarityCombo != null)
            rarityCombo.setValue(currentArtefact.getRarity());

        if (currentArtefact.getImageUrl() != null && !currentArtefact.getImageUrl().isEmpty()
                && formImageView != null) {
            try {
                formImageView.setImage(new Image(currentArtefact.getImageUrl()));
                formImageView.setVisible(true);
            } catch (Exception ex) {
            }
        }
        if (formError != null)
            formError.setVisible(false);
        showFormView();
    }

    @FXML
    private void deleteArtefact() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'artefact");
        confirm.setContentText("Supprimer " + currentArtefact.getName() + " ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        artefactService.delete(currentArtefact.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    showAlert("Succès", "Artefact supprimé");
                    showListView();
                    loadArtefacts();
                });
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void addComment() {
        if (commentArea == null || commentArea.getText().isEmpty())
            return;

        Commentaire comment = new Commentaire();
        comment.setContenu(commentArea.getText());
        comment.setArtefactId(currentArtefact.getId());
        comment.setUserId(UserSession.getCurrentUser().getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                commentaireService.add(comment);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            commentArea.clear();
            loadComments();
            showAlert("Succès", "Commentaire ajouté");
        });
        new Thread(task).start();
    }

    @FXML
    private void goBackToList() {
        showListView();
        loadArtefacts();
    }
}