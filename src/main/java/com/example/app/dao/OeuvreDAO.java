package com.example.app.dao;

import com.example.app.entities.Oeuvre;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OeuvreDAO implements IDAO<Oeuvre> {

    private Connection connection;

    public OeuvreDAO() {
        connection = MyDatabase.getConnection();
        if (connection == null) {
            System.err.println("❌ ERREUR: Connexion NULL dans OeuvreDAO");
        }
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        // ✅ CORRECTION: oeuvre → oeuvres
        String sql = "INSERT INTO oeuvres (title, type, description, date_publication, image_url, author, created_by_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, oeuvre.getTitle());
            ps.setString(2, oeuvre.getType());
            ps.setString(3, oeuvre.getDescription());
            ps.setDate(4, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication())
                    : Date.valueOf(java.time.LocalDate.now()));
            ps.setString(5, oeuvre.getImageUrl());
            ps.setString(6, oeuvre.getAuthor());
            ps.setInt(7, oeuvre.getCreateurId() > 0 ? oeuvre.getCreateurId() : 1);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    oeuvre.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Oeuvre oeuvre) throws SQLException {
        // ✅ CORRECTION: oeuvre → oeuvres
        String sql = "UPDATE oeuvres SET title = ?, type = ?, description = ?, date_publication = ?, image_url = ?, author = ?, updated_at = NOW() WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, oeuvre.getTitle());
            ps.setString(2, oeuvre.getType());
            ps.setString(3, oeuvre.getDescription());
            ps.setDate(4, oeuvre.getDatePublication() != null ? Date.valueOf(oeuvre.getDatePublication()) : null);
            ps.setString(5, oeuvre.getImageUrl());
            ps.setString(6, oeuvre.getAuthor());
            ps.setInt(7, oeuvre.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // ✅ CORRECTION: oeuvre → oeuvres
        String sql = "DELETE FROM oeuvres WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Oeuvre> select() throws SQLException {
        List<Oeuvre> list = new ArrayList<>();
        // ✅ CORRECTION: oeuvre → oeuvres
        String sql = "SELECT * FROM oeuvres ORDER BY created_at DESC";

        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public Oeuvre findById(int id) throws SQLException {
        // ✅ CORRECTION: oeuvre → oeuvres
        String sql = "SELECT * FROM oeuvres WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    public List<Oeuvre> searchOeuvres(String search, String type, Integer userId) throws SQLException {
        List<Oeuvre> list = new ArrayList<>();
        // ✅ CORRECTION: oeuvre → oeuvres
        StringBuilder sql = new StringBuilder("SELECT * FROM oeuvres WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (title LIKE ? OR author LIKE ?)");
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        if (type != null && !type.isEmpty() && !type.equals("Tous") && !type.equals("null")) {
            sql.append(" AND type = ?");
            params.add(type);
        }

        if (userId != null && userId > 0) {
            sql.append(" AND created_by_id = ?");
            params.add(userId);
        }

        sql.append(" ORDER BY created_at DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        // ✅ CORRECTION: oeuvre → oeuvres
        String sql = "SELECT DISTINCT type FROM oeuvres WHERE type IS NOT NULL AND type != ''";

        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        }
        return types;
    }

    private Oeuvre mapResultSet(ResultSet rs) throws SQLException {
        Oeuvre oeuvre = new Oeuvre();
        oeuvre.setId(rs.getInt("id"));
        oeuvre.setTitle(rs.getString("title"));
        oeuvre.setType(rs.getString("type"));
        oeuvre.setDescription(rs.getString("description"));
        oeuvre.setImageUrl(rs.getString("image_url"));
        oeuvre.setAuthor(rs.getString("author"));

        try {
            int createdById = rs.getInt("created_by_id");
            if (!rs.wasNull()) {
                oeuvre.setCreateurId(createdById);
            } else {
                oeuvre.setCreateurId(1);
            }
        } catch (SQLException e) {
            oeuvre.setCreateurId(1);
        }

        Date datePub = rs.getDate("date_publication");
        if (datePub != null) {
            oeuvre.setDatePublication(datePub.toLocalDate());
        } else {
            oeuvre.setDatePublication(java.time.LocalDate.now());
        }

        return oeuvre;
    }
}