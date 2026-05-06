-- Complete MySQL Schema for Midgar (based on DAO/Service analysis)
-- Run: mysql -u root -p midgar < init_database_complete.sql

USE midgar;

-- User table (backticks because USER is reserved)
CREATE TABLE IF NOT EXISTS `user` (
    id INT PRIMARY
