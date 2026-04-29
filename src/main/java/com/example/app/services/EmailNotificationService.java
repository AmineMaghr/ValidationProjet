package com.example.app.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Email notification service
 * Send alerts for character creation, universe updates, achievements
 */
public class EmailNotificationService {
    private static String smtpHost = "smtp.gmail.com";
    private static String smtpPort = "587";
    private static String senderEmail = "";
    private static String senderPassword = "";
    private static boolean isConfigured = false;

    /**
     * Configure email settings (call once at app startup)
     */
    public static void configure(String email, String appPassword) {
        senderEmail = email;
        senderPassword = appPassword;
        isConfigured = true;
    }

    /**
     * Send character creation alert
     */
    public static void notifyCharacterCreated(String recipientEmail, String characterName) {
        if (!isConfigured) {
            System.err.println("Email service not configured. Call configure() first.");
            return;
        }

        String subject = "New Character Created: " + characterName;
        String body = "A new character '" + characterName + "' has been created in your universe!\n\n" +
                     "Check your app for more details.";

        sendEmail(recipientEmail, subject, body);
    }

    /**
     * Send universe update alert
     */
    public static void notifyUniverseUpdate(String recipientEmail, String universeName, String updateMessage) {
        if (!isConfigured) {
            System.err.println("Email service not configured. Call configure() first.");
            return;
        }

        String subject = "Universe Updated: " + universeName;
        String body = "Your universe has been updated!\n\n" + updateMessage + "\n\nCheck your app for details.";

        sendEmail(recipientEmail, subject, body);
    }

    /**
     * Send achievement notification
     */
    public static void notifyAchievement(String recipientEmail, String achievement) {
        if (!isConfigured) {
            System.err.println("Email service not configured. Call configure() first.");
            return;
        }

        String subject = "Achievement Unlocked! 🎉";
        String body = "Congratulations! You've unlocked: " + achievement;

        sendEmail(recipientEmail, subject, body);
    }

    /**
     * Generic email sending
     */
    private static void sendEmail(String toEmail, String subject, String body) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", smtpPort);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.connectiontimeout", "5000");
                props.put("mail.smtp.timeout", "5000");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                System.out.println("Email sent successfully to: " + toEmail);
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }).start();
    }
}
