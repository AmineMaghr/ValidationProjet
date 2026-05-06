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
        System.out.println("🔧 Initialisation du QuestionService. Connexion DB: " + (connection != null ? "OK" : "KO"));
    }

    private void validateQuestion(Question question) {
        if (question == null || question.getQuestion() == null || question.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("La question ne peut pas être vide.");
        }
        String text = question.getQuestion().trim();
        if (text.length() < 10) {
            throw new IllegalArgumentException("La question doit comporter au moins 10 caractères.");
        }
        if (!text.endsWith("?")) {
            throw new IllegalArgumentException("Une question valide doit se terminer par un point d'interrogation '?'.");
        }
    }

    @Override
    public void add(Question question) throws SQLException {
        validateQuestion(question);
        String sql = "INSERT INTO questions (question, created_at) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, question.getQuestion().trim());
            ps.executeUpdate();
            System.out.println("✅ Question insérée: " + question.getQuestion());
        }
    }

    @Override
    public void update(Question question) throws SQLException {
        validateQuestion(question);
        String sql = "UPDATE questions SET question = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, question.getQuestion().trim());
            ps.setInt(2, question.getId());
            ps.executeUpdate();
            System.out.println("✅ Question mise à jour (ID=" + question.getId() + ")");
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // Supprimer d'abord les réponses associées pour éviter l'erreur de contrainte Foreign Key
        String sqlResponses = "DELETE FROM reponses WHERE question_id = ?";
        try (PreparedStatement psR = connection.prepareStatement(sqlResponses)) {
            psR.setInt(1, id);
            psR.executeUpdate();
        } catch (SQLException e) {
            System.out.println("No reponses table or error: " + e.getMessage());
        }

        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("🗑️ Question supprimée (ID=" + id + ")");
        }
    }

    @Override
    public List<Question> select() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setQuestion(rs.getString("question"));
                Timestamp ts = rs.getTimestamp("created_at");
                if(ts != null) {
                    q.setCreatedAt(ts.toLocalDateTime());
                }
                list.add(q);
            }
        }
        System.out.println("📋 Chargement de " + list.size() + " question(s).");
        return list;
    }

    public List<Question> searchAndSort(String search, String sort) throws SQLException {
        List<Question> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM questions ");
        
        boolean hasSearch = (search != null && !search.trim().isEmpty());
        if (hasSearch) {
            sql.append("WHERE LOWER(question) LIKE ? ");
        }

        // Gestion manuelle du tri pour sécuriser l'ORDER BY
        if (sort != null && !sort.trim().isEmpty()) {
            switch (sort) {
                case "Plus rcent":
                case "Plus récent":
                    sql.append("ORDER BY created_at DESC ");
                    break;
                case "Plus ancien":
                    sql.append("ORDER BY created_at ASC ");
                    break;
                default:
                    sql.append("ORDER BY created_at DESC "); // default order
                    break;
            }
        } else {
            sql.append("ORDER BY created_at DESC ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (hasSearch) {
                ps.setString(1, "%" + search.trim().toLowerCase() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question q = new Question();
                    q.setId(rs.getInt("id"));
                    q.setQuestion(rs.getString("question"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if(ts != null) {
                        q.setCreatedAt(ts.toLocalDateTime());
                    }
                    list.add(q);
                }
            }
        }
        return list;
    }

    public int countQuestions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countReponses() throws SQLException {
        // Will only work if reponses table exists, safe check.
        String sql = "SELECT COUNT(*) FROM reponses";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("No reponses table or error: " + e.getMessage());
        }
        return 0;
    }
}