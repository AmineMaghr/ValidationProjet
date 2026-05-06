-- Création de la base de données midgar si elle n'existe pas
CREATE DATABASE IF NOT EXISTS midgar;
USE midgar;

-- Table defi
CREATE TABLE IF NOT EXISTS defi (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    theme VARCHAR(100),
    image_cover VARCHAR(500),
    date_debut DATE,
    date_fin DATE,
    date_limite DATE,
    statut ENUM('OUVERT', 'FERME', 'TERMINE', 'PLANIFIE') DEFAULT 'OUVERT',
    difficulte ENUM('FACILE', 'MOYEN', 'DIFFICILE') DEFAULT 'FACILE',
    createur_id INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour améliorer les performances
CREATE INDEX idx_defi_statut ON defi(statut);
CREATE INDEX idx_defi_theme ON defi(theme);
CREATE INDEX idx_defi_createur ON defi(createur_id);
CREATE INDEX idx_defi_dates ON defi(date_debut, date_fin);
