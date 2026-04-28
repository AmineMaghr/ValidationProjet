package com.example.app.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "zeinebsgh466@gmail.com";
    private static final String EMAIL_PASSWORD = "dkylbjupzplouige";
    
    // ✅ Méthode existante - Inscription
    public void sendWelcomeEmail(String to, String username) {
        String subject = "Bienvenue sur Midgar !";
        String content = "<h1>Bienvenue " + username + " !</h1><p>Votre compte a été créé avec succès.</p>";
        sendEmail(to, subject, content);
    }
    
    // ✅ Méthode existante - Réinitialisation mot de passe
    public void sendResetPasswordEmail(String to, String username, String token) {
        String subject = "Réinitialisation de votre mot de passe - Midgar";
        String resetLink = "midgar://reset-password?token=" + token;
        String content = buildEmailContent(username, resetLink, token);
        sendEmail(to, subject, content);
    }
    
    // ✅ NOUVEAU - Notification de commentaire
    public void envoyerNotificationCommentaire(String emailProprietaire, String nomProprietaire,
            String contenuCommentaire, String titreElement,
            String typeElement, String auteurCommentaire) {
        
        System.out.println("📧 [EMAIL] Notification commentaire pour: " + emailProprietaire);
        
        String subject = "📝 Nouveau commentaire sur votre " + typeElement;
        String content = buildCommentEmailContent(nomProprietaire, auteurCommentaire, titreElement, typeElement, contenuCommentaire);
        sendEmail(emailProprietaire, subject, content);
    }
    
    // ✅ NOUVEAU - Notification nouvelle œuvre/artefact du même type que les favoris
    public void envoyerNotificationNouvelElementSimilaire(String emailDestinataire, String nomDestinataire,
            String nomElement, String typeElement, String typeElementCapitalized) {
        
        System.out.println("📧 [EMAIL] Notification recommandation pour: " + emailDestinataire);
        
        String subject = "✨ Nouvelle " + typeElementCapitalized + " qui pourrait vous plaire !";
        String content = buildRecommendationEmailContent(nomDestinataire, nomElement, typeElement, typeElementCapitalized);
        sendEmail(emailDestinataire, subject, content);
    }
    
    // ✅ Construction email commentaire
    private String buildCommentEmailContent(String nomProprietaire, String auteurCommentaire, 
            String titreElement, String typeElement, String contenuCommentaire) {
        return "<!DOCTYPE html>\n" +
            "<html><head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial; background: #0a0c10; color: #fff; padding: 40px;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; background: #11161c; border-radius: 20px; padding: 30px; border: 1px solid #18E3A4;'>\n" +
            "<h1 style='color: #18E3A4; text-align: center;'>📝 Nouveau commentaire</h1>\n" +
            "<p>Bonjour <strong style='color: #18E3A4;'>" + nomProprietaire + "</strong>,</p>\n" +
            "<p><strong style='color: #18E3A4;'>" + auteurCommentaire + "</strong> a commenté sur votre " + typeElement + " :</p>\n" +
            "<div style='background: #1a2530; padding: 15px; border-left: 4px solid #18E3A4; margin: 20px 0; border-radius: 10px;'>\n" +
            "<h3 style='color: #18E3A4; margin-top: 0;'>" + titreElement + "</h3>\n" +
            "<p style='color: #b0b9b6;'>" + contenuCommentaire + "</p>\n" +
            "</div>\n" +
            "<p>Connectez-vous à Midgar pour gérer vos commentaires.</p>\n" +
            "<hr style='border-color: #2a3540;'>\n" +
            "<p style='font-size: 11px; color: #555; text-align: center;'>© 2026 L'équipe Midgar</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    // ✅ Construction email recommandation favoris
    private String buildRecommendationEmailContent(String nomDestinataire, String nomElement, 
            String typeElement, String typeElementCapitalized) {
        return "<!DOCTYPE html>\n" +
            "<html><head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial; background: #0a0c10; color: #fff; padding: 40px;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; background: #11161c; border-radius: 20px; padding: 30px; border: 1px solid #18E3A4;'>\n" +
            "<h1 style='color: #18E3A4; text-align: center;'>✨ Nouvelle recommandation</h1>\n" +
            "<p>Bonjour <strong style='color: #18E3A4;'>" + nomDestinataire + "</strong>,</p>\n" +
            "<p>Une nouvelle " + typeElementCapitalized + " correspondant à vos goûts vient d'être ajoutée sur Midgar !</p>\n" +
            "<div style='background: #1a2530; padding: 15px; border-left: 4px solid #18E3A4; margin: 20px 0; border-radius: 10px;'>\n" +
            "<h3 style='color: #18E3A4; margin-top: 0;'>" + nomElement + "</h3>\n" +
            "<p style='color: #b0b9b6;'><strong>Type :</strong> " + typeElement + "</p>\n" +
            "</div>\n" +
            "<p>Connectez-vous dès maintenant pour la découvrir et l'ajouter à vos favoris !</p>\n" +
            "<hr style='border-color: #2a3540;'>\n" +
            "<p style='font-size: 11px; color: #555; text-align: center;'>© 2026 L'équipe Midgar</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    // ✅ Méthode existante - Réinitialisation
    private String buildEmailContent(String username, String resetLink, String token) {
        return "<!DOCTYPE html>\n" +
            "<html><head><meta charset='UTF-8'></head>\n" +
            "<body style='font-family: Arial; background: #0a0c10; color: #fff; padding: 40px;'>\n" +
            "<div style='max-width: 600px; margin: 0 auto; background: #11161c; border-radius: 20px; padding: 30px; border: 1px solid #18E3A4;'>\n" +
            "<h1 style='color: #18E3A4; text-align: center;'>🔐 Réinitialisation</h1>\n" +
            "<p>Bonjour <strong style='color: #18E3A4;'>" + username + "</strong>,</p>\n" +
            "<p>Cliquez sur le bouton ci-dessous :</p>\n" +
            "<div style='text-align: center; margin: 30px 0;'>\n" +
            "<a href='" + resetLink + "' style='background: #18E3A4; color: #000; padding: 12px 30px; text-decoration: none; border-radius: 25px; font-weight: bold;'>🔑 Réinitialiser dans l'application</a>\n" +
            "</div>\n" +
            "<p style='color: #ff4444; font-size: 12px;'>⚠️ Ce lien expire dans 5 minutes.</p>\n" +
            "<p style='font-size: 12px; color: #666;'>Si le lien ne fonctionne pas, copiez ce code :</p>\n" +
            "<p style='font-size: 16px; font-weight: bold; color: #18E3A4;'>" + token + "</p>\n" +
            "<hr style='border-color: #2a3540;'>\n" +
            "<p style='font-size: 11px; color: #555; text-align: center;'>© 2026 L'équipe Midgar</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    // ✅ Méthode d'envoi - identique à celle qui fonctionne déjà
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