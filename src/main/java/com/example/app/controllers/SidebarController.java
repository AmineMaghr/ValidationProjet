package com.example.app.controllers;

import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;

public class SidebarController {

    @FXML
    public void initialize() {
        // Le controller est initialisé mais n'a pas besoin de contexte spécial
    }

    @FXML
    public void goAccueil() {
        SceneManager.getInstance().loadScene("/");
    }

    @FXML
    public void goDiscover() {
        SceneManager.getInstance().loadScene("/discover");
    }

    @FXML
    public void goUniverses() {
        SceneManager.getInstance().loadScene("/universes");
    }

    @FXML
    public void goPersonnages() {
        SceneManager.getInstance().loadScene("/personnages");
    }

    @FXML
    public void goOeuvres() {
        SceneManager.getInstance().loadScene("/oeuvre");
    }

    @FXML
    public void goArtefacts() {
        SceneManager.getInstance().loadScene("/artefact");
    }

    @FXML
    public void goShop() {
        SceneManager.getInstance().loadScene("/shop");
    }

    @FXML
    public void goChallenges() {
        SceneManager.getInstance().loadScene("/challenges");
    }

    @FXML
    public void goProfile() {
        SceneManager.getInstance().loadScene("/profile");
    }

    @FXML
    public void goAdmin() {
        SceneManager.getInstance().loadScene("/admin");
    }

    @FXML
    public void goLogin() {
        SceneManager.getInstance().loadScene("/login");
    }
}