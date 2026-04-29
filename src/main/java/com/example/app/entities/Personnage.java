package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Personnage {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty classRole = new SimpleStringProperty();
    private final SimpleStringProperty historyContext = new SimpleStringProperty();
    private final SimpleStringProperty abilitiesPowers = new SimpleStringProperty();
    private final SimpleIntegerProperty strength = new SimpleIntegerProperty();
    private final SimpleIntegerProperty agility = new SimpleIntegerProperty();
    private final SimpleIntegerProperty magic = new SimpleIntegerProperty();
    private final SimpleIntegerProperty defense = new SimpleIntegerProperty();
    private byte[] portraitImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Universe universe;

    public Personnage() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getClassRole() { return classRole.get(); }
    public void setClassRole(String classRole) { this.classRole.set(classRole); }
    public StringProperty classRoleProperty() { return classRole; }

    public String getHistoryContext() { return historyContext.get(); }
    public void setHistoryContext(String historyContext) { this.historyContext.set(historyContext); }
    public StringProperty historyContextProperty() { return historyContext; }

    public String getAbilitiesPowers() { return abilitiesPowers.get(); }
    public void setAbilitiesPowers(String abilitiesPowers) { this.abilitiesPowers.set(abilitiesPowers); }
    public StringProperty abilitiesPowersProperty() { return abilitiesPowers; }

    public int getStrength() { return strength.get(); }
    public void setStrength(int strength) { this.strength.set(strength); }
    public IntegerProperty strengthProperty() { return strength; }

    public int getAgility() { return agility.get(); }
    public void setAgility(int agility) { this.agility.set(agility); }
    public IntegerProperty agilityProperty() { return agility; }

    public int getMagic() { return magic.get(); }
    public void setMagic(int magic) { this.magic.set(magic); }
    public IntegerProperty magicProperty() { return magic; }

    public int getDefense() { return defense.get(); }
    public void setDefense(int defense) { this.defense.set(defense); }
    public IntegerProperty defenseProperty() { return defense; }

    public byte[] getPortraitImage() { return portraitImage; }
    public void setPortraitImage(byte[] portraitImage) { this.portraitImage = portraitImage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Universe getUniverse() { return universe; }
    public void setUniverse(Universe universe) { this.universe = universe; }

    public String getPortraitBase64() {
        if (portraitImage == null || portraitImage.length == 0) return null;
        return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(portraitImage);
    }
    // Les noms des méthodes doivent correspondre à ceux utilisés dans les contrôleurs
    public String getNom() { return getName(); }
    public void setNom(String nom) { setName(nom); }

    public String getDescription() { return getHistoryContext(); }
    public void setDescription(String description) { setHistoryContext(description); }

    public String getUnivers() {
        return getUniverse() != null ? getUniverse().getName() : null;
    }
    public void setUnivers(String universeName) {
        if (universeName != null) {
            Universe u = new Universe();
            u.setName(universeName);
            setUniverse(u);
        }
    }
}