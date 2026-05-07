package com.example.app.services;

import com.example.app.entities.Commande;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements IService<Commande> {

    private Connection connection;

    public CommandeService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Commande p) throws SQLException {
        String sql = "INSERT INTO commande (quantite, date_commande, etat, acheteur, prix_total, reference_commande, produit_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getQuantite());
        ps.setTimestamp(2, Timestamp.valueOf(p.getDateCommande()));
        ps.setString(3, p.getEtat());
        ps.setString(4, p.getAcheteur());  // ← acheteur est un String
        ps.setDouble(5, p.getPrixTotal());
        ps.setString(6, p.getReferenceCommande());
        ps.setInt(7, p.getProduit() != null ? p.getProduit().getId() : null);
        ps.executeUpdate();
    }

    @Override
    public void update(Commande p) throws SQLException {
        String sql = "UPDATE commande SET quantite=?, date_commande=?, etat=?, acheteur=?, prix_total=?, reference_commande=?, produit_id=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getQuantite());
        ps.setTimestamp(2, Timestamp.valueOf(p.getDateCommande()));
        ps.setString(3, p.getEtat());
        ps.setString(4, p.getAcheteur());
        ps.setDouble(5, p.getPrixTotal());
        ps.setString(6, p.getReferenceCommande());
        ps.setInt(7, p.getProduit() != null ? p.getProduit().getId() : null);
        ps.setInt(8, p.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM commande WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Commande> select() throws SQLException {
        List<Commande> list = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Commande c = new Commande();
            c.setId(rs.getInt("id"));
            c.setQuantite(rs.getInt("quantite"));
            c.setAcheteur(rs.getString("acheteur"));
            c.setPrixTotal(rs.getDouble("prix_total"));
            c.setReferenceCommande(rs.getString("reference_commande"));
            c.setEtat(rs.getString("etat"));
            list.add(c);
        }
        return list;
    }
}