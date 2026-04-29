package com.example.app.dao;

import com.example.app.entities.Commentaire;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireDAO implements IDAO<Commentaire> {

    private Connection connection;

    public CommentaireDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Commentaire commentaire) throws SQLException {
        String sql = "INSERT INTO commentaires (contenu, created_at, user_id, oeuvre_id, artefact_id) VALUES (?, NOW(), ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, commentaire.getContenu());
        ps.setInt(2, commentaire.getUserId());
        if (commentaire.getOeuvreId() > 0) {
            ps.setInt(3, commentaire.getOeuvreId());
        } else {
            ps.setNull(3, Types.INTEGER);
        }
        if (commentaire.getArtefactId() > 0) {
            ps.setInt(4, commentaire.getArtefactId());
        } else {
            ps.setNull(4, Types.INTEGER);
        }
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            commentaire.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Commentaire commentaire) throws SQLException {
        String sql = "UPDATE commentaires SET contenu = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, commentaire.getContenu());
        ps.setInt(2, commentaire.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM commentaires WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Commentaire> select() throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaires ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Commentaire> findByOeuvre(int oeuvreId) throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaires WHERE oeuvre_id = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, oeuvreId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Commentaire> findByArtefact(int artefactId) throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaires WHERE artefact_id = ? ORDER BY created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, artefactId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Commentaire> findAllWithPagination(int page, int limit) throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaires ORDER BY created_at DESC LIMIT ? OFFSET ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, limit);
        ps.setInt(2, (page - 1) * limit);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaires";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    private Commentaire mapResultSet(ResultSet rs) throws SQLException {
        Commentaire commentaire = new Commentaire();
        commentaire.setId(rs.getInt("id"));
        commentaire.setContenu(rs.getString("contenu"));
        commentaire.setUserId(rs.getInt("user_id"));
        commentaire.setOeuvreId(rs.getInt("oeuvre_id"));
        commentaire.setArtefactId(rs.getInt("artefact_id"));
        return commentaire;
    }
}