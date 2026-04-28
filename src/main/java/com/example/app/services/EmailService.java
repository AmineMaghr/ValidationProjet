package com.example.app.services;

public class EmailService {

    // Configuration SMTP
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "zeinebsgh466@gmail.com";
    private static final String EMAIL_PASSWORD = "dkylbjupzplouige";

    /**
     * Envoie une notification au propriétaire quand un commentaire est ajouté
     */
    public void envoyerNotificationCommentaire(String emailProprietaire, String nomProprietaire,
            String contenuCommentaire, String titreElement,
            String typeElement, String auteurCommentaire) {

        System.out.println("=========================================");
        System.out.println("📧 [EMAIL] Tentative d'envoi de notification de commentaire");
        System.out.println("=========================================");
        System.out.println("📧 À: " + emailProprietaire);
        System.out.println("📧 Destinataire: " + nomProprietaire);
        System.out.println("📧 Type: " + typeElement);
        System.out.println("📧 Titre: " + titreElement);
        System.out.println("📧 Auteur: " + auteurCommentaire);
        System.out.println("📧 Commentaire: " + contenuCommentaire);
        System.out.println("=========================================");

        // Vérification basique
        if (emailProprietaire == null || emailProprietaire.isEmpty()) {
            System.err.println("❌ [EMAIL] IMPOSSIBLE D'ENVOYER: Email du propriétaire manquant !");
            return;
        }

        System.out.println("✅ [EMAIL] Notification de commentaire enregistrée (à envoyer à " + emailProprietaire + ")");
        
        // Pour activer le vrai SMTP, décommentez le code ci-dessous
        /*
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailProprietaire));
            message.setSubject("📝 Nouveau commentaire sur votre " + typeElement);

            String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #18E3A4;'>📝 Nouveau commentaire</h2>" +
                "<p>Bonjour <strong>" + nomProprietaire + "</strong>,</p>" +
                "<p><strong>" + auteurCommentaire + "</strong> a commenté sur votre " + typeElement +
                " <strong>" + titreElement + "</strong> :</p>" +
                "<div style='background-color: #f0f0f0; padding: 10px; border-left: 3px solid #18E3A4;'>" +
                "<p>" + contenuCommentaire + "</p>" +
                "</div>" +
                "<br>" +
                "<p>Connectez-vous à Midgar pour gérer les commentaires.</p>" +
                "<hr>" +
                "<small>Email envoyé automatiquement par Midgar</small>" +
                "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("✅ [EMAIL] Email envoyé avec succès à " + emailProprietaire);
        } catch (MessagingException e) {
            System.err.println("❌ [EMAIL] Erreur lors de l'envoi: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }

    /**
     * Envoie une notification quand un nouvel élément du même type favori est ajouté
     */
    public void envoyerNotificationNouvelElementSimilaire(String emailDestinataire, String nomDestinataire,
            String nomElement, String typeElement, String typeElementCapitalized) {
        
        System.out.println("=========================================");
        System.out.println("📧 [EMAIL] Tentative d'envoi de notification de recommandation");
        System.out.println("=========================================");
        System.out.println("📧 À: " + emailDestinataire);
        System.out.println("📧 Destinataire: " + nomDestinataire);
        System.out.println("📧 Nouveau " + typeElementCapitalized + ": " + nomElement);
        System.out.println("📧 Type: " + typeElement);
        System.out.println("=========================================");

        if (emailDestinataire == null || emailDestinataire.isEmpty()) {
            System.err.println("❌ [EMAIL] IMPOSSIBLE D'ENVOYER: Email du destinataire manquant !");
            return;
        }

        System.out.println("✅ [EMAIL] Notification de recommandation enregistrée (à envoyer à " + emailDestinataire + ")");
        
        // Pour activer le vrai SMTP, décommentez le code ci-dessous
        /*
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestinataire));
            message.setSubject("✨ Nouvelle " + typeElementCapitalized + " qui pourrait vous plaire !");

            String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #18E3A4;'>✨ Nouvelle recommandation</h2>" +
                "<p>Bonjour <strong>" + nomDestinataire + "</strong>,</p>" +
                "<p>Une nouvelle " + typeElementCapitalized + 
                " correspondant à vos goûts vient d'être ajoutée sur Midgar !</p>" +
                "<div style='background-color: #f0f0f0; padding: 15px; border-left: 4px solid #18E3A4; margin: 15px 0;'>" +
                "<h3 style='margin: 0 0 10px 0;'>" + nomElement + "</h3>" +
                "<p style='margin: 0;'><strong>Type :</strong> " + typeElement + "</p>" +
                "</div>" +
                "<p>Connectez-vous dès maintenant pour la découvrir et l'ajouter à vos favoris !</p>" +
                "<hr>" +
                "<small>Email envoyé automatiquement par Midgar</small>" +
                "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("✅ [EMAIL] Email de recommandation envoyé avec succès à " + emailDestinataire);
        } catch (MessagingException e) {
            System.err.println("❌ [EMAIL] Erreur lors de l'envoi: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }
}