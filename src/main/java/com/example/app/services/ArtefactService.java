package com.example.app.services;

import com.example.app.dao.ArtefactDAO;
import com.example.app.entities.Artefact;
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
        List<Artefact> list = artefactDAO.select();
        System.out.println("=== [ARTEFACT SERVICE] Total artefacts: " + list.size());
        for (Artefact a : list) {
            System.out.println("  - ID: " + a.getId() + ", Nom: " + a.getName() + 
                               ", Creator ID: " + (a.getCreatedBy() != null ? a.getCreatedBy().getId() : "null"));
        }
        return list;
    }

    public Artefact findById(int id) throws SQLException {
        return artefactDAO.findById(id);
    }

    public List<Artefact> searchArtefacts(String search, String type, Integer userId) throws SQLException {
        List<Artefact> allArtefacts = artefactDAO.select();
        List<Artefact> filtered = new java.util.ArrayList<>();

        System.out.println("=== [ARTEFACT SERVICE] searchArtefacts - userId: " + userId);
        System.out.println("=== [ARTEFACT SERVICE] Total artefacts avant filtre: " + allArtefacts.size());

        for (Artefact artefact : allArtefacts) {
            boolean matches = true;

            if (search != null && !search.trim().isEmpty()) {
                String lowerSearch = search.toLowerCase();
                boolean nameMatches = artefact.getName() != null && artefact.getName().toLowerCase().contains(lowerSearch);
                boolean universeMatches = artefact.getUniverse() != null && artefact.getUniverse().toLowerCase().contains(lowerSearch);
                if (!nameMatches && !universeMatches) {
                    matches = false;
                }
            }

            if (type != null && !type.trim().isEmpty() && !"Tous les types".equals(type)) {
                if (artefact.getType() == null || !artefact.getType().equals(type)) {
                    matches = false;
                }
            }

            if (userId != null && userId > 0) {
                int artefactCreatorId = artefact.getCreatedBy() != null ? artefact.getCreatedBy().getId() : -1;
                if (artefactCreatorId != userId) {
                    matches = false;
                } else {
                    System.out.println("  - Artefact match: " + artefact.getName() + " (Creator: " + artefactCreatorId + ")");
                }
            }

            if (matches) {
                filtered.add(artefact);
            }
        }

        System.out.println("=== [ARTEFACT SERVICE] Artefacts après filtre: " + filtered.size());
        return filtered;
    }

    public List<String> getAvailableTypes() throws SQLException {
        List<Artefact> artefacts = artefactDAO.select();
        List<String> types = new java.util.ArrayList<>();
        for (Artefact artefact : artefacts) {
            if (artefact.getType() != null && !artefact.getType().trim().isEmpty() && !types.contains(artefact.getType())) {
                types.add(artefact.getType());
            }
        }
        return types;
    }

    public void saveImage(int artefactId, java.io.File imageFile) throws Exception {
        String uploadsDir = "uploads/artefacts";
        java.io.File dir = new java.io.File(uploadsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
        String fileName = "artefact_" + artefactId + "_" + System.currentTimeMillis() + extension;
        java.io.File destFile = new java.io.File(dir, fileName);
        java.nio.file.Files.copy(imageFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        Artefact artefact = findById(artefactId);
        if (artefact != null) {
            artefact.setImageUrl(destFile.getAbsolutePath());
            update(artefact);
        }
    }
}