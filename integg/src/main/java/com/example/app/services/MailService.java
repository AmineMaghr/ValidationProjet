package com.example.app.services;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MailService {

    public void sendMail(String recipient, String subject, String body) throws Exception {
        Properties smtpConfig = loadSmtpConfig();

        String host = requireConfig(smtpConfig, "SMTP_HOST");
        String username = requireConfig(smtpConfig, "SMTP_USERNAME");
        String password = requirePassword(smtpConfig);
        String from = getConfigOrDefault(smtpConfig, "SMTP_FROM", username);
        
        System.out.println("[MAIL DEBUG] SMTP_HOST=" + host);
        System.out.println("[MAIL DEBUG] SMTP_USERNAME=" + username + " (len=" + username.length() + ")");
        System.out.println("[MAIL DEBUG] SMTP_PASSWORD length=" + password.length() + " (first 3 chars: " + password.substring(0, Math.min(3, password.length())) + ")");
        System.out.println("[MAIL DEBUG] SMTP_FROM=" + from);
        int port = Integer.parseInt(getConfigOrDefault(smtpConfig, "SMTP_PORT", "587"));
        boolean startTls = Boolean.parseBoolean(getConfigOrDefault(smtpConfig, "SMTP_STARTTLS", "true"));
        boolean ssl = Boolean.parseBoolean(getConfigOrDefault(smtpConfig, "SMTP_SSL", "false"));

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.auth.mechanisms", "LOGIN");
        properties.put("mail.smtp.auth.login.disable", "false");
        properties.put("mail.smtp.auth.plain.disable", "true");
        properties.put("mail.smtp.auth.xoauth2.disable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        properties.put("mail.smtp.ssl.enable", String.valueOf(ssl));
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.ssl.checkserveridentity", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");
        properties.put("mail.mime.charset", StandardCharsets.UTF_8.name());

        Session session = Session.getInstance(properties);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
        message.setSubject(subject == null ? "" : subject, StandardCharsets.UTF_8.name());
        message.setText(body == null ? "" : body, StandardCharsets.UTF_8.name());

        Transport transport = session.getTransport("smtp");
        try {
            transport.connect(host, port, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            try {
                transport.close();
            } catch (Exception ignored) {
                // ignore close errors
            }
        }
    }

    private Properties loadSmtpConfig() {
        Properties config = new Properties();

        // First, always try to load from smtp.properties (highest priority local config)
        String[] candidates = new String[] {
                System.getProperty("user.dir") + System.getProperty("file.separator") + "smtp.properties",
                System.getProperty("user.home") + System.getProperty("file.separator") + "smtp.properties"
        };

        for (String path : candidates) {
            try (InputStream input = new FileInputStream(path)) {
                Properties fileConfig = new Properties();
                fileConfig.load(input);
                config.putAll(fileConfig);
                if (!config.isEmpty()) {
                    break;
                }
            } catch (Exception ignored) {
                // try next location
            }
        }

        // Then overlay only host/port env vars (these override file config)
        copyEnv(config, "SMTP_HOST");
        copyEnv(config, "SMTP_PORT");
        copyEnv(config, "SMTP_STARTTLS");
        copyEnv(config, "SMTP_SSL");
        // Do NOT copy SMTP_USERNAME, SMTP_FROM, SMTP_PASSWORD from env
        // These must come from smtp.properties to avoid old setx vars overriding

        return config;
    }

    private void copyEnv(Properties target, String key) {
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            target.setProperty(key, value.trim());
        }
    }

    private String requireConfig(Properties config, String key) {
        String value = config.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing SMTP configuration: " + key);
        }
        return value.trim();
    }

    private String requirePassword(Properties config) {
        String password = config.getProperty("SMTP_PASSWORD");
        if (password != null && !password.isBlank()) {
            return normalizePassword(password);
        }

        String securePassword = tryLoadPasswordFromSecureFile();
        if (securePassword != null && !securePassword.isBlank()) {
            return normalizePassword(securePassword);
        }

        throw new IllegalStateException("Missing SMTP configuration: SMTP_PASSWORD");
    }

    private String getConfigOrDefault(Properties config, String key, String defaultValue) {
        String value = config.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private String normalizePassword(String value) {
        if (value == null) return null;
        // Strip all whitespace including carriage returns from Windows line endings
        return value.replaceAll("[\\s\\r\\n]+", "").trim();
    }

    private String tryLoadPasswordFromSecureFile() {
        String os = System.getProperty("os.name");
        if (os == null || !os.toLowerCase().contains("win")) return null;

        String[] candidatePaths = new String[] {
                System.getProperty("user.dir") + System.getProperty("file.separator") + "smtp_password.sec",
                System.getProperty("user.home") + System.getProperty("file.separator") + "smtp_password.sec"
        };

        for (String path : candidatePaths) {
            try {
                java.io.File f = new java.io.File(path);
                if (!f.exists()) continue;

                String psCommand = String.format("$enc=Get-Content -Raw -Path '%s'; $s=ConvertTo-SecureString $enc; $ptr=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($s); $p=[Runtime.InteropServices.Marshal]::PtrToStringUni($ptr); [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr); Write-Output $p", path.replace("'", "''"));
                ProcessBuilder pb = new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", psCommand);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                InputStream inputStream = p.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";
                p.waitFor();
                if (output != null && !output.isBlank()) {
                    return output.trim();
                }
            } catch (Exception ignored) {
                // ignore and try next
            }
        }
        return null;
    }
}
