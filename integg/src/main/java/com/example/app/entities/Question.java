package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty question = new SimpleStringProperty();
    private LocalDateTime createdAt;
    private List<Reponse> reponses = new ArrayList<>();

    public Question() {
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getQuestion() { return question.get(); }
    public void setQuestion(String question) { this.question.set(question); }
    public StringProperty questionProperty() { return question; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }
}