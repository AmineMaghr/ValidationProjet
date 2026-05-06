package com.example.app.utils;

import com.example.app.entities.User;

/**
 * Gestionnaire de session utilisateur.
 * Gère l'utilisateur actuellement connecté et ses permissions.
 */
public class UserSession {
    private static User currentUser;

    // Simulate an always-logged-in user with ID = 1
    public static int getCurrentUserId() {
        return 1;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setId(1);
            currentUser.setUsername("SimulatedUser");
            currentUser.setRole("user");
        }
        return currentUser; // Might return null if not set, but getCurrentUserId() always returns 1
    }

    public static boolean isLoggedIn() {
        return true; // Always return true for simulation
    }

    public static boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static void logout() {
        currentUser = null;
    }

    public static String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "SimulatedUser";
    }
}
