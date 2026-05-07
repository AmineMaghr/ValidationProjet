package com.example.app.services;

import com.example.app.entities.Produit;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProduitService implements IService<Produit> {

    private Connection connection;

    public ProduitService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Produit produit) throws SQLException {
        String sql = "INSERT INTO produit (nom_produit, description, prix, type_produit, quantite_disponible) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, produit.getNom());
        ps.setString(2, produit.getDescription());
        ps.setDouble(3, produit.getPrix());
        ps.setString(4, produit.getType());
        ps.setInt(5, produit.getQuantiteDisponible());
        ps.executeUpdate();
    }

    @Override
    public void update(Produit produit) throws SQLException {
        String sql = "UPDATE produit SET nom_produit = ?, description = ?, prix = ?, type_produit = ?, quantite_disponible = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, produit.getNom());
        ps.setString(2, produit.getDescription());
        ps.setDouble(3, produit.getPrix());
        ps.setString(4, produit.getType());
        ps.setInt(5, produit.getQuantiteDisponible());
        ps.setInt(6, produit.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produit WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Produit> select() throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Produit p = new Produit();
            p.setId(rs.getInt("id"));
            p.setNom(rs.getString("nom_produit"));
            p.setDescription(rs.getString("description"));
            p.setPrix(rs.getDouble("prix"));
            p.setType(rs.getString("type_produit"));
            p.setQuantiteDisponible(rs.getInt("quantite_disponible"));
            list.add(p);
        }
        return list;
    }

    public List<Produit> searchProduits(String search, String type, String sortBy) throws SQLException {
        return select();
    }

    public List<String> getProductTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type_produit FROM produit";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            types.add(rs.getString("type_produit"));
        }
        return types;
    }

    public List<Produit> findByIds(Collection<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Produit> list = new ArrayList<>();
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM produit WHERE id IN (" + placeholders + ")";
        PreparedStatement ps = connection.prepareStatement(sql);
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Produit p = new Produit();
            p.setId(rs.getInt("id"));
            p.setNom(rs.getString("nom_produit"));
            p.setDescription(rs.getString("description"));
            p.setPrix(rs.getDouble("prix"));
            p.setType(rs.getString("type_produit"));
            p.setQuantiteDisponible(rs.getInt("quantite_disponible"));
            list.add(p);
        }
        return list;
    }
}