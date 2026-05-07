package com.example.app.exceptions;

/**
 * Exception personnalisée pour l'application Midgar
 * Utilisée pour les erreurs métier et de validation
 */
public class AppException extends Exception {
    
    private String errorCode;
    private String userMessage;
    
    /**
     * Constructeur simple avec message
     */
    public AppException(String message) {
        super(message);
        this.userMessage = message;
    }
    
    /**
     * Constructeur avec cause
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.userMessage = message;
    }
    
    /**
     * Constructeur avec code d'erreur
     */
    public AppException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    /**
     * Constructeur complet
     */
    public AppException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    // Getters
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    /**
     * Messages d'erreur prédéfinis
     */
    public static class ValidationError extends AppException {
        public ValidationError(String field, String message) {
            super("VALIDATION_ERROR", 
                  "Erreur de validation : " + field, 
                  field + " : " + message);
        }
    }
    
    public static class DatabaseError extends AppException {
        public DatabaseError(String operation, Throwable cause) {
            super("DATABASE_ERROR", 
                  "Erreur base de données lors de : " + operation,
                  "Une erreur s'est produite. Veuillez réessayer.",
                  cause);
        }
    }
    
    public static class NotFoundError extends AppException {
        public NotFoundError(String resource, int id) {
            super("NOT_FOUND", 
                  resource + " avec ID " + id + " non trouvé",
                  resource + " introuvable.");
        }
    }
    
    public static class UnauthorizedError extends AppException {
        public UnauthorizedError(String resource) {
            super("UNAUTHORIZED", 
                  "Accès non autorisé à : " + resource,
                  "Vous n'avez pas les permissions pour accéder à cette ressource.");
        }
    }
}

