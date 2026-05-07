package com.example.app.dao;

import com.example.app.entities.StockPrediction;
import com.example.app.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StockPredictionDAO implements IDAO<StockPrediction> {

    private Connection connection;

    public StockPredictionDAO() {
        connection = MyDatabase.getInstance().getConnection();
        try {
            ensureTable();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void add(StockPrediction prediction) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        String sql = "INSERT INTO stock_prediction (product_id, product_name, predicted_demand, current_stock, recommended_stock, confidence, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, prediction.getProductId());
        ps.setString(2, prediction.getProductName());
        ps.setDouble(3, prediction.getPredictedDemand());
        ps.setInt(4, prediction.getCurrentStock());
        ps.setInt(5, prediction.getRecommendedStock());
        ps.setDouble(6, prediction.getConfidence());
        ps.setTimestamp(7, Timestamp.valueOf(prediction.getCreatedAt()));
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            prediction.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(StockPrediction prediction) {
        throw new UnsupportedOperationException("La mise à jour des prédictions n'est pas supportée.");
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM stock_prediction WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<StockPrediction> select() throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        List<StockPrediction> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM stock_prediction ORDER BY created_at DESC, id DESC");
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<StockPrediction> findLatest(int limit) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        List<StockPrediction> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM stock_prediction ORDER BY created_at DESC, id DESC LIMIT ?");
        ps.setInt(1, Math.max(1, limit));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<StockPrediction> findCritical(int limit) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        List<StockPrediction> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM stock_prediction WHERE recommended_stock > current_stock ORDER BY (recommended_stock - current_stock) DESC, created_at DESC LIMIT ?");
        ps.setInt(1, Math.max(1, limit));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public void cleanupOlderThan(int daysToKeep) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM stock_prediction WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)");
        ps.setInt(1, Math.max(1, daysToKeep));
        ps.executeUpdate();
    }

    private StockPrediction mapResultSet(ResultSet rs) throws SQLException {
        StockPrediction prediction = new StockPrediction();
        prediction.setId(rs.getInt("id"));
        prediction.setProductId(rs.getInt("product_id"));
        prediction.setProductName(rs.getString("product_name"));
        prediction.setPredictedDemand(rs.getDouble("predicted_demand"));
        prediction.setCurrentStock(rs.getInt("current_stock"));
        prediction.setRecommendedStock(rs.getInt("recommended_stock"));
        prediction.setConfidence(rs.getDouble("confidence"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            prediction.setCreatedAt(createdAt.toLocalDateTime());
        }
        return prediction;
    }

    private Connection requireConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = MyDatabase.getConnection();
            }
        } catch (SQLException e) {
            connection = null;
        }

        if (connection == null) {
            throw new SQLException("Connexion MySQL indisponible pour les prédictions de stock.");
        }

        return connection;
    }

    private void ensureTable() throws SQLException {
        Connection conn = requireConnection();
        String sql = "CREATE TABLE IF NOT EXISTS stock_prediction ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "product_id INT NOT NULL,"
                + "product_name VARCHAR(255) NOT NULL,"
                + "predicted_demand DOUBLE NOT NULL,"
                + "current_stock INT NOT NULL,"
                + "recommended_stock INT NOT NULL,"
                + "confidence DOUBLE NOT NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "INDEX idx_stock_prediction_product (product_id),"
                + "INDEX idx_stock_prediction_created (created_at)"
                + ")";
        Statement st = conn.createStatement();
        st.execute(sql);
    }
}
