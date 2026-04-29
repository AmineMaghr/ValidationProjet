package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static final String URL = "jdbc:mysql://localhost:4306/midgar37";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger le driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base 'midgar37' (MySQL:4306) réussie !");
            } catch (ClassNotFoundException e) {
                System.out.println("❌ Driver MySQL non trouvé : " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("❌ Erreur de connexion : " + e.getMessage());
                System.out.println("   Assurez-vous que:");
                System.out.println("   - MySQL est en cours d'exécution sur le port 4306");
                System.out.println("   - La base de données 'midgar37' existe");
                System.out.println("   - L'utilisateur 'root' a accès à cette base");
            }
        }
        return connection;
    }


    // Méthode singleton
    public static MyDatabase getInstance() {
        return new MyDatabase();
    }

    // Méthode pour tester la connexion
    public static void main(String[] args) {
        getConnection();
    }
}