package com.example.app.dao;

import com.example.app.entities.Universe;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UniverseDAO implements IDAO<Universe> {

    private Connection connection;

    public UniverseDAO() {
        connection = MyDatabase.getInstance().getConnection();
        migrateVideoUrl();
    }

    /** No-op — youtubeurl column already exists in the universe table. */
    private void migrateVideoUrl() {}

    @Override
    public void add(Universe universe) throws SQLException {
        String sql = "INSERT INTO universe (name, genre, short_description, story_context, themes, banner_image, youtubeurl, creator_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, universe.getName());
        ps.setString(2, universe.getGenre());
        ps.setString(3, universe.getShortDescription());
        ps.setString(4, universe.getStoryContext());
        ps.setString(5, universe.getThemesAsString());
        ps.setBytes(6, universe.getBannerImage());
        ps.setString(7, universe.getVideoUrl());
        ps.setInt(8, universe.getCreatorId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            universe.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Universe universe) throws SQLException {
        String sql = "UPDATE universe SET name = ?, genre = ?, short_description = ?, story_context = ?, themes = ?, banner_image = ?, youtubeurl = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, universe.getName());
        ps.setString(2, universe.getGenre());
        ps.setString(3, universe.getShortDescription());
        ps.setString(4, universe.getStoryContext());
        ps.setString(5, universe.getThemesAsString());
        ps.setBytes(6, universe.getBannerImage());
        ps.setString(7, universe.getVideoUrl());
        ps.setInt(8, universe.getId());
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
        String sql = "SELECT * FROM universe ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Universe> findFiltered(String genre, String search, String sort) throws SQLException {
        List<Universe> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM universe WHERE 1=1");

        if (genre != null && !genre.isEmpty()) {
            sql.append(" AND genre = ?");
        }

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (name LIKE ? OR short_description LIKE ?)");
        }

        switch (sort) {
            case "newest":
                sql.append(" ORDER BY created_at DESC");
                break;
            case "oldest":
                sql.append(" ORDER BY created_at ASC");
                break;
            case "name":
                sql.append(" ORDER BY name ASC");
                break;
            default:
                sql.append(" ORDER BY created_at DESC");
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        int index = 1;

        if (genre != null && !genre.isEmpty()) {
            ps.setString(index++, genre);
        }

        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            ps.setString(index++, searchPattern);
            ps.setString(index++, searchPattern);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private Universe mapResultSet(ResultSet rs) throws SQLException {
        Universe universe = new Universe();
        universe.setId(rs.getInt("id"));
        universe.setName(rs.getString("name"));
        universe.setGenre(rs.getString("genre"));
        universe.setShortDescription(rs.getString("short_description"));
        universe.setStoryContext(rs.getString("story_context"));
        universe.setThemesFromString(rs.getString("themes"));
        universe.setBannerImage(rs.getBytes("banner_image"));
        universe.setVideoUrl(rs.getString("youtubeurl"));
        
        try {
            universe.setCreatorId(rs.getInt("creator_id"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist yet
        }

        return universe;
    }
}