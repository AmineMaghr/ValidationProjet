package com.example.app.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Universe {

    private int id;
    private String name;
    private String genre;
    private String shortDescription;
    private String storyContext;
    private List<String> themes = new ArrayList<>();
    private byte[] bannerImage;
    private String bannerBase64;
    private String videoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Personnage> personnages = new ArrayList<>();
    private int creatorId;

    private static final Gson gson = new Gson();

    public Universe() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS & SETTERS =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getStoryContext() { return storyContext; }
    public void setStoryContext(String storyContext) { this.storyContext = storyContext; }

    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }

    /**
     * Convertit la liste de thèmes en JSON valide pour la base de données
     */
    public String getThemesAsString() {
        if (themes == null || themes.isEmpty()) {
            return "[]";
        }
        return gson.toJson(themes);
    }

    /**
     * Convertit un JSON de thèmes en liste Java
     * Exemple: "[\"Fantasy\",\"Aventure\"]" → ["Fantasy", "Aventure"]
     */
    public void setThemesFromString(String themesStr) {
        this.themes = new ArrayList<>();
        
        System.out.println("📥 setThemesFromString reçoit: " + themesStr);
        
        if (themesStr == null || themesStr.isEmpty()) {
            return;
        }
        
        try {
            // Essayer de parser comme JSON
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> parsed = gson.fromJson(themesStr, listType);
            if (parsed != null) {
                this.themes = parsed;
                System.out.println("✅ JSON parsé avec succès: " + this.themes);
                return;
            }
        } catch (Exception e) {
            System.err.println("Erreur parsing JSON: " + e.getMessage());
        }
        
        // Fallback: ancien format avec virgules (pour compatibilité)
        String clean = themesStr.replace("[", "").replace("]", "").replace("\"", "");
        for (String theme : clean.split(",")) {
            String trimmed = theme.trim();
            if (!trimmed.isEmpty()) {
                this.themes.add(trimmed);
            }
        }
        System.out.println("⚠️ Fallback utilisé: " + this.themes);
    }

    public byte[] getBannerImage() { return bannerImage; }
    public void setBannerImage(byte[] bannerImage) { this.bannerImage = bannerImage; }

    public String getBannerBase64() {
        if (bannerBase64 != null) return bannerBase64;
        if (bannerImage != null && bannerImage.length > 0) {
            return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(bannerImage);
        }
        return null;
    }
    public void setBannerBase64(String bannerBase64) { this.bannerBase64 = bannerBase64; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Personnage> getPersonnages() { return personnages; }
    public void setPersonnages(List<Personnage> personnages) { this.personnages = personnages; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    @Override
    public String toString() { return name; }
}