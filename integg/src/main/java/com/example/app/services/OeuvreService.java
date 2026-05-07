package com.example.app.services;

import com.example.app.dao.OeuvreDAO;
import com.example.app.entities.Oeuvre;
import java.sql.SQLException;
import java.util.List;

public class OeuvreService implements IService<Oeuvre> {

    private OeuvreDAO oeuvreDAO;

    public OeuvreService() {
        oeuvreDAO = new OeuvreDAO();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        oeuvreDAO.add(oeuvre);
        
        // ⭐ Envoyer la notification dans un thread séparé (non bloquant)
        new Thread(() -> {
            try {
                System.out.println("=== [OEUVRE] Nouvelle œuvre ajoutée: " + oeuvre.getTitle());
                NotificationService notificationService = new NotificationService();
                notificationService.notifierNouvelleOeuvreParType(
                    oeuvre.getTitle(),
                    oeuvre.getType(),
                    oeuvre.getCreateurId()
                );
            } catch (Exception e) {
                System.err.println("Erreur notification: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void update(Oeuvre oeuvre) throws SQLException {
        oeuvreDAO.update(oeuvre);
    }

    @Override
    public void delete(int id) throws SQLException {
        oeuvreDAO.delete(id);
    }

    @Override
    public List<Oeuvre> select() throws SQLException {
        return oeuvreDAO.select();
    }

    public Oeuvre findById(int id) throws SQLException {
        return oeuvreDAO.findById(id);
    }
    
    public List<Oeuvre> getCreationsRecentes() throws SQLException {
        return oeuvreDAO.select();
    }

    public List<Oeuvre> searchOeuvres(String search, String type, Integer userId) throws SQLException {
        System.out.println("=== [OEUVRE SERVICE] searchOeuvres - userId: " + userId);
        if (userId != null) {
            List<Oeuvre> all = oeuvreDAO.select();
            List<Oeuvre> filtered = all.stream()
                    .filter(o -> o.getCreateurId() == userId)
                    .collect(java.util.stream.Collectors.toList());
            System.out.println("=== [OEUVRE SERVICE] " + filtered.size() + " œuvres pour userId " + userId);
            return filtered;
        }
        return oeuvreDAO.select();
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<Oeuvre> all = oeuvreDAO.select();
        return all.stream()
                .map(Oeuvre::getType)
                .distinct()
                .toList();
    }

    public void saveImage(int oeuvreId, java.io.File imageFile) throws Exception {
        String uploadsDir = "uploads/oeuvres";
        java.io.File dir = new java.io.File(uploadsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
        String fileName = "oeuvre_" + oeuvreId + "_" + System.currentTimeMillis() + extension;
        java.io.File destFile = new java.io.File(dir, fileName);
        java.nio.file.Files.copy(imageFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        Oeuvre oeuvre = findById(oeuvreId);
        if (oeuvre != null) {
            oeuvre.setImageUrl(destFile.getAbsolutePath());
            update(oeuvre);
        }
    }
}