package com.example.app.services;
import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements Iservice<User> {

    private Connection connection;

    public UserService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(User p) throws SQLException {
        String sql = "INSERT INTO user " +
                "(username, email, password, role, avatar, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getUsername());
        ps.setString(2, p.getEmail());
        ps.setString(3, p.getPassword());
        ps.setString(4, p.getRole());
        ps.setString(5, p.getAvatar());
        ps.setTimestamp(6, Timestamp.valueOf(p.getCreatedAt()));
        ps.setTimestamp(7, Timestamp.valueOf(p.getUpdatedAt()));
        ps.executeUpdate();
        System.out.println("User ajoutée");
    }

    @Override
    public void update(User p) throws SQLException {
        String sql = "UPDATE user SET " +
                "username=?, email=?, password=?, role=?, avatar=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getUsername());
        ps.setString(2, p.getEmail());
        ps.setString(3, p.getPassword());
        ps.setString(4, p.getRole());
        ps.setString(5, p.getAvatar());
        ps.setTimestamp(6, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(7, p.getId());
        ps.executeUpdate();
        System.out.println("User modifiée");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("User supprimée");
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User u = new User(rs.getString("username"), rs.getString("email"),
                    rs.getString("password"), rs.getString("role"));
            u.setId(rs.getInt("id"));
            u.setAvatar(rs.getString("avatar"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                u.setCreatedAt(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                u.setUpdatedAt(updated.toLocalDateTime());
            }
            list.add(u);
        }
        return list;
    }
}
