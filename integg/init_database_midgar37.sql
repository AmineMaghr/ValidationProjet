-- ============================================
-- Midgar JavaFX Database - Corrected Schema
-- Database: midgar37
-- Matches code table names used by the app
-- ============================================

CREATE DATABASE IF NOT EXISTS midgar37;
USE midgar37;

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'user',
    nom VARCHAR(255),
    prenom VARCHAR(255),
    avatar VARCHAR(500),
    bio TEXT,
    is_blocked BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT TRUE,
    phone_number VARCHAR(50),
    reset_token VARCHAR(255),
    google_id VARCHAR(255),
    auth_provider VARCHAR(50) DEFAULT 'local',
    face_descriptor TEXT,
    face_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Migration for existing users
ALTER TABLE user ADD COLUMN IF NOT EXISTS nom VARCHAR(255);
ALTER TABLE user ADD COLUMN IF NOT EXISTS prenom VARCHAR(255);
ALTER TABLE user ADD COLUMN IF NOT EXISTS avatar VARCHAR(500);
ALTER TABLE user ADD COLUMN IF NOT EXISTS bio TEXT;
ALTER TABLE user ADD COLUMN IF NOT EXISTS is_blocked BOOLEAN DEFAULT FALSE;
ALTER TABLE user ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT TRUE;
ALTER TABLE user ADD COLUMN IF NOT EXISTS phone_number VARCHAR(50);
ALTER TABLE user ADD COLUMN IF NOT EXISTS reset_token VARCHAR(255);
ALTER TABLE user ADD COLUMN IF NOT EXISTS google_id VARCHAR(255);
ALTER TABLE user ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(50) DEFAULT 'local';
ALTER TABLE user ADD COLUMN IF NOT EXISTS face_descriptor TEXT;
ALTER TABLE user ADD COLUMN IF NOT EXISTS face_enabled BOOLEAN DEFAULT FALSE;


CREATE TABLE IF NOT EXISTS universe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    short_description TEXT,
    story_context TEXT,
    themes VARCHAR(500),
    banner_image LONGBLOB,
    youtubeurl VARCHAR(500),
    creator_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_universe_creator FOREIGN KEY (creator_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Add youtubeurl if it doesn't exist yet (for databases already created without it)
ALTER TABLE universe ADD COLUMN IF NOT EXISTS youtubeurl VARCHAR(500);

CREATE TABLE IF NOT EXISTS oeuvres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    description TEXT,
    date_publication DATE,
    image_url VARCHAR(500),
    author VARCHAR(255),
    created_by_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_oeuvres_creator FOREIGN KEY (created_by_id) REFERENCES user(id) ON DELETE SET NULL,
    CONSTRAINT fk_oeuvres_universe_title UNIQUE (title, author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS artefacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    universe VARCHAR(255),
    origins TEXT,
    powers TEXT,
    rarity VARCHAR(100),
    image_url VARCHAR(500),
    created_by_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_artefacts_creator FOREIGN KEY (created_by_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
    CONSTRAINT fk_personnage_universe FOREIGN KEY (universe_id) REFERENCES universe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty VARCHAR(50),
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS participation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    challenge_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    score INT DEFAULT 0,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_participation_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE,
    UNIQUE KEY unique_participation (user_id, challenge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS commentaire (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    universe_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_commentaire_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_commentaire_universe FOREIGN KEY (universe_id) REFERENCES universe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS defi (
    id INT AUTO_INCREMENT PRIMARY KEY,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_produit VARCHAR(255) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    type_produit VARCHAR(100) NOT NULL,
    quantite_disponible INT NOT NULL DEFAULT 0,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS commandes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantite INT NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etat VARCHAR(50) NOT NULL,
    acheteur VARCHAR(255) NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    reference_commande VARCHAR(100) NOT NULL UNIQUE,
    produit_id INT,
    CONSTRAINT fk_commandes_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS produit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_produit VARCHAR(255) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    type_produit VARCHAR(100) NOT NULL,
    quantite_disponible INT NOT NULL DEFAULT 0,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS commande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantite INT NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etat VARCHAR(50) NOT NULL,
    acheteur VARCHAR(255) NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    reference_commande VARCHAR(100) NOT NULL UNIQUE,
    produit_id INT,
    CONSTRAINT fk_commande_produit FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS stock_prediction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    predicted_demand DOUBLE NOT NULL,
    current_stock INT NOT NULL,
    recommended_stock INT NOT NULL,
    confidence DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS shop_automation_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS favoris (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    oeuvre_id INT,
    artefact_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favoris_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_favoris_oeuvre FOREIGN KEY (oeuvre_id) REFERENCES oeuvres(id) ON DELETE SET NULL,
    CONSTRAINT fk_favoris_artefact FOREIGN KEY (artefact_id) REFERENCES artefacts(id) ON DELETE SET NULL,
    UNIQUE KEY unique_favori_oeuvre (user_id, oeuvre_id),
    UNIQUE KEY unique_favori_artefact (user_id, artefact_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS notifications_envoyees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type VARCHAR(255) NOT NULL,
    contenu_id INT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



INSERT INTO user (username, email, password, role) VALUES
('admin', 'admin@midgar.com', '123456', 'admin'),
('user', 'user@midgar.com', 'password', 'user')
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO universe (name, genre, short_description, story_context, themes, creator_id) VALUES
('Fantasy Realm', 'Fantasy', 'A magical world filled with adventures', 'This universe contains fantasy elements with magic', 'Fantasy, Magic, Adventure', 1),
('Sci-Fi Galaxy', 'Sci-Fi', 'Space exploration and futuristic technology', 'Explore galaxies', 'Science Fiction, Space', 1)
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO oeuvres (title, type, description, date_publication, image_url, author, created_by_id) VALUES
('The Last Dragon', 'Livre', 'A tale of the last dragon in the realm', CURRENT_DATE(), NULL, 'Admin', 1),
('Space Odyssey', 'Livre', 'Journey beyond the stars', CURRENT_DATE(), NULL, 'Admin', 1)
ON DUPLICATE KEY UPDATE title=title;

INSERT INTO personnage (name, class_role, history_context, abilities_powers, strength, agility, magic, defense, universe_id) VALUES
('Aragorn', 'Warrior', 'A noble warrior and leader', 'Swordsmanship', 85, 70, 10, 80, 1),
('Captain Nova', 'Commander', 'Commander of the starship', 'Tactics', 60, 65, 0, 50, 2)
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO challenge (title, description, difficulty, points) VALUES
('Dragon Slayer', 'Defeat the dragon and save the realm', 'Hard', 50),
('Alien Contact', 'Make first contact with an alien species', 'Medium', 30)
ON DUPLICATE KEY UPDATE title=title;

INSERT INTO produit (nom_produit, description, prix, type_produit, quantite_disponible) VALUES
('Sword of Destiny', 'A legendary blade forged in ancient times', 299.99, 'Weapon', 10),
('Mithril Armor', 'Unbreakable armor from the dwarven halls', 749.99, 'Armor', 5)
ON DUPLICATE KEY UPDATE nom_produit=nom_produit;

INSERT INTO defi (titre, description, theme, difficulte, createur_id) VALUES
('Defi 1', 'Example challenge', 'Fantasy', 'FACILE', 1)
ON DUPLICATE KEY UPDATE titre=titre;

INSERT INTO shop_automation_event (event_type, description, status) VALUES
('STOCK_ALERT', 'Stock low on demo product', 'OPEN')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO stock_prediction (product_id, product_name, predicted_demand, current_stock, recommended_stock, confidence) VALUES
(1, 'Sword of Destiny', 12.5, 10, 20, 0.9)
ON DUPLICATE KEY UPDATE id=id;
