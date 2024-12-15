<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
try {
    $pdo = conn::getConnexion();
    $stmt = $pdo->query("SELECT * FROM plats");
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    die("Error fetching data: " . $e->getMessage());
}
?>
