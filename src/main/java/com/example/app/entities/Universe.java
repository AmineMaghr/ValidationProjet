package com.example.app.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Universe {
    private int id;
    private String name;
    private String shortDescription;
    private String description;
    private List<String> themes;
    private String imageUrl;

    // Créateur de cet univers
    private User creator;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Universe() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.themes = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes != null ? themes : new ArrayList<>(); }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Méthodes utilitaires
    public String getThemesAsString() {
        return themes != null ? String.join(", ", themes) : "";
    }

    public void setThemesFromString(String themesString) {
        this.themes = new ArrayList<>();
        if (themesString != null && !themesString.trim().isEmpty()) {
            String[] themeArray = themesString.split(",");
            for (String theme : themeArray) {
                this.themes.add(theme.trim());
            }
        }
    }

    @Override
    public String toString() {
        return "Universe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", themes=" + themes +
                '}';
    }
}
