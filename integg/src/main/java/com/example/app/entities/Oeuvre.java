package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Oeuvre {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleStringProperty imageUrl = new SimpleStringProperty();
    private final SimpleStringProperty author = new SimpleStringProperty();
    private final SimpleIntegerProperty createurId = new SimpleIntegerProperty();
    private LocalDate datePublication;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User createdBy;
    private Universe universe;

    // Pour affichage dans tableaux
    private final SimpleStringProperty datePublicationDisplay = new SimpleStringProperty();

    // Liste d'erreurs de validation
    private List<String> validationErrors = new ArrayList<>();

    public Oeuvre() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.datePublication = LocalDate.now(); // Date automatique = aujourd'hui
        updateDisplayDates();
    }

    private void updateDisplayDates() {
        if (datePublication != null) {
            datePublicationDisplay.set(datePublication.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    // Validation complète
    public boolean isValid() {
        validationErrors.clear();
        
        // Validation du titre (requis)
        if (getTitle() == null || getTitle().trim().isEmpty()) {
            validationErrors.add("Le titre est requis");
        }
        
        // Validation du type (requis)
        if (getType() == null || getType().trim().isEmpty()) {
            validationErrors.add("Le type est requis");
        }
        
        // Validation de l'auteur (requis)
        if (getAuthor() == null || getAuthor().trim().isEmpty()) {
            validationErrors.add("L'auteur est requis");
        }
        
        // Validation de l'image (requise)
        if (getImageUrl() == null || getImageUrl().trim().isEmpty()) {
            validationErrors.add("L'image est requise");
        }
        
        // Validation de la description
        if (getDescription() == null || getDescription().trim().isEmpty()) {
            validationErrors.add("La description est requise");
        } else if (getDescription().length() < 10) {
            validationErrors.add("La description doit contenir au moins 10 caractères");
        } else if (!getDescription().matches("^[a-zA-Z].*$")) {
            validationErrors.add("La description doit commencer par une lettre");
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

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }
    public StringProperty typeProperty() { return type; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String imageUrl) { this.imageUrl.set(imageUrl); }
    public StringProperty imageUrlProperty() { return imageUrl; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }
    public StringProperty authorProperty() { return author; }

    public int getCreateurId() { return createurId.get(); }
    public void setCreateurId(int createurId) { this.createurId.set(createurId); }
    public IntegerProperty createurIdProperty() { return createurId; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
        updateDisplayDates();
    }

    public StringProperty datePublicationProperty() { return datePublicationDisplay; }
    public String getDatePublicationDisplay() { return datePublicationDisplay.get(); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Universe getUniverse() { return universe; }
    public void setUniverse(Universe universe) { this.universe = universe; }
    
    @Override
    public String toString() {
        return title.get() + " - " + author.get();
    }
}