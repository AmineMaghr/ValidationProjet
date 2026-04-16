package com.example.app.controllers;

import com.example.app.services.UserService;
import com.example.app.utils.UserSession;
import com.example.app.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            var user = userService.authenticate(username, password);
            if (user != null) {
                UserSession.setCurrentUser(user);
                showAlert("Succès", "Bienvenue " + user.getUsername() + " !");
                navigateTo("/");
            } else {
                showAlert("Erreur", "Identifiants incorrects");
            }
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        navigateTo("/register");
    }
}