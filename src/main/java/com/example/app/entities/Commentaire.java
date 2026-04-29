package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Commentaire {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty contenu = new SimpleStringProperty();
    private final SimpleIntegerProperty userId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty oeuvreId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty artefactId = new SimpleIntegerProperty();
    private LocalDateTime createdAt;
    private User user;

    public Commentaire() {
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getContenu() { return contenu.get(); }
    public void setContenu(String contenu) { this.contenu.set(contenu); }
    public StringProperty contenuProperty() { return contenu; }

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

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId.set(user.getId());
        }
    }
}