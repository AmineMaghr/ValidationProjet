package com.example.app.dao;

import com.example.app.entities.Artefact;
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
        ps.setInt(8, artefact.getCreatedBy() != null ? artefact.getCreatedBy().getId() : null);
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

    public List<Artefact> findByUniverse(String universe) throws SQLException {
        List<Artefact> list = new ArrayList<>();
        String sql = "SELECT * FROM artefacts WHERE universe = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, universe);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Artefact> findByRarity(String rarity) throws SQLException {
        List<Artefact> list = new ArrayList<>();
        String sql = "SELECT * FROM artefacts WHERE rarity = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, rarity);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

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
        return artefact;
    }
}