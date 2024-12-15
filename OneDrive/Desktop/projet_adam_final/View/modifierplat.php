<?php
require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
require_once 'C:\xampp\htdocs\projet_adam_final\Controller\platcontroller.php';
ini_set('display_errors', 1);
error_reporting(E_ALL);

$platController = new PlatController();

if (isset($_GET['id'])) {
    $idPlat = $_GET['id'];
    $plat = $platController->getPlatById($idPlat);
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {

        $nom_plat = htmlspecialchars(trim($_POST['nom_plat']));
        $prix_plat = htmlspecialchars(trim($_POST['prix_plat']));
        $id_recette = htmlspecialchars(trim($_POST['id_recette']));
        $url_img = htmlspecialchars(trim($_POST['url_img']));

        $platController->updatePlat($idPlat, [
            'nom_plat' => $nom_plat,
            'prix_plat' => $prix_plat,
            'id_recette' => $id_recette,
            'url_img' => $url_img,
        ]);
        header('Location: index.php');
        exit();
    }
} else {
    echo "Erreur: Aucun ID de plat fourni.";
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
  <title>Modifier un Plat</title>
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
                    <h1 class="h4 text-gray-900 mb-4">Modifier un Plat</h1>
                  </div>
                  <form method="POST" action="">
                    <div class="form-group">
                      <label>Nom Plat</label>
                      <input type="text" class="form-control" name="nom_plat" 
                             value="<?= htmlspecialchars($plat['nom_plat'] ?? '') ?>" required>
                    </div>
                    <div class="form-group">
                      <label>Prix Plat</label>
                      <input type="number" class="form-control" name="prix_plat" placeholder="Prix Plat" step="any"
                             value="<?= htmlspecialchars($plat['prix_plat'] ?? '') ?>" required>
                    </div>
                    <div class="form-group">
                      <label>Id Recette</label>
                      <input type="number" class="form-control" name="id_recette" 
                             value="<?= htmlspecialchars($plat['id_recette'] ?? '') ?>" required>
                    </div>
                    <div class="form-group">
                      <label>URL Image</label>
                      <input type="text" class="form-control" name="url_img" 
                             value="<?= htmlspecialchars($plat['url_img'] ?? '') ?>" required>
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
  <script src="js/valid2.js"></script>
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="js/ruang-admin.min.js"></script>
</body>
</html>
