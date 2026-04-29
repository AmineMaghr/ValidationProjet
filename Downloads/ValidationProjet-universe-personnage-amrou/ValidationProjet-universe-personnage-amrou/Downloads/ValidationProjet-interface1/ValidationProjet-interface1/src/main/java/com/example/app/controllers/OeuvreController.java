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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class OeuvreController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private CheckBox myItemsCheckbox;
    @FXML private ListView<Oeuvre> oeuvreList;
    @FXML private TextField titleField;
    @FXML private TextField typeField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField authorField;
    @FXML private DatePicker datePublicationPicker;
    @FXML private ImageView imageView;
    @FXML private Button selectImageBtn;
    @FXML private Button saveBtn;
    @FXML private Button deleteBtn;
    @FXML private TextArea commentArea;
    @FXML private Button addCommentBtn;
    @FXML private ListView<Commentaire> commentList;

    private OeuvreService oeuvreService = new OeuvreService();
    private CommentaireService commentaireService = new CommentaireService();

    private ObservableList<Oeuvre> oeuvres = FXCollections.observableArrayList();
    private Oeuvre currentOeuvre;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        oeuvreList.setItems(oeuvres);

        oeuvreList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadOeuvreDetails(selected);
            }
        });

        loadOeuvres();
        loadTypes();
    }

    private void loadOeuvres() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws Exception {
                String search = searchField.getText();
                String type = typeFilter.getValue();
                boolean myItems = myItemsCheckbox.isSelected();
                Integer userId = myItems && UserSession.isLoggedIn() ? UserSession.getCurrentUser().getId() : null;
                return oeuvreService.searchOeuvres(search, type, userId);
            }
        };
        task.setOnSucceeded(e -> oeuvres.setAll(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            System.err.println("Erreur: " + (ex != null ? ex.getMessage() : "Unknown"));
        });
        new Thread(task).start();
    }

    private void loadTypes() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return oeuvreService.getAvailableTypes();
            }
        };
        task.setOnSucceeded(e -> typeFilter.setItems(FXCollections.observableArrayList(task.getValue())));
        new Thread(task).start();
    }

    private void loadOeuvreDetails(Oeuvre oeuvre) {
        currentOeuvre = oeuvre;
        titleField.setText(oeuvre.getTitle());
        typeField.setText(oeuvre.getType());
        descriptionArea.setText(oeuvre.getDescription());
        authorField.setText(oeuvre.getAuthor());

        if (oeuvre.getImageUrl() != null && !oeuvre.getImageUrl().isEmpty()) {
            try {
                imageView.setImage(new Image(oeuvre.getImageUrl()));
            } catch (Exception e) {
                System.out.println("Image non trouvée");
            }
        }

        loadComments(oeuvre.getId());
    }

    private void loadComments(int oeuvreId) {
        Task<List<Commentaire>> task = new Task<>() {
            @Override
            protected List<Commentaire> call() throws Exception {
                return commentaireService.findByOeuvre(oeuvreId);
            }
        };
        task.setOnSucceeded(e -> commentList.getItems().setAll(task.getValue()));
        new Thread(task).start();
    }

    @FXML
    private void search() { loadOeuvres(); }

    @FXML
    private void resetFilters() {
        searchField.clear();
        typeFilter.setValue(null);
        myItemsCheckbox.setSelected(false);
        loadOeuvres();
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            imageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void createOeuvre() {
        if (!validateForm()) return;

        Oeuvre oeuvre = new Oeuvre();
        populateOeuvre(oeuvre);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                oeuvreService.add(oeuvre);
                if (selectedImageFile != null) {
                    oeuvreService.saveImage(oeuvre.getId(), selectedImageFile);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Œuvre créée avec succès !");
            clearForm();
            loadOeuvres();
        });
        new Thread(task).start();
    }

    @FXML
    private void updateOeuvre() {
        if (currentOeuvre == null) return;
        if (!validateForm()) return;

        populateOeuvre(currentOeuvre);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                oeuvreService.update(currentOeuvre);
                if (selectedImageFile != null) {
                    oeuvreService.saveImage(currentOeuvre.getId(), selectedImageFile);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Œuvre modifiée !");
            loadOeuvres();
        });
        new Thread(task).start();
    }

    @FXML
    private void deleteOeuvre() {
        if (currentOeuvre == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + currentOeuvre.getTitle() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        oeuvreService.delete(currentOeuvre.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    clearForm();
                    loadOeuvres();
                    showAlert("Succès", "Œuvre supprimée");
                });
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void addComment() {
        String content = commentArea.getText();
        if (content.isEmpty() || currentOeuvre == null) return;

        Commentaire comment = new Commentaire();
        comment.setContenu(content);
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
            loadComments(currentOeuvre.getId());
            showAlert("Succès", "Commentaire ajouté");
        });
        new Thread(task).start();
    }

    private boolean validateForm() {
        if (titleField.getText().isEmpty()) {
            showAlert("Erreur", "Le titre est requis");
            return false;
        }
        if (typeField.getText().isEmpty()) {
            showAlert("Erreur", "Le type est requis");
            return false;
        }
        return true;
    }

    private void populateOeuvre(Oeuvre oeuvre) {
        oeuvre.setTitle(titleField.getText());
        oeuvre.setType(typeField.getText());
        oeuvre.setDescription(descriptionArea.getText());
        oeuvre.setAuthor(authorField.getText());
        if (UserSession.isLoggedIn()) {
            oeuvre.setCreateurId(UserSession.getCurrentUser().getId());
        }
    }

    private void clearForm() {
        currentOeuvre = null;
        titleField.clear();
        typeField.clear();
        descriptionArea.clear();
        authorField.clear();
        datePublicationPicker.setValue(null);
        imageView.setImage(null);
        selectedImageFile = null;
        commentList.getItems().clear();
    }

    // ===== MÉTHODES DE NAVIGATION =====

    @FXML
    private void goToOeuvresList() {
        navigateTo("/oeuvre");
    }

    @FXML
    private void goToCreateOeuvre() {
        navigateTo("/oeuvre/create");
    }
}