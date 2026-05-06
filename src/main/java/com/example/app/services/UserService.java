package com.example.app.services;

import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class UserService implements IService<User> {

    private Connection connection;

    public UserService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO user (username, email, password, role, nom, prenom) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getNom());
        ps.setString(6, user.getPrenom());
        ps.executeUpdate();
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET username = ?, email = ?, role = ?, nom = ?, prenom = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getRole());
        ps.setString(4, user.getNom());
        ps.setString(5, user.getPrenom());
        ps.setInt(6, user.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            u.setRole(rs.getString("role"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setAvatar(rs.getString("avatar"));
            u.setBlocked(rs.getBoolean("is_blocked"));
            list.add(u);
        }
        return list;
    }

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            u.setRole(rs.getString("role"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            return u;
        }
        return null;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        String checkSql = "SELECT * FROM user WHERE id = ? AND password = ?";
        PreparedStatement checkPs = connection.prepareStatement(checkSql);
        checkPs.setInt(1, userId);
        checkPs.setString(2, oldPassword);
        ResultSet rs = checkPs.executeQuery();
        if (!rs.next()) return false;

        String updateSql = "UPDATE user SET password = ? WHERE id = ?";
        PreparedStatement updatePs = connection.prepareStatement(updateSql);
        updatePs.setString(1, newPassword);
        updatePs.setInt(2, userId);
        updatePs.executeUpdate();
        return true;
    }

    public List<User> searchUsersAdmin(String search, Object start, Object end, String sort, String direction) throws SQLException {
        return select();
    }
    public List<User> searchUsers(String query) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE username LIKE ? OR prenom LIKE ? OR nom LIKE ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        String searchPattern = "%" + query + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        ps.setString(3, searchPattern);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setPrenom(rs.getString("prenom"));
            u.setNom(rs.getString("nom"));
            u.setEmail(rs.getString("email"));
            u.setAvatar(rs.getString("avatar"));
            users.add(u);
        }
        return users;
    }
    public List<User> searchUsersAdmin(String search, LocalDate start, LocalDate end, String sort, String direction) throws SQLException {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (username LIKE ? OR email LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (start != null) {
            sql.append(" AND created_at >= ?");
            params.add(start.atStartOfDay());
        }

        if (end != null) {
            sql.append(" AND created_at <= ?");
            params.add(end.atTime(23, 59, 59));
        }

        String sortField = switch (sort) {
            case "username" -> "username";
            case "lastName" -> "nom";
            case "firstName" -> "prenom";
            default -> "created_at";
        };
        sql.append(" ORDER BY ").append(sortField).append(" ").append(direction);

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            u.setRole(rs.getString("role"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setAvatar(rs.getString("avatar"));
            u.setBlocked(rs.getBoolean("is_blocked"));
            users.add(u);
        }
        return users;
    }
}