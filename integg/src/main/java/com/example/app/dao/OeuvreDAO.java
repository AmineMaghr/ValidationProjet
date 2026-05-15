package com.example.app.dao;

import com.example.app.entities.Oeuvre;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OeuvreDAO implements IDAO<Oeuvre> {

    private Connection connection;

    public OeuvreDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        String sql = "INSERT INTO oeuvres (title, type, description, image_url, local_path, web_url, author, created_by_id, date_publication, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setString(4, oeuvre.getImageUrl());
        ps.setString(5, oeuvre.getLocalPath());
        ps.setString(6, oeuvre.getWebUrl());
        ps.setString(7, oeuvre.getAuthor());
        ps.setInt(8, oeuvre.getCreateurId());  // createurId correspond à created_by_id
        ps.setDate(9, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication()) : null);
        ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
        ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
        
        ps.executeUpdate();
        
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            oeuvre.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Oeuvre oeuvre) throws SQLException {
        String sql = "UPDATE oeuvres SET title=?, type=?, description=?, image_url=?, local_path=?, web_url=?, author=?, created_by_id=?, date_publication=?, updated_at=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setString(4, oeuvre.getImageUrl());
        ps.setString(5, oeuvre.getLocalPath());
        ps.setString(6, oeuvre.getWebUrl());
        ps.setString(7, oeuvre.getAuthor());
        ps.setInt(8, oeuvre.getCreateurId());
        ps.setDate(9, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication()) : null);
        ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(11, oeuvre.getId());
        
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM oeuvres WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Oeuvre> select() throws SQLException {
        List<Oeuvre> list = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            Oeuvre oeuvre = mapResultSet(rs);
            list.add(oeuvre);
        }
        return list;
    }

    public Oeuvre findById(int id) throws SQLException {
        String sql = "SELECT * FROM oeuvres WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<Oeuvre> searchOeuvres(String search, String type, Integer userId) throws SQLException {
        List<Oeuvre> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM oeuvres WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (search != null && !search.isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + search + "%");
        }
        
        if (type != null && !type.isEmpty() && !type.equals("Tous") && !type.equals("Tous les types")) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        
        if (userId != null && userId > 0) {
            sql.append(" AND created_by_id = ?");  // ← Changé: createur_id → created_by_id
            params.add(userId);
        }
        
        sql.append(" ORDER BY created_at DESC");
        
        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM oeuvres WHERE type IS NOT NULL AND type != '' ORDER BY type";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            types.add(rs.getString("type"));
        }
        return types;
    }

    private Oeuvre mapResultSet(ResultSet rs) throws SQLException {
        Oeuvre oeuvre = new Oeuvre();
        oeuvre.setId(rs.getInt("id"));
        oeuvre.setTitle(rs.getString("title"));
        oeuvre.setType(rs.getString("type"));
        oeuvre.setDescription(rs.getString("description"));
        oeuvre.setImageUrl(rs.getString("image_url"));
        oeuvre.setLocalPath(rs.getString("local_path"));
        oeuvre.setWebUrl(rs.getString("web_url"));
        oeuvre.setAuthor(rs.getString("author"));
        oeuvre.setCreateurId(rs.getInt("created_by_id"));  // ← Changé: createur_id → created_by_id
        
        Date datePub = rs.getDate("date_publication");
        if (datePub != null) {
            oeuvre.setDatePublication(datePub.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            oeuvre.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            oeuvre.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return oeuvre;
    }
}