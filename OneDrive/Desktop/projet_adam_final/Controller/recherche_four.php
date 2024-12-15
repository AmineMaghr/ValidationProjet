<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 

if (isset($_POST['searchTerm'])) {
    $searchTerm = '%' . $_POST['searchTerm'] . '%'; 

    try {
        $db = conn::getConnexion(); 
        $sql = "SELECT id_fournisseur, cin_fournisseur, prenom, nom, date_naiss, adresse, numero, email
                FROM fournisseur 
                WHERE prenom LIKE :searchTerm"; 

        $stmt = $db->prepare($sql);
        $stmt->bindParam(':searchTerm', $searchTerm, PDO::PARAM_STR);
        $stmt->execute();

        $result = $stmt->fetchAll(PDO::FETCH_ASSOC); 
        echo json_encode($result);

    } catch (Exception $e) {
        die('Erreur: ' . $e->getMessage());
    }
}
?>
