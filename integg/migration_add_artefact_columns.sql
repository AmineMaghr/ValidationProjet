-- Migration: Add missing columns to artefacts table
-- Run this on your midgar database

USE midgar;

ALTER TABLE artefacts
ADD COLUMN local_path VARCHAR(500) DEFAULT NULL,
ADD COLUMN web_url VARCHAR(500) DEFAULT NULL;