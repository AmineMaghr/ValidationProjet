package com.example.app.entities;
import java.time.LocalDateTime;

public class Commentaire {
    private int id;
    private String contenu;
    private LocalDateTime createdAt;
    private int userId;
    private Integer oeuvreId;     // nullable
    private Integer artefactId;   // nullable

    public Commentaire() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getOeuvreId() { return oeuvreId; }
    public void setOeuvreId(Integer oeuvreId) { this.oeuvreId = oeuvreId; }

    public Integer getArtefactId() { return artefactId; }
    public void setArtefactId(Integer artefactId) { this.artefactId = artefactId; }
}
