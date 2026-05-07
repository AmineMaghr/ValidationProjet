package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.UserService;
import com.example.app.services.EmailService;  // AJOUTÉ
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
            if (!newVal) {
                checkUsername();
            }
        });

        emailField.focusedProperty().addListener((obs, old, newVal) -> {
            if (!newVal) {
                checkEmail();
            }
        });

        // Validation mot de passe
        passwordField.textProperty().addListener((obs, old, val) -> validatePassword());

        // Validation confirmation
        confirmPasswordField.textProperty().addListener((obs, old, val) -> validateConfirm());
    }

    private void validatePrenom() {
        String prenom = prenomField.getText().trim();
        if (prenom.isEmpty()) {
            setFieldStyle(prenomField, false);
        } else if (prenom.length() < 2) {
            setFieldStyle(prenomField, false);
        } else {
            setFieldStyle(prenomField, true);
        }
        checkFormValidity();
    }

    private void validateNom() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            setFieldStyle(nomField, false);
        } else if (nom.length() < 2) {
            setFieldStyle(nomField, false);
        } else {
            setFieldStyle(nomField, true);
        }
        checkFormValidity();
    }

    private void checkUsername() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            usernameStatus.setText("Nom d'utilisateur requis");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        if (username.length() < 3) {
            usernameStatus.setText("Minimum 3 caractères");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        if (username.length() > 20) {
            usernameStatus.setText("Maximum 20 caractères");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false);
            isUsernameValid = false;
            checkFormValidity();
            return;
        }

        if (!username.matches("^[a-zA-Z0-9]+$")) {
            usernameStatus.setText("Lettres et chiffres uniquement");
            usernameStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(usernameField, false);
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
            usernameStatus.setText("✓ Disponible");
            usernameStatus.setStyle("-fx-text-fill: #18E3A4;");
            setFieldStyle(usernameField, true);
            isUsernameValid = true;
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
            setFieldStyle(emailField, false);
            isEmailValid = false;
            checkFormValidity();
            return;
        }

        if (!email.matches("^[^\\s@]+@([^\\s@]+\\.)+[^\\s@]+$")) {
            emailStatus.setText("Format d'email invalide");
            emailStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(emailField, false);
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
            emailStatus.setText("✓ Email disponible");
            emailStatus.setStyle("-fx-text-fill: #18E3A4;");
            setFieldStyle(emailField, true);
            isEmailValid = true;
            checkFormValidity();
        });

        new Thread(task).start();
    }

    private void validatePassword() {
        String password = passwordField.getText();

        if (password.isEmpty()) {
            passwordStatus.setText("Mot de passe requis");
            passwordStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(passwordField, false);
        } else if (password.length() < 6) {
            passwordStatus.setText("Minimum 6 caractères");
            passwordStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(passwordField, false);
        } else {
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
            setFieldStyle(passwordField, true);
        }

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
            setFieldStyle(confirmPasswordField, false);
        } else if (!password.equals(confirm)) {
            confirmStatus.setText("Les mots de passe ne correspondent pas");
            confirmStatus.setStyle("-fx-text-fill: #EF5350;");
            setFieldStyle(confirmPasswordField, false);
        } else {
            confirmStatus.setText("✓ Mots de passe identiques");
            confirmStatus.setStyle("-fx-text-fill: #18E3A4;");
            setFieldStyle(confirmPasswordField, true);
        }

        checkFormValidity();
    }

    private void setFieldStyle(TextField field, boolean isValid) {
        if (isValid) {
            field.setStyle("-fx-border-color: #18E3A4; -fx-border-radius: 12;");
        } else {
            field.setStyle("-fx-border-color: #EF5350; -fx-border-radius: 12;");
        }
    }

    private void setFieldStyle(PasswordField field, boolean isValid) {
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

        // 🔍 DEBUG
        System.out.println("Email field = [" + emailField.getText() + "]");

        User user = new User();
        user.setPrenom(prenomField.getText().trim());
        user.setNom(nomField.getText().trim());
        user.setUsername(usernameField.getText().trim());

        String emailInput = emailField.getText();
        if (emailInput != null) {
            emailInput = emailInput.trim();
        }

        user.setEmail(emailInput);
        user.setPassword(passwordField.getText());

        if (selectedPhotoFile != null) {
            user.setAvatar(selectedPhotoFile.getAbsolutePath());
        }

        // 🔒 Sécurité EMAIL (IMPORTANT)
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            errorLabel.setText("Email invalide !");
            errorLabel.setVisible(true);
            return;
        }

        if (!user.isValid()) {
            errorLabel.setText(user.getValidationErrorsAsString());
            errorLabel.setVisible(true);
            return;
        }

        registerBtn.setDisable(true);
        registerBtn.setText("Inscription en cours...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                userService.add(user);
                return null;
            }
        };

        task.setOnSucceeded(e -> {

            String email = user.getEmail();
            System.out.println("📧 Email envoyé = [" + email + "]");

            // 🔒 Sécurité avant envoi
            if (email != null && !email.trim().isEmpty()) {
                try {
                    EmailService mailService = new EmailService();
                    mailService.sendWelcomeEmail(email, user.getUsername());
                } catch (Exception ex) {
                    System.err.println("❌ Erreur envoi mail : " + ex.getMessage());
                }
            } else {
                System.out.println("❌ Email invalide, envoi ignoré !");
            }

            Platform.runLater(() -> {
                showAlert("Succès", "Inscription réussie !");
                navigateTo("/login");
            });
        });

        task.setOnFailed(e -> {
            registerBtn.setDisable(false);
            registerBtn.setText("S'inscrire");

            Throwable ex = e.getSource().getException();
            errorLabel.setText("Erreur: " + (ex != null ? ex.getMessage() : "Inconnue"));
            errorLabel.setVisible(true);

            ex.printStackTrace(); // 🔥 DEBUG important
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

   @Override
protected void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.getDialogPane().setStyle("-fx-background-color: #11161c; -fx-text-fill: #fff;");
    alert.showAndWait();
}
}