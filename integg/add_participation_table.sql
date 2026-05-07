-- Table participation
CREATE TABLE IF NOT EXISTS participation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description TEXT,
    date_soumission DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
    user_id INT NOT NULL,
    artwork_id INT NULL,
    image_file_name VARCHAR(500),
    defi_id INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (defi_id) REFERENCES defi(id)
);

-- Index pour améliorer les performances
CREATE INDEX idx_participation_user ON participation(user_id);
CREATE INDEX idx_participation_defi ON participation(defi_id);
CREATE INDEX idx_participation_statut ON participation(statut);
CREATE INDEX idx_participation_date ON participation(date_soumission);