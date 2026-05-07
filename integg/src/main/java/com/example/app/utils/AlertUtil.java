package com.example.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import java.util.Optional;

/**
 * Gestionnaire centralisé des alertes et dialogues
 * Fournit des méthodes pour afficher des messages utilisateur
 */
public class AlertUtil {
    
    private static final String DARK_THEME = 
        "-fx-background-color: #1a1a1a; " +
        "-fx-text-fill: #ffffff; " +
        "-fx-font-family: 'Segoe UI'; " +
        "-fx-font-size: 12;";
    
    /**
     * Affiche une alerte d'erreur
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte d'erreur (surcharge)
     */
    public static void showError(String message) {
        showError("❌ Erreur", message);
    }
    
    /**
     * Affiche une alerte d'information
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte d'information (surcharge)
     */
    public static void showInfo(String message) {
        showInfo("ℹ️ Information", message);
    }
    
    /**
     * Affiche une alerte d'avertissement
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte d'avertissement (surcharge)
     */
    public static void showWarning(String message) {
        showWarning("⚠️ Avertissement", message);
    }
    
    /**
     * Affiche une alerte de confirmation
     * Retourne true si utilisateur a cliqué "OK"
     */
    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Affiche une alerte de confirmation (surcharge)
     */
    public static boolean showConfirm(String message) {
        return showConfirm("Confirmation", message);
    }
    
    /**
     * Affiche une alerte de confirmation de suppression
     */
    public static boolean showDeleteConfirm(String itemName) {
        return showConfirm("Confirmer la suppression", 
            "Êtes-vous sûr de vouloir supprimer '" + itemName + "' ?\n" +
            "Cette action est irréversible.");
    }
    
    /**
     * Affiche un dialogue avec une zone de texte éditable
     */
    public static Optional<String> showInputDialog(String title, String headerText, String promptText) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(promptText);
        return dialog.showAndWait();
    }
}



