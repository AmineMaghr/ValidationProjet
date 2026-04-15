package com.example.app.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private String titre;
    private String contenu;
    private User createur; // L'utilisateur qui pose la question
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Liste des réponses liées à cette question
    private List<Reponse> reponses;

    public Question() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.reponses = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public User getCreateur() { return createur; }
    public void setCreateur(User createur) { this.createur = createur; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }

    public void addReponse(Reponse reponse) {
        this.reponses.add(reponse);
        reponse.setQuestion(this);
    }
}
