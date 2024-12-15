<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 

class RecetteController {
    public function getRecettes() {
        $conn = conn::getConnexion();
        $sql = "SELECT * FROM recettes";

        try {
            $query = $conn->prepare($sql);
            $query->execute();
            return $query->fetchAll();
        } catch (Exception $e) {
            die('Erreur: ' . $e->getMessage());
        }
    }
    public function ajouterRecette($recette) {
        $conn = conn::getConnexion();
        $sql = "INSERT INTO recettes (nom_recette, nombre_ing, instructions_recette) 
                VALUES (:nom_recette, :nombre_ing, :instructions_recette)";

        try {
            $query = $conn->prepare($sql);
            $query->execute([
                ':nom_recette' => $recette['nom_recette'],
                ':nombre_ing' => $recette['nombre_ing'],
                ':instructions_recette' => $recette['instructions_recette']
            ]);

            echo "Recette ajoutée avec succès!";
        } catch (Exception $e) {
            die('Erreur: ' . $e->getMessage());
        }
    }
    public function updateRecette($id, $recette) {
        $conn = conn::getConnexion();
        $sql = "UPDATE recettes SET nom_recette = :nom_recette, nombre_ing = :nombre_ing, 
                instructions_recette = :instructions_recette WHERE id_recette = :id";

        try {
            $query = $conn->prepare($sql);
            $query->execute([
                ':id' => $id,
                ':nom_recette' => $recette['nom_recette'],
                ':nombre_ing' => $recette['nombre_ing'],
                ':instructions_recette' => $recette['instructions_recette']
            ]);
        } catch (Exception $e) {
            die('Erreur: ' . $e->getMessage());
        }
    }

    public function deleteRecette($id) {
        $conn = conn::getConnexion();
        $sql = "DELETE FROM recettes WHERE id_recette = :id";

        try {
            $query = $conn->prepare($sql);
            $query->execute([':id' => $id]);
        } catch (Exception $e) {
            die('Erreur: ' . $e->getMessage());
        }
    }
    public function getRecetteById($id) {
        $conn = conn::getConnexion();
        $sql = "SELECT * FROM recettes WHERE id_recette = :id";

        try {
            $query = $conn->prepare($sql);
            $query->execute([':id' => $id]);
            return $query->fetch();
        } catch (Exception $e) {
            die('Erreur: ' . $e->getMessage());
        }
    }
    // Function to fetch recipes by ingredients
public function getrecettesbyingredients($ingredients) {
    $conn = conn::getConnexion();
    $sql = "SELECT * FROM recettes WHERE ";
    $conditions = [];
    $params = [];

    foreach ($ingredients as $index => $ingredient) {
        $conditions[] = "instructions_recette LIKE :ingredient_$index";
        $params[":ingredient_$index"] = '%' . $ingredient . '%';
    }

    $sql .= implode(' AND ', $conditions);

    try {
        $query = $conn->prepare($sql);
        $query->execute($params);
        return $query->fetchAll();
    } catch (Exception $e) {
        die('Erreur: ' . $e->getMessage());
    }
}
}
?>
