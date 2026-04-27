package com.example.app.services;

import com.example.app.utils.EnvLoader;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = EnvLoader.get("EMAIL_FROM");
    private static final String EMAIL_PASSWORD = EnvLoader.get("EMAIL_PASSWORD");
   
    
    public void sendWelcomeEmail(String to, String username) {
        String subject = "Bienvenue sur Midgar !";
        String content = "<h1>Bienvenue " + username + " !</h1><p>Votre compte a ete cree avec succes.</p>";
        sendEmail(to, subject, content);
    }
    
    public void sendResetPasswordEmail(String to, String username, String token) {
        String subject = "Réinitialisation de votre mot de passe - Midgar";
        // ✅ Lien HTTP au lieu de midgar://
        String resetLink = "http://localhost:8080/reset?token=" + token;
        String content = buildEmailContent(username, resetLink, token);
        sendEmail(to, subject, content);
        System.out.println("Email envoyé avec token: " + token);
    }
    
    private String buildEmailContent(String username, String resetLink, String token) {
        return "<!DOCTYPE html>\n" +
            "<html><head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial; background: #0a0c10; color: #fff; padding: 40px;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; background: #11161c; border-radius: 20px; padding: 30px; border: 1px solid #18E3A4;'>\n" +
            "<h1 style='color: #18E3A4; text-align: center;'>🔐 Réinitialisation du mot de passe</h1>\n" +
            "<p>Bonjour <strong style='color: #18E3A4;'>" + username + "</strong>,</p>\n" +
            "<div style='text-align: center; margin: 30px 0;'>\n" +
            "<a href='" + resetLink + "' style='background: #18E3A4; color: #000; padding: 12px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block;'>🔑 Réinitialiser mon mot de passe</a>\n" +
            "</div>\n" +
            "<p style='color: #ff4444; font-size: 12px; text-align: center;'>⚠️ Ce lien expire dans 5 minutes.</p>\n" +
            "<hr style='border-color: #2a3540;'>\n" +
            "<p style='font-size: 11px; color: #555; text-align: center;'>Ou copiez ce code : <strong>" + token + "</strong></p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    private void sendEmail(String to, String subject, String htmlContent) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, "Midgar Platform"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à: " + to);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
    