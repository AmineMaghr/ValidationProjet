package com.example.app.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CartService {
    private static final CartService INSTANCE = new CartService();
    private final Map<Integer, Integer> cart = new LinkedHashMap<>();

    public static CartService getInstance() {
        return INSTANCE;
    }

    public CartService() {}

    public void addItem(int productId, int quantity) {
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
    }

    public void removeItem(int productId) {
        cart.remove(productId);
    }

    public void updateQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
        } else {
            cart.put(productId, quantity);
        }
    }

    public Map<Integer, Integer> getCart() { return cart; }

    public void clearCart() { cart.clear(); }

    public boolean isEmpty() { return cart.isEmpty(); }

    public int getCartCount() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }
}