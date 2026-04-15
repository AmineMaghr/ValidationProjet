package com.example.app.services;
import com.example.app.entities.Reponse;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements Iservice<Reponse> {

    private Connection connection;

    public ReponseService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Reponse p) throws SQLException {
        String sql = "INSERT INTO reponse " +
                "(contenu, createur_id, question_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getContenu());
        ps.setInt(2, p.getCreateur().getId());
        ps.setInt(3, p.getQuestion().getId());
        ps.setTimestamp(4, Timestamp.valueOf(p.getCreatedAt()));
        ps.setTimestamp(5, Timestamp.valueOf(p.getUpdatedAt()));
        ps.executeUpdate();
        System.out.println("Reponse ajoutée");
    }

    @Override
    public void update(Reponse p) throws SQLException {
        String sql = "UPDATE reponse SET " +
                "contenu=?, createur_id=?, question_id=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getContenu());
        ps.setInt(2, p.getCreateur().getId());
        ps.setInt(3, p.getQuestion().getId());
        ps.setTimestamp(4, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(5, p.getId());
        ps.executeUpdate();
        System.out.println("Reponse modifiée");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reponse WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Reponse supprimée");
    }

    @Override
    public List<Reponse> select() throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponse";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Reponse r = new Reponse();
            r.setId(rs.getInt("id"));
            r.setContenu(rs.getString("contenu"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                r.setCreatedAt(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                r.setUpdatedAt(updated.toLocalDateTime());
            }
            list.add(r);
        }
        return list;
    }
}
