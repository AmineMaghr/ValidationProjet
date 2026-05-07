package com.example.app.dao;

import com.example.app.entities.ShopAutomationEvent;
import com.example.app.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShopAutomationEventDAO implements IDAO<ShopAutomationEvent> {

    private Connection connection;

    public ShopAutomationEventDAO() {
        connection = MyDatabase.getInstance().getConnection();
        try {
            ensureTable();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void add(ShopAutomationEvent event) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        String sql = "INSERT INTO shop_automation_event (event_type, description, status, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, event.getEventType());
        ps.setString(2, event.getDescription());
        ps.setString(3, event.getStatus());
        ps.setTimestamp(4, Timestamp.valueOf(event.getCreatedAt()));
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            event.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(ShopAutomationEvent event) {
        throw new UnsupportedOperationException("La mise à jour des événements d'automatisation n'est pas supportée.");
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM shop_automation_event WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<ShopAutomationEvent> select() throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        List<ShopAutomationEvent> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM shop_automation_event ORDER BY created_at DESC, id DESC");
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<ShopAutomationEvent> findLatest(int limit) throws SQLException {
        Connection conn = requireConnection();
        ensureTable();
        List<ShopAutomationEvent> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM shop_automation_event ORDER BY created_at DESC, id DESC LIMIT ?");
        ps.setInt(1, Math.max(1, limit));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private ShopAutomationEvent mapResultSet(ResultSet rs) throws SQLException {
        ShopAutomationEvent event = new ShopAutomationEvent();
        event.setId(rs.getInt("id"));
        event.setEventType(rs.getString("event_type"));
        event.setDescription(rs.getString("description"));
        event.setStatus(rs.getString("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            event.setCreatedAt(createdAt.toLocalDateTime());
        }
        return event;
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
            throw new SQLException("Connexion MySQL indisponible pour les événements d'automatisation.");
        }

        return connection;
    }

    private void ensureTable() throws SQLException {
        Connection conn = requireConnection();
        String sql = "CREATE TABLE IF NOT EXISTS shop_automation_event ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "event_type VARCHAR(50) NOT NULL,"
                + "description TEXT NOT NULL,"
                + "status VARCHAR(20) NOT NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "INDEX idx_shop_automation_type (event_type),"
                + "INDEX idx_shop_automation_status (status)"
                + ")";
        Statement st = conn.createStatement();
        st.execute(sql);
    }
}
