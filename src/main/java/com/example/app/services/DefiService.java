package com.example.app.services;

import com.example.app.entities.Defi;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DefiService implements IService<Defi> {

    private Connection connection;

    public DefiService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Defi defi) throws SQLException {
        String sql = "INSERT INTO defi (titre, description, theme, image_cover, date_debut, date_fin, statut, createur_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, defi.getTitre());
        ps.setString(2, defi.getDescription());
        ps.setString(3, defi.getTheme());
        ps.setString(4, defi.getImageCover());

        // Conversion LocalDate -> java.sql.Date
        ps.setDate(5, defi.getDateDebut() != null ? Date.valueOf(defi.getDateDebut()) : null);
        ps.setDate(6, defi.getDateFin() != null ? Date.valueOf(defi.getDateFin()) : null);

        ps.setString(7, defi.getStatut());
        ps.setInt(8, defi.getCreateurId());
        ps.executeUpdate();
    }

    @Override
    public void update(Defi defi) throws SQLException {
        String sql = "UPDATE defi SET titre = ?, description = ?, theme = ?, image_cover = ?, date_debut = ?, date_fin = ?, statut = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, defi.getTitre());
        ps.setString(2, defi.getDescription());
        ps.setString(3, defi.getTheme());
        ps.setString(4, defi.getImageCover());

        // Conversion LocalDate -> java.sql.Date
        ps.setDate(5, defi.getDateDebut() != null ? Date.valueOf(defi.getDateDebut()) : null);
        ps.setDate(6, defi.getDateFin() != null ? Date.valueOf(defi.getDateFin()) : null);

        ps.setString(7, defi.getStatut());
        ps.setInt(8, defi.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM challenge WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Defi> select() throws SQLException {
        List<Defi> list = new ArrayList<>();
        String sql = "SELECT * FROM challenge";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Defi d = new Defi();
            d.setId(rs.getInt("id"));
            d.setTitre(rs.getString("titre"));
            d.setDescription(rs.getString("description"));
            d.setTheme(rs.getString("theme"));
            d.setImageCover(rs.getString("image_cover"));

            // Conversion java.sql.Date -> LocalDate
            Date dateDebut = rs.getDate("date_debut");
            if (dateDebut != null) d.setDateDebut(dateDebut.toLocalDate());

            Date dateFin = rs.getDate("date_fin");
            if (dateFin != null) d.setDateFin(dateFin.toLocalDate());

            d.setStatut(rs.getString("statut"));
            d.setCreateurId(rs.getInt("createur_id"));
            list.add(d);
        }
        return list;
    }

    public List<Defi> searchDefis(String search, String sortBy) throws SQLException {
        List<Defi> list = new ArrayList<>();
        String orderBy = switch (sortBy) {
            case "titre" -> "ORDER BY titre ASC";
            case "theme" -> "ORDER BY theme ASC";
            case "ancien" -> "ORDER BY date_debut ASC";
            default -> "ORDER BY date_debut DESC";
        };
        String sql;
        PreparedStatement ps;
        if (search != null && !search.isEmpty()) {
            sql = "SELECT * FROM challenge WHERE titre LIKE ? OR theme LIKE ? " + orderBy;
            ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");
        } else {
            sql = "SELECT * FROM challenge " + orderBy;
            ps = connection.prepareStatement(sql);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Defi d = new Defi();
            d.setId(rs.getInt("id"));
            d.setTitre(rs.getString("titre"));
            d.setDescription(rs.getString("description"));
            d.setTheme(rs.getString("theme"));
            d.setImageCover(rs.getString("image_cover"));

            Date dateDebut = rs.getDate("date_debut");
            if (dateDebut != null) d.setDateDebut(dateDebut.toLocalDate());

            Date dateFin = rs.getDate("date_fin");
            if (dateFin != null) d.setDateFin(dateFin.toLocalDate());

            d.setStatut(rs.getString("statut"));
            d.setCreateurId(rs.getInt("createur_id"));
            list.add(d);
        }
        return list;
    }

    public List<Defi> getAllDefis() throws SQLException {
        return select();
    }
}
