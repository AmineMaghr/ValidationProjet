package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // Configuration MySQL (correspond à votre DATABASE_URL)
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/midgar?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Initialisation statique - s'exécute une seule fois au chargement de la classe
    static {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base MySQL 'midgar' réussie !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé !");
            System.err.println("Vérifiez que mysql-connector-j est dans votre pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à MySQL : " + e.getMessage());
            System.err.println("Vérifiez que :");
            System.err.println("1. MySQL est démarré");
            System.err.println("2. La base 'midgar' existe");
            System.err.println("3. L'utilisateur 'root' a les droits");
            e.printStackTrace();
        }
    }

    // Méthode pour obtenir la connexion
    public static Connection getConnection() {
        // Vérifier si la connexion est encore valide
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Reconnexion à MySQL réussie !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de reconnexion : " + e.getMessage());
        }
        return connection;
    }

    // Méthode pour obtenir l'instance (compatibilité avec votre code)
    public static MyDatabase getInstance() {
        return new MyDatabase();
    }

    // Méthode pour fermer la connexion
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connexion MySQL fermée");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour tester la connexion
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}