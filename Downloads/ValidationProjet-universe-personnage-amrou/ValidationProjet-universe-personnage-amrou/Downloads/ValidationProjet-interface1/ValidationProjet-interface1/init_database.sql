-- ============================================
-- Midgar JavaFX Database Initialization Script
-- Database: midgar37
-- Port: 4306
-- ============================================

-- Create the database
CREATE DATABASE IF NOT EXISTS midgar37;
USE midgar37;

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
);

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
);

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
);

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
);

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
);

-- ============================================
-- Artefact (Product/Shop Item) Table
-- ============================================
CREATE TABLE IF NOT EXISTS artefact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    image_url VARCHAR(500),
    type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type)
);

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
);

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
);

-- ============================================
-- Test Data
-- ============================================

-- Insert test users
INSERT INTO user (username, email, password, role) VALUES
('admin', 'admin@midgar.com', '123456', 'admin'),
('user', 'user@midgar.com', 'password', 'user'),
('testuser', 'test@midgar.com', 'test123', 'user');

-- Insert test universes
INSERT INTO universe (name, genre, short_description, story_context, themes, creator_id) VALUES
('Fantasy Realm', 'Fantasy', 'A magical world filled with adventures', 'This universe contains fantasy elements with magic', 'Fantasy, Magic, Adventure', 1),
('Sci-Fi Galaxy', 'Sci-Fi', 'Space exploration and futuristic technology', 'Explore galaxies', 'Science Fiction, Space', 1),
('Medieval Kingdom', 'Medieval', 'Knights, castles, and ancient legends', 'Experience the age of knights', 'Medieval, History', 2);

-- Insert test oeuvres (works)
INSERT INTO oeuvre (title, description, universe_id) VALUES
('The Last Dragon', 'A tale of the last dragon in the realm', 1),
('Space Odyssey', 'Journey beyond the stars', 2),
('Castle Chronicles', 'Stories from the kingdom', 3);

-- Insert test personnages (characters)
INSERT INTO personnage (name, class_role, history_context, abilities_powers, strength, agility, magic, defense, universe_id) VALUES
('Aragorn', 'Warrior', 'A noble warrior and leader', 'Swordsmanship', 85, 70, 10, 80, 1),
('Captain Nova', 'Commander', 'Commander of the starship', 'Tactics', 60, 65, 0, 50, 2),
('King Arthur', 'Knight', 'The legendary king', 'Excalibur wield', 90, 60, 20, 85, 3);

-- Insert test challenges
INSERT INTO challenge (title, description, difficulty, points) VALUES
('Dragon Slayer', 'Defeat the dragon and save the realm', 'Hard', 50),
('Alien Contact', 'Make first contact with an alien species', 'Medium', 30),
('Quest Master', 'Complete all quests in the kingdom', 'Hard', 75);

-- Insert test artefacts (shop items)
INSERT INTO artefact (name, description, price, type) VALUES
('Excalibur', 'The legendary sword of legends', 999.99, 'Weapon'),
('Mithril Armor', 'Unbreakable armor from ancient times', 749.99, 'Armor'),
('Magic Wand', 'A wand for casting powerful spells', 199.99, 'Accessory'),
('Invisibility Cloak', 'Become invisible to all', 599.99, 'Accessory'),
('Shield of Protection', 'A shield that protects from all harm', 449.99, 'Armor');

-- ============================================
-- Database Summary
-- ============================================
-- Created Tables:
-- 1. user - Stores user account information
-- 2. universe - Stores universe/world information
-- 3. oeuvre - Stores works/creations related to universes
-- 4. personnage - Stores characters in universes
-- 5. challenge - Stores game challenges
-- 6. artefact - Stores shop items/artifacts
-- 7. participation - Tracks user participation in challenges
-- 8. commentaire - Stores user comments on universes
-- ============================================




