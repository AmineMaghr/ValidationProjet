package com.example.app.services;




import com.example.app.entities.Artefact;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtefactService implements Iservice<Artefact> {

    private Connection connection;

    public ArtefactService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Artefact a) throws SQLException {

        String sql = "INSERT INTO artefact (name, type, universe, origins, powers, rarity, image_url, created_by_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, a.getName());
        ps.setString(2, a.getType());
        ps.setString(3, a.getUniverse());
        ps.setString(4, a.getOrigins());
        ps.setString(5, a.getPowers());
        ps.setString(6, a.getRarity());
        ps.setString(7, a.getImageUrl());
        ps.setInt(8, a.getCreatedById());
        ps.setTimestamp(9, Timestamp.valueOf(a.getCreatedAt()));
        ps.setTimestamp(10, Timestamp.valueOf(a.getUpdatedAt()));

        ps.executeUpdate();

        System.out.println("Artefact ajouté");
    }

    @Override
    public void update(Artefact a) throws SQLException {

        String sql = "UPDATE artefact SET name=?, type=?, universe=?, origins=?, powers=?, rarity=?, image_url=?, created_by_id=?, updated_at=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, a.getName());
        ps.setString(2, a.getType());
        ps.setString(3, a.getUniverse());
        ps.setString(4, a.getOrigins());
        ps.setString(5, a.getPowers());
        ps.setString(6, a.getRarity());
        ps.setString(7, a.getImageUrl());
        ps.setInt(8, a.getCreatedById());
        ps.setTimestamp(9, Timestamp.valueOf(a.getUpdatedAt()));
        ps.setInt(10, a.getId());

        ps.executeUpdate();

        System.out.println("Artefact modifié");
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM artefact WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();

        System.out.println("Artefact supprimé");
    }

    @Override
    public List<Artefact> select() throws SQLException {

        List<Artefact> list = new ArrayList<>();

        String sql = "SELECT * FROM artefact";

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
            a.setCreatedById(rs.getInt("created_by_id"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                a.setCreatedAt(created.toLocalDateTime());
            }

            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                a.setUpdatedAt(updated.toLocalDateTime());
            }

            list.add(a);
        }

        return list;
    }
}
