package com.example.app.dao;

import com.example.app.entities.Defi;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DefiDAO implements IDAO<Defi> {

    private Connection connection;

    public DefiDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Defi defi) throws SQLException {
        String sql = "INSERT INTO defi (titre, description, theme, difficulte, image_cover, date_debut, date_fin, date_limite, statut, createur_id, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, defi.getTitre());
        ps.setString(2, defi.getDescription());
        ps.setString(3, defi.getTheme());
        ps.setString(4, defi.getDifficulte());
        ps.setString(5, defi.getImageCover());
        ps.setDate(6, Date.valueOf(defi.getDateDebut()));
        ps.setDate(7, Date.valueOf(defi.getDateFin()));
        ps.setDate(8, defi.getDateLimite() != null ? Date.valueOf(defi.getDateLimite()) : null);
        ps.setString(9, defi.getStatut());
        ps.setInt(10, defi.getCreateurId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            defi.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Defi defi) throws SQLException {
        String sql = "UPDATE defi SET titre = ?, description = ?, theme = ?, difficulte = ?, image_cover = ?, date_debut = ?, date_fin = ?, date_limite = ?, statut = ?, updated_at = NOW() WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, defi.getTitre());
        ps.setString(2, defi.getDescription());
        ps.setString(3, defi.getTheme());
        ps.setString(4, defi.getDifficulte());
        ps.setString(5, defi.getImageCover());
        ps.setDate(6, Date.valueOf(defi.getDateDebut()));
        ps.setDate(7, Date.valueOf(defi.getDateFin()));
        ps.setDate(8, defi.getDateLimite() != null ? Date.valueOf(defi.getDateLimite()) : null);
        ps.setString(9, defi.getStatut());
        ps.setInt(10, defi.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM defi WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Defi> select() throws SQLException {
        List<Defi> list = new ArrayList<>();
        String sql = "SELECT * FROM defi ORDER BY date_debut DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Defi> findOuverts() throws SQLException {
        List<Defi> list = new ArrayList<>();
        String sql = "SELECT * FROM defi WHERE statut = 'OUVERT' ORDER BY date_debut DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Defi> findByTheme(String theme) throws SQLException {
        List<Defi> list = new ArrayList<>();
        String sql = "SELECT * FROM defi WHERE theme = ? ORDER BY date_debut DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, theme);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Defi> findActifs() throws SQLException {
        List<Defi> list = new ArrayList<>();
        String sql = "SELECT * FROM defi WHERE date_fin >= CURDATE() ORDER BY date_debut DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public Defi findWithParticipations(int id) throws SQLException {
        String sql = "SELECT * FROM defi WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public int countByStatut(String statut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM defi WHERE statut = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, statut);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public List<Defi> searchDefis(String search, String sortBy) throws SQLException {
        List<Defi> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM defi WHERE 1=1");

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (titre LIKE ? OR theme LIKE ?)");
        }

        switch (sortBy) {
            case "titre":
                sql.append(" ORDER BY titre ASC");
                break;
            case "theme":
                sql.append(" ORDER BY theme ASC");
                break;
            case "ancien":
                sql.append(" ORDER BY date_debut ASC");
                break;
            default:
                sql.append(" ORDER BY date_debut DESC");
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

     private Defi mapResultSet(ResultSet rs) throws SQLException {
         Defi defi = new Defi();
         defi.setId(rs.getInt("id"));
         defi.setTitre(rs.getString("titre"));
         defi.setDescription(rs.getString("description"));
         defi.setTheme(rs.getString("theme"));
         defi.setImageCover(rs.getString("image_cover"));

         Date dateDebut = rs.getDate("date_debut");
         if (dateDebut != null) defi.setDateDebut(dateDebut.toLocalDate());

         Date dateFin = rs.getDate("date_fin");
         if (dateFin != null) defi.setDateFin(dateFin.toLocalDate());

         Date dateLimite = rs.getDate("date_limite");
         if (dateLimite != null) defi.setDateLimite(dateLimite.toLocalDate());

         defi.setStatut(rs.getString("statut"));
         defi.setCreateurId(rs.getInt("createur_id"));
         defi.setDifficulte(rs.getString("difficulte"));
         return defi;
     }
}