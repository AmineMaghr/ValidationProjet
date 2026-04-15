package com.example.app.services;

import com.example.app.entities.Defi.*;
import com.example.app.entities.Favoris;
import com.example.app.services.*;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavorisService implements Iservice<Favoris> {

    private Connection connection;

    public FavorisService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Favoris p) throws SQLException {
        String sql = "INSERT INTO favoris " +
                "(user_id, oeuvre_id, artefact_id, created_at) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getUserId());
        if (p.getOeuvreId() != null) {
            ps.setInt(2, p.getOeuvreId());
        } else {
            ps.setNull(2, Types.INTEGER);
        }
        if (p.getArtefactId() != null) {
            ps.setInt(3, p.getArtefactId());
        } else {
            ps.setNull(3, Types.INTEGER);
        }
        ps.setTimestamp(4, Timestamp.valueOf(p.getCreatedAt()));
        ps.executeUpdate();
        System.out.println("Favoris ajouté");
    }

    @Override
    public void update(Favoris p) throws SQLException {
        String sql = "UPDATE favoris SET " +
                "user_id=?, oeuvre_id=?, artefact_id=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getUserId());
        if (p.getOeuvreId() != null) {
            ps.setInt(2, p.getOeuvreId());
        } else {
            ps.setNull(2, Types.INTEGER);
        }
        if (p.getArtefactId() != null) {
            ps.setInt(3, p.getArtefactId());
        } else {
            ps.setNull(3, Types.INTEGER);
        }
        ps.setInt(4, p.getId());
        ps.executeUpdate();
        System.out.println("Favoris modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM favoris WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Favoris supprimé");
    }

    @Override
    public List<Favoris> select() throws SQLException {
        List<Favoris> list = new ArrayList<>();
        String sql = "SELECT * FROM favoris";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Favoris f = new Favoris();
            f.setId(rs.getInt("id"));
            f.setUserId(rs.getInt("user_id"));
            int oeuvreId = rs.getInt("oeuvre_id");
            if (!rs.wasNull()) {
                f.setOeuvreId(oeuvreId);
            }
            int artefactId = rs.getInt("artefact_id");
            if (!rs.wasNull()) {
                f.setArtefactId(artefactId);
            }
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                f.setCreatedAt(created.toLocalDateTime());
            }
            list.add(f);
        }
        return list;
    }
}
