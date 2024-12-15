<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
include_once 'C:\xampp\htdocs\projet_adam_final\Controller\stock.php';
include_once 'C:\xampp\htdocs\projet_adam_final\Model\stockModel.php';
ini_set('display_errors', 1);
error_reporting(E_ALL);
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  
    error_log(print_r($_POST, true));

    if (
        isset($_POST["nom_produit"]) && isset($_POST['quantite']) && isset($_POST['unite']) &&
        isset($_POST['date_expir']) && isset($_POST['prix_uni']) &&
        isset($_POST['id_four']) && isset($_POST['dispo'])
    ) {
       
        $stock = new stockl(
            $_POST['nom_produit'],
            $_POST['quantite'],
            $_POST['unite'],
            $_POST['date_expir'],
            $_POST['prix_uni'],
            $_POST['id_four'],
            $_POST['dispo']
        );
        $id_produit=htmlspecialchars($_GET['id_produit']);
        $controller = new stockC();
        
        $controller->modifier($stock, $id_produit);
        header('location:../View/index.php');
        //echo "Produit a été modifié! <a href='../view/index.php'>Retour au Dashboard</a>";
        

    } else {
        error_log('Required fields missing.');
    }
}
