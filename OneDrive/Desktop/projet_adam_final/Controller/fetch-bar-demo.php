<?php
// Include necessary files for database connection
require_once 'C:/xampp/htdocs/projet_adam_final/config.php';


$sql = "SELECT f.prenom, f.nom, COUNT(p.id_produit) AS nombre_produits
                       FROM fournisseur f
                       LEFT JOIN stock p ON f.id_fournisseur = p.id_four
                       GROUP BY f.prenom, f.nom";
$db = conn::getConnexion();
$stmt = $db->prepare($sql);
$stmt->execute();

// Fetching the results from the database
$result = $stmt->fetchAll(PDO::FETCH_ASSOC);

$data = [];
foreach ($result as $row) {
    $data[] = $row;
}

// Convertir les données en JSON pour les envoyer à JavaScript
echo json_encode($data);
?>
