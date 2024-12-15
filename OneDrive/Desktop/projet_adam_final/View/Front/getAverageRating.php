<?php
function getAverageRating($id_plat) {
    // Include the connection file to get the connection instance
require_once 'C:/xampp/htdocs/projet_adam_final/config.php';  // Make sure the path is correct

    // Get the database connection
    $conn = conn::getConnexion();

    // Ensure the connection is valid
    if (!$conn) {
        return 0; // Return 0 if no valid connection
    }

    $sql = "SELECT AVG(rating) AS average_rating FROM ratings WHERE id_plat = :id_plat";

    try {
        $query = $conn->prepare($sql);
        $query->execute([':id_plat' => $id_plat]);
        $result = $query->fetch();
        
        // Return rounded average rating or 0 if no ratings are available
        return $result['average_rating'] ? round($result['average_rating'], 2) : 0;
    } catch (Exception $e) {
        // Log the error (could log to a file or database for later investigation)
        error_log("Error fetching average rating for plat ID $id_plat: " . $e->getMessage());
        return 0;  // Return 0 if there's an error
    }
}
?>
