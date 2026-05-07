package com.example.app.controllers;

import com.example.app.entities.Question;
import com.example.app.entities.Reponse;
import com.example.app.services.QuestionService;
import com.example.app.services.ReponseService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizController extends BaseController {

    @FXML private Label questionNumberLabel;
    @FXML private Label scoreLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressTextLabel;
    @FXML private Label questionTextLabel;
    @FXML private GridPane answersContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button submitBtn;

    private final QuestionService questionService = new QuestionService();
    private final ReponseService reponseService = new ReponseService();
    private final ToggleGroup answersGroup = new ToggleGroup();

    private List<Question> questions = new ArrayList<>();
    private final List<String> selectedTags = new ArrayList<>();
    private int currentIndex = 0;

    @FXML
    public void initialize() {
        if (scoreLabel != null) {
            scoreLabel.setText("0");
        }
        loadQuestions();
    }

    private void loadQuestions() {
        try {
            questions = questionService.select();
            if (questions == null || questions.isEmpty()) {
                if (questionTextLabel != null) questionTextLabel.setText("Aucune question disponible pour le moment.");
                if (previousButton != null) previousButton.setDisable(true);
                if (nextButton != null) nextButton.setDisable(true);
                if (questionNumberLabel != null) questionNumberLabel.setText("Question 0/0");
                if (progressTextLabel != null) progressTextLabel.setText("Question 0 de 0");
                if (progressBar != null) progressBar.setProgress(0);
                return;
            }
            showQuestion(0);
        } catch (SQLException e) {
            questionTextLabel.setText("Erreur de chargement du quiz: " + e.getMessage());
            previousButton.setDisable(true);
            nextButton.setDisable(true);
        }
    }

    private void showQuestion(int index) {
        if (questions == null || questions.isEmpty() || index < 0 || index >= questions.size()) {
            return;
        }

        Question question = questions.get(index);
        if (questionTextLabel != null) questionTextLabel.setText(question.getQuestion());
        if (questionNumberLabel != null) questionNumberLabel.setText("Question " + (index + 1) + "/" + questions.size());
        if (progressTextLabel != null) progressTextLabel.setText("Question " + (index + 1) + " de " + questions.size());
        if (progressBar != null) progressBar.setProgress((double) (index + 1) / questions.size());

        loadAnswers(question.getId());

        if (previousButton != null) previousButton.setDisable(index == 0);
        
        if (index == questions.size() - 1) {
            if (nextButton != null) { nextButton.setVisible(false); nextButton.setManaged(false); }
            if (submitBtn != null) { submitBtn.setVisible(true); submitBtn.setManaged(true); }
        } else {
            if (nextButton != null) { nextButton.setVisible(true); nextButton.setManaged(true); }
            if (submitBtn != null) { submitBtn.setVisible(false); submitBtn.setManaged(false); }
        }
    }

    private void loadAnswers(int questionId) {
        answersContainer.getChildren().clear();
        answersGroup.selectToggle(null);

        try {
            List<Reponse> reponses = reponseService.findByQuestion(questionId);
            int row = 0;
            int col = 0;
            for (Reponse reponse : reponses) {
                javafx.scene.control.ToggleButton option = new javafx.scene.control.ToggleButton(reponse.getOption());
                option.setUserData(reponse.getTag());
                option.setToggleGroup(answersGroup);
                option.setWrapText(true);
                option.setMinHeight(80);
                option.getStyleClass().add("quiz-option");
                option.setMaxWidth(Double.MAX_VALUE);
                
                GridPane.setHgrow(option, javafx.scene.layout.Priority.ALWAYS);
                answersContainer.add(option, col, row);
                
                col++;
                if (col > 1) {
                    col = 0;
                    row++;
                }
            }

            if (reponses.isEmpty()) {
                Label emptyLabel = new Label("Aucune reponse disponible pour cette question.");
                emptyLabel.setStyle("-fx-text-fill: #B0B9B6;");
                answersContainer.getChildren().add(emptyLabel);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur de chargement des reponses: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;");
            answersContainer.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void goToPreviousQuestion() {
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion(currentIndex);
        }
    }

    @FXML
    private void goToNextQuestion() {
        javafx.scene.control.ToggleButton selected = (javafx.scene.control.ToggleButton) answersGroup.getSelectedToggle();
        if (selected == null) {
            showAlert("Attention", "Veuillez selectionner une reponse");
            return;
        }

        String tag = String.valueOf(selected.getUserData());
        if (tag != null && !tag.isEmpty()) {
            selectedTags.add(tag);
        }

        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        } else {
            finishQuiz();
        }
    }

    @FXML
    private void exitQuiz() {
        navigateTo("/");
    }

    @FXML
    private void goAdvancedPreferences(javafx.event.ActionEvent event) {
        navigateTo("/preferences/advanced");
    }

    private void finishQuiz() {
        javafx.scene.control.ToggleButton selected = (javafx.scene.control.ToggleButton) answersGroup.getSelectedToggle();
        if (selected == null) {
            showAlert("Attention", "Veuillez selectionner une reponse");
            return;
        }
        
        String tag = String.valueOf(selected.getUserData());
        if (tag != null && !tag.isEmpty()) {
            selectedTags.add(tag);
        }

        // Save selected tags into user_preferences
        int userId = com.example.app.utils.UserSession.getCurrentUserId();
        try {
            java.sql.Connection conn = com.example.app.utils.MyDatabase.getConnection();
            String joinedTags = String.join(",", selectedTags);
            
            // Check if preference already exists
            String checkSql = "SELECT count(*) FROM user_preferences WHERE user_id = ?";
            boolean exists = false;
            try (java.sql.PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, userId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }
            
            if (exists) {
                String updateSql = "UPDATE user_preferences SET tags = ?, updated_at = NOW() WHERE user_id = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, joinedTags);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO user_preferences (user_id, tags, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, joinedTags);
                    ps.executeUpdate();
                }
            }
            System.out.println("Preferences saved: " + joinedTags);
        } catch (Exception e) {
            System.out.println("Could not save user preferences: " + e.getMessage());
            e.printStackTrace();
        }

        showAlert("Validation du quiz", "Vos préférences ont été mises à jour avec succès.\nTags retenus: " + String.join(", ", selectedTags));
        navigateTo("/");
    }
}
