package com.example.app.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Commande {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleIntegerProperty quantite = new SimpleIntegerProperty();
    private final SimpleStringProperty etat = new SimpleStringProperty();
    private final SimpleStringProperty acheteur = new SimpleStringProperty();  // ← String, pas User
    private final SimpleDoubleProperty prixTotal = new SimpleDoubleProperty();
    private final SimpleStringProperty referenceCommande = new SimpleStringProperty();
    private LocalDateTime dateCommande;
    private Produit produit;

    public Commande() {
        this.dateCommande = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getQuantite() { return quantite.get(); }
    public void setQuantite(int quantite) { this.quantite.set(quantite); }
    public IntegerProperty quantiteProperty() { return quantite; }

    public String getEtat() { return etat.get(); }
    public void setEtat(String etat) { this.etat.set(etat); }
    public StringProperty etatProperty() { return etat; }

    public String getAcheteur() { return acheteur.get(); }  // ← retourne String
    public void setAcheteur(String acheteur) { this.acheteur.set(acheteur); }
    public StringProperty acheteurProperty() { return acheteur; }

    public double getPrixTotal() { return prixTotal.get(); }
    public void setPrixTotal(double prixTotal) { this.prixTotal.set(prixTotal); }
    public DoubleProperty prixTotalProperty() { return prixTotal; }

    public String getReferenceCommande() { return referenceCommande.get(); }
    public void setReferenceCommande(String referenceCommande) { this.referenceCommande.set(referenceCommande); }
    public StringProperty referenceCommandeProperty() { return referenceCommande; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
}