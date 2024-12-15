<?php
class Plat {
    private $id_plat;
    private $nom_plat;
    private $prix_plat;
    private $id_recette;
    private $url_img;
    private $disponible;
    public function __construct($nom_plat, $prix_plat, $id_recette, $url_img, $disponible, $id_plat = null) {
        $this->id_plat = $id_plat;
        $this->nom_plat = $nom_plat;
        $this->prix_plat = $prix_plat;
        $this->id_recette = $id_recette;
        $this->url_img = $url_img;
        $this->disponible = $disponible;
    }

    // Getters
    public function getIdPlat() {
        return $this->id_plat;
    }

    public function getNomPlat() {
        return $this->nom_plat;
    }

    public function getPrixPlat() {
        return $this->prix_plat;
    }

    public function getIdRecette() {
        return $this->id_recette;
    }

    public function getUrlImg() {
        return $this->url_img;
    }

    public function getDisponible() {
        return $this->disponible;
    }

    // Setters
    public function setIdPlat($id_plat) {
        $this->id_plat = $id_plat;
    }

    public function setNomPlat($nom_plat) {
        $this->nom_plat = $nom_plat;
    }

    public function setPrixPlat($prix_plat) {
        $this->prix_plat = $prix_plat;
    }

    public function setIdRecette($id_recette) {
        $this->id_recette = $id_recette;
    }

    public function setUrlImg($url_img) { 
        $this->url_img = $url_img;
    }

    public function setDisponible($disponible) {
        $this->disponible = $disponible;
    }
}
?>
