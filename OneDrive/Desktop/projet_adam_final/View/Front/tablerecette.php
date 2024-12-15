<?php
  require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
require_once 'C:\xampp\htdocs\Controller\RecetteController.php';

// Create an instance of RecetteController
$recetteController = new RecetteController();

// Fetch all recettes
$recettes = $recetteController->getRecettes();
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recettes Back Office</title>
    <!-- Include Bootstrap for styling -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="col-xl-9 col-lg-7 mb-4">
        <div class="card">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Recettes</h6>
                <a class="m-0 float-right btn btn-danger btn-sm" href="ajouterrecette.php" target="_blank">
                    Ajouter Recette <i class="fas fa-chevron-right"></i>
                </a>
            </div>
            <div class="table-responsive">
                <table class="table align-items-center table-flush">
                    <thead class="thead-light">
                        <tr>
                            <th>Recette ID</th>
                            <th>Nom Recette</th>
                            <th>Nombre d'Ingrédients</th>
                            <th>Instructions Recette</th>
                            <th>Edit</th>
                            <th>Delete</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($recettes as $recette): ?>
                            <tr>
                                <td><?php echo $recette['id_recette']; ?></td>
                                <td><?php echo $recette['nom_recette']; ?></td>
                                <td><?php echo $recette['nombre_ing']; ?></td>
                                <td><?php echo $recette['instructions_recette']; ?></td>
                                <td>
                                    <a href="editrecette.php?id=<?php echo $recette['id_recette']; ?>" class="btn btn-primary btn-sm">Edit</a>
                                </td>
                                <td>
                                    <a href="deleterecette.php?id=<?php echo $recette['id_recette']; ?>" 
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Are you sure you want to delete this recette?');">
                                       Delete
                                    </a>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<!-- Include Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
