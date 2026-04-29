package com.example.app.services;

import com.example.app.entities.Commentaire;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements IService<Commentaire> {

    private Connection connection;

    public CommentaireService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Commentaire commentaire) throws SQLException {
        String sql = "INSERT INTO commentaires (contenu, user_id, oeuvre_id, artefact_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, commentaire.getContenu());
        ps.setInt(2, commentaire.getUserId());
        if (commentaire.getOeuvreId() > 0) ps.setInt(3, commentaire.getOeuvreId());
        else ps.setNull(3, Types.INTEGER);
        if (commentaire.getArtefactId() > 0) ps.setInt(4, commentaire.getArtefactId());
        else ps.setNull(4, Types.INTEGER);
        ps.executeUpdate();
    }

    @Override
    public void update(Commentaire commentaire) throws SQLException {
        String sql = "UPDATE commentaires SET contenu = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, commentaire.getContenu());
        ps.setInt(2, commentaire.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM commentaires WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Commentaire> select() throws SQLException {
        return new ArrayList<>();
    }

    public List<Commentaire> findByOeuvre(int oeuvreId) throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaires WHERE oeuvre_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, oeuvreId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Commentaire c = new Commentaire();
            c.setId(rs.getInt("id"));
            c.setContenu(rs.getString("contenu"));
            c.setUserId(rs.getInt("user_id"));
            list.add(c);
        }
        return list;
    }

    public List<Commentaire> findByArtefact(int artefactId) throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaires WHERE artefact_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, artefactId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Commentaire c = new Commentaire();
            c.setId(rs.getInt("id"));
            c.setContenu(rs.getString("contenu"));
            c.setUserId(rs.getInt("user_id"));
            list.add(c);
        }
        return list;
    }
}