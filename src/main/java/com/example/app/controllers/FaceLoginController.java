package com.example.app.controllers;

import com.example.app.entities.User;
import com.example.app.services.FaceLoginService;
import com.example.app.utils.UserSession;
import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import org.bytedeco.opencv.opencv_core.Mat;


import java.awt.image.BufferedImage;

public class FaceLoginController extends BaseController {

    @FXML private ImageView cameraPreview;
    @FXML private Label statusLabel;

    private Webcam webcam;
    private FaceLoginService service;
    private volatile boolean running = false;

    @FXML
    public void initialize() {
        service = new FaceLoginService();
        statusLabel.setText("Clique pour démarrer caméra");
    }

    @FXML
    public void startCamera() {

        webcam = Webcam.getDefault();

        if (webcam == null) {
            statusLabel.setText("❌ Webcam introuvable");
            return;
        }

        webcam.setViewSize(new java.awt.Dimension(640, 480));

        if (!webcam.isOpen()) {
            webcam.open();
        }

        running = true;

        new Thread(() -> {

            while (running) {

                BufferedImage img = webcam.getImage();
                if (img == null) continue;

                Mat mat = bufferedImageToMat(img);

                User user = service.authenticate(mat);

                Platform.runLater(() ->
                        cameraPreview.setImage(SwingFXUtils.toFXImage(img, null))
                );

                // =========================
                // ✅ CAS 1 : USER TROUVÉ
                // =========================
                if (user != null) {

                    running = false;

                    if (webcam.isOpen()) {
                        webcam.close();
                    }

                    Platform.runLater(() -> {
                        statusLabel.setText("Bienvenue " + user.getUsername());

                        UserSession.setCurrentUser(user);

                        navigateTo("/profile");
                    });

                    break;
                }

                // =========================
                // ❌ CAS 2 : VISAGE NON RECONNU
                // =========================
                if (user == null) {

                    Platform.runLater(() -> {
                        statusLabel.setText("❌ Accès refusé... retour login");

                        new Thread(() -> {
                            try {
                                Thread.sleep(1500);

                                running = false;

                                if (webcam.isOpen()) {
                                    webcam.close();
                                }

                                Platform.runLater(() -> navigateTo("/login"));

                            } catch (InterruptedException ignored) {}
                        }).start();
                    });

                    break;
                }
            }

        }).start();
    }

    private Mat bufferedImageToMat(BufferedImage image) {

        BufferedImage converted = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );

        converted.getGraphics().drawImage(image, 0, 0, null);

        byte[] pixels = ((java.awt.image.DataBufferByte)
                converted.getRaster().getDataBuffer()).getData();

        Mat mat = new Mat(
                converted.getHeight(),
                converted.getWidth(),
                org.bytedeco.opencv.global.opencv_core.CV_8UC3
        );

        mat.data().put(pixels);

        return mat;
    }
}