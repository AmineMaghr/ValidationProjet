package com.example.app.services;

import com.example.app.dao.OeuvreDAO;
import com.example.app.entities.Oeuvre;
import javafx.scene.image.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class OeuvreService implements IService<Oeuvre> {

    private static final String SYMFONY_URL = "http://127.0.0.1:8000";
    private static final String UPLOADS_DIR = "uploads/oeuvres";
    private OeuvreDAO oeuvreDAO;

    public OeuvreService() {
        oeuvreDAO = new OeuvreDAO();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        oeuvreDAO.add(oeuvre);
        
        // Envoyer la notification
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
        // Supprimer l'image associée si elle existe
        Oeuvre oeuvre = findById(id);
        if (oeuvre != null && oeuvre.getImageUrl() != null) {
            deleteImageFile(oeuvre.getImageUrl());
        }
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
        List<Oeuvre> oeuvres = oeuvreDAO.select();
        return oeuvres.stream().limit(6).collect(java.util.stream.Collectors.toList());
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
                .filter(t -> t != null && !t.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Sauvegarde l'image et retourne l'URL web
     */
    public String saveImage(int oeuvreId, File imageFile) throws Exception {
        // Créer le dossier s'il n'existe pas
        File dir = new File(UPLOADS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Générer un nom unique
        String extension = getFileExtension(imageFile.getName());
        String fileName = "oeuvre_" + oeuvreId + "_" + System.currentTimeMillis() + extension;
        File destFile = new File(dir, fileName);
        
        // Copier le fichier
        Files.copy(imageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Stocker l'URL web
        String webUrl = "/uploads/oeuvres/" + fileName;
        
        // Mettre à jour l'œuvre dans la base de données
        Oeuvre oeuvre = findById(oeuvreId);
        if (oeuvre != null) {
            oeuvre.setImageUrl(webUrl);
            update(oeuvre);
        }
        
        System.out.println("Image sauvegardée: " + destFile.getAbsolutePath());
        System.out.println("URL web: " + webUrl);
        
        return webUrl;
    }
    
    /**
     * Sauvegarde l'image à partir d'un tableau de bytes
     */
    public String saveImageFromBytes(int oeuvreId, byte[] imageBytes, String extension) throws Exception {
        File dir = new File(UPLOADS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String fileName = "oeuvre_" + oeuvreId + "_" + System.currentTimeMillis() + "." + extension;
        File destFile = new File(dir, fileName);
        
        Files.write(destFile.toPath(), imageBytes);
        
        String webUrl = "/uploads/oeuvres/" + fileName;
        
        Oeuvre oeuvre = findById(oeuvreId);
        if (oeuvre != null) {
            oeuvre.setImageUrl(webUrl);
            update(oeuvre);
        }
        
        return webUrl;
    }
    
    /**
     * Récupère l'image JavaFX à partir de l'URL stockée
     */
    public Image getImage(Oeuvre oeuvre) {
        if (oeuvre == null) return getDefaultImage();
        
        // 1. Priorité au chemin local (pour JavaFX)
        String localPath = oeuvre.getLocalPath();
        if (localPath != null && !localPath.isEmpty()) {
            File localFile = new File(localPath);
            if (localFile.exists()) {
                try {
                    Image img = new Image(localFile.toURI().toString());
                    if (img != null && !img.isError()) {
                        System.out.println("Chargement image locale: " + localPath);
                        return img;
                    }
                } catch (Exception e) {}
            }
        }
        
        // 2. Essayer via l'URL web
        String webUrl = oeuvre.getWebUrl();
        if (webUrl != null && !webUrl.isEmpty()) {
            try {
                String fullUrl = webUrl.startsWith("http") ? webUrl : SYMFONY_URL + webUrl;
                System.out.println("Chargement image depuis Symfony: " + fullUrl);
                return new Image(fullUrl, true);
            } catch (Exception e) {}
        }
        
        // 3. Fallback: ancienne méthode (chemin direct)
        String imageUrl = oeuvre.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                if (imageUrl.startsWith("/uploads/")) {
                    String fullUrl = SYMFONY_URL + imageUrl;
                    return new Image(fullUrl, true);
                } else if (imageUrl.startsWith("http")) {
                    return new Image(imageUrl, true);
                } else {
                    File localFile = new File(imageUrl);
                    if (localFile.exists()) {
                        return new Image(localFile.toURI().toString());
                    }
                }
            } catch (Exception e) {}
        }
        
        return getDefaultImage();
    }
    
    /**
     * Supprime le fichier image
     */
    private void deleteImageFile(String imageUrl) {
        if (imageUrl == null) return;
        
        try {
            // Extraire le nom du fichier
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            File file = new File(UPLOADS_DIR, fileName);
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("Image supprimée: " + fileName + " - " + (deleted ? "OK" : "FAIL"));
            }
        } catch (Exception e) {
            System.err.println("Erreur suppression image: " + e.getMessage());
        }
    }
    
    /**
     * Vérifie si le serveur Symfony est accessible
     */
    public boolean isSymfonyServerAvailable() {
        try {
            java.net.Socket socket = new java.net.Socket();
            socket.connect(new java.net.InetSocketAddress("127.0.0.1", 8000), 3000);
            socket.close();
            return true;
        } catch (Exception e) {
            System.err.println("Serveur Symfony non accessible: " + e.getMessage());
            return false;
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return lastDot > 0 ? filename.substring(lastDot).toLowerCase() : ".jpg";
    }
    
    private Image getDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default-oeuvre.png"));
        } catch (Exception e) {
            return null;
        }
    }
}