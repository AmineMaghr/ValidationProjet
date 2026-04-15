package com.example.app.entities;

import java.time.LocalDateTime;

public class Personnage {
    private int id;
    private String nom;
    private String univers;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Personnage() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getUnivers() { return univers; }
    public void setUnivers(String univers) { this.univers = univers; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
