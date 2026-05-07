package com.example.app.utils;

import java.io.IOException;
import java.util.logging.*;

/**
 * Gestionnaire de logging centralisé pour l'application
 * Fournit des méthodes de log consistantes avec timestamp et couleurs
 */
public class LogUtil {
    
    private static final Logger logger = Logger.getLogger("MidgarApp");
    private static boolean initialized = false;
    
    /**
     * Initialiser le logger une seule fois au démarrage
     */
    public static void init() {
        if (initialized) return;
        
        try {
            // Créer un handler console avec un formatter personnalisé
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            
            // Créer un formatter personnalisé
            Formatter formatter = new LogFormatter();
            consoleHandler.setFormatter(formatter);
            
            // Configurer le logger
            logger.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);
            
            initialized = true;
            info("🚀 Logger initialisé avec succès");
            
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Log de niveau INFO
     */
    public static void info(String message) {
        init();
        logger.info(message);
    }
    
    /**
     * Log de niveau WARNING
     */
    public static void warning(String message) {
        init();
        logger.warning("⚠️  " + message);
    }
    
    /**
     * Log de niveau ERROR/SEVERE
     */
    public static void error(String message) {
        init();
        logger.severe("❌ " + message);
    }
    
    /**
     * Log de niveau ERROR avec exception
     */
    public static void error(String message, Throwable throwable) {
        init();
        logger.log(Level.SEVERE, "❌ " + message, throwable);
    }
    
    /**
     * Log de niveau DEBUG
     */
    public static void debug(String message) {
        init();
        logger.fine("🔍 " + message);
    }
    
    /**
     * Log de niveau TRACE
     */
    public static void trace(String message) {
        init();
        logger.finest("📍 " + message);
    }
    
    /**
     * Log pour les opérations réussies
     */
    public static void success(String message) {
        init();
        logger.info("✅ " + message);
    }
    
    /**
     * Log pour les opérations en cours
     */
    public static void inProgress(String message) {
        init();
        logger.info("⏳ " + message);
    }
    
    /**
     * Formatter personnalisé pour les logs
     */
    public static class LogFormatter extends Formatter {
        private static final String RESET = "\u001B[0m";
        private static final String CYAN = "\u001B[36m";
        private static final String YELLOW = "\u001B[33m";
        private static final String RED = "\u001B[31m";
        
        @Override
        public String format(LogRecord record) {
            String timestamp = String.format("[%tH:%tM:%tS]", 
                record.getMillis(), record.getMillis(), record.getMillis());
            
            String level = record.getLevel().getName();
            String color;
            if (record.getLevel() == Level.SEVERE) {
                color = RED;
            } else if (record.getLevel() == Level.WARNING) {
                color = YELLOW;
            } else {
                color = CYAN;
            }
            
            String message = formatMessage(record);
            
            if (record.getThrown() != null) {
                return String.format("%s %s[%s]%s %s%n%s%n",
                    timestamp, color, level, RESET, message,
                    getStackTrace(record.getThrown()));
            }
            
            return String.format("%s %s[%s]%s %s%n",
                timestamp, color, level, RESET, message);
        }
        
        private String getStackTrace(Throwable throwable) {
            StringBuilder sb = new StringBuilder();
            sb.append(throwable.getClass().getName()).append(": ")
              .append(throwable.getMessage()).append("\n");
            
            StackTraceElement[] elements = throwable.getStackTrace();
            for (int i = 0; i < Math.min(5, elements.length); i++) {
                sb.append("  at ").append(elements[i]).append("\n");
            }
            
            return sb.toString();
        }
    }
}


