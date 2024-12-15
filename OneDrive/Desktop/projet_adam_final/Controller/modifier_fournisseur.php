<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
include_once 'C:\xampp\htdocs\projet_adam_final\Controller\fournisseur.php';
include_once 'C:\xampp\htdocs\projet_adam_final\Model\fournisseurModel.php';
ini_set('display_errors', 1);
error_reporting(E_ALL);
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  
    error_log(print_r($_POST, true));

    if (
        isset($_POST["cin_fournisseur"]) && isset($_POST['prenom']) && isset($_POST['nom']) &&
        isset($_POST['date_naiss']) && isset($_POST['adresse']) &&
        isset($_POST['email']) && isset($_POST['numero'])
    ) {
       
        $fournisseur = new fournisseurl(
            $_POST['cin_fournisseur'],
            $_POST['nom'],
            $_POST['prenom'],
            $_POST['date_naiss'],
            $_POST['adresse'],
            $_POST['email'],
            $_POST['numero']
        );
        $id_fournisseur=htmlspecialchars($_GET['id_fournisseur']);
        $controller = new fournisseurC();
        
        $controller->modifier($fournisseur, $id_fournisseur);
        header('location:../View/index.php');
        //echo "Produit a été modifié! <a href='../view/index.php'>Retour au Dashboard</a>";
        

    } else {
        error_log('Required fields missing.');
    }
}
