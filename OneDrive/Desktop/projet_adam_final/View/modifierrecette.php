<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
require_once 'C:\xampp\htdocs\projet_adam_final\Controller\RecetteController.php';
ini_set('display_errors', 1);
error_reporting(E_ALL);
$recetteController = new RecetteController();
if (isset($_GET['id'])) {
    $idRecette = $_GET['id'];
    $recette = $recetteController->getRecetteById($idRecette);
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $nom_recette = $_POST['nom_recette'];
        $nombre_ing = $_POST['nombre_ing'];
        $instructions_recette = $_POST['instructions_recette'];
        $recetteController->updateRecette($idRecette, [
            'nom_recette' => $nom_recette,
            'nombre_ing' => $nombre_ing,
            'instructions_recette' => $instructions_recette,
        ]);
        header('Location: index.php');
        exit;
    }
} else {
    echo "Erreur: Aucun ID de recette fourni.";
    exit;
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <link href="img/logo/logo.png" rel="icon">
  <title>Modifier une Recette</title>
  <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
  <link href="css/ruang-admin.min.css" rel="stylesheet">
</head>
<body class="bg-gradient-login">
  <div class="container-login">
    <div class="row justify-content-center">
      <div class="col-xl-10 col-lg-12 col-md-9">
        <div class="card shadow-sm my-5">
          <div class="card-body p-0">
            <div class="row">
              <div class="col-lg-12">
                <div class="login-form">
                  <div class="text-center">
                    <h1 class="h4 text-gray-900 mb-4">Modifier une Recette</h1>
                  </div>
                  <form method="POST" action="">
                    <div class="form-group">
                      <label>Nom Recette</label>
                      <input type="text" class="form-control" name="nom_recette" 
                             value="<?= htmlspecialchars($recette['nom_recette'] ?? '') ?>">
                    </div>
                    <div class="form-group">
                      <label>Nombre des Ingrédients</label>
                      <input type="number" class="form-control" name="nombre_ing" 
                             value="<?= htmlspecialchars($recette['nombre_ing'] ?? '') ?>">
                    </div>
                    <div class="form-group">
  <label>Instructions Recette</label>
  <textarea class="form-control" name="instructions_recette" rows="4"><?= htmlspecialchars(trim($recette['instructions_recette'] ?? '')) ?></textarea>
</div>
                    <div class="form-group">
                      <button type="submit" class="btn btn-primary btn-block">Mettre à Jour</button>
                    </div>
                    <hr>
                  </form>
                  <hr>
                  <div class="text-center"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <script src="js\valid1.js"></script>
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="js/ruang-admin.min.js"></script>
</body>
</html>
