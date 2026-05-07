-- ============================================
-- Midgar JavaFX Database - Final Integration
-- Database: midgar
-- Port: 3306 (localhost)
-- Combined: Original midgar37 + Shop Workstream
-- ============================================

-- Create the database
CREATE DATABASE IF NOT EXISTS midgar;
USE midgar;

-- ============================================
-- User Table
-- ============================================
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Universe Table
-- ============================================
CREATE TABLE IF NOT EXISTS universe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    short_description TEXT,
    story_context TEXT,
    themes VARCHAR(500),
    banner_image LONGBLOB,
    creator_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_creator (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Oeuvre (Works) Table
-- ============================================
CREATE TABLE IF NOT EXISTS oeuvre (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    universe_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (universe_id) REFERENCES universe(id) ON DELETE CASCADE,
    INDEX idx_universe (universe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Personnage (Character) Table
-- ============================================
CREATE TABLE IF NOT EXISTS personnage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    class_role VARCHAR(255),
    history_context TEXT,
    abilities_powers TEXT,
    strength INT DEFAULT 0,
    agility INT DEFAULT 0,
    magic INT DEFAULT 0,
    defense INT DEFAULT 0,
    portrait_image LONGBLOB,
    universe_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (universe_id) REFERENCES universe(id) ON DELETE CASCADE,
    INDEX idx_universe (universe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Challenge Table
-- ============================================
CREATE TABLE IF NOT EXISTS challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty VARCHAR(50),
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Participation Table (User-Challenge Junction)
-- ============================================
CREATE TABLE IF NOT EXISTS participation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    challenge_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    score INT DEFAULT 0,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE,
    UNIQUE KEY unique_participation (user_id, challenge_id),
    INDEX idx_user (user_id),
    INDEX idx_challenge (challenge_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Commentaire (Comment) Table
-- ============================================
CREATE TABLE IF NOT EXISTS commentaire (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    universe_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (universe_id) REFERENCES universe(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_universe (universe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Defi Table (Challenge Alternative)
-- ============================================
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_defi_statut (statut),
    INDEX idx_defi_theme (theme),
    INDEX idx_defi_createur (createur_id),
    INDEX idx_defi_dates (date_debut, date_fin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- SHOP WORKSTREAM TABLES
-- ============================================

-- ============================================
-- Produit (Product) Table
-- ============================================
CREATE TABLE IF NOT EXISTS produit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_produit VARCHAR(255) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    type_produit VARCHAR(100) NOT NULL,
    quantite_disponible INT NOT NULL DEFAULT 0,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_produit_type (type_produit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Commande (Order) Table
-- ============================================
CREATE TABLE IF NOT EXISTS commande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantite INT NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etat VARCHAR(50) NOT NULL,
    acheteur VARCHAR(255) NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    reference_commande VARCHAR(100) NOT NULL UNIQUE,
    produit_id INT,
    FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE SET NULL,
    UNIQUE KEY uk_reference_commande (reference_commande),
    INDEX idx_commande_produit (produit_id),
    INDEX idx_commande_reference (reference_commande),
    INDEX idx_commande_acheteur (acheteur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Stock Prediction Table
-- ============================================
CREATE TABLE IF NOT EXISTS stock_prediction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    predicted_demand DOUBLE NOT NULL,
    current_stock INT NOT NULL,
    recommended_stock INT NOT NULL,
    confidence DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stock_prediction_product (product_id),
    INDEX idx_stock_prediction_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Shop Automation Event Table
-- ============================================
CREATE TABLE IF NOT EXISTS shop_automation_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_shop_automation_type (event_type),
    INDEX idx_shop_automation_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Test Data
-- ============================================

-- Insert test users
INSERT INTO user (username, email, password, role) VALUES
('admin', 'admin@midgar.com', '123456', 'admin'),
('user', 'user@midgar.com', 'password', 'user'),
('testuser', 'test@midgar.com', 'test123', 'user')
ON DUPLICATE KEY UPDATE id=id;

-- Insert test universes
INSERT INTO universe (name, genre, short_description, story_context, themes, creator_id) VALUES
('Fantasy Realm', 'Fantasy', 'A magical world filled with adventures', 'This universe contains fantasy elements with magic', 'Fantasy, Magic, Adventure', 1),
('Sci-Fi Galaxy', 'Sci-Fi', 'Space exploration and futuristic technology', 'Explore galaxies', 'Science Fiction, Space', 1),
('Medieval Kingdom', 'Medieval', 'Knights, castles, and ancient legends', 'Experience the age of knights', 'Medieval, History', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert test oeuvres (works)
INSERT INTO oeuvre (title, description, universe_id) VALUES
('The Last Dragon', 'A tale of the last dragon in the realm', 1),
('Space Odyssey', 'Journey beyond the stars', 2),
('Castle Chronicles', 'Stories from the kingdom', 3)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Insert test personnages (characters)
INSERT INTO personnage (name, class_role, history_context, abilities_powers, strength, agility, magic, defense, universe_id) VALUES
('Aragorn', 'Warrior', 'A noble warrior and leader', 'Swordsmanship', 85, 70, 10, 80, 1),
('Captain Nova', 'Commander', 'Commander of the starship', 'Tactics', 60, 65, 0, 50, 2),
('King Arthur', 'Knight', 'The legendary king', 'Excalibur wield', 90, 60, 20, 85, 3)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert test challenges
INSERT INTO challenge (title, description, difficulty, points) VALUES
('Dragon Slayer', 'Defeat the dragon and save the realm', 'Hard', 50),
('Alien Contact', 'Make first contact with an alien species', 'Medium', 30),
('Quest Master', 'Complete all quests in the kingdom', 'Hard', 75)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Insert test products for shop
INSERT INTO produit (nom_produit, description, prix, type_produit, quantite_disponible) VALUES
('Sword of Destiny', 'A legendary blade forged in ancient times', 299.99, 'Weapon', 10),
('Mithril Armor', 'Unbreakable armor from the dwarven halls', 749.99, 'Armor', 5),
('Potion of Healing', 'Restores 100 HP when consumed', 49.99, 'Consumable', 50),
('Magic Wand', 'Amplifies spell casting power by 30%', 199.99, 'Accessory', 8),
('Shield of Protection', 'Reduces all damage taken by 20%', 449.99, 'Armor', 7)
ON DUPLICATE KEY UPDATE nom_produit=VALUES(nom_produit);

-- ============================================
-- Database Summary
-- ============================================
-- Created Tables:
-- Core Tables (16):
--   1. user - User account information
--   2. universe - Universes/worlds
--   3. oeuvre - Works/creations
--   4. personnage - Characters
--   5. challenge - Game challenges
--   6. participation - User-Challenge tracking
--   7. commentaire - Comments on universes
--   8. defi - Alternative challenge system
-- Shop Workstream Tables (4):
--   9. produit - Shop products
--   10. commande - Shop orders
--   11. stock_prediction - Demand forecasting
--   12. shop_automation_event - Stock automation events
-- ============================================
