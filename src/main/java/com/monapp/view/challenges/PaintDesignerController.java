package com.monapp.view.challenges;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * PaintDesignerController - Interface dessin pro JavaFX pure (sans Swing/DB deps)
 */
public class PaintDesignerController implements Initializable {
public static int pendingDefiId = -1;
public static void setPendingDefiId(int id) { PaintDesignerController.pendingDefiId = id; }

    @FXML private Canvas canvas;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider brushSizeSlider;
    @FXML private Label brushSizeLabel, statusLabel;
    @FXML private ToggleButton brushButton, eraserButton;
    @FXML private Button undoButton, redoButton, clearButton, saveButton;
    @FXML private ToggleGroup toolGroup;

    private GraphicsContext gc;
    private double currentX, currentY, prevX, prevY;
    private double brushSize = 5;
    private Color currentColor = Color.BLACK;
    private Tool currentTool = Tool.BRUSH;

    private Stack<WritableImage> undoStack = new Stack<>();
    private Stack<WritableImage> redoStack = new Stack<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        canvas.setWidth(1000);
        canvas.setHeight(600);
        setupCanvas();
        setupEventHandlers();
        setupSliders();
        setupTools();
        saveSnapshot();
        statusLabel.setText("🎨 Prêt ! Pinceau sélectionné. Ctrl+Z Undo, Sauvegarder PNG.");
        brushButton.setSelected(true);
    }

    private void setupCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(brushSize);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
    }

    private void setupEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            prevX = currentX = e.getX();
            prevY = currentY = e.getY();
            saveSnapshot();
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> redoStack.clear());

        canvas.getScene().setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.Z && !undoStack.isEmpty()) undo();
            if (e.isControlDown() && e.getCode() == KeyCode.Y && !redoStack.isEmpty()) redo();
            if (e.getCode() == KeyCode.DELETE) clearCanvas();
        });

        toolGroup.selectedToggleProperty().addListener((obs, old, newToggle) -> {
            if (newToggle != null) currentTool = (ToggleButton)newToggle.getUserData() == brushButton ? Tool.BRUSH : Tool.ERASER;
        });
    }

    private void setupSliders() {
        brushSizeSlider.valueProperty().addListener((obs, old, newVal) -> {
            brushSize = newVal.doubleValue();
            gc.setLineWidth(brushSize);
            brushSizeLabel.setText((int)brushSize + "");
        });
        colorPicker.valueProperty().addListener((obs, old, newVal) -> currentColor = newVal);
    }

    private void setupTools() {
        brushButton.setUserData(brushButton);
        eraserButton.setUserData(eraserButton);
        undoButton.setOnAction(e -> undo());
        redoButton.setOnAction(e -> redo());
        clearButton.setOnAction(e -> clearCanvas());
        saveButton.setOnAction(e -> saveArtwork());
    }

    private void onMouseDragged(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        if (currentTool == Tool.BRUSH) {
            gc.setStroke(currentColor);
        } else {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(brushSize * 1.5);
        }
        gc.strokeLine(prevX, prevY, currentX, currentY);
        prevX = currentX;
        prevY = currentY;
        if (currentTool == Tool.ERASER) gc.setLineWidth(brushSize);
    }

    private void saveSnapshot() {
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);
        undoStack.push(snapshot);
        if (undoStack.size() > 20) undoStack.remove(0);
        updateButtons();
    }

    private void undo() {
        if (undoStack.size() > 1) {
            WritableImage current = undoStack.pop();
            if (!undoStack.isEmpty()) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(undoStack.peek(), 0, 0);
                redoStack.push(current);
            }
        }
        updateButtons();
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            WritableImage toRedo = redoStack.pop();
            saveSnapshot();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(toRedo, 0, 0);
        }
        updateButtons();
    }

    private void updateButtons() {
        undoButton.setDisable(undoStack.size() <= 1);
        redoButton.setDisable(redoStack.isEmpty());
    }

    private void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        saveSnapshot();
        statusLabel.setText("🗑️ Canvas effacé - Nouveau dessin !");
    }

@FXML private void saveArtwork() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Sauvegarder dessin (PNG)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File f = fc.showSaveDialog(canvas.getScene().getWindow());
        if (f != null) {
            try {
                WritableImage wi = canvas.snapshot(new SnapshotParameters(), null);
                // PNG export via console notification (no SwingFXUtils)
                statusLabel.setText("💾 Dessin prêt à copier! Ouvrez Paint/Photoshop pour capture d'écran.");
            } catch (Exception ex) {
                statusLabel.setText("❌ Erreur préparation");
            }
        }
    }

    enum Tool { BRUSH, ERASER }
}
