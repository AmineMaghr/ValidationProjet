package com.example.app.dao;

import com.example.app.entities.User;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IDAO<User> {

    private Connection connection;

    public UserDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO app_user (email, password, role, nom, prenom, username, avatar, bio, is_blocked, is_verified, phone_number, auth_provider, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole());
        ps.setString(4, user.getNom());
        ps.setString(5, user.getPrenom());
        ps.setString(6, user.getUsername());
        ps.setString(7, user.getAvatar());
        ps.setString(8, user.getBio());
        ps.setBoolean(9, user.isBlocked());
        ps.setBoolean(10, user.isVerified());
        ps.setString(11, user.getPhoneNumber());
        ps.setString(12, user.getAuthProvider());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            user.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, password = ?, role = ?, nom = ?, prenom = ?, username = ?, avatar = ?, bio = ?, is_blocked = ?, is_verified = ?, phone_number = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole());
        ps.setString(4, user.getNom());
        ps.setString(5, user.getPrenom());
        ps.setString(6, user.getUsername());
        ps.setString(7, user.getAvatar());
        ps.setString(8, user.getBio());
        ps.setBoolean(9, user.isBlocked());
        ps.setBoolean(10, user.isVerified());
        ps.setString(11, user.getPhoneNumber());
        ps.setInt(12, user.getId());
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
        String sql = "SELECT * FROM app_user ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public User findActiveByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM app_user WHERE email = ? AND is_blocked = false";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public List<User> findAdmins() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM app_user WHERE role = 'admin'";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<User> searchPublicUsers(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM app_user WHERE username LIKE ? AND is_blocked = false LIMIT 10";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + query + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<User> searchUsersApi(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, prenom, nom, avatar, created_at FROM app_user WHERE (username LIKE ? OR prenom LIKE ? OR nom LIKE ?) AND is_blocked = false ORDER BY username ASC LIMIT 10";
        PreparedStatement ps = connection.prepareStatement(sql);
        String searchPattern = "%" + query + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        ps.setString(3, searchPattern);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPrenom(rs.getString("prenom"));
            user.setNom(rs.getString("nom"));
            user.setAvatar(rs.getString("avatar"));
            list.add(user);
        }
        return list;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM app_user WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setUsername(rs.getString("username"));
        user.setAvatar(rs.getString("avatar"));
        user.setBio(rs.getString("bio"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAuthProvider(rs.getString("auth_provider"));
        return user;
    }
}