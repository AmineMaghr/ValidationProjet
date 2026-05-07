package com.example.app.controllers;

import com.example.app.dao.CommandeDAO;
import com.example.app.entities.Commande;
import com.example.app.entities.Produit;
import com.example.app.entities.ShopAutomationEvent;
import com.example.app.entities.StockPrediction;
import com.example.app.services.ChatbotService;
import com.example.app.services.MailService;
import com.example.app.services.ProduitService;
import com.example.app.services.ShopAnalyticsService;
import com.example.app.services.ShopAutomationEventService;
import com.example.app.services.StockPredictionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.awt.Desktop;
import java.sql.SQLException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShopBackendController extends BaseController {

    @FXML private TableView<Produit> productTable;
    @FXML private TableColumn<Produit, Number> colProductId;
    @FXML private TableColumn<Produit, String> colProductName;
    @FXML private TableColumn<Produit, String> colProductType;
    @FXML private TableColumn<Produit, Number> colProductPrice;
    @FXML private TableColumn<Produit, Number> colProductStock;
    @FXML private TableColumn<Produit, String> colProductDesc;

    @FXML private TextField productSearchField;
    @FXML private ComboBox<String> productSortCombo;
    @FXML private TextField productNameField;
    @FXML private TextField productTypeField;
    @FXML private TextField productPriceField;
    @FXML private TextField productStockField;
    @FXML private TextArea productDescField;

    @FXML private TableView<Commande> orderTable;
    @FXML private TableColumn<Commande, Number> colOrderId;
    @FXML private TableColumn<Commande, String> colOrderRef;
    @FXML private TableColumn<Commande, String> colOrderBuyer;
    @FXML private TableColumn<Commande, String> colOrderProduct;
    @FXML private TableColumn<Commande, Number> colOrderQty;
    @FXML private TableColumn<Commande, Number> colOrderTotal;
    @FXML private TableColumn<Commande, String> colOrderState;

    @FXML private TextField orderSearchField;
    @FXML private ComboBox<String> orderSortCombo;
    @FXML private TextField orderBuyerField;
    @FXML private ComboBox<Produit> orderProductCombo;
    @FXML private TextField orderQtyField;
    @FXML private ComboBox<String> orderStateCombo;
    @FXML private TextField orderTotalField;

    @FXML private Label analyticsSalesLabel;
    @FXML private Label analyticsOrdersLabel;
    @FXML private Label analyticsGrowthLabel;
    @FXML private Label analyticsAverageBasketLabel;
    @FXML private Label analyticsVipLabel;
    @FXML private Label analyticsRegularLabel;
    @FXML private Label analyticsOccasionalLabel;
    @FXML private LineChart<String, Number> salesChart;
    @FXML private BarChart<String, Number> topProductsChart;
    @FXML private TableView<ShopAnalyticsService.ProductPerformanceRow> performanceTable;
    @FXML private TableColumn<ShopAnalyticsService.ProductPerformanceRow, String> colPerformanceProduct;
    @FXML private TableColumn<ShopAnalyticsService.ProductPerformanceRow, Number> colPerformanceDemand;
    @FXML private TableColumn<ShopAnalyticsService.ProductPerformanceRow, Number> colPerformanceMargin;
    @FXML private TableColumn<ShopAnalyticsService.ProductPerformanceRow, String> colPerformanceRisk;
    @FXML private TableColumn<ShopAnalyticsService.ProductPerformanceRow, Number> colPerformanceStock;
    @FXML private ListView<StockPrediction> predictionListView;
    @FXML private ListView<ShopAutomationEvent> automationListView;
    @FXML private TextField mailRecipientField;
    @FXML private TextField mailSubjectField;
    @FXML private TextArea mailBodyArea;
    @FXML private TextArea mailPreviewArea;

    private final ProduitService produitService = new ProduitService();
    private final CommandeDAO commandeDAO = new CommandeDAO();
    private final ShopAnalyticsService analyticsService = new ShopAnalyticsService();
    private final StockPredictionService stockPredictionService = new StockPredictionService();
    private final ShopAutomationEventService automationEventService = new ShopAutomationEventService();
    private final ChatbotService chatbotService = new ChatbotService();
    private final MailService mailService = new MailService();

    private final ObservableList<Produit> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Commande> allOrders = FXCollections.observableArrayList();
    private final ObservableList<StockPrediction> allPredictions = FXCollections.observableArrayList();
    private final ObservableList<ShopAutomationEvent> allAutomationEvents = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        setupCombos();
        setupSelectionBinding();
        setupInsightsTables();
        setupMailingTab();

        reloadProducts();
        reloadOrders();
        reloadAnalytics();
        reloadPredictions();
        reloadAutomationEvents();
    }

    private void setupTables() {
        colProductId.setCellValueFactory(c -> c.getValue().idProperty());
        colProductName.setCellValueFactory(c -> c.getValue().nomProperty());
        colProductType.setCellValueFactory(c -> c.getValue().typeProperty());
        colProductPrice.setCellValueFactory(c -> c.getValue().prixProperty());
        colProductStock.setCellValueFactory(c -> c.getValue().quantiteDisponibleProperty());
        colProductDesc.setCellValueFactory(c -> c.getValue().descriptionProperty());
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.setPlaceholder(new Label("Aucun produit disponible."));

        colOrderId.setCellValueFactory(c -> c.getValue().idProperty());
        colOrderRef.setCellValueFactory(c -> c.getValue().referenceCommandeProperty());
        colOrderBuyer.setCellValueFactory(c -> c.getValue().acheteurProperty());
        colOrderProduct.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getProduit() != null && c.getValue().getProduit().getNom() != null
                        ? c.getValue().getProduit().getNom()
                        : ""
        ));
        colOrderQty.setCellValueFactory(c -> c.getValue().quantiteProperty());
        colOrderTotal.setCellValueFactory(c -> c.getValue().prixTotalProperty());
        colOrderState.setCellValueFactory(c -> c.getValue().etatProperty());
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setPlaceholder(new Label("Aucune commande disponible."));

        if (performanceTable != null) {
            colPerformanceProduct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));
            colPerformanceDemand.setCellValueFactory(c -> new ReadOnlyObjectWrapper<Number>(c.getValue().getDemand()));
            colPerformanceMargin.setCellValueFactory(c -> new ReadOnlyObjectWrapper<Number>(c.getValue().getEstimatedMargin()));
            colPerformanceRisk.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStockRisk()));
            colPerformanceStock.setCellValueFactory(c -> new ReadOnlyObjectWrapper<Number>(c.getValue().getCurrentStock()));
            performanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            performanceTable.setItems(FXCollections.observableArrayList());
            performanceTable.setPlaceholder(new Label("Aucune donnée d'analytics disponible pour le moment."));
        }
    }

    private void setupCombos() {
        productSortCombo.setItems(FXCollections.observableArrayList(
                "Récent", "Nom (A-Z)", "Prix ↑", "Prix ↓", "Stock ↓"
        ));
        productSortCombo.setValue("Récent");

        orderSortCombo.setItems(FXCollections.observableArrayList(
                "Récent", "Référence", "Montant ↑", "Montant ↓", "Quantité ↓"
        ));
        orderSortCombo.setValue("Récent");

        orderStateCombo.setItems(FXCollections.observableArrayList(
                "EN_ATTENTE", "CONFIRMEE", "EXPEDIEE", "ANNULEE"
        ));
        orderStateCombo.setValue("EN_ATTENTE");

        orderProductCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Produit produit) {
                return produit == null ? "" : produit.getNom() + " (stock: " + produit.getQuantiteDisponible() + ")";
            }

            @Override
            public Produit fromString(String string) {
                return null;
            }
        });

        if (predictionListView != null) {
            predictionListView.setItems(allPredictions);
            predictionListView.setPlaceholder(new Label("Aucune prédiction disponible."));
            predictionListView.setFixedCellSize(-1);
            predictionListView.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(StockPrediction item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    Label content = new Label(item.getProductName() + " | demande: " + String.format("%.2f", item.getPredictedDemand())
                            + " | stock: " + item.getCurrentStock() + " | recommandé: " + item.getRecommendedStock()
                            + " | confiance: " + String.format("%.1f%%", item.getConfidence()));
                    content.setWrapText(true);
                    content.maxWidthProperty().bind(list.widthProperty().subtract(28));
                    content.getStyleClass().add("shop-muted");
                    setGraphic(content);
                    setText(null);
                }
            });
        }

        if (automationListView != null) {
            automationListView.setItems(allAutomationEvents);
            automationListView.setPlaceholder(new Label("Aucun événement d'automatisation."));
            automationListView.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(ShopAutomationEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    Label content = new Label(item.toString());
                    content.setWrapText(true);
                    content.maxWidthProperty().bind(list.widthProperty().subtract(28));
                    content.getStyleClass().add("shop-muted");
                    setGraphic(content);
                    setText(null);
                }
            });
        }
    }

    private void setupInsightsTables() {
        if (salesChart != null) {
            salesChart.setLegendVisible(false);
        }
        if (topProductsChart != null) {
            topProductsChart.setLegendVisible(false);
        }
    }

    private void setupMailingTab() {
        if (mailBodyArea != null) {
            mailBodyArea.setWrapText(true);
        }
        if (mailPreviewArea != null) {
            mailPreviewArea.setWrapText(true);
            mailPreviewArea.setEditable(false);
        }

        if (mailRecipientField != null && safe(mailRecipientField.getText()).isBlank()) {
            mailRecipientField.setText("demo@validation.local");
        }

        prepareStockAlertMail();

        if (mailRecipientField != null) {
            mailRecipientField.textProperty().addListener((obs, oldValue, newValue) -> refreshMailPreview());
        }
        if (mailSubjectField != null) {
            mailSubjectField.textProperty().addListener((obs, oldValue, newValue) -> refreshMailPreview());
        }
        if (mailBodyArea != null) {
            mailBodyArea.textProperty().addListener((obs, oldValue, newValue) -> refreshMailPreview());
        }
    }

    private void setupSelectionBinding() {
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected == null) return;
            productNameField.setText(selected.getNom());
            productTypeField.setText(selected.getType());
            productPriceField.setText(String.valueOf(selected.getPrix()));
            productStockField.setText(String.valueOf(selected.getQuantiteDisponible()));
            productDescField.setText(selected.getDescription());
        });

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected == null) return;
            orderBuyerField.setText(selected.getAcheteur());
            orderQtyField.setText(String.valueOf(selected.getQuantite()));
            orderStateCombo.setValue(selected.getEtat());
            orderTotalField.setText(String.valueOf(selected.getPrixTotal()));

            if (selected.getProduit() != null) {
                Produit selectedProduct = allProducts.stream()
                        .filter(p -> p.getId() == selected.getProduit().getId())
                        .findFirst().orElse(null);
                orderProductCombo.setValue(selectedProduct);
            }
        });
    }

    private void reloadProducts() {
        try {
            allProducts.setAll(produitService.select());
            orderProductCombo.setItems(FXCollections.observableArrayList(allProducts));
            applyProductFilterSort();
        } catch (SQLException e) {
            showError("Erreur produits", e.getMessage());
        }
    }

    private void reloadOrders() {
        try {
            allOrders.setAll(commandeDAO.select());
            applyOrderFilterSort();
        } catch (SQLException e) {
            showError("Erreur commandes", e.getMessage());
        }
    }

    private void reloadAnalytics() {
        if (analyticsSalesLabel == null) {
            return;
        }

        try {
            analyticsSalesLabel.setText(formatCurrency(analyticsService.getSalesLast30Days()));
            analyticsOrdersLabel.setText(String.valueOf(analyticsService.getOrderCountLast30Days()));
            analyticsGrowthLabel.setText(formatSignedPercent(analyticsService.getGrowthPercentage()));
            analyticsAverageBasketLabel.setText(formatCurrency(analyticsService.getAverageBasketValue()));

            Map<String, Integer> segments = analyticsService.getCustomerSegments();
            analyticsVipLabel.setText(String.valueOf(segments.getOrDefault("vip", 0)));
            analyticsRegularLabel.setText(String.valueOf(segments.getOrDefault("regular", 0)));
            analyticsOccasionalLabel.setText(String.valueOf(segments.getOrDefault("occasional", 0)));

            if (salesChart != null) {
                salesChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Ventes 7 jours");
                analyticsService.getDailySalesLast7Days().forEach((day, total) -> series.getData().add(new XYChart.Data<>(day, total)));
                salesChart.getData().add(series);
            }

            if (topProductsChart != null) {
                topProductsChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Top produits");
                analyticsService.getTopProducts(5).forEach(row -> series.getData().add(new XYChart.Data<>(row.getProductName(), row.getDemand())));
                topProductsChart.getData().add(series);
            }

            if (performanceTable != null) {
                List<ShopAnalyticsService.ProductPerformanceRow> performanceRows = analyticsService.getProductPerformance();
                performanceTable.setItems(FXCollections.observableArrayList(performanceRows));
                if (performanceRows.isEmpty()) {
                    performanceTable.setPlaceholder(new Label("Aucune donnée d'analytics disponible pour le moment."));
                }
                performanceTable.refresh();
            }
        } catch (SQLException e) {
            showError("Analytics", e.getMessage());
        }
    }

    private void reloadPredictions() {
        if (predictionListView == null) {
            return;
        }

        try {
            allPredictions.setAll(stockPredictionService.findLatestPredictions(20));
            if (allPredictions.isEmpty()) {
                List<StockPrediction> generated = stockPredictionService.refreshPredictions();
                allPredictions.setAll(generated);

                if (allPredictions.isEmpty()) {
                    automationEventService.createEvent(
                            "INFO",
                            "Aucune prédiction calculable pour le moment. Vérifiez les produits et commandes.",
                            "INFO"
                    );
                }
            }
            predictionListView.refresh();
        } catch (SQLException e) {
            showError("Prédictions", e.getMessage());
        }
    }

    private void reloadAutomationEvents() {
        if (automationListView == null) {
            return;
        }

        try {
            allAutomationEvents.setAll(automationEventService.findLatest(20));
            if (allAutomationEvents.isEmpty()) {
                List<ShopAutomationEvent> generated = automationEventService.generateStockAlerts(10);
                if (generated.isEmpty()) {
                    automationEventService.createEvent(
                            "INFO",
                            "Aucune alerte critique détectée actuellement.",
                            "INFO"
                    );
                }
                allAutomationEvents.setAll(automationEventService.findLatest(20));
            }
        } catch (SQLException e) {
            showError("Automatisation", e.getMessage());
        }
    }

    @FXML
    private void onProductFilterChanged() {
        applyProductFilterSort();
    }

    private void applyProductFilterSort() {
        String query = normalize(productSearchField.getText());
        String sort = productSortCombo.getValue();

        List<Produit> filtered = allProducts.stream()
                .filter(p -> {
                    if (query.isEmpty()) return true;
                    return normalize(p.getNom()).contains(query) || normalize(p.getType()).contains(query);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        Comparator<Produit> comparator = Comparator.comparingInt(Produit::getId).reversed();
        if ("Nom (A-Z)".equals(sort)) comparator = Comparator.comparing(p -> normalize(p.getNom()));
        if ("Prix ↑".equals(sort)) comparator = Comparator.comparingDouble(Produit::getPrix);
        if ("Prix ↓".equals(sort)) comparator = Comparator.comparingDouble(Produit::getPrix).reversed();
        if ("Stock ↓".equals(sort)) comparator = Comparator.comparingInt(Produit::getQuantiteDisponible).reversed();

        filtered.sort(comparator);
        productTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onOrderFilterChanged() {
        applyOrderFilterSort();
    }

    private void applyOrderFilterSort() {
        String query = normalize(orderSearchField.getText());
        String sort = orderSortCombo.getValue();

        List<Commande> filtered = allOrders.stream()
                .filter(c -> {
                    if (query.isEmpty()) return true;
                    String productName = c.getProduit() != null ? c.getProduit().getNom() : "";
                    return normalize(c.getReferenceCommande()).contains(query)
                            || normalize(c.getAcheteur()).contains(query)
                            || normalize(c.getEtat()).contains(query)
                            || normalize(productName).contains(query);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        Comparator<Commande> comparator = Comparator.comparingInt(Commande::getId).reversed();
        if ("Référence".equals(sort)) comparator = Comparator.comparing(c -> normalize(c.getReferenceCommande()));
        if ("Montant ↑".equals(sort)) comparator = Comparator.comparingDouble(Commande::getPrixTotal);
        if ("Montant ↓".equals(sort)) comparator = Comparator.comparingDouble(Commande::getPrixTotal).reversed();
        if ("Quantité ↓".equals(sort)) comparator = Comparator.comparingInt(Commande::getQuantite).reversed();

        filtered.sort(comparator);
        orderTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void createProduct() {
        try {
            Produit produit = buildProductFromForm(null);
            produitService.add(produit);
            clearProductForm();
            reloadProducts();
            showAlert("Succès", "Produit ajouté.");
        } catch (Exception e) {
            showError("Saisie produit", e.getMessage());
        }
    }

    @FXML
    private void updateProduct() {
        Produit selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Produit", "Sélectionne un produit à modifier.");
            return;
        }

        try {
            Produit produit = buildProductFromForm(selected.getId());
            produitService.update(produit);
            clearProductForm();
            reloadProducts();
            reloadOrders();
            showAlert("Succès", "Produit modifié.");
        } catch (Exception e) {
            showError("Saisie produit", e.getMessage());
        }
    }

    @FXML
    private void deleteProduct() {
        Produit selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Produit", "Sélectionne un produit à supprimer.");
            return;
        }

        try {
            produitService.delete(selected.getId());
            clearProductForm();
            reloadProducts();
            reloadOrders();
            showAlert("Succès", "Produit supprimé.");
        } catch (Exception e) {
            showError("Suppression produit", e.getMessage());
        }
    }

    @FXML
    private void clearProductForm() {
        productTable.getSelectionModel().clearSelection();
        productNameField.clear();
        productTypeField.clear();
        productPriceField.clear();
        productStockField.clear();
        productDescField.clear();
    }

    private Produit buildProductFromForm(Integer id) {
        String name = safe(productNameField.getText());
        String type = safe(productTypeField.getText());
        String desc = safe(productDescField.getText());

        if (name.isBlank()) throw new IllegalArgumentException("Nom produit obligatoire.");
        if (type.isBlank()) throw new IllegalArgumentException("Type produit obligatoire.");

        double prix;
        int stock;
        try {
            prix = Double.parseDouble(safe(productPriceField.getText()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Prix invalide.");
        }
        try {
            stock = Integer.parseInt(safe(productStockField.getText()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Stock invalide.");
        }

        if (prix < 0) throw new IllegalArgumentException("Prix >= 0 requis.");
        if (stock < 0) throw new IllegalArgumentException("Stock >= 0 requis.");

        Produit p = new Produit();
        if (id != null) p.setId(id);
        p.setNom(name);
        p.setType(type);
        p.setDescription(desc);
        p.setPrix(prix);
        p.setQuantiteDisponible(stock);
        return p;
    }

    @FXML
    private void createOrder() {
        try {
            Commande c = buildOrderFromForm(null);
            commandeDAO.add(c);
            clearOrderForm();
            reloadOrders();
            showAlert("Succès", "Commande ajoutée.");
        } catch (Exception e) {
            showError("Saisie commande", e.getMessage());
        }
    }

    @FXML
    private void updateOrder() {
        Commande selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Commande", "Sélectionne une commande à modifier.");
            return;
        }

        try {
            Commande c = buildOrderFromForm(selected.getId());
            c.setReferenceCommande(selected.getReferenceCommande());
            commandeDAO.update(c);
            clearOrderForm();
            reloadOrders();
            showAlert("Succès", "Commande modifiée.");
        } catch (Exception e) {
            showError("Saisie commande", e.getMessage());
        }
    }

    @FXML
    private void deleteOrder() {
        Commande selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Commande", "Sélectionne une commande à supprimer.");
            return;
        }

        try {
            commandeDAO.delete(selected.getId());
            clearOrderForm();
            reloadOrders();
            showAlert("Succès", "Commande supprimée.");
        } catch (Exception e) {
            showError("Suppression commande", e.getMessage());
        }
    }

    @FXML
    private void clearOrderForm() {
        orderTable.getSelectionModel().clearSelection();
        orderBuyerField.clear();
        orderQtyField.clear();
        orderTotalField.clear();
        orderProductCombo.setValue(null);
        orderStateCombo.setValue("EN_ATTENTE");
    }

    private Commande buildOrderFromForm(Integer id) {
        String buyer = safe(orderBuyerField.getText());
        Produit product = orderProductCombo.getValue();
        String state = orderStateCombo.getValue();

        if (buyer.isBlank()) throw new IllegalArgumentException("Acheteur obligatoire.");
        if (product == null) throw new IllegalArgumentException("Produit obligatoire.");
        if (state == null || state.isBlank()) throw new IllegalArgumentException("État obligatoire.");

        int qty;
        try {
            qty = Integer.parseInt(safe(orderQtyField.getText()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Quantité invalide.");
        }
        if (qty <= 0) throw new IllegalArgumentException("Quantité > 0 requise.");

        double total;
        String totalRaw = safe(orderTotalField.getText());
        if (totalRaw.isBlank()) {
            total = product.getPrix() * qty;
        } else {
            try {
                total = Double.parseDouble(totalRaw);
            } catch (Exception e) {
                throw new IllegalArgumentException("Prix total invalide.");
            }
        }
        if (total < 0) throw new IllegalArgumentException("Prix total >= 0 requis.");

        Commande c = new Commande();
        if (id != null) c.setId(id);
        c.setAcheteur(buyer);
        c.setProduit(product);
        c.setQuantite(qty);
        c.setEtat(state);
        c.setPrixTotal(total);
        c.setDateCommande(LocalDateTime.now());
        if (id == null) {
            c.setReferenceCommande("ADM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        }
        return c;
    }

    private String normalize(String value) {
        return safe(value).toLowerCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    @FXML
    private void goBackShop() {
        navigateTo("/shop");
    }

    @FXML
    private void refreshInsights() {
        reloadProducts();
        reloadOrders();
        reloadAnalytics();
        reloadPredictions();
        reloadAutomationEvents();
    }

    @FXML
    private void generatePredictions() {
        try {
            List<StockPrediction> generated = stockPredictionService.refreshPredictions();
            if (generated.isEmpty()) {
                automationEventService.createEvent(
                        "INFO",
                        "Aucune prédiction calculable pour le moment. Vérifiez les produits et commandes.",
                        "INFO"
                );
                reloadPredictions();
            } else {
                allPredictions.setAll(generated);
            }
            showAlert("Prédictions", "Les prédictions de stock ont été recalculées.");
        } catch (SQLException e) {
            showError("Prédictions", e.getMessage());
        }
    }

    @FXML
    private void generateAutomationAlerts() {
        try {
            List<ShopAutomationEvent> generated = automationEventService.generateStockAlerts(10);
            if (generated.isEmpty()) {
                automationEventService.createEvent(
                        "INFO",
                        "Aucune alerte critique détectée actuellement.",
                        "INFO"
                );
            }
            reloadAutomationEvents();
            showAlert("Automatisation", "Les alertes d'automatisation ont été générées.");
        } catch (SQLException e) {
            showError("Automatisation", e.getMessage());
        }
    }

    @FXML
    private void openChatbot() {
        TextArea conversationArea = new TextArea();
        conversationArea.setEditable(false);
        conversationArea.setWrapText(true);
        conversationArea.setPrefRowCount(12);

        TextField inputField = new TextField();
        inputField.setPromptText("Posez une question sur les produits, commandes ou le support");

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

            javafx.concurrent.Task<String> task = new javafx.concurrent.Task<>() {
                @Override
                protected String call() {
                    return chatbotService.chat(userMessage, ChatbotService.buildDefaultCustomerInfo(
                            com.example.app.utils.UserSession.getCurrentUser() != null ? com.example.app.utils.UserSession.getCurrentUser().getUsername() : null,
                            null,
                            "support"));
                }
            };
            task.setOnSucceeded(workerStateEvent -> conversationArea.appendText("\nAssistant: " + task.getValue()));
            task.setOnFailed(workerStateEvent -> conversationArea.appendText("\nAssistant: Impossible de générer une réponse pour le moment."));

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        VBox root = new VBox(12, conversationArea, inputField, sendButton);
        root.setStyle("-fx-padding: 16; -fx-background-color: #0d1117;");

        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Assistant Boutique");
        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void prepareStockAlertMail() {
        try {
            List<com.example.app.entities.Produit> lowStockProducts = analyticsService.getLowStockProducts(5);
            mailSubjectField.setText("Alerte stock - boutique");

            String body;
            if (lowStockProducts.isEmpty()) {
                body = "Bonjour,\n\nAucun produit critique n'a été détecté pour le moment.\n\nCordialement,\nEquipe boutique";
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Bonjour,\n\n");
                builder.append("Voici le récapitulatif des produits à surveiller :\n\n");
                for (com.example.app.entities.Produit product : lowStockProducts) {
                    builder.append("- ")
                            .append(product.getNom())
                            .append(" | stock actuel: ")
                            .append(product.getQuantiteDisponible())
                            .append(" | prix: ")
                            .append(String.format(Locale.FRANCE, "%.2f €", product.getPrix()))
                            .append('\n');
                }
                builder.append("\nMerci de prévoir un réassort rapide.\n\nCordialement,\nEquipe boutique");
                body = builder.toString();
            }

            mailBodyArea.setText(body);
            refreshMailPreview();
        } catch (SQLException e) {
            showError("Mailing", e.getMessage());
        }
    }

    @FXML
    private void prepareClientFollowUpMail() {
        if (safe(mailRecipientField.getText()).isBlank()) {
            mailRecipientField.setText("client@example.com");
        }

        mailSubjectField.setText("Suivi de votre commande");
        mailBodyArea.setText("Bonjour,\n\nNous revenons vers vous pour faire le point sur votre commande et votre satisfaction.\n"
                + "Si vous avez une question ou un besoin de suivi, répondez simplement à ce message.\n\n"
                + "Cordialement,\nEquipe boutique");
        refreshMailPreview();
    }

    @FXML
    private void openMailClient() {
        try {
            String recipient = safe(mailRecipientField.getText());
            String subject = safe(mailSubjectField.getText());
            String body = safe(mailBodyArea.getText());

            if (recipient.isBlank()) {
                recipient = "demo@validation.local";
                mailRecipientField.setText(recipient);
            }

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                URI mailUri = new URI(
                        "mailto:" + encodeMailto(recipient)
                                + "?subject=" + encodeMailto(subject)
                                + "&body=" + encodeMailto(body)
                );
                Desktop.getDesktop().mail(mailUri);
            } else {
                showAlert("Mailing", "Le client mail n'est pas disponible sur cette machine. Le brouillon est prêt dans l'onglet.");
            }
        } catch (Exception e) {
            showError("Mailing", "Impossible d'ouvrir le client mail: " + e.getMessage());
        }
    }

    @FXML
    private void sendMail() {
        try {
            String recipient = safe(mailRecipientField.getText());
            String subject = safe(mailSubjectField.getText());
            String body = safe(mailBodyArea.getText());

            if (recipient.isBlank()) {
                throw new IllegalArgumentException("Destinataire obligatoire.");
            }
            if (subject.isBlank()) {
                throw new IllegalArgumentException("Sujet obligatoire.");
            }
            if (body.isBlank()) {
                throw new IllegalArgumentException("Message obligatoire.");
            }

            mailService.sendMail(recipient, subject, body);
            showAlert("Mailing", "Mail envoyé à " + recipient + ".");
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.startsWith("Missing SMTP configuration:")) {
                message = "Configuration SMTP manquante: " + message.substring("Missing SMTP configuration:".length()).trim()
                        + ". Donne-moi SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD et SMTP_FROM.";
            }
            showError("Mailing", message == null ? "Erreur inconnue." : message);
        }
    }

    private void refreshMailPreview() {
        if (mailPreviewArea == null) {
            return;
        }

        mailPreviewArea.setText("À: " + safe(mailRecipientField.getText()) + "\n"
                + "Sujet: " + safe(mailSubjectField.getText()) + "\n\n"
                + safe(mailBodyArea.getText()));
    }

    private String encodeMailto(String value) {
        return URLEncoder.encode(value == null ? "" : value, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String formatCurrency(double value) {
        return String.format(Locale.FRANCE, "%.2f €", value);
    }

    private String formatSignedPercent(double value) {
        return String.format(Locale.FRANCE, "%+.1f%%", value);
    }
}
