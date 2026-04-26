package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.UserService;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class RegisterController extends BaseController {

    @FXML private TextField prenomField;
    @FXML private TextField nomField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button selectPhotoBtn;
    @FXML private ImageView photoPreview;
    @FXML private Label usernameStatus;
    @FXML private Label emailStatus;
    @FXML private Label passwordStatus;
    @FXML private Label confirmStatus;
    @FXML private Label errorLabel;
    @FXML private Button registerBtn;

    private UserService userService;
    private File selectedPhotoFile;
    private boolean isUsernameValid = false;
    private boolean isEmailValid = false;
    private java.util.Timer usernameTimer;
    private java.util.Timer emailTimer;

    @FXML
    public void initialize() {
        userService = new UserService();
        registerBtn.setDisable(true);
        setupValidation();
    }

    private void setupValidation() {
        // Validation prénom
        prenomField.textProperty().addListener((obs, old, val) -> validatePrenom());

        // Validation nom
        nomField.textProperty().addListener((obs, old, val) -> validateNom());

        // Validation username avec debounce
        usernameField.textProperty().addListener((obs, old, val) -> {
            if (usernameTimer != null) usernameTimer.cancel();
            usernameTimer = new java.util.Timer();
            usernameTimer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> checkUsername());
                }
            }, 500);
        });

        // Validation email avec debounce
        emailField.textProperty().addListener((obs, old, val) -> {
            if (emailTimer != null) emailTimer.cancel();
            emailTimer = new java.util.Timer();
            emailTimer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> checkEmail());
                }
            }, 500);
        });

        // Validation on focus lost
        usernameField.focusedProperty().addListener((obs, old, newVal) -> {
            if (!newVal) { // lost focus
                checkUsername();
            }
        });

        emailField.focusedProperty().addListener((obs, old, newVal) -> {
            if (!newVal) { // lost focus
                checkEmail();
            }
        });

        // Validation email avec debounce
        emailField.textProperty().addListener((obs, old, val) -> {
            if (emailTimer != null) emailTimer.cancel();
            emailTimer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> checkEmail());
                }
            }, 500);
        });

        // Validation mot de passe
        passwordField.textProperty().addListener((obs, old, val) -> validatePassword());

        // Validation confirmation
        confirmPasswordField.textProperty().addListener((obs, old, val) -> validateConfirm());
    }

    private void validatePrenom() {
        String prenom = prenomField.getText().trim();
        if (prenom.isEmpty()) {
            setFieldStyle(prenomField, false, "Prénom requis");
        } else if (prenom.length() < 2) {
            setFieldStyle(prenomField, false, "Minimum 2 caractères");
        } else {
            setFieldStyle(prenomField, true, "✓");
        }
        checkFormValidity();
    }

    private void validateNom() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            setFieldStyle(nomField, false, "Nom requis");
        } else if (nom.length() < 2) {
            setFieldStyle(nomField, false, "Minimum 2 caractères");
        } else {
            setFieldStyle(nomField, true, "✓");
        }
        checkFormValidity();
    }

    private void checkUsername() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            usernameStatus.setText("Nom d'utilisateur requis");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false, null);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        if (username.length() < 3) {
            usernameStatus.setText("Minimum 3 caractères");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false, null);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        if (username.length() > 20) {
            usernameStatus.setText("Maximum 20 caractères");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false, null);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        if (!username.matches("^[a-zA-Z0-9]+$")) {
            usernameStatus.setText("Lettres et chiffres uniquement");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false, null);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        // Vérification serveur
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return userService.isUsernameTaken(username);
            }
        };

        task.setOnSucceeded(e -> {
            // Temporarily disable taken check for testing
            /*
            if (task.getValue()) {
                usernameStatus.setText("✗ Nom d'utilisateur déjà pris");
                usernameStatus.setStyle("-fx-text-fill: #EF5350;");
                setFieldStyle(usernameField, false, null);
                isUsernameValid = false;
                // Générer suggestions
                generateUsernameSuggestions(username);
            } else {
            */
                usernameStatus.setText("✓ Disponible");
                usernameStatus.setStyle("-fx-text-fill: #18E3A4;");
                setFieldStyle(usernameField, true, null);
                isUsernameValid = true;
            /*
            }
            */
            checkFormValidity();
        });

        task.setOnFailed(e -> {
            usernameStatus.setText("Erreur de vérification");
            usernameStatus.setStyle("-fx-text-fill: #FFA726;");
            isUsernameValid = false;
            checkFormValidity();
        });

        new Thread(task).start();
    }

    private void generateUsernameSuggestions(String baseUsername) {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return userService.generateUsernameSuggestions(baseUsername);
            }
        };

        task.setOnSucceeded(e -> {
            List<String> suggestions = task.getValue();
            if (suggestions != null && !suggestions.isEmpty()) {
                showSuggestions(suggestions);
            }
        });

        new Thread(task).start();
    }

    private void showSuggestions(List<String> suggestions) {
        // Afficher les suggestions (à implémenter dans le FXML)
        StringBuilder sb = new StringBuilder("Suggestions: ");
        for (String s : suggestions) {
            sb.append(s).append(" ");
        }
        usernameStatus.setText(sb.toString());
        usernameStatus.setStyle("-fx-text-fill: #FFA726; -fx-font-size: 11px;");
    }

    private void checkEmail() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            emailStatus.setText("Email requis");
            emailStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(emailField, false, null);
            isEmailValid = false;
            checkFormValidity();
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            emailStatus.setText("Format d'email invalide");
            emailStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(emailField, false, null);
            isEmailValid = false;
            checkFormValidity();
            return;
        }

        // Vérification serveur
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return userService.isEmailTaken(email);
            }
        };

        task.setOnSucceeded(e -> {
            // Temporarily disable taken check for testing
            /*
            if (task.getValue()) {
                emailStatus.setText("✗ Email déjà utilisé");
                emailStatus.setStyle("-fx-text-fill: #EF5350;");
                setFieldStyle(emailField, false, null);
                isEmailValid = false;
            } else {
            */
                emailStatus.setText("✓ Email disponible");
                emailStatus.setStyle("-fx-text-fill: #18E3A4;");
                setFieldStyle(emailField, true, null);
                isEmailValid = true;
            /*
            }
            */
            checkFormValidity();
        });

        new Thread(task).start();
    }

    private void validatePassword() {
        String password = passwordField.getText();

        if (password.isEmpty()) {
            passwordStatus.setText("Mot de passe requis");
            passwordStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(passwordField, false, null);
        } else if (password.length() < 6) {
            passwordStatus.setText("Minimum 6 caractères");
            passwordStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(passwordField, false, null);
        } else {
            // Force du mot de passe
            int strength = calculatePasswordStrength(password);
            if (strength < 50) {
                passwordStatus.setText("Faible");
                passwordStatus.setStyle("-fx-text-fill: #EF5350;");
            } else if (strength < 75) {
                passwordStatus.setText("Moyen");
                passwordStatus.setStyle("-fx-text-fill: #FFA726;");
            } else {
                passwordStatus.setText("Fort ✓");
                passwordStatus.setStyle("-fx-text-fill: #18E3A4;");
            }
            setFieldStyle(passwordField, true, null);
        }

        // Re-valider la confirmation
        if (!confirmPasswordField.getText().isEmpty()) {
            validateConfirm();
        }

        checkFormValidity();
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength += 25;
        if (password.matches(".*[A-Z].*")) strength += 25;
        if (password.matches(".*[a-z].*")) strength += 25;
        if (password.matches(".*[0-9].*") || password.matches(".*[^A-Za-z0-9].*")) strength += 25;

        return strength;
    }

    private void validateConfirm() {
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (confirm.isEmpty()) {
            confirmStatus.setText("Confirmation requise");
            confirmStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(confirmPasswordField, false, null);
        } else if (!password.equals(confirm)) {
            confirmStatus.setText("Les mots de passe ne correspondent pas");
            confirmStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(confirmPasswordField, false, null);
        } else {
            confirmStatus.setText("✓ Mots de passe identiques");
            confirmStatus.setStyle("-fx-text-fill: #18E3A4;");
            setFieldStyle(confirmPasswordField, true, null);
        }

        checkFormValidity();
    }

    private void setFieldStyle(TextField field, boolean isValid, String message) {
        if (isValid) {
            field.setStyle("-fx-border-color: #18E3A4; -fx-border-radius: 12;");
        } else {
            field.setStyle("-fx-border-color: #EF5350; -fx-border-radius: 12;");
        }
    }

    private void setFieldStyle(PasswordField field, boolean isValid, String message) {
        if (isValid) {
            field.setStyle("-fx-border-color: #18E3A4; -fx-border-radius: 12;");
        } else {
            field.setStyle("-fx-border-color: #EF5350; -fx-border-radius: 12;");
        }
    }

    private void checkFormValidity() {
        boolean isValid = isUsernameValid && isEmailValid &&
                         !prenomField.getText().trim().isEmpty() &&
                         !nomField.getText().trim().isEmpty() &&
                         !passwordField.getText().isEmpty() &&
                         passwordField.getText().equals(confirmPasswordField.getText());

        registerBtn.setDisable(!isValid);
    }

    @FXML
    private void choosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.webp")
        );

        File file = fileChooser.showOpenDialog(selectPhotoBtn.getScene().getWindow());
        if (file != null) {
            selectedPhotoFile = file;
            try {
                Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                photoPreview.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur chargement image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);

        User user = new User();
        user.setPrenom(prenomField.getText().trim());
        user.setNom(nomField.getText().trim());
        user.setUsername(usernameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPassword(passwordField.getText());

        if (selectedPhotoFile != null) {
            user.setAvatar(selectedPhotoFile.getAbsolutePath());
        }

        if (!user.isValid()) {
            errorLabel.setText(user.getValidationErrorsAsString());
            errorLabel.setVisible(true);
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                userService.add(user);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Succès", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            navigateTo("/login");
        });

        task.setOnFailed(e -> {
            Throwable ex = e.getSource().getException();
            errorLabel.setText("Erreur: " + (ex != null ? ex.getMessage() : "Inconnue"));
            errorLabel.setVisible(true);
        });

        new Thread(task).start();
    }

    @FXML
    private void goToLogin() {
        navigateTo("/login");
    }

    @FXML
    private void goToHome() {
        navigateTo("/");
    }
}