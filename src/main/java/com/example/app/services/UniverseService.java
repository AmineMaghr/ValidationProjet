package com.example.app.services;

import com.example.app.entities.Universe;
import com.example.app.utils.MyDatabase;
import java.io.File;
import java.nio.file.Files;
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
        String sql = "INSERT INTO universe (name, genre, short_description, story_context, themes, banner_image) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, universe.getName());
        ps.setString(2, universe.getGenre());
        ps.setString(3, universe.getShortDescription());
        ps.setString(4, universe.getStoryContext());
        ps.setString(5, universe.getThemesAsString());
        if (universe.getBannerImage() != null) {
            ps.setBytes(6, universe.getBannerImage());
        } else {
            ps.setNull(6, java.sql.Types.BLOB);
        }
        ps.executeUpdate();
    }

    @Override
    public void update(Universe universe) throws SQLException {
        String sql = "UPDATE universe SET name = ?, genre = ?, short_description = ?, story_context = ?, themes = ?, banner_image = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, universe.getName());
        ps.setString(2, universe.getGenre());
        ps.setString(3, universe.getShortDescription());
        ps.setString(4, universe.getStoryContext());
        ps.setString(5, universe.getThemesAsString());
        if (universe.getBannerImage() != null) {
            ps.setBytes(6, universe.getBannerImage());
        } else {
            ps.setNull(6, java.sql.Types.BLOB);
        }
        ps.setInt(7, universe.getId());
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
            
            byte[] bannerBytes = rs.getBytes("banner_image");
            if (bannerBytes != null) {
                u.setBannerImage(bannerBytes);
            }
            
            list.add(u);
        }
        return list;
    }

    public List<Universe> searchUniverses(String search, String genre, String sort) throws SQLException {
        List<Universe> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM universe WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add(search.trim() + "%");
        }
        
        if (genre != null && !genre.trim().isEmpty() && !genre.equals("Tout")) {
            sql.append(" AND genre = ?");
            params.add(genre.trim());
        }

        if ("A-Z".equals(sort)) {
            sql.append(" ORDER BY name ASC");
        } else if ("Z-A".equals(sort)) {
            sql.append(" ORDER BY name DESC");
        } else {
            sql.append(" ORDER BY id DESC");
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Universe u = new Universe();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setGenre(rs.getString("genre"));
            u.setStoryContext(rs.getString("story_context"));
            u.setShortDescription(rs.getString("short_description"));
            u.setThemesFromString(rs.getString("themes"));
            
            byte[] bannerBytes = rs.getBytes("banner_image");
            if (bannerBytes != null) {
                u.setBannerImage(bannerBytes);
            }
            list.add(u);
        }
        return list;
    }

    public void saveBanner(int id, File file) throws SQLException {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String sql = "UPDATE universe SET banner_image = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBytes(1, bytes);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la lecture du fichier image", e);
        }
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
            
            byte[] bannerBytes = rs.getBytes("banner_image");
            if (bannerBytes != null) {
                u.setBannerImage(bannerBytes);
            }
            
            univers.add(u);
        }
        return univers;
    }
}