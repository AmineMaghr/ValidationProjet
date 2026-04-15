package com.example.app.services;

import com.example.app.entities.StockPrediction;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service pour les prédictions de stock
 * TODO: Implémenter les vraies prédictions basées sur l'IA/ML
 */
public class StockPredictionService implements Iservice<StockPrediction> {

    private Connection connection;

    public StockPredictionService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(StockPrediction prediction) throws SQLException {
        String sql = "INSERT INTO stock_prediction " +
                "(product_id, product_name, predicted_demand, current_stock, recommended_stock, confidence, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, prediction.getProductId());
        ps.setString(2, prediction.getProductName());
        ps.setDouble(3, prediction.getPredictedDemand());
        ps.setInt(4, prediction.getCurrentStock());
        ps.setInt(5, prediction.getRecommendedStock());
        ps.setDouble(6, prediction.getConfidence());
        ps.setTimestamp(7, Timestamp.valueOf(prediction.getCreatedAt()));

        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            prediction.setId(rs.getInt(1));
        }

        System.out.println("StockPrediction ajouté");
    }

    @Override
    public void update(StockPrediction prediction) throws SQLException {
        String sql = "UPDATE stock_prediction SET " +
                "product_id=?, product_name=?, predicted_demand=?, current_stock=?, recommended_stock=?, confidence=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, prediction.getProductId());
        ps.setString(2, prediction.getProductName());
        ps.setDouble(3, prediction.getPredictedDemand());
        ps.setInt(4, prediction.getCurrentStock());
        ps.setInt(5, prediction.getRecommendedStock());
        ps.setDouble(6, prediction.getConfidence());
        ps.setInt(7, prediction.getId());

        ps.executeUpdate();
        System.out.println("StockPrediction modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM stock_prediction WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("StockPrediction supprimé");
    }

    @Override
    public List<StockPrediction> select() throws SQLException {
        List<StockPrediction> list = new ArrayList<>();
        String sql = "SELECT * FROM stock_prediction ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            StockPrediction prediction = new StockPrediction();
            prediction.setId(rs.getInt("id"));
            prediction.setProductId(rs.getInt("product_id"));
            prediction.setProductName(rs.getString("product_name"));
            prediction.setPredictedDemand(rs.getDouble("predicted_demand"));
            prediction.setCurrentStock(rs.getInt("current_stock"));
            prediction.setRecommendedStock(rs.getInt("recommended_stock"));
            prediction.setConfidence(rs.getDouble("confidence"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                prediction.setCreatedAt(created.toLocalDateTime());
            }

            list.add(prediction);
        }
        return list;
    }

    public void predictAllProducts() {
        System.out.println("Calcul des prédictions de stock pour tous les produits...");
        try {
            // Simulation: créer des prédictions pour quelques produits
            String[] products = {"Épée Légendaire", "Armure Mystique", "Potion Magique", "Bouclier Cassé"};

            for (int i = 0; i < products.length; i++) {
                StockPrediction prediction = new StockPrediction();
                prediction.setProductId(i + 1);
                prediction.setProductName(products[i]);
                prediction.setPredictedDemand(Math.random() * 50 + 10);
                prediction.setCurrentStock((int)(Math.random() * 20 + 5));
                prediction.setRecommendedStock((int)(prediction.getPredictedDemand() * 1.5));
                prediction.setConfidence(Math.random() * 30 + 70); // 70-100%

                add(prediction);
            }

            System.out.println("Prédictions calculées pour " + products.length + " produits");
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des prédictions: " + e.getMessage());
        }
    }

    public List<StockPrediction> findLatestPredictions(int limit) throws SQLException {
        List<StockPrediction> list = new ArrayList<>();
        String sql = "SELECT * FROM stock_prediction ORDER BY created_at DESC LIMIT ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, limit);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            StockPrediction prediction = new StockPrediction();
            prediction.setId(rs.getInt("id"));
            prediction.setProductId(rs.getInt("product_id"));
            prediction.setProductName(rs.getString("product_name"));
            prediction.setPredictedDemand(rs.getDouble("predicted_demand"));
            prediction.setCurrentStock(rs.getInt("current_stock"));
            prediction.setRecommendedStock(rs.getInt("recommended_stock"));
            prediction.setConfidence(rs.getDouble("confidence"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                prediction.setCreatedAt(created.toLocalDateTime());
            }

            list.add(prediction);
        }
        return list;
    }

    public List<Map<String, Object>> getLatestPredictionsAsMap(int limit) {
        List<Map<String, Object>> predictions = new ArrayList<>();

        // Simulation de prédictions
        for (int i = 1; i <= Math.min(limit, 10); i++) {
            Map<String, Object> prediction = Map.of(
                "product_id", i,
                "product_name", "Produit " + i,
                "predicted_demand", Math.random() * 100,
                "current_stock", (int)(Math.random() * 50),
                "recommended_stock", (int)(Math.random() * 200),
                "confidence", Math.random() * 100,
                "created_at", LocalDateTime.now().minusDays(i)
            );
            predictions.add(prediction);
        }

        return predictions;
    }

    public List<Map<String, Object>> findCriticalProducts(int days) {
        List<Map<String, Object>> critical = new ArrayList<>();

        // Simulation de produits critiques
        critical.add(Map.of(
            "product_name", "Épée Légendaire",
            "current_stock", 2,
            "predicted_demand", 15,
            "days_until_stockout", 3
        ));

        critical.add(Map.of(
            "product_name", "Potion Magique",
            "current_stock", 5,
            "predicted_demand", 20,
            "days_until_stockout", 7
        ));

        return critical;
    }
}
