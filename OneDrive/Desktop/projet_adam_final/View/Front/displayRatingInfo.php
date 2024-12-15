<?php
function displayRatingInfo($average_rating, $latest_rating) {
    $output = '';
    
    // Display the average rating
    if ($average_rating > 0) {
        $output .= '<p class="card-text">Average Rating: ' . number_format($average_rating, 2) . ' ⭐</p>';
    } else {
        $output .= '<p class="card-text">No ratings yet</p>';
    }
    
    // Display the latest rating
    if ($latest_rating !== null) {
        $output .= '<p class="card-text">Latest Rating: ' . $latest_rating . ' ⭐</p>';
    } else {
        $output .= '<p class="card-text">No ratings yet</p>';
    }
    
    return $output;
}

?>