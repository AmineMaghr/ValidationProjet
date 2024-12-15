<?php
require_once 'C:\xampp\htdocs\QQQQQ\controller\ratingcontroller.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_plat = htmlspecialchars(trim($_POST['id_plat']));
    $rating = htmlspecialchars(trim($_POST['rating']));

    if (empty($id_plat) || empty($rating)) {
    } else {
        $ratingController = new RatingController();
        $ratingController->addRating($id_plat, $rating);
    }
    header('Location: index.php');
    exit();
}
?>
