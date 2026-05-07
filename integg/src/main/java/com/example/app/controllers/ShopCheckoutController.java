package com.example.app.controllers;

import com.example.app.dao.CommandeDAO;
import com.example.app.entities.Commande;
import com.example.app.entities.Produit;
import com.example.app.services.ProduitService;
import com.example.app.utils.CartService;
import com.example.app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShopCheckoutController extends BaseController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField zipCodeField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;
    @FXML private Button payButton;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalLabel;

    private final CartService cartService = CartService.getInstance();
    private final ProduitService produitService = new ProduitService();
    private final CommandeDAO commandeDAO = new CommandeDAO();

    private Map<Integer, Integer> cart;
    private Map<Integer, Produit> productsById;
    private double total;

    @FXML
    public void initialize() {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser() != null) {
            String username = UserSession.getCurrentUser().getUsername();
            if (username != null) {
                firstNameField.setText(username);
            }
        }

        loadSummary();
    }

    private void loadSummary() {
        cart = cartService.getCart();
        if (cart.isEmpty()) {
            showError("Panier", "Votre panier est vide.");
            navigateTo("/shop");
            return;
        }

        try {
            List<Produit> produits = produitService.findByIds(cart.keySet());
            productsById = produits.stream().collect(Collectors.toMap(Produit::getId, p -> p));
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger le récapitulatif: " + e.getMessage());
            navigateTo("/shop/cart");
            return;
        }

        orderItemsContainer.getChildren().clear();
        total = 0.0;

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Produit p = productsById.get(entry.getKey());
            if (p == null) {
                continue;
            }

            int qty = entry.getValue();
            double lineTotal = qty * p.getPrix();
            total += lineTotal;

            HBox row = new HBox();
            Label left = new Label(p.getNom() + " x" + qty);
            left.setStyle("-fx-text-fill: #fff;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            Label right = new Label(String.format("%.2f€", lineTotal));
            right.setStyle("-fx-text-fill: #18E3A4;");
            row.getChildren().addAll(left, spacer, right);
            orderItemsContainer.getChildren().add(row);
        }

        total += 5.0;
        totalLabel.setText(String.format("%.2f€", total));
        payButton.setText(String.format("Payer %.2f€", total));
    }

    @FXML
    private void processPayment() {
        if (!validateForm()) {
            return;
        }

        String buyer = (firstNameField.getText().trim() + " " + lastNameField.getText().trim()).trim();

        try {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                Produit produit = productsById.get(entry.getKey());
                if (produit == null) {
                    continue;
                }

                int qty = entry.getValue();
                Commande commande = new Commande();
                commande.setAcheteur(buyer);
                commande.setProduit(produit);
                commande.setQuantite(qty);
                commande.setEtat("CONFIRMEE");
                commande.setDateCommande(LocalDateTime.now());
                commande.setPrixTotal(qty * produit.getPrix());
                commande.setReferenceCommande("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

                commandeDAO.add(commande);
            }

            cartService.clearCart();
            showAlert("Commande validée", "Paiement accepté, commande créée avec succès.");
            navigateTo("/shop/orders");
        } catch (SQLException e) {
            showError("Erreur commande", "Impossible de créer la commande: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (isBlank(firstNameField) || isBlank(lastNameField) || isBlank(addressField)
                || isBlank(cityField) || isBlank(zipCodeField)) {
            showError("Saisie invalide", "Veuillez remplir les informations de livraison.");
            return false;
        }

        String card = cardNumberField.getText().replaceAll("\\s+", "");
        if (!card.matches("\\d{13,19}")) {
            showError("Saisie invalide", "Numéro de carte invalide.");
            return false;
        }

        if (!expiryDateField.getText().trim().matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            showError("Saisie invalide", "Date d'expiration invalide (MM/AA).");
            return false;
        }

        if (!cvvField.getText().trim().matches("\\d{3,4}")) {
            showError("Saisie invalide", "CVV invalide.");
            return false;
        }

        return true;
    }

    private boolean isBlank(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    @FXML
    private void goBackToCart() {
        navigateTo("/shop/cart");
    }
}
