package com.example.app;
import com.example.app.utils.MyDatabase;
import java.sql.*;
public class DbTest {
    public static void main(String[] args) {
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            // Check artefacts table
            System.out.println("=== Checking artefacts table ===");
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as count FROM artefacts");
            if (rs.next()) {
                System.out.println("Total artefacts: " + rs.getInt("count"));
            }
            rs.close();

            // Get some artefacts
            rs = conn.createStatement().executeQuery("SELECT id, name, type, universe FROM artefacts LIMIT 5");
            System.out.println("First 5 artefacts:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Type: " + rs.getString("type") + ", Universe: " + rs.getString("universe"));
            }
            rs.close();

            // Check commandes table
            System.out.println("=== Checking commandes table ===");
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) as count FROM commande");
            if (rs.next()) {
                System.out.println("Total commandes: " + rs.getInt("count"));
            }
            rs.close();

            // Get some commandes
            rs = conn.createStatement().executeQuery("SELECT id, acheteur, etat, prix_total, reference_commande FROM commande LIMIT 5");
            System.out.println("First 5 commandes:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Acheteur: " + rs.getString("acheteur") + ", Etat: " + rs.getString("etat") + ", Prix: " + rs.getDouble("prix_total") + ", Ref: " + rs.getString("reference_commande"));
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
