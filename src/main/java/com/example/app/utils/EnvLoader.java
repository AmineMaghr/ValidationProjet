package com.example.app.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    
    private static Properties properties = new Properties();
    
    static {
        try {
            // Essayer de charger depuis le fichier .env
            FileInputStream fis = new FileInputStream(".env");
            properties.load(fis);
            fis.close();
            System.out.println("✅ Fichier .env chargé");
            System.out.println("📋 Variables trouvées: " + properties.keySet());
        } catch (IOException e) {
            System.err.println("⚠️ Fichier .env non trouvé: " + e.getMessage());
        }
    }
    
    public static String get(String key) {
        // 1. D'abord chercher dans les variables système
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // 2. Ensuite dans le fichier .env
        value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        System.err.println("⚠️ Variable non trouvée: " + key);
        return null;
    }
    
    public static boolean getBoolean(String key) {
        String value = get(key);
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }
}