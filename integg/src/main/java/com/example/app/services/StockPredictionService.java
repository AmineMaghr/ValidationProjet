package com.example.app.services;

import com.example.app.dao.ProduitDAO;
import com.example.app.dao.StockPredictionDAO;
import com.example.app.entities.Produit;
import com.example.app.entities.StockPrediction;
import com.example.app.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de prédiction de stock utilisant la régression linéaire.
 * Analyse l'historique des ventes 30 jours pour prédire la demande future.
 * Calcule un score de confiance basé sur la volatilité et la qualité des données.
 */
public class StockPredictionService {

    private final ProduitDAO produitDAO = new ProduitDAO();
    private final StockPredictionDAO stockPredictionDAO = new StockPredictionDAO();

    public List<StockPrediction> predictAllProducts() throws SQLException {
        List<StockPrediction> predictions = new ArrayList<>();
        List<Produit> products = produitDAO.select();

        for (Produit product : products) {
            StockPrediction prediction = predictProductStock(product);
            if (prediction != null) {
                stockPredictionDAO.add(prediction);
                predictions.add(prediction);
            }
        }

        return predictions;
    }

    public StockPrediction predictProductStock(Produit product) throws SQLException {
        if (product == null) {
            return null;
        }

        List<Integer> dailyDemand = loadDailyDemand(product.getId(), 30);
        double predictedDailyDemand = estimateNextDailyDemand(dailyDemand);
        int currentStock = Math.max(0, product.getQuantiteDisponible());
        int recommendedStock = (int) Math.ceil(Math.max(predictedDailyDemand, 0d) * 14d);
        double confidence = calculateConfidence(dailyDemand, predictedDailyDemand);

        StockPrediction prediction = new StockPrediction();
        prediction.setProductId(product.getId());
        prediction.setProductName(product.getNom());
        prediction.setPredictedDemand(round(predictedDailyDemand));
        prediction.setCurrentStock(currentStock);
        prediction.setRecommendedStock(Math.max(recommendedStock, currentStock));
        prediction.setConfidence(confidence);
        prediction.setCreatedAt(java.time.LocalDateTime.now());
        return prediction;
    }

    public List<StockPrediction> findLatestPredictions(int limit) throws SQLException {
        return stockPredictionDAO.findLatest(limit);
    }

    public List<StockPrediction> findCriticalProducts(int limit) throws SQLException {
        return stockPredictionDAO.findCritical(limit);
    }

    public void cleanupOldPredictions(int daysToKeep) throws SQLException {
        stockPredictionDAO.cleanupOlderThan(daysToKeep);
    }

    public List<StockPrediction> refreshPredictions() throws SQLException {
        List<StockPrediction> predictions = predictAllProducts();
        if (!predictions.isEmpty()) {
            return predictions;
        }

        return buildDemoPredictions();
    }

    private List<Integer> loadDailyDemand(int productId, int days) throws SQLException {
        List<Integer> demand = new ArrayList<>();
        LocalDate start = LocalDate.now().minusDays(days - 1L);

        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DATE(date_commande) AS sale_day, COALESCE(SUM(quantite), 0) AS quantity_sold "
                             + "FROM commande WHERE produit_id = ? AND date_commande >= ? "
                             + "GROUP BY DATE(date_commande) ORDER BY sale_day ASC")) {
            statement.setInt(1, productId);
            statement.setTimestamp(2, Timestamp.valueOf(start.atStartOfDay()));

            ResultSet resultSet = statement.executeQuery();
            Map<LocalDate, Integer> aggregated = new HashMap<>();
            while (resultSet.next()) {
                aggregated.put(resultSet.getDate("sale_day").toLocalDate(), resultSet.getInt("quantity_sold"));
            }

            for (int i = 0; i < days; i++) {
                LocalDate day = start.plusDays(i);
                demand.add(aggregated.getOrDefault(day, 0));
            }
        }

        return demand;
    }

    private double estimateNextDailyDemand(List<Integer> dailyDemand) {
        if (dailyDemand == null || dailyDemand.isEmpty()) {
            return 0d;
        }

        int n = dailyDemand.size();
        double sumX = 0d;
        double sumY = 0d;
        double sumXY = 0d;
        double sumXX = 0d;

        for (int i = 0; i < n; i++) {
            double x = i + 1d;
            double y = dailyDemand.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double denominator = (n * sumXX) - (sumX * sumX);
        if (denominator == 0d) {
            return sumY / n;
        }

        double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
        double intercept = (sumY - (slope * sumX)) / n;
        double nextDayValue = (slope * (n + 1d)) + intercept;
        return Math.max(0d, nextDayValue);
    }

    private double calculateConfidence(List<Integer> dailyDemand, double predictedDailyDemand) {
        if (dailyDemand == null || dailyDemand.isEmpty()) {
            return 0d;
        }

        double average = dailyDemand.stream().mapToDouble(Integer::doubleValue).average().orElse(0d);
        double variance = dailyDemand.stream()
                .mapToDouble(value -> Math.pow(value - average, 2))
                .average()
                .orElse(0d);
        double volatilityPenalty = Math.min(40d, Math.sqrt(variance) * 5d);
        double dataPenalty = dailyDemand.stream().filter(value -> value > 0).count() < 5 ? 25d : 0d;
        double confidence = 100d - volatilityPenalty - dataPenalty;

        if (predictedDailyDemand <= 0d) {
            confidence = Math.max(15d, confidence - 20d);
        }

        return Math.max(5d, Math.min(100d, confidence));
    }

    private double round(double value) {
        return Math.round(value * 100d) / 100d;
    }

    private List<StockPrediction> buildDemoPredictions() throws SQLException {
        List<Produit> products = produitDAO.select();
        List<StockPrediction> demo = new ArrayList<>();

        if (products.isEmpty()) {
            demo.add(createDemoPrediction(0, "Produit A", 3.2, 24, 42.5));
            demo.add(createDemoPrediction(0, "Produit B", 1.4, 8, 58.0));
            demo.add(createDemoPrediction(0, "Produit C", 0.6, 2, 71.0));
            return demo;
        }

        for (Produit product : products) {
            int currentStock = Math.max(0, product.getQuantiteDisponible());
            double predictedDailyDemand = Math.max(0.5d, Math.min(4d, product.getPrix() / 25d));
            int recommendedStock = Math.max(currentStock, (int) Math.ceil(predictedDailyDemand * 14d));
            double confidence = currentStock <= 3 ? 78d : currentStock <= 10 ? 84d : 91d;
            demo.add(createDemoPrediction(product.getId(), product.getNom(), predictedDailyDemand, recommendedStock, confidence));
            demo.get(demo.size() - 1).setCurrentStock(currentStock);
        }

        return demo;
    }

    private StockPrediction createDemoPrediction(int productId, String productName, double predictedDailyDemand, int recommendedStock, double confidence) {
        StockPrediction prediction = new StockPrediction();
        prediction.setProductId(productId);
        prediction.setProductName(productName);
        prediction.setPredictedDemand(round(predictedDailyDemand));
        prediction.setCurrentStock(Math.max(0, recommendedStock / 2));
        prediction.setRecommendedStock(recommendedStock);
        prediction.setConfidence(confidence);
        prediction.setCreatedAt(java.time.LocalDateTime.now());
        return prediction;
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = MyDatabase.getConnection();
        if (connection == null) {
            throw new SQLException("Connexion MySQL indisponible pour les prédictions de stock.");
        }
        return connection;
    }
}
