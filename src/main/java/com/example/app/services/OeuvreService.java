package com.example.app.services;

import com.example.app.entities.Oeuvre;
import com.example.app.utils.MyDatabase;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OeuvreService implements IService<Oeuvre> {

    private Connection connection;

    public OeuvreService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        String sql = "INSERT INTO oeuvres (title, type, description, image_url, author) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setString(4, oeuvre.getImageUrl());
        ps.setString(5, oeuvre.getAuthor());
        ps.executeUpdate();
    }

    @Override
    public void update(Oeuvre oeuvre) throws SQLException {
        String sql = "UPDATE oeuvres SET title = ?, type = ?, description = ?, image_url = ?, author = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, oeuvre.getTitle());
        ps.setString(2, oeuvre.getType());
        ps.setString(3, oeuvre.getDescription());
        ps.setString(4, oeuvre.getImageUrl());
        ps.setString(5, oeuvre.getAuthor());
        ps.setInt(6, oeuvre.getId());
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
        String sql = "SELECT * FROM oeuvres";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Oeuvre o = new Oeuvre();
            o.setId(rs.getInt("id"));
            o.setTitle(rs.getString("title"));
            o.setType(rs.getString("type"));
            o.setDescription(rs.getString("description"));
            o.setImageUrl(rs.getString("image_url"));
            o.setAuthor(rs.getString("author"));
            list.add(o);
        }
        return list;
    }

    public List<Oeuvre> searchOeuvres(String search, String type, Integer userId) throws SQLException {
        return select();
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM oeuvres";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            types.add(rs.getString("type"));
        }
        return types;
    }

    public void saveImage(int id, File file) throws SQLException {
        // Implémentation à faire
    }
    public List<Oeuvre> getCreationsRecentes() throws SQLException {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres ORDER BY created_at DESC LIMIT 4";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Oeuvre o = new Oeuvre();
            o.setId(rs.getInt("id"));
            o.setTitle(rs.getString("title"));
            o.setDescription(rs.getString("description"));
            o.setImageUrl(rs.getString("image_url"));
            o.setAuthor(rs.getString("author"));
            o.setType(rs.getString("type"));
            oeuvres.add(o);
        }
        return oeuvres;
    }
}