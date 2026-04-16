package com.example.app.controllers;

import com.example.app.entities.Oeuvre;
import com.example.app.entities.Commentaire;
import com.example.app.services.OeuvreService;
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
import java.time.LocalDate;
import java.util.List;

public class OeuvreController extends BaseController {

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

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox listView;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private FlowPane oeuvreList;
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

    @FXML
    private VBox formView;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField authorField;
    @FXML
    private TextArea descriptionArea;
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

    @FXML
    private VBox detailView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private TextArea descriptionDetailArea;
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

    private OeuvreService oeuvreService = new OeuvreService();
    private CommentaireService commentaireService = new CommentaireService();
    private ObservableList<Oeuvre> oeuvreDataList = FXCollections.observableArrayList();
    private Oeuvre currentOeuvre;
    private File selectedImageFile;
    private boolean isEditMode = false;
    private String currentFilter = "all";

    @FXML
    public void initialize() {
        if (typeFilter != null) {
            typeFilter.setItems(FXCollections.observableArrayList("Tous les types"));
            typeFilter.setValue("Tous les types");
        }
        if (typeCombo != null) {
            typeCombo.setItems(
                    FXCollections.observableArrayList("Peinture", "Sculpture", "Musique", "Literaturer", "Poésie",
                            "Photographie", "Cinéma", "Autre"));
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, val) -> loadOeuvres());
        }
        if (typeFilter != null) {
            typeFilter.valueProperty().addListener((obs, old, val) -> loadOeuvres());
        }
        if (myItemsCheckbox != null) {
            myItemsCheckbox.selectedProperty().addListener((obs, old, val) -> loadOeuvres());
        }

        showListView();
        loadTypes();
        loadOeuvres();

        if (!UserSession.isLoggedIn()) {
            if (myItemsCheckbox != null)
                myItemsCheckbox.setVisible(false);
            if (myItemsBtn != null)
                myItemsBtn.setVisible(false);
        }
    }

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

    private void loadTypes() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws SQLException {
                return oeuvreService.getAvailableTypes();
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

    private void loadOeuvres() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws SQLException {
                String search = searchField != null ? searchField.getText() : "";
                String type = (typeFilter != null && "Tous les types".equals(typeFilter.getValue())) ? null
                        : (typeFilter != null ? typeFilter.getValue() : null);
                Integer userId = null;
                if ("my".equals(currentFilter) && UserSession.isLoggedIn()) {
                    userId = UserSession.getCurrentUser().getId();
                }
                return oeuvreService.searchOeuvres(search, type, userId);
            }
        };
        task.setOnSucceeded(e -> {
            oeuvreDataList.setAll(task.getValue());
            updateGrid();
            if (resultCount != null) {
                resultCount.setText(oeuvreDataList.size() + " œuvre(s) trouvée(s)");
            }
            if (emptyState != null)
                emptyState.setVisible(oeuvreDataList.isEmpty());
        });
        new Thread(task).start();
    }

    private void updateGrid() {
        if (oeuvreList == null)
            return;
        oeuvreList.getChildren().clear();
        for (Oeuvre oeuvre : oeuvreDataList) {
            oeuvreList.getChildren().add(createOeuvreCard(oeuvre));
        }
    }

    private VBox createOeuvreCard(Oeuvre oeuvre) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: #1a1f1e; -fx-background-radius: 12; -fx-padding: 0; -fx-pref-width: 280; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> showOeuvreDetail(oeuvre.getId()));

        StackPane banner = new StackPane();
        banner.setStyle(
                "-fx-pref-height: 160; -fx-background-color: linear-gradient(to bottom, #18E3A4, #14B589); -fx-alignment: center;");

        if (oeuvre.getImageUrl() != null && !oeuvre.getImageUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(oeuvre.getImageUrl()));
                imageView.setFitWidth(280);
                imageView.setFitHeight(160);
                imageView.setPreserveRatio(true);
                banner.getChildren().add(imageView);
            } catch (Exception ex) {
                Label fallback = new Label("🎨");
                fallback.setStyle("-fx-font-size: 48px;");
                banner.getChildren().add(fallback);
            }
        } else {
            Label fallback = new Label("🎨");
            fallback.setStyle("-fx-font-size: 48px;");
            banner.getChildren().add(fallback);
        }

        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 15;");

        Label name = new Label(oeuvre.getTitle());
        name.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox tags = new HBox(5);
        Label typeTag = new Label(oeuvre.getType());
        typeTag.setStyle(
                "-fx-background-color: rgba(24,227,164,0.1); -fx-text-fill: #18E3A4; -fx-border-color: #18E3A4; -fx-border-radius: 12; -fx-padding: 4 10; -fx-font-size: 11px;");
        tags.getChildren().add(typeTag);

        Text description = new Text(oeuvre.getDescription());
        description.setStyle("-fx-fill: #B0B9B6; -fx-font-size: 12px;");
        description.wrappingWidthProperty().bind(card.widthProperty().subtract(30));
        if (description.getText() != null && description.getText().length() > 100) {
            description.setText(description.getText().substring(0, 100) + "...");
        }

        HBox footer = new HBox(10);
        footer.setStyle("-fx-padding: 10 0 0 0; -fx-border-color: rgba(24,227,164,0.2); -fx-border-width: 1 0 0 0;");
        Label author = new Label(oeuvre.getAuthor());
        author.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        footer.getChildren().add(author);

        content.getChildren().addAll(name, tags, description, footer);
        card.getChildren().addAll(banner, content);
        return card;
    }

    @FXML
    private void resetFilters() {
        if (searchField != null)
            searchField.clear();
        if (typeFilter != null)
            typeFilter.setValue("Tous les types");
        if (myItemsCheckbox != null)
            myItemsCheckbox.setSelected(false);
        currentFilter = "all";
        loadOeuvres();
    }

    @FXML
    private void showCreateForm() {
        isEditMode = false;
        currentOeuvre = null;
        selectedImageFile = null;
        if (formTitle != null)
            formTitle.setText("Créer une Œuvre");
        if (submitBtn != null)
            submitBtn.setText("Créer");
        clearForm();
        showFormView();
    }

    @FXML
    private void showAll() {
        currentFilter = "all";
        updateButtonStyles(allBtn);
        loadOeuvres();
    }

    @FXML
    private void showMyItems() {
        if (!UserSession.isLoggedIn()) {
            showAlert("Connexion requise", "Veuillez vous connecter pour voir vos œuvres");
            navigateTo("/login");
            return;
        }
        currentFilter = "my";
        updateButtonStyles(myItemsBtn);
        loadOeuvres();
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
        String title = titleField == null ? "" : titleField.getText().trim();
        String type = typeCombo == null ? null : typeCombo.getValue();
        String author = authorField == null ? "" : authorField.getText().trim();
        String description = descriptionArea == null ? "" : descriptionArea.getText().trim();
        boolean hasExistingImage = isEditMode && currentOeuvre != null && currentOeuvre.getImageUrl() != null
                && !currentOeuvre.getImageUrl().isEmpty();

        if (title.isEmpty()) {
            showFormError("Le titre est requis");
            return;
        }
        if (title.length() < 2) {
            showFormError("Le titre doit contenir au moins 2 lettres");
            return;
        }
        if (type == null || type.isEmpty()) {
            showFormError("Le type est requis");
            return;
        }
        if (author.isEmpty()) {
            showFormError("L'auteur est requis");
            return;
        }
        if (description.isEmpty()) {
            showFormError("La description est requise");
            return;
        }
        if (!hasMinimumLetters(description, 10)) {
            showFormError("La description doit contenir au moins 10 caractères alphabétiques");
            return;
        }
        if (selectedImageFile == null && !hasExistingImage) {
            showFormError("L'image est requise");
            return;
        }

        Oeuvre oeuvre = isEditMode ? currentOeuvre : new Oeuvre();
        oeuvre.setTitle(title);
        oeuvre.setType(type);
        oeuvre.setAuthor(author);
        oeuvre.setDescription(description);
        oeuvre.setDatePublication(isEditMode ? currentOeuvre.getDatePublication() : LocalDate.now());
        if (UserSession.isLoggedIn()) {
            oeuvre.setCreateurId(UserSession.getCurrentUser().getId());
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (isEditMode) {
                    oeuvreService.update(oeuvre);
                } else {
                    oeuvreService.add(oeuvre);
                }
                if (selectedImageFile != null) {
                    oeuvreService.saveImage(oeuvre.getId(), selectedImageFile);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", isEditMode ? "Œuvre modifiée !" : "Œuvre créée !");
            clearForm();
            isEditMode = false;
            showListView();
            loadOeuvres();
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
        if (titleField != null)
            titleField.clear();
        if (typeCombo != null)
            typeCombo.setValue(null);
        if (authorField != null)
            authorField.clear();
        if (descriptionArea != null)
            descriptionArea.clear();
        if (formImageView != null) {
            formImageView.setImage(null);
            formImageView.setVisible(false);
        }
        selectedImageFile = null;
        if (formError != null)
            formError.setVisible(false);
    }

    private boolean hasMinimumLetters(String text, int minLetters) {
        if (text == null) {
            return false;
        }
        String lettersOnly = text.replaceAll("[^\\p{L}]", "");
        return lettersOnly.length() >= minLetters;
    }

    public void showOeuvreDetail(int oeuvreId) {
        Task<Oeuvre> task = new Task<>() {
            @Override
            protected Oeuvre call() throws SQLException {
                return oeuvreService.selectById(oeuvreId);
            }
        };
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                currentOeuvre = task.getValue();
                if (titleLabel != null)
                    titleLabel.setText(currentOeuvre.getTitle());
                if (typeLabel != null)
                    typeLabel.setText(currentOeuvre.getType());
                if (authorLabel != null)
                    authorLabel.setText(currentOeuvre.getAuthor());
                if (descriptionDetailArea != null)
                    descriptionDetailArea.setText(currentOeuvre.getDescription());

                if (currentOeuvre.getImageUrl() != null && !currentOeuvre.getImageUrl().isEmpty()
                        && detailImageView != null) {
                    try {
                        detailImageView.setImage(new Image(currentOeuvre.getImageUrl()));
                    } catch (Exception ex) {
                        detailImageView.setImage(null);
                    }
                } else if (detailImageView != null) {
                    detailImageView.setImage(null);
                }

                boolean canEdit = UserSession.isLoggedIn() &&
                        (UserSession.getCurrentUser().isAdmin() ||
                                (currentOeuvre.getCreateurId() > 0 &&
                                        currentOeuvre.getCreateurId() == UserSession.getCurrentUser().getId()));
                if (editBtn != null)
                    editBtn.setVisible(canEdit);
                if (deleteBtn != null)
                    deleteBtn.setVisible(canEdit);

                loadComments();
                showDetailView();
            }
        });
        task.setOnFailed(e -> showAlert("Erreur", "Impossible de charger l'œuvre."));
        new Thread(task).start();
    }

    private void loadComments() {
        if (currentOeuvre == null || commentList == null)
            return;
        Task<List<Commentaire>> task = new Task<>() {
            @Override
            protected List<Commentaire> call() throws SQLException {
                return commentaireService.findByOeuvre(currentOeuvre.getId());
            }
        };
        task.setOnSucceeded(e -> commentList.setItems(FXCollections.observableArrayList(task.getValue())));
        new Thread(task).start();
    }

    @FXML
    private void editOeuvre() {
        isEditMode = true;
        selectedImageFile = null;
        if (formTitle != null)
            formTitle.setText("Modifier l'Œuvre");
        if (submitBtn != null)
            submitBtn.setText("Mettre à jour");
        if (titleField != null)
            titleField.setText(currentOeuvre.getTitle());
        if (typeCombo != null)
            typeCombo.setValue(currentOeuvre.getType());
        if (authorField != null)
            authorField.setText(currentOeuvre.getAuthor());
        if (descriptionArea != null)
            descriptionArea.setText(currentOeuvre.getDescription());

        if (currentOeuvre.getImageUrl() != null && !currentOeuvre.getImageUrl().isEmpty()
                && formImageView != null) {
            try {
                formImageView.setImage(new Image(currentOeuvre.getImageUrl()));
                formImageView.setVisible(true);
            } catch (Exception ex) {
            }
        }
        if (formError != null)
            formError.setVisible(false);
        showFormView();
    }

    @FXML
    private void deleteOeuvre() {
        if (currentOeuvre == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'œuvre");
        confirm.setContentText("Supprimer " + currentOeuvre.getTitle() + " ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        oeuvreService.delete(currentOeuvre.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    showAlert("Succès", "Œuvre supprimée");
                    showListView();
                    loadOeuvres();
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
        comment.setOeuvreId(currentOeuvre.getId());
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
        loadOeuvres();
    }
}