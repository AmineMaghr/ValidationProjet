package com.example.app.services;

import com.example.app.entities.ShopAutomationEvent;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour les événements d'automatisation de la boutique
 */
public class ShopAutomationEventService implements Iservice<ShopAutomationEvent> {

    private Connection connection;

    public ShopAutomationEventService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(ShopAutomationEvent event) throws SQLException {
        String sql = "INSERT INTO shop_automation_event " +
                "(event_type, description, status, created_at) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, event.getEventType());
        ps.setString(2, event.getDescription());
        ps.setString(3, event.getStatus());
        ps.setTimestamp(4, Timestamp.valueOf(event.getCreatedAt()));

        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            event.setId(rs.getInt(1));
        }

        System.out.println("ShopAutomationEvent ajouté");
    }

    @Override
    public void update(ShopAutomationEvent event) throws SQLException {
        String sql = "UPDATE shop_automation_event SET " +
                "event_type=?, description=?, status=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, event.getEventType());
        ps.setString(2, event.getDescription());
        ps.setString(3, event.getStatus());
        ps.setInt(4, event.getId());

        ps.executeUpdate();
        System.out.println("ShopAutomationEvent modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM shop_automation_event WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("ShopAutomationEvent supprimé");
    }

    @Override
    public List<ShopAutomationEvent> select() throws SQLException {
        List<ShopAutomationEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM shop_automation_event ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            ShopAutomationEvent event = new ShopAutomationEvent();
            event.setId(rs.getInt("id"));
            event.setEventType(rs.getString("event_type"));
            event.setDescription(rs.getString("description"));
            event.setStatus(rs.getString("status"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                event.setCreatedAt(created.toLocalDateTime());
            }

            list.add(event);
        }
        return list;
    }

    /**
     * Trouve les événements par type
     */
    public List<ShopAutomationEvent> findByType(String eventType) throws SQLException {
        List<ShopAutomationEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM shop_automation_event WHERE event_type = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, eventType);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ShopAutomationEvent event = new ShopAutomationEvent();
            event.setId(rs.getInt("id"));
            event.setEventType(rs.getString("event_type"));
            event.setDescription(rs.getString("description"));
            event.setStatus(rs.getString("status"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                event.setCreatedAt(created.toLocalDateTime());
            }

            list.add(event);
        }
        return list;
    }

    /**
     * Trouve les événements par statut
     */
    public List<ShopAutomationEvent> findByStatus(String status) throws SQLException {
        List<ShopAutomationEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM shop_automation_event WHERE status = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ShopAutomationEvent event = new ShopAutomationEvent();
            event.setId(rs.getInt("id"));
            event.setEventType(rs.getString("event_type"));
            event.setDescription(rs.getString("description"));
            event.setStatus(rs.getString("status"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                event.setCreatedAt(created.toLocalDateTime());
            }

            list.add(event);
        }
        return list;
    }

    /**
     * Crée un nouvel événement d'automatisation
     */
    public void createEvent(String eventType, String description, String status) throws SQLException {
        ShopAutomationEvent event = new ShopAutomationEvent();
        event.setEventType(eventType);
        event.setDescription(description);
        event.setStatus(status);
        add(event);
    }

    /**
     * Obtient les statistiques des événements
     */
    public java.util.Map<String, Integer> getEventStats() throws SQLException {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        String sql = "SELECT event_type, COUNT(*) as count FROM shop_automation_event GROUP BY event_type";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            stats.put(rs.getString("event_type"), rs.getInt("count"));
        }
        return stats;
    }
}
