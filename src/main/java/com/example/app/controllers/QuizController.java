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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

public class QuizController extends BaseController {

    @FXML private Label questionText;
    @FXML private ToggleGroup reponseGroup;
    @FXML private RadioButton option1;
    @FXML private RadioButton option2;
    @FXML private RadioButton option3;
    @FXML private RadioButton option4;
    @FXML private Label stepLabel;
    @FXML private Button nextBtn;
    @FXML private Button prevBtn;

    private QuestionService questionService = new QuestionService();
    private ReponseService reponseService = new ReponseService();

    private List<Question> questions;
    private int currentIndex = 0;
    private List<String> selectedTags;

    @FXML
    public void initialize() {
        loadQuestions();
    }

    private void loadQuestions() {
        Task<List<Question>> task = new Task<>() {
            @Override
            protected List<Question> call() throws SQLException {
                return questionService.select();
            }
        };
        task.setOnSucceeded(e -> {
            questions = task.getValue();
            if (questions != null && !questions.isEmpty()) {
                showQuestion(0);
            }
        });
        new Thread(task).start();
    }

    private void showQuestion(int index) {
        if (questions == null || index >= questions.size()) return;

        Question q = questions.get(index);
        questionText.setText(q.getQuestion());
        stepLabel.setText("Question " + (index + 1) + " / " + questions.size());

        // Charger les réponses
        Task<List<Reponse>> task = new Task<>() {
            @Override
            protected List<Reponse> call() throws SQLException {
                return reponseService.findByQuestion(q.getId());
            }
        };
        task.setOnSucceeded(e -> {
            List<Reponse> reponses = task.getValue();
            RadioButton[] buttons = {option1, option2, option3, option4};
            for (int i = 0; i < buttons.length; i++) {
                if (i < reponses.size()) {
                    buttons[i].setText(reponses.get(i).getOption());
                    buttons[i].setUserData(reponses.get(i).getTag());
                    buttons[i].setVisible(true);
                } else {
                    buttons[i].setVisible(false);
                }
            }
            reponseGroup.selectToggle(null);
        });
        new Thread(task).start();

        prevBtn.setDisable(index == 0);
        nextBtn.setText(index == questions.size() - 1 ? "Terminer" : "Suivant");
    }

    @FXML
    private void next() {
        RadioButton selected = (RadioButton) reponseGroup.getSelectedToggle();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une réponse");
            return;
        }

        String tag = (String) selected.getUserData();
        // Stocker le tag pour le résultat final
        if (selectedTags == null) selectedTags = new java.util.ArrayList<>();
        selectedTags.add(tag);

        if (currentIndex + 1 < questions.size()) {
            currentIndex++;
            showQuestion(currentIndex);
        } else {
            finishQuiz();
        }
    }

    @FXML
    private void prev() {
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion(currentIndex);
        }
    }

    private void finishQuiz() {
        // Analyser les tags et déterminer le résultat
        String result = analyzeResults();
        showAlert("Résultat du quiz", "Votre profil : " + result);
        navigateTo("/");
    }

    private String analyzeResults() {
        // Logique d'analyse des tags
        if (selectedTags == null || selectedTags.isEmpty()) return "Explorateur";

        long fantasyCount = selectedTags.stream().filter(t -> t.contains("#fantasy")).count();
        long adventureCount = selectedTags.stream().filter(t -> t.contains("#aventure")).count();

        if (fantasyCount > adventureCount) return "Fantasy Épique";
        return "Aventurier";
    }
}