package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Participation {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleStringProperty statut = new SimpleStringProperty();
    private final SimpleIntegerProperty userId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty artworkId = new SimpleIntegerProperty();
    private final SimpleStringProperty imageFileName = new SimpleStringProperty();
    private LocalDateTime dateSoumission;  // ← Changé de LocalDate à LocalDateTime
    private LocalDateTime updatedAt;
    private Defi defi;
    private User user;  // ← Ajout de l'utilisateur

    public Participation() {
        this.dateSoumission = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public String getStatut() { return statut.get(); }
    public void setStatut(String statut) { this.statut.set(statut); }
    public StringProperty statutProperty() { return statut; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int userId) { this.userId.set(userId); }
    public IntegerProperty userIdProperty() { return userId; }

    public int getArtworkId() { return artworkId.get(); }
    public void setArtworkId(int artworkId) { this.artworkId.set(artworkId); }
    public IntegerProperty artworkIdProperty() { return artworkId; }

    public String getImageFileName() { return imageFileName.get(); }
    public void setImageFileName(String imageFileName) { this.imageFileName.set(imageFileName); }
    public StringProperty imageFileNameProperty() { return imageFileName; }

    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Defi getDefi() { return defi; }
    public void setDefi(Defi defi) { this.defi = defi; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId.set(user.getId());
        }
    }
}