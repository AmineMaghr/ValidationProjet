package com.example.app.entities;

import java.time.LocalDateTime;

public class AdvancedPreference {
    private int id;
    private String freeDescription;
    private String favoriteGenre;
    private int affinityLevel;
    private String favoriteThemes;
    private String customTags;
    private int userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdvancedPreference() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.affinityLevel = 1; // valeur par défaut
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFreeDescription() { return freeDescription; }
    public void setFreeDescription(String freeDescription) { this.freeDescription = freeDescription; }

    public String getFavoriteGenre() { return favoriteGenre; }
    public void setFavoriteGenre(String favoriteGenre) { this.favoriteGenre = favoriteGenre; }

    public int getAffinityLevel() { return affinityLevel; }
    public void setAffinityLevel(int affinityLevel) { this.affinityLevel = affinityLevel; }

    public String getFavoriteThemes() { return favoriteThemes; }
    public void setFavoriteThemes(String favoriteThemes) { this.favoriteThemes = favoriteThemes; }

    public String getCustomTags() { return customTags; }
    public void setCustomTags(String customTags) { this.customTags = customTags; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
