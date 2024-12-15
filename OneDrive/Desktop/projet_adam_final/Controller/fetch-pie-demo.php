<?php

require_once 'C:/xampp/htdocs/projet_adam_final/config.php';



try {
    $sql = "SELECT nom_produit, quantite FROM stock";
    $db = conn::getConnexion();
    $stmt = $db->prepare($sql);
    $stmt->execute();


    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $data = array_column($result, 'quantite'); 
    $labels = array_column($result, 'nom_produit');
    
    echo json_encode(['labels' => $labels, 'data' => $data]);

} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);}
?>
