package com.example.app.entities;

import java.time.LocalDateTime;

public class Oeuvre {
    private int id;
    private String titre;
    private String description;
    private String theme;
    private String imageCoverUrl;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
    private int createurId; // ou référence à un User

    public Oeuvre() {
        this.dateCreation = LocalDateTime.now();
        this.dateMiseAJour = LocalDateTime.now();
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

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateMiseAJour() { return dateMiseAJour; }
    public void setDateMiseAJour(LocalDateTime dateMiseAJour) { this.dateMiseAJour = dateMiseAJour; }

    public int getCreateurId() { return createurId; }
    public void setCreateurId(int createurId) { this.createurId = createurId; }
}
