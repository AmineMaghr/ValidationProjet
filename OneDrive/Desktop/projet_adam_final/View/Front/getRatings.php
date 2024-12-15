<?php
require_once 'C:/xampp/htdocs/projet_adam_final/Controller/ratingcontroller.php';

$id_plat = htmlspecialchars(trim($_GET['id_plat'] ?? ''));

if (!empty($id_plat)) {
    $ratingController = new RatingController();
    $ratings = $ratingController->getAverageRating($id_plat);

    foreach ($ratings as $rating) {
        echo "Rating: " . htmlspecialchars($rating['rating']) . "<br>";
    }
} else {
    echo "Aucun identifiant de plat fourni.";
}
?>
