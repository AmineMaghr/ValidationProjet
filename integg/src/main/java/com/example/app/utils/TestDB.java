package com.example.app.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("🔍 Test de connexion à MySQL...");

        Connection conn = MyDatabase.getConnection();

        if (conn != null) {
            System.out.println("✅ Connexion réussie !");
            try {
                System.out.println("📁 Base de données : " + conn.getCatalog());
                conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Erreur : " + e.getMessage());
            }
        } else {
            System.out.println("❌ Échec de connexion");
        }
    }
}