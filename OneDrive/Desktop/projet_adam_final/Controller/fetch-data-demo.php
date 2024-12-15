<?php
// Include necessary files for database connection
require_once 'C:/xampp/htdocs/projet_adam_final/config.php';

// SQL query to fetch product names and prices
$sql = "SELECT nom_produit, prix_uni FROM stock";
$db = conn::getConnexion();
$stmt = $db->prepare($sql);
$stmt->execute();

// Fetching the results from the database
$result = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Arrays to hold product names and prices
$produits = [];
$prix = [];

// Storing the results in arrays
foreach ($result as $row) {
    $produits[] = $row['nom_produit'];
    $prix[] = $row['prix_uni'];
}

// Return the data as a JSON response
echo json_encode(['produits' => $produits, 'prix' => $prix]);
?>
