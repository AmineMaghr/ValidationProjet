package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Artefact {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    private final SimpleStringProperty universe = new SimpleStringProperty();
    private final SimpleStringProperty origins = new SimpleStringProperty();
    private final SimpleStringProperty powers = new SimpleStringProperty();
    private final SimpleStringProperty rarity = new SimpleStringProperty();
    private final SimpleStringProperty imageUrl = new SimpleStringProperty();
    private User createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Liste d'erreurs de validation
    private List<String> validationErrors = new ArrayList<>();

    public Artefact() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Validation complète
    public boolean isValid() {
        validationErrors.clear();

        // Validation du nom (requis)
        if (getName() == null || getName().trim().isEmpty()) {
            validationErrors.add("Le nom est requis");
        }

        // Validation du type (requis)
        if (getType() == null || getType().trim().isEmpty()) {
            validationErrors.add("Le type est requis");
        }

        // Validation de l'univers (requis)
        if (getUniverse() == null || getUniverse().trim().isEmpty()) {
            validationErrors.add("L'univers est requis");
        }

        // Validation des origines (requises)
        if (getOrigins() == null || getOrigins().trim().isEmpty()) {
            validationErrors.add("Les origines sont requises");
        }

        // Validation des pouvoirs
        if (getPowers() == null || getPowers().trim().isEmpty()) {
            validationErrors.add("Les pouvoirs sont requis");
        } else if (getPowers().length() < 10) {
            validationErrors.add("Les pouvoirs doivent contenir au moins 10 caractères");
        } else if (!getPowers().matches("^[a-zA-Z].*$")) {
            validationErrors.add("Les pouvoirs doivent commencer par une lettre");
        }

        // Validation de la rareté (requise)
        if (getRarity() == null || getRarity().trim().isEmpty()) {
            validationErrors.add("La rareté est requise");
        }

        // Validation de l'image (requise)
        if (getImageUrl() == null || getImageUrl().trim().isEmpty()) {
            validationErrors.add("L'image est requise");
        }

        return validationErrors.isEmpty();
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public String getValidationErrorsAsString() {
        return String.join("\n", validationErrors);
    }

    // Getters et Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }
    public StringProperty typeProperty() { return type; }

    public String getUniverse() { return universe.get(); }
    public void setUniverse(String universe) { this.universe.set(universe); }
    public StringProperty universeProperty() { return universe; }

    public String getOrigins() { return origins.get(); }
    public void setOrigins(String origins) { this.origins.set(origins); }
    public StringProperty originsProperty() { return origins; }

    public String getPowers() { return powers.get(); }
    public void setPowers(String powers) { this.powers.set(powers); }
    public StringProperty powersProperty() { return powers; }

    public String getRarity() { return rarity.get(); }
    public void setRarity(String rarity) { this.rarity.set(rarity); }
    public StringProperty rarityProperty() { return rarity; }

    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String imageUrl) { this.imageUrl.set(imageUrl); }
    public StringProperty imageUrlProperty() { return imageUrl; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name.get();
    }
}