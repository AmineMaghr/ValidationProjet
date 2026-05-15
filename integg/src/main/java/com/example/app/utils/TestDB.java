package com.example.app.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("🔍 Test de connexion à MySQL...");

        Connection conn = MyDatabase.getConnection();

        if (conn != null) {
            System.out.println("✅ Connexion réussie !");
            try {
                System.out.println("📁 Base de données : " + conn.getCatalog());

                // Check commandes
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as count FROM commande");
                if (rs.next()) {
                    System.out.println("Total commandes: " + rs.getInt("count"));
                }
                rs.close();

                rs = st.executeQuery("SELECT id, acheteur, etat, prix_total FROM commande LIMIT 5");
                System.out.println("First 5 commandes:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Acheteur: " + rs.getString("acheteur") + ", Etat: " + rs.getString("etat") + ", Prix: " + rs.getDouble("prix_total"));
                }
                rs.close();

                conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Erreur : " + e.getMessage());
            }
        } else {
            System.out.println("❌ Échec de connexion");
        }
    }
}