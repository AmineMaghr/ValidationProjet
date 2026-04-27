package com.example.app.services;

import com.example.app.utils.EnvLoader;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class SmsService {
    
    private static final boolean SIMULATION_MODE = EnvLoader.getBoolean("SIMULATION_MODE");
    private static final String ACCOUNT_SID = EnvLoader.get("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = EnvLoader.get("TWILIO_AUTH_TOKEN");
    private static final String VERIFY_SERVICE_SID = EnvLoader.get("TWILIO_VERIFY_SID");
    
    static {
        if (!SIMULATION_MODE && ACCOUNT_SID != null && AUTH_TOKEN != null) {
            com.twilio.Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            System.out.println("✅ Twilio Verify initialisé");
        } else if (SIMULATION_MODE) {
            System.out.println("📱 Mode SIMULATION activé");
        } else {
            System.err.println("❌ Configuration Twilio manquante");
        }
    }
    
    public boolean sendResetCode(String phoneNumber, String code) {
        System.out.println("📱 Envoi code à " + phoneNumber + ": " + code);
        
        if (SIMULATION_MODE) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("📱 Code de réinitialisation");
                alert.setHeaderText("Mode Simulation");
                alert.setContentText("Code: " + code + "\n\nUtilisez ce code pour continuer.");
                alert.getDialogPane().setStyle("-fx-background-color: #11161c;");
                alert.showAndWait();
            });
            return true;
        }
        
        // ✅ Code réel Twilio avec WhatsApp (gratuit et non bloqué)
        try {
            String cleanNumber = cleanPhoneNumber(phoneNumber);
            System.out.println("📱 Envoi via WhatsApp à: " + cleanNumber);
            
            // Changer "sms" en "whatsapp" pour éviter les blocages
            com.twilio.rest.verify.v2.service.Verification verification = 
                com.twilio.rest.verify.v2.service.Verification.creator(
                    VERIFY_SERVICE_SID,
                    cleanNumber,
                    "whatsapp"  // ← WhatsApp au lieu de SMS
                ).create();
            
            System.out.println("✅ Code envoyé via WhatsApp!");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi WhatsApp: " + e.getMessage());
            return false;
        }
    }
    
    public boolean verifyCode(String phoneNumber, String code) {
        if (SIMULATION_MODE) {
            System.out.println("🔍 Vérification code: " + code + " -> VALIDE (simulation)");
            return true;
        }
        
        try {
            String cleanNumber = cleanPhoneNumber(phoneNumber);
            com.twilio.rest.verify.v2.service.VerificationCheck verificationCheck = 
                com.twilio.rest.verify.v2.service.VerificationCheck.creator(VERIFY_SERVICE_SID)
                    .setTo(cleanNumber)
                    .setCode(code)
                    .create();
            return verificationCheck.getStatus().equals("approved");
        } catch (Exception e) {
            System.err.println("❌ Erreur vérification: " + e.getMessage());
            return false;
        }
    }
    
    private String cleanPhoneNumber(String phone) {
        String cleaned = phone.replaceAll("[\\s-]", "");
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }
        if (cleaned.startsWith("+0")) {
            cleaned = cleaned.substring(0, 2) + "216" + cleaned.substring(2);
        }
        return cleaned;
    }
}