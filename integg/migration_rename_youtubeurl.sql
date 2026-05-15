-- Migration: Rename youtubeurl to youtube_url in universe table
-- Run this on your midgar database to update existing installations

USE midgar;

-- Rename the column if it exists with the old name
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'midgar'
    AND TABLE_NAME = 'universe'
    AND COLUMN_NAME = 'youtubeurl'
);

SET @alter_sql = IF(@column_exists > 0, 'ALTER TABLE universe CHANGE youtubeurl youtube_url VARCHAR(500);', 'SELECT "Column youtubeurl does not exist, no changes needed." AS message;');

PREPARE stmt FROM @alter_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;