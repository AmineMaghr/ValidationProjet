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
        // ⭐ CORRECTION: users → user
        String sql = "SELECT c.*, u.username FROM commentaires c LEFT JOIN user u ON c.user_id = u.id ORDER BY c.created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Commentaire> findByOeuvre(int oeuvreId) throws SQLException {
        List<Commentaire> list = new ArrayList<>();
        // ⭐ CORRECTION: users → user
        String sql = "SELECT c.*, u.username FROM commentaires c LEFT JOIN user u ON c.user_id = u.id WHERE c.oeuvre_id = ? ORDER BY c.created_at DESC";
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
        // ⭐ CORRECTION: users → user
        String sql = "SELECT c.*, u.username FROM commentaires c LEFT JOIN user u ON c.user_id = u.id WHERE c.artefact_id = ? ORDER BY c.created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, artefactId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }
    
    public boolean isProprietaireDeLElement(int userId, int commentaireId) throws SQLException {
        String sql = "SELECT c.*, o.created_by_id as oeuvre_owner, a.created_by_id as artefact_owner " +
                     "FROM commentaires c " +
                     "LEFT JOIN oeuvres o ON c.oeuvre_id = o.id " +
                     "LEFT JOIN artefacts a ON c.artefact_id = a.id " +
                     "WHERE c.id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, commentaireId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int oeuvreOwner = rs.getInt("oeuvre_owner");
            int artefactOwner = rs.getInt("artefact_owner");
            return (oeuvreOwner == userId) || (artefactOwner == userId);
        }
        return false;
    }

    private Commentaire mapResultSet(ResultSet rs) throws SQLException {
        Commentaire commentaire = new Commentaire();
        commentaire.setId(rs.getInt("id"));
        commentaire.setContenu(rs.getString("contenu"));
        commentaire.setUserId(rs.getInt("user_id"));
        commentaire.setOeuvreId(rs.getInt("oeuvre_id"));
        commentaire.setArtefactId(rs.getInt("artefact_id"));
        commentaire.setUsername(rs.getString("username"));
        if (rs.getTimestamp("created_at") != null) {
            commentaire.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return commentaire;
    }
}