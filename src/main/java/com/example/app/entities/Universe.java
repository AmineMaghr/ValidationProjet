package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Universe {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();
    private final SimpleStringProperty shortDescription = new SimpleStringProperty();
    private final SimpleStringProperty storyContext = new SimpleStringProperty();
    private List<String> themes = new ArrayList<>();
    private byte[] bannerImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Personnage> personnages = new ArrayList<>();
    private List<Oeuvre> oeuvres = new ArrayList<>();

    // Propriété pour bannerBase64 (pour l'affichage)
    private String bannerBase64;

    public Universe() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS ET SETTERS =====

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getGenre() { return genre.get(); }
    public void setGenre(String genre) { this.genre.set(genre); }
    public StringProperty genreProperty() { return genre; }

    public String getShortDescription() { return shortDescription.get(); }
    public void setShortDescription(String shortDescription) { this.shortDescription.set(shortDescription); }
    public StringProperty shortDescriptionProperty() { return shortDescription; }

    public String getStoryContext() { return storyContext.get(); }
    public void setStoryContext(String storyContext) { this.storyContext.set(storyContext); }
    public StringProperty storyContextProperty() { return storyContext; }

    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }

    public String getThemesAsString() {
        return String.join(", ", themes);
    }

    public void setThemesFromString(String themesStr) {
        this.themes = new ArrayList<>();
        if (themesStr != null && !themesStr.isEmpty()) {
            for (String theme : themesStr.split(",")) {
                this.themes.add(theme.trim());
            }
        }
    }

    public byte[] getBannerImage() { return bannerImage; }
    public void setBannerImage(byte[] bannerImage) { this.bannerImage = bannerImage; }

    // ===== MÉTHODES POUR BANNER BASE64 =====
    public String getBannerBase64() {
        if (bannerBase64 != null) return bannerBase64;
        if (bannerImage != null && bannerImage.length > 0) {
            return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(bannerImage);
        }
        return null;
    }

    public void setBannerBase64(String bannerBase64) {
        this.bannerBase64 = bannerBase64;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Personnage> getPersonnages() { return personnages; }
    public void setPersonnages(List<Personnage> personnages) { this.personnages = personnages; }

    public List<Oeuvre> getOeuvres() { return oeuvres; }
    public void setOeuvres(List<Oeuvre> oeuvres) { this.oeuvres = oeuvres; }

    @Override
    public String toString() {
        return getName();
    }
}