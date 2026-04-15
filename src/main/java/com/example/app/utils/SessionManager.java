package com.example.app.utils;

import com.example.app.entities.User;

/**
 * Gestionnaire de session utilisateur.
 * Gère l'utilisateur actuellement connecté et ses permissions.
 */
public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static void logout() {
        currentUser = null;
    }

    public static String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "Invité";
    }
}

