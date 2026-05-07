package com.example.app.services;

import com.example.app.entities.Personnage;
import com.example.app.entities.Universe;
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
        String sql = "INSERT INTO personnage (name, class_role, history_context, abilities_powers, strength, agility, magic, defense, universe_id, portrait_image, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, personnage.getName());
        ps.setString(2, personnage.getClassRole());
        ps.setString(3, personnage.getHistoryContext());
        ps.setString(4, personnage.getAbilitiesPowers());
        ps.setInt(5, personnage.getStrength());
        ps.setInt(6, personnage.getAgility());
        ps.setInt(7, personnage.getMagic());
        ps.setInt(8, personnage.getDefense());

        if (personnage.getUniverse() != null) {
            ps.setInt(9, personnage.getUniverse().getId());
        } else {
            ps.setNull(9, java.sql.Types.INTEGER);
        }

        if (personnage.getPortraitImage() != null) {
            ps.setBytes(10, personnage.getPortraitImage());
        } else {
            ps.setNull(10, java.sql.Types.BLOB);
        }

        if (personnage.getCreatorId() > 0) {
            ps.setInt(11, personnage.getCreatorId());
        } else {
            ps.setNull(11, java.sql.Types.INTEGER);
        }

        ps.executeUpdate();
    }

    @Override
    public void update(Personnage personnage) throws SQLException {
        String sql = "UPDATE personnage SET name = ?, class_role = ?, history_context = ?, abilities_powers = ?, strength = ?, agility = ?, magic = ?, defense = ?, portrait_image = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, personnage.getName());
        ps.setString(2, personnage.getClassRole());
        ps.setString(3, personnage.getHistoryContext());
        ps.setString(4, personnage.getAbilitiesPowers());
        ps.setInt(5, personnage.getStrength());
        ps.setInt(6, personnage.getAgility());
        ps.setInt(7, personnage.getMagic());
        ps.setInt(8, personnage.getDefense());
        
        if (personnage.getPortraitImage() != null) {
            ps.setBytes(9, personnage.getPortraitImage());
        } else {
            ps.setNull(9, java.sql.Types.BLOB);
        }
        
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
        String sql = "SELECT p.*, u.name as universe_name, u.id as u_id FROM personnage p LEFT JOIN universe u ON p.universe_id = u.id";
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
            
            String uname = rs.getString("universe_name");
            if (uname != null) {
                Universe u = new Universe();
                u.setId(rs.getInt("u_id"));
                u.setName(uname);
                p.setUniverse(u);
            }
            
            byte[] portraitBytes = rs.getBytes("portrait_image");
            if (portraitBytes != null) {
                p.setPortraitImage(portraitBytes);
            }
            
            list.add(p);
        }
        return list;
    }

    public List<Personnage> searchPersonnages(String search, List<String> classRoles, List<String> universes, String sort) throws SQLException {
        return searchPersonnages(search, classRoles, universes, sort, null);
    }

    public List<Personnage> searchPersonnages(String search, List<String> classRoles, List<String> universes, String sort, Integer creatorId) throws SQLException {
        List<Personnage> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, u.name as universe_name, u.id as u_id FROM personnage p LEFT JOIN universe u ON p.universe_id = u.id WHERE 1=1");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND p.name LIKE ?");
        }
        if (classRoles != null && !classRoles.isEmpty()) {
            sql.append(" AND p.class_role IN (");
            for (int i = 0; i < classRoles.size(); i++) {
                sql.append("?");
                if (i < classRoles.size() - 1) sql.append(",");
            }
            sql.append(")");
        }
        
        if (creatorId != null) {
            sql.append(" AND p.creator_id = ?");
        }

        if ("A-Z".equals(sort)) {
            sql.append(" ORDER BY p.name ASC");
        } else if ("Z-A".equals(sort)) {
            sql.append(" ORDER BY p.name DESC");
        } else if ("Niveau Max (Stats)".equals(sort)) {
            sql.append(" ORDER BY ((IFNULL(p.strength, 0) + IFNULL(p.agility, 0) + IFNULL(p.magic, 0) + IFNULL(p.defense, 0))) DESC");
        } else if ("Plus de Force".equals(sort)) {
            sql.append(" ORDER BY p.strength DESC");
        } else if ("Plus de Magie".equals(sort)) {
            sql.append(" ORDER BY p.magic DESC");
        } else {
            sql.append(" ORDER BY p.id DESC");
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        int paramIndex = 1;

        if (search != null && !search.trim().isEmpty()) {
            ps.setString(paramIndex++, search.trim() + "%");
        }
        if (classRoles != null && !classRoles.isEmpty()) {
            for (String role : classRoles) {
                ps.setString(paramIndex++, role);
            }
        }
        if (creatorId != null) {
            ps.setInt(paramIndex++, creatorId);
        }

        ResultSet rs = ps.executeQuery();
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
            
            try {
                p.setCreatorId(rs.getInt("creator_id"));
            } catch (SQLException e) {
                // Ignore
            }
            
            String uname = rs.getString("universe_name");
            if (uname != null) {
                Universe u = new Universe();
                u.setId(rs.getInt("u_id"));
                u.setName(uname);
                p.setUniverse(u);
            }
            
            byte[] portraitBytes = rs.getBytes("portrait_image");
            if (portraitBytes != null) {
                p.setPortraitImage(portraitBytes);
            }
            
            list.add(p);
        }
        return list;
    }

    public void savePortrait(int id, File file) throws SQLException {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String sql = "UPDATE personnage SET portrait_image = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBytes(1, bytes);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la lecture du fichier image", e);
        }
    }

    public Personnage getById(int id) throws SQLException {
        String sql = "SELECT p.*, u.name as universe_name, u.id as u_id FROM personnage p LEFT JOIN universe u ON p.universe_id = u.id WHERE p.id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
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
            
            String uname = rs.getString("universe_name");
            if (uname != null) {
                Universe u = new Universe();
                u.setId(rs.getInt("u_id"));
                u.setName(uname);
                p.setUniverse(u);
            }
            
            byte[] portraitBytes = rs.getBytes("portrait_image");
            if (portraitBytes != null) p.setPortraitImage(portraitBytes);
            
            return p;
        }
        return null;
    }
}