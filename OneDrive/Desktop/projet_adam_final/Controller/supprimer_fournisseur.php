
<?php
include_once '../controller/fournisseur.php';
$fournisseur=new fournisseurC();
$fournisseur->supprimer($_GET["id_fournisseur"]);
header('location:../View/index.php');
?>