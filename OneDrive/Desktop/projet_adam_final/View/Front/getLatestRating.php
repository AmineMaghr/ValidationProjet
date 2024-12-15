<?php

function getLatestRating($id_plat) {
    $conn = conn::getConnexion();
    $sql = "SELECT rating FROM ratings WHERE id_plat = :id_plat ORDER BY id_rating DESC LIMIT 1";
    
    try {
        $query = $conn->prepare($sql);
        $query->execute([':id_plat' => $id_plat]);
        return $query->fetchColumn();  // Returns the latest rating or null if no ratings
    } catch (Exception $e) {
        echo 'Error: ' . $e->getMessage();
        return null;  // Return null if there's an error or no latest rating
    }
}

?>