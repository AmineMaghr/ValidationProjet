package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static final String URL = "jdbc:h2:mem:midgar;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger le driver H2
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base 'midgar' (H2) réussie !");
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                System.out.println("❌ Driver H2 non trouvé : " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("❌ Erreur de connexion : " + e.getMessage());
            }
        }
        return connection;
    }

    private static void initializeDatabase() {
        try {
            // Créer les tables
            String createTables = """
                CREATE TABLE IF NOT EXISTS user (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(50) DEFAULT 'user',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS universe (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    short_description TEXT,
                    description TEXT,
                    themes VARCHAR(500),
                    image_url VARCHAR(500),
                    creator_id INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS oeuvre (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    description TEXT,
                    image_url VARCHAR(500),
                    universe_id INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS personnage (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    image_url VARCHAR(500),
                    universe_id INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS challenge (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    description TEXT,
                    difficulty VARCHAR(50),
                    points INT DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS artefact (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    price DECIMAL(10,2),
                    image_url VARCHAR(500),
                    type VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;

            connection.createStatement().execute(createTables);

            // Insérer des données de test
            String insertData = """
                INSERT INTO user (username, email, password, role) VALUES
                ('admin', 'admin@midgar.com', '123456', 'admin'),
                ('user', 'user@midgar.com', 'password', 'user');

                INSERT INTO universe (name, short_description, description, themes, creator_id) VALUES
                ('Univers Test', 'Un univers de démonstration', 'Cet univers sert à tester l''application Midgar.', 'Fantasy, Aventure', 1);

                INSERT INTO oeuvre (title, description, universe_id) VALUES
                ('Oeuvre Test', 'Une oeuvre de démonstration dans l''univers test.', 1);

                INSERT INTO personnage (name, description, universe_id) VALUES
                ('Personnage Test', 'Un personnage de démonstration.', 1);

                INSERT INTO challenge (title, description, difficulty, points) VALUES
                ('Défi Test', 'Un défi de démonstration pour tester l''application.', 'Facile', 10);

                INSERT INTO artefact (name, description, price, type) VALUES
                ('Artefact Test', 'Un artefact de démonstration pour la boutique.', 99.99, 'Arme');
                """;

            connection.createStatement().execute(insertData);
            System.out.println("✅ Base de données initialisée avec des données de test !");

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'initialisation de la base : " + e.getMessage());
        }
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