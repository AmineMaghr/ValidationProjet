package com.example.app.services;

import com.example.app.entities.Oeuvre;
import com.example.app.utils.MyDatabase;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OeuvreService implements IService<Oeuvre> {

    private final Connection connection;

    public OeuvreService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        String sql = "INSERT INTO oeuvres (title, type, description, author, date_publication, image_url, created_by_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setString(4, oeuvre.getAuthor());
        ps.setDate(5,
                Date.valueOf(oeuvre.getDatePublication() != null ? oeuvre.getDatePublication() : LocalDate.now()));
        ps.setString(6, oeuvre.getImageUrl());
        ps.setInt(7, oeuvre.getCreateurId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            oeuvre.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Oeuvre oeuvre) throws SQLException {
        String sql = "UPDATE oeuvres SET title = ?, type = ?, description = ?, author = ?, date_publication = ?, image_url = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setString(4, oeuvre.getAuthor());
        ps.setDate(5, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication())
                : Date.valueOf(LocalDate.now()));
        ps.setString(6, oeuvre.getImageUrl());
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

    public Oeuvre selectById(int id) throws SQLException {
        String sql = "SELECT * FROM oeuvres WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<Oeuvre> searchOeuvres(String search, String type, Integer userId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM oeuvres WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + search.toLowerCase().trim() + "%");
        }
        if (type != null && !type.isBlank()) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (userId != null) {
            sql.append(" AND created_by_id = ?");
            params.add(userId);
        }
        sql.append(" ORDER BY created_at DESC");

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        List<Oeuvre> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapResultSet(rs));
        }
        return results;
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM oeuvres WHERE type IS NOT NULL ORDER BY type";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            types.add(rs.getString("type"));
        }
        return types;
    }

    public void saveImage(int id, File file) throws SQLException {
        if (file == null || id <= 0) {
            return;
        }
        String imageUrl = file.toURI().toString();
        String sql = "UPDATE oeuvres SET image_url = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, imageUrl);
        ps.setInt(2, id);
        ps.executeUpdate();
    }

    public List<Oeuvre> getCreationsRecentes() throws SQLException {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres ORDER BY created_at DESC LIMIT 4";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            oeuvres.add(mapResultSet(rs));
        }
        return oeuvres;
    }

    private Oeuvre mapResultSet(ResultSet rs) throws SQLException {
        Oeuvre o = new Oeuvre();
        o.setId(rs.getInt("id"));
        o.setTitle(rs.getString("title"));
        o.setType(rs.getString("type"));
        o.setDescription(rs.getString("description"));
        o.setImageUrl(rs.getString("image_url"));
        o.setAuthor(rs.getString("author"));
        Date datePublication = rs.getDate("date_publication");
        if (datePublication != null) {
            o.setDatePublication(datePublication.toLocalDate());
        }
        o.setCreateurId(rs.getInt("created_by_id"));
        return o;
    }
}
