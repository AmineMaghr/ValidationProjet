package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;        // AJOUTER
import java.util.ArrayList;   // AJOUTER

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
    private final SimpleStringProperty resetCode = new SimpleStringProperty();  // AJOUTÉ
    private final SimpleStringProperty googleId = new SimpleStringProperty();
    private final SimpleStringProperty authProvider = new SimpleStringProperty("local");
    private final SimpleStringProperty faceDescriptor = new SimpleStringProperty();
    private final SimpleBooleanProperty faceEnabled = new SimpleBooleanProperty(false);
    private LocalDateTime createdAt;
    private LocalDateTime resetTokenExpiresAt;
    private LocalDateTime resetCodeExpiresAt;  // AJOUTÉ
    private LocalDateTime updatedAt;

    // Liste d'erreurs de validation
    private java.util.List<String> validationErrors = new java.util.ArrayList<>();

    // Pour l'affichage dans les tableaux
    private final SimpleStringProperty createdAtDisplay = new SimpleStringProperty();
    private final SimpleStringProperty updatedAtDisplay = new SimpleStringProperty();

    // ==================== CONSTRUCTEURS ====================
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateDisplayDates();
        this.authProvider.set("local");
    }

    // ==================== MÉTHODES UTILITAIRES ====================
    
    private void updateDisplayDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (createdAt != null) {
            createdAtDisplay.set(createdAt.format(formatter));
        }
    }

    public boolean isValid() {
        validationErrors.clear();

        if (getPrenom() == null || getPrenom().trim().isEmpty()) {
            validationErrors.add("Le prénom est requis");
        } else if (getPrenom().length() < 2) {
            validationErrors.add("Le prénom doit contenir au moins 2 caractères");
        } else if (!getPrenom().matches("^[a-zA-ZÀ-ÿ\\s-]+$")) {
            validationErrors.add("Le prénom ne doit contenir que des lettres");
        }

        if (getNom() == null || getNom().trim().isEmpty()) {
            validationErrors.add("Le nom est requis");
        } else if (getNom().length() < 2) {
            validationErrors.add("Le nom doit contenir au moins 2 caractères");
        }

        if (getUsername() == null || getUsername().trim().isEmpty()) {
            validationErrors.add("Le nom d'utilisateur est requis");
        } else if (getUsername().length() < 3) {
            validationErrors.add("Le nom d'utilisateur doit contenir au moins 3 caractères");
        } else if (getUsername().length() > 20) {
            validationErrors.add("Le nom d'utilisateur ne doit pas dépasser 20 caractères");
        } else if (!getUsername().matches("^[a-zA-Z0-9]+$")) {
            validationErrors.add("Le nom d'utilisateur ne doit contenir que des lettres et chiffres");
        }

        if (getEmail() == null || getEmail().trim().isEmpty()) {
            validationErrors.add("L'email est requis");
        } else if (!getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            validationErrors.add("Format d'email invalide");
        }

        if (getAuthProvider().equals("local") && (getPassword() == null || getPassword().isEmpty())) {
            validationErrors.add("Le mot de passe est requis");
        }

        return validationErrors.isEmpty();
    }

    public java.util.List<String> getValidationErrors() {
        return validationErrors;
    }

    public String getValidationErrorsAsString() {
        return String.join("\n", validationErrors);
    }

    public String getFullName() { 
        return (getPrenom() + " " + getNom()).trim();    
    }

    // ==================== GETTERS & SETTERS ====================
    
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public StringProperty roleProperty() { return role; }
    public boolean isAdmin() { return "admin".equals(role.get()); }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public StringProperty prenomProperty() { return prenom; }

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    public String getAvatar() { return avatar.get(); }
    public void setAvatar(String avatar) { this.avatar.set(avatar); }
    public StringProperty avatarProperty() { return avatar; }

    public String getBio() { return bio.get(); }
    public void setBio(String bio) { this.bio.set(bio); }
    public StringProperty bioProperty() { return bio; }

    public boolean isBlocked() { return isBlocked.get(); }
    public void setBlocked(boolean blocked) { this.isBlocked.set(blocked); }
    public BooleanProperty blockedProperty() { return isBlocked; }

    public boolean isVerified() { return isVerified.get(); }
    public void setVerified(boolean verified) { this.isVerified.set(verified); }
    public BooleanProperty verifiedProperty() { return isVerified; }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public String getResetToken() { return resetToken.get(); }
    public void setResetToken(String resetToken) { this.resetToken.set(resetToken); }
    public StringProperty resetTokenProperty() { return resetToken; }
    
    public String getResetCode() { return resetCode.get(); }
    public void setResetCode(String resetCode) { this.resetCode.set(resetCode); }
    public StringProperty resetCodeProperty() { return resetCode; }

    public String getGoogleId() { return googleId.get(); }
    public void setGoogleId(String googleId) { this.googleId.set(googleId); }
    public StringProperty googleIdProperty() { return googleId; }

    public String getAuthProvider() { return authProvider.get(); }
    public void setAuthProvider(String authProvider) { this.authProvider.set(authProvider); }
    public StringProperty authProviderProperty() { return authProvider; }

    public String getFaceDescriptor() { return faceDescriptor.get(); }
    public void setFaceDescriptor(String faceDescriptor) { this.faceDescriptor.set(faceDescriptor); }
    public StringProperty faceDescriptorProperty() { return faceDescriptor; }

    public boolean isFaceEnabled() { return faceEnabled.get(); }
    public void setFaceEnabled(boolean faceEnabled) { this.faceEnabled.set(faceEnabled); }
    public BooleanProperty faceEnabledProperty() { return faceEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        updateDisplayDates();
    }

    public StringProperty createdAtProperty() { return createdAtDisplay; }
    public String getCreatedAtDisplay() { return createdAtDisplay.get(); }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResetTokenExpiresAt() { return resetTokenExpiresAt; }
    public void setResetTokenExpiresAt(LocalDateTime resetTokenExpiresAt) { this.resetTokenExpiresAt = resetTokenExpiresAt; }
    
    public LocalDateTime getResetCodeExpiresAt() { return resetCodeExpiresAt; }
    public void setResetCodeExpiresAt(LocalDateTime resetCodeExpiresAt) { this.resetCodeExpiresAt = resetCodeExpiresAt; }

     }