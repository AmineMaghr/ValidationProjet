package com.example.app.controllers;

import com.example.app.entities.Defi;
import com.example.app.entities.User;
import com.example.app.services.DefiService;
import com.example.app.utils.ConfigManager;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class AdminChallengesController extends BaseController {

    @FXML private Label totalDefisLabel;
    @FXML private Label activeDefisLabel;
    @FXML private Label participantsLabel;
    @FXML private Label openCountLabel;
    @FXML private Label closedCountLabel;
    @FXML private ProgressBar openProgressBar;
    @FXML private ProgressBar closedProgressBar;

    @FXML private PieChart statusChart;
    @FXML private LineChart<String, Number> evolutionChart;

    @FXML private TextField titreField;
    @FXML private TextField themeField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> difficulteCombo;
    @FXML private Button createButton;
    @FXML private Button aiGenerateButton;

    @FXML private StackPane aiLoadingOverlay;
    @FXML private Label aiLoadingText;
    
    @FXML private StackPane difficultyLoadingOverlay;
    @FXML private Button generateDifficultyButton;

    @FXML private VBox formContainer;
@FXML private VBox tableContainer;
@FXML private Button backToDefisBtn;
    @FXML private Button analyticsButton;
    @FXML private VBox emptyState;

    @FXML private TableView<Defi> defisTable;
    @FXML private Label defiCountLabel;
    @FXML private Label tableFooterLabel;
    @FXML private Label lastUpdateLabel;

    private final DefiService defiService = new DefiService();
    private ObservableList<Defi> defis = FXCollections.observableArrayList();
    private File selectedImageFile;
    private Defi editingDefi = null;

    private final String[] AI_LOADING_MESSAGES = {
        "L'IA réfléchit...",
        "Génération en cours...",
        "Création du défi..."
    };

    private final String[] DIFFICULTY_LOADING_MESSAGES = {
        "Analyse de la difficulté...",
        "Évaluation en cours...",
        "Détermination du niveau..."
    };

    @FXML
    private void analyzePerformance() {
        navigateTo("/admin/defi-analytics");
    }

    @FXML
    public void initialize() {
        checkAdminAccess();
        setupUI();
        loadData();
    }

    private void checkAdminAccess() {
        if (!UserSession.isLoggedIn()) {
            User defaultAdmin = new User();
            defaultAdmin.setId(1);
            defaultAdmin.setUsername("admin");
            defaultAdmin.setRole("admin");
            UserSession.setCurrentUser(defaultAdmin);
        }
        
        if (!UserSession.isLoggedIn() || !UserSession.getCurrentUser().isAdmin()) {
            showError("Accès refusé", "Vous n'avez pas accès à cette page");
            navigateTo("/defis");
        }
    }

    private String validateDefiForm() {
        String titre = titreField.getText() != null ? titreField.getText().trim() : "";
        if (titre.isEmpty()) return "Le titre est requis";
        if (titre.length() < 2) return "Le titre doit contenir au moins 2 caractères";
        
        String theme = themeField.getText() != null ? themeField.getText().trim() : "";
        if (theme.isEmpty()) return "Le thème est requis";
        if (theme.length() < 2) return "Le thème doit contenir au moins 2 caractères";
        
        String description = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        if (description.isEmpty()) return "La description est requise";
        if (description.length() < 25) return "La description doit contenir au moins 25 caractères";
        
        if (dateDebutPicker.getValue() == null) return "La date de début est requise";
        if (dateFinPicker.getValue() == null) return "La date de fin est requise";
        if (statusCombo.getValue() == null) return "Le statut est requis";
        if (difficulteCombo.getValue() == null) return "La difficulté est requise";
        
        if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            return "La date de fin doit être postérieure à la date de début";
        }
        
        return null;
    }

    private void setupUI() {
        statusCombo.setItems(FXCollections.observableArrayList("OUVERT", "FERME", "TERMINE", "PLANIFIE"));
        statusCombo.setValue("OUVERT");

        difficulteCombo.setItems(FXCollections.observableArrayList("FACILE", "MOYEN", "DIFFICILE"));
        difficulteCombo.setValue("FACILE");

        setupTableColumns();
        formContainer.setManaged(false);
        formContainer.setVisible(false);

        if (aiLoadingOverlay != null) {
            aiLoadingOverlay.setVisible(false);
            aiLoadingOverlay.setManaged(false);
        }
        
        if (difficultyLoadingOverlay != null) {
            difficultyLoadingOverlay.setVisible(false);
            difficultyLoadingOverlay.setManaged(false);
        }
    }

    private void setupTableColumns() {
        ObservableList<TableColumn<Defi, ?>> columns = defisTable.getColumns();

        @SuppressWarnings("unchecked")
        TableColumn<Defi, Integer> idCol = (TableColumn<Defi, Integer>) columns.get(0);
        idCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getId())
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, String> titreCol = (TableColumn<Defi, String>) columns.get(1);
        titreCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getTitre())
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, String> themeCol = (TableColumn<Defi, String>) columns.get(2);
        themeCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getTheme())
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, String> dateDebutCol = (TableColumn<Defi, String>) columns.get(3);
        dateDebutCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> 
                cellData.getValue().getDateDebut().toString()
            )
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, String> dateFinCol = (TableColumn<Defi, String>) columns.get(4);
        dateFinCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> 
                cellData.getValue().getDateFin().toString()
            )
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, Integer> participantsCol = (TableColumn<Defi, Integer>) columns.get(5);
        participantsCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> 
                cellData.getValue().getParticipations() != null ? 
                cellData.getValue().getParticipations().size() : 0
            )
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, String> difficulteCol = (TableColumn<Defi, String>) columns.get(6);
        difficulteCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> 
                cellData.getValue().getDifficulte() != null ? 
                cellData.getValue().getDifficulte() : "N/A"
            )
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, String> statutCol = (TableColumn<Defi, String>) columns.get(7);
        statutCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(() -> 
                cellData.getValue().getStatut() != null ? 
                cellData.getValue().getStatut().toString() : "N/A"
            )
        );

        @SuppressWarnings("unchecked")
        TableColumn<Defi, Void> actionsCol = (TableColumn<Defi, Void>) columns.get(8);
        actionsCol.setCellFactory(param -> new TableCell<Defi, Void>() {
            private final Button editBtn = new Button("✏");
            private final Button deleteBtn = new Button("🗑");
            private final Button diffBtn = new Button("🎲");
            private final HBox pane = new HBox(editBtn, diffBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #18E3A4; -fx-text-fill: white; -fx-font-size: 10;");
                deleteBtn.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-font-size: 10;");
                diffBtn.setStyle("-fx-background-color: linear-gradient(to right, #8B5CF6, #7C3AED); -fx-text-fill: white; -fx-font-size: 10; -fx-background-radius: 10;");
                pane.setSpacing(5);
                pane.setAlignment(javafx.geometry.Pos.CENTER);

                editBtn.setOnAction(event -> {
                    Defi defi = getTableView().getItems().get(getIndex());
                    editDefi(defi);
                });

                deleteBtn.setOnAction(event -> {
                    Defi defi = getTableView().getItems().get(getIndex());
                    deleteDefi(defi);
                });

                diffBtn.setOnAction(event -> {
                    Defi defi = getTableView().getItems().get(getIndex());
                    generateDifficulty(defi);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        defisTable.setItems(defis);
    }

    private void loadData() {
        Task<List<Defi>> task = new Task<>() {
            @Override
            protected List<Defi> call() throws SQLException {
                return defiService.getAllDefis();
            }

            @Override
            protected void succeeded() {
                List<Defi> loadedDefis = getValue();
                defis.setAll(loadedDefis);
                updateStatistics();
                updateCharts();
                updateTableVisibility();
            }

            @Override
            protected void failed() {
                showError("Erreur", "Impossible de charger les défis");
                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    private void updateStatistics() {
        Platform.runLater(() -> {
            int total = defis.size();
            long active = defis.stream()
                    .filter(d -> "OUVERT".equals(d.getStatut().toString()))
                    .count();
            long closed = defis.stream()
                    .filter(d -> "FERME".equals(d.getStatut().toString()) || "TERMINE".equals(d.getStatut().toString()))
                    .count();
            long participants = defis.stream()
                    .mapToLong(d -> d.getParticipations() != null ? d.getParticipations().size() : 0)
                    .sum();

            totalDefisLabel.setText(String.valueOf(total));
            activeDefisLabel.setText(String.valueOf(active));
            participantsLabel.setText(String.valueOf(participants));

            openCountLabel.setText(String.valueOf(active));
            closedCountLabel.setText(String.valueOf(closed));

            double totalProgress = total > 0 ? (double) active / total : 0;
            double closedProgress = total > 0 ? (double) closed / total : 0;

            openProgressBar.setProgress(totalProgress);
            closedProgressBar.setProgress(closedProgress);

            defiCountLabel.setText(total + " défi" + (total > 1 ? "s" : ""));
            tableFooterLabel.setText("Affichage de " + total + " défi" + (total > 1 ? "s" : ""));
        });
    }

    private void updateCharts() {
        Platform.runLater(() -> {
            long open = defis.stream()
                    .filter(d -> "OUVERT".equals(d.getStatut().toString()))
                    .count();
            long closed = defis.stream()
                    .filter(d -> !("OUVERT".equals(d.getStatut().toString())))
                    .count();

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Ouverts (" + open + ")", open),
                    new PieChart.Data("Fermés (" + closed + ")", closed)
            );
            statusChart.setData(pieData);
            
            // Appliquer des couleurs personnalisées au camembert
            statusChart.getData().forEach(data -> {
                if (data.getName().startsWith("Ouverts")) {
                    data.getNode().setStyle("-fx-pie-color: #18E3A4;");
                } else if (data.getName().startsWith("Fermés")) {
                    data.getNode().setStyle("-fx-pie-color: #EF5350;");
                }
            });
            
            updateEvolutionChart();
        });
    }

    private void updateEvolutionChart() {
        Platform.runLater(() -> {
            Map<YearMonth, Long> monthlyData = defis.stream()
                    .collect(Collectors.groupingBy(
                            d -> YearMonth.from(d.getDateDebut()),
                            Collectors.counting()
                    ));

            ObservableList<XYChart.Series<String, Number>> series = FXCollections.observableArrayList();
            XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
            dataSeries.setName("Évolution des participations");

            monthlyData.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> dataSeries.getData().add(
                            new XYChart.Data<>(entry.getKey().toString(), entry.getValue())
                    ));

            series.add(dataSeries);
            evolutionChart.setData(series);
            
            // Style the chart
            evolutionChart.setLegendVisible(false);
            evolutionChart.setAnimated(true);
            evolutionChart.setStyle("-fx-background-color: transparent; -fx-plot-background-color: transparent;");
            
            // Style the line
            if (!evolutionChart.getData().isEmpty()) {
                XYChart.Series<String, Number> seriesToStyle = evolutionChart.getData().get(0);
                seriesToStyle.getNode().setStyle("-fx-stroke: #18E3A4; -fx-stroke-width: 2.5px;");
                
                // Style data points
                for (XYChart.Data<String, Number> data : seriesToStyle.getData()) {
                    data.getNode().setStyle("-fx-background-color: #18E3A4; -fx-background-radius: 4px;");
                    
                    // Add tooltip for better UX
                    Tooltip tooltip = new Tooltip(String.format("%d participations", data.getYValue()));
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
            
            // Style axes
            evolutionChart.getXAxis().setStyle("-fx-tick-label-fill: #9BA6A2; -fx-axis-label-fill: #9BA6A2; -fx-tick-mark-length: 0;");
            evolutionChart.getYAxis().setStyle("-fx-tick-label-fill: #9BA6A2; -fx-axis-label-fill: #9BA6A2;");
            
            // Add subtle grid lines
            evolutionChart.setStyle(evolutionChart.getStyle() + 
                "-fx-horizontal-grid-lines-visible: true; " +
                "-fx-vertical-grid-lines-visible: true; " +
                "-fx-horizontal-grid-lines-stroke: rgba(255,255,255,0.05); " +
                "-fx-vertical-grid-lines-stroke: rgba(255,255,255,0.05);");
        });
    }

    private void updateTableVisibility() {
        if (defis.isEmpty()) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            tableContainer.setVisible(true);
            tableContainer.setManaged(true);
            emptyState.setVisible(false);
            emptyState.setManaged(false);
        }
    }

    @FXML
    public void toggleForm() {
        boolean isVisible = formContainer.isVisible();
        formContainer.setVisible(!isVisible);
        formContainer.setManaged(!isVisible);

        if (!isVisible) {
            clearForm();
        }
    }

    private void clearForm() {
        titreField.clear();
        themeField.clear();
        descriptionArea.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        statusCombo.setValue("OUVERT");
        difficulteCombo.setValue("FACILE");
        selectedImageFile = null;
        editingDefi = null;
        updateFormUI();
    }

    private void updateFormUI() {
        if (editingDefi != null) {
            createButton.setText("Modifier le défi");
        } else {
            createButton.setText("Créer le défi");
        }
    }

    @FXML
    public void createDefi() {
        String validationError = validateDefiForm();
        if (validationError != null) {
            showError("Validation", validationError);
            return;
        }

        Defi defi;
        if (editingDefi != null) {
            defi = editingDefi;
        } else {
            defi = new Defi();
            defi.setCreateurId(UserSession.getCurrentUser().getId());
        }

        defi.setTitre(titreField.getText());
        defi.setTheme(themeField.getText());
        defi.setDescription(descriptionArea.getText());
        defi.setDateDebut(dateDebutPicker.getValue());
        defi.setDateFin(dateFinPicker.getValue());
        defi.setStatut(statusCombo.getValue());
        defi.setDifficulte(difficulteCombo.getValue());
        if (selectedImageFile != null) {
            defi.setImageCover(selectedImageFile.getName());
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                if (editingDefi != null) {
                    defiService.update(defi);
                } else {
                    defiService.add(defi);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                String message = editingDefi != null ? "Défi modifié avec succès" : "Défi créé avec succès";
                showAlert("Succès", message);
                editingDefi = null;
                toggleForm();
                loadData();
            }

            @Override
            protected void failed() {
                String message = editingDefi != null ? "Impossible de modifier le défi" : "Impossible de créer le défi";
                showError("Erreur", message);
                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        File selected = fileChooser.showOpenDialog(null);
        if (selected != null) {
            selectedImageFile = selected;
            showAlert("Image", "Image sélectionnée: " + selected.getName());
        }
    }

    // ============================================
    // AI GENERATION WITH LOADING OVERLAY
    // ============================================
    private static final String GROK_API_KEY = "xai-i8xtxwVYQ1Is92OiF526YBljbgeKYbI2yY4dlRzkumVx9BM98ZWRRItXEo0vKXBKsnXYq9vGaJSMFkcb";
    private static final String GROK_API_URL = "https://api.x.ai/v1/chat/completions";
private static final String GROK_MODEL = "grok-3-fast";

    @FXML
    public void generateWithAI() {
        showAILoadingOverlay();

        Task<GeneratedChallenge> task = new Task<>() {
            @Override
            protected GeneratedChallenge call() throws Exception {
                // Simulate AI thinking delay (5-8 seconds)
                int delayMs = 5000 + new Random().nextInt(3000);
                Thread.sleep(delayMs);
                
                try {
                    return callGrokAPI();
                } catch (Exception e) {
                    System.out.println("API Grok indisponible, utilisation du générateur local: " + e.getMessage());
                    return generateChallengeLocally();
                }
            }

            @Override
            protected void succeeded() {
                GeneratedChallenge challenge = getValue();
                hideAILoadingOverlay();
                fillFormWithChallenge(challenge);
                showAlert("✨ IA", "Défi généré avec succès ! Vous pouvez modifier les informations avant de créer.");
            }

            @Override
            protected void failed() {
                hideAILoadingOverlay();
                Throwable ex = getException();
                ex.printStackTrace();
                showError("Erreur IA", "Impossible de générer le défi : " + ex.getMessage());
            }
        };

        new Thread(task).start();
    }

    // ============================================
    // RANDOM DIFFICULTY GENERATION WITH LOADING
    // ============================================
    @FXML
    public void generateRandomDifficulty() {
        // Disable button and show loading overlay
        if (generateDifficultyButton != null) {
            generateDifficultyButton.setDisable(true);
        }
        showDifficultyLoadingOverlay();

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                // Simulate thinking delay (5-8 seconds)
                int delayMs = 5000 + new Random().nextInt(3000);
                Thread.sleep(delayMs);
                
                // Generate random difficulty
                String[] difficulties = {"FACILE", "MOYEN", "DIFFICILE"};
                Random random = new Random();
                return difficulties[random.nextInt(difficulties.length)];
            }

            @Override
            protected void succeeded() {
                String difficulty = getValue();
                hideDifficultyLoadingOverlay();
                
                // Enable button again
                if (generateDifficultyButton != null) {
                    generateDifficultyButton.setDisable(false);
                }
                
                // Fill the difficulty combo box
                if (difficulteCombo != null) {
                    difficulteCombo.setValue(difficulty);
                }
                
                showAlert("🎲 Difficulté générée", "Difficulté : " + difficulty);
            }

            @Override
            protected void failed() {
                hideDifficultyLoadingOverlay();
                
                // Enable button again
                if (generateDifficultyButton != null) {
                    generateDifficultyButton.setDisable(false);
                }
                
                Throwable ex = getException();
                ex.printStackTrace();
                showError("Erreur", "Impossible de générer la difficulté : " + ex.getMessage());
            }
        };

        new Thread(task).start();
    }

    private void showAILoadingOverlay() {
        if (aiLoadingOverlay != null) {
            Platform.runLater(() -> {
                aiLoadingOverlay.setVisible(true);
                aiLoadingOverlay.setManaged(true);
                if (aiLoadingText != null) {
                    aiLoadingText.setText(AI_LOADING_MESSAGES[0]);
                }
                if (aiGenerateButton != null) {
                    aiGenerateButton.setDisable(true);
                }
            });
            startLoadingTextAnimation();
        }
    }

    private void hideAILoadingOverlay() {
        stopLoadingTextAnimation();
        if (aiLoadingOverlay != null) {
            Platform.runLater(() -> {
                aiLoadingOverlay.setVisible(false);
                aiLoadingOverlay.setManaged(false);
                if (aiGenerateButton != null) {
                    aiGenerateButton.setDisable(false);
                }
            });
        }
    }

    private java.util.Timer loadingTextTimer;

    private void startLoadingTextAnimation() {
        if (loadingTextTimer != null) {
            loadingTextTimer.cancel();
        }
        loadingTextTimer = new java.util.Timer();
        loadingTextTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            int messageIndex = 0;
            @Override
            public void run() {
                messageIndex = (messageIndex + 1) % AI_LOADING_MESSAGES.length;
                final int idx = messageIndex;
                Platform.runLater(() -> {
                    if (aiLoadingText != null) {
                        aiLoadingText.setText(AI_LOADING_MESSAGES[idx]);
                    }
                });
            }
        }, 2000, 2000);
    }

    private void stopLoadingTextAnimation() {
        if (loadingTextTimer != null) {
            loadingTextTimer.cancel();
            loadingTextTimer = null;
        }
    }

    // ============================================
    // DIFFICULTY LOADING OVERLAY METHODS
    // ============================================
    private void showDifficultyLoadingOverlay() {
        if (difficultyLoadingOverlay != null) {
            Platform.runLater(() -> {
                difficultyLoadingOverlay.setVisible(true);
                difficultyLoadingOverlay.setManaged(true);
                // Update loading text with animation
                if (difficultyLoadingOverlay != null) {
                    // We'll animate the text separately
                }
                if (generateDifficultyButton != null) {
                    generateDifficultyButton.setDisable(true);
                }
            });
            startDifficultyLoadingTextAnimation();
        }
    }

    private void hideDifficultyLoadingOverlay() {
        stopDifficultyLoadingTextAnimation();
        if (difficultyLoadingOverlay != null) {
            Platform.runLater(() -> {
                difficultyLoadingOverlay.setVisible(false);
                difficultyLoadingOverlay.setManaged(false);
                if (generateDifficultyButton != null) {
                    generateDifficultyButton.setDisable(false);
                }
            });
        }
    }

    private java.util.Timer difficultyLoadingTextTimer;

    private void startDifficultyLoadingTextAnimation() {
        if (difficultyLoadingTextTimer != null) {
            difficultyLoadingTextTimer.cancel();
        }
        difficultyLoadingTextTimer = new java.util.Timer();
        difficultyLoadingTextTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            int messageIndex = 0;
            @Override
            public void run() {
                messageIndex = (messageIndex + 1) % DIFFICULTY_LOADING_MESSAGES.length;
                final int idx = messageIndex;
                Platform.runLater(() -> {
                    // Find the difficulty loading text label and update it
                    // We need to access the label inside the overlay
                    if (difficultyLoadingOverlay != null) {
                        // Get the VBox inside the StackPane, then get the Label
                        var children = difficultyLoadingOverlay.getChildren();
                        if (!children.isEmpty()) {
                            var vbox = (VBox) children.get(0);
                            var labelChildren = vbox.getChildren();
                            if (labelChildren.size() >= 2) { // Icon, Text, Spinner
                                var difficultyTextLabel = (Label) labelChildren.get(1);
                                difficultyTextLabel.setText(DIFFICULTY_LOADING_MESSAGES[idx]);
                            }
                        }
                    }
                });
            }
        }, 2000, 2000);
    }

    private void stopDifficultyLoadingTextAnimation() {
        if (difficultyLoadingTextTimer != null) {
            difficultyLoadingTextTimer.cancel();
            difficultyLoadingTextTimer = null;
        }
    }

    private void fillFormWithChallenge(GeneratedChallenge challenge) {
        formContainer.setVisible(true);
        formContainer.setManaged(true);

        titreField.setText(challenge.titre);
        themeField.setText(challenge.theme);
        descriptionArea.setText(challenge.description);

        if (dateDebutPicker.getValue() == null) {
            dateDebutPicker.setValue(LocalDate.now());
        }
        if (dateFinPicker.getValue() == null) {
            dateFinPicker.setValue(LocalDate.now().plusDays(7));
        }

        Platform.runLater(() -> {
            formContainer.requestFocus();
        });
    }

    private GeneratedChallenge generateChallengeLocally() {
        String[][] challenges = {
            {
                "Défi Zéro Déchet pendant 30 jours",
                "Environnement",
                "Relevez le défi de produire zéro déchet pendant un mois entier ! Apprenez à réduire vos emballages, compostez vos déchets organiques et trouvez des alternatives durables aux produits jetables. Partagez vos astuces et progrès avec la communauté pour inspirer d'autres participants à adopter un mode de vie plus respectueux de l'environnement."
            },
            {
                "Marathon de la Créativité Quotidienne",
                "Créativité",
                "Pendant 30 jours, consacrez au moins 20 minutes chaque jour à une activité créative de votre choix : dessin, écriture, musique, photographie, ou tout autre art. Le but est de développer une habitude créative régulière et de partager vos créations avec la communauté pour recevoir des retours et vous motiver mutuellement."
            },
            {
                "Défi Bien-être Mental",
                "Bien-être",
                "Pendant 21 jours, pratiquez quotidiennement une activité favorisant votre bien-être mental : méditation, journal de gratitude, exercices de respiration, ou déconnexion numérique. Suivez votre humeur et partagez vos ressentis pour créer un espace d'entraide et de partage d'expériences positives."
            },
            {
                "Apprends une Nouvelle Compétence",
                "Éducation",
                "Choisissez une compétence que vous souhaitez acquérir (langue, programmation, cuisine, musique...) et consacrez-y 30 minutes par jour pendant 30 jours. Établissez un plan d'apprentissage, suivez vos progrès et partagez vos victoires avec les autres participants pour rester motivé tout au long du parcours."
            },
            {
                "Défi Fitness 30 Jours",
                "Sport",
                "Relevez le défi de faire 30 minutes d'activité physique chaque jour pendant un mois. Que ce soit la course à pied, le yoga, la musculation ou la natation, l'important est de bouger quotidiennement. Partagez vos séances, vos progrès et encouragez les autres participants à tenir bon !"
            },
            {
                "Exploration Culturelle Locale",
                "Culture",
                "Découvrez les trésors culturels de votre région ! Visitez un musée, une bibliothèque, un monument historique ou assistez à un événement culturel chaque semaine. Partagez vos découvertes, photos et réflexions pour créer une carte collaborative des richesses culturelles locales."
            },
            {
                "Défi Digital Detox",
                "Technologie",
                "Réduisez votre temps d'écran de 50% pendant une semaine. Identifiez les applications qui vous font perdre le plus de temps et remplacez-les par des activités enrichissantes : lecture, sport, rencontres amicales. Partagez votre expérience et découvrez comment retrouver un équilibre numérique sain."
            },
            {
                "Cuisine du Monde",
                "Social",
                "Chaque semaine, préparez un plat issu d'une cuisine différente du monde. Documentez la recette, partagez des photos et racontez l'histoire culturelle du plat. Invitez des amis ou des membres de la communauté à goûter vos créations et échangez sur les saveurs découvertes."
            }
        };

        Random random = new Random();
        int index = random.nextInt(challenges.length);

        GeneratedChallenge challenge = new GeneratedChallenge();
        challenge.titre = challenges[index][0];
        challenge.theme = challenges[index][1];
        challenge.description = challenges[index][2];

        return challenge;
    }

    private GeneratedChallenge callGrokAPI() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String systemPrompt = "Tu es un assistant créatif spécialisé dans la génération de défis pour une plateforme communautaire. " +
                "Génère un défi original, motivant et réaliste. Réponds UNIQUEMENT en JSON avec les champs: titre, theme, description. " +
                "Le titre doit être accrocheur (3-8 mots). Le thème doit être une catégorie courte (1-3 mots). " +
                "La description doit être détaillée (minimum 80 mots), expliquant le défi, ses objectifs et comment y participer.";

        String userPrompt = "Génère un nouveau défi créatif et original pour notre plateforme. Sois inventif !";

        JSONObject messageSystem = new JSONObject();
        messageSystem.put("role", "system");
        messageSystem.put("content", systemPrompt);

        JSONObject messageUser = new JSONObject();
        messageUser.put("role", "user");
        messageUser.put("content", userPrompt);

        JSONArray messages = new JSONArray();
        messages.put(messageSystem);
        messages.put(messageUser);

        JSONObject requestBody = new JSONObject();
ConfigManager config = ConfigManager.getInstance();
        requestBody.put("model", config.getString("ai.grok.model", "grok-3-fast"));
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.85);
        requestBody.put("max_tokens", 800);
        requestBody.put("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getString("ai.grok.api_url", "https://api.x.ai/v1/chat/completions")))
                .header("Authorization", "Bearer " + config.getString("ai.grok.api_key"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur API Grok (HTTP " + response.statusCode() + "): " + response.body());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices.isEmpty()) {
            throw new RuntimeException("Aucune réponse de l'IA");
        }

        String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

        String jsonStr = extractJsonFromContent(content);

        JSONObject challengeJson = new JSONObject(jsonStr);
        GeneratedChallenge challenge = new GeneratedChallenge();
        challenge.titre = challengeJson.optString("titre", "Nouveau Défi").trim();
        challenge.theme = challengeJson.optString("theme", "Général").trim();
        challenge.description = challengeJson.optString("description", "Description du défi").trim();

        if (challenge.titre.length() < 2) challenge.titre = "Défi Créatif";
        if (challenge.theme.length() < 2) challenge.theme = "Créativité";
        if (challenge.description.length() < 25) {
            challenge.description = "Participez à ce défi passionnant et montrez vos talents ! " +
                    "Ce défi vous propose de repousser vos limites et de découvrir de nouvelles compétences. " +
                    "Rejoignez la communauté et partagez votre progression avec les autres participants.";
        }

        return challenge;
    }

    private String extractJsonFromContent(String content) {
        if (content == null) return "{}";
        String trimmed = content.trim();

        if (trimmed.contains("```json")) {
            int start = trimmed.indexOf("```json") + 7;
            int end = trimmed.indexOf("```", start);
            if (end > start) {
                trimmed = trimmed.substring(start, end).trim();
            }
        } else if (trimmed.contains("```")) {
            int start = trimmed.indexOf("```") + 3;
            int end = trimmed.indexOf("```", start);
            if (end > start) {
                trimmed = trimmed.substring(start, end).trim();
            }
        }

        if (!trimmed.startsWith("{")) {
            int objStart = trimmed.indexOf("{");
            if (objStart >= 0) {
                trimmed = trimmed.substring(objStart);
            }
        }

        if (!trimmed.startsWith("{")) {
            return "{}";
        }

        return trimmed;
    }

    private static class GeneratedChallenge {
        String titre;
        String theme;
        String description;
    }

    @FXML
    public void navigateTo() {
        showAlert("Navigation", "Redirection en cours...");
    }

    @FXML
    public void goToAdminChallenges() {
        com.example.app.utils.SceneManager.getInstance().loadScene("/admin/challenges");
    }

    @FXML
    public void goToAdminUsers() {
        com.example.app.utils.SceneManager.getInstance().loadScene("/admin/users");
    }

    @FXML
    public void goToAdminOeuvres() {
        // Route admin oeuvres (si elle n'existe pas, elle retournera vers index)
        com.example.app.utils.SceneManager.getInstance().loadScene("/admin/oeuvres");
    }

    @FXML
    public void goToAdminArtefacts() {
        com.example.app.utils.SceneManager.getInstance().loadScene("/admin/artefacts");
    }

    @FXML
    public void goToAdminUniverses() {
        com.example.app.utils.SceneManager.getInstance().loadScene("/admin/universes");
    }

    @FXML
    public void goToAdminPersonnages() {
        com.example.app.utils.SceneManager.getInstance().loadScene("/admin/personnages");
        }

@FXML
    public void goBackToDefis() {
        navigateTo("/challenges");
    }

    private void editDefi(Defi defi) {
        editingDefi = defi;
        
        titreField.setText(defi.getTitre() != null ? defi.getTitre() : "");
        themeField.setText(defi.getTheme() != null ? defi.getTheme() : "");
        descriptionArea.setText(defi.getDescription() != null ? defi.getDescription() : "");
        dateDebutPicker.setValue(defi.getDateDebut());
        dateFinPicker.setValue(defi.getDateFin());
        statusCombo.setValue(defi.getStatut() != null ? defi.getStatut() : "OUVERT");
        difficulteCombo.setValue(defi.getDifficulte() != null ? defi.getDifficulte() : "FACILE");
        
        selectedImageFile = null;

        updateFormUI();

        formContainer.setVisible(true);
        formContainer.setManaged(true);
        
        Platform.runLater(() -> {
            formContainer.requestFocus();
            System.out.println("✏️ Édition du défi: " + defi.getTitre());
        });
    }

    private void generateDifficulty(Defi defi) {
        String[] difficulties = {"FACILE", "MOYEN", "DIFFICILE", "EXPERT"};
        String current = defi.getDifficulte();
        String next;
        do {
            next = difficulties[new Random().nextInt(difficulties.length)];
        } while (next.equals(current));

        final String selectedDifficulty = next;
        defi.setDifficulte(selectedDifficulty);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int delayMs = 5000 + new Random().nextInt(3000);
                Thread.sleep(delayMs);
                defiService.update(defi);
                return null;
            }

            @Override
            protected void succeeded() {
                showAlert("Difficulté générée", "La difficulté du défi '" + defi.getTitre() + "' est maintenant : " + selectedDifficulty);
                loadData();
            }

            @Override
            protected void failed() {
                showError("Erreur", "Impossible de mettre à jour la difficulté");
                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    private void deleteDefi(Defi defi) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le défi: " + defi.getTitre());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce défi ?\n" +
                            "Cette action est irréversible et supprimera aussi\n" +
                            "toutes les participations associées.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    defiService.delete(defi.getId());
                    System.out.println("🗑️  Suppression du défi: " + defi.getTitre());
                    return null;
                }

                @Override
                protected void succeeded() {
                    showAlert("Succès", "Défi '" + defi.getTitre() + "' supprimé avec succès");
                    loadData();
                }

                @Override
                protected void failed() {
                    showError("Erreur", "Impossible de supprimer le défi: " + defi.getTitre());
                    getException().printStackTrace();
                }
            };

            new Thread(task).start();
        }
    }
}
