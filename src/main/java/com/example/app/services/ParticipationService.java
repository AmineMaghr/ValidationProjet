package com.example.app.services;
import com.example.app.entities.Defi;
import com.example.app.entities.Participation;
import com.example.app.entities.User;
import com.example.app.services.*;
import com.example.app.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService implements Iservice<Participation> {

    private Connection connection;

    public ParticipationService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Participation p) throws SQLException {

        String sql = "INSERT INTO participation (user_id, defi_id, date_participation) VALUES (?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, p.getUser().getId());
        ps.setInt(2, p.getDefi().getId());
        ps.setTimestamp(3, Timestamp.valueOf(p.getDateParticipation()));

        ps.executeUpdate();

        System.out.println("Participation ajoutée");
    }

    @Override
    public void update(Participation p) throws SQLException {

        String sql = "UPDATE participation SET user_id=?, defi_id=?, date_participation=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, p.getUser().getId());
        ps.setInt(2, p.getDefi().getId());
        ps.setTimestamp(3, Timestamp.valueOf(p.getDateParticipation()));
        ps.setInt(4, p.getId());

        ps.executeUpdate();

        System.out.println("Participation modifiée");
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM participation WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();

        System.out.println("Participation supprimée");
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

            // User
            User user = new User();
            user.setId(rs.getInt("user_id"));
            p.setUser(user);

            // Defi
            Defi defi = new Defi();
            defi.setId(rs.getInt("defi_id"));
            p.setDefi(defi);

            Timestamp date = rs.getTimestamp("date_participation");
            if (date != null) {
                p.setDateParticipation(date.toLocalDateTime());
            }

            list.add(p);
        }

        return list;
    }
}
