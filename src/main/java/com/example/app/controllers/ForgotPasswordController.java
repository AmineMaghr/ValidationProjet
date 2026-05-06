package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.UserService;
import com.example.app.services.EmailService;
import com.example.app.services.SmsService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;

public class ForgotPasswordController extends BaseController {

    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Button sendEmailBtn;
    @FXML private Button sendSmsBtn;
    @FXML private Label messageLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    private UserService userService;
    private EmailService emailService;
    private SmsService smsService;
    private String currentPhone;
    private String pendingToken;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        emailService = new EmailService();
        smsService = new SmsService();
    }
    
    // ==================== EMAIL ====================
    
    @FXML
    private void sendResetEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) { showMessage("Veuillez saisir votre email", "error"); return; }
        
        sendEmailBtn.setDisable(true);
        loadingIndicator.setVisible(true);
        
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                User user = userService.findByEmail(email);
                if (user == null) return false;
                
                String token = java.util.UUID.randomUUID().toString();
                userService.saveResetToken(user.getId(), token, LocalDateTime.now().plusMinutes(5));
                emailService.sendResetPasswordEmail(user.getEmail(), user.getUsername(), token);
                return true;
            }
        };
        
        task.setOnSucceeded(e -> {
            boolean success = task.getValue();
            sendEmailBtn.setDisable(false);
            loadingIndicator.setVisible(false);
            
            if (success) {
                showMessage("✅ Email envoyé à " + email, "success");
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override public void run() { Platform.runLater(() -> navigateTo("/login")); }
                }, 3000);
            } else {
                showMessage("❌ Aucun compte trouvé", "error");
            }
        });
        
        task.setOnFailed(e -> {
            sendEmailBtn.setDisable(false);
            loadingIndicator.setVisible(false);
            showMessage("Erreur: " + task.getException().getMessage(), "error");
        });
        
        new Thread(task).start();
    }
    
    // ==================== SMS ====================
    
    @FXML
    private void sendResetSms() {
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) { showMessage("Veuillez saisir votre numéro", "error"); return; }
        
        currentPhone = phone;
        sendSmsBtn.setDisable(true);
        loadingIndicator.setVisible(true);
        
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                User user = userService.findByPhoneNumber(phone);
                if (user == null) return false;
                
                pendingToken = java.util.UUID.randomUUID().toString();
                userService.saveResetToken(user.getId(), pendingToken, LocalDateTime.now().plusMinutes(5));
                
                String code = String.format("%06d", (int)(Math.random() * 1000000));
                return smsService.sendResetCode(phone, code);
            }
        };
        
        task.setOnSucceeded(e -> {
            boolean success = task.getValue();
            sendSmsBtn.setDisable(false);
            loadingIndicator.setVisible(false);
            
            if (success) {
                showMessage("✅ Code envoyé au " + phone, "success");
                askForCode();
            } else {
                showMessage("❌ Aucun compte trouvé", "error");
            }
        });
        
        task.setOnFailed(e -> {
            sendSmsBtn.setDisable(false);
            loadingIndicator.setVisible(false);
            showMessage("Erreur: " + task.getException().getMessage(), "error");
        });
        
        new Thread(task).start();
    }
    
    private void askForCode() {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Code SMS");
            dialog.setHeaderText("Code reçu par SMS");
            dialog.setContentText("Entrez le code à 6 chiffres:");
            dialog.getDialogPane().setStyle("-fx-background-color: #11161c;");
            
            dialog.showAndWait().ifPresent(code -> {
                if (code != null && !code.trim().isEmpty()) {
                    verifyCode(code.trim());
                }
            });
        });
    }
    
    private void verifyCode(String userCode) {
        loadingIndicator.setVisible(true);
        
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return smsService.verifyCode(currentPhone, userCode);
            }
        };
        
        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            boolean valid = task.getValue();
            
            if (valid) {
                showMessage("✅ Code valide !", "success");
                // Rediriger vers reset-password avec le token
                Platform.runLater(() -> {
                    try {
                        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/monapp/view/reset-password-view.fxml"));
                        javafx.scene.Parent root = loader.load();
                        ResetPasswordController controller = loader.getController();
                        controller.setToken(pendingToken);
                        javafx.stage.Stage stage = (javafx.stage.Stage) sendSmsBtn.getScene().getWindow();
                        stage.getScene().setRoot(root);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            } else {
                showMessage("❌ Code invalide", "error");
            }
        });
        
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            showMessage("Erreur: " + task.getException().getMessage(), "error");
        });
        
        new Thread(task).start();
    }
    
    // ==================== NAVIGATION ====================
    
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