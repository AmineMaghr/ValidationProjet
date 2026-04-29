package com.example.app.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("🔍 Test de connexion à H2 Database...\n");

        try {
            // Get connection
            Connection conn = MyDatabase.getInstance().getConnection();

            if (conn != null) {
                System.out.println("✅ Connexion réussie !\n");
                
                // Test SELECT users
                System.out.println("📊 Utilisateurs :");
                var usersRs = conn.createStatement().executeQuery("SELECT * FROM \"USER\"");
                while (usersRs.next()) {
                    System.out.println("  - " + usersRs.getString("username") + " (" + usersRs.getString("role") + ")");
                }
                
                // Test SELECT defis
                System.out.println("\n🏆 Défis :");
                var defisRs = conn.createStatement().executeQuery("SELECT * FROM \"DEFI\"");
                while (defisRs.next()) {
                    System.out.println("  - " + defisRs.getString("titre") + " (" + defisRs.getString("statut") + ")");
                }
                
                // Test INSERT
                System.out.println("\n➕ Création d'un nouveau défi...");
                conn.createStatement().execute(
                    "INSERT INTO \"DEFI\" (titre, description, theme, statut, difficulte, createur_id, date_debut, date_fin) " +
                    "VALUES ('Défi Technologie', 'Apprendre Java', 'Tech', 'OUVERT', 'MOYEN', 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '45' DAY)"
                );
                System.out.println("✅ Défi créé avec succès !");
                
                // Test UPDATE
                System.out.println("\n✏️  Mise à jour du défi...");
                conn.createStatement().execute(
                    "UPDATE \"DEFI\" SET description = 'Maîtriser Java et JavaFX' WHERE titre = 'Défi Technologie'"
                );
                System.out.println("✅ Défi mis à jour !");
                
                // Test DELETE
                System.out.println("\n🗑️  Suppression du défi...");
                conn.createStatement().execute(
                    "DELETE FROM \"DEFI\" WHERE titre = 'Défi Technologie'"
                );
                System.out.println("✅ Défi supprimé !");
                
                // Final count
                System.out.println("\n📈 Total final des défis :");
                var countRs = conn.createStatement().executeQuery("SELECT COUNT(*) as count FROM \"DEFI\"");
                countRs.next();
                System.out.println("  Nombre de défis : " + countRs.getInt("count"));
                
                System.out.println("\n✅ Tous les tests CRUD sont passés avec succès !\n");
                
            } else {
                System.out.println("❌ Échec de connexion");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}