package com.example.app.entities;
import java.time.LocalDateTime;

public class Participation {
    private int id;
    private User user; // référence à un objet User
    private Defi defi; // référence à un objet Defi
    private LocalDateTime dateParticipation;

    public Participation() {
        this.dateParticipation = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Defi getDefi() {
        return defi;
    }

    public void setDefi(Defi defi) {
        this.defi = defi;
    }

    public LocalDateTime getDateParticipation() {
        return dateParticipation;
    }

    public void setDateParticipation(LocalDateTime dateParticipation) {
        this.dateParticipation = dateParticipation;
    }
}
