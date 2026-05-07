package com.example.app.controllers;

import com.example.app.entities.Defi;
import com.example.app.services.DefiService;
import com.example.app.services.ParticipationService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DefiAnalyticsController extends BaseController {

    @FXML private TableView<DefiScore> analyticsTable;

    @FXML private Label avgScoreLabel, topDefiLabel, worstDefiLabel;
    @FXML private Label keepCountLabel, keepListLabel;
    @FXML private Label improveCountLabel, improveListLabel;
    @FXML private Label deleteCountLabel, deleteListLabel;
    @FXML private BarChart<String, Number> scoreChart;
    @FXML private PieChart topChart;
    @FXML private Label lastUpdateAnalytics;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button refreshButton;

    private final DefiService defiService = new DefiService();
    private final ParticipationService participationService = new ParticipationService();

    private final ObservableList<DefiScore> defiScores = FXCollections.observableArrayList();

    // =========================
    // DATA CLASS (PROPRE)
    // =========================
    public static class DefiScore {

        private final Defi defi;
        private final double score;
        private final String recommendation;
        private final double successRate;
        private final double abandonRate;
        private final int participants;

        public DefiScore(Defi defi, double score, String recommendation,
                         double successRate, double abandonRate, int participants) {
            this.defi = defi;
            this.score = score;
            this.recommendation = recommendation;
            this.successRate = successRate;
            this.abandonRate = abandonRate;
            this.participants = participants;
        }

        public Defi getDefi() { return defi; }
        public double getScore() { return score; }
        public String getRecommendation() { return recommendation; }
        public double getSuccessRate() { return successRate; }
        public double getAbandonRate() { return abandonRate; }
        public int getParticipants() { return participants; }
        public String getTitre() { return defi.getTitre(); }
    }

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        setupTable();
        sortCombo.setValue("Score ↓");
        refreshAnalytics();
    }

    private void setupTable() {

        analyticsTable.getColumns().get(0)
                .setCellValueFactory(new PropertyValueFactory<>("score"));

        analyticsTable.getColumns().get(1)
                .setCellValueFactory(new PropertyValueFactory<>("titre"));

        analyticsTable.getColumns().get(2)
                .setCellValueFactory(new PropertyValueFactory<>("participants"));

        analyticsTable.getColumns().get(3)
                .setCellValueFactory(new PropertyValueFactory<>("successRate"));

        analyticsTable.getColumns().get(4)
                .setCellValueFactory(new PropertyValueFactory<>("abandonRate"));

        analyticsTable.getColumns().get(5)
                .setCellValueFactory(new PropertyValueFactory<>("recommendation"));

        analyticsTable.setItems(defiScores);

        // DOUBLE CLICK ACTION
        analyticsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {

                DefiScore selected = analyticsTable.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    if (selected.getScore() < 4) {
                        deleteDefi(selected.getDefi());
                    } else {
                        editDefi(selected.getDefi());
                    }
                }
            }
        });
    }

    // =========================
    // REFRESH ANALYTICS
    // =========================
    @FXML
    private void refreshAnalytics() {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {

                List<Defi> allDefis = defiService.getAllDefis();

                List<DefiScore> scores = allDefis.stream()
                        .map(DefiAnalyticsController.this::calculateScore)
                        .sorted(Comparator.comparingDouble(DefiScore::getScore).reversed())
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    defiScores.setAll(scores);
                    updateSummary(scores);
                    updateRecommendations(scores);
                    updateCharts(scores);

                    lastUpdateAnalytics.setText(
                            "Dernière analyse: " +
                                    LocalDateTime.now().format(
                                            DateTimeFormatter.ofPattern("HH:mm:ss")
                                    )
                    );
                });

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // =========================
    // SCORE IA (SIMULATION)
    // =========================
    private DefiScore calculateScore(Defi defi) {

        int participants = defi.getParticipations() != null
                ? defi.getParticipations().size()
                : 0;

        double success = participants * 0.8;
        double abandon = participants - success;

        double successRate = participants > 0
                ? (success * 100.0 / participants)
                : 0.0;

        double abandonRate = participants > 0
                ? (abandon * 100.0 / participants)
                : 0.0;

        double score = (participants * 0.3)
                     + (successRate * 0.5)
                     - (abandonRate * 0.2);

        score = Math.max(0, Math.min(10, score));

        String rec;
        if (score >= 7) rec = "GARDER";
        else if (score >= 4) rec = "AMÉLIORER";
        else rec = "SUPPRIMER";

        return new DefiScore(defi, score, rec, successRate, abandonRate, participants);
    }

    // =========================
    // SUMMARY
    // =========================
    private void updateSummary(List<DefiScore> scores) {

        if (scores.isEmpty()) return;

        double avg = scores.stream()
                .mapToDouble(DefiScore::getScore)
                .average()
                .orElse(0);

        DefiScore top = scores.get(0);
        DefiScore worst = scores.get(scores.size() - 1);

        avgScoreLabel.setText(String.format("%.1f", avg));
        topDefiLabel.setText(top.getTitre());
        worstDefiLabel.setText(worst.getTitre());
    }

    // =========================
    // RECOMMENDATIONS
    // =========================
    private void updateRecommendations(List<DefiScore> scores) {

        List<DefiScore> keep = scores.stream()
                .filter(s -> s.getRecommendation().equals("GARDER"))
                .toList();

        List<DefiScore> improve = scores.stream()
                .filter(s -> s.getRecommendation().equals("AMÉLIORER"))
                .toList();

        List<DefiScore> delete = scores.stream()
                .filter(s -> s.getRecommendation().equals("SUPPRIMER"))
                .toList();

        keepCountLabel.setText(String.valueOf(keep.size()));
        improveCountLabel.setText(String.valueOf(improve.size()));
        deleteCountLabel.setText(String.valueOf(delete.size()));

        keepListLabel.setText(keep.stream().map(DefiScore::getTitre).collect(Collectors.joining(", ")));
        improveListLabel.setText(improve.stream().map(DefiScore::getTitre).collect(Collectors.joining(", ")));
        deleteListLabel.setText(delete.stream().map(DefiScore::getTitre).collect(Collectors.joining(", ")));
    }

    // =========================
    // CHARTS
    // =========================
    private void updateCharts(List<DefiScore> scores) {

        scoreChart.getData().clear();

        long low = scores.stream().filter(s -> s.getScore() < 4).count();
        long mid = scores.stream().filter(s -> s.getScore() >= 4 && s.getScore() < 7).count();
        long high = scores.stream().filter(s -> s.getScore() >= 7).count();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>("<4", low));
        series.getData().add(new XYChart.Data<>("4-7", mid));
        series.getData().add(new XYChart.Data<>("≥7", high));

        scoreChart.getData().add(series);

        topChart.setData(
                FXCollections.observableArrayList(
                        scores.stream()
                                .limit(5)
                                .map(s -> new PieChart.Data(
                                        s.getTitre(),
                                        s.getScore()
                                ))
                                .toList()
                )
        );
    }

    // =========================
    // ACTIONS
    // =========================
    @FXML
    private void goBack() {
        navigateTo("/admin/challenges");
    }

    private void deleteDefi(Defi defi) {
        showAlert("Suppression", "Défi supprimé");
        refreshAnalytics();
    }

    private void editDefi(Defi defi) {
        navigateTo("/admin/challenges/" + defi.getId() + "/edit");
    }
}