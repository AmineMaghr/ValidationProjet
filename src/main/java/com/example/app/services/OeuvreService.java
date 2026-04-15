package com.example.app.services;
import com.example.app.entities.Defi;
import com.example.app.entities.Oeuvre;
import com.example.app.services.*;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OeuvreService implements Iservice<Oeuvre> {

    private Connection connection;

    public OeuvreService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Oeuvre o) throws SQLException {

        String sql = "INSERT INTO oeuvre (titre, description, theme, image_cover_url, date_creation, date_mise_a_jour, createur_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, o.getTitre());
        ps.setString(2, o.getDescription());
        ps.setString(3, o.getTheme());
        ps.setString(4, o.getImageCoverUrl());
        ps.setTimestamp(5, Timestamp.valueOf(o.getDateCreation()));
        ps.setTimestamp(6, Timestamp.valueOf(o.getDateMiseAJour()));
        ps.setInt(7, o.getCreateurId());

        ps.executeUpdate();

        System.out.println("Oeuvre ajoutée");
    }

    @Override
    public void update(Oeuvre o) throws SQLException {

        String sql = "UPDATE oeuvre SET titre=?, description=?, theme=?, image_cover_url=?, date_mise_a_jour=?, createur_id=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, o.getTitre());
        ps.setString(2, o.getDescription());
        ps.setString(3, o.getTheme());
        ps.setString(4, o.getImageCoverUrl());
        ps.setTimestamp(5, Timestamp.valueOf(o.getDateMiseAJour()));
        ps.setInt(6, o.getCreateurId());
        ps.setInt(7, o.getId());

        ps.executeUpdate();

        System.out.println("Oeuvre modifiée");
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM oeuvre WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();

        System.out.println("Oeuvre supprimée");
    }

    @Override
    public List<Oeuvre> select() throws SQLException {

        List<Oeuvre> list = new ArrayList<>();

        String sql = "SELECT * FROM oeuvre";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            Oeuvre o = new Oeuvre();

            o.setId(rs.getInt("id"));
            o.setTitre(rs.getString("titre"));
            o.setDescription(rs.getString("description"));
            o.setTheme(rs.getString("theme"));
            o.setImageCoverUrl(rs.getString("image_cover_url"));
            o.setCreateurId(rs.getInt("createur_id"));

            Timestamp creation = rs.getTimestamp("date_creation");
            if (creation != null) {
                o.setDateCreation(creation.toLocalDateTime());
            }

            Timestamp update = rs.getTimestamp("date_mise_a_jour");
            if (update != null) {
                o.setDateMiseAJour(update.toLocalDateTime());
            }

            list.add(o);
        }

        return list;
    }
}
