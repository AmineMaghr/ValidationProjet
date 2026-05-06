package com.example.app.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Service pour les analytics de la boutique
 * TODO: Implémenter les vraies fonctionnalités d'analytics
 */
public class ShopAnalyticsService {

    public Map<String, Object> getKPIs() {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("total_sales", 1250.50);
        kpis.put("total_orders", 45);
        kpis.put("average_order", 27.79);
        kpis.put("total_customers", 32);
        return kpis;
    }

    public Map<String, Object> getCustomerSegments() {
        Map<String, Object> segments = new HashMap<>();
        segments.put("new_customers", 12);
        segments.put("regular_customers", 15);
        segments.put("vip_customers", 5);
        return segments;
    }

    public Map<String, Object> getDailySalesLast7Days() {
        Map<String, Object> sales = new HashMap<>();
        // Simulation des ventes des 7 derniers jours
        sales.put("2024-01-01", 150.00);
        sales.put("2024-01-02", 200.00);
        sales.put("2024-01-03", 175.00);
        sales.put("2024-01-04", 220.00);
        sales.put("2024-01-05", 190.00);
        sales.put("2024-01-06", 180.00);
        sales.put("2024-01-07", 135.00);
        return sales;
    }

    public Map<String, Object> getTopProducts() {
        Map<String, Object> products = new HashMap<>();
        products.put("product_1", Map.of("name", "Épée Légendaire", "sales", 25));
        products.put("product_2", Map.of("name", "Armure Mystique", "sales", 18));
        products.put("product_3", Map.of("name", "Potion Magique", "sales", 15));
        return products;
    }

    public Map<String, Object> getProductPerformance() {
        Map<String, Object> performance = new HashMap<>();
        performance.put("best_seller", "Épée Légendaire");
        performance.put("worst_seller", "Bouclier Cassé");
        performance.put("trending", "Potion Magique");
        return performance;
    }
}
