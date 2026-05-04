package com.example.app.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;

import com.github.sarxos.webcam.Webcam;
import com.example.app.services.FaceRecognitionService;
import com.example.app.dao.UserDAO;
import com.example.app.entities.User;

import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FaceRegisterController extends BaseController {

    @FXML private Label statusLabel;
    @FXML private ImageView cameraPreview;

    private Webcam webcam;
    private FaceRecognitionService service;
    private UserDAO userDAO;

    private final List<Mat> faces = new ArrayList<>();
    private User currentUser;

    private ScheduledExecutorService executor;
    private volatile boolean running = false;
    @FXML private javafx.scene.control.Button captureBtn;
    @FXML private javafx.scene.control.Button saveBtn;

    @FXML
    public void initialize() {
        service = new FaceRecognitionService();
        userDAO = new UserDAO();
        statusLabel.setText("Clique sur Activer caméra");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // ================= CAMERA =================
    @FXML
    public void startCamera() {

        webcam = Webcam.getDefault();

        if (webcam == null) {
            statusLabel.setText("❌ Webcam introuvable");
            return;
        }
        if (webcam.isOpen()) {
           webcam.close();
        }

        webcam.setViewSize(new java.awt.Dimension(640, 480));
        webcam.open();

        running = true;

        executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {

            if (!running || webcam == null || !webcam.isOpen()) return;

            BufferedImage img = webcam.getImage();
            if (img == null) return;

            Platform.runLater(() -> {
                cameraPreview.setImage(SwingFXUtils.toFXImage(img, null));
            });

        }, 0, 100, TimeUnit.MILLISECONDS);

        statusLabel.setText("📷 Caméra active");
        captureBtn.setDisable(false);
        saveBtn.setDisable(false);
    }

    // ================= CAPTURE =================
    @FXML
    public void capture() {

        if (webcam == null || !webcam.isOpen()) {
            statusLabel.setText("⚠ caméra inactive");
            return;
        }

        BufferedImage img = webcam.getImage();
        if (img == null) return;

        Mat mat = bufferedImageToMat(img);

        faces.add(mat);

        statusLabel.setText("📸 Capturé: " + faces.size() + "/5");

        if (faces.size() >= 5) {
            saveFaceModel();
        }
    }

    // ================= SAVE =================
    @FXML
    public void save() {
        saveFaceModel();
    }

    private void saveFaceModel() {

        if (currentUser == null) {
            statusLabel.setText("❌ user null");
            return;
        }

        if (faces.isEmpty()) {
            statusLabel.setText("⚠ aucune image capturée");
            return;
        }

        List<Integer> labels = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            labels.add(currentUser.getId());
        }

        service.train(faces, labels);

        userDAO.updateFaceDescriptor(currentUser.getId(), "TRAINED");

        stopCamera();

        statusLabel.setText("✅ Sauvegardé");

        goProfile();
    }

    // ================= STOP =================
    private void stopCamera() {

        running = false;

        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }

        if (webcam != null) {
            webcam.close();
            webcam = null;
        }
        captureBtn.setDisable(true);
        saveBtn.setDisable(true);
    }

    // ================= CONVERT SAFE =================
    private Mat bufferedImageToMat(BufferedImage img) {

        BufferedImage converted = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );

        converted.getGraphics().drawImage(img, 0, 0, null);

        byte[] pixels = ((java.awt.image.DataBufferByte)
                converted.getRaster().getDataBuffer()).getData();

        Mat mat = new Mat(img.getHeight(), img.getWidth(),
                org.bytedeco.opencv.global.opencv_core.CV_8UC3);

        mat.data().put(pixels);

        return mat;
    }
}