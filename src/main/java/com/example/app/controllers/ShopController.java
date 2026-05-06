package com.example.app.controllers;

import com.example.app.dao.AdvancedPreferenceDAO;
import com.example.app.dao.ArtefactDAO;
import com.example.app.dao.CommandeDAO;
import com.example.app.dao.CommentaireDAO;
import com.example.app.dao.DefiDAO;
import com.example.app.dao.FavorisDAO;
import com.example.app.dao.OeuvreDAO;
import com.example.app.dao.ParticipationDAO;
import com.example.app.dao.PersonnageDAO;
import com.example.app.dao.ProduitDAO;
import com.example.app.dao.QuestionDAO;
import com.example.app.dao.ReponseDAO;
import com.example.app.utils.CartService;

import com.example.app.dao.UniverseDAO;
import com.example.app.dao.UserDAO;
import com.example.app.dao.IDAO;
import com.example.app.entities.Produit;
import com.example.app.entities.Commande;
import com.example.app.services.ProduitService;
import com.example.app.services.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

public class ShopController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> sortCombo;
    @FXML private ListView<Produit> productList;
    @FXML private Label productName;
    @FXML private Label productPrice;
    @FXML private Label productDescription;
    @FXML private Label productStock;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addToCartBtn;
    @FXML private TableView<Commande> commandeTable;
    @FXML private TabPane adminTabPane;
    @FXML private TextField produitNomField;
    @FXML private TextField produitPrixField;
    @FXML private TextField produitTypeField;
    @FXML private TextArea produitDescriptionArea;
    @FXML private Spinner<Integer> produitStockSpinner;

    private ProduitService produitService = new ProduitService();
    private CommandeService commandeService = new CommandeService();
    private CartService cartService = new CartService();

    private ObservableList<Produit> produits = FXCollections.observableArrayList();
    private Produit currentProduit;

    @FXML
    public void initialize() {
        productList.setItems(produits);
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        productList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                showProductDetails(selected);
            }
        });

        sortCombo.setItems(FXCollections.observableArrayList("date", "price_asc", "price_desc", "name"));
        sortCombo.setValue("date");

        loadProducts();
        loadTypes();

        if (com.example.app.utils.UserSession.isLoggedIn() &&
                com.example.app.utils.UserSession.getCurrentUser().isAdmin()) {
            setupAdminTab();
            loadCommandes();
        }
    }

    private void loadProducts() {
        Task<List<Produit>> task = new Task<>() {
            @Override
            protected List<Produit> call() throws SQLException {
                String search = searchField.getText();
                String type = typeFilter.getValue();
                String sort = sortCombo.getValue();
                return produitService.searchProduits(search, type, sort);
            }
        };
        task.setOnSucceeded(e -> produits.setAll(task.getValue()));
        new Thread(task).start();
    }

    private void loadTypes() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws SQLException {
                return produitService.getProductTypes();
            }
        };
        task.setOnSucceeded(e -> typeFilter.setItems(FXCollections.observableArrayList(task.getValue())));
        new Thread(task).start();
    }

    private void loadCommandes() {
        Task<List<Commande>> task = new Task<>() {
            @Override
            protected List<Commande> call() throws SQLException {
                return commandeService.select();
            }
        };
        task.setOnSucceeded(e -> commandeTable.setItems(FXCollections.observableArrayList(task.getValue())));
        new Thread(task).start();
    }

    private void showProductDetails(Produit produit) {
        currentProduit = produit;
        productName.setText(produit.getNom());
        productPrice.setText(String.format("%.2f €", produit.getPrix()));
        productDescription.setText(produit.getDescription());
        productStock.setText("Stock: " + produit.getQuantiteDisponible());

        int maxStock = Math.min(produit.getQuantiteDisponible(), 100);
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxStock, 1));
    }

    @FXML
    private void search() { loadProducts(); }

    @FXML
    private void addToCart() {
        if (currentProduit == null) return;

        int quantity = quantitySpinner.getValue();
        cartService.addItem(currentProduit.getId(), quantity);
        showAlert("Succès", quantity + " x " + currentProduit.getNom() + " ajouté au panier");
    }

    @FXML
    private void viewCart() { navigateTo("/shop/cart"); }

    @FXML
    private void checkout() { navigateTo("/shop/checkout"); }

    private void setupAdminTab() {
        produitStockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0));
    }

    @FXML
    private void createProduit() {
        Produit produit = new Produit();
        produit.setNom(produitNomField.getText());
        produit.setPrix(Double.parseDouble(produitPrixField.getText()));
        produit.setType(produitTypeField.getText());
        produit.setDescription(produitDescriptionArea.getText());
        produit.setQuantiteDisponible(produitStockSpinner.getValue());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                produitService.add(produit);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Succès", "Produit créé");
            clearProduitForm();
            loadProducts();
        });
        new Thread(task).start();
    }

    private void clearProduitForm() {
        produitNomField.clear();
        produitPrixField.clear();
        produitTypeField.clear();
        produitDescriptionArea.clear();
        produitStockSpinner.getValueFactory().setValue(0);
    }
}