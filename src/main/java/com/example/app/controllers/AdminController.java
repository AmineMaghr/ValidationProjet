package com.example.app.controllers;

import com.example.app.entities.*;
import com.example.app.services.*;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminController extends BaseController {

    @FXML private TextField searchField;
    @FXML private TextField searchOeuvreField;
    @FXML private TextField searchArtefactField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> sortCombo;
    @FXML private ComboBox<String> directionCombo;
    @FXML private TableView<User> userTable;
    @FXML private TableView<Oeuvre> oeuvreTable;
    @FXML private TableView<Artefact> artefactTable;
    @FXML private TabPane adminTabPane;
    @FXML private Label pageTitle;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalAdminsLabel;
    @FXML private Label totalBlockedLabel;

    // Pour la barre de recherche header
    @FXML private TextField searchInput;
    @FXML private ListView<String> searchResults;
    @FXML private VBox searchBarContainer;
    @FXML private HBox authContainer;

    private UserService userService = new UserService();
    private OeuvreService oeuvreService = new OeuvreService();
    private ArtefactService artefactService = new ArtefactService();

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Oeuvre> oeuvres = FXCollections.observableArrayList();
    private ObservableList<Artefact> artefacts = FXCollections.observableArrayList();

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn() || !UserSession.isAdmin()) {
            showAlert("Accès refusé", "Vous n'avez pas les droits d'administration.");
            goToProfile();
            return;
        }

        setupUserTable();
        setupOeuvreTable();
        setupArtefactTable();

        loadUsers();
        loadOeuvres();
        loadArtefacts();
        updateStats();

        sortCombo.setItems(FXCollections.observableArrayList("username", "nom", "prenom", "createdAt"));
        directionCombo.setItems(FXCollections.observableArrayList("ASC", "DESC"));
        sortCombo.setValue("createdAt");
        directionCombo.setValue("DESC");
    }

    // ⭐ MÉTHODES POUR LA BARRE DE RECHERCHE ⭐
    
    @FXML
    public void toggleSearchBar() {
        if (searchBarContainer != null) {
            boolean isVisible = searchBarContainer.isVisible();
            searchBarContainer.setVisible(!isVisible);
            if (!isVisible && searchInput != null) {
                searchInput.requestFocus();
            }
        }
    }

    @FXML
    public void searchUsersGlobal() {
        String query = searchInput != null ? searchInput.getText().trim() : "";
        if (query.isEmpty()) {
            if (searchResults != null) {
                searchResults.setVisible(false);
            }
            return;
        }

        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() throws SQLException {
                List<User> allUsers = userService.select();
                return allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                                u.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                (u.getNom() != null && u.getNom().toLowerCase().contains(query.toLowerCase())) ||
                                (u.getPrenom() != null && u.getPrenom().toLowerCase().contains(query.toLowerCase())))
                    .collect(java.util.stream.Collectors.toList());
            }
        };
        task.setOnSucceeded(e -> {
            List<User> results = task.getValue();
            if (searchResults != null) {
                searchResults.getItems().clear();
                for (User user : results) {
                    searchResults.getItems().add(user.getUsername() + " (" + user.getEmail() + ")");
                }
                searchResults.setVisible(!results.isEmpty());
            }
        });
        new Thread(task).start();
    }

    @FXML
    public void onSearchResultSelected() {
        if (searchResults != null && searchResults.getSelectionModel().getSelectedItem() != null) {
            String selected = searchResults.getSelectionModel().getSelectedItem();
            String username = selected.split(" ")[0];
            if (searchField != null) {
                searchField.setText(username);
                searchUsers();
            }
            toggleSearchBar();
        }
    }

    // ⭐ MÉTHODES POUR LES EFFETS HOVER ⭐
    
    @FXML
    public void onButtonHover(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle(btn.getStyle() + "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
    }

    @FXML
    public void onButtonExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        String style = btn.getStyle();
        style = style.replace("-fx-scale-x: 1.02;", "").replace("-fx-scale-y: 1.02;", "").replace("  ", " ");
        btn.setStyle(style);
    }

    // ⭐ MÉTHODES DE NAVIGATION ⭐
    
    @FXML
    public void goHome() { navigateTo("/"); }
    @FXML
    public void goDiscover() { navigateTo("/discover"); }
    @FXML
    public void goUniverses() { navigateTo("/universes"); }
    @FXML
    public void goPersonnages() { navigateTo("/personnages"); }
    @FXML
    public void goOeuvres() { navigateTo("/oeuvre"); }
    @FXML
    public void goArtefacts() { navigateTo("/artefact"); }
    @FXML
    public void goShop() { navigateTo("/shop"); }
    @FXML
    public void goChallenges() { navigateTo("/challenges"); }

    @FXML
    public void returnToProfile() {
        System.out.println("🔄 Retour au profil");
        goToProfile();
    }

    protected void goToProfile() {
        navigateTo("/profile");
    }

    @FXML
    public void showDashboard() {
        navigateTo("/");
    }

    @FXML
    public void showUsersTab() {
        if (adminTabPane != null) {
            adminTabPane.getSelectionModel().select(0);
            pageTitle.setText("Gestion des utilisateurs");
        }
    }

    @FXML
    public void showOeuvresTab() {
        if (adminTabPane != null) {
            adminTabPane.getSelectionModel().select(1);
            pageTitle.setText("Gestion des œuvres");
            loadOeuvres();
        }
    }

    @FXML
    public void showArtefactsTab() {
        if (adminTabPane != null) {
            adminTabPane.getSelectionModel().select(2);
            pageTitle.setText("Gestion des artefacts");
            loadArtefacts();
        }
    }

    // ⭐ MÉTHODES DE RECHERCHE ⭐
    
    @FXML
    public void searchUsers() {
        String search = searchField.getText().toLowerCase();
        if (search.isEmpty()) {
            loadUsers();
            return;
        }

        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() throws SQLException {
                List<User> allUsers = userService.select();
                return allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(search) ||
                                u.getEmail().toLowerCase().contains(search) ||
                                (u.getNom() != null && u.getNom().toLowerCase().contains(search)) ||
                                (u.getPrenom() != null && u.getPrenom().toLowerCase().contains(search)))
                    .collect(java.util.stream.Collectors.toList());
            }
        };
        task.setOnSucceeded(e -> {
            users.setAll(task.getValue());
            userTable.setItems(users);
            userTable.refresh();
            updateStats();
        });
        new Thread(task).start();
    }

    @FXML
    public void resetFilters() {
        searchField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        sortCombo.setValue("createdAt");
        directionCombo.setValue("DESC");
        loadUsers();
    }

    @FXML
    public void searchOeuvres() { loadOeuvres(); }
    @FXML
    public void searchArtefacts() { loadArtefacts(); }
    @FXML
    public void resetOeuvreFilters() {
        if (searchOeuvreField != null) searchOeuvreField.clear();
        loadOeuvres();
    }
    @FXML
    public void resetArtefactFilters() {
        if (searchArtefactField != null) searchArtefactField.clear();
        loadArtefacts();
    }

    // ⭐⭐ MÉTHODES DE CHARGEMENT ⭐⭐
    
    private void loadUsers() {
        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() throws SQLException {
                List<User> allUsers = userService.select();
                System.out.println("📊 Nombre d'utilisateurs en BD: " + (allUsers != null ? allUsers.size() : 0));
                if (allUsers != null) {
                    for (User u : allUsers) {
                        System.out.println("  - " + u.getUsername() + " | " + u.getEmail());
                    }
                }
                return allUsers;
            }
        };
        task.setOnSucceeded(e -> {
            List<User> userList = task.getValue();
            if (userList != null && !userList.isEmpty()) {
                users.clear();
                users.addAll(userList);
                userTable.setItems(users);
                userTable.refresh();
                updateStats();
                System.out.println("✅ " + users.size() + " utilisateurs affichés dans la table");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé");
                users.clear();
                userTable.setItems(users);
                userTable.refresh();
                updateStats();
            }
        });
        task.setOnFailed(e -> {
            System.err.println("❌ Erreur loadUsers: " + e.getSource().getException());
            e.getSource().getException().printStackTrace();
            showAlert("Erreur", "Impossible de charger les utilisateurs");
        });
        new Thread(task).start();
    }

    private void loadOeuvres() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws SQLException {
                return oeuvreService.select();
            }
        };
        task.setOnSucceeded(e -> {
            oeuvres.setAll(task.getValue());
            oeuvreTable.setItems(oeuvres);
            oeuvreTable.refresh();
        });
        new Thread(task).start();
    }

    private void loadArtefacts() {
        Task<List<Artefact>> task = new Task<>() {
            @Override
            protected List<Artefact> call() throws SQLException {
                return artefactService.select();
            }
        };
        task.setOnSucceeded(e -> {
            artefacts.setAll(task.getValue());
            artefactTable.setItems(artefacts);
            artefactTable.refresh();
        });
        new Thread(task).start();
    }

    private void updateStats() {
        if (totalUsersLabel != null) {
            totalUsersLabel.setText(String.valueOf(users.size()));
        }
        if (totalAdminsLabel != null) {
            long adminCount = users.stream().filter(u -> "admin".equals(u.getRole())).count();
            totalAdminsLabel.setText(String.valueOf(adminCount));
        }
        if (totalBlockedLabel != null) {
            long blockedCount = users.stream().filter(User::isBlocked).count();
            totalBlockedLabel.setText(String.valueOf(blockedCount));
        }
    }

    // ⭐ CONFIGURATION DES TABLES ⭐
    
    @SuppressWarnings("unchecked")
    private void setupUserTable() {
        // ID Column
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        // Username Column
        TableColumn<User, String> usernameCol = new TableColumn<>("Pseudo");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);

        // Nom Column
        TableColumn<User, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(120);

        // Prénom Column
        TableColumn<User, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomCol.setPrefWidth(120);

        // Email Column
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        // Rôle Column
        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(80);
        roleCol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.equals("admin") ? "👑 Admin" : "👤 User");
                    setStyle(item.equals("admin") ? "-fx-text-fill: #FFA726;" : "-fx-text-fill: #18E3A4;");
                }
            }
        });

        // Statut Column
        TableColumn<User, Boolean> blockedCol = new TableColumn<>("Statut");
        blockedCol.setCellValueFactory(new PropertyValueFactory<>("blocked"));
        blockedCol.setPrefWidth(80);
        blockedCol.setCellFactory(col -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "🔒 Bloqué" : "✅ Actif");
                    setStyle(item ? "-fx-text-fill: #EF5350;" : "-fx-text-fill: #18E3A4;");
                }
            }
        });

        // ⭐ Date Column - CORRIGÉE ⭐
        TableColumn<User, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getCreatedAt().toLocalDate().format(dateFormatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        dateCol.setPrefWidth(120);
        dateCol.setStyle("-fx-alignment: CENTER;");

        // Actions Column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setStyle("-fx-alignment: CENTER;");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button roleBtn = new Button("👑 Rôle");
            private final Button blockBtn = new Button("🔒 Bloquer");
            private final Button deleteBtn = new Button("🗑️ Supprimer");

            {
                roleBtn.setStyle("-fx-background-color: #FFA726; -fx-text-fill: #0a0c10; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
                blockBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #fff; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #EF5350; -fx-text-fill: #fff; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    blockBtn.setText(user.isBlocked() ? "🔓 Débloquer" : "🔒 Bloquer");
                    
                    roleBtn.setOnAction(e -> toggleUserRole(user));
                    blockBtn.setOnAction(e -> toggleUserBlock(user));
                    deleteBtn.setOnAction(e -> deleteUser(user));
                    
                    HBox box = new HBox(5, roleBtn, blockBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });

        userTable.getColumns().setAll(idCol, usernameCol, nomCol, prenomCol, emailCol, roleCol, blockedCol, dateCol, actionsCol);
        userTable.setItems(users);
    }

    @SuppressWarnings("unchecked")
private void setupOeuvreTable() {
    TableColumn<Oeuvre, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    idCol.setPrefWidth(50);
    idCol.setStyle("-fx-alignment: CENTER;");

    TableColumn<Oeuvre, String> titleCol = new TableColumn<>("Titre");
    titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
    titleCol.setPrefWidth(200);
    titleCol.setStyle("-fx-alignment: CENTER-LEFT;");

    TableColumn<Oeuvre, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    typeCol.setPrefWidth(100);
    typeCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: #18E3A4;");

    TableColumn<Oeuvre, String> authorCol = new TableColumn<>("Auteur");
    authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
    authorCol.setPrefWidth(150);
    authorCol.setStyle("-fx-alignment: CENTER-LEFT;");

    // Date Column pour Oeuvre
    TableColumn<Oeuvre, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(cellData -> {
        if (cellData.getValue().getDatePublication() != null) {
            return new javafx.beans.property.SimpleStringProperty(
                dateFormatter.format(cellData.getValue().getDatePublication())
            );
        }
        return new javafx.beans.property.SimpleStringProperty("-");
    });
    dateCol.setPrefWidth(100);
    dateCol.setStyle("-fx-alignment: CENTER;");

    // Actions Column
    TableColumn<Oeuvre, Void> actionsCol = new TableColumn<>("Actions");
    actionsCol.setPrefWidth(150);
    actionsCol.setStyle("-fx-alignment: CENTER;");
    actionsCol.setCellFactory(col -> new TableCell<>() {
        private final Button deleteBtn = new Button("🗑️ Supprimer");
        {
            deleteBtn.setStyle("-fx-background-color: #EF5350; -fx-text-fill: #fff; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
            deleteBtn.setOnAction(e -> deleteOeuvre(getTableView().getItems().get(getIndex())));
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(deleteBtn);
            }
        }
    });

    oeuvreTable.getColumns().setAll(idCol, titleCol, typeCol, authorCol, dateCol, actionsCol);
    oeuvreTable.setItems(oeuvres);
}

    @SuppressWarnings("unchecked")
    private void setupArtefactTable() {
        TableColumn<Artefact, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Artefact, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Artefact, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        TableColumn<Artefact, String> universeCol = new TableColumn<>("Univers");
        universeCol.setCellValueFactory(new PropertyValueFactory<>("universe"));
        universeCol.setPrefWidth(150);

        TableColumn<Artefact, String> rarityCol = new TableColumn<>("Rareté");
        rarityCol.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        rarityCol.setPrefWidth(100);

        TableColumn<Artefact, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑️ Supprimer");
            {
                deleteBtn.setStyle("-fx-background-color: #EF5350; -fx-text-fill: #fff; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> deleteArtefact(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        artefactTable.getColumns().setAll(idCol, nameCol, typeCol, universeCol, rarityCol, actionsCol);
        artefactTable.setItems(artefacts);
    }

    // ⭐ ACTIONS SUR LES UTILISATEURS ⭐
    
    @FXML
    public void deleteUser(User user) {
        if (user == null) return;
        if (user.getId() == UserSession.getCurrentUserId()) {
            showAlert("Erreur", "Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + user.getUsername() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        userService.delete(user.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> loadUsers());
                new Thread(task).start();
            }
        });
    }

    @FXML
    public void toggleUserRole(User user) {
        if (user == null) return;
        if (user.getId() == UserSession.getCurrentUserId()) {
            showAlert("Erreur", "Vous ne pouvez pas modifier votre propre rôle");
            return;
        }

        String newRole = "admin".equals(user.getRole()) ? "user" : "admin";
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                user.setRole(newRole);
                userService.update(user);
                return null;
            }
        };
        task.setOnSucceeded(e -> loadUsers());
        new Thread(task).start();
    }

    @FXML
    public void toggleUserBlock(User user) {
        if (user == null) return;
        if (user.getId() == UserSession.getCurrentUserId()) {
            showAlert("Erreur", "Vous ne pouvez pas vous bloquer vous-même");
            return;
        }

        boolean newStatus = !user.isBlocked();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                user.setBlocked(newStatus);
                userService.update(user);
                return null;
            }
        };
        task.setOnSucceeded(e -> loadUsers());
        new Thread(task).start();
    }

    @FXML
    public void deleteOeuvre(Oeuvre oeuvre) {
        if (oeuvre == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer \"" + oeuvre.getTitle() + "\" ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        oeuvreService.delete(oeuvre.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> loadOeuvres());
                new Thread(task).start();
            }
        });
    }

    @FXML
    public void deleteArtefact(Artefact artefact) {
        if (artefact == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer \"" + artefact.getName() + "\" ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        artefactService.delete(artefact.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> loadArtefacts());
                new Thread(task).start();
            }
        });
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}