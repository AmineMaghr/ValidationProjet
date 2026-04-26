package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.UserService;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginController extends BaseController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;
    @FXML private Hyperlink forgotPasswordLink;

    private UserService userService;

    @FXML
    public void initialize() {
        userService = new UserService();
        setupEnterKeyHandler();
        loadSavedCredentials();
    }

    private void setupEnterKeyHandler() {
        // Permettre la connexion avec la touche Entrée
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    private void loadSavedCredentials() {
        // Charger les identifiants sauvegardés (optionnel)
        // Vous pouvez utiliser Preferences ou un fichier
        String savedEmail = Preferences.userNodeForPackage(LoginController.class)
            .get("saved_email", "");
        boolean savedRemember = Preferences.userNodeForPackage(LoginController.class)
            .getBoolean("remember_me", false);

        if (savedRemember && !savedEmail.isEmpty()) {
            emailField.setText(savedEmail);
            rememberMeCheckbox.setSelected(true);
            passwordField.requestFocus();
        }
    }

    private void saveCredentials(String email, boolean remember) {
        var prefs = Preferences.userNodeForPackage(LoginController.class);
        if (remember && email != null && !email.isEmpty()) {
            prefs.put("saved_email", email);
            prefs.putBoolean("remember_me", true);
        } else {
            prefs.remove("saved_email");
            prefs.putBoolean("remember_me", false);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation des champs
        if (email.isEmpty()) {
            showError("Veuillez saisir votre email");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Veuillez saisir votre mot de passe");
            passwordField.requestFocus();
            return;
        }

        // Désactiver le bouton pendant la connexion
        loginBtn.setDisable(true);
        loginBtn.setText("Connexion en cours...");

        // Validation en arrière-plan
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                return userService.login(email, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            if (user != null) {
                // Sauvegarder les identifiants si "Se souvenir de moi" est coché
                saveCredentials(email, rememberMeCheckbox.isSelected());

                // Connecter l'utilisateur
                UserSession.setCurrentUser(user);

                showSuccess("Bienvenue " + user.getPrenom() + " " + user.getNom() + " !");

                // Rediriger vers la page principale
                navigateTo("/");
            } else {
                showError("Email ou mot de passe incorrect");
                passwordField.clear();
                passwordField.requestFocus();
                loginBtn.setDisable(false);
                loginBtn.setText("Se Connecter");
            }
        });

        loginTask.setOnFailed(ex -> {
            Throwable exception = ex.getSource().getException();
            exception.printStackTrace();
            showError("Erreur de connexion: " + exception.getMessage());
            loginBtn.setDisable(false);
            loginBtn.setText("Se Connecter");
        });

        new Thread(loginTask).start();
    }

    @FXML
    private void handleForgotPassword() {
        // Créer une boîte de dialogue pour réinitialiser le mot de passe
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mot de passe oublié");
        dialog.setHeaderText("Réinitialisation du mot de passe");
        dialog.setContentText("Veuillez saisir votre email :");
        dialog.getDialogPane().setStyle("-fx-background-color: #11161c;");

        // Styliser la boîte de dialogue
        dialog.getDialogPane().lookup(".content-label").setStyle("-fx-text-fill: #fff;");

        dialog.showAndWait().ifPresent(email -> {
            if (!email.trim().isEmpty()) {
                showInfo("Un email de réinitialisation a été envoyé à " + email);
            }
        });
    }

    @FXML
    private void goToRegister() {
        navigateTo("/register");
    }

    @FXML
    private void goToHome() {
        navigateTo("/");
    }

    @FXML
    private void googleLogin() {
        showInfo("Connexion avec Google (à implémenter)");
        // Implémentation de l'OAuth Google
    }

    @FXML
    private void facialRecognition() {
        showInfo("Reconnaissance faciale (à implémenter)");
        // Implémentation de la reconnaissance faciale
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #EF5350;");

        // Masquer après 5 secondes
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> errorLabel.setVisible(false));
            }
        }, 5000);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #18E3A4;");

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> errorLabel.setVisible(false));
            }
        }, 3000);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #11161c; -fx-text-fill: #fff;");
        alert.showAndWait();
    }
}