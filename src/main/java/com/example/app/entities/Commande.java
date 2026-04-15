package com.example.app.entities;
import java.time.LocalDateTime;

public class Commande {
    private int id;
    private int quantite;
    private LocalDateTime dateCommande;
    private String etat;      // EN_ATTENTE, CONFIRME, LIVRE, etc.
    private User acheteur;
    private double prixTotal;
    private String referenceCommande;
    private Produit produit;

    public Commande() {
        this.dateCommande = LocalDateTime.now();
        this.etat = "EN_ATTENTE";
        this.referenceCommande = "CMD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 9000 + 1000);
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public User getAcheteur() { return acheteur; }
    public void setAcheteur(User acheteur) { this.acheteur = acheteur; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public String getReferenceCommande() { return referenceCommande; }
    public void setReferenceCommande(String referenceCommande) { this.referenceCommande = referenceCommande; }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
}
