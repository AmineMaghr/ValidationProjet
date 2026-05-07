package com.example.app;
import com.example.app.utils.MyDatabase;
import java.sql.*;
public class DbTest {
    public static void main(String[] args) {
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM reponses LIMIT 1");
            ResultSetMetaData rsmd = rs.getMetaData();
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println(rsmd.getColumnName(i));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
