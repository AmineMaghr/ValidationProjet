package com.example.app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.app.entities.Question;
import com.example.app.entities.Reponse;
import com.example.app.services.QuestionService;
import com.example.app.services.ReponseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;

public class AdminQuizQuestionsController extends BaseController {

    @FXML private Label questionsTotalLabel;
    @FXML private Label answersTotalLabel;
    @FXML private Label tagsTotalLabel;
    @FXML private Label averageLabel;

    @FXML private HBox successAlert;
    @FXML private Label successAlertText;
    @FXML private HBox errorAlert;
    @FXML private Label errorAlertText;

    @FXML private VBox questionFormCard;
    @FXML private Label formHeaderIcon;
    @FXML private Label formTitleLabel;
    @FXML private Label formSubtitleLabel;
    @FXML private TextArea questionTextArea;
    @FXML private Button saveQuestionButton;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<Question, Integer> idColumn;
    @FXML private TableColumn<Question, String> questionColumn;
    @FXML private TableColumn<Question, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<Question, Void> actionsColumn;
    @FXML private TableColumn<Question, String> tagsColumn; // Maybe null now

    @FXML private VBox responseFormCard;
    @FXML private Label responseFormTitleLabel;
    @FXML private Button saveResponseButton;
    @FXML private ComboBox<Question> questionComboBox;
    @FXML private TextField tagField;
    @FXML private TextField responseOptionTextArea; // Changed to TextField per FXML

    @FXML private TableView<Reponse> reponsesTable;
    @FXML private TableColumn<Reponse, Integer> repIdColumn;
    @FXML private TableColumn<Reponse, String> repOptionColumn;
    @FXML private TableColumn<Reponse, String> repTagColumn;
    @FXML private TableColumn<Reponse, String> repQuestionColumn;
    @FXML private TableColumn<Reponse, Void> repActionsColumn;

    @FXML private ScrollPane mainScrollPane;

    private final QuestionService questionService = new QuestionService();
    private final ReponseService reponseService = new ReponseService();
    
    private ObservableList<Question> questionObservableList = FXCollections.observableArrayList();
    private ObservableList<Reponse> reponseObservableList = FXCollections.observableArrayList();
    
    private Question currentEditQuestion = null;
    private Reponse currentEditReponse = null;

    @FXML
    public void initialize() {
        // Init columns for Questions
        if (idColumn != null) idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (questionColumn != null) questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        
        if (createdAtColumn != null) {
            createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            createdAtColumn.setCellFactory(column -> new TableCell<Question, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            });
        }

        if (actionsColumn != null) {
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteBtn = new Button("Supprimer");
                private final Button editBtn = new Button("Éditer");
                private final HBox actionBox = new HBox(10, editBtn, deleteBtn);

                {
                    deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
                    editBtn.setStyle("-fx-background-color: #3bca95; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
                    actionBox.setStyle("-fx-alignment: center-left;");

                    editBtn.setOnAction(e -> {
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            Question question = getTableView().getItems().get(getIndex());
                            startEditQuestion(question);
                        }
                    });

                    deleteBtn.setOnAction(e -> {
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            Question question = getTableView().getItems().get(getIndex());
                            deleteQuestion(question.getId());
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionBox);
                    }
                }
            });
        }

        // Init columns for Réponses
        if (repIdColumn != null) repIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (repOptionColumn != null) repOptionColumn.setCellValueFactory(new PropertyValueFactory<>("option"));
        if (repTagColumn != null) {
            repTagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
            repTagColumn.setCellFactory(tc -> new TableCell<Reponse, String>() {
                private final Label tagLabel = new Label();
                {
                    tagLabel.setStyle("-fx-background-color: #2c2534; -fx-text-fill: #b388ff; -fx-padding: 3 8; -fx-background-radius: 12;");
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.trim().isEmpty()) {
                        setGraphic(null);
                    } else {
                        tagLabel.setText("#" + item.trim());
                        setGraphic(tagLabel);
                    }
                }
            });
        }
        if (repQuestionColumn != null) repQuestionColumn.setCellValueFactory(cellData -> {
            Question q = cellData.getValue().getQuestion();
            if (q != null) {
                return new SimpleStringProperty("#" + q.getId() + " - " + q.getQuestion());
            }
            return new SimpleStringProperty("");
        });

        if (repActionsColumn != null) {
            repActionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteBtn = new Button("Supprimer");
                private final Button editBtn = new Button("Éditer");
                private final HBox actionBox = new HBox(10, editBtn, deleteBtn);

                {
                    deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
                    editBtn.setStyle("-fx-background-color: #3bca95; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
                    actionBox.setStyle("-fx-alignment: center-left;");

                    editBtn.setOnAction(e -> {
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            Reponse reponse = getTableView().getItems().get(getIndex());
                            startEditReponse(reponse);
                        }
                    });

                    deleteBtn.setOnAction(e -> {
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            Reponse reponse = getTableView().getItems().get(getIndex());
                            deleteReponse(reponse.getId());
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionBox);
                    }
                }
            });
        }

        if (questionsTable != null) {
            questionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            questionsTable.setItems(questionObservableList);
        }
        if (reponsesTable != null) {
            reponsesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            reponsesTable.setItems(reponseObservableList);
        }

        if (questionComboBox != null) {
            questionComboBox.setConverter(new StringConverter<Question>() {
                @Override
                public String toString(Question q) {
                    if (q == null) return null;
                    return "#" + q.getId() + " - " + q.getQuestion();
                }
                @Override
                public Question fromString(String string) {
                    return null; // Not needed
                }
            });
        }

        // Add Listeners for dynamic search and sort
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchQuestions();
            });
        }

        if (sortComboBox != null) {
            sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                searchQuestions();
            });
        }

        loadQuestions();
        loadReponses();
    }

    private void loadQuestions() {
        try {
            // Use searchAndSort with empty params to enforce "Plus rcent" default ordering
            String searchStr = searchField != null ? searchField.getText() : null;
            String sortStr = sortComboBox != null ? sortComboBox.getValue() : null;
            List<Question> questions = questionService.searchAndSort(searchStr, sortStr);
            questionObservableList.setAll(questions);
            
            if (questionComboBox != null) {
                Question selected = questionComboBox.getValue();
                questionComboBox.setItems(questionObservableList);
                if (selected != null && questionObservableList.contains(selected)) {
                    questionComboBox.setValue(selected);
                }
            }

            questionsTotalLabel.setText(String.valueOf(questionService.countQuestions()));
            answersTotalLabel.setText(String.valueOf(questionService.countReponses()));
            tagsTotalLabel.setText("0");
            averageLabel.setText("0");
        } catch (SQLException e) {
            showError("Erreur SQL lors du chargement des questions : " + e.getMessage());
        }
    }

    private void loadReponses() {
        try {
            List<Reponse> reponses = reponseService.select();
            reponseObservableList.setAll(reponses);
            answersTotalLabel.setText(String.valueOf(reponses.size()));
        } catch (SQLException e) {
            System.err.println("Load Reponses error: " + e.getMessage());
        }
    }

    @FXML private void generatePdf() {
        try {
            com.example.app.services.PdfExportService pdfService = new com.example.app.services.PdfExportService();
            List<Question> questions = questionService.searchAndSort(null, null);
            List<Reponse> reponses = reponseService.select();
            
            String dest = pdfService.generateQuizReport(questions, reponses);
            if (dest != null) {
                showSuccess("PDF généré avec succès dans le dossier Téléchargements.");
            } else {
                showError("Erreur lors de la génération du PDF.");
            }
        } catch (Exception e) {
            showError("Erreur PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteQuestion(int id) {
        try {
            questionService.delete(id);
            showSuccess("Question supprimée avec succès !");
            loadQuestions(); // refresh questions
            loadReponses();  // refresh reponses (if cascade or manual deletion happened)
        } catch (SQLException e) {
            showError("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goUsers() {
        System.out.println("Navigating to Admin Users");
        // navigateTo("/admin/users");
    }

    @FXML
    public void goQuiz() {
        System.out.println("Navigating to Admin Quiz");
        navigateTo("/admin/quiz");
    }

    @FXML
    public void goAccueil() {
        System.out.println("Navigating to Accueil");
        navigateTo("/accueil");
    }

    @FXML
    public void goOeuvres() {
        System.out.println("Navigating to Oeuvres");
        navigateTo("/admin/oeuvres");
    }

    @FXML
    public void goArtefacts() {
        System.out.println("Navigating to Artefacts");
        navigateTo("/admin/artefacts");
    }

    @FXML
    public void goShop() {
        System.out.println("Navigating to Shop");
        navigateTo("/admin/shop");
    }

    @FXML
    public void goChallenges() {
        System.out.println("Navigating to Challenges");
        navigateTo("/admin/challenges");
    }

    @FXML
    public void toggleQuestionForm() {
        boolean isVisible = questionFormCard.isVisible();
        if (!isVisible) {
            currentEditQuestion = null; // C'est un ajout
            if(formTitleLabel!=null) formTitleLabel.setText("Nouvelle question");
            if(formSubtitleLabel!=null) formSubtitleLabel.setText("Créez une nouvelle question pour le quiz");
            saveQuestionButton.setText("Ajouter la question");
            questionTextArea.clear();
        }
        questionFormCard.setVisible(!isVisible);
        questionFormCard.setManaged(!isVisible);
    }
    
    private void startEditQuestion(Question question) {
        currentEditQuestion = question;
        questionFormCard.setVisible(true);
        questionFormCard.setManaged(true);
        if(formTitleLabel!=null) formTitleLabel.setText("Modifier la question #" + question.getId());
        if(formSubtitleLabel!=null) formSubtitleLabel.setText("Modifiez le texte de la question existante");
        saveQuestionButton.setText("Mettre à jour");
        questionTextArea.setText(question.getQuestion());
    }

    @FXML
    public void hideQuestionForm() {
        questionFormCard.setVisible(false);
        questionFormCard.setManaged(false);
        questionTextArea.clear();
        currentEditQuestion = null;
    }

    @FXML
    public void saveQuestion() {
        try {
            if (currentEditQuestion == null) {
                // ADD
                Question q = new Question();
                q.setQuestion(questionTextArea.getText());
                questionService.add(q);
                showSuccess("Question ajoutée avec succès !");
            } else {
                // UPDATE
                currentEditQuestion.setQuestion(questionTextArea.getText());
                questionService.update(currentEditQuestion);
                showSuccess("Question mise à jour avec succès !");
            }
            hideQuestionForm();
            loadQuestions();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Erreur BDD : " + e.getMessage());
        }
    }

    @FXML
    public void toggleResponseForm() {
        boolean isVisible = responseFormCard.isVisible();
        if (!isVisible) {
            currentEditReponse = null;
            if(responseFormTitleLabel != null) responseFormTitleLabel.setText("Nouvelle réponse");
            if(saveResponseButton != null) saveResponseButton.setText("Ajouter la réponse");
            responseOptionTextArea.clear();
            tagField.clear();
            questionComboBox.getSelectionModel().clearSelection();
        }
        responseFormCard.setVisible(!isVisible);
        responseFormCard.setManaged(!isVisible);
    }

    private void startEditReponse(Reponse reponse) {
        currentEditReponse = reponse;
        responseFormCard.setVisible(true);
        responseFormCard.setManaged(true);
        if(responseFormTitleLabel != null) responseFormTitleLabel.setText("Modifier la réponse #" + reponse.getId());
        if(saveResponseButton != null) saveResponseButton.setText("Mettre à jour");
        
        responseOptionTextArea.setText(reponse.getOption());
        tagField.setText(reponse.getTag());
        
        if (reponse.getQuestion() != null) {
            for (Question q : questionObservableList) {
                if (q.getId() == reponse.getQuestion().getId()) {
                    questionComboBox.setValue(q);
                    break;
                }
            }
        }
    }

    @FXML
    public void hideResponseForm() {
        responseFormCard.setVisible(false);
        responseFormCard.setManaged(false);
        currentEditReponse = null;
    }

    @FXML
    public void saveResponse() {
        try {
            String option = responseOptionTextArea.getText();
            String tag = tagField.getText();
            Question question = questionComboBox.getValue();

            if (option == null || option.trim().isEmpty()) {
                showError("L'option ne peut pas tre vide.");
                return;
            }
            if (option.trim().length() < 5) {
                showError("L'option doit contenir au moins 5 caractres.");
                return;
            }
            if (tag != null && !tag.trim().isEmpty() && !tag.trim().startsWith("#")) {
                showError("Le tag doit commencer par un '#'.");
                return;
            }
            if (question == null) {
                showError("Veuillez slectionner une question.");
                return;
            }

            if (currentEditReponse == null) {
                Reponse r = new Reponse();
                r.setOption(option);
                r.setTag(tag);
                r.setQuestion(question);
                reponseService.add(r);
                showSuccess("Réponse ajoutée avec succès !");
            } else {
                currentEditReponse.setOption(option);
                currentEditReponse.setTag(tag);
                currentEditReponse.setQuestion(question);
                reponseService.update(currentEditReponse);
                showSuccess("Réponse mise à jour !");
            }
            hideResponseForm();
            loadReponses();
        } catch (SQLException e) {
            showError("Erreur SQL (Réponse) : " + e.getMessage());
        }
    }

    private void deleteReponse(int id) {
        try {
            reponseService.delete(id);
            showSuccess("Réponse supprimée !");
            loadReponses();
        } catch (SQLException e) {
            showError("Erreur suppression réponse : " + e.getMessage());
        }
    }

    @FXML
    public void searchQuestions() {
        try {
            String searchStr = searchField != null ? searchField.getText() : null;
            String sortStr = sortComboBox != null ? sortComboBox.getValue() : null;
            List<Question> result = questionService.searchAndSort(searchStr, sortStr);
            questionObservableList.setAll(result);
            if (questionsTotalLabel != null) {
                questionsTotalLabel.setText(String.valueOf(questionService.countQuestions()));
            }
            // Optional visually update, omitting explicit success to avoid spamming the UI on every key press
        } catch (SQLException e) {
            showError("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    @FXML
    public void resetSearch() {
        searchField.clear();
        if (sortComboBox != null) {
            sortComboBox.getSelectionModel().clearSelection();
        }
        loadQuestions();
    }

    private void showSuccess(String msg) {
        successAlertText.setText(msg);
        successAlert.setVisible(true);
        successAlert.setManaged(true);
        errorAlert.setVisible(false);
        errorAlert.setManaged(false);
        if (mainScrollPane != null) {
            mainScrollPane.setVvalue(0.0);
        }
    }

    private void showError(String msg) {
        errorAlertText.setText(msg);
        errorAlert.setVisible(true);
        errorAlert.setManaged(true);
        successAlert.setVisible(false);
        successAlert.setManaged(false);
        if (mainScrollPane != null) {
            mainScrollPane.setVvalue(0.0);
        }
    }
}
