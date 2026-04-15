package com.example.app.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Favoris {
    private int id;
    private int userId;
    private Integer oeuvreId;     // nullable
    private Integer artefactId;   // nullable
    private LocalDateTime createdAt;

    public Favoris() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getOeuvreId() { return oeuvreId; }
    public void setOeuvreId(Integer oeuvreId) { this.oeuvreId = oeuvreId; }

    public Integer getArtefactId() { return artefactId; }
    public void setArtefactId(Integer artefactId) { this.artefactId = artefactId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
