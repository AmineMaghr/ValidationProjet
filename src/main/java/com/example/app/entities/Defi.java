package com.example.app.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Defi {
    private int id;
    private String titre;
    private String description;
    private String theme;
    private String imageCoverUrl;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateLimite; // nullable
    private String statut; // "OUVERT", "FERME", etc.
    private int createurId;
    private LocalDateTime updatedAt;
    private List<Participation> participations;

    public Defi() {
        this.participations = new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
        this.statut = "OUVERT";
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getImageCoverUrl() { return imageCoverUrl; }
    public void setImageCoverUrl(String imageCoverUrl) { this.imageCoverUrl = imageCoverUrl; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getCreateurId() { return createurId; }
    public void setCreateurId(int createurId) { this.createurId = createurId; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Participation> getParticipations() { return participations; }
    public void addParticipation(Participation participation) {
        if (!participations.contains(participation)) {
            participations.add(participation);
        }
    }
    public void removeParticipation(Participation participation) {
        participations.remove(participation);
    }
}
