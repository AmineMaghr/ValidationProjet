package com.example.app.controllers;

import com.example.app.utils.SceneManager;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public abstract class BaseController {

    @FXML
    public void goAccueil() { navigateTo("/"); }

    @FXML
    public void goDiscover() { navigateTo("/discover"); }

    @FXML
    public void goUniverses() { navigateTo("/universes"); }

    @FXML
    public void goPersonnages() { navigateTo("/personnages"); }

    @FXML
    public void goOeuvres() { navigateTo("/oeuvre"); }

    @FXML
    public void goShop() { navigateTo("/shop"); }

    @FXML
    public void goChallenges() { navigateTo("/challenges"); }

    @FXML
    public void lancerQuiz() { navigateTo("/quiz"); }

    @FXML
    public void goAdmin() {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            navigateTo("/admin");  // Changé de "/admin" à "/admin/users" ou gardez "/admin"
        } else {
            showAlert("Accès refusé", "Vous n'êtes pas administrateur");
        }
    }

    @FXML
    public void goProfile() { navigateTo("/profile"); }

    @FXML
    public void goLogin() { navigateTo("/login"); }
    
    @FXML
    public void goArtefacts() { navigateTo("/artefact"); }
    
    @FXML
    public void goRegister() { navigateTo("/register"); }

    protected void navigateTo(String view) {
        try {
            String fxmlPath;

            switch (view) {
                case "/admin":
                case "/admin/users":
                    fxmlPath = "/com/monapp/view/admin/users.fxml";
                    break;
                case "/profile":
                    fxmlPath = "/com/monapp/view/profile-view.fxml";
                    break;
                case "/login":
                    fxmlPath = "/com/monapp/view/login-view.fxml";
                    break;
                case "/register":
                    fxmlPath = "/com/monapp/view/register-view.fxml";
                    break;
                case "/oeuvre":
                    fxmlPath = "/com/monapp/view/oeuvre/index.fxml";
                    break;
                case "/artefact":
                    fxmlPath = "/com/monapp/view/artefact/index.fxml";
                    break;
                case "/":
                    fxmlPath = "/com/monapp/view/index.fxml";
                    break;
                default:
                    fxmlPath = "/com/monapp/view" + view + ".fxml";
                    break;
            }

            System.out.println("Tentative de chargement: " + fxmlPath);

            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Fichier non trouvé: " + fxmlPath);
                showAlert("Erreur", "Fichier non trouvé: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());

            try {
                URL cssResource = getClass().getResource("/css/modern-style.css");
                if (cssResource != null) {
                    scene.getStylesheets().add(cssResource.toExternalForm());
                }
            } catch (Exception e) {
                System.out.println("CSS non chargé");
            }

            Stage stage = SceneManager.getInstance().getPrimaryStage();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page: " + view + "\n" + e.getMessage());
        }
    }

    protected void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur d'affichage d'alerte: " + e.getMessage());
        }
    }

    protected void showError(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur d'affichage d'erreur: " + e.getMessage());
        }
    }
}