package com.example.app.services;

import com.example.app.utils.EnvLoader;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    
    private static final String ACCOUNT_SID = EnvLoader.get("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = EnvLoader.get("TWILIO_AUTH_TOKEN");
    private static final String FROM_NUMBER = "+19784643328"; // Ton numéro Twilio américain
    
    static {
        if (ACCOUNT_SID != null && AUTH_TOKEN != null) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            System.out.println("✅ Twilio initialisé avec numéro: " + FROM_NUMBER);
        }
    }
    
    public boolean sendResetCode(String phoneNumber, String code) {
        try {
            String cleanNumber = cleanPhoneNumber(phoneNumber);
            String message = "Midgar - Votre code: " + code + " (valable 5 minutes)";
            
            Message twilioMessage = Message.creator(
                new PhoneNumber(cleanNumber),
                new PhoneNumber(FROM_NUMBER),
                message
            ).create();
            
            System.out.println("✅ SMS envoyé à " + phoneNumber);
            System.out.println("📱 SID: " + twilioMessage.getSid());
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi SMS: " + e.getMessage());
            return false;
        }
    }
    
    // ✅ AJOUTER CETTE MÉTHODE MANQUANTE
    public boolean verifyCode(String phoneNumber, String code) {
        System.out.println("🔍 Vérification code: " + code + " pour " + phoneNumber);
        
        // Tu peux ajouter ici la logique de vérification
        // Pour l'instant, on simule la vérification
        // En production, utilise Twilio Verify ou stocke le code en base
        
        System.out.println("✅ Code VALIDE (simulation)");
        return true;
    }
    
    private String cleanPhoneNumber(String phone) {
        String cleaned = phone.replaceAll("[\\s-]", "");
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }
        return cleaned;
    }
}