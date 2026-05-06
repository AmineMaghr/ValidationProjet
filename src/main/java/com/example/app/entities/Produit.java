package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Produit {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty nom = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleDoubleProperty prix = new SimpleDoubleProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    private final SimpleIntegerProperty quantiteDisponible = new SimpleIntegerProperty();
    private LocalDateTime dateAjout;
    private List<Commande> commandes = new ArrayList<>();

    public Produit() {
        this.dateAjout = LocalDateTime.now();
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public double getPrix() { return prix.get(); }
    public void setPrix(double prix) { this.prix.set(prix); }
    public DoubleProperty prixProperty() { return prix; }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }
    public StringProperty typeProperty() { return type; }

    public int getQuantiteDisponible() { return quantiteDisponible.get(); }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible.set(quantiteDisponible); }
    public IntegerProperty quantiteDisponibleProperty() { return quantiteDisponible; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }

    public List<Commande> getCommandes() { return commandes; }
    public void setCommandes(List<Commande> commandes) { this.commandes = commandes; }
}