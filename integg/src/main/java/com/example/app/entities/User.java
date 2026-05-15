package com.example.app.entities;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private static final String SHARED_AVATAR_UPLOADS_DIR = "C:/midgar_shared/uploads/avatars";
    private static final String SYMFONY_URL = "http://127.0.0.1:8000";

    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty email = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private final SimpleStringProperty role = new SimpleStringProperty();
    private final SimpleStringProperty nom = new SimpleStringProperty();
    private final SimpleStringProperty prenom = new SimpleStringProperty();
    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty avatar = new SimpleStringProperty();
    private final SimpleStringProperty avatarLocalPath = new SimpleStringProperty();
    private final SimpleStringProperty avatarWebUrl = new SimpleStringProperty();
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
    private LocalDateTime updatedAt;

    // Liste d'erreurs de validation
    private java.util.List<String> validationErrors = new java.util.ArrayList<>();

    // Pour l'affichage dans les tableaux
    private final SimpleStringProperty createdAtDisplay = new SimpleStringProperty();
    private final SimpleStringProperty updatedAtDisplay = new SimpleStringProperty();

    private transient Image cachedAvatar;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateDisplayDates();
    }

    private void updateDisplayDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (createdAt != null) {
            createdAtDisplay.set(createdAt.format(formatter));
        }
    }

    // Validation complète
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
        } else {
            String normalizedEmail = getEmail().trim();
            if (!normalizedEmail.matches("^[^\\s@]+@([^\\s@]+\\.)+[^\\s@]+$")) {
                validationErrors.add("Format d'email invalide");
            }
        }

        if (getPassword() == null || getPassword().isEmpty()) {
            validationErrors.add("Le mot de passe est requis");
        } else if (getPassword().length() < 6) {
            validationErrors.add("Le mot de passe doit contenir au moins 6 caractères");
        }

        return validationErrors.isEmpty();
    }

    public java.util.List<String> getValidationErrors() {
        return validationErrors;
    }

    public String getValidationErrorsAsString() {
        return String.join("\n", validationErrors);
    }

    public String saveAvatar(File imageFile) {
        try {
            Files.createDirectories(Paths.get(SHARED_AVATAR_UPLOADS_DIR));

            String extension = getFileExtension(imageFile.getName());
            String fileName = "avatar_" + System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().substring(0, 8) + "." + extension;

            Path destination = Paths.get(SHARED_AVATAR_UPLOADS_DIR, fileName);
            Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            setAvatar(fileName);
            setAvatarLocalPath(destination.toString());
            setAvatarWebUrl("/uploads/avatars/" + fileName);
            refreshAvatar();

            return fileName;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de l'avatar : " + e.getMessage());
            return null;
        }
    }

    public Image getAvatarImage() {
        if (cachedAvatar != null) {
            return cachedAvatar;
        }

        String local = getAvatarLocalPath();
        if (local != null && !local.isEmpty()) {
            File localFile = new File(local);
            if (localFile.exists()) {
                try {
                    cachedAvatar = new Image(localFile.toURI().toString(), true);
                    if (!cachedAvatar.isError()) {
                        return cachedAvatar;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        String webUrl = getAvatarWebUrl();
        if (webUrl != null && !webUrl.isEmpty()) {
            try {
                String fullUrl = webUrl.startsWith("http") ? webUrl : SYMFONY_URL + webUrl;
                cachedAvatar = new Image(fullUrl, true);
                if (!cachedAvatar.isError()) {
                    return cachedAvatar;
                }
            } catch (Exception ignored) {
            }
        }

        String avatarReference = getAvatar();
        if (avatarReference != null && !avatarReference.isEmpty()) {
            File avatarFile = new File(avatarReference);
            if (!avatarFile.exists()) {
                avatarFile = new File(SHARED_AVATAR_UPLOADS_DIR, avatarReference);
            }
            if (avatarFile.exists()) {
                try {
                    cachedAvatar = new Image(avatarFile.toURI().toString(), true);
                    if (!cachedAvatar.isError()) {
                        return cachedAvatar;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        cachedAvatar = getDefaultAvatar();
        return cachedAvatar;
    }

    public void deleteAvatar() {
        String local = getAvatarLocalPath();
        if (local != null && !local.isEmpty()) {
            try {
                File localFile = new File(local);
                if (localFile.exists()) {
                    boolean deleted = localFile.delete();
                    System.out.println("Suppression avatar local : " + deleted);
                }
            } catch (Exception e) {
                System.err.println("Erreur suppression avatar local : " + e.getMessage());
            }
        }

        String fileName = getAvatar();
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path sharedFile = Paths.get(SHARED_AVATAR_UPLOADS_DIR, fileName);
                Files.deleteIfExists(sharedFile);
            } catch (Exception e) {
                System.err.println("Erreur suppression avatar partagé : " + e.getMessage());
            }
        }

        setAvatar(null);
        setAvatarLocalPath(null);
        setAvatarWebUrl(null);
        refreshAvatar();
    }

    public boolean hasAvatar() {
        return (getAvatar() != null && !getAvatar().isEmpty()) ||
                (getAvatarLocalPath() != null && !getAvatarLocalPath().isEmpty()) ||
                (getAvatarWebUrl() != null && !getAvatarWebUrl().isEmpty());
    }

    public void refreshAvatar() {
        cachedAvatar = null;
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "png";
    }

    private Image getDefaultAvatar() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default.png"));
        } catch (Exception e) {
            return null;
        }
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public boolean isAdmin() {
        return "admin".equals(role.get());
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getPrenom() {
        return prenom.get();
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getAvatar() {
        return avatar.get();
    }

    public void setAvatar(String avatar) {
        this.avatar.set(avatar);
    }

    public StringProperty avatarProperty() {
        return avatar;
    }

    public String getAvatarLocalPath() {
        return avatarLocalPath.get();
    }

    public void setAvatarLocalPath(String avatarLocalPath) {
        this.avatarLocalPath.set(avatarLocalPath);
    }

    public StringProperty avatarLocalPathProperty() {
        return avatarLocalPath;
    }

    public String getAvatarWebUrl() {
        return avatarWebUrl.get();
    }

    public void setAvatarWebUrl(String avatarWebUrl) {
        this.avatarWebUrl.set(avatarWebUrl);
    }

    public StringProperty avatarWebUrlProperty() {
        return avatarWebUrl;
    }

    public String getBio() {
        return bio.get();
    }

    public void setBio(String bio) {
        this.bio.set(bio);
    }

    public StringProperty bioProperty() {
        return bio;
    }

    public boolean isBlocked() {
        return isBlocked.get();
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked.set(blocked);
    }

    public BooleanProperty blockedProperty() {
        return isBlocked;
    }

    public boolean isVerified() {
        return isVerified.get();
    }

    public void setVerified(boolean verified) {
        this.isVerified.set(verified);
    }

    public BooleanProperty verifiedProperty() {
        return isVerified;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getResetToken() {
        return resetToken.get();
    }

    public void setResetToken(String resetToken) {
        this.resetToken.set(resetToken);
    }

    public StringProperty resetTokenProperty() {
        return resetToken;
    }

    public String getGoogleId() {
        return googleId.get();
    }

    public void setGoogleId(String googleId) {
        this.googleId.set(googleId);
    }

    public StringProperty googleIdProperty() {
        return googleId;
    }

    public String getAuthProvider() {
        return authProvider.get();
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider.set(authProvider);
    }

    public StringProperty authProviderProperty() {
        return authProvider;
    }

    public String getFaceDescriptor() {
        return faceDescriptor.get();
    }

    public void setFaceDescriptor(String faceDescriptor) {
        this.faceDescriptor.set(faceDescriptor);
    }

    public StringProperty faceDescriptorProperty() {
        return faceDescriptor;
    }

    public boolean isFaceEnabled() {
        return faceEnabled.get();
    }

    public void setFaceEnabled(boolean faceEnabled) {
        this.faceEnabled.set(faceEnabled);
    }

    public BooleanProperty faceEnabledProperty() {
        return faceEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        updateDisplayDates();
    }

    public StringProperty createdAtProperty() {
        return createdAtDisplay;
    }

    public String getCreatedAtDisplay() {
        return createdAtDisplay.get();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResetTokenExpiresAt() {
        return resetTokenExpiresAt;
    }

    public void setResetTokenExpiresAt(LocalDateTime resetTokenExpiresAt) {
        this.resetTokenExpiresAt = resetTokenExpiresAt;
    }

    public String getFullName() {
        return (prenom.get() + " " + nom.get()).trim();
    }
}
