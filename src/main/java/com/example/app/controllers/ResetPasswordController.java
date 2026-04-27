package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.UserService;
import com.example.app.utils.PasswordHasher;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;

public class ResetPasswordController extends BaseController {

    @FXML private TextField tokenField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button resetBtn;
    @FXML private Label messageLabel;
    
    private UserService userService;
    private PasswordHasher passwordHasher;
    private String token;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        passwordHasher = new PasswordHasher();
    }
    
    public void setToken(String token) {
        this.token = token;
        if (tokenField != null) {
            tokenField.setText(token);
            tokenField.setEditable(false);
        }
        System.out.println("🔑 Token reçu: " + token);
    }
    
    @FXML
    private void handleResetPassword() {
        String resetToken = (token != null && !token.isEmpty()) ? token : tokenField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (resetToken == null || resetToken.isEmpty()) {
            showMessage("Code invalide", "error");
            return;
        }
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", "error");
            return;
        }
        
        if (newPassword.length() < 6) {
            showMessage("Le mot de passe doit contenir au moins 6 caractères", "error");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showMessage("Les mots de passe ne correspondent pas", "error");
            return;
        }
        
        resetBtn.setDisable(true);
        resetBtn.setText("Réinitialisation...");
        
        Task<Boolean> resetTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                User user = userService.findByResetToken(resetToken);
                
                if (user == null) {
                    System.out.println("❌ Token invalide");
                    return false;
                }
                
                if (user.getResetTokenExpiresAt() != null && 
                    user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
                    System.out.println("❌ Token expiré");
                    return false;
                }
                
                System.out.println("✅ Token valide pour: " + user.getUsername());
                userService.updatePassword(user.getId(), newPassword);
                userService.clearResetToken(user.getId());
                
                return true;
            }
        };
        
        resetTask.setOnSucceeded(e -> {
            boolean success = resetTask.getValue();
            resetBtn.setDisable(false);
            resetBtn.setText("Réinitialiser");
            
            if (success) {
                showMessage("✅ Mot de passe réinitialisé !", "success");
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override public void run() { Platform.runLater(() -> navigateTo("/login")); }
                }, 2000);
            } else {
                showMessage("❌ Code invalide ou expiré", "error");
            }
        });
        
        resetTask.setOnFailed(e -> {
            resetBtn.setDisable(false);
            resetBtn.setText("Réinitialiser");
            showMessage("Erreur: " + resetTask.getException().getMessage(), "error");
        });
        
        new Thread(resetTask).start();
    }
    
    @FXML private void goToLogin() { navigateTo("/login"); }
    @FXML private void goToHome() { navigateTo("/"); }
    
    private void showMessage(String message, String type) {
        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setVisible(true);
            messageLabel.setStyle(type.equals("error") ? "-fx-text-fill: #EF5350; -fx-background-color: rgba(239,83,80,0.1); -fx-padding: 12; -fx-background-radius: 8;" : "-fx-text-fill: #18E3A4; -fx-background-color: rgba(24,227,164,0.1); -fx-padding: 12; -fx-background-radius: 8;");
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override public void run() { Platform.runLater(() -> messageLabel.setVisible(false)); }
            }, 5000);
        });
    }
}