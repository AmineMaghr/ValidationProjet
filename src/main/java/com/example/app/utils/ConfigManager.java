package com.example.app.utils;

import java.io.*;
import java.util.Properties;

/**
 * Gestionnaire de configuration centralisé
 * Gère les propriétés d'application et les configurations
 */
public class ConfigManager {
    
    private static ConfigManager instance;
    private Properties properties;
    private static final String CONFIG_FILE = "application.properties";
    
    private ConfigManager() {
        properties = new Properties();
        loadConfiguration();
    }
    
    /**
     * Obtenir l'instance singleton
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Charger la configuration depuis le fichier
     */
    private void loadConfiguration() {
        try {
            File configFile = new File(CONFIG_FILE);
            
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    properties.load(fis);
                    LogUtil.info("✅ Configuration chargée depuis " + CONFIG_FILE);
                }
            } else {
                // Créer un fichier de configuration par défaut
                createDefaultConfiguration();
            }
        } catch (IOException e) {
            LogUtil.error("Erreur lors du chargement de la configuration", e);
            setDefaults();
        }
    }
    
    /**
     * Créer un fichier de configuration par défaut
     */
    private void createDefaultConfiguration() {
        setDefaults();
        saveConfiguration();
    }
    
    /**
     * Définir les valeurs par défaut
     */
    private void setDefaults() {
        // Application
        properties.setProperty("app.name", "Midgar");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.title", "Plateforme de Création Fantasy");
        
        // UI
        properties.setProperty("ui.width", "1200");
        properties.setProperty("ui.height", "800");
        properties.setProperty("ui.theme", "dark");
        
        // Database
        properties.setProperty("db.type", "MySQL");
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/midgar?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        
        // Debug
        properties.setProperty("debug.enabled", "true");
        properties.setProperty("debug.verbose", "false");
        
        // MySQL specific
        properties.setProperty("db.persistent", "true");
        
        LogUtil.info("⚙️  Configuration par défaut appliquée (MySQL)");
    }
    
    /**
     * Sauvegarder la configuration
     */
    public void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Configuration Midgar Application");
            LogUtil.info("✅ Configuration sauvegardée");
        } catch (IOException e) {
            LogUtil.error("Erreur lors de la sauvegarde de la configuration", e);
        }
    }
    
    /**
     * Obtenir une propriété string
     */
    public String getString(String key) {
        return properties.getProperty(key, "");
    }
    
    /**
     * Obtenir une propriété string avec valeur par défaut
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Obtenir une propriété int
     */
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LogUtil.warning("Valeur invalide pour " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Obtenir une propriété boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Obtenir une propriété double
     */
    public double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LogUtil.warning("Valeur invalide pour " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Définir une propriété
     */
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Définir une propriété int
     */
    public void set(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    /**
     * Définir une propriété boolean
     */
    public void set(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    /**
     * Obtenir toutes les propriétés
     */
    public Properties getAll() {
        return (Properties) properties.clone();
    }
    
    /**
     * Vérifier si une clé existe
     */
    public boolean exists(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * Afficher toutes les configurations
     */
    public void printAll() {
        LogUtil.info("📋 Configuration actuelle:");
        properties.forEach((key, value) -> 
            System.out.println("  " + key + " = " + value)
        );
    }
}

