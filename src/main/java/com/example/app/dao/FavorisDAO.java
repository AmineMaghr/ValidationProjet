package com.example.app.dao;

import com.example.app.entities.Favoris;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavorisDAO implements IDAO<Favoris> {

    private Connection connection;

    public FavorisDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Favoris favoris) throws SQLException {
        String sql = "INSERT INTO favoris (user_id, oeuvre_id, artefact_id, created_at) VALUES (?, ?, ?, NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, favoris.getUserId());
        if (favoris.getOeuvreId() > 0) {
            ps.setInt(2, favoris.getOeuvreId());
        } else {
            ps.setNull(2, Types.INTEGER);
        }
        if (favoris.getArtefactId() > 0) {
            ps.setInt(3, favoris.getArtefactId());
        } else {
            ps.setNull(3, Types.INTEGER);
        }
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            favoris.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Favoris favoris) throws SQLException {
        // Pas de mise à jour pour les favoris
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM favoris WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Favoris> select() throws SQLException {
        List<Favoris> list = new ArrayList<>();
        String sql = "SELECT id, user_id, oeuvre_id, artefact_id, created_at FROM favoris ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public Favoris findByUserAndOeuvre(int userId, int oeuvreId) throws SQLException {
        String sql = "SELECT id, user_id, oeuvre_id, artefact_id, created_at FROM favoris WHERE user_id = ? AND oeuvre_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, oeuvreId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public Favoris findByUserAndArtefact(int userId, int artefactId) throws SQLException {
        String sql = "SELECT id, user_id, oeuvre_id, artefact_id, created_at FROM favoris WHERE user_id = ? AND artefact_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, artefactId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<Favoris> findFavoriOeuvresByUser(int userId) throws SQLException {
        List<Favoris> list = new ArrayList<>();
        String sql = "SELECT id, user_id, oeuvre_id, artefact_id, created_at FROM favoris WHERE user_id = ? AND oeuvre_id IS NOT NULL AND oeuvre_id > 0 ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Favoris> findFavoriArtefactsByUser(int userId) throws SQLException {
        List<Favoris> list = new ArrayList<>();
        String sql = "SELECT id, user_id, oeuvre_id, artefact_id, created_at FROM favoris WHERE user_id = ? AND artefact_id IS NOT NULL AND artefact_id > 0 ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public int countOeuvreLikes(int oeuvreId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM favoris WHERE oeuvre_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, oeuvreId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int countArtefactLikes(int artefactId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM favoris WHERE artefact_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, artefactId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM favoris";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // ⭐ MAPRESULSET CORRIGÉE - GÈRE LES DATES NULL
    private Favoris mapResultSet(ResultSet rs) throws SQLException {
        Favoris favoris = new Favoris();
        favoris.setId(rs.getInt("id"));
        favoris.setUserId(rs.getInt("user_id"));
        favoris.setOeuvreId(rs.getInt("oeuvre_id"));
        favoris.setArtefactId(rs.getInt("artefact_id"));
        
        // ⭐ GÉRER CORRECTEMENT LA DATE (évite l'erreur "Zero date value")
        try {
            Timestamp timestamp = rs.getTimestamp("created_at");
            if (timestamp != null) {
                favoris.setCreatedAt(timestamp.toLocalDateTime());
            } else {
                favoris.setCreatedAt(java.time.LocalDateTime.now());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture date: " + e.getMessage());
            favoris.setCreatedAt(java.time.LocalDateTime.now());
        }
        return favoris;
    }
}