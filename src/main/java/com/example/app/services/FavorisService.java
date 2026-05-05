package com.example.app.services;

import com.example.app.dao.FavorisDAO;
import com.example.app.entities.Favoris;
import com.example.app.entities.User;
import com.example.app.utils.UserSession;
import java.sql.SQLException;
import java.util.List;

public class FavorisService {

    private FavorisDAO favorisDAO;
    private UserService userService;

    public FavorisService() {
        this.favorisDAO = new FavorisDAO();
        this.userService = new UserService();
    }

    // Méthodes pour les œuvres
    public boolean isOeuvreFavorite(int oeuvreId) throws SQLException {
        if (!UserSession.isLoggedIn()) return false;
        int userId = UserSession.getCurrentUser().getId();
        Favoris favoris = favorisDAO.findByUserAndOeuvre(userId, oeuvreId);
        return favoris != null;
    }

    public void addFavoriteOeuvre(int oeuvreId) throws SQLException {
        if (!UserSession.isLoggedIn()) return;
        int userId = UserSession.getCurrentUser().getId();
        Favoris favoris = new Favoris();
        favoris.setUserId(userId);
        favoris.setOeuvreId(oeuvreId);
        favoris.setArtefactId(0);
        favorisDAO.add(favoris);
    }

    public void removeFavoriteOeuvre(int oeuvreId) throws SQLException {
        if (!UserSession.isLoggedIn()) return;
        int userId = UserSession.getCurrentUser().getId();
        Favoris favoris = favorisDAO.findByUserAndOeuvre(userId, oeuvreId);
        if (favoris != null) {
            favorisDAO.delete(favoris.getId());
        }
    }

    public List<Favoris> getUserFavoriteOeuvres(int userId) throws SQLException {
        return favorisDAO.findFavoriOeuvresByUser(userId);
    }

    // Méthodes pour les artefacts
    public boolean isArtefactFavorite(int artefactId) throws SQLException {
        if (!UserSession.isLoggedIn()) return false;
        int userId = UserSession.getCurrentUser().getId();
        Favoris favoris = favorisDAO.findByUserAndArtefact(userId, artefactId);
        return favoris != null;
    }

    public void addFavoriteArtefact(int artefactId) throws SQLException {
        if (!UserSession.isLoggedIn()) return;
        int userId = UserSession.getCurrentUser().getId();
        Favoris favoris = new Favoris();
        favoris.setUserId(userId);
        favoris.setOeuvreId(0);
        favoris.setArtefactId(artefactId);
        favorisDAO.add(favoris);
    }

    public void removeFavoriteArtefact(int artefactId) throws SQLException {
        if (!UserSession.isLoggedIn()) return;
        int userId = UserSession.getCurrentUser().getId();
        Favoris favoris = favorisDAO.findByUserAndArtefact(userId, artefactId);
        if (favoris != null) {
            favorisDAO.delete(favoris.getId());
        }
    }

    public List<Favoris> getUserFavoriteArtefacts(int userId) throws SQLException {
        return favorisDAO.findFavoriArtefactsByUser(userId);
    }

    // ⭐ NOUVELLE MÉTHODE : Récupérer les types d'œuvres favoris d'un utilisateur
    public List<String> getUserFavoriteOeuvreTypes(int userId) throws SQLException {
        return favorisDAO.findFavoriteOeuvreTypesByUser(userId);
    }
    
    // ⭐ NOUVELLE MÉTHODE : Récupérer les types d'artefacts favoris d'un utilisateur
    public List<String> getUserFavoriteArtefactTypes(int userId) throws SQLException {
        return favorisDAO.findFavoriteArtefactTypesByUser(userId);
    }

    // Méthodes utilitaires
    public int getOeuvreLikesCount(int oeuvreId) throws SQLException {
        return favorisDAO.countOeuvreLikes(oeuvreId);
    }

    public int getArtefactLikesCount(int artefactId) throws SQLException {
        return favorisDAO.countArtefactLikes(artefactId);
    }
}