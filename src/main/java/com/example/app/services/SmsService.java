package com.example.app.services;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

public class SmsService {
    
    private static final String ACCOUNT_SID = "ACfedb765bc9d992368b347f08040f46d1";
    private static final String AUTH_TOKEN = "bfd77d23566c21fe14b953020c57d11c";
    
    // ✅ Ton Service SID
    private static final String VERIFY_SERVICE_SID = "VAc1ab8dbeb7e41ce5d443620309b21d8d";
    
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        System.out.println("✅ Twilio Verify initialisé");
    }
    
    public boolean sendResetCode(String phoneNumber, String code) {
        try {
            String cleanNumber = cleanPhoneNumber(phoneNumber);
            System.out.println("📱 Envoi du code à: " + cleanNumber);
            
            Verification verification = Verification.creator(
                VERIFY_SERVICE_SID,
                cleanNumber,
                "sms"
            ).create();
            
            System.out.println("✅ Code envoyé avec succès!");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi SMS: " + e.getMessage());
            return false;
        }
    }
    
    // ✅ MÉTHODE DE VÉRIFICATION AVEC LE MÊME SERVICE SID
    public boolean verifyCode(String phoneNumber, String code) {
        try {
            String cleanNumber = cleanPhoneNumber(phoneNumber);
            System.out.println("🔍 Vérification du code: " + code + " pour " + cleanNumber);
            
            VerificationCheck verificationCheck = VerificationCheck.creator(
                VERIFY_SERVICE_SID  // ← Maintenant le bon SID est utilisé
            ).setTo(cleanNumber)
             .setCode(code)
             .create();
            
            boolean isValid = verificationCheck.getStatus().equals("approved");
            System.out.println("🔍 Résultat: " + (isValid ? "✅ VALIDE" : "❌ INVALIDE"));
            return isValid;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur vérification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String cleanPhoneNumber(String phone) {
        String cleaned = phone.replaceAll("[\\s-]", "");
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }
        if (cleaned.startsWith("+0")) {
            cleaned = "+216" + cleaned.substring(2);
        }
        System.out.println("📱 Numéro formaté: " + cleaned);
        return cleaned;
    }
}