package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Defi {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty titre = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleStringProperty theme = new SimpleStringProperty();
    private final SimpleStringProperty difficulte = new SimpleStringProperty();
    private final SimpleStringProperty imageCover = new SimpleStringProperty();
    private final SimpleStringProperty statut = new SimpleStringProperty();
    private final SimpleIntegerProperty createurId = new SimpleIntegerProperty();
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateLimite;
    private LocalDateTime updatedAt;
    private List<Participation> participations = new ArrayList<>();

    public Defi() {}

    // ===== ID =====
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // ===== TITRE =====
    public String getTitre() { return titre.get(); }
    public void setTitre(String titre) { this.titre.set(titre); }
    public StringProperty titreProperty() { return titre; }

    // ===== DESCRIPTION =====
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    // ===== THEME =====
    public String getTheme() { return theme.get(); }
    public void setTheme(String theme) { this.theme.set(theme); }
    public StringProperty themeProperty() { return theme; }

    // ===== DIFFICULTE =====
    public String getDifficulte() { return difficulte.get(); }
    public void setDifficulte(String difficulte) { this.difficulte.set(difficulte); }
    public StringProperty difficulteProperty() { return difficulte; }

    // ===== IMAGE COVER =====
    public String getImageCover() { return imageCover.get(); }
    public void setImageCover(String imageCover) { this.imageCover.set(imageCover); }
    public StringProperty imageCoverProperty() { return imageCover; }

    public String getImageCoverUrl() { return imageCover.get(); }
    public void setImageCoverUrl(String imageCoverUrl) { this.imageCover.set(imageCoverUrl); }

    // ===== STATUT =====
    public String getStatut() { return statut.get(); }
    public void setStatut(String statut) { this.statut.set(statut); }
    public StringProperty statutProperty() { return statut; }

    // ===== CREATEUR ID =====
    public int getCreateurId() { return createurId.get(); }
    public void setCreateurId(int createurId) { this.createurId.set(createurId); }
    public IntegerProperty createurIdProperty() { return createurId; }

    // ===== DATES (LocalDate) =====
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }

    // ===== UPDATED AT =====
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ===== PARTICIPATIONS =====
    public List<Participation> getParticipations() { return participations; }
    public void setParticipations(List<Participation> participations) { this.participations = participations; }

    // ===== MÉTHODES POUR STRING (pour compatibilité avec les services) =====
    public String getDateDebutAsString() {
        return dateDebut != null ? dateDebut.toString() : null;
    }

    public String getDateFinAsString() {
        return dateFin != null ? dateFin.toString() : null;
    }
    // Ajoute ces méthodes dans Defi.java
    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut != null ? LocalDate.parse(dateDebut) : null;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin != null ? LocalDate.parse(dateFin) : null;
    }

    public void setDateLimite(String dateLimite) {
        this.dateLimite = dateLimite != null ? LocalDate.parse(dateLimite) : null;
    }
}