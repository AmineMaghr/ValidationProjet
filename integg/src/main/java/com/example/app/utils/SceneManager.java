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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void loadScene(String path, String param) {
        // Store param for controllers that need it, then delegate
        loadScene(path);
    }

    public void loadScene(String path) {
        try {
            System.out.println("Chargement: " + path);

            Parent root = null;

            // Handle pure Java UI views
            if ("/universes".equals(path)) {
                root = new com.example.app.views.UniverseListView();
            } else if ("/universes/create".equals(path)) {
                root = new com.example.app.views.UniverseCreateView();
            } else if ("/personnages".equals(path)) {
                root = new com.example.app.views.PersonnageListView();
            } else if ("/personnages/create".equals(path)) {
                root = new com.example.app.views.PersonnageCreateView();
            } else if ("/battle".equals(path)) {
                root = new com.example.app.views.BattleSimulatorView();
            } else if ("/admin/universes".equals(path)) {
                root = new com.example.app.views.AdminUniverseDashboardView();
            } else if ("/admin/personnages".equals(path)) {
                root = new com.example.app.views.AdminPersonnageDashboardView();
            } else {
                String fxmlFile = getFxmlPath(path);
                System.out.println("Chargement FXML: " + fxmlFile);

                if (getClass().getResource(fxmlFile) == null) {
                    System.err.println("FICHIER NON TROUVÉ: " + fxmlFile);
                    return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                root = loader.load();
            }

            // First load: create the scene. Subsequent loads: swap the root.
            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root, 1280, 800);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Midgar");
                primaryStage.setMaximized(true);
                primaryStage.show();
            } else {
                primaryStage.getScene().setRoot(root);
                primaryStage.setMaximized(true);
            }


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
            case "/universes/create" -> "/com/monapp/view/universe_create.fxml";
            case "/personnages" -> "/com/monapp/view/personnages.fxml";

            // Œuvres - tes fichiers existent
            case "/oeuvre" -> "/com/monapp/view/oeuvre/index.fxml";
            case "/oeuvre/create" -> "/com/monapp/view/oeuvre/create.fxml";

            case "/artefact" -> "/com/monapp/view/artefact/index.fxml";
            case "/shop" -> "/com/monapp/view/shop/index.fxml";
            case "/challenges" -> "/com/monapp/view/challenges.fxml";
            case "/quiz" -> "/com/monapp/view/quiz.fxml";

             case "/admin", "/admin/users" -> "/com/monapp/view/admin/users.fxml";
             case "/admin/challenges" -> "/com/monapp/view/admin_challenges.fxml";
            case "/login" -> "/com/monapp/view/login-view.fxml";
            case "/register" -> "/com/monapp/view/register-view.fxml";
            case "/profile" -> "/com/monapp/view/profile-view.fxml";

            default -> "/com/monapp/view/index.fxml";
        };
    }
}