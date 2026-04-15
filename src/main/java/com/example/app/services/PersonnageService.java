package com.example.app.services  ;

import com.example.app.entities.Personnage;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonnageService implements Iservice<Personnage> {

    private Connection connection;

    public PersonnageService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Personnage p) throws SQLException {
        String sql = "INSERT INTO personnage " +
                "(nom, univers, description, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getNom());
        ps.setString(2, p.getUnivers());
        ps.setString(3, p.getDescription());
        ps.setTimestamp(4, Timestamp.valueOf(p.getCreatedAt()));
        ps.setTimestamp(5, Timestamp.valueOf(p.getUpdatedAt()));
        ps.executeUpdate();
        System.out.println("Personnage ajouté");
    }

    @Override
    public void update(Personnage p) throws SQLException {
        String sql = "UPDATE personnage SET " +
                "nom=?, univers=?, description=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getNom());
        ps.setString(2, p.getUnivers());
        ps.setString(3, p.getDescription());
        ps.setTimestamp(4, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(5, p.getId());
        ps.executeUpdate();
        System.out.println("Personnage modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM personnage WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Personnage supprimé");
    }

    @Override
    public List<Personnage> select() throws SQLException {
        List<Personnage> list = new ArrayList<>();
        String sql = "SELECT * FROM personnage";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Personnage pers = new Personnage();
            pers.setId(rs.getInt("id"));
            pers.setNom(rs.getString("nom"));
            pers.setUnivers(rs.getString("univers"));
            pers.setDescription(rs.getString("description"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                pers.setCreatedAt(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                pers.setUpdatedAt(updated.toLocalDateTime());
            }
            list.add(pers);
        }
        return list;
    }
}
