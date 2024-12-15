<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
require_once 'C:\xampp\htdocs\projet_adam_final\config.php';        
include_once 'C:\xampp\htdocs\projet_adam_final\Controller\stock.php';


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['nom_produit'], $_POST['quantite'], $_POST['unite'], $_POST['date_expir'],$_POST['prix_uni'], $_POST['id_four'], $_POST['dispo'])){    $nom_produit = $_POST['nom_produit'];
    $quantite = $_POST['quantite'];
    $unite = $_POST['unite'];
    $date_expir = $_POST['date_expir'];
    $prix_uni = $_POST['prix_uni'];
    $id_four = $_POST['id_four'];
    $dispo = $_POST['dispo'];

   
    $stock = new stockl($nom_produit, $quantite, $unite, $date_expir,$prix_uni, $id_four, $dispo);


    $controller = new stockC();
    $controller->ajouter($stock);
    
    
    header('location:../View/index.php');
    }
    else {
        echo "All form fields are required!";
    }
} else {
    echo "No form data submitted!";
}
?>
