package com.example.app.controllers;

import com.example.app.entities.*;
import com.example.app.services.*;
import com.example.app.utils.SceneManager;
import com.example.app.utils.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AdminController extends BaseController {

    @FXML private TextField searchField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> sortCombo;
    @FXML private ComboBox<String> directionCombo;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colLastName;
    @FXML private TableColumn<User, String> colFirstName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colCreatedAt;
    @FXML private TableColumn<User, Boolean> colBlocked;
    @FXML private TableView<Oeuvre> oeuvreTable;
    @FXML private TableView<Artefact> artefactTable;
    @FXML private TabPane adminTabPane;

    private UserService userService = new UserService();
    private OeuvreService oeuvreService = new OeuvreService();
    private ArtefactService artefactService = new ArtefactService();
    private CommentaireService commentaireService = new CommentaireService();

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Oeuvre> oeuvres = FXCollections.observableArrayList();
    private ObservableList<Artefact> artefacts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupUserTable();
        setupOeuvreTable();
        setupArtefactTable();
        loadUsers();
        loadOeuvres();
        loadArtefacts();

        sortCombo.setItems(FXCollections.observableArrayList("username", "lastName", "firstName", "createdAt"));
        directionCombo.setItems(FXCollections.observableArrayList("ASC", "DESC"));
        sortCombo.setValue("createdAt");
        directionCombo.setValue("DESC");
    }

    private void setupUserTable() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colUsername.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        colCreatedAt.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
        colBlocked.setCellValueFactory(cellData -> cellData.getValue().blockedProperty().asObject());

        userTable.setItems(users);
    }

    private void setupOeuvreTable() {
        TableColumn<Oeuvre, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Oeuvre, String> titleCol = new TableColumn<>("Titre");
        TableColumn<Oeuvre, String> typeCol = new TableColumn<>("Type");
        TableColumn<Oeuvre, String> authorCol = new TableColumn<>("Auteur");
        TableColumn<Oeuvre, String> createdAtCol = new TableColumn<>("Créé le");

        oeuvreTable.getColumns().addAll(idCol, titleCol, typeCol, authorCol, createdAtCol);
        oeuvreTable.setItems(oeuvres);
    }

    private void setupArtefactTable() {
        TableColumn<Artefact, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Artefact, String> nameCol = new TableColumn<>("Nom");
        TableColumn<Artefact, String> typeCol = new TableColumn<>("Type");
        TableColumn<Artefact, String> universeCol = new TableColumn<>("Univers");

        artefactTable.getColumns().addAll(idCol, nameCol, typeCol, universeCol);
        artefactTable.setItems(artefacts);
    }

    private void loadUsers() {
        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() throws SQLException {
                String search = searchField.getText();
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();
                String sort = sortCombo.getValue();
                String direction = directionCombo.getValue();
                return userService.searchUsersAdmin(search, start, end, sort, direction);
            }
        };
        task.setOnSucceeded(e -> {
            users.setAll(task.getValue());
            userTable.refresh();
        });
        task.setOnFailed(e -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    private void loadOeuvres() {
        Task<List<Oeuvre>> task = new Task<>() {
            @Override
            protected List<Oeuvre> call() throws SQLException {
                return oeuvreService.select();
            }
        };
        task.setOnSucceeded(e -> oeuvres.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void loadArtefacts() {
        Task<List<Artefact>> task = new Task<>() {
            @Override
            protected List<Artefact> call() throws SQLException {
                return artefactService.select();
            }
        };
        task.setOnSucceeded(e -> artefacts.setAll(task.getValue()));
        new Thread(task).start();
    }

    @FXML
    private void searchUsers() {
        loadUsers();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        sortCombo.setValue("createdAt");
        directionCombo.setValue("DESC");
        loadUsers();
    }

    @FXML
    private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getId() == UserSession.getCurrentUser().getId()) {
                showAlert("Erreur", "Vous ne pouvez pas supprimer votre propre compte.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Supprimer l'utilisateur " + selected.getUsername() + " ?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() throws SQLException {
                            userService.delete(selected.getId());
                            return null;
                        }
                    };
                    task.setOnSucceeded(e -> loadUsers());
                    new Thread(task).start();
                }
            });
        }
    }

    @FXML
    private void toggleUserRole() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String newRole = "admin".equals(selected.getRole()) ? "user" : "admin";
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    selected.setRole(newRole);
                    userService.update(selected);
                    return null;
                }
            };
            task.setOnSucceeded(e -> loadUsers());
            new Thread(task).start();
        }
    }

    @FXML
    private void toggleUserBlock() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getId() == UserSession.getCurrentUser().getId()) {
                showAlert("Erreur", "Vous ne pouvez pas vous bloquer vous-même.");
                return;
            }

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    selected.setBlocked(!selected.isBlocked());
                    userService.update(selected);
                    return null;
                }
            };
            task.setOnSucceeded(e -> loadUsers());
            new Thread(task).start();
        }
    }

    @FXML
    private void deleteOeuvre() {
        Oeuvre selected = oeuvreTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            confirmDelete(() -> {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        oeuvreService.delete(selected.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> loadOeuvres());
                new Thread(task).start();
            });
        }
    }

    @FXML
    private void deleteArtefact() {
        Artefact selected = artefactTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            confirmDelete(() -> {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        artefactService.delete(selected.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> loadArtefacts());
                new Thread(task).start();
            });
        }
    }

    @FXML
    private void showOeuvreComments() {
        Oeuvre selected = oeuvreTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showCommentsDialog("Œuvre: " + selected.getTitle(), selected.getId(), "oeuvre");
        }
    }

    @FXML
    private void showArtefactComments() {
        Artefact selected = artefactTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showCommentsDialog("Artefact: " + selected.getName(), selected.getId(), "artefact");
        }
    }

    private void showCommentsDialog(String title, int itemId, String type) {
        Dialog<VBox> dialog = new Dialog<>();
        dialog.setTitle("Commentaires - " + title);

        ListView<String> listView = new ListView<>();

        Task<List<Commentaire>> task = new Task<>() {
            @Override
            protected List<Commentaire> call() throws SQLException {
                if ("oeuvre".equals(type)) {
                    return commentaireService.findByOeuvre(itemId);
                } else {
                    return commentaireService.findByArtefact(itemId);
                }
            }
        };

        task.setOnSucceeded(e -> {
            for (Commentaire comment : task.getValue()) {
                listView.getItems().add(comment.getContenu() + " - " + comment.getCreatedAt());
            }
        });

        new Thread(task).start();

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    private void confirmDelete(Runnable action) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                action.run();
            }
        });
    }

    @Override
    protected void showAlert(String title, String message) {
        super.showAlert(title, message);
    }}