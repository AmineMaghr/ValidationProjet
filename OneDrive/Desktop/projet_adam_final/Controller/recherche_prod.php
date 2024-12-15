<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 

if (isset($_POST['searchTerm'])) {
    $searchTerm = '%' . $_POST['searchTerm'] . '%'; // Add wildcards for partial matching

    // Database connection
    try {
        $db = conn::getConnexion(); // Assuming you have a config class for DB connection
        $sql = "SELECT id_produit, nom_produit, quantite, unite, date_expir, prix_uni, id_four, dispo 
                FROM stock 
                WHERE nom_produit LIKE :searchTerm"; 

        $stmt = $db->prepare($sql);
        $stmt->bindParam(':searchTerm', $searchTerm, PDO::PARAM_STR);
        $stmt->execute();

        $result = $stmt->fetchAll(PDO::FETCH_ASSOC); // Fetch all results as an associative array
        echo json_encode($result); // Return the data as JSON

    } catch (Exception $e) {
        die('Erreur: ' . $e->getMessage());
    }
}
?>
