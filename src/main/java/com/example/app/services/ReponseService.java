package com.example.app.services;

import com.example.app.entities.Reponse;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements IService<Reponse> {

    private Connection connection;

    public ReponseService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Reponse reponse) throws SQLException {
        String sql = "INSERT INTO reponses (option_text, tag, question_id) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, reponse.getOption());
        ps.setString(2, reponse.getTag());
        ps.setInt(3, reponse.getQuestion().getId());
        ps.executeUpdate();
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
        String sql = "SELECT * FROM reponses";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Reponse r = new Reponse();
            r.setId(rs.getInt("id"));
            r.setOption(rs.getString("option_text"));
            r.setTag(rs.getString("tag"));
            list.add(r);
        }
        return list;
    }

    public List<Reponse> findByQuestion(int questionId) throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT * FROM reponses WHERE question_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, questionId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Reponse r = new Reponse();
            r.setId(rs.getInt("id"));
            r.setOption(rs.getString("option_text"));
            r.setTag(rs.getString("tag"));
            list.add(r);
        }
        return list;
    }
}