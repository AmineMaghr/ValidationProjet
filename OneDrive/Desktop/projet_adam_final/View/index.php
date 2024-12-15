
<?php
  include_once 'C:/xampp/htdocs/projet_adam_final/Controller/afficher_stock.php';
  include_once 'C:/xampp/htdocs/projet_adam_final/Controller/afficher_fournisseur.php';
  include_once 'C:/xampp/htdocs/projet_adam_final/Controller/fournisseur.php';
 include_once 'C:/xampp/htdocs/projet_adam_final/Controller/afficher_prod_four.php';
 include_once 'C:/xampp/htdocs/projet_adam_final/Controller/stock.php';
 require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
 require_once 'C:/xampp/htdocs/projet_adam_final/Controller/platcontroller.php';
require_once 'C:/xampp/htdocs/projet_adam_final/Controller/recettecontroller.php';

 $platController = new PlatController();
$recetteController = new RecetteController();

// Fetch data
$plats = $platController-> getPlats();
$recettes = $recetteController->getRecettes();
  ?>
  
  
  
 
  <?php

$list = isset($_SESSION['list']) ? $_SESSION['list'] : [];
?>
<!DOCTYPE html>
<html lang="en">
<?php $fournisseurC = new fournisseurC(); 
$fournisseur=$fournisseurC->afficherFournisseur();?>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <link href="img/logo/logo.png" rel="icon" >
  <title>SAHA PREP - Dashboard</title>
  <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
  <link href="css/ruang-admin.min.css" rel="stylesheet">
</head>

<body id="page-top">
  <div id="wrapper">
    <!-- Sidebar -->
    <ul class="navbar-nav sidebar sidebar-light accordion" id="accordionSidebar">
      <a class="sidebar-brand d-flex align-items-center justify-content-center" href="index.html">
        <div class="sidebar-brand-icon">
        <img src="img/logo/logo.png"  width="150" height="150" alt="logo" class="img-fluid">
        </div>
        <div class="sidebar-brand-text mx-3">SAHA PREP</div>
      </a>
      <hr class="sidebar-divider my-0">
      <li class="nav-item active">
        <a class="nav-link" href="index.html">
          <i class="fas fa-fw fa-tachometer-alt"></i>
          <span>Dashboard</span></a>
      </li>
      <hr class="sidebar-divider">
      <div class="sidebar-heading">
        Features
      </div>
      <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseBootstrap"
          aria-expanded="true" aria-controls="collapseBootstrap">
          <i class="far fa-fw fa-window-maximize"></i>
          <span>Bootstrap UI</span>
        </a>
        <div id="collapseBootstrap" class="collapse" aria-labelledby="headingBootstrap" data-parent="#accordionSidebar">
          <div class="bg-white py-2 collapse-inner rounded">
            <h6 class="collapse-header">Bootstrap UI</h6>
            <a class="collapse-item" href="alerts.html">Alerts</a>
            <a class="collapse-item" href="buttons.html">Buttons</a>
            <a class="collapse-item" href="dropdowns.html">Dropdowns</a>
            <a class="collapse-item" href="modals.html">Modals</a>
            <a class="collapse-item" href="popovers.html">Popovers</a>
            <a class="collapse-item" href="progress-bar.html">Progress Bars</a>
          </div>
        </div>
      </li>
      <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseForm" aria-expanded="true"
          aria-controls="collapseForm">
          <i class="fab fa-fw fa-wpforms"></i>
          <span>Forms</span>
        </a>
        <div id="collapseForm" class="collapse" aria-labelledby="headingForm" data-parent="#accordionSidebar">
          <div class="bg-white py-2 collapse-inner rounded">
            <h6 class="collapse-header">Forms</h6>
            <a class="collapse-item" href="form_basics.html">Form Basics</a>
            <a class="collapse-item" href="form_advanceds.html">Form Advanceds</a>
          </div>
        </div>
      </li>
      <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTable" aria-expanded="true"
          aria-controls="collapseTable">
          <i class="fas fa-fw fa-table"></i>
          <span>Tables</span>
        </a>
        <div id="collapseTable" class="collapse" aria-labelledby="headingTable" data-parent="#accordionSidebar">
          <div class="bg-white py-2 collapse-inner rounded">
            <h6 class="collapse-header">Tables</h6>
            <a class="collapse-item" href="formfournisseur.html">Ajouter un fournisseur</a>
            <a class="collapse-item" href="formstock.php">Ajouter au stock</a>
          </div>
        </div>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="ui-colors.html">
          <i class="fas fa-fw fa-palette"></i>
          <span>UI Colors</span>
        </a>
      </li>
      <hr class="sidebar-divider">
      <div class="sidebar-heading">
        Examples
      </div>
      <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapsePage" aria-expanded="true"
          aria-controls="collapsePage">
          <i class="fas fa-fw fa-columns"></i>
          <span>Pages</span>
        </a>
        <div id="collapsePage" class="collapse" aria-labelledby="headingPage" data-parent="#accordionSidebar">
          <div class="bg-white py-2 collapse-inner rounded">
            <h6 class="collapse-header">Example Pages</h6>
            <a class="collapse-item" href="login.html">Login</a>
            <a class="collapse-item" href="register.html">Register</a>
            <a class="collapse-item" href="404.html">404 Page</a>
            <a class="collapse-item" href="blank.html">Blank Page</a>
          </div>
        </div>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="charts.php">
          <i class="fas fa-fw fa-chart-area"></i>
          <span>Charts</span>
        </a>
      </li>
      <hr class="sidebar-divider">
      <div class="version" id="version-ruangadmin"></div>
    </ul>
    <!-- Sidebar -->
    <div id="content-wrapper" class="d-flex flex-column">
      <div id="content">
        <!-- TopBar -->
        <nav class="navbar navbar-expand navbar-light bg-navbar topbar mb-4 static-top">
          <button id="sidebarToggleTop" class="btn btn-link rounded-circle mr-3">
            <i class="fa fa-bars"></i>
          </button>
          <ul class="navbar-nav ml-auto">
            <li class="nav-item dropdown no-arrow">
              <a class="nav-link dropdown-toggle" href="#" id="searchDropdown" role="button" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-search fa-fw"></i>
              </a>
              <div class="dropdown-menu dropdown-menu-right p-3 shadow animated--grow-in"
                aria-labelledby="searchDropdown">
                <form class="navbar-search">
                  <div class="input-group">
                    <input type="text" class="form-control bg-light border-1 small" placeholder="What do you want to look for?"
                      aria-label="Search" aria-describedby="basic-addon2" style="border-color: #3f51b5;">
                    <div class="input-group-append">
                      <button class="btn btn-primary" type="button">
                        <i class="fas fa-search fa-sm"></i>
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </li>
            <li class="nav-item dropdown no-arrow mx-1">
              <a class="nav-link dropdown-toggle" href="#" id="alertsDropdown" role="button" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-bell fa-fw"></i>
                <span class="badge badge-danger badge-counter">3+</span>
              </a>
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow animated--grow-in"
                aria-labelledby="alertsDropdown">
                <h6 class="dropdown-header">
                  Alerts Center
                </h6>
                <a class="dropdown-item d-flex align-items-center" href="#">
                  <div class="mr-3">
                    <div class="icon-circle bg-primary">
                      <i class="fas fa-file-alt text-white"></i>
                    </div>
                  </div>
                  <div>
                    <div class="small text-gray-500">December 12, 2019</div>
                    <span class="font-weight-bold">A new monthly report is ready to download!</span>
                  </div>
                </a>
                <a class="dropdown-item d-flex align-items-center" href="#">
                  <div class="mr-3">
                    <div class="icon-circle bg-success">
                      <i class="fas fa-donate text-white"></i>
                    </div>
                  </div>
                  <div>
                    <div class="small text-gray-500">December 7, 2019</div>
                    $290.29 has been deposited into your account!
                  </div>
                </a>
                <a class="dropdown-item d-flex align-items-center" href="#">
                  <div class="mr-3">
                    <div class="icon-circle bg-warning">
                      <i class="fas fa-exclamation-triangle text-white"></i>
                    </div>
                  </div>
                  <div>
                    <div class="small text-gray-500">December 2, 2019</div>
                    Spending Alert: We've noticed unusually high spending for your account.
                  </div>
                </a>
                <a class="dropdown-item text-center small text-gray-500" href="#">Show All Alerts</a>
              </div>
            </li>
            <li class="nav-item dropdown no-arrow mx-1">
              <a class="nav-link dropdown-toggle" href="#" id="messagesDropdown" role="button" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-envelope fa-fw"></i>
                <span class="badge badge-warning badge-counter">2</span>
              </a>
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow animated--grow-in"
                aria-labelledby="messagesDropdown">
                <h6 class="dropdown-header">
                  Message Center
                </h6>
                <a class="dropdown-item d-flex align-items-center" href="#">
                  <div class="dropdown-list-image mr-3">
                    <img class="rounded-circle" src="img/man.png" style="max-width: 60px" alt="">
                    <div class="status-indicator bg-success"></div>
                  </div>
                  <div class="font-weight-bold">
                    <div class="text-truncate">Hi there! I am wondering if you can help me with a problem I've been
                      having.</div>
                    <div class="small text-gray-500">Udin Cilok · 58m</div>
                  </div>
                </a>
                <a class="dropdown-item d-flex align-items-center" href="#">
                  <div class="dropdown-list-image mr-3">
                    <img class="rounded-circle" src="img/girl.png" style="max-width: 60px" alt="">
                    <div class="status-indicator bg-default"></div>
                  </div>
                  <div>
                    <div class="text-truncate">Am I a good boy? The reason I ask is because someone told me that people
                      say this to all dogs, even if they aren't good...</div>
                    <div class="small text-gray-500">Jaenab · 2w</div>
                  </div>
                </a>
                <a class="dropdown-item text-center small text-gray-500" href="#">Read More Messages</a>
              </div>
            </li>
            <li class="nav-item dropdown no-arrow mx-1">
              <a class="nav-link dropdown-toggle" href="#" id="messagesDropdown" role="button" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-tasks fa-fw"></i>
                <span class="badge badge-success badge-counter">3</span>
              </a>
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow animated--grow-in"
                aria-labelledby="messagesDropdown">
                <h6 class="dropdown-header">
                  Task
                </h6>
                <a class="dropdown-item align-items-center" href="#">
                  <div class="mb-3">
                    <div class="small text-gray-500">Design Button
                      <div class="small float-right"><b>50%</b></div>
                    </div>
                    <div class="progress" style="height: 12px;">
                      <div class="progress-bar bg-success" role="progressbar" style="width: 50%" aria-valuenow="50"
                        aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                  </div>
                </a>
                <a class="dropdown-item align-items-center" href="#">
                  <div class="mb-3">
                    <div class="small text-gray-500">Make Beautiful Transitions
                      <div class="small float-right"><b>30%</b></div>
                    </div>
                    <div class="progress" style="height: 12px;">
                      <div class="progress-bar bg-warning" role="progressbar" style="width: 30%" aria-valuenow="30"
                        aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                  </div>
                </a>
                <a class="dropdown-item align-items-center" href="#">
                  <div class="mb-3">
                    <div class="small text-gray-500">Create Pie Chart
                      <div class="small float-right"><b>75%</b></div>
                    </div>
                    <div class="progress" style="height: 12px;">
                      <div class="progress-bar bg-danger" role="progressbar" style="width: 75%" aria-valuenow="75"
                        aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                  </div>
                </a>
                <a class="dropdown-item text-center small text-gray-500" href="#">View All Taks</a>
              </div>
            </li>
            <div class="topbar-divider d-none d-sm-block"></div>
            <li class="nav-item dropdown no-arrow">
              <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false">
                <img class="img-profile rounded-circle" src="img/adam_admin.png" style="max-width: 60px">
                <span class="ml-2 d-none d-lg-inline text-white small">Adam Ouederni</span>
              </a>
              <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="userDropdown">
                <a class="dropdown-item" href="#">
                  <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                  Profile
                </a>
                <a class="dropdown-item" href="#">
                  <i class="fas fa-cogs fa-sm fa-fw mr-2 text-gray-400"></i>
                  Settings
                </a>
                <a class="dropdown-item" href="#">
                  <i class="fas fa-list fa-sm fa-fw mr-2 text-gray-400"></i>
                  Activity Log
                </a>
                <div class="dropdown-divider"></div>
                <a class="dropdown-item" href="javascript:void(0);" data-toggle="modal" data-target="#logoutModal">
                  <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                  Logout
                </a>
              </div>
            </li>
          </ul>
        </nav>
        <!-- Topbar -->

        <!-- Container Fluid-->
        <div class="container-fluid" id="container-wrapper">
          <div class="d-sm-flex align-items-center justify-content-between mb-4">
            <h1 class="h3 mb-0 text-gray-800">Dashboard</h1>
            <ol class="breadcrumb">
              <li class="breadcrumb-item"><a href="./">Home</a></li>
              <li class="breadcrumb-item active" aria-current="page">Dashboard</li>
            </ol>
          </div>

          <div class="row mb-3">
            <!-- Earnings (Monthly) Card Example -->
            <div class="col-xl-3 col-md-6 mb-4">
              <div class="card h-100">
                <div class="card-body">
                  <div class="row align-items-center">
                    <div class="col mr-2">
                      <div class="text-xs font-weight-bold text-uppercase mb-1">La Somme Toatle Payée</div>
                      <div class="h5 mb-0 font-weight-bold text-gray-800"> <?php

    $total_value = 0;

    foreach ($rows as $row) {
        $total_value += $row['quantite'] * $row['prix_uni'];
    }

  
    echo number_format($total_value, 2);
    ?></div>
                    </div>
                    <div class="col-auto">
                      <i class="fas fa-calendar fa-2x text-primary"></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- Earnings (Annual) Card Example -->
            <div class="col-xl-3 col-md-6 mb-4">
              <div class="card h-100">
                <div class="card-body">
                  <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                      <div class="text-xs font-weight-bold text-uppercase mb-1">Nombre des Produits</div>
                      <div class="h5 mb-0 font-weight-bold text-gray-800"><?php echo count($rows)?></div>
                      
                    </div>
                    <div class="col-auto">
                      <i class="fas fa-shopping-cart fa-2x text-success"></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- New User Card Example -->
            <div class="col-xl-3 col-md-6 mb-4">
              <div class="card h-100">
                <div class="card-body">
                  <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                      <div class="text-xs font-weight-bold text-uppercase mb-1">Nombre des Fournisseurs</div>
                      <div class="h5 mb-0 mr-3 font-weight-bold text-gray-800"><?php echo count($rowsf)?></div>
                      
                    </div>
                    <div class="col-auto">
                      <i class="fas fa-users fa-2x text-info"></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- Pending Requests Card Example -->
            <div class="col-xl-3 col-md-6 mb-4">
              <div class="card h-100">
                <div class="card-body">
                  <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                      <div class="text-xs font-weight-bold text-uppercase mb-1">Pending Requests</div>
                      <div class="h5 mb-0 font-weight-bold text-gray-800">18</div>
                      <div class="mt-2 mb-0 text-muted text-xs">
                        <span class="text-danger mr-2"><i class="fas fa-arrow-down"></i> 1.10%</span>
                        <span>Since yesterday</span>
                      </div>
                    </div>
                    <div class="col-auto">
                      <i class="fas fa-comments fa-2x text-warning"></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Area Chart -->
            <div class="col-xl-8 col-md-6 mb-4">
              <div class="card mb-4">
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold text-primary">Les prix Des Produits</h6>
                  <div class="dropdown no-arrow">
                    <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown"
                      aria-haspopup="true" aria-expanded="false">
                      <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right shadow animated--fade-in"
                      aria-labelledby="dropdownMenuLink">
                      <div class="dropdown-header">Dropdown Header:</div>
                      <a class="dropdown-item" href="#">Action</a>
                      <a class="dropdown-item" href="#">Another action</a>
                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item" href="#">Something else here</a>
                    </div>
                  </div>
                </div>
                <div class="card-body">
                  <div class="chart-area">
                    <canvas id="myAreaChart"></canvas>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-xl-8 col-md-6 mb-4">
              <div class="card mb-4">
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold text-primary">Les Quantités Des Produits</h6>
                  <div class="dropdown no-arrow">
                    <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown"
                      aria-haspopup="true" aria-expanded="false">
                      <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right shadow animated--fade-in"
                      aria-labelledby="dropdownMenuLink">
                      <div class="dropdown-header">Dropdown Header:</div>
                      <a class="dropdown-item" href="#">Action</a>
                      <a class="dropdown-item" href="#">Another action</a>
                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item" href="#">Something else here</a>
                    </div>
                  </div>
                </div>
                <div class="card-body">
                  <div class="chart-area">
                    <canvas id="myPieChart"></canvas>
                  </div>
                </div>
              </div>
            </div>
            <!-- bar chart -->
            <div class="col-xl-8 col-lg-7">
              <div class="card mb-4">
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold text-primary">Le nombre de produits par Fournisseur</h6>
                  <div class="dropdown no-arrow">
                    <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown"
                      aria-haspopup="true" aria-expanded="false">
                      <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right shadow animated--fade-in"
                      aria-labelledby="dropdownMenuLink">
                      <div class="dropdown-header">Dropdown Header:</div>
                      <a class="dropdown-item" href="#">Action</a>
                      <a class="dropdown-item" href="#">Another action</a>
                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item" href="#">Something else here</a>
                    </div>
                  </div>
                </div>
                <div class="card-body">
                  <div class="chart-area">
                    <canvas id="myBarChart"></canvas>
                  </div>
                </div>
              </div>
            </div>








                
            <div class="col-xl-9 col-lg-7 mb-4">
  <div class="card">
    <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
      <h6 class="m-0 font-weight-bold text-primary">Tableau des Plats</h6>
      <a class="m-0 float-right btn btn-danger btn-sm" href="ajouterplat.php">
        Ajouter Plat <i class="fas fa-chevron-right"></i>
      </a>
    </div>
    <div class="table-responsive">
      <table class="table table-bordered table-hover text-center">
        <thead class="thead-light">
          <tr>
            <th>Nom PLAT</th>
            <th>Prix plat</th>
            <th>ID Recette</th>
            <th>Edit</th>
            <th>Delete</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($plats as $plat): ?>
            <tr>
              <td><a href="#"><?= htmlspecialchars($plat['nom_plat']); ?></a></td>
              <td><?= htmlspecialchars($plat['prix_plat']); ?></td>
              <td><?= htmlspecialchars($plat['id_recette']); ?></td>
              <td>
                <a href="modifierplat.php?id=<?= $plat['id_plat']; ?>" class="btn btn-sm btn-warning">
                  Modifier
                </a>
              </td>
              <td>
                <a href="supprimerplat.php?id=<?= $plat['id_plat']; ?>" class="btn btn-sm btn-danger">
                  Supprimer
                </a>
              </td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </div>
</div>


























<div class="col-xl-9 col-lg-7 mb-4">
  <div class="card">
    <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
      <h6 class="m-0 font-weight-bold text-primary">Tableau Recettes</h6>
      <a class="m-0 float-right btn btn-danger btn-sm" href="ajouterrecette.php">
        Ajouter Recette <i class="fas fa-chevron-right"></i>
      </a>
    </div>
    <div class="table-responsive">
      <table class="table table-bordered table-hover text-center">
        <thead class="thead-light">
          <tr>
            <th>Nom Recette</th>
            <th>Nombre d'Ingrédients</th>
            <th>Description Recette</th>
            <th>Edit</th>
            <th>Delete</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($recettes as $recette): ?>
            <tr>
              <td><a href="#"><?= htmlspecialchars($recette['nom_recette']); ?></a></td>
              <td><?= htmlspecialchars($recette['nombre_ing']); ?></td>
              <td><?= htmlspecialchars($recette['instructions_recette']); ?></td>
              <td>
                <a href="modifierrecette.php?id=<?= $recette['id_recette']; ?>" class="btn btn-sm btn-warning">
                  Modifier
                </a>
              </td>
              <td>
                <a href="supprimerrecette.php?id=<?= $recette['id_recette']; ?>" class="btn btn-sm btn-danger">
                  Supprimer
                </a>
              </td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </div>
</div>



















            
            <!-- Invoice Example -->
            <div class="col-xl-8 col-lg-7 mb-4">
              <div class="card">
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold text-primary">Stock</h6>
                  
                </div>
                <div class="table-responsive">
                <button id="sortAsc" class="btn btn-sm btn-primary">Tri descendant</button>
                <button id="sortDesc" class="btn btn-sm btn-primary">Tri ascendant</button>
                <input type="text" id="searchInput" placeholder="Recherche..." />
                  <table class="table align-items-center table-flush">
                    <thead class="thead-light">
                      <tr>
                        <th>Nom_produit</th>
                        <th>Quantité</th>
                        <th>Unité</th>
                        <th>Date_expiration</th>
                        <th>Prix_unitaire</th>
                        <th>Fournisseur</th>
                        <th>Disponibilité</th>
                        <th></th>
                        <th></th>
                        
                      </tr>
                    </thead>
                    <tbody id="bodyprod">
                      <?php foreach ($rows as $row): ?>
                      <tr>
                        <td><a href="#"><?= $row['nom_produit']; ?></a></td>
                        <td><?= $row['quantite']; ?></td>
                        <td><?= $row['unite']; ?></td>
                        <td><span class="badge <?= (($auj = new DateTime()) < ($expird = new DateTime($row['date_expir']))) ? 'badge-success' : 'badge-danger'; ?>"><?= $row['date_expir']; ?></span></td>
                        <td><a href="#" class="btn btn-sm btn-primary"><?= $row['prix_uni']; ?></a></td>
                        <td><a href="#">

                        <?php
        // Assuming you have a connection through PDO
        $pdo = conn::getConnexion();
        $id_four = $row['id_four']; // The ID of the supplier from $row

        // Prepare the query to fetch supplier details
        $query = "SELECT nom, prenom FROM fournisseur WHERE id_fournisseur = :id_fournisseur";
        $stmt = $pdo->prepare($query);

        // Bind the parameter to prevent SQL injection
        $stmt->bindParam(':id_fournisseur', $id_four, PDO::PARAM_INT);

        $stmt->execute();

        // Check if there are results
        if ($stmt->rowCount() > 0) {
        
            $rowi = $stmt->fetch(PDO::FETCH_ASSOC);
            $nom = $rowi['nom'];
            $prenom = $rowi['prenom'];

            // Display the name and surname
            echo $prenom . " " . $nom ;
        } else {
            echo "Fournisseur non trouvé.";
        }
        ?>



                        </a></td>
                        <td><span class="badge <?= ($row['dispo'] == 'Oui') ? 'badge-success' : 'badge-danger'; ?>">
                        <?= $row['dispo']; ?></span></td>
                        <td><button class="btn btn-sm btn-primary badge-warning"><a style="color:white"href="modifierstock.php?id_produit=<?=$row['id_produit'];?>&nom_produit=<?= $row['nom_produit']; ?>&quantite=<?= $row['quantite']; ?>&unite=<?= $row['unite']; ?>&date_expir=<?= $row['date_expir']; ?>&prix_uni=<?= $row['prix_uni']; ?>&id_four=<?= $row['id_four']; ?>&dispo=<?= $row['dispo']; ?>"> Modifier</a></button></td>
                          <td><button class="btn btn-sm btn-primary badge-danger"><a style="color:white" href="../Controller/supprimer_stock.php?id_produit=<?=$row['id_produit']; ?>">Supprimer</a></button></td>
                      </tr>
                      <?php endforeach; ?>
                      
                    </tbody>
                    
                  </table>
                </div>
                <button for="stock" class="badge badge-success" style="border:0;"><a href="formstock.php" style="color:white">Ajouter dans le stock</a></button>
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold text-primary">Fournisseurs</h6>
                  
                </div>
                <div class="table-responsive">
                <button id="sortAscf" class="btn btn-sm btn-primary">Tri descendant</button>
                <button id="sortDescf" class="btn btn-sm btn-primary">Tri ascendant</button>
                <input type="text" id="searchfour" placeholder="Recherche..." /> 
                  <table class="table align-items-center table-flush">
                    <thead class="thead-light">
                      <tr>
                        <th>CIN_fournisseur</th>
                        <th>Prénom</th>
                        <th>Nom</th>
                        <th>Date_de_naissance</th>
                        <th>Adresse</th>
                        <th>Numéro</th>
                        <th>email</th>
      
                        <th></th>
                        <th></th>
                        <th></th>
                        
                      </tr>
                    </thead>
                    <tbody id="bodyf">
                    <?php foreach ($rowsf as $row): ?>
                      <tr>
                        <td><a href="#"><?= $row['cin_fournisseur']; ?></a></td>
                        <td><?= $row['prenom']; ?></td>
                        <td><?= $row['nom']; ?></td>
                        <td><span class="badge badge-success"><?= $row['date_naiss']; ?></span></td>
                        <td><?= $row['adresse']; ?></td>
                        <td><?= $row['numero']; ?></td>
                        <td><span class="badge badge-success"><?= $row['email']; ?></span></td>
                        <td><button class="btn btn-sm btn-primary badge-warning"><a style="color:white"href="modifierfournisseur.php?id_fournisseur=<?=$row['id_fournisseur'];?>&cin_fournisseur=<?= $row['cin_fournisseur']; ?>&prenom=<?= $row['prenom']; ?>&nom=<?= $row['nom']; ?>&date_naiss=<?= $row['date_naiss']; ?>&adresse=<?= $row['adresse']; ?>&email=<?= $row['email']; ?>&numero=<?= $row['numero']; ?>">Modifier</a></td>
                        <td><button class="btn btn-sm btn-primary badge-danger"><a style="color:white" href="../Controller/supprimer_fournisseur.php?id_fournisseur=<?=$row['id_fournisseur']; ?>">Supprimer</a></button></td>
                        <td><button class="btn btn-sm btn-primary" ><a style="color:white" href="../View/contacter_fournisseur.php?email=<?=$row['email']; ?>&prenom=<?=$row['prenom']?>">Contacter</a></button></td>
                      </tr>
                      <?php endforeach; ?>
                    </tbody>
                    
                  </table>
                </div>
                <button for="fournisseur" class="badge badge-success" style="border:0;"><a href="formfournisseur.html" style="color:white">Ajouter un fournisseur</a></button>





                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold text-primary">Recherche des produits par fournisseur:</h6>
                  
                </div>
                
                <div class="table-responsive">
                <?php
$lastfour = isset($_SESSION['lastfour']) ? $_SESSION['lastfour'] : null;
?>
                  <!-- <form method="post" action="../Controller/afficher_prod_four.php"> -->
                  <label>Sélectionner un fournisseur</label>
                  <select class="table align-items-center table-flush"  id="choix" name="choix">
                    <option>Choisir un fournisseur</option>
                    <?php foreach($fournisseur as $fournisseur):?>
                    
                      <option value="<?= htmlspecialchars($fournisseur['id_fournisseur']); ?>">
    <?= htmlspecialchars($fournisseur['prenom'] . " " . $fournisseur['nom']); ?>
</option>
                      <?php endforeach; ?>
                    </select>
                      
                      
                </div>
                <!-- <button for="rechercher" class="badge badge-success" style="border:0;">Rechercher</button>
                    </form> -->

                <div class="table-responsive">
                
                  <table class="table align-items-center table-flush">
                 
                    <thead class="thead-light">
                
                      <tr>
                        <th>Nom_produit</th>
                        <th>Quantité</th>
                        <th>Unité</th>
                        <th>Date_expiration</th>
                        <th>Prix_unitaire</th>
                        <th>Founisseur</th>
                        <th>Disponibilité</th>
                        <th></th>
                        <th></th>
                        
                      </tr>
                    
                    </thead>
                    <?php




$_SESSION['list'] = [];


?>
                    <tbody id="rechprod">
                      
    <?php
    $list = isset($_SESSION['list']) ? $_SESSION['list'] : [];  
    ?>
    <?php if (!empty($list)) : ?>
        <?php foreach ($list as $row): ?>
            <tr>
                <td><a href="#"><?= htmlspecialchars($row['nom_produit']); ?></a></td>
                <td><?= htmlspecialchars($row['quantite']); ?></td>
                <td><?= htmlspecialchars($row['unite']); ?></td>
                <td>
                    <span class="badge <?= (($auj = new DateTime()) < ($expird = new DateTime($row['date_expir']))) ? 'badge-success' : 'badge-danger'; ?>">
                        <?= htmlspecialchars($row['date_expir']); ?>
                    </span>
                </td>
                <td><a href="#" class="btn btn-sm btn-primary"><?= htmlspecialchars($row['prix_uni']); ?></a></td>
                <td><a href="#"><?= htmlspecialchars($row['id_four']); ?></a></td>
                <td><span class="badge <?= ($row['dispo'] == 'Oui') ? 'badge-success' : 'badge-danger'; ?>">
                    <?= htmlspecialchars($row['dispo']); ?>
                </span></td>
                <td><button class="btn btn-sm btn-primary badge-warning">
                    <a style="color:white" href="modifierstock.php?id_produit=<?= urlencode($row['id_produit']); ?>&nom_produit=<?= urlencode($row['nom_produit']); ?>&quantite=<?= urlencode($row['quantite']); ?>&unite=<?= urlencode($row['unite']); ?>&date_expir=<?= urlencode($row['date_expir']); ?>&prix_uni=<?= urlencode($row['prix_uni']); ?>&id_four=<?= urlencode($row['id_four']); ?>&dispo=<?= urlencode($row['dispo']); ?>">Modifier</a></button></td>
                <td><button class="btn btn-sm btn-primary badge-danger">
                    <a style="color:white" href="../Controller/supprimer_stock.php?id_produit=<?= urlencode($row['id_produit']); ?>">Supprimer</a></button></td>
            </tr>
        <?php endforeach; ?>
    <?php else: ?>
        <tr><td colspan="9">No products found</td></tr>
    <?php endif; ?>
</tbody>

                    
                  </table>
                  
                  
                </div>


                <div class="card-footer"></div>
              </div>
              
              
            </div>
            
          </div>
          <!--Row-->

          <div class="row">
            <div class="col-lg-12 text-center">
              
            </div>
          </div>

          <!-- Modal Logout -->
          <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabelLogout"
            aria-hidden="true">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="exampleModalLabelLogout">Ohh No!</h5>
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                  </button>
                </div>
                <div class="modal-body">
                  <p>Are you sure you want to logout?</p>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-outline-primary" data-dismiss="modal">Cancel</button>
                  <a href="formstock.html" class="btn btn-primary">Logout</a>
                </div>
              </div>
            </div>
          </div>

        </div>
        <!---Container Fluid-->
      </div>
      <!-- Footer -->
      <footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="copyright text-center my-auto">
            <span>copyright &copy; <script> document.write(new Date().getFullYear()); </script> - developed by
              <b>GOATS</b>
            </span>
          </div>
        </div>
      </footer>
      <!-- Footer -->
    </div>
  </div>

  <!-- Scroll to top -->
  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>
 
  
  
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="js/ruang-admin.min.js"></script>
  <script src="vendor/chart.js/Chart.min.js"></script>
  <script src="js/demo/chart-area-demo.js"></script>  
  <script src="js/demo/chart-bar-demo.js"></script>  
  <script src="js/demo/chart-pie-demo.js"></script>  
  <!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> -->
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- Bootstrap JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
<script>
$(document).ready(function () {
    $('#sortAsc').on('click', function () {
        fetchSortedData('ASC'); 
    });

    $('#sortDesc').on('click', function () {
        fetchSortedData('DESC');
    });

    function fetchSortedData(order) {
        $.ajax({
            url:'../Controller/sort_stock.php', 
            type: 'POST',
            data: { sortOrder: order }, 
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.error) {
                    resultHtml = `<tr><td colspan="8">Erreur : ${data.error}</td></tr>`;
                } else {
                    data.forEach(function (row) {
                        const badgeClass = (new Date(row.date_expir) > new Date()) ? 'badge-success' : 'badge-danger';
                        const dispoClass = row.dispo === 'Oui' ? 'badge-success' : 'badge-danger';
                        resultHtml += `<tr>
                            <td><a href="#">${row.nom_produit}</a></td>
                            <td>${row.quantite}</td>
                            <td>${row.unite}</td>
                            <td><span class="badge ${badgeClass}">${row.date_expir}</span></td>
                            <td><a href="#" class="btn btn-sm btn-primary">${row.prix_uni}</a></td>
                            <td><a href="#">${row.id_four}</a></td>
                            <td><span class="badge ${dispoClass}">${row.dispo}</span></td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-warning">
                                    <a style="color:white" href="modifierstock.php?id_produit=${row.id_produit}&nom_produit=${row.nom_produit}&quantite=${row.quantite}&unite=${row.unite}&date_expir=${row.date_expir}&prix_uni=${row.prix_uni}&id_four=${row.id_four}&dispo=${row.dispo}">
                                        Modifier
                                    </a>
                                </button>
                            </td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-danger">
                                    <a style="color:white" href="../Controller/supprimer_stock.php?id_produit=${row.id_produit}">Supprimer</a>
                                </button>
                            </td>
                        </tr>`;
                    });
                }
                $('#bodyprod').html(resultHtml); 
            },
            error: function (xhr, status, errorThrown) {
    console.error('AJAX Error:', errorThrown);
    console.error('Status:', status);
    console.error('Response:', xhr.responseText); 
    $('#bodyprod').html('<tr><td colspan="8">Une erreur est survenue.</td></tr>');
}
        });
    }
});
</script>


<script>
$(document).ready(function () {
    $('#sortAscf').on('click', function () {
        fetchSortedSupplierData('ASC'); 
    });

    $('#sortDescf').on('click', function () {
        fetchSortedSupplierData('DESC');
    });

    function fetchSortedSupplierData(order) {
        $.ajax({
            url: '../Controller/sort_four.php', 
            type: 'POST',
            data: { sortOrder: order }, 
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.error) {
                    resultHtml = `<tr><td colspan="9">Erreur : ${data.error}</td></tr>`;
                } else {
                    data.forEach(function (row) {
                        resultHtml += `<tr>
                            <td><a href="#">${row.cin_fournisseur}</a></td>
                            <td>${row.prenom}</td>
                            <td>${row.nom}</td>
                            <td><span class="badge badge-success">${row.date_naiss}</span></td>
                            <td>${row.adresse}</td>
                            <td>${row.numero}</td>
                            <td><span class="badge badge-success">${row.email}</span></td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-warning">
                                    <a style="color:white" href="modifierfournisseur.php?id_fournisseur=${row.id_fournisseur}&cin_fournisseur=${row.cin_fournisseur}&prenom=${row.prenom}&nom=${row.nom}&date_naiss=${row.date_naiss}&adresse=${row.adresse}&email=${row.email}&numero=${row.numero}">
                                        Modifier
                                    </a>
                                </button>
                            </td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-danger">
                                    <a style="color:white" href="../Controller/supprimer_fournisseur.php?id_fournisseur=${row.id_fournisseur}">Supprimer</a>
                                </button>
                            </td>
                        </tr>`;
                    });
                }
                $('#bodyf').html(resultHtml); 
            },
            error: function () {
                $('#bodyf').html('<tr><td colspan="9">Une erreur est survenue.</td></tr>');
            }
        });
    }
});
</script>
<script>
$(document).ready(function () {
  $('#searchInput').on('input', function () {
        var searchTerm = $(this).val().trim();

        if (searchTerm.length > 0) {
            fetchSearchedData(searchTerm); 
        } else {
            resetTable(); 
        }
    });

    function fetchSearchedData(searchTerm) {
        $.ajax({
           url: '../Controller/recherche_prod.php',
            type: 'POST',
            data: {
                searchTerm: searchTerm
            },
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.length > 0) {
                    data.forEach(function (row) {
                        const badgeClass = (new Date(row.date_expir) > new Date()) ? 'badge-success' : 'badge-danger';
                        const dispoClass = row.dispo === 'Oui' ? 'badge-success' : 'badge-danger';
                        resultHtml += `<tr>
                            <td><a href="#">${row.nom_produit}</a></td>
                            <td>${row.quantite}</td>
                            <td>${row.unite}</td>
                            <td><span class="badge ${badgeClass}">${row.date_expir}</span></td>
                            <td><a href="#" class="btn btn-sm btn-primary">${row.prix_uni}</a></td>
                            <td><a href="#">${row.id_four}</a></td>
                            <td><span class="badge ${dispoClass}">${row.dispo}</span></td>
                            <td><button class="btn btn-sm btn-primary badge-warning"><a style="color:white" href="modifierstock.php?id_produit=${row.id_produit}&nom_produit=${row.nom_produit}&quantite=${row.quantite}&unite=${row.unite}&date_expir=${row.date_expir}&prix_uni=${row.prix_uni}&id_four=${row.id_four}&dispo=${row.dispo}">Modifier</a></button></td>
                            <td><button class="btn btn-sm btn-primary badge-danger"><a style="color:white" href="../Controller/supprimer_stock.php?id_produit=${row.id_produit}">Supprimer</a></button></td>
                        </tr>`;
                    });
                } else {
                    resultHtml = `<tr><td colspan="9">No results found</td></tr>`;
                }
                $('#bodyprod').html(resultHtml);
            },
            error: function (xhr, status, error) {
    console.error("AJAX Error: " + error);
    console.error("Status: " + status);
    console.error("Response: " + xhr.responseText);
    alert('Error occurred while fetching data.');
}

        });
    }
    function resetTable() {
    
        $.ajax({
            url: '/projet_adam_final/Controller/recherche_prod.php',
            type: 'POST',
            data: {
                searchTerm: ''
            },
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.length > 0) {
                    data.forEach(function (row) {
                        const badgeClass = (new Date(row.date_expir) > new Date()) ? 'badge-success' : 'badge-danger';
                        const dispoClass = row.dispo === 'Oui' ? 'badge-success' : 'badge-danger';
                        resultHtml += `<tr>
                            <td><a href="#">${row.nom_produit}</a></td>
                            <td>${row.quantite}</td>
                            <td>${row.unite}</td>
                            <td><span class="badge ${badgeClass}">${row.date_expir}</span></td>
                            <td><a href="#" class="btn btn-sm btn-primary">${row.prix_uni}</a></td>
                            <td><a href="#">${row.id_four}</a></td>
                            <td><span class="badge ${dispoClass}">${row.dispo}</span></td>
                            <td><button class="btn btn-sm btn-primary badge-warning"><a style="color:white" href="modifierstock.php?id_produit=${row.id_produit}&nom_produit=${row.nom_produit}&quantite=${row.quantite}&unite=${row.unite}&date_expir=${row.date_expir}&prix_uni=${row.prix_uni}&id_four=${row.id_four}&dispo=${row.dispo}">Modifier</a></button></td>
                            <td><button class="btn btn-sm btn-primary badge-danger"><a style="color:white" href="../Controller/supprimer_stock.php?id_produit=${row.id_produit}">Supprimer</a></button></td>
                        </tr>`;
                    });
                } else {
                    resultHtml = `<tr><td colspan="9">No results found</td></tr>`;
                }
                $('#bodyprod').html(resultHtml); 
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error: " + error);
                console.error("Status: " + status);
                console.error("Response: " + xhr.responseText);
                alert('Error occurred while resetting data.');
            }
        });
    }
});
</script>


<script>
$(document).ready(function () {
    $('#searchfour').on('input', function () {
        var searchTerm = $(this).val().trim();

        if (searchTerm.length > 0) {
            fetchSearchedData(searchTerm); 
        } else {
            resetTable(); 
        }
    });

    function fetchSearchedData(searchTerm) {
        $.ajax({
            url: '../Controller/recherche_four.php',
            type: 'POST',
            data: {
                searchTerm: searchTerm
            },
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.length > 0) {
                    data.forEach(function (row) {
                        resultHtml += `<tr>
                           
                            <td><a href="#">${row.cin_fournisseur}</a></td>
                            <td>${row.prenom}</td>
                            <td>${row.nom}</td>
                            <td ><span class="badge badge-success">${row.date_naiss}</span></td>
                            <td>${row.adresse}</td>
                            <td>${row.numero}</td>
                            <td>${row.email}</td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-warning">
                                    <a style="color:white" href="modifierfournisseur.php?id_fournisseur=${row.id_fournisseur}&cin_fournisseur=${row.cin_fournisseur}&prenom=${row.prenom}&nom=${row.nom}&date_naiss=${row.date_naiss}&adresse=${row.adresse}&numero=${row.numero}&email=${row.email}">Modifier</a>
                                </button>
                            </td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-danger">
                                    <a style="color:white" href="../Controller/supprimer_fournisseur.php?id_fournisseur=${row.id_fournisseur}">Supprimer</a>
                                </button>
                            </td>
                        </tr>`;
                    });
                } else {
                    resultHtml = `<tr><td colspan="10">No results found</td></tr>`;
                }
                $('#bodyf').html(resultHtml); 
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error: " + error);
                console.error("Status: " + status);
                console.error("Response: " + xhr.responseText);
                alert('Error occurred while fetching data.');
            }
        });
    }

    function resetTable() {
   
        $.ajax({
            url: '../Controller/recherche_four.php',
            type: 'POST',
            data: {
                searchTerm: '' 
            },
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.length > 0) {
                    data.forEach(function (row) {
                        resultHtml += `<tr>
                            <td>${row.cin_fournisseur}</td>
                            <td>${row.prenom}</td>
                            <td>${row.nom}</td>
                            <td ><span class="badge badge-success">${row.date_naiss}</span></td>
                            <td>${row.adresse}</td>
                            <td>${row.numero}</td>
                            <td>${row.email}</td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-warning">
                                    <a style="color:white" href="modifierfournisseur.php?id_fournisseur=${row.id_fournisseur}&cin_fournisseur=${row.cin_fournisseur}&prenom=${row.prenom}&nom=${row.nom}&date_naiss=${row.date_naiss}&adresse=${row.adresse}&numero=${row.numero}&email=${row.email}">Modifier</a>
                                </button>
                            </td>
                            <td>
                                <button class="btn btn-sm btn-primary badge-danger">
                                    <a style="color:white" href="../Controller/supprimer_fournisseur.php?id_fournisseur=${row.id_fournisseur}">Supprimer</a>
                                </button>
                            </td>
                        </tr>`;
                    });
                } else {
                    resultHtml = `<tr><td colspan="10">No results found</td></tr>`;
                }
                $('#bodyf').html(resultHtml);
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error: " + error);
                console.error("Status: " + status);
                console.error("Response: " + xhr.responseText);
                alert('Error occurred while resetting data.');
            }
        });
    }
});
</script>

<script>
$(document).ready(function () {
    $('#choix').on('change', function () {
        var fournisseurId = $(this).val();
        console.log('Selected value:', fournisseurId);

        
        if (fournisseurId && !isNaN(fournisseurId)) {
            fournisseurId = parseInt(fournisseurId); 
        } else {
            fournisseurId = ''; 
        }

        
        var searchTerm = $('#searchTermInput').val();
        if (searchTerm && typeof searchTerm === 'string') {
            searchTerm = searchTerm.trim();
        } else {
            searchTerm = '';  
        }

       
        if (fournisseurId) {
            fetchSearchedData(fournisseurId, searchTerm); 
        } else {
            resetTable();  
        }
    });

    
    function fetchSearchedData(fournisseurId, searchTerm) {
        $.ajax({
            url: '../Controller/afficher_prod_four.php',
            type: 'POST',
            data: {
                fournisseurId: fournisseurId,
                searchTerm: searchTerm        
            },
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.length > 0) {
                    data.forEach(function (row) {
                        const badgeClass = (new Date(row.date_expir) > new Date()) ? 'badge-success' : 'badge-danger';
                        const dispoClass = row.dispo === 'Oui' ? 'badge-success' : 'badge-danger';
                        resultHtml += `<tr>
                          <td><a href="#">${row.nom_produit}</a></td>
                          <td>${row.quantite}</td>
                          <td>${row.unite}</td>
                          <td><span class="badge ${badgeClass}">${row.date_expir}</span></td>
                          <td><a href="#" class="btn btn-sm btn-primary">${row.prix_uni}</a></td>
                          <td><a href="#">${row.id_four}</a></td>
                          <td><span class="badge ${dispoClass}">${row.dispo}</span></td>
                          <td><button class="btn btn-sm btn-primary badge-warning"><a style="color:white" href="modifierstock.php?id_produit=${row.id_produit}">Modifier</a></button></td>
                          <td><button class="btn btn-sm btn-primary badge-danger"><a style="color:white" href="../Controller/supprimer_stock.php?id_produit=${row.id_produit}">Supprimer</a></button></td>
                        </tr>`;
                    });
                } else {
                    resultHtml = `<tr><td colspan="9">No results found</td></tr>`;
                }
                $('#rechprod').html(resultHtml); 
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error:", error);
                alert('Error occurred while fetching data.');
            }
        });
    }

    
    function resetTable() {
        $.ajax({
            url: '../Controller/afficher_prod_four.php',
            type: 'POST',
            data: {
                fournisseurId: '',  
                searchTerm: ''      
            },
            dataType: 'json',
            success: function (data) {
                let resultHtml = '';
                if (data.length > 0) {
                    data.forEach(function (row) {
                        const badgeClass = (new Date(row.date_expir) > new Date()) ? 'badge-success' : 'badge-danger';
                        const dispoClass = row.dispo === 'Oui' ? 'badge-success' : 'badge-danger';
                        resultHtml += `<tr>
                          <td><a href="#">${row.nom_produit}</a></td>
                          <td>${row.quantite}</td>
                          <td>${row.unite}</td>
                          <td><span class="badge ${badgeClass}">${row.date_expir}</span></td>
                          <td><a href="#" class="btn btn-sm btn-primary">${row.prix_uni}</a></td>
                          <td><a href="#">${row.id_four}</a></td>
                          <td><span class="badge ${dispoClass}">${row.dispo}</span></td>
                          <td><button class="btn btn-sm btn-primary badge-warning"><a style="color:white" href="modifierstock.php?id_produit=${row.id_produit}">Modifier</a></button></td>
                          <td><button class="btn btn-sm btn-primary badge-danger"><a style="color:white" href="../Controller/supprimer_stock.php?id_produit=${row.id_produit}">Supprimer</a></button></td>
                        </tr>`;
                    });
                } else {
                    resultHtml = `<tr><td colspan="9">No results found</td></tr>`;
                }
                $('#rechprod').html(resultHtml); 
            },
            error: function (xhr, status, error) {
                console.error("AJAX Error:", error);
                alert('Error occurred while fetching data: ' + error);
            }
        });
    }
});

</script>








</body>

</html>