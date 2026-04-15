package com.example.app.services;



import com.example.app.entities.Commentaire;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements Iservice<Commentaire> {

    private Connection connection;

    public CommentaireService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Commentaire p) throws SQLException {
        String sql = "INSERT INTO commentaire " +
                "(contenu, created_at, user_id, oeuvre_id, artefact_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getContenu());
        ps.setTimestamp(2, Timestamp.valueOf(p.getCreatedAt()));
        ps.setInt(3, p.getUserId());
        if (p.getOeuvreId() != null) {
            ps.setInt(4, p.getOeuvreId());
        } else {
            ps.setNull(4, Types.INTEGER);
        }
        if (p.getArtefactId() != null) {
            ps.setInt(5, p.getArtefactId());
        } else {
            ps.setNull(5, Types.INTEGER);
        }
        ps.executeUpdate();
        System.out.println("Commentaire ajouté");
    }

    @Override
    public void update(Commentaire p) throws SQLException {
        String sql = "UPDATE commentaire SET " +
                "contenu=?, user_id=?, oeuvre_id=?, artefact_id=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getContenu());
        ps.setInt(2, p.getUserId());
        if (p.getOeuvreId() != null) {
            ps.setInt(3, p.getOeuvreId());
        } else {
            ps.setNull(3, Types.INTEGER);
        }
        if (p.getArtefactId() != null) {
            ps.setInt(4, p.getArtefactId());
        } else {
            ps.setNull(4, Types.INTEGER);
        }
        ps.setInt(5, p.getId());
        ps.executeUpdate();
        System.out.println("Commentaire modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM commentaire WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Commentaire supprimé");
    }

    @Override
    public List<Commentaire> select() throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaire";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Commentaire c = new Commentaire();
            c.setId(rs.getInt("id"));
            c.setContenu(rs.getString("contenu"));
            c.setUserId(rs.getInt("user_id"));
            int oeuvreId = rs.getInt("oeuvre_id");
            if (!rs.wasNull()) {
                c.setOeuvreId(oeuvreId);
            }
            int artefactId = rs.getInt("artefact_id");
            if (!rs.wasNull()) {
                c.setArtefactId(artefactId);
            }
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                c.setCreatedAt(created.toLocalDateTime());
            }
            list.add(c);
        }
        return list;
    }
}
