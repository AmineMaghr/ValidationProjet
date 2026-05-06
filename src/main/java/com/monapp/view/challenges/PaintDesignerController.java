package com.monapp.view.challenges;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;

import com.example.app.utils.SceneManager;
import com.midgar.controller.ParticiperController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class PaintDesignerController implements Initializable {

    private static int pendingDefiId = -1;

    @FXML private Canvas canvas;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider brushSizeSlider;
    @FXML private ToggleButton brushButton;
    @FXML private ToggleButton eraserButton;
    @FXML private Button clearButton;
    @FXML private Button undoButton;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private GraphicsContext gc;

    private double lastX, lastY;
    private double brushSize = 5;
    private Color currentColor = Color.BLACK;

    private boolean drawing = false;

    private final Stack<WritableImage> undoStack = new Stack<>();

    private enum Tool { BRUSH, ERASER }
    private Tool currentTool = Tool.BRUSH;

    public static void setPendingDefiId(int defiId) {
        pendingDefiId = defiId;
    }

    public static int getPendingDefiId() {
        return pendingDefiId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gc = canvas.getGraphicsContext2D();

        clearFullCanvas();

        // UI init
        colorPicker.setValue(Color.BLACK);
        brushSizeSlider.setValue(5);

        brushSizeSlider.valueProperty().addListener((o, a, b) ->
                brushSize = b.doubleValue()
        );

        colorPicker.setOnAction(e ->
                currentColor = colorPicker.getValue()
        );

        // tools
        brushButton.setSelected(true);

        brushButton.setOnAction(e -> currentTool = Tool.BRUSH);
        eraserButton.setOnAction(e -> currentTool = Tool.ERASER);

        // drawing
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::startDraw);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::draw);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            drawing = false;
            saveState();
        });

        saveState();
    }

    private void startDraw(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        drawing = true;
    }

    private void draw(MouseEvent e) {
        if (!drawing) return;

        double x = e.getX();
        double y = e.getY();

        gc.setLineWidth(brushSize);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        if (currentTool == Tool.ERASER) {
            gc.clearRect(x - brushSize, y - brushSize, brushSize * 2, brushSize * 2);
        } else {
            gc.setStroke(currentColor);
            gc.strokeLine(lastX, lastY, x, y);
        }

        lastX = x;
        lastY = y;
    }

    // 🧼 Clear propre
    @FXML
    private void clearCanvas() {
        clearFullCanvas();
        saveState();
        statusLabel.setText("Canvas vidé");
    }

    private void clearFullCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // ↩ Undo stable
    @FXML
    private void undo() {
        if (undoStack.size() > 1) {
            undoStack.pop();
            WritableImage img = undoStack.peek();

            clearFullCanvas();
            gc.drawImage(img, 0, 0);

            statusLabel.setText("Undo");
        }
    }

    private void saveState() {
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);
        undoStack.push(snapshot);
    }

    // 💾 Save pro
    @FXML
    private void saveDrawing() {
        try {
            WritableImage image = canvas.snapshot(new SnapshotParameters(), null);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Artwork");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png")
            );

            File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());

            if (file != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

                statusLabel.setText("Sauvegardé ✔");

                // Pass pending Defi ID to next controller
                ParticiperController.setPendingDefiId(getPendingDefiId());
                // Navigate to participer.fxml after successful save
                SceneManager.getInstance().loadScene("/challenges/participer");
            }
            // If file is null (user cancelled), do nothing and stay on current screen

        } catch (Exception e) {
            statusLabel.setText("Erreur save");
            e.printStackTrace();
            // Do not navigate on error - user should fix the issue and try again
        }
    }
}
