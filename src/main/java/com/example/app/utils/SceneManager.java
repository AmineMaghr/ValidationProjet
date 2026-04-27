package com.example.app.utils;

import com.example.app.controllers.ResetPasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

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

    // Méthode sans token
    public void loadScene(String path) {
        loadScene(path, null);
    }
    
    // Méthode avec token
    public void loadScene(String path, String token) {
        try {
            String fxmlFile = getFxmlPath(path);
            System.out.println("Chargement: " + fxmlFile);

            if (getClass().getResource(fxmlFile) == null) {
                System.err.println("FICHIER NON TROUVÉ: " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            // Si c'est la page reset-password, passer le token au contrôleur
            if ("/reset-password".equals(path) && token != null) {
                Object controller = loader.getController();
                if (controller instanceof ResetPasswordController) {
                    ((ResetPasswordController) controller).setToken(token);
                }
            }
            
            Scene scene = new Scene(root, 1200, 800);
            
            // Charger le CSS
            try {
                URL cssResource = getClass().getResource("/css/modern-style.css");
                if (cssResource != null) {
                    scene.getStylesheets().add(cssResource.toExternalForm());
                }
            } catch (Exception e) {
                System.out.println("CSS non chargé");
            }
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ MÉTHODE SPÉCIFIQUE POUR RESET PASSWORD AVEC TOKEN
    public void loadResetPasswordWithToken(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/view/reset-password-view.fxml"));
            Parent root = loader.load();
            ResetPasswordController controller = loader.getController();
            controller.setToken(token);
            
            Scene scene = new Scene(root, 1200, 800);
            try {
                URL cssResource = getClass().getResource("/css/modern-style.css");
                if (cssResource != null) {
                    scene.getStylesheets().add(cssResource.toExternalForm());
                }
            } catch (Exception e) {
                System.out.println("CSS non chargé");
            }
            
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFxmlPath(String route) {
        return switch (route) {
            case "/" -> "/com/monapp/view/index.fxml";
            case "/discover" -> "/com/monapp/view/discover.fxml";
            case "/universes" -> "/com/monapp/view/universes.fxml";
            case "/personnages" -> "/com/monapp/view/personnages.fxml";
            case "/oeuvre" -> "/com/monapp/view/oeuvre/index.fxml";
            case "/oeuvre/create" -> "/com/monapp/view/oeuvre/create.fxml";
            case "/oeuvre/show" -> "/com/monapp/view/oeuvre/show.fxml";
            case "/artefact" -> "/com/monapp/view/artefact/index.fxml";
            case "/artefact/create" -> "/com/monapp/view/artefact/create.fxml";
            case "/artefact/show" -> "/com/monapp/view/artefact/show.fxml";
            case "/shop" -> "/com/monapp/view/shop/shop.fxml";
            case "/challenges" -> "/com/monapp/view/challenges.fxml";
            case "/quiz" -> "/com/monapp/view/quiz.fxml";
            case "/login" -> "/com/monapp/view/login-view.fxml";
            case "/register" -> "/com/monapp/view/register-view.fxml";
            case "/forgot-password" -> "/com/monapp/view/forgot-password-view.fxml";
            case "/reset-password" -> "/com/monapp/view/reset-password-view.fxml";
            case "/profile" -> "/com/monapp/view/profile-view.fxml";
            case "/admin" -> "/com/monapp/view/admin/users.fxml";
            case "/admin/users" -> "/com/monapp/view/admin/users.fxml";
            default -> "/com/monapp/view/index.fxml";
        };
    }
}