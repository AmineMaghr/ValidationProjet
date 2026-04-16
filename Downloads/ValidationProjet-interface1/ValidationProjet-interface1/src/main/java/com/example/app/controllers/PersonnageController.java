package com.example.app.controllers;
import java.io.ByteArrayInputStream;
import com.example.app.dao.AdvancedPreferenceDAO;
import com.example.app.dao.ArtefactDAO;
import com.example.app.dao.CommandeDAO;
import com.example.app.dao.CommentaireDAO;
import com.example.app.dao.DefiDAO;
import com.example.app.dao.FavorisDAO;
import com.example.app.dao.OeuvreDAO;
import com.example.app.dao.ParticipationDAO;
import com.example.app.dao.PersonnageDAO;
import com.example.app.dao.ProduitDAO;
import com.example.app.dao.QuestionDAO;
import com.example.app.dao.ReponseDAO;
import com.example.app.dao.UniverseDAO;
import com.example.app.dao.UserDAO;
import com.example.app.dao.IDAO;
import com.example.app.entities.Personnage;
import com.example.app.entities.Universe;
import com.example.app.services.PersonnageService;
import com.example.app.services.UniverseService;
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

public class PersonnageController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ListView<String> classRoleFilter;
    @FXML private ListView<String> universeFilter;
    @FXML private ComboBox<String> sortCombo;
    @FXML private ListView<Personnage> personnageList;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> classRoleCombo;
    @FXML private ComboBox<String> universeCombo;
    @FXML private ImageView portraitView;
    @FXML private Button selectImageBtn;
    @FXML private Button saveBtn;
    @FXML private Button deleteBtn;

    private PersonnageService personnageService = new PersonnageService();
    private UniverseService universeService = new UniverseService();

    private ObservableList<Personnage> personnages = FXCollections.observableArrayList();
    private ObservableList<String> universes = FXCollections.observableArrayList();
    private ObservableList<String> classRoles = FXCollections.observableArrayList(
            "Guerrier", "Mage", "Voleur", "Prêtre", "Druide", "Paladin", "Chasseur"
    );
    private Personnage currentPersonnage;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        personnageList.setItems(personnages);
        
        personnageList.setCellFactory(param -> new ListCell<Personnage>() {
            @Override
            protected void updateItem(Personnage p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null || p.getNom() == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(5);
                    card.setStyle("-fx-background-color: #2a3139; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #3b424b; -fx-border-radius: 10;");
                    
                    javafx.scene.control.Label nameLbl = new javafx.scene.control.Label(p.getNom());
                    nameLbl.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 16px; -fx-font-weight: bold;");
                    
                    javafx.scene.control.Label classLbl = new javafx.scene.control.Label("Classe: " + (p.getClassRole() != null ? p.getClassRole() : "N/A"));
                    classLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12px;");
                    
                    javafx.scene.control.Label univLbl = new javafx.scene.control.Label("Univers: " + (p.getUnivers() != null ? p.getUnivers() : "N/A"));
                    univLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12px;");
                    
                    card.getChildren().addAll(nameLbl, classLbl, univLbl);
                    setGraphic(card);
                    setStyle("-fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        classRoleFilter.setItems(classRoles);
        universeFilter.setItems(universes);
        classRoleCombo.setItems(classRoles);
        universeCombo.setItems(universes);
        sortCombo.setItems(FXCollections.observableArrayList("Nom", "Récents"));
        sortCombo.setValue("Récents");
        
        sortCombo.setOnAction(e -> loadPersonnages());
        classRoleFilter.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> loadPersonnages());
        universeFilter.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> loadPersonnages());

        personnageList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadPersonnageDetails(selected);
            }
        });

        loadPersonnages();
        loadUniverses();
    }

    private void loadPersonnages() {
        Task<List<Personnage>> task = new Task<>() {
            @Override
            protected List<Personnage> call() throws SQLException {
                String search = searchField.getText();
                List<String> selectedClassRoles = classRoleFilter.getSelectionModel().getSelectedItems();
                List<String> selectedUniverses = universeFilter.getSelectionModel().getSelectedItems();
                String sortVal = sortCombo.getValue();
                String sort = "Nom".equals(sortVal) ? "name" : "createdAt";
                return personnageService.searchPersonnages(search, selectedClassRoles, selectedUniverses, sort);
            }
        };
        task.setOnSucceeded(e -> personnages.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void loadUniverses() {
        Task<List<Universe>> task = new Task<>() {
            @Override
            protected List<Universe> call() throws SQLException {
                return universeService.select();
            }
        };
        task.setOnSucceeded(e -> {
            universes.clear();
            for (Universe u : task.getValue()) {
                universes.add(u.getName());
            }
        });
        new Thread(task).start();
    }

    private void loadPersonnageDetails(Personnage personnage) {
        currentPersonnage = personnage;
        nameField.setText(personnage.getNom());
        descriptionArea.setText(personnage.getDescription());
        classRoleCombo.setValue(personnage.getClassRole());
        universeCombo.setValue(personnage.getUnivers());

        if (personnage.getPortraitImage() != null && personnage.getPortraitImage().length > 0) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(personnage.getPortraitImage());
                portraitView.setImage(new Image(bis));
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void search() { loadPersonnages(); }

    @FXML
    private void resetFilters() {
        searchField.clear();
        classRoleFilter.getSelectionModel().clearSelection();
        universeFilter.getSelectionModel().clearSelection();
        sortCombo.setValue("createdAt");
        loadPersonnages();
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            portraitView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void prepareNewPersonnage() {
        clearForm();
        personnageList.getSelectionModel().clearSelection();
    }

    @FXML
    private void createPersonnage() {
        if (currentPersonnage != null) {
            updatePersonnage();
            return;
        }

        if (!validateForm()) return;

        Personnage personnage = new Personnage();
        populatePersonnage(personnage);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                personnageService.add(personnage);
                if (selectedImageFile != null) {
                    personnageService.savePortrait(personnage.getId(), selectedImageFile);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Personnage créé !");
            clearForm();
            loadPersonnages();
        });
        new Thread(task).start();
    }

    @FXML
    private void updatePersonnage() {
        if (currentPersonnage == null) return;
        if (!validateForm()) return;

        populatePersonnage(currentPersonnage);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                personnageService.update(currentPersonnage);
                if (selectedImageFile != null) {
                    personnageService.savePortrait(currentPersonnage.getId(), selectedImageFile);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Personnage modifié !");
            loadPersonnages();
        });
        new Thread(task).start();
    }

    @FXML
    private void deletePersonnage() {
        if (currentPersonnage == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Supprimer " + currentPersonnage.getNom() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        personnageService.delete(currentPersonnage.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    clearForm();
                    loadPersonnages();
                    showAlert("Succès", "Personnage supprimé");
                });
                new Thread(task).start();
            }
        });
    }

    private boolean validateForm() {
        if (nameField.getText().isEmpty()) {
            showAlert("Erreur", "Le nom est requis");
            return false;
        }
        if (classRoleCombo.getValue() == null) {
            showAlert("Erreur", "La classe est requise");
            return false;
        }
        return true;
    }

    private void populatePersonnage(Personnage personnage) {
        personnage.setNom(nameField.getText());
        personnage.setDescription(descriptionArea.getText());
        personnage.setClassRole(classRoleCombo.getValue());
        personnage.setUnivers(universeCombo.getValue());
    }

    private void clearForm() {
        currentPersonnage = null;
        nameField.clear();
        descriptionArea.clear();
        classRoleCombo.setValue(null);
        universeCombo.setValue(null);
        portraitView.setImage(null);
        selectedImageFile = null;
    }
}

