package com.example.app.services;
import com.example.app.entities.Defi;
import com.example.app.services.*;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefiService implements Iservice<Defi> {

    private Connection connection;

    public DefiService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Defi p) throws SQLException {
        String sql = "INSERT INTO defi " +
                "(titre, description, theme, image_cover_url, date_debut, date_fin, date_limite, statut, createur_id, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getTitre());
        ps.setString(2, p.getDescription());
        ps.setString(3, p.getTheme());
        ps.setString(4, p.getImageCoverUrl());
        ps.setDate(5, Date.valueOf(p.getDateDebut()));
        ps.setDate(6, Date.valueOf(p.getDateFin()));
        if (p.getDateLimite() != null) {
            ps.setDate(7, Date.valueOf(p.getDateLimite()));
        } else {
            ps.setNull(7, Types.DATE);
        }
        ps.setString(8, p.getStatut());
        ps.setInt(9, p.getCreateurId());
        ps.setTimestamp(10, Timestamp.valueOf(p.getUpdatedAt()));
        ps.executeUpdate();
        System.out.println("Defi ajouté");
    }

    @Override
    public void update(Defi p) throws SQLException {
        String sql = "UPDATE defi SET " +
                "titre=?, description=?, theme=?, image_cover_url=?, date_debut=?, date_fin=?, date_limite=?, statut=?, createur_id=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getTitre());
        ps.setString(2, p.getDescription());
        ps.setString(3, p.getTheme());
        ps.setString(4, p.getImageCoverUrl());
        ps.setDate(5, Date.valueOf(p.getDateDebut()));
        ps.setDate(6, Date.valueOf(p.getDateFin()));
        if (p.getDateLimite() != null) {
            ps.setDate(7, Date.valueOf(p.getDateLimite()));
        } else {
            ps.setNull(7, Types.DATE);
        }
        ps.setString(8, p.getStatut());
        ps.setInt(9, p.getCreateurId());
        ps.setTimestamp(10, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(11, p.getId());
        ps.executeUpdate();
        System.out.println("Defi modifié");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM defi WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Defi supprimé");
    }

    @Override
    public List<Defi> select() throws SQLException {
        List<Defi> list = new ArrayList<>();
        String sql = "SELECT * FROM defi";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Defi d = new Defi();
            d.setId(rs.getInt("id"));
            d.setTitre(rs.getString("titre"));
            d.setDescription(rs.getString("description"));
            d.setTheme(rs.getString("theme"));
            d.setImageCoverUrl(rs.getString("image_cover_url"));
            Date dateDebut = rs.getDate("date_debut");
            if (dateDebut != null) {
                d.setDateDebut(dateDebut.toLocalDate());
            }
            Date dateFin = rs.getDate("date_fin");
            if (dateFin != null) {
                d.setDateFin(dateFin.toLocalDate());
            }
            Date dateLimite = rs.getDate("date_limite");
            if (dateLimite != null) {
                d.setDateLimite(dateLimite.toLocalDate());
            }
            d.setStatut(rs.getString("statut"));
            d.setCreateurId(rs.getInt("createur_id"));
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                d.setUpdatedAt(updated.toLocalDateTime());
            }
            list.add(d);
        }
        return list;
    }
}
