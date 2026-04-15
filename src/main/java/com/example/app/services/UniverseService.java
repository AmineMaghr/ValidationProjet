package com.example.app.services;
import com.example.app.entities.Universe;
import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UniverseService implements Iservice<Universe> {

    private Connection connection;

    public UniverseService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Universe u) throws SQLException {

        String sql = "INSERT INTO universe (name, short_description, description, themes, image_url, creator_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, u.getName());
        ps.setString(2, u.getShortDescription());
        ps.setString(3, u.getDescription());
        ps.setString(4, u.getThemesAsString()); // Convertir la liste en string
        ps.setString(5, u.getImageUrl());
        ps.setInt(6, u.getCreator() != null ? u.getCreator().getId() : 0);
        ps.setTimestamp(7, Timestamp.valueOf(u.getCreatedAt()));
        ps.setTimestamp(8, Timestamp.valueOf(u.getUpdatedAt()));

        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            u.setId(rs.getInt(1));
        }

        System.out.println("Universe ajouté");
    }

    @Override
    public void update(Universe u) throws SQLException {

        String sql = "UPDATE universe SET name=?, short_description=?, description=?, themes=?, image_url=?, creator_id=?, updated_at=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, u.getName());
        ps.setString(2, u.getShortDescription());
        ps.setString(3, u.getDescription());
        ps.setString(4, u.getThemesAsString());
        ps.setString(5, u.getImageUrl());
        ps.setInt(6, u.getCreator() != null ? u.getCreator().getId() : 0);
        ps.setTimestamp(7, Timestamp.valueOf(u.getUpdatedAt()));
        ps.setInt(8, u.getId());

        ps.executeUpdate();

        System.out.println("Universe modifié");
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM universe WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();

        System.out.println("Universe supprimé");
    }

    @Override
    public List<Universe> select() throws SQLException {

        List<Universe> list = new ArrayList<>();

        String sql = "SELECT * FROM universe";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            Universe u = new Universe();

            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setShortDescription(rs.getString("short_description"));
            u.setDescription(rs.getString("description"));
            u.setThemesFromString(rs.getString("themes")); // Convertir string en liste
            u.setImageUrl(rs.getString("image_url"));

            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                u.setCreatedAt(created.toLocalDateTime());
            }

            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                u.setUpdatedAt(updated.toLocalDateTime());
            }

            // Creator
            User user = new User();
            user.setId(rs.getInt("creator_id"));
            u.setCreator(user);

            list.add(u);
        }

        return list;
    }
}
