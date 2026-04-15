package com.example.app.entities;

import java.time.LocalDateTime;

public class Reponse {
    private int id;
    private String contenu;
    private User createur; // L'utilisateur qui répond
    private Question question; // La question liée
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reponse() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public User getCreateur() { return createur; }
    public void setCreateur(User createur) { this.createur = createur; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
