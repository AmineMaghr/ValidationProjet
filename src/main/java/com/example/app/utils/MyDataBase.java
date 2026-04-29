package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // H2 Database configuration (embedded in-memory database)
    private final String url = "jdbc:h2:mem:midgar;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS midgar";
    private final String user = "sa";
    private final String password = "";

    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connecté à la base de données H2 en mémoire");

            // Initialize database schema
            initializeDatabase();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            // Create tables if they don't exist
            String[] sqlStatements = {
                    // User table
                    "CREATE TABLE IF NOT EXISTS \"USER\" (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(50) DEFAULT 'user', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")",

                    // Defi table
                    "CREATE TABLE IF NOT EXISTS \"DEFI\" (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "titre VARCHAR(255) NOT NULL, " +
                    "description TEXT, " +
                    "theme VARCHAR(100), " +
                    "image_cover VARCHAR(500), " +
                    "date_debut DATE, " +
                    "date_fin DATE, " +
                    "date_limite DATE, " +
                    "statut VARCHAR(50) DEFAULT 'OUVERT', " +
                    "difficulte VARCHAR(50) DEFAULT 'FACILE', " +
                    "createur_id INT NOT NULL, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (createur_id) REFERENCES \"USER\"(id)" +
                    ")",

                    // Participation table
                    "CREATE TABLE IF NOT EXISTS \"PARTICIPATION\" (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "defi_id INT NOT NULL, " +
                    "user_id INT NOT NULL, " +
                    "date_participation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "statut VARCHAR(50) DEFAULT 'EN_COURS', " +
                    "FOREIGN KEY (defi_id) REFERENCES \"DEFI\"(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES \"USER\"(id) ON DELETE CASCADE" +
                    ")",

                    // Create indexes
                    "CREATE INDEX IF NOT EXISTS idx_defi_statut ON \"DEFI\"(statut)",
                    "CREATE INDEX IF NOT EXISTS idx_defi_theme ON \"DEFI\"(theme)",
                    "CREATE INDEX IF NOT EXISTS idx_defi_createur ON \"DEFI\"(createur_id)",
                    "CREATE INDEX IF NOT EXISTS idx_defi_dates ON \"DEFI\"(date_debut, date_fin)",
                    "CREATE INDEX IF NOT EXISTS idx_participation_defi ON \"PARTICIPATION\"(defi_id)",
                    "CREATE INDEX IF NOT EXISTS idx_participation_user ON \"PARTICIPATION\"(user_id)"
            };

            for (String sql : sqlStatements) {
                connection.createStatement().execute(sql);
            }

            System.out.println("✅ Schéma de base de données initialisé avec succès");

            // Insert sample data
            insertSampleData();

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertSampleData() {
        try {
            // Check if admin user already exists
            var rs = connection.createStatement().executeQuery("SELECT COUNT(*) as count FROM \"USER\" WHERE username = 'admin'");
            rs.next();

            if (rs.getInt("count") == 0) {
                // Insert admin user
                connection.createStatement().execute(
                        "INSERT INTO \"USER\" (username, email, password, role) VALUES " +
                        "('admin', 'admin@midgar.com', 'password123', 'admin')," +
                        "('user1', 'user1@midgar.com', 'password123', 'user')," +
                        "('user2', 'user2@midgar.com', 'password123', 'user')"
                );

                // Insert sample defis
                connection.createStatement().execute(
                        "INSERT INTO \"DEFI\" (titre, description, theme, statut, difficulte, createur_id, date_debut, date_fin) VALUES " +
                        "('Défi Éco-Responsable', 'Réduire votre empreinte carbone', 'Environnement', 'OUVERT', 'FACILE', 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '30' DAY)," +
                        "('Défi Santé', 'Faire 10000 pas par jour', 'Santé', 'OUVERT', 'MOYEN', 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '60' DAY)," +
                        "('Défi Créatif', 'Créer une œuvre artistique', 'Art', 'TERMINÉ', 'DIFFICILE', 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '14' DAY)"
                );

                System.out.println("✅ Données d'exemple insérées");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur lors de l'insertion des données d'exemple : " + e.getMessage());
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