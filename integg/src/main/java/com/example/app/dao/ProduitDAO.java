package com.example.app.dao;

import com.example.app.entities.Produit;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO implements IDAO<Produit> {

    private Connection connection;

    public ProduitDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Produit produit) throws SQLException {
        String sql = "INSERT INTO produit (nom_produit, description, prix, type_produit, quantite_disponible, date_ajout) VALUES (?, ?, ?, ?, ?, NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, produit.getNom());
        ps.setString(2, produit.getDescription());
        ps.setDouble(3, produit.getPrix());
        ps.setString(4, produit.getType());
        ps.setInt(5, produit.getQuantiteDisponible());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            produit.setId(rs.getInt(1));
        }
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
        String sql = "SELECT * FROM produit ORDER BY date_ajout DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Produit> findByType(String type) throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE type_produit = ? ORDER BY date_ajout DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, type);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Produit> searchProduits(String search, String type, String sortBy) throws SQLException {
        List<Produit> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM produit WHERE 1=1");

        if (search != null && !search.isEmpty()) {
            sql.append(" AND nom_produit LIKE ?");
        }

        if (type != null && !type.isEmpty()) {
            sql.append(" AND type_produit = ?");
        }

        switch (sortBy) {
            case "price_asc":
                sql.append(" ORDER BY prix ASC");
                break;
            case "price_desc":
                sql.append(" ORDER BY prix DESC");
                break;
            case "name":
                sql.append(" ORDER BY nom_produit ASC");
                break;
            case "stock":
                sql.append(" ORDER BY quantite_disponible DESC");
                break;
            default:
                sql.append(" ORDER BY date_ajout DESC");
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        int index = 1;

        if (search != null && !search.isEmpty()) {
            ps.setString(index++, "%" + search + "%");
        }
        if (type != null && !type.isEmpty()) {
            ps.setString(index++, type);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<String> getProductTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type_produit FROM produit ORDER BY type_produit ASC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            types.add(rs.getString("type_produit"));
        }
        return types;
    }

    private Produit mapResultSet(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("id"));
        produit.setNom(rs.getString("nom_produit"));
        produit.setDescription(rs.getString("description"));
        produit.setPrix(rs.getDouble("prix"));
        produit.setType(rs.getString("type_produit"));
        produit.setQuantiteDisponible(rs.getInt("quantite_disponible"));
        return produit;
    }
}