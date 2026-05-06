package com.example.app.dao;

import com.example.app.entities.Reponse;
import com.example.app.entities.Question;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseDAO implements IDAO<Reponse> {

    private Connection connection;

    public ReponseDAO() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Reponse reponse) throws SQLException {
        String sql = "INSERT INTO reponses (option_text, tag, question_id) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, reponse.getOption());
        ps.setString(2, reponse.getTag());
        ps.setInt(3, reponse.getQuestion().getId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            reponse.setId(rs.getInt(1));
        }
    }

    @Override
    public void update(Reponse reponse) throws SQLException {
        String sql = "UPDATE reponses SET option_text = ?, tag = ?, question_id = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, reponse.getOption());
        ps.setString(2, reponse.getTag());
        ps.setInt(3, reponse.getQuestion().getId());
        ps.setInt(4, reponse.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reponses WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Reponse> select() throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT r.*, q.question as question_text FROM reponses r LEFT JOIN questions q ON r.question_id = q.id ORDER BY r.id ASC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Reponse> searchAndSort(String search, String sort, String direction) throws SQLException {
        List<Reponse> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT r.*, q.question as question_text FROM reponses r LEFT JOIN questions q ON r.question_id = q.id WHERE 1=1");

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (r.option_text LIKE ? OR r.tag LIKE ?)");
        }

        String sortField = switch (sort) {
            case "option" -> "r.option_text";
            case "tag" -> "r.tag";
            default -> "r.id";
        };

        String dir = "DESC".equalsIgnoreCase(direction) ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(sortField).append(" ").append(dir);

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Reponse> findByQuestion(int questionId) throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponses WHERE question_id = ? ORDER BY id ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, questionId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Reponse reponse = new Reponse();
            reponse.setId(rs.getInt("id"));
            reponse.setOption(rs.getString("option_text"));
            reponse.setTag(rs.getString("tag"));
            list.add(reponse);
        }
        return list;
    }

    private Reponse mapResultSet(ResultSet rs) throws SQLException {
        Reponse reponse = new Reponse();
        reponse.setId(rs.getInt("id"));
        reponse.setOption(rs.getString("option_text"));
        reponse.setTag(rs.getString("tag"));

        Question question = new Question();
        question.setId(rs.getInt("question_id"));
        question.setQuestion(rs.getString("question_text"));
        reponse.setQuestion(question);

        return reponse;
    }
}