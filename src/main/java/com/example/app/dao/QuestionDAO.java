package com.example.app.dao;

import com.example.app.entities.Question;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO implements IDAO<Question> {

    private Connection connection;

    public QuestionDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Question question) throws SQLException {
        String sql = "INSERT INTO questions (question, created_at) VALUES (?, NOW())";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, question.getQuestion());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            question.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Question question) throws SQLException {
        String sql = "UPDATE questions SET question = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, question.getQuestion());
        ps.setInt(2, question.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Question> select() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Question> searchAndSort(String search, String sort, String direction) throws SQLException {
        List<Question> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM questions WHERE 1=1");

        if (search != null && !search.isEmpty()) {
            sql.append(" AND question LIKE ?");
        }

        String sortField = switch (sort) {
            case "question" -> "question";
            default -> "created_at";
        };

        String dir = "DESC".equalsIgnoreCase(direction) ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(sortField).append(" ").append(dir);

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        if (search != null && !search.isEmpty()) {
            ps.setString(1, "%" + search + "%");
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Question> findAllOrderedByDate() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Question> findAllOrderedAlphabetically() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY question ASC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    private Question mapResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setQuestion(rs.getString("question"));
        return question;
    }
}