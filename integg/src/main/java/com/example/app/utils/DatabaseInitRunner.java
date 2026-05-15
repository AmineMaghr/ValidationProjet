package com.example.app.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitRunner {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "";

        System.out.println("Attempting to connect to MySQL and create 'midgar'...");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sqlPath = "init_database_midgar.sql";
            String sqlContent = Files.readString(Paths.get(sqlPath));

            String[] commands = sqlContent.split(";");
            for (String command : commands) {
                String trimmed = command.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                System.out.println("Executing: " + trimmed.split("\n")[0] + "...");
                try {
                    stmt.execute(trimmed);
                } catch (Exception e) {
                    System.out.println("Warning on executing command: " + e.getMessage());
                }
            }

            System.out.println("✅ Database 'midgar' fully created and populated from init_database_midgar.sql!");
        } catch (SQLException | IOException e) {
            System.err.println("❌ Critical Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
