<?php
require_once 'C:\xampp\htdocs\projet_adam_final\Controller\recettecontroller.php';  // Ensure the path to your file is correct

// Create an instance of RecetteController
$recetteController = new RecetteController();

// Get the `id_recette` from the query string
$idRecette = $_GET['id'] ?? null;

// Ensure `id_recette` is provided
if ($idRecette) {
    // Call the deleteRecette method to remove the recette
    $recetteController->deleteRecette($idRecette);

    // Redirect to the list page after deletion
    header('Location: index.php');
    exit;
} else {
    // Handle the case where no ID is provided
    echo "Erreur: ID de recette non fourni.";
    exit;
}
?>
