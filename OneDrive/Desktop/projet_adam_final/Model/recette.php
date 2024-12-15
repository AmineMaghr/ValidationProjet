<?php
class Recette {
    private $id_recette;
    private $nom_recette;
    private $nombre_ing;
    private $instructions_recette;

    // Constructor to initialize all fields
    public function __construct($nom_recette, $nombre_ing, $instructions_recette, $id_recette = null) {
        $this->id_recette = $id_recette;
        $this->nom_recette = $nom_recette;
        $this->nombre_ing = $nombre_ing;
        $this->instructions_recette = $instructions_recette;
    }

    // Getters
    public function getIdRecette() {
        return $this->id_recette;
    }

    public function getNomRecette() {
        return $this->nom_recette;
    }

    public function getNombreIng() {
        return $this->nombre_ing;
    }

    public function getInstructionsRecette() {
        return $this->instructions_recette;
    }

    // Setters
    public function setIdRecette($id_recette) {
        $this->id_recette = $id_recette;
    }

    public function setNomRecette($nom_recette) {
        $this->nom_recette = $nom_recette;
    }

    public function setNombreIng($nombre_ing) {
        $this->nombre_ing = $nombre_ing;
    }

    public function setInstructionsRecette($instructions_recette) {
        $this->instructions_recette = $instructions_recette;
    }
}
?>
