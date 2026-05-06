package com.example.app.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    // ── Configuration (loaded from config.properties) ────────────────────
    private String FROM_EMAIL;
    private String APP_PASSWORD;

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int    SMTP_PORT = 587;

    // ── SMTP session (created once, reused) ──────────────────────────────
    private Session session;

    public EmailService() {
        System.out.println("EmailService initialized");
        Properties config = new Properties();
        try (java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                config.load(input);
                FROM_EMAIL   = config.getProperty("mail.username");
                APP_PASSWORD = config.getProperty("mail.password");
            } else {
                LOG.warning("config.properties not found.");
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error loading config.properties", ex);
        }

        if (FROM_EMAIL == null || FROM_EMAIL.isBlank()) {
            LOG.severe("mail.username is missing or empty — emails will not send.");
        }
        if (APP_PASSWORD == null || APP_PASSWORD.isBlank()) {
            LOG.severe("mail.password is missing or empty — emails will not send.");
        }

        // Capture into finals so the anonymous Authenticator closes over
        // the already-loaded values (not over 'this', which causes a null bug).
        final String user = FROM_EMAIL;
        final String pass = APP_PASSWORD;

        Properties props = new Properties();
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    /**
     * Sends a plain-text email.
     * Never throws — all failures are logged and swallowed so the caller is never crashed.
     */
    public void sendText(String toEmail, String subject, String textBody) {
        System.out.println("[DEBUG] 5. sendText() called for recipient: " + toEmail + " | Subject: " + subject);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(textBody);

            Transport.send(message);

            System.out.println("[DEBUG] 6. SUCCESS: Email successfully sent to -> " + toEmail);
            LOG.info(String.format("Email sent to %s | subject: %s", toEmail, subject));

        } catch (MessagingException e) {
            System.err.println("[DEBUG] 7. FAILURE: Email failed to send to -> " + toEmail);
            e.printStackTrace();
            LOG.log(Level.SEVERE,
                    String.format("Failed to send email to %s | subject: %s", toEmail, subject), e);
        }
    }

    /**
     * Sends an HTML-formatted email.
     * Never throws — all failures are logged and swallowed so the caller is never crashed.
     */
    public void sendHtml(String toEmail, String subject, String htmlBody) {
        System.out.println("[DEBUG] 5. sendHtml() called for recipient: " + toEmail + " | Subject: " + subject);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("[DEBUG] 6. SUCCESS: HTML Email successfully sent to -> " + toEmail);
            LOG.info(String.format("HTML Email sent to %s | subject: %s", toEmail, subject));

        } catch (MessagingException e) {
            System.err.println("[DEBUG] 7. FAILURE: HTML Email failed to send to -> " + toEmail);
            e.printStackTrace();
            LOG.log(Level.SEVERE,
                    String.format("Failed to send HTML email to %s | subject: %s", toEmail, subject), e);
        }
    }

    /**
     * Validates an email address using Jakarta Mail's InternetAddress parser.
     * Mirrors PHP's filter_var($email, FILTER_VALIDATE_EMAIL).
     */
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
}