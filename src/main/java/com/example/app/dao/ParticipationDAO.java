package com.example.app.dao;

import com.example.app.entities.Participation;
import com.example.app.entities.Defi;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationDAO implements IDAO<Participation> {

    private Connection connection;

    public ParticipationDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Participation participation) throws SQLException {
        String sql = "INSERT INTO participation (description, date_soumission, statut, user_id, artwork_id, image_file_name, defi_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, participation.getDescription());
        ps.setTimestamp(2, Timestamp.valueOf(participation.getDateSoumission()));
        ps.setString(3, participation.getStatut());
        ps.setInt(4, participation.getUserId());
        if (participation.getArtworkId() > 0) {
            ps.setInt(5, participation.getArtworkId());
        } else {
            ps.setNull(5, Types.INTEGER);
        }
        ps.setString(6, participation.getImageFileName());
        ps.setInt(7, participation.getDefi().getId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            participation.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Participation participation) throws SQLException {
        String sql = "UPDATE participation SET description = ?, statut = ?, updated_at = NOW() WHERE id = ?";
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
        String sql = "SELECT * FROM participation ORDER BY date_soumission DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Participation> findByUserId(int userId) throws SQLException {
        List<Participation> list = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE user_id = ? ORDER BY date_soumission DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Participation> findByDefi(int defiId) throws SQLException {
        List<Participation> list = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE defi_id = ? ORDER BY date_soumission DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, defiId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Participation> findByStatut(String statut) throws SQLException {
        List<Participation> list = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE statut = ? ORDER BY date_soumission DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, statut);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public Participation findByUserAndDefi(int userId, int defiId) throws SQLException {
        String sql = "SELECT * FROM participation WHERE user_id = ? AND defi_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, defiId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public int countAcceptedByDefi(int defiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participation WHERE defi_id = ? AND statut = 'ACCEPTEE'";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, defiId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int countByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participation WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public List<Participation> searchParticipations(Integer userId) throws SQLException {
        List<Participation> list = new ArrayList<>();
        String sql;
        PreparedStatement ps;

        if (userId != null) {
            sql = "SELECT * FROM participation WHERE user_id = ? ORDER BY date_soumission DESC";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
        } else {
            sql = "SELECT * FROM participation ORDER BY date_soumission DESC";
            ps = connection.prepareStatement(sql);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private Participation mapResultSet(ResultSet rs) throws SQLException {
        Participation participation = new Participation();
        participation.setId(rs.getInt("id"));
        participation.setDescription(rs.getString("description"));
        participation.setStatut(rs.getString("statut"));
        participation.setUserId(rs.getInt("user_id"));
        participation.setArtworkId(rs.getInt("artwork_id"));
        participation.setImageFileName(rs.getString("image_file_name"));

        Timestamp dateSoumission = rs.getTimestamp("date_soumission");
        if (dateSoumission != null) {
            participation.setDateSoumission(dateSoumission.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            participation.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        Defi defi = new Defi();
        defi.setId(rs.getInt("defi_id"));
        participation.setDefi(defi);

        return participation;
    }
}