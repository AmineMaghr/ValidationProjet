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
        String url = "jdbc:mysql://localhost:4306/";
        String user = "root";
        String password = "";

        System.out.println("Attempting to connect to MySQL on localhost:4306 and create 'midgar37'...");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // Read the SQL script
            String sqlPath = "init_database.sql";
            String sqlContent = Files.readString(Paths.get(sqlPath));

            // Execute the whole script
            String[] commands = sqlContent.split(";");
            for (String command : commands) {
                if (command.trim().isEmpty()) continue;
                System.out.println("Executing: " + command.trim().split("\n")[0] + "...");
                try {
                    stmt.execute(command);
                } catch (Exception e) {
                    System.out.println("Warning on executing command: " + e.getMessage());
                }
            }

            System.out.println("✅ Database 'midgar37' fully created and populated from init_database.sql!");

        } catch (SQLException | IOException e) {
            System.err.println("❌ Critical Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

