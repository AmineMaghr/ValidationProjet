package com.example.app.dao;

import com.example.app.entities.Oeuvre;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OeuvreDAO implements IDAO<Oeuvre> {

    private Connection connection;

    public OeuvreDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        String sql = "INSERT INTO oeuvres (title, type, description, date_publication, image_url, author, created_by_id, universe_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setDate(4, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication()) : null);
        ps.setString(5, oeuvre.getImageUrl());
        ps.setString(6, oeuvre.getAuthor());
        ps.setInt(7, oeuvre.getCreateurId());
        ps.setInt(8, oeuvre.getUniverse() != null ? oeuvre.getUniverse().getId() : null);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            oeuvre.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Oeuvre oeuvre) throws SQLException {
        String sql = "UPDATE oeuvres SET title = ?, type = ?, description = ?, date_publication = ?, image_url = ?, author = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setDate(4, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication()) : null);
        ps.setString(5, oeuvre.getImageUrl());
        ps.setString(6, oeuvre.getAuthor());
        ps.setInt(7, oeuvre.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM oeuvres WHERE id = ?";
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
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Oeuvre> findByType(String type) throws SQLException {
        List<Oeuvre> list = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres WHERE type = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, type);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Oeuvre> findByAuthor(String author) throws SQLException {
        List<Oeuvre> list = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres WHERE author = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, author);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private Oeuvre mapResultSet(ResultSet rs) throws SQLException {
        Oeuvre oeuvre = new Oeuvre();
        oeuvre.setId(rs.getInt("id"));
        oeuvre.setTitle(rs.getString("title"));
        oeuvre.setType(rs.getString("type"));
        oeuvre.setDescription(rs.getString("description"));
        oeuvre.setImageUrl(rs.getString("image_url"));
        oeuvre.setAuthor(rs.getString("author"));
        oeuvre.setCreateurId(rs.getInt("created_by_id"));
        return oeuvre;
    }
}