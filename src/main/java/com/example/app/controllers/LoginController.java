package com.example.app.controllers;

import com.example.app.services.UserService;
import com.example.app.entities.User;
import com.example.app.utils.SessionManager;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.util.List;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;

    private UserService userService = new UserService();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            // Vérifier les identifiants
            List<User> users = userService.select();
            User loggedInUser = null;

            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    loggedInUser = user;
                    break;
                }
            }

            if (loggedInUser != null) {
                // Succès
                SessionManager.setCurrentUser(loggedInUser);
                SceneManager.showScene("common/home", "Midgar - Accueil");
            } else {
                errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegisterRedirect() {
        // TODO: Implémenter la page d'inscription
        SceneManager.showScene("common/register", "Midgar - Inscription");
    }
}

