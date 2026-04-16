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
    public void add(User app_user) throws SQLException {
        String sql = "INSERT INTO app_user (app_username, email, password, role, nom, prenom) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, app_user.getUsername());
        ps.setString(2, app_user.getEmail());
        ps.setString(3, app_user.getPassword());
        ps.setString(4, app_user.getRole());
        ps.setString(5, app_user.getNom());
        ps.setString(6, app_user.getPrenom());
        ps.executeUpdate();
    }

    @Override
    public void update(User app_user) throws SQLException {
        String sql = "UPDATE app_user SET app_username = ?, email = ?, role = ?, nom = ?, prenom = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, app_user.getUsername());
        ps.setString(2, app_user.getEmail());
        ps.setString(3, app_user.getRole());
        ps.setString(4, app_user.getNom());
        ps.setString(5, app_user.getPrenom());
        ps.setInt(6, app_user.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM app_user WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM app_user";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("app_username"));
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

    public User authenticate(String app_username, String password) throws SQLException {
        String sql = "SELECT * FROM app_user WHERE app_username = ? AND password = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, app_username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("app_username"));
            u.setEmail(rs.getString("email"));
            u.setRole(rs.getString("role"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            return u;
        }
        return null;
    }

    public boolean changePassword(int app_userId, String oldPassword, String newPassword) throws SQLException {
        String checkSql = "SELECT * FROM app_user WHERE id = ? AND password = ?";
        PreparedStatement checkPs = connection.prepareStatement(checkSql);
        checkPs.setInt(1, app_userId);
        checkPs.setString(2, oldPassword);
        ResultSet rs = checkPs.executeQuery();
        if (!rs.next()) return false;

        String updateSql = "UPDATE app_user SET password = ? WHERE id = ?";
        PreparedStatement updatePs = connection.prepareStatement(updateSql);
        updatePs.setString(1, newPassword);
        updatePs.setInt(2, app_userId);
        updatePs.executeUpdate();
        return true;
    }

    public List<User> searchUsersAdmin(String search, Object start, Object end, String sort, String direction) throws SQLException {
        return select();
    }
    public List<User> searchUsers(String query) throws SQLException {
        List<User> app_users = new ArrayList<>();
        String sql = "SELECT * FROM app_user WHERE app_username LIKE ? OR prenom LIKE ? OR nom LIKE ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        String searchPattern = "%" + query + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        ps.setString(3, searchPattern);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("app_username"));
            u.setPrenom(rs.getString("prenom"));
            u.setNom(rs.getString("nom"));
            u.setEmail(rs.getString("email"));
            u.setAvatar(rs.getString("avatar"));
            app_users.add(u);
        }
        return app_users;
    }
    public List<User> searchUsersAdmin(String search, LocalDate start, LocalDate end, String sort, String direction) throws SQLException {
        List<User> app_users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM app_user WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (app_username LIKE ? OR email LIKE ?)");
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
            case "app_username" -> "app_username";
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
            u.setUsername(rs.getString("app_username"));
            u.setEmail(rs.getString("email"));
            u.setRole(rs.getString("role"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setAvatar(rs.getString("avatar"));
            u.setBlocked(rs.getBoolean("is_blocked"));
            app_users.add(u);
        }
        return app_users;
    }
}