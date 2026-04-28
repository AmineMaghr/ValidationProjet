package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.application.Platform;
import com.github.sarxos.webcam.Webcam;
import com.example.app.services.FaceRecognitionService;
import com.example.app.utils.SceneManager;
import com.example.app.utils.SessionManager;
import com.example.app.dao.UserDAO;
import com.example.app.entities.User;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bytedeco.opencv.opencv_core.*;

public class FaceLoginController {
    
    @FXML private ImageView cameraView;
    @FXML private Label statusLabel;
    @FXML private Label detectionLabel;
    @FXML private Label userNameLabel;
    @FXML private Button loginButton;
    @FXML private Button retryButton;
    @FXML private ProgressIndicator progressIndicator;
    
    private Webcam webcam;
    private ScheduledExecutorService cameraExecutor;
    private FaceRecognitionService faceService;
    private UserDAO userDAO;
    private boolean cameraRunning = false;
    private int detectedUserId = -1;
    private int consecutiveDetections = 0;
    
    @FXML
    public void initialize() {
        statusLabel.setText("Initialisation...");
        loginButton.setDisable(true);
        retryButton.setDisable(true);
        if (progressIndicator != null) progressIndicator.setVisible(false);
        
        faceService = new FaceRecognitionService();
        userDAO = new UserDAO();
        
        if (faceService.isReady()) {
            statusLabel.setText("Service pret - Demarrage camera...");
            startCamera();
        } else {
            statusLabel.setText("Service non disponible");
        }
        
        loginButton.setOnAction(e -> performLogin());
        retryButton.setOnAction(e -> resetDetection());
    }
    
    private void startCamera() {
        try {
            webcam = Webcam.getDefault();
            if (webcam != null) {
                webcam.open();
                
                cameraRunning = true;
                cameraExecutor = Executors.newSingleThreadScheduledExecutor();
                cameraExecutor.scheduleAtFixedRate(this::captureAndProcess, 0, 100, TimeUnit.MILLISECONDS);
                
                statusLabel.setText("Camera active - Regardez la camera");
            } else {
                statusLabel.setText("Aucune webcam trouvee");
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }
    
    private void captureAndProcess() {
        if (!cameraRunning || webcam == null || !webcam.isOpen()) return;
        
        BufferedImage bufferedImage = webcam.getImage();
        if (bufferedImage != null) {
            processImage(bufferedImage);
        }
    }
    
    private void processImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) return;
        
        Mat frame = null;
        try {
            frame = bufferedImageToMat(bufferedImage);
            if (frame == null || frame.empty()) return;
            
            java.util.List<Rect> faces = faceService.detectFaces(frame);
            
            final int frameCols = frame.cols();
            final int frameRows = frame.rows();
            
            Platform.runLater(() -> {
                if (faces.isEmpty()) {
                    detectionLabel.setText("Aucun visage detecte");
                    detectedUserId = -1;
                    consecutiveDetections = 0;
                    loginButton.setDisable(true);
                    retryButton.setDisable(true);
                    userNameLabel.setText("");
                    if (progressIndicator != null) progressIndicator.setVisible(false);
                } else if (faces.size() > 1) {
                    detectionLabel.setText(faces.size() + " visages - Un seul a la fois");
                    detectedUserId = -1;
                    loginButton.setDisable(true);
                } else {
                    detectionLabel.setText("1 visage detecte");
                    detectionLabel.setStyle("-fx-text-fill: #ffd700;");
                    
                    // Démarrer la reconnaissance dans un thread séparé
                    new Thread(() -> recognizeFaceInThread(bufferedImage, faces.get(0), frameCols, frameRows)).start();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Erreur traitement: " + e.getMessage());
        } finally {
            if (frame != null) frame.close();
        }
    }
    
    private void recognizeFaceInThread(BufferedImage bufferedImage, Rect faceRect, int frameCols, int frameRows) {
        Mat tempFrame = null;
        Mat faceImage = null;
        
        try {
            tempFrame = bufferedImageToMat(bufferedImage);
            if (tempFrame == null || tempFrame.empty()) return;
            
            int x = faceRect.x();
            int y = faceRect.y();
            int w = faceRect.width();
            int h = faceRect.height();
            
            // Ajuster les coordonnées
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + w > frameCols) w = frameCols - x;
            if (y + h > frameRows) h = frameRows - y;
            
            if (w <= 0 || h <= 0) return;
            
            Rect adjustedRect = new Rect(x, y, w, h);
            faceImage = new Mat(tempFrame, adjustedRect);
            
            final int userId = faceService.recognizeFace(faceImage);
            
            Platform.runLater(() -> {
                if (progressIndicator != null) progressIndicator.setVisible(false);
                
                if (userId != -1) {
                    try {
                        User user = userDAO.findById(userId);
                        if (user != null) {
                            String userName = user.getUsername();
                            
                            if (detectedUserId != -1 && detectedUserId == userId) {
                                consecutiveDetections++;
                            } else {
                                detectedUserId = userId;
                                consecutiveDetections = 1;
                            }
                            
                            if (consecutiveDetections >= 3) {
                                userNameLabel.setText("Bienvenue " + userName + " !");
                                userNameLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                loginButton.setDisable(false);
                                retryButton.setDisable(false);
                                statusLabel.setText("Visage reconnu - Cliquez sur Connexion");
                                detectionLabel.setText("✓ Reconnaissance reussie");
                                detectionLabel.setStyle("-fx-text-fill: green;");
                            } else {
                                userNameLabel.setText("Reconnaissance: " + userName + " (" + consecutiveDetections + "/3)");
                                userNameLabel.setStyle("-fx-text-fill: #ffd700;");
                                loginButton.setDisable(true);
                            }
                        } else {
                            resetDetectionUI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        resetDetectionUI();
                    }
                } else {
                    detectedUserId = -1;
                    consecutiveDetections = 0;
                    userNameLabel.setText("Visage non reconnu");
                    userNameLabel.setStyle("-fx-text-fill: red;");
                    loginButton.setDisable(true);
                    retryButton.setDisable(false);
                    statusLabel.setText("Visage non reconnu - Reessayez");
                    detectionLabel.setText("❌ Aucune correspondance");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(this::resetDetectionUI);
        } finally {
            if (faceImage != null) faceImage.close();
            if (tempFrame != null) tempFrame.close();
        }
    }
    
    private void resetDetectionUI() {
        detectedUserId = -1;
        consecutiveDetections = 0;
        loginButton.setDisable(true);
        userNameLabel.setText("");
        detectionLabel.setText("Recherche de visage...");
        statusLabel.setText("Regardez la camera...");
    }
    
    private void performLogin() {
        if (detectedUserId != -1) {
            try {
                User user = userDAO.findById(detectedUserId);
                if (user != null) {
                    SessionManager.setCurrentUser(user);
                    showAlert("Succes", "Connexion reussie !\nBienvenue " + user.getUsername());
                    stopCamera();
                    SceneManager.getInstance().loadScene("/");
                } else {
                    showAlert("Erreur", "Utilisateur non trouve");
                }
            } catch (Exception e) {
                showAlert("Erreur", e.getMessage());
            }
        }
    }
    
    private void resetDetection() {
        detectedUserId = -1;
        consecutiveDetections = 0;
        loginButton.setDisable(true);
        userNameLabel.setText("");
        detectionLabel.setText("Recherche de visage...");
        statusLabel.setText("Regardez la camera...");
    }
    
    @FXML
    private void goToLogin() {
        stopCamera();
        SceneManager.getInstance().loadScene("/login");
    }
    
    private void stopCamera() {
        cameraRunning = false;
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
            webcam = null;
        }
    }
    
    private Mat bufferedImageToMat(BufferedImage bi) {
        if (bi == null) return null;
        
        try {
            int width = bi.getWidth();
            int height = bi.getHeight();
            
            if (width <= 0 || height <= 0) return null;
            
            Mat mat = new Mat(height, width, 16);
            byte[] pixels = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            
            if (pixels == null) return null;
            
            ByteBuffer buf = mat.createBuffer();
            buf.put(pixels);
            
            return mat;
            
        } catch (Exception e) {
            System.err.println("Erreur conversion: " + e.getMessage());
            return null;
        }
    }
    
    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}