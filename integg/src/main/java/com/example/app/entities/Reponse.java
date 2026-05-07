package com.example.app.entities;

import javafx.beans.property.*;

public class Reponse {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty option = new SimpleStringProperty();
    private final SimpleStringProperty tag = new SimpleStringProperty();
    private Question question;

    public Reponse() {}

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getOption() { return option.get(); }
    public void setOption(String option) { this.option.set(option); }
    public StringProperty optionProperty() { return option; }

    public String getTag() { return tag.get(); }
    public void setTag(String tag) { this.tag.set(tag); }
    public StringProperty tagProperty() { return tag; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}