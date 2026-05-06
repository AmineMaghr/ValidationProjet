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
        // Parse dynamic routes
        String[] parts = route.split("/");
        int defiId = -1;
        if (parts.length >= 3 && "challenges".equals(parts[1])) {
            try {
                defiId = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid defi ID in route: " + parts[2]);
            }
        }
        
        // Handle dynamic routes like /challenges/participer/{id} or /challenges/{id}
        if (route.startsWith("/challenges/participer/")) {
            String[] routeParts = route.split("/");
            if (routeParts.length >= 4) {
                try {
                    int parsedDefiId = Integer.parseInt(routeParts[3]);
                    com.midgar.controller.ParticiperController.setPendingDefiId(parsedDefiId);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid defi ID in route: " + routeParts[3]);
                }
            }
            return "/com/monapp/view/challenges/participer.fxml";
        } else if (parts.length == 3 && "challenges".equals(parts[1])) {
            // Handle /challenges/{id} where {id} is a number
            try {
                int routeDefiId = Integer.parseInt(parts[2]);
                com.midgar.controller.ParticiperController.setPendingDefiId(routeDefiId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid defi ID in route: " + parts[2]);
            }
            return "/com/monapp/view/challenges/participer.fxml";
        }
        // Handle dynamic routes like /challenges/peindre/{id}
        if (route.startsWith("/challenges/peindre/") || "/challenges/paint_designer".equals(route)) {
            String[] routeParts = route.split("/");
            if (routeParts.length >= 4) {
                try {
                    int parsedDefiId = Integer.parseInt(routeParts[3]);
                    com.monapp.view.challenges.PaintDesignerController.setPendingDefiId(parsedDefiId);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid defi ID in route: " + routeParts[3]);
                }
            }
            return "/com/monapp/view/challenges/paint_designer.fxml";
        }
        
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

            // Admin pages
            case "/admin" -> "/com/monapp/view/admin_challenges.fxml";
            case "/admin/challenges" -> "/com/monapp/view/admin_challenges.fxml";
            case "/admin/defi-analytics" -> "/com/monapp/view/admin/defi_analytics.fxml";

            case "/login" -> "/com/monapp/view/security/login.fxml";
            case "/register" -> "/com/monapp/view/registration/register.fxml";
            case "/profile" -> "/com/monapp/view/profile/profile.fxml";

            default -> "/com/monapp/view/index.fxml";
        };
    }
}
