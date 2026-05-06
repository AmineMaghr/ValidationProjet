package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.app.utils.ConfigManager;

public class MyDatabase {
    private Connection connection;
    private static MyDatabase instance;

    private final ConfigManager config = ConfigManager.getInstance();

    private MyDatabase() {
        try {
            String dbUrl = config.getString("db.url");
            String dbUser = config.getString("db.user");
            String dbPassword = config.getString("db.password");

            // Auto-detect MySQL driver (JDBC 4.0+)
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("✅ Connecté à MySQL: " + dbUrl);
            
            // Schema must be created manually via init_database.sql
            System.out.println("ℹ️  Exécutez 'source init_database.sql' dans MySQL pour créer le schéma");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur MySQL: " + e.getMessage());
            System.err.println("Vérifiez: MySQL démarré? DB 'midgar' existe? init_database.sql exécuté?");
            e.printStackTrace();
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
