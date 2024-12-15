<?php

require_once 'C:\xampp\htdocs\projet_adam_final\config.php';   

try {
    
    $pdo = conn::getConnexion();
    $stmt = $pdo->query("SELECT * FROM fournisseur");
    $rowsf = $stmt->fetchAll();
} catch (PDOException $e) {
    die("Error fetching data: " . $e->getMessage());
}
?>
