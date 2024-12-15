<?php

if (isset($_POST['sortOrder'])) {
    $sortOrder = $_POST['sortOrder'];

    if ($sortOrder !== 'ASC' && $sortOrder !== 'DESC') {
        echo json_encode(['error' => 'Invalid sort order.']);
        exit;
    }

    require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
    $sql = "SELECT id_fournisseur, cin_fournisseur, prenom, nom, date_naiss, adresse, numero, email 
            FROM fournisseur
            ORDER BY date_naiss $sortOrder"; 

    $db = conn::getConnexion();

    try {
        $stmt = $db->query($sql);
        $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo json_encode($result);
    } catch (Exception $e) {
        echo json_encode(['error' => $e->getMessage()]);
    }
}
?>
