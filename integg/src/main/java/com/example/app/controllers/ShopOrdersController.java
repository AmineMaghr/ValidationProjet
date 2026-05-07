package com.example.app.controllers;

import com.example.app.dao.CommandeDAO;
import com.example.app.entities.Commande;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class ShopOrdersController extends BaseController {

    @FXML private VBox ordersContainer;

    private final CommandeDAO commandeDAO = new CommandeDAO();

    @FXML
    public void initialize() {
        loadOrders();
    }

    private void loadOrders() {
        if (ordersContainer == null) {
            return;
        }

        ordersContainer.getChildren().clear();

        String acheteur = UserSession.isLoggedIn() && UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getUsername()
                : null;

        try {
            List<Commande> commandes = (acheteur != null && !acheteur.isBlank())
                    ? commandeDAO.findByAcheteur(acheteur)
                    : commandeDAO.select();

            if (commandes.isEmpty()) {
                Label empty = new Label("Aucune commande pour le moment.");
                empty.setStyle("-fx-text-fill: #b0b9b6; -fx-font-size: 16;");
                ordersContainer.getChildren().add(empty);
                return;
            }

            for (Commande commande : commandes) {
                ordersContainer.getChildren().add(buildOrderCard(commande));
            }
        } catch (SQLException e) {
            Label error = new Label("Erreur chargement commandes: " + e.getMessage());
            error.setStyle("-fx-text-fill: #f87171;");
            ordersContainer.getChildren().add(error);
        }
    }

    private VBox buildOrderCard(Commande commande) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #1a1f1e; -fx-background-radius: 10; -fx-padding: 20;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ref = new Label("Commande #" + commande.getReferenceCommande());
        ref.setStyle("-fx-text-fill: #fff; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label state = new Label(commande.getEtat());
        state.setStyle("-fx-text-fill: #18E3A4;");

        top.getChildren().addAll(ref, spacer, state);

        String produitNom = commande.getProduit() != null && commande.getProduit().getNom() != null
                ? commande.getProduit().getNom() : "Produit";

        Label details = new Label("Produit: " + produitNom + " | Quantité: " + commande.getQuantite()
                + " | Montant: " + String.format("%.2f€", commande.getPrixTotal()));
        details.setStyle("-fx-text-fill: #b0b9b6;");

        Button backShop = new Button("Retour boutique");
        backShop.setStyle("-fx-background-color: #2a3139; -fx-text-fill: #fff;");
        backShop.setOnAction(e -> navigateTo("/shop"));

        card.getChildren().addAll(top, details, backShop);
        return card;
    }

    @FXML
    private void goBack() {
        navigateTo("/shop");
    }
}
