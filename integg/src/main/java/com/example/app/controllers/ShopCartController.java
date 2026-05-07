package com.example.app.controllers;

import com.example.app.entities.Produit;
import com.example.app.services.ProduitService;
import com.example.app.utils.CartService;
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
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ShopCartController extends BaseController {

    @FXML private VBox cartItemsContainer;
    @FXML private Label itemCountLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label shippingLabel;
    @FXML private Label totalLabel;
    @FXML private Button checkoutButton;

    private final CartService cartService = CartService.getInstance();
    private final ProduitService produitService = new ProduitService();

    @FXML
    public void initialize() {
        refreshCart();
    }

    private void refreshCart() {
        Map<Integer, Integer> cart = cartService.getCart();

        List<Produit> produits;
        try {
            produits = produitService.findByIds(cart.keySet());
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger le panier: " + e.getMessage());
            produits = List.of();
        }

        Map<Integer, Produit> productsById = produits.stream()
                .collect(Collectors.toMap(Produit::getId, p -> p));

        cartItemsContainer.getChildren().clear();
        cartItemsContainer.getChildren().add(itemCountLabel);

        double subtotal = 0.0;
        int totalItems = 0;

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Produit produit = productsById.get(entry.getKey());
            if (produit == null) {
                continue;
            }

            int quantity = entry.getValue();
            totalItems += quantity;
            subtotal += quantity * produit.getPrix();

            cartItemsContainer.getChildren().add(buildCartItem(produit, quantity));
        }

        if (totalItems == 0) {
            Label empty = new Label("Votre panier est vide.");
            empty.setStyle("-fx-text-fill: #b0b9b6; -fx-font-size: 16;");
            cartItemsContainer.getChildren().add(empty);
        }

        double shipping = subtotal > 0 ? 5.0 : 0.0;
        double total = subtotal + shipping;

        itemCountLabel.setText("Articles (" + totalItems + ")");
        subtotalLabel.setText(String.format("%.2f€", subtotal));
        shippingLabel.setText(String.format("%.2f€", shipping));
        totalLabel.setText(String.format("%.2f€", total));
        checkoutButton.setDisable(totalItems == 0);
    }

    private VBox buildCartItem(Produit produit, int quantity) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: #1a1f1e; -fx-background-radius: 10; -fx-padding: 15;");

        Label name = new Label(produit.getNom());
        name.setStyle("-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 15;");

        Label type = new Label(produit.getType());
        type.setStyle("-fx-text-fill: #888;");

        Label price = new Label(String.format("%.2f€", produit.getPrix()));
        price.setStyle("-fx-text-fill: #18E3A4;");

        Label qtyLabel = new Label(String.valueOf(quantity));
        qtyLabel.setStyle("-fx-text-fill: #fff;");

        Button minus = new Button("-");
        minus.setStyle("-fx-background-color: #2a3139; -fx-text-fill: #fff;");
        minus.setOnAction(e -> updateQuantity(produit, quantity - 1, 1));

        Button plus = new Button("+");
        plus.setStyle("-fx-background-color: #2a3139; -fx-text-fill: #fff;");
        plus.setOnAction(e -> updateQuantity(produit, quantity + 1, -1));

        Button remove = new Button("✕");
        remove.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff6b6b;");
        remove.setOnAction(e -> removeItem(produit, quantity));

        HBox qtyBox = new HBox(8, minus, qtyLabel, plus);
        qtyBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(10, name, spacer, remove);
        top.setAlignment(Pos.CENTER_LEFT);

        HBox bottom = new HBox(12, type, price, qtyBox);
        bottom.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(top, bottom);
        return card;
    }

    private void updateQuantity(Produit produit, int newQuantity, int stockDelta) {
        if (newQuantity <= 0) {
            removeItem(produit, cartService.getCart().getOrDefault(produit.getId(), 1));
            return;
        }

        if (stockDelta < 0 && produit.getQuantiteDisponible() <= 0) {
            showError("Stock", "Plus de stock disponible pour ce produit.");
            return;
        }

        cartService.updateQuantity(produit.getId(), newQuantity);
        adjustStock(produit, stockDelta);
        refreshCart();
    }

    private void removeItem(Produit produit, int quantityInCart) {
        cartService.removeItem(produit.getId());
        adjustStock(produit, quantityInCart);
        refreshCart();
    }

    private void adjustStock(Produit produit, int delta) {
        if (delta == 0) {
            return;
        }

        produit.setQuantiteDisponible(Math.max(0, produit.getQuantiteDisponible() + delta));
        try {
            produitService.update(produit);
        } catch (SQLException e) {
            showError("Erreur stock", "Impossible de mettre à jour le stock: " + e.getMessage());
        }
    }

    @FXML
    private void checkout() {
        if (cartService.isEmpty()) {
            showError("Panier", "Votre panier est vide.");
            return;
        }
        navigateTo("/shop/checkout");
    }

    @FXML
    private void continueShopping() {
        navigateTo("/shop");
    }
}
