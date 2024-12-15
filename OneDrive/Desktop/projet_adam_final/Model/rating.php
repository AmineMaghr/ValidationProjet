<?php
class Rating {
    private $id_rating;
    private $id_plat;
    private $rating;

    // Constructor
    public function __construct($id_plat, $rating, $id_rating = null) {
        $this->id_rating = $id_rating;
        $this->id_plat = $id_plat;
        $this->rating = $rating;
    }

    // Getters
    public function getIdRating() {
        return $this->id_rating;
    }

    public function getIdPlat() {
        return $this->id_plat;
    }

    public function getRating() {
        return $this->rating;
    }

    // Setters
    public function setIdRating($id_rating) {
        $this->id_rating = $id_rating;
    }

    public function setIdPlat($id_plat) {
        $this->id_plat = $id_plat;
    }

    public function setRating($rating) {
        $this->rating = $rating;
    }
}
?>
