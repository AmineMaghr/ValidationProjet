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

    // =========================
    // EMAIL VALIDATION
    // =========================
    public boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        try {
            InternetAddress addr = new InternetAddress(email);
            addr.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    // =========================
    // PLAIN TEXT EMAIL (sendText)
    // =========================
    public void sendText(String toEmail, String subject, String textBody) {
        String htmlContent = buildPlainTextEmail(textBody);
        sendEmail(toEmail, subject, htmlContent);
    }

    // =========================
    // WELCOME EMAIL
    // =========================
    public void sendWelcomeEmail(String to, String username) {
        String subject = "Bienvenue sur Midgar !";
        String content = buildWelcomeEmail(username);
        sendEmail(to, subject, content);
    }

    // =========================
    // RESET PASSWORD
    // =========================
    public void sendResetPasswordEmail(String to, String username, String token) {
        String subject = "Réinitialisation de votre mot de passe - Midgar";
        String resetLink = "http://localhost:8080/reset?token=" + token;
        String content = buildResetEmail(username, resetLink, token);
        sendEmail(to, subject, content);
        System.out.println("📧 Reset email envoyé avec token: " + token);
    }

    // =========================
    // COMMENT NOTIFICATION - VERSION AVEC TEXTE BLANC
    // =========================
    public void envoyerNotificationCommentaire(
            String emailProprietaire,
            String nomProprietaire,
            String contenuCommentaire,
            String titreElement,
            String typeElement,
            String auteurCommentaire
    ) {
        String subject = "📝 Nouveau commentaire sur votre " + typeElement + " : " + titreElement;
        String content = buildCommentEmail(
                nomProprietaire,
                auteurCommentaire,
                titreElement,
                typeElement,
                contenuCommentaire
        );
        sendEmail(emailProprietaire, subject, content);
    }

    // =========================
    // RECOMMENDATION EMAIL
    // =========================
    public void envoyerNotificationNouvelElementSimilaire(
            String emailDestinataire,
            String nomDestinataire,
            String nomElement,
            String typeElement,
            String typeElementCapitalized
    ) {
        String subject = "✨ Nouvelle " + typeElementCapitalized + " qui pourrait vous plaire !";
        String content = buildRecommendationEmail(
                nomDestinataire,
                nomElement,
                typeElement,
                typeElementCapitalized
        );
        sendEmail(emailDestinataire, subject, content);
    }

    // =========================
    // WELCOME TEMPLATE (Midgar Style)
    // =========================
    private String buildWelcomeEmail(String username) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background: #0a0c10; color: #ffffff !important; padding: 40px; }
                .container { max-width: 600px; margin: auto; background: #11161c; padding: 30px; border-radius: 20px; border: 1px solid #18E3A4; }
                h1 { color: #18E3A4; text-align: center; }
                p { color: #ffffff !important; }
                b { color: #ffffff !important; }
                .logo { text-align: center; font-size: 28px; font-weight: bold; margin-bottom: 20px; }
                .logo span:first-child { color: #ffffff; }
                .logo span:last-child { color: #18E3A4; }
                .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #aaa; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="logo">
                    <span>Mid</span><span>gar</span>
                </div>
                <h1>Bienvenue sur Midgar !</h1>
                <p>Bonjour <b>%s</b>,</p>
                <p>Nous sommes ravis de vous compter parmi nous.</p>
                <p>Votre compte a été créé avec succès.</p>
                <div class="footer">
                    <p>© 2025 Midgar - Tous droits réservés</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(username);
    }

    // =========================
    // RESET TEMPLATE (Midgar Style)
    // =========================
    private String buildResetEmail(String username, String resetLink, String token) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background: #0a0c10; color: #ffffff !important; padding: 40px; }
                .container { max-width: 600px; margin: auto; background: #11161c; padding: 30px; border-radius: 20px; border: 1px solid #18E3A4; }
                h2 { color: #18E3A4; text-align: center; }
                p { color: #ffffff !important; }
                b { color: #ffffff !important; }
                .btn { display: inline-block; background: #18E3A4; color: black; padding: 12px 25px; border-radius: 25px; text-decoration: none; font-weight: bold; margin: 20px 0; }
                .btn:hover { background: #13c48a; }
                .token { font-size: 12px; color: #aaa; word-break: break-all; }
                .logo { text-align: center; font-size: 28px; font-weight: bold; margin-bottom: 20px; }
                .logo span:first-child { color: #ffffff; }
                .logo span:last-child { color: #18E3A4; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="logo">
                    <span>Mid</span><span>gar</span>
                </div>
                <h2>Réinitialisation du mot de passe</h2>
                <p>Bonjour <b>%s</b>,</p>
                <p>Vous avez demandé la réinitialisation de votre mot de passe.</p>
                <div style="text-align:center;">
                    <a href="%s" class="btn">Réinitialiser mon mot de passe</a>
                </div>
                <p class="token">Ou utilisez ce token : <b>%s</b></p>
                <p style="font-size:12px;color:#aaa;">Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>
            </div>
        </body>
        </html>
        """.formatted(username, resetLink, token);
    }

    // =========================
    // COMMENT TEMPLATE (Midgar Style - TEXTE BLANC)
    // =========================
    private String buildCommentEmail(
            String nomProprietaire,
            String auteur,
            String titre,
            String type,
            String commentaire
    ) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { 
                    font-family: Arial, sans-serif; 
                    background: #0a0c10; 
                    color: #ffffff !important; 
                    padding: 40px; 
                }
                .container { 
                    max-width: 600px; 
                    margin: auto; 
                    background: #11161c; 
                    padding: 30px; 
                    border-radius: 20px; 
                    border: 1px solid #18E3A4; 
                }
                h2 { color: #18E3A4; }
                h3 { color: #18E3A4; }
                p { color: #ffffff !important; }
                b { color: #ffffff !important; }
                .comment-box { 
                    background: #1a1f2a; 
                    padding: 15px; 
                    border-radius: 12px; 
                    margin: 15px 0; 
                    border-left: 4px solid #18E3A4; 
                }
                .comment-box p { 
                    color: #ffffff !important; 
                    font-style: italic; 
                }
                .logo { 
                    text-align: center; 
                    font-size: 28px; 
                    font-weight: bold; 
                    margin-bottom: 20px; 
                }
                .logo span:first-child { color: #ffffff; }
                .logo span:last-child { color: #18E3A4; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="logo">
                    <span>Mid</span><span>gar</span>
                </div>
                <h2>📝 Nouveau commentaire</h2>
                <p>Bonjour <b>%s</b>,</p>
                <p><b>%s</b> a commenté votre %s :</p>
                <h3>%s</h3>
                <div class="comment-box">
                    <p>"%s"</p>
                </div>
                <p>Connectez-vous à Midgar pour lui répondre.</p>
            </div>
        </body>
        </html>
        """.formatted(nomProprietaire, auteur, type, titre, commentaire);
    }

    // =========================
    // RECOMMENDATION TEMPLATE (Midgar Style)
    // =========================
    private String buildRecommendationEmail(
            String nom,
            String element,
            String type,
            String typeCap
    ) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background: #0a0c10; color: #ffffff !important; padding: 40px; }
                .container { max-width: 600px; margin: auto; background: #11161c; padding: 30px; border-radius: 20px; border: 1px solid #18E3A4; }
                h2 { color: #18E3A4; }
                p { color: #ffffff !important; }
                b { color: #ffffff !important; }
                .card { background: #1a1f2a; padding: 20px; border-radius: 12px; text-align: center; margin: 15px 0; }
                .logo { text-align: center; font-size: 28px; font-weight: bold; margin-bottom: 20px; }
                .logo span:first-child { color: #ffffff; }
                .logo span:last-child { color: #18E3A4; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="logo">
                    <span>Mid</span><span>gar</span>
                </div>
                <h2>✨ Nouvelle recommandation</h2>
                <p>Bonjour <b>%s</b>,</p>
                <p>Une nouvelle %s qui pourrait vous plaire :</p>
                <div class="card">
                    <h3 style="color:#18E3A4; margin:0;">%s</h3>
                    <p style="margin-top:10px;">Type: %s</p>
                </div>
                <p>Connectez-vous à Midgar pour découvrir ce contenu !</p>
            </div>
        </body>
        </html>
        """.formatted(nom, typeCap, element, type);
    }

    // =========================
    // PLAIN TEXT TEMPLATE (Midgar Style)
    // =========================
    private String buildPlainTextEmail(String textBody) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background: #0a0c10; color: #ffffff !important; padding: 40px; }
                .container { max-width: 600px; margin: auto; background: #11161c; padding: 30px; border-radius: 20px; border: 1px solid #18E3A4; }
                .logo { text-align: center; font-size: 28px; font-weight: bold; margin-bottom: 20px; }
                .logo span:first-child { color: #ffffff; }
                .logo span:last-child { color: #18E3A4; }
                p { color: #ffffff !important; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="logo">
                    <span>Mid</span><span>gar</span>
                </div>
                <p>%s</p>
            </div>
        </body>
        </html>
        """.formatted(textBody.replace("\n", "<br>"));
    }

    // =========================
    // CORE EMAIL SENDER
    // =========================
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

            System.out.println("✅ Email envoyé à: " + to);

        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}