package com.example.app.controllers;

import com.example.app.services.PostDetails;
import com.example.app.services.RankedPost;
import com.example.app.services.WhyResult;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WhyController {

    // ── FXML injections ──────────────────────────────────────────────────

    @FXML private Label    lblTypeLabel;
    @FXML private Label    lblTitle;
    @FXML private Label    lblTag;
    @FXML private Label    lblDescription;

    @FXML private Label    lblAiExplanation;

    @FXML private HBox     fillTagSimilarity;
    @FXML private HBox     fillEngagement;
    @FXML private HBox     fillRecency;
    @FXML private HBox     fillBehavior;

    @FXML private FlowPane prefTagsPane;
    @FXML private FlowPane behaviorTagsPane;

    private Scene previousScene;

    // ═══════════════════════════════════════════════════════════════════════
    // Data injection
    // ═══════════════════════════════════════════════════════════════════════

    public void setData(RankedPost post,
                        PostDetails postDetails,
                        WhyResult whyResult,
                        List<String> topPreferenceTags,
                        List<String> topBehaviorTags) {

        // ── Post card ─────────────────────────────────────────────────────
        lblTypeLabel.setText(postDetails.getTypeLabel());
        lblTitle.setText(postDetails.getTitle());
        lblTag.setText(post.getTag());
        lblDescription.setText(postDetails.getDescription() != null
                ? postDetails.getDescription() : "");

        // ── AI explanation fade-in ────────────────────────────────────────
        String explanation = (whyResult != null) ? whyResult.getExplanation() : "";
        lblAiExplanation.setText(explanation);
        lblAiExplanation.setOpacity(0);
        lblAiExplanation.setTranslateY(4);
        FadeTransition ft = new FadeTransition(Duration.millis(320), lblAiExplanation);
        ft.setFromValue(0); ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(320), lblAiExplanation);
        tt.setFromY(4); tt.setToY(0);
        new ParallelTransition(ft, tt).play();

        // ── Scores: prefer WhyResult, fall back to post breakdown ─────────
        Map<String, Double> b = (whyResult != null && whyResult.getScores() != null)
                ? whyResult.getScores()
                : post.getScoreBreakdown();

        double scoreTag      = clamp(b.getOrDefault("tag_similarity",      0.0));
        double scoreEngage   = clamp(b.getOrDefault("engagement_score",    0.0));
        double scoreRecency  = clamp(b.getOrDefault("recency_decay",       0.0));
        double scoreBehavior = clamp(b.getOrDefault("behavior_similarity", 0.0));

        // Defer bar animation until after first layout pass
        Platform.runLater(() -> {
            animateFill(fillTagSimilarity, scoreTag);
            animateFill(fillEngagement,    scoreEngage);
            animateFill(fillRecency,       scoreRecency);
            animateFill(fillBehavior,      scoreBehavior);
        });

        // ── Chips ─────────────────────────────────────────────────────────
        populateChips(prefTagsPane,     topPreferenceTags, "Aucun tag de préférence.");
        populateChips(behaviorTagsPane, topBehaviorTags,   "Aucun signal comportemental.");
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FXML handlers
    // ═══════════════════════════════════════════════════════════════════════

    @FXML
    private void handleBack() {
        if (previousScene != null) {
            Stage stage = (Stage) lblTitle.getScene().getWindow();
            stage.setScene(previousScene);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Bar animation — FIXED
    // ═══════════════════════════════════════════════════════════════════════

    private void animateFill(HBox fill, double score) {
        fill.setPrefWidth(0);
        fill.setMaxWidth(0);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        StackPane track = (fill.getParent() instanceof StackPane sp) ? sp : null;

        if (track != null && track.getWidth() > 0) {
            // Layout already settled — run after a single pulse
            PauseTransition delay = new PauseTransition(Duration.millis(30));
            delay.setOnFinished(e -> runFillAnimation(fill, track.getWidth(), score));
            delay.play();
        } else if (track != null) {
            // Track exists but width not yet computed
            track.widthProperty().addListener((obs, oldW, newW) -> {
                if (newW.doubleValue() > 0 && fill.getPrefWidth() == 0) {
                    runFillAnimation(fill, newW.doubleValue(), score);
                }
            });
        } else {
            // Parent not yet attached to scene graph
            fill.parentProperty().addListener((obs, oldP, newP) -> {
                if (newP instanceof StackPane sp) {
                    if (sp.getWidth() > 0) {
                        runFillAnimation(fill, sp.getWidth(), score);
                    } else {
                        sp.widthProperty().addListener((wObs, oldW, newW) -> {
                            if (newW.doubleValue() > 0 && fill.getPrefWidth() == 0) {
                                runFillAnimation(fill, newW.doubleValue(), score);
                            }
                        });
                    }
                }
            });
        }
    }

    private void runFillAnimation(HBox fill, double trackWidth, double score) {
        double target = trackWidth * score;
        Timeline anim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(fill.prefWidthProperty(), 0),
                        new KeyValue(fill.maxWidthProperty(),  0)),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(fill.prefWidthProperty(), target, Interpolator.EASE_OUT),
                        new KeyValue(fill.maxWidthProperty(),  target, Interpolator.EASE_OUT))
        );
        anim.play();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Chip helper
    // ═══════════════════════════════════════════════════════════════════════

    private void populateChips(FlowPane pane, List<String> tags, String emptyMessage) {
        pane.getChildren().clear();
        if (tags == null || tags.isEmpty()) {
            Label empty = new Label(emptyMessage);
            empty.getStyleClass().add("empty-label");
            pane.getChildren().add(empty);
        } else {
            for (String tag : tags) {
                Label chip = new Label(tag);
                chip.getStyleClass().add("chip");
                pane.getChildren().add(chip);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Static factory
    // ═══════════════════════════════════════════════════════════════════════

    public static void navigateTo(Stage stage,
                                  RankedPost post,
                                  PostDetails postDetails,
                                  WhyResult whyResult,
                                  List<String> topPreferenceTags,
                                  List<String> topBehaviorTags) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    WhyController.class.getResource("/com/monapp/view/why.fxml"));
            Parent root = loader.load();

            WhyController ctrl = loader.getController();
            ctrl.setPreviousScene(stage.getScene());
            ctrl.setData(post, postDetails, whyResult, topPreferenceTags, topBehaviorTags);

            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ── Utility ──────────────────────────────────────────────────────────

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}