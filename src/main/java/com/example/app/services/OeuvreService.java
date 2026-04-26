package com.example.app.services;

import com.example.app.entities.Oeuvre;
import com.example.app.dao.OeuvreDAO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OeuvreService implements IService<Oeuvre> {

    private OeuvreDAO oeuvreDAO;

    public OeuvreService() {
        oeuvreDAO = new OeuvreDAO();
    }

    @Override
    public void add(Oeuvre oeuvre) throws SQLException {
        oeuvreDAO.add(oeuvre);
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

    public List<Oeuvre> searchOeuvres(String search, String type, Integer userId) throws SQLException {
        return oeuvreDAO.searchOeuvres(search, type, userId);
    }

    public List<Oeuvre> getCreationsRecentes() throws SQLException {
        return oeuvreDAO.select().stream().limit(4).collect(Collectors.toList());
    }

    public List<String> getAvailableTypes() throws SQLException {
        return oeuvreDAO.getAvailableTypes();
    }

    public void saveImage(int id, File imageFile) throws Exception {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String extension = getFileExtension(imageFile.getName());
        String newFileName = "oeuvre_" + id + "_" + System.currentTimeMillis() + "." + extension;
        Path targetPath = uploadPath.resolve(newFileName);
        
        Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Store absolute path for direct file loading
        String imageUrl = targetPath.toAbsolutePath().toString();
        
        Oeuvre oeuvre = oeuvreDAO.findById(id);
        if (oeuvre != null) {
            oeuvre.setImageUrl(imageUrl);
            oeuvreDAO.update(oeuvre);
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "png";
    }
}