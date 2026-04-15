package com.example.app.services;
import com.example.app.entities.Produit;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements Iservice<Produit> {

    private Connection connection;

    public ProduitService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Produit p) throws SQLException {
        String sql = "INSERT INTO produit " +
                "(nom, description, prix, image_url, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getNom());
        ps.setString(2, p.getDescription());
        ps.setDouble(3, p.getPrix());
        ps.setString(4, p.getImageUrl());
        ps.setTimestamp(5, Timestamp.valueOf(p.getCreatedAt()));
        ps.setTimestamp(6, Timestamp.valueOf(p.getUpdatedAt()));
        ps.executeUpdate();
        System.out.println("Produit ajouté");
    }

    @Override
    public void update(Produit p) throws SQLException {
        String sql = "UPDATE produit SET " +
                "nom=?, description=?, prix=?, image_url=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getNom());
        ps.setString(2, p.getDescription());
        ps.setDouble(3, p.getPrix());
        ps.setString(4, p.getImageUrl());
        ps.setTimestamp(5, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(6, p.getId());
        ps.executeUpdate();
        System.out.println("Produit modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produit WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Produit supprimé");
    }

    @Override
    public List<Produit> select() throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Produit prod = new Produit();
            prod.setId(rs.getInt("id"));
            prod.setNom(rs.getString("nom"));
            prod.setDescription(rs.getString("description"));
            prod.setPrix(rs.getDouble("prix"));
            prod.setImageUrl(rs.getString("image_url"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                prod.setCreatedAt(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                prod.setUpdatedAt(updated.toLocalDateTime());
            }
            list.add(prod);
        }
        return list;
    }
}
