    <?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
    class PlatController {
        public function getPlats() {
            $conn = conn::getConnexion();
            $sql = "SELECT * FROM plats";

            try {
                $query = $conn->prepare($sql);
                $query->execute();
                return $query->fetchAll();
            } catch (Exception $e) {
                die('Erreur: ' . $e->getMessage());
            }
        }

        public function ajouterplat($plat) {
            $conn = conn::getConnexion();
            $checkRecetteSql = "SELECT COUNT(*) FROM recettes WHERE id_recette = :id_recette";
            try {
                $query = $conn->prepare($checkRecetteSql);
                $query->execute([':id_recette' => $plat['id_recette']]);
                $count = $query->fetchColumn();
                if ($count == 0) {
                    echo "Erreur: L'id_recette spécifié n'existe pas dans la table recettes.";
                    return;
                }
                $sql = "INSERT INTO plats (nom_plat, prix_plat, id_recette, url_img) 
                        VALUES (:nom_plat, :prix_plat, :id_recette, :url_img)";
                
                $query = $conn->prepare($sql);
                $query->execute([
                    ':nom_plat' => $plat['nom_plat'],
                    ':prix_plat' => $plat['prix_plat'],
                    ':id_recette' => $plat['id_recette'],
                    ':url_img' => $plat['url_img']
                ]);
        
                echo "Plat ajouté avec succès!";
            } catch (Exception $e) {
                die('Erreur: ' . $e->getMessage());
            }
        }
        public function updatePlat($id, $plat) {
            $conn = conn::getConnexion();
            $sql = "UPDATE plats SET nom_plat = :nom_plat, prix_plat = :prix_plat, 
                    id_recette = :id_recette, url_img = :url_img WHERE id_plat = :id";

            try {
                $query = $conn->prepare($sql);
                $query->execute([
                    ':id' => $id,
                    ':nom_plat' => $plat['nom_plat'],
                    ':prix_plat' => $plat['prix_plat'],
                    ':id_recette' => $plat['id_recette'],
                    ':url_img' => $plat['url_img']
                ]);
            } catch (Exception $e) {
                die('Erreur: ' . $e->getMessage());
            }
        }
        public function deletePlat($id) {
            $conn = conn::getConnexion();
            $sql = "DELETE FROM plats WHERE id_plat = :id";

            try {
                $query = $conn->prepare($sql);
                $query->execute([':id' => $id]);
            } catch (Exception $e) {
                die('Erreur: ' . $e->getMessage());
            }
        }
        public function getPlatById($id) {
            $conn = conn::getConnexion();
            $sql = "SELECT * FROM plats WHERE id_plat = :id";

            try {
                $query = $conn->prepare($sql);
                $query->execute([':id' => $id]);
                return $query->fetch();
            } catch (Exception $e) {
                die('Erreur: ' . $e->getMessage());
            }
        }
    }
    ?>
