package com.example.app.services;

import com.example.app.entities.Participation;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService implements IService<Participation> {

    private Connection connection;

    public ParticipationService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Participation participation) throws SQLException {
        String sql = "INSERT INTO participation (description, statut, user_id, artwork_id, defi_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, participation.getDescription());
        ps.setString(2, participation.getStatut());
        ps.setInt(3, participation.getUserId());
        ps.setInt(4, participation.getArtworkId());
        ps.setInt(5, participation.getDefi().getId());
        ps.executeUpdate();
    }

    @Override
    public void update(Participation participation) throws SQLException {
        String sql = "UPDATE participation SET description = ?, statut = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, participation.getDescription());
        ps.setString(2, participation.getStatut());
        ps.setInt(3, participation.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM participation WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Participation> select() throws SQLException {
        List<Participation> list = new ArrayList<>();
        String sql = "SELECT * FROM participation";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Participation p = new Participation();
            p.setId(rs.getInt("id"));
            p.setDescription(rs.getString("description"));
            p.setStatut(rs.getString("statut"));
            p.setUserId(rs.getInt("user_id"));
            p.setArtworkId(rs.getInt("artwork_id"));
            list.add(p);
        }
        return list;
    }
    public boolean hasUserParticipated(int defiId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participation WHERE defi_id = ? AND user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, defiId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}