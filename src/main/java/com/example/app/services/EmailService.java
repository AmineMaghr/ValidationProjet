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
    // WELCOME EMAIL
    // =========================
    public void sendWelcomeEmail(String to, String username) {
        String subject = "Bienvenue sur Midgar !";
        String content =
                "<h1>Bienvenue " + username + " !</h1>" +
                "<p>Votre compte a été créé avec succès.</p>";

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
    // COMMENT NOTIFICATION
    // =========================
    public void envoyerNotificationCommentaire(
            String emailProprietaire,
            String nomProprietaire,
            String contenuCommentaire,
            String titreElement,
            String typeElement,
            String auteurCommentaire
    ) {

        String subject = "📝 Nouveau commentaire sur votre " + typeElement;

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
    // RESET TEMPLATE
    // =========================
    private String buildResetEmail(String username, String resetLink, String token) {
        return """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial;background:#0a0c10;color:#fff;padding:40px;">
            <div style="max-width:600px;margin:auto;background:#11161c;padding:30px;border-radius:20px;border:1px solid #18E3A4;">
                
                <h2 style="color:#18E3A4;text-align:center;">Réinitialisation du mot de passe</h2>
                
                <p>Bonjour <b>%s</b></p>
                
                <p>Cliquez sur le bouton ci-dessous :</p>
                
                <div style="text-align:center;margin:20px;">
                    <a href="%s"
                       style="background:#18E3A4;color:black;padding:12px 25px;
                       border-radius:25px;text-decoration:none;font-weight:bold;">
                       Réinitialiser
                    </a>
                </div>

                <p style="font-size:12px;color:#aaa;">Token: %s</p>

            </div>
        </body>
        </html>
        """.formatted(username, resetLink, token);
    }

    // =========================
    // COMMENT TEMPLATE
    // =========================
    private String buildCommentEmail(
            String nomProprietaire,
            String auteur,
            String titre,
            String type,
            String commentaire
    ) {
        return "<html><body style='font-family:Arial;background:#0a0c10;color:white;padding:40px;'>" +
                "<div style='max-width:600px;margin:auto;background:#11161c;padding:30px;border-radius:20px;border:1px solid #18E3A4;'>" +
                "<h2 style='color:#18E3A4;'>📝 Nouveau commentaire</h2>" +
                "<p>Bonjour <b>" + nomProprietaire + "</b></p>" +
                "<p><b>" + auteur + "</b> a commenté votre " + type + "</p>" +
                "<h3 style='color:#18E3A4;'>" + titre + "</h3>" +
                "<p>" + commentaire + "</p>" +
                "</div></body></html>";
    }

    // =========================
    // RECOMMENDATION TEMPLATE
    // =========================
    private String buildRecommendationEmail(
            String nom,
            String element,
            String type,
            String typeCap
    ) {
        return "<html><body style='font-family:Arial;background:#0a0c10;color:white;padding:40px;'>" +
                "<div style='max-width:600px;margin:auto;background:#11161c;padding:30px;border-radius:20px;border:1px solid #18E3A4;'>" +
                "<h2 style='color:#18E3A4;'>✨ Recommandation</h2>" +
                "<p>Bonjour <b>" + nom + "</b></p>" +
                "<p>Nouvelle " + typeCap + " qui pourrait vous plaire :</p>" +
                "<h3 style='color:#18E3A4;'>" + element + "</h3>" +
                "<p>Type: " + type + "</p>" +
                "</div></body></html>";
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