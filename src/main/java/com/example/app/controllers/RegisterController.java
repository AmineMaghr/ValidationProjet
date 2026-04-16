package com.example.app.controllers;

import com.example.app.dao.AdvancedPreferenceDAO;
import com.example.app.dao.ArtefactDAO;
import com.example.app.dao.CommandeDAO;
import com.example.app.dao.CommentaireDAO;
import com.example.app.dao.DefiDAO;
import com.example.app.dao.FavorisDAO;
import com.example.app.dao.OeuvreDAO;
import com.example.app.dao.ParticipationDAO;
import com.example.app.dao.PersonnageDAO;
import com.example.app.dao.ProduitDAO;
import com.example.app.dao.QuestionDAO;
import com.example.app.dao.ReponseDAO;

import com.example.app.dao.UniverseDAO;
import com.example.app.dao.UserDAO;
import com.example.app.dao.IDAO;
import com.example.app.entities.User;
import com.example.app.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas");
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPrenom(firstNameField.getText());
            user.setNom(lastNameField.getText());
            user.setPassword(password);

            userService.add(user);
            showAlert("Succès", "Inscription réussie ! Vous pouvez vous connecter");
            navigateTo("/login");
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        navigateTo("/login");
    }
}