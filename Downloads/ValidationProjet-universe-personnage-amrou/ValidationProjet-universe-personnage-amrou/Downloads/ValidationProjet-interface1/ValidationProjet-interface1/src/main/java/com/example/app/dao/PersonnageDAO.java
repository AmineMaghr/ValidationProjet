package com.example.app.dao;

import com.example.app.entities.Personnage;
import com.example.app.entities.Universe;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonnageDAO implements IDAO<Personnage> {

    private Connection connection;

    public PersonnageDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Personnage personnage) throws SQLException {
        String sql = "INSERT INTO personnage (name, class_role, history_context, abilities_powers, strength, agility, magic, defense, portrait_image, universe_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, personnage.getName());
        ps.setString(2, personnage.getClassRole());
        ps.setString(3, personnage.getHistoryContext());
        ps.setString(4, personnage.getAbilitiesPowers());
        ps.setInt(5, personnage.getStrength());
        ps.setInt(6, personnage.getAgility());
        ps.setInt(7, personnage.getMagic());
        ps.setInt(8, personnage.getDefense());
        ps.setBytes(9, personnage.getPortraitImage());
        ps.setInt(10, personnage.getUniverse() != null ? personnage.getUniverse().getId() : null);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            personnage.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Personnage personnage) throws SQLException {
        String sql = "UPDATE personnage SET name = ?, class_role = ?, history_context = ?, abilities_powers = ?, strength = ?, agility = ?, magic = ?, defense = ?, portrait_image = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, personnage.getName());
        ps.setString(2, personnage.getClassRole());
        ps.setString(3, personnage.getHistoryContext());
        ps.setString(4, personnage.getAbilitiesPowers());
        ps.setInt(5, personnage.getStrength());
        ps.setInt(6, personnage.getAgility());
        ps.setInt(7, personnage.getMagic());
        ps.setInt(8, personnage.getDefense());
        ps.setBytes(9, personnage.getPortraitImage());
        ps.setInt(10, personnage.getId());
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
        String sql = "SELECT p.*, u.name as universe_name FROM personnage p LEFT JOIN universe u ON p.universe_id = u.id ORDER BY p.created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Personnage> findFiltered(String search, List<String> universes, List<String> classRoles, String sort) throws SQLException {
        List<Personnage> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, u.name as universe_name FROM personnage p LEFT JOIN universe u ON p.universe_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (universes != null && !universes.isEmpty()) {
            sql.append(" AND u.name IN (");
            for (int i = 0; i < universes.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
                params.add(universes.get(i));
            }
            sql.append(")");
        }

        if (classRoles != null && !classRoles.isEmpty()) {
            sql.append(" AND p.class_role IN (");
            for (int i = 0; i < classRoles.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
                params.add(classRoles.get(i));
            }
            sql.append(")");
        }

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR p.history_context LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        switch (sort) {
            case "newest":
                sql.append(" ORDER BY p.created_at DESC");
                break;
            case "oldest":
                sql.append(" ORDER BY p.created_at ASC");
                break;
            case "name":
                sql.append(" ORDER BY p.name ASC");
                break;
            default:
                sql.append(" ORDER BY p.created_at DESC");
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private Personnage mapResultSet(ResultSet rs) throws SQLException {
        Personnage personnage = new Personnage();
        personnage.setId(rs.getInt("id"));
        personnage.setName(rs.getString("name"));
        personnage.setClassRole(rs.getString("class_role"));
        personnage.setHistoryContext(rs.getString("history_context"));
        personnage.setAbilitiesPowers(rs.getString("abilities_powers"));
        personnage.setStrength(rs.getInt("strength"));
        personnage.setAgility(rs.getInt("agility"));
        personnage.setMagic(rs.getInt("magic"));
        personnage.setDefense(rs.getInt("defense"));
        personnage.setPortraitImage(rs.getBytes("portrait_image"));

        Universe universe = new Universe();
        universe.setId(rs.getInt("universe_id"));
        universe.setName(rs.getString("universe_name"));
        personnage.setUniverse(universe);

        return personnage;
    }
}