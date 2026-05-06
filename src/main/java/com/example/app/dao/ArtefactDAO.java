package com.example.app.dao;

import com.example.app.entities.Artefact;
import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtefactDAO implements IDAO<Artefact> {

    private Connection connection;

    public ArtefactDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Artefact artefact) throws SQLException {
        String sql = "INSERT INTO artefacts (name, type, universe, origins, powers, rarity, image_url, created_by_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, artefact.getName());
        ps.setString(2, artefact.getType());
        ps.setString(3, artefact.getUniverse());
        ps.setString(4, artefact.getOrigins());
        ps.setString(5, artefact.getPowers());
        ps.setString(6, artefact.getRarity());
        ps.setString(7, artefact.getImageUrl());
        if (artefact.getCreatedBy() != null) {
            ps.setInt(8, artefact.getCreatedBy().getId());
        } else {
            ps.setNull(8, java.sql.Types.INTEGER);
        }
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            artefact.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Artefact artefact) throws SQLException {
        String sql = "UPDATE artefacts SET name = ?, type = ?, universe = ?, origins = ?, powers = ?, rarity = ?, image_url = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, artefact.getName());
        ps.setString(2, artefact.getType());
        ps.setString(3, artefact.getUniverse());
        ps.setString(4, artefact.getOrigins());
        ps.setString(5, artefact.getPowers());
        ps.setString(6, artefact.getRarity());
        ps.setString(7, artefact.getImageUrl());
        ps.setInt(8, artefact.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM artefacts WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Artefact> select() throws SQLException {
        List<Artefact> list = new ArrayList<>();
        String sql = "SELECT * FROM artefacts ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public Artefact findById(int id) throws SQLException {
        String sql = "SELECT * FROM artefacts WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<Artefact> findByType(String type) throws SQLException {
        List<Artefact> list = new ArrayList<>();
        String sql = "SELECT * FROM artefacts WHERE type = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, type);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    // ⭐ MÉTHODE CORRIGÉE - Récupère le created_by_id
    private Artefact mapResultSet(ResultSet rs) throws SQLException {
        Artefact artefact = new Artefact();
        artefact.setId(rs.getInt("id"));
        artefact.setName(rs.getString("name"));
        artefact.setType(rs.getString("type"));
        artefact.setUniverse(rs.getString("universe"));
        artefact.setOrigins(rs.getString("origins"));
        artefact.setPowers(rs.getString("powers"));
        artefact.setRarity(rs.getString("rarity"));
        artefact.setImageUrl(rs.getString("image_url"));
        
        // ⭐ RÉCUPÉRER LE CRÉATEUR (CREATED_BY_ID)
        int createdById = rs.getInt("created_by_id");
        if (createdById > 0) {
            User creator = new User();
            creator.setId(createdById);
            artefact.setCreatedBy(creator);
        }
        
        if (rs.getTimestamp("created_at") != null) {
            artefact.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            artefact.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return artefact;
    }
}