package com.example.app.services;

import com.example.app.entities.Personnage;
import com.example.app.utils.MyDatabase;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonnageService implements IService<Personnage> {

    private Connection connection;

    public PersonnageService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Personnage personnage) throws SQLException {
        String sql = "INSERT INTO personnage (name, class_role, history_context, abilities_powers, strength, agility, magic, defense, universe_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, personnage.getName());
        ps.setString(2, personnage.getClassRole());
        ps.setString(3, personnage.getHistoryContext());
        ps.setString(4, personnage.getAbilitiesPowers());
        ps.setInt(5, personnage.getStrength());
        ps.setInt(6, personnage.getAgility());
        ps.setInt(7, personnage.getMagic());
        ps.setInt(8, personnage.getDefense());
        ps.setInt(9, personnage.getUniverse() != null ? personnage.getUniverse().getId() : null);
        ps.executeUpdate();
    }

    @Override
    public void update(Personnage personnage) throws SQLException {
        String sql = "UPDATE personnage SET name = ?, class_role = ?, history_context = ?, abilities_powers = ?, strength = ?, agility = ?, magic = ?, defense = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, personnage.getName());
        ps.setString(2, personnage.getClassRole());
        ps.setString(3, personnage.getHistoryContext());
        ps.setString(4, personnage.getAbilitiesPowers());
        ps.setInt(5, personnage.getStrength());
        ps.setInt(6, personnage.getAgility());
        ps.setInt(7, personnage.getMagic());
        ps.setInt(8, personnage.getDefense());
        ps.setInt(9, personnage.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM personnage WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Personnage> select() throws SQLException {
        List<Personnage> list = new ArrayList<>();
        String sql = "SELECT * FROM personnage";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Personnage p = new Personnage();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setClassRole(rs.getString("class_role"));
            p.setHistoryContext(rs.getString("history_context"));
            p.setAbilitiesPowers(rs.getString("abilities_powers"));
            p.setStrength(rs.getInt("strength"));
            p.setAgility(rs.getInt("agility"));
            p.setMagic(rs.getInt("magic"));
            p.setDefense(rs.getInt("defense"));
            list.add(p);
        }
        return list;
    }

    public List<Personnage> searchPersonnages(String search, List<String> classRoles, List<String> universes, String sort) throws SQLException {
        return select();
    }

    public void savePortrait(int id, File file) throws SQLException {
        // Implémentation à faire
    }
}