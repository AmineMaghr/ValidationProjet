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

public class QuestionController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, Integer> colId;
    @FXML private TableColumn<Question, String> colQuestion;
    @FXML private TableColumn<Question, String> colCreatedAt;
    @FXML private TextField questionField;
    @FXML private Button saveBtn;
    @FXML private Button deleteBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLabel;
    @FXML private TabPane mainTabPane;
    @FXML private TableView<Reponse> reponseTable;
    @FXML private TableColumn<Reponse, Integer> repColId;
    @FXML private TableColumn<Reponse, String> repColOption;
    @FXML private TableColumn<Reponse, String> repColTag;
    @FXML private TextField optionField;
    @FXML private TextField tagField;
    @FXML private ComboBox<Question> repQuestionCombo;
    @FXML private Button repSaveBtn;
    @FXML private Button repDeleteBtn;
    @FXML private Button repCancelBtn;

    private QuestionService questionService = new QuestionService();
    private ReponseService reponseService = new ReponseService();

    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private ObservableList<Reponse> reponses = FXCollections.observableArrayList();
    private Question currentQuestion;
    private Reponse currentReponse;
    private boolean isEditingQuestion = false;
    private boolean isEditingReponse = false;

    @FXML
    public void initialize() {
        setupQuestionTable();
        setupReponseTable();
        setupComboBoxes();
        loadQuestions();
        loadReponses();

        sortCombo.setItems(FXCollections.observableArrayList(
                "createdAt_desc", "createdAt_asc", "question_asc", "question_desc"
        ));
        sortCombo.setValue("createdAt_desc");

        // Sélection dans le tableau des questions
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadQuestionForEdit(selected);
                deleteBtn.setDisable(false);
                // Filtrer les réponses par question
                filterReponsesByQuestion(selected.getId());
            } else {
                deleteBtn.setDisable(true);
            }
        });

        // Sélection dans le tableau des réponses
        reponseTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadReponseForEdit(selected);
                repDeleteBtn.setDisable(false);
            } else {
                repDeleteBtn.setDisable(true);
            }
        });

        deleteBtn.setDisable(true);
        repDeleteBtn.setDisable(true);
        cancelBtn.setVisible(false);
        repCancelBtn.setVisible(false);
    }

    private void setupQuestionTable() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colQuestion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQuestion()));
        colCreatedAt.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""
        ));
        questionTable.setItems(questions);
    }

    private void setupReponseTable() {
        repColId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        repColOption.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOption()));
        repColTag.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));
        reponseTable.setItems(reponses);
    }

    private void setupComboBoxes() {
        repQuestionCombo.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });
        repQuestionCombo.setButtonCell(new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });
        repQuestionCombo.setItems(questions);
    }

    private void loadQuestions() {
        Task<List<Question>> task = new Task<>() {
            @Override
            protected List<Question> call() throws SQLException {
                String search = searchField.getText();
                String sort = sortCombo.getValue();
                return questionService.searchAndSort(search, sort);
            }
        };
        task.setOnSucceeded(e -> {
            questions.setAll(task.getValue());
            questionTable.refresh();
            repQuestionCombo.setItems(questions);
        });
        task.setOnFailed(e -> showError("Erreur chargement: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadReponses() {
        Task<List<Reponse>> task = new Task<>() {
            @Override
            protected List<Reponse> call() throws SQLException {
                return reponseService.select();
            }
        };
        task.setOnSucceeded(e -> reponses.setAll(task.getValue()));
        task.setOnFailed(e -> showError("Erreur chargement réponses: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void filterReponsesByQuestion(int questionId) {
        Task<List<Reponse>> task = new Task<>() {
            @Override
            protected List<Reponse> call() throws SQLException {
                return reponseService.findByQuestion(questionId);
            }
        };
        task.setOnSucceeded(e -> {
            reponses.setAll(task.getValue());
            reponseTable.refresh();
        });
        new Thread(task).start();
    }

    private void loadQuestionForEdit(Question question) {
        currentQuestion = question;
        isEditingQuestion = true;
        questionField.setText(question.getQuestion());
        saveBtn.setText("Modifier");
        cancelBtn.setVisible(true);
        errorLabel.setText("");
    }

    private void loadReponseForEdit(Reponse reponse) {
        currentReponse = reponse;
        isEditingReponse = true;
        optionField.setText(reponse.getOption());
        tagField.setText(reponse.getTag());
        repQuestionCombo.setValue(reponse.getQuestion());
        repSaveBtn.setText("Modifier");
        repCancelBtn.setVisible(true);
    }

    private void clearQuestionForm() {
        currentQuestion = null;
        isEditingQuestion = false;
        questionField.clear();
        saveBtn.setText("Créer");
        cancelBtn.setVisible(false);
        errorLabel.setText("");
        questionTable.getSelectionModel().clearSelection();
    }

    private void clearReponseForm() {
        currentReponse = null;
        isEditingReponse = false;
        optionField.clear();
        tagField.clear();
        repQuestionCombo.setValue(null);
        repSaveBtn.setText("Créer");
        repCancelBtn.setVisible(false);
        reponseTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void search() {
        loadQuestions();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        sortCombo.setValue("createdAt_desc");
        loadQuestions();
    }

    @FXML
    private void saveQuestion() {
        String questionText = questionField.getText().trim();

        // Validation
        StringBuilder errors = new StringBuilder();

        if (questionText.isEmpty()) {
            errors.append("• La question ne peut pas être vide\n");
        } else if (questionText.length() < 10) {
            errors.append("• La question doit contenir au minimum 10 caractères\n");
        } else if (!questionText.endsWith("?")) {
            errors.append("• La question doit se terminer par un point d'interrogation\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return;
        }

        if (isEditingQuestion && currentQuestion != null) {
            updateQuestion(questionText);
        } else {
            createQuestion(questionText);
        }
    }

    private void createQuestion(String questionText) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                Question question = new Question();
                question.setQuestion(questionText);
                questionService.add(question);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showSuccess("Question créée avec succès");
            clearQuestionForm();
            loadQuestions();
        });
        task.setOnFailed(e -> showError("Erreur création: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void updateQuestion(String questionText) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                currentQuestion.setQuestion(questionText);
                questionService.update(currentQuestion);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showSuccess("Question modifiée avec succès");
            clearQuestionForm();
            loadQuestions();
        });
        task.setOnFailed(e -> showError("Erreur modification: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void deleteQuestion() {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la question");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette question ?\nToutes les réponses associées seront également supprimées.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        questionService.delete(selected.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    showSuccess("Question supprimée avec succès");
                    clearQuestionForm();
                    loadQuestions();
                    loadReponses();
                });
                task.setOnFailed(e -> showError("Erreur suppression: " + task.getException().getMessage()));
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void saveReponse() {
        String option = optionField.getText().trim();
        String tag = tagField.getText().trim();
        Question question = repQuestionCombo.getValue();

        // Validation
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

        if (isEditingReponse && currentReponse != null) {
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
            clearReponseForm();
            if (question != null) {
                filterReponsesByQuestion(question.getId());
            }
            loadReponses();
        });
        task.setOnFailed(e -> showError("Erreur création réponse: " + task.getException().getMessage()));
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
            clearReponseForm();
            if (question != null) {
                filterReponsesByQuestion(question.getId());
            }
            loadReponses();
        });
        task.setOnFailed(e -> showError("Erreur modification réponse: " + task.getException().getMessage()));
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
                    clearReponseForm();
                    if (currentQuestion != null) {
                        filterReponsesByQuestion(currentQuestion.getId());
                    }
                    loadReponses();
                });
                task.setOnFailed(e -> showError("Erreur suppression réponse: " + task.getException().getMessage()));
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void cancelQuestionEdit() {
        clearQuestionForm();
    }

    @FXML
    private void cancelReponseEdit() {
        clearReponseForm();
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

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    if (errorLabel.getText().equals(message)) {
                        errorLabel.setText("");
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}