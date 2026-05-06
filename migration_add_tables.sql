-- Migration: add tables required by oeuvre/admin/artefact modules
-- Run this on your midgar37 database ONCE

USE midgar37;

-- --------------------------------------------------------
-- Patch `user` table to match the security/admin module
-- (adds all columns expected by UserDAO)
-- --------------------------------------------------------
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS `prenom` varchar(100) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `nom` varchar(100) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `role` varchar(50) NOT NULL DEFAULT 'user',
  ADD COLUMN IF NOT EXISTS `avatar` varchar(500) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `bio` text DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `is_blocked` tinyint(1) NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `is_verified` tinyint(1) NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `phone_number` varchar(20) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `auth_provider` varchar(50) NOT NULL DEFAULT 'local',
  ADD COLUMN IF NOT EXISTS `google_id` varchar(255) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `face_enabled` tinyint(1) NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `face_descriptor` text DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `face_label` int(11) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `reset_token` varchar(255) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `reset_token_expires_at` datetime DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `reset_code` varchar(10) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `reset_code_expires_at` datetime DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp();


-- --------------------------------------------------------
-- advanced_preferences
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `advanced_preferences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `free_description` text DEFAULT NULL,
  `favorite_genre` varchar(100) DEFAULT NULL,
  `affinity_level` int(11) DEFAULT 0,
  `favorite_themes` text DEFAULT NULL,
  `custom_tags` text DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- produit (needed by commande)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `produit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nom_produit` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `prix` double DEFAULT 0,
  `quantite_stock` int(11) DEFAULT 0,
  `image_url` varchar(500) DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- commande
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `commande` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quantite` int(11) NOT NULL DEFAULT 1,
  `date_commande` datetime DEFAULT current_timestamp(),
  `etat` varchar(50) DEFAULT 'EN_ATTENTE',
  `acheteur` varchar(255) DEFAULT NULL,
  `prix_total` double DEFAULT 0,
  `reference_commande` varchar(100) DEFAULT NULL,
  `produit_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `produit_id` (`produit_id`),
  CONSTRAINT `commande_ibfk_1` FOREIGN KEY (`produit_id`) REFERENCES `produit` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- commentaires (note: your DB has 'commentaire' singular — this is a separate table)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `commentaires` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contenu` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `user_id` int(11) DEFAULT NULL,
  `oeuvre_id` int(11) DEFAULT NULL,
  `artefact_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `oeuvre_id` (`oeuvre_id`),
  KEY `artefact_id` (`artefact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- favoris
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `favoris` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `oeuvre_id` int(11) DEFAULT NULL,
  `artefact_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `oeuvre_id` (`oeuvre_id`),
  KEY `artefact_id` (`artefact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- notifications_envoyees
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `notifications_envoyees` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `contenu_id` int(11) DEFAULT NULL,
  `date_envoi` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Add missing columns to defi if needed
-- --------------------------------------------------------
ALTER TABLE `defi`
  ADD COLUMN IF NOT EXISTS `titre` varchar(255) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `theme` varchar(100) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `image_cover` varchar(500) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `date_debut` date DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `date_fin` date DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `date_limite` date DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `statut` varchar(50) DEFAULT 'OUVERT',
  ADD COLUMN IF NOT EXISTS `createur_id` int(11) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp();
