package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class AdvancedPreference {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty freeDescription = new SimpleStringProperty();
    private final SimpleStringProperty favoriteGenre = new SimpleStringProperty();
    private final SimpleIntegerProperty affinityLevel = new SimpleIntegerProperty();
    private final SimpleStringProperty favoriteThemes = new SimpleStringProperty();
    private final SimpleStringProperty customTags = new SimpleStringProperty();
    private final SimpleStringProperty userId = new SimpleStringProperty();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdvancedPreference() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getFreeDescription() { return freeDescription.get(); }
    public void setFreeDescription(String freeDescription) { this.freeDescription.set(freeDescription); }
    public StringProperty freeDescriptionProperty() { return freeDescription; }

    public String getFavoriteGenre() { return favoriteGenre.get(); }
    public void setFavoriteGenre(String favoriteGenre) { this.favoriteGenre.set(favoriteGenre); }
    public StringProperty favoriteGenreProperty() { return favoriteGenre; }

    public int getAffinityLevel() { return affinityLevel.get(); }
    public void setAffinityLevel(int affinityLevel) { this.affinityLevel.set(affinityLevel); }
    public IntegerProperty affinityLevelProperty() { return affinityLevel; }

    public String getFavoriteThemes() { return favoriteThemes.get(); }
    public void setFavoriteThemes(String favoriteThemes) { this.favoriteThemes.set(favoriteThemes); }
    public StringProperty favoriteThemesProperty() { return favoriteThemes; }

    public String getCustomTags() { return customTags.get(); }
    public void setCustomTags(String customTags) { this.customTags.set(customTags); }
    public StringProperty customTagsProperty() { return customTags; }

    public String getUserId() { return userId.get(); }
    public void setUserId(String userId) { this.userId.set(userId); }
    public StringProperty userIdProperty() { return userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}