package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.UserService;
import com.example.app.services.GoogleOAuthService;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.scene.Scene;
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
    private GoogleOAuthService googleOAuthService;

    @FXML
    public void initialize() {
        userService = new UserService();
        googleOAuthService = new GoogleOAuthService();
        setupEnterKeyHandler();
        loadSavedCredentials();
    }

    private void setupEnterKeyHandler() {
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

        loginBtn.setDisable(true);
        loginBtn.setText("Connexion en cours...");

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                return userService.login(email, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            if (user != null) {
                saveCredentials(email, rememberMeCheckbox.isSelected());
                UserSession.setCurrentUser(user);
                showSuccess("Bienvenue " + user.getPrenom() + " " + user.getNom() + " !");
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

    // ==================== GOOGLE LOGIN AUTOMATIQUE AVEC WEBVIEW ====================
    
    @FXML
    private void googleLogin() {
        loginBtn.setDisable(true);
        loginBtn.setText("Ouverture Google...");
        
        // Créer une fenêtre avec WebView
        Stage stage = new Stage();
        stage.setTitle("Connexion Google - Midgar");
        stage.setWidth(500);
        stage.setHeight(650);
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        // Charger l'URL d'authentification Google
        String authUrl = googleOAuthService.getAuthorizationUrl();
        System.out.println("🌐 Chargement de: " + authUrl);
        webEngine.load(authUrl);
        
        // Écouter les changements d'URL pour capturer le code automatiquement
        webEngine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            System.out.println("📍 URL: " + newUrl);
            
            // Vérifier si c'est l'URL de callback avec le code
            if (newUrl != null && newUrl.contains("code=")) {
                // Extraire le code automatiquement
                String code = extractCodeFromUrl(newUrl);
                System.out.println("🔑 Code capturé automatiquement: " + code);
                
                if (code != null && !code.isEmpty()) {
                    // Fermer la fenêtre WebView
                    stage.close();
                    
                    // Traiter le code
                    loginBtn.setText("Connexion en cours...");
                    
                    new Thread(() -> {
                        try {
                            GoogleOAuthService.GoogleUserInfo googleUser = googleOAuthService.exchangeCodeForUserInfo(code);
                            User user = processGoogleUser(googleUser);
                            
                            Platform.runLater(() -> {
                                if (user != null) {
                                    UserSession.setCurrentUser(user);
                                    showSuccess("Bienvenue " + user.getPrenom() + " " + user.getNom() + " !");
                                    navigateTo("/");
                                } else {
                                    showError("Erreur lors de la création du compte");
                                    loginBtn.setDisable(false);
                                    loginBtn.setText("Se Connecter");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                showError("Erreur: " + e.getMessage());
                                loginBtn.setDisable(false);
                                loginBtn.setText("Se Connecter");
                            });
                        }
                    }).start();
                }
            }
        });
        
        // Gérer les erreurs de chargement
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.err.println("❌ Erreur de chargement: " + webEngine.getLoadWorker().getException());
            }
        });
        
        Scene scene = new Scene(webView);
        stage.setScene(scene);
        stage.show();
        
        // Si la fenêtre est fermée manuellement, réactiver le bouton
        stage.setOnCloseRequest(e -> {
            loginBtn.setDisable(false);
            loginBtn.setText("Se Connecter");
        });
    }
    
  private String extractCodeFromUrl(String url) {
    if (url != null && url.contains("code=")) {
        int start = url.indexOf("code=") + 5;
        int end = url.indexOf("&", start);
        if (end == -1) {
            end = url.length();
        }
        String encodedCode = url.substring(start, end);
        
        // Décoder le code automatiquement
        try {
            String decodedCode = java.net.URLDecoder.decode(encodedCode, "UTF-8");
            System.out.println("🔑 Code encodé: " + encodedCode);
            System.out.println("🔑 Code décodé: " + decodedCode);
            return decodedCode;
        } catch (Exception e) {
            System.err.println("Erreur décodage: " + e.getMessage());
            return encodedCode;
        }
    }
    return null;
}
    
    private User processGoogleUser(GoogleOAuthService.GoogleUserInfo googleUser) throws SQLException {
        System.out.println("📝 Traitement de: " + googleUser.getEmail());
        
        User user = userService.findByGoogleId(googleUser.getId());
        if (user != null) {
            System.out.println("✅ Utilisateur existant (Google ID)");
            return user;
        }
        
        User existingUser = userService.findByEmail(googleUser.getEmail());
        if (existingUser != null) {
            System.out.println("📧 Email existant, liaison Google ID");
            existingUser.setGoogleId(googleUser.getId());
            existingUser.setAuthProvider("google");
            userService.update(existingUser);
            return existingUser;
        }
        
        System.out.println("🆕 Création d'un nouveau compte");
        User newUser = new User();
        newUser.setEmail(googleUser.getEmail());
        newUser.setGoogleId(googleUser.getId());
        newUser.setAuthProvider("google");
        newUser.setPrenom(googleUser.getGivenName() != null && !googleUser.getGivenName().isEmpty() 
            ? googleUser.getGivenName() : "Google");
        newUser.setNom(googleUser.getFamilyName() != null && !googleUser.getFamilyName().isEmpty() 
            ? googleUser.getFamilyName() : "User");
        newUser.setAvatar(googleUser.getPicture());
        newUser.setVerified(true);
        newUser.setBlocked(false);
        
        String baseUsername = googleUser.getEmail().split("@")[0];
        String username = baseUsername;
        int counter = 1000;
        while (userService.isUsernameTaken(username)) {
            username = baseUsername + counter++;
        }
        newUser.setUsername(username);
        newUser.setPassword(generateRandomPassword());
        
        userService.add(newUser);
        System.out.println("✅ Nouveau compte créé avec ID: " + newUser.getId());
        
        return newUser;
    }
    
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    // ==================== AUTRES MÉTHODES ====================
    
    @FXML
private void handleForgotPassword() {
    System.out.println("🔐 Redirection vers la page mot de passe oublié");
    navigateTo("/forgot-password");
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
    private void facialRecognition() {
        showInfo("Reconnaissance faciale (à implémenter)");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #EF5350;");
        
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
        alert.getDialogPane().setStyle("-fx-background-color: #11161c;");
        alert.showAndWait();
    }
    
}