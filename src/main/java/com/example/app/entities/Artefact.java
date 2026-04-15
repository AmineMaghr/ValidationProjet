package com.example.app.entities;

import java.time.LocalDateTime;

public class Artefact {
    private int id;
    private String name;
    private String type;
    private String universe;
    private String origins;
    private String powers;
    private String rarity;
    private String imageUrl;
    private int createdById; // si tu veux stocker juste l'id de l'utilisateur
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Artefact() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUniverse() { return universe; }
    public void setUniverse(String universe) { this.universe = universe; }

    public String getOrigins() { return origins; }
    public void setOrigins(String origins) { this.origins = origins; }

    public String getPowers() { return powers; }
    public void setPowers(String powers) { this.powers = powers; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getCreatedById() { return createdById; }
    public void setCreatedById(int createdById) { this.createdById = createdById; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
