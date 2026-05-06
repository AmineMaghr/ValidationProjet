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

    private void validateReponse(Reponse reponse) {
        if (reponse == null) {
            throw new IllegalArgumentException("La reponse ne peut pas être nulle.");
        }
        if (reponse.getOption() == null || reponse.getOption().trim().isEmpty()) {
            throw new IllegalArgumentException("L'option ne peut pas être vide.");
        }
        if (reponse.getOption().trim().length() < 5) {
            throw new IllegalArgumentException("L'option doit contenir au moins 5 caractères.");
        }
        if (reponse.getTag() == null || reponse.getTag().trim().isEmpty()) {
            throw new IllegalArgumentException("Le tag est requis.");
        }
        if (!reponse.getTag().trim().startsWith("#")) {
            throw new IllegalArgumentException("Le tag doit commencer par '#'.");
        }
        if (reponse.getQuestion() == null || reponse.getQuestion().getId() <= 0) {
            throw new IllegalArgumentException("La réponse doit être associée à une question valide.");
        }
    }

    @Override
    public void add(Reponse reponse) throws SQLException {
        validateReponse(reponse);
        String sql = "INSERT INTO reponses (`option`, tag, question_id) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, reponse.getOption());
        ps.setString(2, reponse.getTag());
        ps.setInt(3, reponse.getQuestion().getId());
        ps.executeUpdate();
    }

    @Override
    public void update(Reponse reponse) throws SQLException {
        validateReponse(reponse);
        String sql = "UPDATE reponses SET `option` = ?, tag = ?, question_id = ? WHERE id = ?";
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
        String sql = "SELECT r.*, q.question AS question_text FROM reponses r LEFT JOIN questions q ON r.question_id = q.id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setOption(rs.getString("option"));
                r.setTag(rs.getString("tag"));
                
                com.example.app.entities.Question q = new com.example.app.entities.Question();
                q.setId(rs.getInt("question_id"));
                q.setQuestion(rs.getString("question_text"));
                r.setQuestion(q);
                
                list.add(r);
            }
        }
        return list;
    }

    public List<Reponse> findByQuestion(int questionId) throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT r.*, q.question AS question_text FROM reponses r LEFT JOIN questions q ON r.question_id = q.id WHERE r.question_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id"));
                    r.setOption(rs.getString("option"));
                    r.setTag(rs.getString("tag"));
                    
                    com.example.app.entities.Question q = new com.example.app.entities.Question();
                    q.setId(rs.getInt("question_id"));
                    q.setQuestion(rs.getString("question_text"));
                    r.setQuestion(q);
                    
                    list.add(r);
                }
            }
        }
        return list;
    }
}