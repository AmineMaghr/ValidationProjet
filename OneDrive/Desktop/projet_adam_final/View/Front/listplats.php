<?php
    require_once 'C:\xampp\htdocs\projet_adam_final\Controller\platcontroller.php';
    ini_set('display_errors', 1);
error_reporting(E_ALL);
$platController = new PlatController();
$plats = $platController->getPlats();
?>
<!DOCTYPE html>
<html>
<head>
    <title>List of Plates</title>
</head>
<body>
    <h1>List of Plates</h1>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Prix</th>
            <th>ID Recette</th>
            <th>Actions</th>
        </tr>
        <?php foreach ($plats as $plat): ?>
        <tr>
            <td><?php echo $plat['id_plat']; ?></td>
            <td><?php echo $plat['nom_plat']; ?></td>
            <td><?php echo $plat['prix_plat']; ?></td>
            <td><?php echo $plat['id_recette']; ?></td>
            <td>
                <a href="updatePlate.php?id_plat=<?php echo $plat['id_plat']; ?>">Update</a>
                <a href="deletePlate.php?id_plat=<?php echo $plat['id_plat']; ?>" onclick="return confirm('Are you sure?')">Delete</a>
            </td>
        </tr>
        <?php endforeach; ?>
    </table>
    <a href="addPlate.php">Add a New Plate</a>
</body>
</html>
