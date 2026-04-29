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
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ProfileController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private UserService userService = new UserService();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            firstNameField.setText(currentUser.getPrenom());
            lastNameField.setText(currentUser.getNom());
        }
    }

    @FXML
    private void updateProfile() {
        currentUser.setUsername(usernameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setPrenom(firstNameField.getText());
        currentUser.setNom(lastNameField.getText());

        try {
            userService.update(currentUser);
            showAlert("Succès", "Profil mis à jour");
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void changePassword() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!newPass.equals(confirm)) {
            showAlert("Erreur", "Les nouveaux mots de passe ne correspondent pas");
            return;
        }

        try {
            if (userService.changePassword(currentUser.getId(), current, newPass)) {
                showAlert("Succès", "Mot de passe modifié");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                showAlert("Erreur", "Mot de passe actuel incorrect");
            }
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void deleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.delete(currentUser.getId());
                    UserSession.logout();
                    showAlert("Succès", "Compte supprimé");
                    navigateTo("/");
                } catch (Exception e) {
                    showAlert("Erreur", e.getMessage());
                }
            }
        });
    }
}