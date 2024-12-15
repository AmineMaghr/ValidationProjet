<?php
session_start();
include_once 'C:/xampp/htdocs/projet_adam_final/Controller/stock.php';

ini_set('display_errors', 1);
error_reporting(E_ALL);

$stockC = new stockC();


if (isset($_POST['fournisseurId'])) {
    $fournisseurId = $_POST['fournisseurId'];
    $searchTerm = isset($_POST['searchTerm']) ? $_POST['searchTerm'] : '';

    
    $list = $stockC->afficherProduit($fournisseurId, $searchTerm);
    
    $_SESSION['list'] = $list;
    error_log("Debug: List returned = " . print_r($list, true));
    
    
    echo json_encode($list);
    exit(); 
}
?>
