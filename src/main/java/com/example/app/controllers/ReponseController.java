package com.example.app.controllers;

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
import com.example.app.entities.Question;
import com.example.app.entities.Reponse;
import com.example.app.services.QuestionService;
import com.example.app.services.ReponseService;
import com.example.app.utils.SceneManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.List;

public class ReponseController extends BaseController {

    @FXML private TableView<Reponse> reponseTable;
    @FXML private TableColumn<Reponse, Integer> colId;
    @FXML private TableColumn<Reponse, String> colOption;
    @FXML private TableColumn<Reponse, String> colTag;
    @FXML private TableColumn<Reponse, String> colQuestion;
    @FXML private ComboBox<Question> questionCombo;
    @FXML private TextField optionField;
    @FXML private TextField tagField;
    @FXML private Button saveBtn;
    @FXML private Button deleteBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLabel;

    private ReponseService reponseService = new ReponseService();
    private QuestionService questionService = new QuestionService();

    private ObservableList<Reponse> reponses = FXCollections.observableArrayList();
    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private Reponse currentReponse;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        setupTable();
        setupComboBox();
        loadReponses();
        loadQuestions();

        // Désactiver les boutons tant qu'aucune sélection
        deleteBtn.setDisable(true);

        // Sélection dans le tableau
        reponseTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadReponseForEdit(selected);
                deleteBtn.setDisable(false);
            } else {
                deleteBtn.setDisable(true);
            }
        });
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colOption.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOption()));
        colTag.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));
        colQuestion.setCellValueFactory(cellData -> {
            Question q = cellData.getValue().getQuestion();
            return new SimpleStringProperty(q != null ? q.getQuestion() : "");
        });

        reponseTable.setItems(reponses);
    }

    private void setupComboBox() {
        questionCombo.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });
        questionCombo.setButtonCell(new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });
        questionCombo.setItems(questions);
    }

    private void loadReponses() {
        Task<List<Reponse>> task = new Task<>() {
            @Override
            protected List<Reponse> call() throws SQLException {
                return reponseService.select();
            }
        };
        task.setOnSucceeded(e -> {
            reponses.setAll(task.getValue());
            reponseTable.refresh();
        });
        task.setOnFailed(e -> showError("Erreur chargement: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadQuestions() {
        Task<List<Question>> task = new Task<>() {
            @Override
            protected List<Question> call() throws SQLException {
                return questionService.select();
            }
        };
        task.setOnSucceeded(e -> questions.setAll(task.getValue()));
        task.setOnFailed(e -> showError("Erreur chargement questions: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadReponseForEdit(Reponse reponse) {
        currentReponse = reponse;
        isEditing = true;

        optionField.setText(reponse.getOption());
        tagField.setText(reponse.getTag());
        questionCombo.setValue(reponse.getQuestion());

        saveBtn.setText("Modifier");
        cancelBtn.setVisible(true);
        errorLabel.setText("");
    }

    private void clearForm() {
        currentReponse = null;
        isEditing = false;
        optionField.clear();
        tagField.clear();
        questionCombo.setValue(null);
        saveBtn.setText("Créer");
        cancelBtn.setVisible(false);
        errorLabel.setText("");
    }

    @FXML
    private void saveReponse() {
        // Validation
        String option = optionField.getText().trim();
        String tag = tagField.getText().trim();
        Question question = questionCombo.getValue();

        StringBuilder errors = new StringBuilder();

        if (option.isEmpty()) {
            errors.append("• L'option ne peut pas être vide\n");
        }

        if (tag.isEmpty()) {
            errors.append("• Le tag ne peut pas être vide\n");
        } else if (!tag.startsWith("#")) {
            errors.append("• Le tag doit commencer par #\n");
        }

        if (question == null) {
            errors.append("• Veuillez sélectionner une question\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return;
        }

        if (isEditing && currentReponse != null) {
            updateReponse(option, tag, question);
        } else {
            createReponse(option, tag, question);
        }
    }

    private void createReponse(String option, String tag, Question question) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                Reponse reponse = new Reponse();
                reponse.setOption(option);
                reponse.setTag(tag);
                reponse.setQuestion(question);
                reponseService.add(reponse);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showSuccess("Réponse créée avec succès");
            clearForm();
            loadReponses();
        });
        task.setOnFailed(e -> showError("Erreur création: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void updateReponse(String option, String tag, Question question) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                currentReponse.setOption(option);
                currentReponse.setTag(tag);
                currentReponse.setQuestion(question);
                reponseService.update(currentReponse);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showSuccess("Réponse modifiée avec succès");
            clearForm();
            loadReponses();
        });
        task.setOnFailed(e -> showError("Erreur modification: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void deleteReponse() {
        Reponse selected = reponseTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la réponse");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer la réponse \"" + selected.getOption() + "\" ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        reponseService.delete(selected.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    showSuccess("Réponse supprimée avec succès");
                    clearForm();
                    loadReponses();
                });
                task.setOnFailed(e -> showError("Erreur suppression: " + task.getException().getMessage()));
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void cancelEdit() {
        clearForm();
        reponseTable.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #ff4444;");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #18E3A4;");

        // Effacer le message après 3 secondes
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    if (!errorLabel.getText().equals(message)) return;
                    errorLabel.setText("");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}