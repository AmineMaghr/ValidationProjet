<?php
require_once 'C:/xampp/htdocs/projet_adam_final/Controller/recettecontroller.php';
$recetteController = new RecetteController();
$recettes = [];
if ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_POST['ingredients'])) {
    $ingredients = explode(',', $_POST['ingredients']);
    $ingredients = array_map('trim', $ingredients);
    $recettes = $recetteController->getrecettesbyingredients($ingredients);
} else {
    $recettes = $recetteController->getRecettes();
}
