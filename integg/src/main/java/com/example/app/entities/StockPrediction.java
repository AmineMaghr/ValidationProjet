package com.example.app.entities;

import java.time.LocalDateTime;

public class StockPrediction {
    private int id;
    private int productId;
    private String productName;
    private double predictedDemand;
    private int currentStock;
    private int recommendedStock;
    private double confidence;
    private LocalDateTime createdAt;

    public StockPrediction() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(double predictedDemand) { this.predictedDemand = predictedDemand; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public int getRecommendedStock() { return recommendedStock; }
    public void setRecommendedStock(int recommendedStock) { this.recommendedStock = recommendedStock; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("%s: Demande=%.1f, Stock=%d, Recommandé=%d (%.1f%% confiance)",
            productName, predictedDemand, currentStock, recommendedStock, confidence);
    }
}
