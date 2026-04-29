package com.example.app.utils;

import com.example.app.dao.DefiDAO;
import com.example.app.entities.Defi;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe d'exemple montrant comment utiliser DefiDAO avec H2
 */
public class ExempleDefiDAOUsage {
    
    public static void main(String[] args) {
        System.out.println("📚 Exemple d'utilisation de DefiDAO avec H2\n");
        
        DefiDAO defiDAO = new DefiDAO();
        
        try {
            // 1️⃣ CREATE - Ajouter un nouveau défi
            System.out.println("1️⃣  CREATE - Ajouter un nouveau défi");
            System.out.println("━".repeat(50));
            Defi newDefi = new Defi();
            newDefi.setTitre("Défi Java Avancé");
            newDefi.setDescription("Maîtriser les concepts avancés de Java");
            newDefi.setTheme("Programmation");
            newDefi.setImageCover("java.png");
            newDefi.setDateDebut(LocalDate.now());
            newDefi.setDateFin(LocalDate.now().plusDays(60));
            newDefi.setStatut("OUVERT");
            newDefi.setCreateurId(1);
            
            defiDAO.add(newDefi);
            System.out.println("✅ Défi ajouté avec ID: " + newDefi.getId() + "\n");
            
            // 2️⃣ READ - Récupérer tous les défis
            System.out.println("2️⃣  READ - Récupérer tous les défis");
            System.out.println("━".repeat(50));
            List<Defi> tousLesDefis = defiDAO.select();
            System.out.println("📊 Total de défis : " + tousLesDefis.size());
            for (Defi defi : tousLesDefis) {
                System.out.printf("  • [%d] %s (%s) - %s\n", 
                    defi.getId(), defi.getTitre(), defi.getStatut(), defi.getTheme());
            }
            System.out.println();
            
            // 3️⃣ READ - Récupérer les défis ouverts
            System.out.println("3️⃣  READ - Récupérer les défis ouverts");
            System.out.println("━".repeat(50));
            List<Defi> defisOuverts = defiDAO.findOuverts();
            System.out.println("🔓 Défis ouverts : " + defisOuverts.size());
            for (Defi defi : defisOuverts) {
                System.out.printf("  • %s (difficulté: %s)\n", 
                    defi.getTitre(), defi.getStatut());
            }
            System.out.println();
            
            // 4️⃣ READ - Rechercher par thème
            System.out.println("4️⃣  READ - Rechercher par thème");
            System.out.println("━".repeat(50));
            List<Defi> defisProgrammation = defiDAO.findByTheme("Programmation");
            System.out.println("🎯 Défis 'Programmation' : " + defisProgrammation.size());
            for (Defi defi : defisProgrammation) {
                System.out.printf("  • %s\n", defi.getTitre());
            }
            System.out.println();
            
            // 5️⃣ READ - Défis actifs (non expirés)
            System.out.println("5️⃣  READ - Défis actifs (non expirés)");
            System.out.println("━".repeat(50));
            List<Defi> defisActifs = defiDAO.findActifs();
            System.out.println("⏰ Défis actifs : " + defisActifs.size());
            for (Defi defi : defisActifs) {
                System.out.printf("  • %s (date fin: %s)\n", 
                    defi.getTitre(), defi.getDateFin());
            }
            System.out.println();
            
            // 6️⃣ READ - Statistiques par statut
            System.out.println("6️⃣  READ - Statistiques par statut");
            System.out.println("━".repeat(50));
            int countOuvert = defiDAO.countByStatut("OUVERT");
            int countTermine = defiDAO.countByStatut("TERMINÉ");
            System.out.printf("  📌 Ouverts: %d\n", countOuvert);
            System.out.printf("  ✓ Terminés: %d\n", countTermine);
            System.out.println();
            
            // 7️⃣ UPDATE - Mettre à jour un défi
            System.out.println("7️⃣  UPDATE - Mettre à jour un défi");
            System.out.println("━".repeat(50));
            if (!defisProgrammation.isEmpty()) {
                Defi defiToUpdate = defisProgrammation.get(0);
                defiToUpdate.setDescription("Description mise à jour!");
                defiDAO.update(defiToUpdate);
                System.out.println("✏️  Défi '" + defiToUpdate.getTitre() + "' mis à jour\n");
            }
            
            // 8️⃣ SEARCH - Recherche avancée
            System.out.println("8️⃣  SEARCH - Recherche avancée");
            System.out.println("━".repeat(50));
            List<Defi> results = defiDAO.searchDefis("Java", "titre");
            System.out.println("🔍 Résultats pour 'Java' : " + results.size());
            for (Defi defi : results) {
                System.out.printf("  • %s (%s)\n", defi.getTitre(), defi.getTheme());
            }
            System.out.println();
            
            // 9️⃣ READ - Trouver un défi spécifique
            System.out.println("9️⃣  READ - Trouver un défi spécifique");
            System.out.println("━".repeat(50));
            if (!tousLesDefis.isEmpty()) {
                Defi defi = defiDAO.findWithParticipations(tousLesDefis.get(0).getId());
                if (defi != null) {
                    System.out.println("📌 Détails du défi:");
                    System.out.printf("   Titre: %s\n", defi.getTitre());
                    System.out.printf("   Description: %s\n", defi.getDescription());
                    System.out.printf("   Thème: %s\n", defi.getTheme());
                    System.out.printf("   Statut: %s\n", defi.getStatut());
                    System.out.printf("   Début: %s\n", defi.getDateDebut());
                    System.out.printf("   Fin: %s\n", defi.getDateFin());
                }
            }
            System.out.println();
            
            // 🔟 DELETE - Supprimer un défi
            System.out.println("🔟 DELETE - Supprimer un défi");
            System.out.println("━".repeat(50));
            if (!tousLesDefis.isEmpty()) {
                int idToDelete = newDefi.getId();
                defiDAO.delete(idToDelete);
                System.out.println("🗑️  Défi #" + idToDelete + " supprimé\n");
            }
            
            // Résumé final
            System.out.println("\n" + "═".repeat(50));
            System.out.println("✅ Tous les exemples CRUD ont été exécutés avec succès!");
            System.out.println("═".repeat(50));
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

