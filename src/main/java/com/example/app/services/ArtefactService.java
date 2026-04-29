package com.example.app.services;

import com.example.app.entities.Artefact;
import com.example.app.utils.MyDatabase;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtefactService implements IService<Artefact> {

    private Connection connection;

    public ArtefactService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Artefact artefact) throws SQLException {
        String sql = "INSERT INTO artefacts (name, type, universe, origins, powers, rarity, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, artefact.getName());
        ps.setString(2, artefact.getType());
        ps.setString(3, artefact.getUniverse());
        ps.setString(4, artefact.getOrigins());
        ps.setString(5, artefact.getPowers());
        ps.setString(6, artefact.getRarity());
        ps.setString(7, artefact.getImageUrl());
        ps.executeUpdate();
    }

    @Override
    public void update(Artefact artefact) throws SQLException {
        String sql = "UPDATE artefacts SET name = ?, type = ?, universe = ?, origins = ?, powers = ?, rarity = ?, image_url = ? WHERE id = ?";
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
        String sql = "SELECT * FROM artefacts";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Artefact a = new Artefact();
            a.setId(rs.getInt("id"));
            a.setName(rs.getString("name"));
            a.setType(rs.getString("type"));
            a.setUniverse(rs.getString("universe"));
            a.setOrigins(rs.getString("origins"));
            a.setPowers(rs.getString("powers"));
            a.setRarity(rs.getString("rarity"));
            a.setImageUrl(rs.getString("image_url"));
            list.add(a);
        }
        return list;
    }

    public List<Artefact> searchArtefacts(String search, String type, Integer userId) throws SQLException {
        return select();
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM artefacts";
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
}