package com.example.app.services;
import com.example.app.entities.Question;
import com.example.app.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService implements Iservice<Question> {

    private Connection connection;

    public QuestionService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Question p) throws SQLException {
        String sql = "INSERT INTO question " +
                "(titre, contenu, createur_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getTitre());
        ps.setString(2, p.getContenu());
        ps.setInt(3, p.getCreateur().getId());
        ps.setTimestamp(4, Timestamp.valueOf(p.getCreatedAt()));
        ps.setTimestamp(5, Timestamp.valueOf(p.getUpdatedAt()));
        ps.executeUpdate();
        System.out.println("Question ajoutée");
    }

    @Override
    public void update(Question p) throws SQLException {
        String sql = "UPDATE question SET " +
                "titre=?, contenu=?, createur_id=?, updated_at=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getTitre());
        ps.setString(2, p.getContenu());
        ps.setInt(3, p.getCreateur().getId());
        ps.setTimestamp(4, Timestamp.valueOf(p.getUpdatedAt()));
        ps.setInt(5, p.getId());
        ps.executeUpdate();
        System.out.println("Question modifiée");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM question WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Question supprimée");
    }

    @Override
    public List<Question> select() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Question q = new Question();
            q.setId(rs.getInt("id"));
            q.setTitre(rs.getString("titre"));
            q.setContenu(rs.getString("contenu"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) {
                q.setCreatedAt(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("updated_at");
            if (updated != null) {
                q.setUpdatedAt(updated.toLocalDateTime());
            }
            list.add(q);
        }
        return list;
    }
}
