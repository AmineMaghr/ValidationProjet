package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Favoris {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleIntegerProperty userId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty oeuvreId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty artefactId = new SimpleIntegerProperty();
    private LocalDateTime createdAt;

    public Favoris() {
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int userId) { this.userId.set(userId); }
    public IntegerProperty userIdProperty() { return userId; }

    public int getOeuvreId() { return oeuvreId.get(); }
    public void setOeuvreId(int oeuvreId) { this.oeuvreId.set(oeuvreId); }
    public IntegerProperty oeuvreIdProperty() { return oeuvreId; }

    public int getArtefactId() { return artefactId.get(); }
    public void setArtefactId(int artefactId) { this.artefactId.set(artefactId); }
    public IntegerProperty artefactIdProperty() { return artefactId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}