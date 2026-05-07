package com.example.app.views;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import com.example.app.entities.Universe;
import com.example.app.services.UniverseService;

import java.util.List;

public class UniverseTableView extends TableView<Universe> {

    private static final String PRIMARY_COLOR = "#18E3A4";
    private static final String BG_DARK = "#0B0F0E";
    private static final String TEXT_PRIMARY = "#E6FFF6";

    private final UniverseService universeService;

    public UniverseTableView() {
        this.universeService = new UniverseService();
        setupTable();
        loadData();
    }

    private void setupTable() {
        String css = ".table-view { -fx-background-color: transparent; -fx-border-color: transparent; } " +
                     ".table-view .column-header-background { -fx-background-color: #0B0F0E; -fx-border-color: #2A3139; -fx-border-width: 0 0 2 0; } " +
                     ".table-view .column-header { -fx-background-color: transparent; -fx-size: 45px; } " +
                     ".table-view .column-header .label { -fx-text-fill: #18E3A4; -fx-font-weight: bold; -fx-font-size: 14px; } " +
                     ".table-view .filler, .table-view .corner { -fx-background-color: #0B0F0E; } " +
                     ".table-row-cell:empty { -fx-background-color: transparent; -fx-border-color: transparent; } " +
                     ".table-row-cell { -fx-background-color: #1A1F1E; -fx-border-color: #2A3139; -fx-border-width: 0 0 1 0; -fx-cell-size: 60px; } " +
                     ".table-row-cell:hover { -fx-background-color: #252A29; -fx-border-color: #18E3A4; -fx-border-width: 0 0 1 0; } " +
                     ".table-cell { -fx-font-size: 15px; -fx-text-fill: #E6FFF6; -fx-alignment: center-left; -fx-padding: 0 15px; } " +
                     ".scroll-bar:horizontal, .scroll-bar:vertical { -fx-background-color: #0B0F0E; } " +
                     ".scroll-bar:horizontal .thumb, .scroll-bar:vertical .thumb { -fx-background-color: #2A3139; -fx-background-radius: 4px; }";

        String base64Css = java.util.Base64.getEncoder().encodeToString(css.getBytes());
        this.getStylesheets().add("data:text/css;base64," + base64Css);
        this.setStyle("-fx-background-color: transparent;");

        TableColumn<Universe, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Universe, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(220);

        TableColumn<Universe, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreCol.setPrefWidth(140);

        TableColumn<Universe, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));
        descCol.setPrefWidth(300);

        TableColumn<Universe, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        setupActionsColumn(actionsCol);

        this.getColumns().addAll(idCol, nameCol, genreCol, descCol, actionsCol);
    }

    private void setupActionsColumn(TableColumn<Universe, Void> actionsCol) {
        actionsCol.setCellFactory(param -> new TableCell<Universe, Void>() {
            private final Button editBtn   = createBtn("Éditer",    PRIMARY_COLOR, BG_DARK);
            private final Button deleteBtn = createBtn("Supprimer", "#e74c3c",     "#FFFFFF");
            private final HBox pane = new HBox(12, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER_LEFT);
                pane.setStyle("-fx-padding: 0 10;");

                editBtn.setOnAction(e -> {
                    Universe u = getTableView().getItems().get(getIndex());
                    getScene().setRoot(new UniverseCreateView(u, true));
                });

                deleteBtn.setOnAction(e -> {
                    Universe u = getTableView().getItems().get(getIndex());
                    try {
                        universeService.delete(u.getId());
                        loadData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
                setStyle("-fx-background-color: transparent;");
            }
        });
    }

    private Button createBtn(String label, String bg, String fg) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand; -fx-padding: 6 14;");
        btn.setOnMouseEntered(e -> { btn.setScaleX(1.1); btn.setScaleY(1.1); btn.setEffect(new DropShadow(10, Color.web(bg))); });
        btn.setOnMouseExited(e  -> { btn.setScaleX(1.0); btn.setScaleY(1.0); btn.setEffect(null); });
        return btn;
    }

    public void loadData() {
        try {
            List<Universe> data = universeService.select();
            this.getItems().setAll(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void search(String query) {
        try {
            List<Universe> data = universeService.searchUniverses(query, null, "Récents");
            this.getItems().setAll(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
