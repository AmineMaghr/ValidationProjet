package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.utils.SceneManager;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public abstract class BaseController {

    @FXML
    public void goAccueil() {
        navigateTo("/");
    }

    @FXML
    public void goDiscover() {
        navigateTo("/discover");
    }

    @FXML
    public void goUniverses() {
        navigateTo("/universes");
    }

    @FXML
    public void goPersonnages() {
        navigateTo("/personnages");
    }

    @FXML
    public void goOeuvres() {
        navigateTo("/oeuvre");
    }

    @FXML
    public void goShop() {
        navigateTo("/shop");
    }

    @FXML
    public void goChallenges() {
        navigateTo("/challenges");
    }

    @FXML
    public void lancerQuiz() {
        navigateTo("/quiz");
    }

    @FXML
    public void goAdmin() {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            navigateTo("/admin");
        } else {
            showAlert("Accès refusé", "Vous n'êtes pas administrateur");
        }
    }

    @FXML
    public void goProfile() {
        navigateTo("/profile");
    }

    @FXML
    public void goLogin() {
        navigateTo("/login");
    }

    @FXML
    public void goArtefacts() {
        navigateTo("/artefact");
    }

    @FXML
    public void goRegister() {
        navigateTo("/register");
    }

    // ==================== MÉTHODE NAVIGATION STANDARD ====================
    protected void navigateTo(String view) {
        navigateTo(view, null);
    }

    // ==================== MÉTHODE NAVIGATION AVEC UTILISATEUR ====================
    protected void navigateTo(String view, User user) {

        // Java-view routes — always delegate to SceneManager (no FXML involved)
        switch (view) {
            case "/universes":
            case "/universes/create":
            case "/personnages":
            case "/personnages/create":
            case "/battle":
            case "/admin/universes":
            case "/admin/personnages":
                SceneManager.getInstance().loadScene(view);
                return;
        }

        try {
            String fxmlPath = "";
            Parent root = null;

            switch (view) {
                case "/admin":
                case "/admin/users":
                    fxmlPath = "/com/monapp/view/admin/users.fxml";
                    break;
                case "/profile":
                    fxmlPath = "/com/monapp/view/profile-view.fxml";
                    break;
                case "/login":
                    fxmlPath = "/com/monapp/view/login-view.fxml";
                    break;
                case "/register":
                    fxmlPath = "/com/monapp/view/register-view.fxml";
                    break;
                case "/forgot-password":
                    fxmlPath = "/com/monapp/view/forgot-password-view.fxml";
                    break;
                case "/reset-password":
                    fxmlPath = "/com/monapp/view/reset-password-view.fxml";
                    break;
                case "/oeuvre":
                    fxmlPath = "/com/monapp/view/oeuvre/index.fxml";
                    break;
                case "/artefact":
                    fxmlPath = "/com/monapp/view/artefact/index.fxml";
                    break;
                case "/shop":
                    fxmlPath = "/com/monapp/view/shop/index.fxml";
                    break;
                case "/discover":
                    fxmlPath = "/com/monapp/view/discover.fxml";
                    break;
                case "/challenges":
                    fxmlPath = "/com/monapp/view/challenges.fxml";
                    break;
                case "/quiz":
                    fxmlPath = "/com/monapp/view/quiz.fxml";
                    break;
                case "/":
                    fxmlPath = "/com/monapp/view/index.fxml";
                    break;
                case "/face-register":
                    fxmlPath = "/com/monapp/view/face-register-view.fxml";
                    break;
                case "/face-login":
                    fxmlPath = "/com/monapp/view/face-login-view.fxml";
                    break;
                default:
                    if (view.startsWith("/challenges/participer/")) {
                        fxmlPath = "/com/monapp/view/challenges/participer.fxml";
                    } else if (view.startsWith("/challenges/peindre/")) {
                        fxmlPath = "/com/monapp/view/challenges/paint_designer.fxml";
                    } else if (view.startsWith("/challenges/")) {
                         // Some other challenge route? Fallback to participation or index
                        fxmlPath = "/com/monapp/view/challenges/participer.fxml";
                    } else if (view.startsWith("/personnage/")) {
                        try {
                            int id = Integer.parseInt(view.substring(view.lastIndexOf("/") + 1));
                            com.example.app.entities.Personnage p = new com.example.app.services.PersonnageService().getById(id);
                            if (p != null) {
                                root = new com.example.app.views.PersonnageDetailView(p);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if (view.startsWith("/universe/")) {
                        try {
                            int id = Integer.parseInt(view.substring(view.lastIndexOf("/") + 1));
                            com.example.app.entities.Universe u = new com.example.app.services.UniverseService().getById(id);
                            if (u != null) {
                                root = new com.example.app.views.UniverseDetailView(u);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        String cleanView = view.startsWith("/") ? view.substring(1) : view;
                        fxmlPath = "/com/monapp/view/" + cleanView + ".fxml";
                    }
                    break;

            }

            Stage stage = SceneManager.getInstance().getPrimaryStage();
            FXMLLoader loader = null;

            if (root == null) {
                System.out.println("Tentative de chargement FXML: " + fxmlPath);
                URL resource = getClass().getResource(fxmlPath);
                if (resource == null) {
                    System.err.println("Fichier non trouvé: " + fxmlPath);
                    showAlert("Erreur", "Fichier non trouvé: " + fxmlPath);
                    return;
                }
                loader = new FXMLLoader(resource);
                root = loader.load();
            }

            // Passer l'utilisateur au contrôleur si c'est FaceRegisterController
            if (user != null && loader != null) {
                Object controller = loader.getController();
                if (controller instanceof FaceRegisterController) {
                    ((FaceRegisterController) controller).setCurrentUser((com.example.app.entities.User) user);
                    System.out.println("✅ Utilisateur transmis à FaceRegisterController: " + ((com.example.app.entities.User)user).getUsername());
                }
            }

            
            if (stage.getScene() == null) {
                Scene scene = new Scene(root, 1280, 800);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }

            // Ensure stylesheets are loaded on the current scene
            Scene currentScene = stage.getScene();
            if (currentScene != null) {
                try {
                    String modernStyle = getClass().getResource("/css/modern-style.css").toExternalForm();
                    if (!currentScene.getStylesheets().contains(modernStyle)) {
                        currentScene.getStylesheets().add(modernStyle);
                    }
                    String accueilStyle = getClass().getResource("/com/monapp/view/accueil.css").toExternalForm();
                    if (!currentScene.getStylesheets().contains(accueilStyle)) {
                        currentScene.getStylesheets().add(accueilStyle);
                    }
                } catch (Exception e) {
                    System.out.println("CSS non chargé: " + e.getMessage());
                }
            }

            
            Platform.runLater(() -> stage.setMaximized(true));
            stage.show();



        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page: " + view + "\n" + e.getMessage());
        }
    }

    protected void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur d'affichage d'alerte: " + e.getMessage());
        }
    }

    protected void showError(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur d'affichage d'erreur: " + e.getMessage());
        }
    }
    
}