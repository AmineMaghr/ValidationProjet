package com.example.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void loadScene(String path) {
        try {
            String fxmlFile = getFxmlPath(path);
            System.out.println("Chargement: " + fxmlFile);

            if (getClass().getResource(fxmlFile) == null) {
                System.err.println("FICHIER NON TROUVÉ: " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFxmlPath(String route) {
        return switch (route) {
            case "/" -> "/com/monapp/view/index.fxml";
            case "/discover" -> "/com/monapp/view/discover.fxml";
            case "/universes" -> "/com/monapp/view/universes.fxml";
            case "/personnages" -> "/com/monapp/view/personnages.fxml";

            // Œuvres - tes fichiers existent
            case "/oeuvre" -> "/com/monapp/view/oeuvre/index.fxml";
            case "/oeuvre/create" -> "/com/monapp/view/oeuvre/create.fxml";

            case "/artefact" -> "/com/monapp/view/artefact/index.fxml";
            case "/shop" -> "/com/monapp/view/shop/shop.fxml";
            case "/challenges" -> "/com/monapp/view/challenges.fxml";
            case "/quiz" -> "/com/monapp/view/quiz.fxml";
            case "/admin" -> "/com/monapp/view/admin/quiz_question.fxml";
            case "/admin/quiz" -> "/com/monapp/view/admin/quiz_question.fxml";
            case "/preferences/advanced", "/prefrence/advanced" -> "/com/monapp/view/preference/advanced.fxml";

            case "/login" -> "/com/monapp/view/security/login.fxml";
            case "/register" -> "/com/monapp/view/registration/register.fxml";
            case "/profile" -> "/com/monapp/view/profile/profile.fxml";

            default -> "/com/monapp/view/index.fxml";
        };
    }
}