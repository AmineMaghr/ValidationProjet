<?php
require_once 'C:\xampp\htdocs\projet_adam_final\Controller\platcontroller.php';
$platController = new PlatController();
$idPlat = $_GET['id'] ?? null;
if ($idPlat) {
    $platController->deletePlat($idPlat);
    header('Location: index.php');
    exit;
} else {
    echo "Erreur: ID de plat non fourni.";
    exit;
}
?>
