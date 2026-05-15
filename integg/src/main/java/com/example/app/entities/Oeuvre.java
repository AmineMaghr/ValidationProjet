package com.example.app.entities;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Oeuvre {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty type = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleStringProperty imageUrl = new SimpleStringProperty();      // Nom du fichier
    private final SimpleStringProperty localPath = new SimpleStringProperty();     // Chemin local JavaFX
    private final SimpleStringProperty webUrl = new SimpleStringProperty();        // URL web
    private final SimpleStringProperty author = new SimpleStringProperty();
    private final SimpleIntegerProperty createurId = new SimpleIntegerProperty();
    private LocalDate datePublication;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User createdBy;
    private Universe universe;
    private final SimpleStringProperty datePublicationDisplay = new SimpleStringProperty();
    private List<String> validationErrors = new ArrayList<>();
    
    // Dossiers
    private static final String SHARED_UPLOADS_DIR = "C:/midgar_shared/uploads/oeuvres";
    private static final String SYMFONY_URL = "http://127.0.0.1:8000";
    
    private transient Image cachedImage;

    public Oeuvre() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.datePublication = LocalDate.now();
        updateDisplayDates();
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(Paths.get(SHARED_UPLOADS_DIR));
        } catch (Exception e) {
            System.err.println("Erreur création dossier: " + e.getMessage());
        }
    }

    private void updateDisplayDates() {
        if (datePublication != null) {
            datePublicationDisplay.set(datePublication.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    public boolean isValid() {
        validationErrors.clear();
        if (getTitle() == null || getTitle().trim().isEmpty()) validationErrors.add("Le titre est requis");
        if (getType() == null || getType().trim().isEmpty()) validationErrors.add("Le type est requis");
        if (getAuthor() == null || getAuthor().trim().isEmpty()) validationErrors.add("L'auteur est requis");
        if (getDescription() == null || getDescription().trim().isEmpty()) validationErrors.add("La description est requise");
        return validationErrors.isEmpty();
    }
    
    public List<String> getValidationErrors() { return validationErrors; }

    // ==================== GESTION DES IMAGES ====================
    
    public String saveImage(File imageFile) {
        try {
            String extension = getFileExtension(imageFile.getName());
            String fileName = "oeuvre_" + System.currentTimeMillis() + "_" + 
                              UUID.randomUUID().toString().substring(0, 13) + "." + extension;
            
            Path destination = Paths.get(SHARED_UPLOADS_DIR, fileName);
            Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            // Stocker les 3 chemins
            setImageUrl(fileName);
            setLocalPath(destination.toString());
            setWebUrl("/uploads/oeuvres/" + fileName);
            
            refreshImage();
            
            System.out.println("✅ Image sauvegardée:");
            System.out.println("   ImageUrl: " + getImageUrl());
            System.out.println("   LocalPath: " + getLocalPath());
            System.out.println("   WebUrl: " + getWebUrl());
            
            return fileName;
        } catch (Exception e) {
            System.err.println("❌ Erreur sauvegarde: " + e.getMessage());
            return null;
        }
    }
    
    public Image getImage() {
        if (cachedImage != null) return cachedImage;
        
        // 1. Priorité au chemin local
        String local = getLocalPath();
        if (local != null && !local.isEmpty()) {
            File localFile = new File(local);
            if (localFile.exists()) {
                try {
                    cachedImage = new Image(localFile.toURI().toString());
                    if (cachedImage != null && !cachedImage.isError()) {
                        return cachedImage;
                    }
                } catch (Exception e) {}
            }
        }
        
        // 2. Essayer via le nom du fichier
        String fileName = getImageUrl();
        if (fileName != null && !fileName.isEmpty()) {
            File uploadFile = new File(SHARED_UPLOADS_DIR, fileName);
            if (uploadFile.exists()) {
                try {
                    cachedImage = new Image(uploadFile.toURI().toString());
                    if (cachedImage != null && !cachedImage.isError()) {
                        return cachedImage;
                    }
                } catch (Exception e) {}
            }
        }
        
        // 3. Dernier recours: via l'URL web
        String web = getWebUrl();
        if (web != null && !web.isEmpty()) {
            try {
                String fullUrl = web.startsWith("http") ? web : SYMFONY_URL + web;
                cachedImage = new Image(fullUrl, true);
                return cachedImage;
            } catch (Exception e) {}
        }
        
        cachedImage = getDefaultImage();
        return cachedImage;
    }
    
    public boolean hasImage() {
        return (getImageUrl() != null && !getImageUrl().isEmpty()) ||
               (getLocalPath() != null && !getLocalPath().isEmpty());
    }
    
    public void refreshImage() {
        cachedImage = null;
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "png";
    }
    
    private Image getDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default.png"));
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== GETTERS ET SETTERS ====================
    
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String imageUrl) { this.imageUrl.set(imageUrl); }

    public String getLocalPath() { return localPath.get(); }
    public void setLocalPath(String localPath) { this.localPath.set(localPath); }

    public String getWebUrl() { return webUrl.get(); }
    public void setWebUrl(String webUrl) { this.webUrl.set(webUrl); }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }

    public int getCreateurId() { return createurId.get(); }
    public void setCreateurId(int createurId) { this.createurId.set(createurId); }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
        updateDisplayDates();
    }

    public String getDatePublicationDisplay() { return datePublicationDisplay.get(); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Universe getUniverse() { return universe; }
    public void setUniverse(Universe universe) { this.universe = universe; }
    
    @Override
    public String toString() {
        return title.get() + " - " + author.get();
    }
    // Ajoutez cette méthode dans la classe Oeuvre (vers ligne 150-160)

/**
 * Supprime l'image du disque
 */
public void deleteImage() {
    // Supprimer le fichier local
    String local = getLocalPath();
    if (local != null && !local.isEmpty()) {
        try {
            File file = new File(local);
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("Image locale supprimée: " + local + " - " + (deleted ? "OK" : "FAIL"));
            }
        } catch (Exception e) {
            System.err.println("Erreur suppression image locale: " + e.getMessage());
        }
    }
    
    // Supprimer le fichier dans le dossier partagé
    String fileName = getImageUrl();
    if (fileName != null && !fileName.isEmpty()) {
        try {
            Path path = Paths.get(SHARED_UPLOADS_DIR, fileName);
            Files.deleteIfExists(path);
            System.out.println("Image partagée supprimée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur suppression image partagée: " + e.getMessage());
        }
    }
    
    // Réinitialiser les chemins
    setImageUrl(null);
    setLocalPath(null);
    setWebUrl(null);
    refreshImage();
}
}