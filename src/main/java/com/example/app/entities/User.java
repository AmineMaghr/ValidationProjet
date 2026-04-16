package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty email = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private final SimpleStringProperty role = new SimpleStringProperty();
    private final SimpleStringProperty nom = new SimpleStringProperty();
    private final SimpleStringProperty prenom = new SimpleStringProperty();
    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty avatar = new SimpleStringProperty();
    private final SimpleStringProperty bio = new SimpleStringProperty();
    private final SimpleBooleanProperty isBlocked = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isVerified = new SimpleBooleanProperty(true);
    private final SimpleStringProperty phoneNumber = new SimpleStringProperty();
    private final SimpleStringProperty resetToken = new SimpleStringProperty();
    private final SimpleStringProperty googleId = new SimpleStringProperty();
    private final SimpleStringProperty authProvider = new SimpleStringProperty("local");
    private final SimpleStringProperty faceDescriptor = new SimpleStringProperty();
    private final SimpleBooleanProperty faceEnabled = new SimpleBooleanProperty(false);
    private LocalDateTime createdAt;
    private LocalDateTime resetTokenExpiresAt;

    // Pour l'affichage dans les tableaux
    private final SimpleStringProperty createdAtDisplay = new SimpleStringProperty();
    private final SimpleStringProperty updatedAtDisplay = new SimpleStringProperty();

    public User() {
        this.createdAt = LocalDateTime.now();
        updateDisplayDates();
    }

    private void updateDisplayDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (createdAt != null) {
            createdAtDisplay.set(createdAt.format(formatter));
        }
    }

    // ===== ID =====
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // ===== EMAIL =====
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    // ===== PASSWORD =====
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    // ===== ROLE =====
    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public StringProperty roleProperty() { return role; }
    public boolean isAdmin() { return "admin".equals(role.get()); }

    // ===== NOM =====
    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    // ===== PRENOM =====
    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public StringProperty prenomProperty() { return prenom; }

    // ===== USERNAME =====
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    // ===== AVATAR =====
    public String getAvatar() { return avatar.get(); }
    public void setAvatar(String avatar) { this.avatar.set(avatar); }
    public StringProperty avatarProperty() { return avatar; }

    // ===== BIO =====
    public String getBio() { return bio.get(); }
    public void setBio(String bio) { this.bio.set(bio); }
    public StringProperty bioProperty() { return bio; }

    // ===== BLOCKED =====
    public boolean isBlocked() { return isBlocked.get(); }
    public void setBlocked(boolean blocked) { this.isBlocked.set(blocked); }
    public BooleanProperty blockedProperty() { return isBlocked; }

    // ===== VERIFIED =====
    public boolean isVerified() { return isVerified.get(); }
    public void setVerified(boolean verified) { this.isVerified.set(verified); }
    public BooleanProperty verifiedProperty() { return isVerified; }

    // ===== PHONE NUMBER =====
    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    // ===== RESET TOKEN =====
    public String getResetToken() { return resetToken.get(); }
    public void setResetToken(String resetToken) { this.resetToken.set(resetToken); }
    public StringProperty resetTokenProperty() { return resetToken; }

    // ===== GOOGLE ID =====
    public String getGoogleId() { return googleId.get(); }
    public void setGoogleId(String googleId) { this.googleId.set(googleId); }
    public StringProperty googleIdProperty() { return googleId; }

    // ===== AUTH PROVIDER =====
    public String getAuthProvider() { return authProvider.get(); }
    public void setAuthProvider(String authProvider) { this.authProvider.set(authProvider); }
    public StringProperty authProviderProperty() { return authProvider; }

    // ===== FACE DESCRIPTOR =====
    public String getFaceDescriptor() { return faceDescriptor.get(); }
    public void setFaceDescriptor(String faceDescriptor) { this.faceDescriptor.set(faceDescriptor); }
    public StringProperty faceDescriptorProperty() { return faceDescriptor; }

    // ===== FACE ENABLED =====
    public boolean isFaceEnabled() { return faceEnabled.get(); }
    public void setFaceEnabled(boolean faceEnabled) { this.faceEnabled.set(faceEnabled); }
    public BooleanProperty faceEnabledProperty() { return faceEnabled; }

    // ===== CREATED AT (LocalDateTime) =====
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        updateDisplayDates();
    }

    // ===== CREATED AT DISPLAY (pour les tableaux) =====
    public StringProperty createdAtProperty() {
        return createdAtDisplay;
    }

    public String getCreatedAtDisplay() {
        return createdAtDisplay.get();
    }

    // ===== RESET TOKEN EXPIRES AT =====
    public LocalDateTime getResetTokenExpiresAt() { return resetTokenExpiresAt; }
    public void setResetTokenExpiresAt(LocalDateTime resetTokenExpiresAt) { this.resetTokenExpiresAt = resetTokenExpiresAt; }

    // ===== FULL NAME =====
    public String getFullName() { return (prenom.get() + " " + nom.get()).trim(); }
}