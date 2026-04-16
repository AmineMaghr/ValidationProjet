package com.example.app.services;

import com.example.app.entities.Artefact;
import com.example.app.dao.ArtefactDAO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class ArtefactService implements IService<Artefact> {

    private ArtefactDAO artefactDAO;

    public ArtefactService() {
        artefactDAO = new ArtefactDAO();
    }

    @Override
    public void add(Artefact artefact) throws SQLException {
        artefactDAO.add(artefact);
    }

    @Override
    public void update(Artefact artefact) throws SQLException {
        artefactDAO.update(artefact);
    }

    @Override
    public void delete(int id) throws SQLException {
        artefactDAO.delete(id);
    }

    @Override
    public List<Artefact> select() throws SQLException {
        return artefactDAO.select();
    }

    public List<Artefact> findByType(String type) throws SQLException {
        return artefactDAO.findByType(type);
    }

    public Artefact findById(int id) throws SQLException {
        return artefactDAO.findById(id);
    }

    public void saveImage(int id, File imageFile) throws Exception {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = getFileExtension(imageFile.getName());
        String newFileName = "artefact_" + id + "_" + System.currentTimeMillis() + "." + extension;
        Path targetPath = uploadPath.resolve(newFileName);

        Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = targetPath.toAbsolutePath().toString();

        Artefact artefact = artefactDAO.findById(id);
        if (artefact != null) {
            artefact.setImageUrl(imageUrl);
            artefactDAO.update(artefact);
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