package com.example.app.controllers;

import com.example.app.services.UserService;
import com.example.app.entities.User;
import com.example.app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Label errorLabel;

    private UserService userService = new UserService();

    @FXML
    public void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Le mot de passe doit faire au moins 6 caractères");
            return;
        }

        try {
            // Créer le nouvel utilisateur
            User newUser = new User(username, email, password, "user");
            userService.add(newUser);
            
            // Rediriger vers la page de connexion
            errorLabel.setText("");
            SceneManager.showScene("common/login", "Midgar - Connexion");
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLoginRedirect() {
        SceneManager.showScene("common/login", "Midgar - Connexion");
    }
}

