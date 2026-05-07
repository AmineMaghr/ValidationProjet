package com.example.app.services;

import com.example.app.dao.ProduitDAO;
import com.example.app.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service d'analytics pour la boutique.
 */
public class ShopAnalyticsService {

    public static class ProductPerformanceRow {
        private final int productId;
        private final String productName;
        private final int demand;
        private final double estimatedMargin;
        private final String stockRisk;
        private final int currentStock;

        public ProductPerformanceRow(int productId, String productName, int demand, double estimatedMargin, String stockRisk, int currentStock) {
            this.productId = productId;
            this.productName = productName;
            this.demand = demand;
            this.estimatedMargin = estimatedMargin;
            this.stockRisk = stockRisk;
            this.currentStock = currentStock;
        }

        public int getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getDemand() {
            return demand;
        }

        public double getEstimatedMargin() {
            return estimatedMargin;
        }

        public String getStockRisk() {
            return stockRisk;
        }

        public int getCurrentStock() {
            return currentStock;
        }
    }

    private final ProduitDAO produitDAO = new ProduitDAO();

    public double getSalesLast30Days() throws SQLException {
        return sumSalesBetween(LocalDate.now().minusDays(29), LocalDate.now());
    }

    public int getOrderCountLast30Days() throws SQLException {
        return countOrdersBetween(LocalDate.now().minusDays(29), LocalDate.now());
    }

    public double getGrowthPercentage() throws SQLException {
        double last30 = sumSalesBetween(LocalDate.now().minusDays(29), LocalDate.now());
        double previous30 = sumSalesBetween(LocalDate.now().minusDays(59), LocalDate.now().minusDays(30));

        if (previous30 <= 0d) {
            return last30 > 0d ? 100d : 0d;
        }

        return ((last30 - previous30) / previous30) * 100d;
    }

    public double getAverageBasketValue() throws SQLException {
        int orders = getOrderCountLast30Days();
        if (orders <= 0) {
            return 0d;
        }

        return getSalesLast30Days() / orders;
    }

    public Map<String, Double> getDailySalesLast7Days() throws SQLException {
        Map<String, Double> sales = new LinkedHashMap<>();
        LocalDate start = LocalDate.now().minusDays(6);
        LocalDate end = LocalDate.now();

        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DATE(date_commande) AS sale_day, COALESCE(SUM(prix_total), 0) AS total_sales "
                             + "FROM commande WHERE date_commande >= ? AND date_commande < ? "
                             + "GROUP BY DATE(date_commande) ORDER BY sale_day ASC")) {
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(start.atStartOfDay()));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(end.plusDays(1).atStartOfDay()));

            ResultSet resultSet = statement.executeQuery();
            Map<LocalDate, Double> aggregated = new LinkedHashMap<>();
            while (resultSet.next()) {
                LocalDate day = resultSet.getDate("sale_day").toLocalDate();
                aggregated.put(day, resultSet.getDouble("total_sales"));
            }

            for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
                sales.put(day.format(DateTimeFormatter.ISO_LOCAL_DATE), aggregated.getOrDefault(day, 0d));
            }
        }

        return sales;
    }

    public List<ProductPerformanceRow> getTopProducts(int limit) throws SQLException {
        return loadProductPerformance(limit, "ORDER BY sold_quantity DESC, revenue DESC");
    }

    public List<ProductPerformanceRow> getProductPerformance() throws SQLException {
        List<ProductPerformanceRow> rows = loadProductPerformance(100, "ORDER BY sold_quantity DESC, p.nom_produit ASC");
        if (!rows.isEmpty()) {
            return rows;
        }

        return buildDemoPerformanceRows();
    }

    public Map<String, Integer> getCustomerSegments() throws SQLException {
        Map<String, Integer> segments = new LinkedHashMap<>();
        segments.put("vip", 0);
        segments.put("regular", 0);
        segments.put("occasional", 0);

        try (Connection connection = requireConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT acheteur, COUNT(*) AS order_count FROM commande GROUP BY acheteur")) {

            while (resultSet.next()) {
                int orderCount = resultSet.getInt("order_count");
                if (orderCount >= 5) {
                    segments.put("vip", segments.get("vip") + 1);
                } else if (orderCount >= 2) {
                    segments.put("regular", segments.get("regular") + 1);
                } else {
                    segments.put("occasional", segments.get("occasional") + 1);
                }
            }
        }

        return segments;
    }

    public List<com.example.app.entities.Produit> getLowStockProducts(int threshold) throws SQLException {
        List<com.example.app.entities.Produit> products = produitDAO.select();
        return products.stream()
                .filter(product -> product.getQuantiteDisponible() <= threshold)
                .sorted(Comparator.comparingInt(com.example.app.entities.Produit::getQuantiteDisponible))
                .collect(Collectors.toList());
    }

    private List<ProductPerformanceRow> loadProductPerformance(int limit, String orderingClause) throws SQLException {
        List<ProductPerformanceRow> rows = new ArrayList<>();
        String sql = "SELECT p.id, p.nom_produit, p.prix, p.quantite_disponible, "
                + "COALESCE(SUM(c.quantite), 0) AS sold_quantity, "
                + "COALESCE(SUM(c.prix_total), 0) AS revenue "
                + "FROM produit p LEFT JOIN commande c ON c.produit_id = p.id "
                + "GROUP BY p.id, p.nom_produit, p.prix, p.quantite_disponible "
                + orderingClause;

        if (limit > 0) {
            sql += " LIMIT " + limit;
        }

        try (Connection connection = requireConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("nom_produit");
                int soldQuantity = resultSet.getInt("sold_quantity");
                double revenue = resultSet.getDouble("revenue");
                int currentStock = resultSet.getInt("quantite_disponible");

                String stockRisk;
                if (currentStock <= 3) {
                    stockRisk = "Critique";
                } else if (currentStock <= 10) {
                    stockRisk = "Faible";
                } else {
                    stockRisk = "Sain";
                }

                rows.add(new ProductPerformanceRow(productId, productName, soldQuantity, estimateMargin(revenue), stockRisk, currentStock));
            }
        }

        return rows;
    }

    private double estimateMargin(double revenue) {
        return Math.max(0d, Math.round((revenue * 0.25d) * 100d) / 100d);
    }

    private List<ProductPerformanceRow> buildDemoPerformanceRows() throws SQLException {
        List<com.example.app.entities.Produit> products = produitDAO.select();
        if (products.isEmpty()) {
            List<ProductPerformanceRow> demoRows = new ArrayList<>();
            demoRows.add(new ProductPerformanceRow(0, "Produit A", 0, 0d, "Sain", 24));
            demoRows.add(new ProductPerformanceRow(0, "Produit B", 0, 0d, "Faible", 8));
            demoRows.add(new ProductPerformanceRow(0, "Produit C", 0, 0d, "Critique", 2));
            return demoRows;
        }

        List<ProductPerformanceRow> demoRows = new ArrayList<>();
        for (com.example.app.entities.Produit product : products) {
            String stockRisk;
            if (product.getQuantiteDisponible() <= 3) {
                stockRisk = "Critique";
            } else if (product.getQuantiteDisponible() <= 10) {
                stockRisk = "Faible";
            } else {
                stockRisk = "Sain";
            }

            demoRows.add(new ProductPerformanceRow(
                    product.getId(),
                    product.getNom(),
                    0,
                    estimateMargin(product.getPrix()),
                    stockRisk,
                    product.getQuantiteDisponible()
            ));
        }

        return demoRows;
    }

    private double sumSalesBetween(LocalDate startInclusive, LocalDate endInclusive) throws SQLException {
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COALESCE(SUM(prix_total), 0) AS total_sales FROM commande WHERE date_commande >= ? AND date_commande < ?")) {
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(startInclusive.atStartOfDay()));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(endInclusive.plusDays(1).atStartOfDay()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("total_sales");
            }
            return 0d;
        }
    }

    private int countOrdersBetween(LocalDate startInclusive, LocalDate endInclusive) throws SQLException {
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) AS total_orders FROM commande WHERE date_commande >= ? AND date_commande < ?")) {
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(startInclusive.atStartOfDay()));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(endInclusive.plusDays(1).atStartOfDay()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total_orders");
            }
            return 0;
        }
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = MyDatabase.getConnection();
        if (connection == null) {
            throw new SQLException("Connexion MySQL indisponible pour les analytics de boutique.");
        }
        return connection;
    }
}
