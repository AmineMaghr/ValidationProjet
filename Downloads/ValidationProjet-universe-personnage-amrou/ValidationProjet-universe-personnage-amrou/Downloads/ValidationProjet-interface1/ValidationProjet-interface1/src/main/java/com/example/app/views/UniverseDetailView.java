package com.example.app.views;

import com.example.app.entities.Universe;
import com.example.app.services.ChatbotService;
import com.example.app.services.ImageGenerationService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.concurrent.Task;

public class UniverseDetailView extends VBox {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_MAIN = "#1A1F1E";
    private static final String BG_DARK = "#141615";
    private static final String TEXT_PRIMARY = "#E6FFF6";
    private static final String TEXT_SECONDARY = "#B0B9B6";

    private final VBox mainBox;
    private final Universe universe;

    public UniverseDetailView(Universe universe) {
        this.universe = universe;
        this.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Top Navigation
        this.getChildren().add(new HeaderView());

        mainBox = new VBox();
        mainBox.setSpacing(0);
        mainBox.setStyle("-fx-background-color: " + BG_MAIN + ";");

        setupHeader();
        setupBody();

        ScrollPane scrollPane = new ScrollPane(mainBox);
        scrollPane.setStyle("-fx-background: " + BG_MAIN + "; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().add(scrollPane);
    }

    private void setupHeader() {
        StackPane heroContainer = new StackPane();
        heroContainer.setPrefHeight(400);
        heroContainer.setMinHeight(400);
        heroContainer.setStyle("-fx-background-color: " + BG_DARK + ";");

        ImageView hero = new ImageView();
        hero.setFitWidth(1200);
        hero.setFitHeight(400);
        hero.setPreserveRatio(false); // standard scaling for banner look
        
        if (universe.getBannerImage() != null && universe.getBannerImage().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(universe.getBannerImage());
                hero.setImage(new javafx.scene.image.Image(bis));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        Region gradient = new Region();
        gradient.setStyle("-fx-background-color: linear-gradient(to top, " + BG_MAIN + " 0%, transparent 80%);");

        VBox headerText = new VBox(15);
        headerText.setAlignment(Pos.BOTTOM_LEFT);
        headerText.setPadding(new Insets(40));

        Label nameTitle = new Label(universe.getName());
        nameTitle.setFont(Font.font("System", FontWeight.BOLD, 48));
        nameTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0, 0, 2);");

        Label genreBadge = new Label(universe.getGenre() != null ? universe.getGenre() : "Unknown Genre");
        genreBadge.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-padding: 8 20; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-font-size: 14px;");

        javafx.scene.control.Button btnEdit = new javafx.scene.control.Button("Modifier");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-radius: 8px; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        applyButtonHoverEffect(btnEdit, PRIMARY_COLOR, "transparent");
        btnEdit.setOnAction(e -> {
            try {
                this.getScene().setRoot(new UniverseCreateView(universe));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-border-color: #e74c3c; -fx-border-radius: 8px; -fx-padding: 8 20; -fx-cursor: hand;");
        applyButtonHoverEffect(btnDelete, "#E6FFF6", "#e74c3c");
        btnDelete.setOnAction(e -> {
            try {
                new com.example.app.services.UniverseService().delete(universe.getId());
                com.example.app.utils.SceneManager.getInstance().loadScene("/universes");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnVideo = new javafx.scene.control.Button("🎬 Vidéo");
        btnVideo.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 20; -fx-border-color: #9b59b6; -fx-border-radius: 8px; -fx-cursor: hand;");
        applyButtonHoverEffect(btnVideo, "#E6FFF6", "#9b59b6");
        btnVideo.setOnAction(e -> {
            try {
                VBox videoPanel = createVideoPanel();
                StackPane videoWindow = new StackPane(videoPanel);
                javafx.scene.Scene videoScene = new javafx.scene.Scene(videoWindow, 800, 600);
                javafx.stage.Stage videoStage = new javafx.stage.Stage();
                videoStage.setTitle("Universe Video - " + universe.getName());
                videoStage.setScene(videoScene);
                videoStage.show();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        javafx.scene.control.Button btnAIBanner = new javafx.scene.control.Button("🖼️ Bannière IA");
        btnAIBanner.setStyle("-fx-background-color: #e67e22; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 20; -fx-cursor: hand;");
        applyButtonHoverEffect(btnAIBanner, "#E6FFF6", "#e67e22");
        btnAIBanner.setOnAction(e -> {
            btnAIBanner.setText("⏳ Génération...");
            btnAIBanner.setDisable(true);
            String url = ImageGenerationService.generateUniverseImageURL(universe);
            new Thread(() -> {
                try {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(url, true);
                    javafx.application.Platform.runLater(() -> {
                        hero.setImage(img);
                        btnAIBanner.setText("🖼️ Bannière IA");
                        btnAIBanner.setDisable(false);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> { btnAIBanner.setText("🖼️ Bannière IA"); btnAIBanner.setDisable(false); });
                }
            }).start();
        });

        HBox actionsBox = new HBox(15, genreBadge, btnEdit, btnDelete, btnVideo, btnAIBanner);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        headerText.getChildren().addAll(nameTitle, actionsBox);

        heroContainer.getChildren().addAll(hero, gradient, headerText);
        mainBox.getChildren().add(heroContainer);
    }

    private void applyButtonHoverEffect(javafx.scene.control.Button btn, String hoverTextUrl, String hoverBgUrl) {
        String originalStyle = btn.getStyle();
        btn.setOnMouseEntered(e -> btn.setStyle(originalStyle + " -fx-opacity: 0.8;"));
        btn.setOnMouseExited(e -> btn.setStyle(originalStyle));
    }

    private void setupBody() {
        VBox body = new VBox(30);
        body.setPadding(new Insets(40));
        body.setAlignment(Pos.TOP_CENTER);
        
        VBox contentCard = new VBox(25);
        contentCard.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 40; -fx-background-radius: 16px;");
        contentCard.setMaxWidth(900);

        Label shortDescTitle = new Label("Description Courte");
        shortDescTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 22px;");

        Text shortDescText = new Text(universe.getShortDescription());
        shortDescText.setFill(Color.web(TEXT_SECONDARY));
        shortDescText.setWrappingWidth(820);
        shortDescText.setStyle("-fx-font-size: 15px; -fx-line-spacing: 1.5em;");

        Label storyTitle = new Label("Contexte Narratif");
        storyTitle.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 22px;");

        Text storyContextText = new Text(universe.getStoryContext());
        storyContextText.setFill(Color.web(TEXT_SECONDARY));
        storyContextText.setWrappingWidth(820);
        storyContextText.setStyle("-fx-font-size: 15px; -fx-line-spacing: 1.5em;");

        contentCard.getChildren().addAll(shortDescTitle, shortDescText, storyTitle, storyContextText);
        body.getChildren().add(contentCard);

        // Random Lore Event Generator
        VBox loreEventPanel = createLoreEventPanel();
        body.getChildren().add(loreEventPanel);

        // Add Chatbot
        VBox chatPanel = createChatPanel();
        body.getChildren().add(chatPanel);

        mainBox.getChildren().add(body);
    }

    private VBox createLoreEventPanel() {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 30; -fx-background-radius: 16px;");
        panel.setMaxWidth(900);

        Label lblTitle = new Label("✨ Générateur d'Événements");
        lblTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 22px;");

        Label lblSub = new Label("Générez un événement aléatoire dans l'univers " + universe.getName());
        lblSub.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 13px;");

        TextArea eventArea = new TextArea();
        eventArea.setWrapText(true);
        eventArea.setEditable(false);
        eventArea.setPrefHeight(120);
        eventArea.setStyle("-fx-control-inner-background: #0a0c0b; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-border-color: " + PRIMARY_COLOR + "44; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        eventArea.setText("Cliquez sur le bouton pour générer un événement narratif...");

        String[] eventTemplates = {
            "Un mystérieux voyageur arrive aux portes de la cité principale, porteur d'une ancienne prophétie oubliée.",
            "Une fissure entre les dimensions s'ouvre soudainement au cœur du territoire, laissant s'échapper des créatures inconnues.",
            "Le conseil des anciens se réunit en urgence — une ressource vitale est sur le point de disparaître.",
            "Une alliance inattendue se forme entre deux factions ennemies, face à une menace commune.",
            "Les étoiles changent de position dans le ciel nocturne, signalant l'avènement d'une nouvelle ère.",
            "Un artefact légendaire, perdu depuis des siècles, refait surface dans les mains d'un inconnu.",
            "Une tempête magique balaie la région, modifiant les propriétés de tout ce qu'elle touche.",
            "Un ancien monument s'illumine pour la première fois, révélant des inscriptions en langue oubliée.",
            "Des rumeurs de trahison au sein même de l'élite font trembler les fondations du pouvoir en place.",
            "Une épidémie étrange frappe les animaux de la région — les chamans y voient un présage."
        };

        Button generateBtn = new Button("🎲 Générer un Événement");
        generateBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 10px; -fx-cursor: hand;");
        generateBtn.setOnMouseEntered(e -> generateBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "CC; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 10px; -fx-cursor: hand;"));
        generateBtn.setOnMouseExited(e -> generateBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 10px; -fx-cursor: hand;"));

        generateBtn.setOnAction(e -> {
            String event = eventTemplates[(int) (Math.random() * eventTemplates.length)];
            String timestamp = java.time.LocalTime.now().withNano(0).toString();
            eventArea.setText("[" + timestamp + "] — " + universe.getName() + "\n\n" + event);
        });

        panel.getChildren().addAll(lblTitle, lblSub, eventArea, generateBtn);
        return panel;
    }

    private VBox createChatPanel() {
        VBox chatBox = new VBox(15);
        chatBox.setStyle("-fx-background-color: " + BG_DARK + "; -fx-padding: 30; -fx-background-radius: 16px;");
        chatBox.setMaxWidth(900);

        Label lblChat = new Label("🤖 Ask About This Universe");
        lblChat.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 22px;");

        TextArea chatArea = new TextArea();
        chatArea.setWrapText(true);
        chatArea.setEditable(false);
        chatArea.setStyle("-fx-control-inner-background: " + BG_MAIN + "; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");
        chatArea.setPrefHeight(200);
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        HBox inputBox = new HBox(10);
        TextField inputField = new TextField();
        inputField.setPromptText("Ask about " + universe.getName() + "...");
        inputField.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-prompt-text-fill: " + TEXT_SECONDARY + ";");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-padding: 8 15;");

        sendBtn.setOnAction(e -> {
            String question = inputField.getText().trim();
            if (!question.isEmpty()) {
                chatArea.appendText("\nYou: " + question + "\n");
                inputField.clear();

                Task<String> task = new Task<>() {
                    @Override
                    protected String call() {
                        return ChatbotService.askAboutUniverse(
                            universe.getName(),
                            universe.getStoryContext() != null ? universe.getStoryContext() : universe.getShortDescription(),
                            question
                        );
                    }
                };

                task.setOnSucceeded(evt -> {
                    chatArea.appendText("Universe: " + task.getValue() + "\n\n");
                });

                new Thread(task).start();
            }
        });

        inputField.setOnAction(e -> sendBtn.fire());

        inputBox.getChildren().addAll(inputField, sendBtn);
        chatBox.getChildren().addAll(lblChat, chatArea, inputBox);

        return chatBox;
    }

    private VBox createVideoPanel() {
        VBox videoBox = new VBox(15);
        videoBox.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-padding: 20;");
        videoBox.setAlignment(Pos.TOP_CENTER);

        Label lblVideo = new Label("🎬 Universe Presentation Video");
        lblVideo.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-font-size: 18px;");

        javafx.scene.media.MediaView mediaView = new javafx.scene.media.MediaView();
        mediaView.setFitHeight(400);
        mediaView.setPreserveRatio(true);

        String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
        try {
            javafx.scene.media.Media media = new javafx.scene.media.Media(videoPath);
            javafx.scene.media.MediaPlayer player = new javafx.scene.media.MediaPlayer(media);
            mediaView.setMediaPlayer(player);
        } catch (Exception e) {
            Label errorLabel = new Label("Unable to load video");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            videoBox.getChildren().addAll(lblVideo, errorLabel);
            return videoBox;
        }

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));

        Button playBtn = new Button("▶ Play");
        playBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + BG_DARK + "; -fx-font-weight: bold; -fx-padding: 8 20;");
        playBtn.setOnAction(e -> mediaView.getMediaPlayer().play());

        Button pauseBtn = new Button("⏸ Pause");
        pauseBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-padding: 8 20;");
        pauseBtn.setOnAction(e -> mediaView.getMediaPlayer().pause());

        Button stopBtn = new Button("⏹ Stop");
        stopBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #E6FFF6; -fx-font-weight: bold; -fx-padding: 8 20;");
        stopBtn.setOnAction(e -> mediaView.getMediaPlayer().stop());

        controls.getChildren().addAll(playBtn, pauseBtn, stopBtn);
        videoBox.getChildren().addAll(lblVideo, mediaView, controls);

        return videoBox;
    }
}

