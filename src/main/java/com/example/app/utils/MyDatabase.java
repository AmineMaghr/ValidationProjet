package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/midgar_3?allowMultiQueries=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    // Charger le driver MySQL
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("✅ Connexion à la base 'midgar_3' (MySQL) réussie !");
                    initializeDatabase();
                } catch (ClassNotFoundException e) {
                    System.out.println("❌ Driver MySQL non trouvé : " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("❌ Erreur de connexion : " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur de vérification de connexion : " + e.getMessage());
        }
        return connection;
    }

    private static void initializeDatabase() {
        System.out.println("✅ Base de données 'midgar_3' prête (tables existantes) !");
    }

    // Méthode singleton
    public static MyDatabase getInstance() {
        return new MyDatabase();
    }

    // Méthode pour tester la connexion et l'initialisation de la table
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Test de connexion réussi. La base est prête à l'emploi.");
            try {
                java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM questions");
                if (rs.next()) {
                    System.out.println("La table 'questions' contient " + rs.getInt(1) + " enregistrement(s).");
                }
            } catch (SQLException e) {
                System.out.println("Erreur de requête sur 'questions': " + e.getMessage());
            }
        } else {
            System.out.println("Echec du test de connexion.");
        }
    }
}