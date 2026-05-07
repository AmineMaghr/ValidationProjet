package com.example.app.controllers;

import com.example.app.entities.Produit;
import com.example.app.services.ChatbotService;
import com.example.app.services.ProduitService;
import com.example.app.utils.CartService;
import com.example.app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ShopController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> sortFilter;
    @FXML private FlowPane productsGrid;
    @FXML private Button addProductButton;
    @FXML private Button quickBackendButton;
    @FXML private Button supportButton;

    private final ProduitService produitService = new ProduitService();
    private final CartService cartService = CartService.getInstance();
    private final ChatbotService chatbotService = new ChatbotService();
    private final ObservableList<Produit> produits = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (sortFilter != null) {
            sortFilter.setItems(FXCollections.observableArrayList(
                    "Récent",
                    "Prix (bas → haut)",
                    "Prix (haut → bas)",
                    "Nom (A-Z)",
                    "Stock (plus disponible)"
            ));
            sortFilter.setValue("Récent");
            sortFilter.valueProperty().addListener((obs, oldV, newV) -> loadProducts());
        }

        if (typeFilter != null) {
            typeFilter.setItems(FXCollections.observableArrayList("Tous les types"));
            typeFilter.setValue("Tous les types");
            typeFilter.valueProperty().addListener((obs, oldV, newV) -> loadProducts());
        }

        if (addProductButton != null) {
            boolean admin = isAdmin();
            addProductButton.setVisible(admin);
            addProductButton.setManaged(admin);
        }

        if (quickBackendButton != null) {
            quickBackendButton.setVisible(!isAdmin());
            quickBackendButton.setManaged(!isAdmin());
        }

        if (supportButton != null) {
            supportButton.setVisible(true);
            supportButton.setManaged(true);
        }

        loadProducts();
        loadTypes();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> loadProducts());
        }
    }

    private void loadProducts() {
        Task<List<Produit>> task = new Task<>() {
            @Override
            protected List<Produit> call() throws SQLException {
                String search = searchField != null ? searchField.getText() : null;
                String type = typeFilter != null ? typeFilter.getValue() : null;
                String sort = mapSortLabelToKey(sortFilter != null ? sortFilter.getValue() : null);
                return produitService.searchProduits(search, type, sort);
            }
        };

        task.setOnSucceeded(e -> {
            produits.setAll(task.getValue());
            renderProducts();
        });

        task.setOnFailed(e -> showError("Erreur", "Impossible de charger les produits: " + getErrorMessage(task.getException())));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTypes() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws SQLException {
                return produitService.getProductTypes();
            }
        };

        task.setOnSucceeded(e -> {
            ObservableList<String> types = FXCollections.observableArrayList("Tous les types");
            types.addAll(task.getValue());
            if (typeFilter != null) {
                typeFilter.setItems(types);
                if (typeFilter.getValue() == null) {
                    typeFilter.setValue("Tous les types");
                }
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void renderProducts() {
        if (productsGrid == null) {
            return;
        }

        productsGrid.getChildren().clear();
        for (Produit produit : produits) {
            productsGrid.getChildren().add(buildProductCard(produit));
        }
    }

    private VBox buildProductCard(Produit produit) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #1a1f1e; -fx-border-color: #2a3139; -fx-border-radius: 8; -fx-background-radius: 8;");
        card.setPrefWidth(260);

        Label name = new Label(produit.getNom());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        Label type = new Label(produit.getType());
        type.setStyle("-fx-text-fill: #9ca3af;");

        String descText = produit.getDescription() == null ? "" : produit.getDescription();
        Label desc = new Label(descText.length() > 110 ? descText.substring(0, 110) + "..." : descText);
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #d1d5db;");

        Label price = new Label(String.format("%.2f €", produit.getPrix()));
        price.setStyle("-fx-text-fill: #18E3A4; -fx-font-size: 15; -fx-font-weight: bold;");

        Label stock = new Label("Stock: " + produit.getQuantiteDisponible());
        stock.setStyle("-fx-text-fill: " + (produit.getQuantiteDisponible() > 0 ? "#18E3A4" : "#f87171") + ";");

        Spinner<Integer> quantitySpinner = new Spinner<>();
        int maxStock = Math.max(1, Math.min(100, produit.getQuantiteDisponible()));
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxStock, 1));
        quantitySpinner.setDisable(produit.getQuantiteDisponible() <= 0);
        quantitySpinner.setPrefWidth(90);

        Button addCart = new Button("Ajouter au panier");
        addCart.setStyle("-fx-background-color: #18E3A4; -fx-text-fill: #0b0f10; -fx-font-weight: bold;");
        addCart.setDisable(produit.getQuantiteDisponible() <= 0);
        addCart.setOnAction(e -> {
            int quantity = quantitySpinner.getValue();
            if (quantity <= 0 || quantity > produit.getQuantiteDisponible()) {
                showError("Stock insuffisant", "Quantité demandée non disponible.");
                return;
            }

            cartService.addItem(produit.getId(), quantity);

            int newStock = produit.getQuantiteDisponible() - quantity;
            produit.setQuantiteDisponible(newStock);
            stock.setText("Stock: " + newStock);
            stock.setStyle("-fx-text-fill: " + (newStock > 0 ? "#18E3A4" : "#f87171") + ";");

            if (newStock <= 0) {
                addCart.setDisable(true);
                quantitySpinner.setDisable(true);
            } else {
                int maxUpdated = Math.min(100, newStock);
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxUpdated, 1));
            }

            Task<Void> stockTask = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    produitService.update(produit);
                    return null;
                }
            };
            stockTask.setOnFailed(event -> {
                showError("Erreur stock", "Impossible de mettre à jour le stock: " + getErrorMessage(stockTask.getException()));
                loadProducts();
            });
            Thread stockThread = new Thread(stockTask);
            stockThread.setDaemon(true);
            stockThread.start();

            showAlert("Panier", quantity + " × " + produit.getNom() + " ajouté(s). Restant: " + newStock);
        });

        HBox frontActions = new HBox(8, quantitySpinner, addCart);
        frontActions.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(name, type, desc, price, stock, frontActions);

        if (isAdmin()) {
            HBox adminActions = new HBox(8);
            Button editBtn = new Button("Modifier");
            Button deleteBtn = new Button("Supprimer");

            editBtn.setStyle("-fx-background-color: #374151; -fx-text-fill: #e5e7eb;");
            deleteBtn.setStyle("-fx-background-color: #7f1d1d; -fx-text-fill: #fee2e2;");

            editBtn.setOnAction(e -> editProduct(produit));
            deleteBtn.setOnAction(e -> deleteProduct(produit));

            adminActions.getChildren().addAll(editBtn, deleteBtn);
            card.getChildren().add(adminActions);
        }

        return card;
    }

    private boolean isAdmin() {
        return UserSession.isLoggedIn() && UserSession.getCurrentUser() != null && UserSession.getCurrentUser().isAdmin();
    }

    @FXML
    private void accessBackendQuick() {
        navigateTo("/shop/backend");
    }

    private String mapSortLabelToKey(String label) {
        if (label == null) {
            return "date";
        }

        return switch (label) {
            case "Prix (bas → haut)" -> "price_asc";
            case "Prix (haut → bas)" -> "price_desc";
            case "Nom (A-Z)" -> "name";
            case "Stock (plus disponible)" -> "stock";
            default -> "date";
        };
    }

    @FXML
    private void handleSearch() {
        loadProducts();
    }

    @FXML
    private void resetFilters() {
        if (searchField != null) {
            searchField.clear();
        }
        if (typeFilter != null) {
            typeFilter.setValue("Tous les types");
        }
        if (sortFilter != null) {
            sortFilter.setValue("Récent");
        }
        loadProducts();
    }

    @FXML
    private void openSupportChat() {
        TextArea conversationArea = new TextArea();
        conversationArea.setEditable(false);
        conversationArea.setWrapText(true);
        conversationArea.setPrefRowCount(12);

        TextField inputField = new TextField();
        inputField.setPromptText("Posez une question au support boutique");

        Button sendButton = new Button("Envoyer");
        sendButton.setDefaultButton(true);

        sendButton.setOnAction(event -> {
            String userMessage = inputField.getText();
            if (userMessage == null || userMessage.isBlank()) {
                return;
            }

            if (!conversationArea.getText().isBlank()) {
                conversationArea.appendText("\n\n");
            }
            conversationArea.appendText("Vous: " + userMessage);
            inputField.clear();

            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    return chatbotService.chat(userMessage, ChatbotService.buildDefaultCustomerInfo(
                            UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getUsername() : null,
                            null,
                            "support"));
                }
            };
            task.setOnSucceeded(workerStateEvent -> conversationArea.appendText("\nSupport: " + task.getValue()));
            task.setOnFailed(workerStateEvent -> conversationArea.appendText("\nSupport: Je n'ai pas pu répondre pour le moment."));

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        VBox content = new VBox(10, conversationArea, inputField, sendButton);
        content.setStyle("-fx-padding: 12;");

        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Support Boutique");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void addProduct() {
        if (!isAdmin()) {
            showError("Accès refusé", "Seul l'admin peut gérer les produits.");
            return;
        }

        Optional<Produit> optionalProduit = showProduitDialog(null);
        if (optionalProduit.isEmpty()) {
            return;
        }

        Produit produit = optionalProduit.get();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                produitService.add(produit);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Succès", "Produit créé avec succès.");
            loadProducts();
            loadTypes();
        });

        task.setOnFailed(e -> showError("Erreur", getErrorMessage(task.getException())));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void goCart() {
        navigateTo("/shop/cart");
    }

    @FXML
    private void goOrders() {
        navigateTo("/shop/orders");
    }

    private void editProduct(Produit source) {
        Optional<Produit> optionalProduit = showProduitDialog(source);
        if (optionalProduit.isEmpty()) {
            return;
        }

        Produit produit = optionalProduit.get();
        produit.setId(source.getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                produitService.update(produit);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Succès", "Produit modifié avec succès.");
            loadProducts();
            loadTypes();
        });

        task.setOnFailed(e -> showError("Erreur", getErrorMessage(task.getException())));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void deleteProduct(Produit produit) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer le produit '" + produit.getNom() + "' ?");

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    produitService.delete(produit.getId());
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                showAlert("Succès", "Produit supprimé avec succès.");
                loadProducts();
                loadTypes();
            });

            task.setOnFailed(e -> showError("Erreur", getErrorMessage(task.getException())));

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });
    }

    private Optional<Produit> showProduitDialog(Produit source) {
        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle(source == null ? "Nouveau produit" : "Modifier le produit");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nomField = new TextField(source != null ? source.getNom() : "");
        TextField typeField = new TextField(source != null ? source.getType() : "");
        TextField prixField = new TextField(source != null ? String.valueOf(source.getPrix()) : "");
        TextField stockField = new TextField(source != null ? String.valueOf(source.getQuantiteDisponible()) : "0");
        TextArea descriptionArea = new TextArea(source != null ? source.getDescription() : "");
        descriptionArea.setPrefRowCount(4);

        VBox content = new VBox(8,
                labeledField("Nom", nomField),
                labeledField("Type", typeField),
                labeledField("Prix", prixField),
                labeledField("Stock", stockField),
                labeledField("Description", descriptionArea)
        );
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != saveButtonType) {
                return null;
            }

            try {
                Produit produit = new Produit();
                produit.setNom(nomField.getText() == null ? "" : nomField.getText().trim());
                produit.setType(typeField.getText() == null ? "" : typeField.getText().trim());
                produit.setDescription(descriptionArea.getText());
                produit.setPrix(Double.parseDouble(prixField.getText().trim()));
                produit.setQuantiteDisponible(Integer.parseInt(stockField.getText().trim()));
                return produit;
            } catch (NumberFormatException ex) {
                showError("Saisie invalide", "Le prix doit être un nombre et le stock un entier.");
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private HBox labeledField(String label, Control field) {
        Label lbl = new Label(label);
        lbl.setMinWidth(90);
        lbl.setStyle("-fx-font-weight: bold;");

        HBox row = new HBox(10, lbl, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        ((Region) field).setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private String getErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Une erreur est survenue.";
        }

        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
        String message = cause.getMessage();
        return (message == null || message.isBlank()) ? "Une erreur est survenue." : message;
    }
}
