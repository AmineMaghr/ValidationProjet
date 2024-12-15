<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
require_once 'C:\xampp\htdocs\projet_adam_final\config.php';        
include_once 'C:\xampp\htdocs\projet_adam_final\Controller\fournisseur.php';


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['cin_fournisseur'], $_POST['nom'], $_POST['prenom'], $_POST['date_naiss'],$_POST['adresse'], $_POST['email'], $_POST['numero'])){    $cin_fournisseur = $_POST['cin_fournisseur'];
    $nom = $_POST['nom'];
    $prenom = $_POST['prenom'];
    $date_naiss= $_POST['date_naiss'];
    $adresse = $_POST['adresse'];
    $email = $_POST['email'];
    $numero = $_POST['numero'];

   
    $fournisseur = new fournisseurl($cin_fournisseur, $nom, $prenom, $date_naiss,$adresse, $email,$numero);


    $controller = new fournisseurC();
    $controller->ajouter($fournisseur);
    
    
    header('location:../View/index.php');
    }
    else {
        echo "All form fields are required!";
    }
} else {
    echo "No form data submitted!";
}
?>
