package com.example.app.controllers;

import com.example.app.utils.SceneManager;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public abstract class BaseController {

    @FXML
    public void goAccueil() {
        navigateTo("/");
    }

    @FXML
    public void goHome() {
        navigateTo("/");
    }

    @FXML
    public void goDiscover() {
        navigateTo("/discover");
    }

    @FXML
    public void goUniverses() {
        navigateTo("/universes");
    }

    @FXML
    public void goPersonnages() {
        navigateTo("/personnages");
    }

    @FXML
    public void goOeuvres() {
        navigateTo("/oeuvre");
    }

    @FXML
    public void goShop() {
        navigateTo("/shop");
    }

    @FXML
    public void goChallenges() {
        navigateTo("/challenges");
    }

    @FXML
    public void lancerQuiz() {
        navigateTo("/quiz");
    }

    @FXML
    public void goAdmin() {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            navigateTo("/admin");
        } else {
            showAlert("Accès refusé", "Vous n'êtes pas administrateur");
        }
    }

    @FXML
    public void goProfile() {
        navigateTo("/profile");
    }

    @FXML
    public void goLogin() {
        navigateTo("/login");
    }

    @FXML
    public void goArtefacts() {
        navigateTo("/artefact");
    }

    @FXML
    public void goRegister() {
        navigateTo("/register");
    }

    protected void navigateTo(String path) {
        try {
            if (SceneManager.getInstance() == null) {
                System.err.println("SceneManager non initialisé");
                return;
            }
            SceneManager.getInstance().loadScene(path);
        } catch (Exception e) {
            System.err.println("Erreur de navigation vers " + path + ": " + e.getMessage());
            e.printStackTrace();
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