package com.example.app.services;

import com.example.app.entities.Question;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService implements IService<Question> {

    private Connection connection;

    public QuestionService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Question question) throws SQLException {
        String sql = "INSERT INTO questions (question) VALUES (?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, question.getQuestion());
        ps.executeUpdate();
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
        String sql = "SELECT * FROM questions";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Question q = new Question();
            q.setId(rs.getInt("id"));
            q.setQuestion(rs.getString("question"));
            list.add(q);
        }
        return list;
    }

    public List<Question> searchAndSort(String search, String sort) throws SQLException {
        return select();
    }
}