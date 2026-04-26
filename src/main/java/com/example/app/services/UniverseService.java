package com.example.app.services;

import com.example.app.entities.Universe;
import com.example.app.utils.MyDatabase;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UniverseService implements IService<Universe> {

    private Connection connection;

    public UniverseService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Universe universe) throws SQLException {
        String sql = "INSERT INTO universe (name, genre, short_description, story_context, themes) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, universe.getName());
        ps.setString(2, universe.getGenre());
        ps.setString(3, universe.getShortDescription());
        ps.setString(4, universe.getStoryContext());
        ps.setString(5, universe.getThemesAsString());
        ps.executeUpdate();
    }

    @Override
    public void update(Universe universe) throws SQLException {
        String sql = "UPDATE universe SET name = ?, genre = ?, short_description = ?, story_context = ?, themes = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, universe.getName());
        ps.setString(2, universe.getGenre());
        ps.setString(3, universe.getShortDescription());
        ps.setString(4, universe.getStoryContext());
        ps.setString(5, universe.getThemesAsString());
        ps.setInt(6, universe.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM universe WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Universe> select() throws SQLException {
        List<Universe> list = new ArrayList<>();
        String sql = "SELECT * FROM universe";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Universe u = new Universe();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setGenre(rs.getString("genre"));
            u.setShortDescription(rs.getString("short_description"));
            u.setStoryContext(rs.getString("story_context"));
            u.setThemesFromString(rs.getString("themes"));
            list.add(u);
        }
        return list;
    }

    public List<Universe> searchUniverses(String search, String genre, String sort) throws SQLException {
        return select();
    }

    public void saveBanner(int id, File file) throws SQLException {
        // Implémentation à faire
    }
    public List<Universe> getUniversPopulaires() throws SQLException {
        List<Universe> univers = new ArrayList<>();
        String sql = "SELECT * FROM universe ORDER BY created_at DESC LIMIT 4";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Universe u = new Universe();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setShortDescription(rs.getString("short_description"));
            u.setThemesFromString(rs.getString("themes"));
            u.setBannerBase64(rs.getString("image_url"));
            univers.add(u);
        }
        return univers;
    }
}