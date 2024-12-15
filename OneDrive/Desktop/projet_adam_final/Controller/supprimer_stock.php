
<?php
include_once '../controller/stock.php';
$stock=new stockC();
$stock->supprimer($_GET["id_produit"]);
header('location:../View/index.php');
?>