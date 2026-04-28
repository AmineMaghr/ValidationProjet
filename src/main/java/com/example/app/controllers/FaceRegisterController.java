package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.application.Platform;
import com.github.sarxos.webcam.Webcam;
import com.example.app.services.FaceRecognitionService;
import com.example.app.utils.SceneManager;
import com.example.app.utils.SessionManager;
import com.example.app.entities.User;
import com.example.app.dao.UserDAO;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bytedeco.opencv.opencv_core.*;

public class FaceRegisterController {
    
    @FXML private ImageView cameraPreview;
    @FXML private Label statusLabel;
    @FXML private Label captureCountLabel;
    @FXML private Button startCameraBtn;
    @FXML private Button registerFaceBtn;
    @FXML private ProgressIndicator progressIndicator;
    
    private Webcam webcam;
    private ScheduledExecutorService cameraExecutor;
    private FaceRecognitionService faceService;
    private Mat capturedFace = null;
    private int currentStep = 0;
    private static final int REQUIRED_CAPTURES = 5;
    private boolean cameraStarted = false;
    private UserDAO userDAO;
    private User currentUser;
    
    @FXML
    public void initialize() {
        faceService = new FaceRecognitionService();
        userDAO = new UserDAO();
        
        registerFaceBtn.setVisible(false);
        registerFaceBtn.setManaged(false);
        captureCountLabel.setVisible(false);
        progressIndicator.setVisible(false);
        
        if (!faceService.isReady()) {
            statusLabel.setText("Service non disponible");
            startCameraBtn.setDisable(true);
        } else {
            statusLabel.setText("Pret - Cliquez sur Activer");
        }
        
        startCameraBtn.setOnAction(event -> startCamera());
        registerFaceBtn.setOnAction(event -> registerFace());
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Utilisateur reçu: " + (user != null ? user.getUsername() : "null"));
    }
    
    private void startCamera() {
        if (cameraStarted) return;
        
        statusLabel.setText("Demarrage de la camera...");
        
        closeCamera();
        
        try {
            webcam = Webcam.getDefault();
            
            if (webcam != null) {
                webcam.open();
                
                if (webcam.isOpen()) {
                    cameraStarted = true;
                    currentStep = 0;
                    captureCountLabel.setText("Captures: 0/" + REQUIRED_CAPTURES);
                    captureCountLabel.setVisible(true);
                    registerFaceBtn.setVisible(false);
                    registerFaceBtn.setManaged(false);
                    startCameraBtn.setText("Capturer (0/" + REQUIRED_CAPTURES + ")");
                    
                    cameraExecutor = Executors.newSingleThreadScheduledExecutor();
                    cameraExecutor.scheduleAtFixedRate(this::previewCamera, 0, 33, TimeUnit.MILLISECONDS);
                    
                    startCameraBtn.setOnAction(event -> captureFace());
                    
                    statusLabel.setText("Camera active - Cliquez sur Capturer");
                    statusLabel.setStyle("-fx-text-fill: #18E3A4;");
                } else {
                    statusLabel.setText("Impossible d'ouvrir la camera");
                    statusLabel.setStyle("-fx-text-fill: #EF5350;");
                }
            } else {
                statusLabel.setText("Aucune webcam trouvee");
                statusLabel.setStyle("-fx-text-fill: #EF5350;");
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #EF5350;");
            e.printStackTrace();
        }
    }
    
    private void previewCamera() {
        if (webcam == null || !webcam.isOpen()) return;
        
        try {
            BufferedImage bufferedImage = webcam.getImage();
            if (bufferedImage != null) {
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                Platform.runLater(() -> cameraPreview.setImage(fxImage));
            }
        } catch (Exception e) {
            System.err.println("Erreur preview: " + e.getMessage());
        }
    }
    
    private void captureFace() {
        if (webcam == null || !webcam.isOpen()) {
            statusLabel.setText("Camera non disponible");
            return;
        }
        
        try {
            BufferedImage bufferedImage = webcam.getImage();
            if (bufferedImage == null) {
                statusLabel.setText("Erreur capture image");
                return;
            }
            
            Mat frame = bufferedImageToMat(bufferedImage);
            if (frame == null || frame.empty()) {
                statusLabel.setText("Erreur frame video");
                if (frame != null) frame.close();
                return;
            }
            
            java.util.List<Rect> faces = faceService.detectFaces(frame);
            
            if (faces.isEmpty()) {
                statusLabel.setText("Aucun visage detecte - Placez-vous face a la camera");
                statusLabel.setStyle("-fx-text-fill: #EF5350;");
                frame.close();
                return;
            }
            
            Rect faceRect = faces.get(0);
            int x = faceRect.x();
            int y = faceRect.y();
            int w = faceRect.width();
            int h = faceRect.height();
            
            int imgWidth = frame.cols();
            int imgHeight = frame.rows();
            
            if (x < 0) {
                w += x;
                x = 0;
            }
            if (y < 0) {
                h += y;
                y = 0;
            }
            if (x + w > imgWidth) {
                w = imgWidth - x;
            }
            if (y + h > imgHeight) {
                h = imgHeight - y;
            }
            
            if (w <= 0 || h <= 0) {
                statusLabel.setText("Visage mal cadre - Reculez-vous");
                statusLabel.setStyle("-fx-text-fill: #EF5350;");
                frame.close();
                return;
            }
            
            Rect adjustedRect = new Rect(x, y, w, h);
            Mat faceImage = new Mat(frame, adjustedRect);
            
            if (capturedFace != null) {
                capturedFace.close();
            }
            capturedFace = faceImage.clone();
            
            faceImage.close();
            frame.close();
            
            currentStep++;
            captureCountLabel.setText("Captures: " + currentStep + "/" + REQUIRED_CAPTURES);
            startCameraBtn.setText("Capturer (" + currentStep + "/" + REQUIRED_CAPTURES + ")");
            statusLabel.setText("Capture " + currentStep + "/" + REQUIRED_CAPTURES + " reussie");
            statusLabel.setStyle("-fx-text-fill: #18E3A4;");
            
            if (currentStep >= REQUIRED_CAPTURES) {
                startCameraBtn.setDisable(true);
                registerFaceBtn.setVisible(true);
                registerFaceBtn.setManaged(true);
                statusLabel.setText("Captures terminees ! Cliquez sur Enregistrer");
                closeCamera();
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur capture: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void registerFace() {
        if (capturedFace == null) {
            statusLabel.setText("Aucun visage capture");
            return;
        }
        
        if (currentUser == null && SessionManager.isLoggedIn()) {
            currentUser = SessionManager.getCurrentUser();
        }
        
        if (currentUser == null) {
            statusLabel.setText("Erreur: utilisateur non identifie");
            showAlert("Erreur", "Veuillez vous reconnecter");
            return;
        }
        
        progressIndicator.setVisible(true);
        statusLabel.setText("Enregistrement en cours...");
        
        try {
            Mat processedFace = faceService.extractFace(capturedFace, new Rect(0, 0, capturedFace.cols(), capturedFace.rows()));
            if (processedFace == null) {
                progressIndicator.setVisible(false);
                statusLabel.setText("Erreur extraction visage");
                return;
            }
            
            String base64Data = matToBase64(processedFace);
            processedFace.close();
            
            if (base64Data == null || base64Data.isEmpty()) {
                progressIndicator.setVisible(false);
                statusLabel.setText("Erreur conversion image");
                return;
            }
            
            boolean saved = userDAO.saveFaceDescriptor(currentUser.getId(), base64Data);
            
            progressIndicator.setVisible(false);
            
            if (saved) {
                statusLabel.setText("Visage enregistre avec succes !");
                statusLabel.setStyle("-fx-text-fill: #18E3A4;");
                showAlert("Succes", "Visage enregistre avec succes !");
                faceService.refreshFaces();
                goProfile();
            } else {
                statusLabel.setText("Echec de l'enregistrement");
                statusLabel.setStyle("-fx-text-fill: #EF5350;");
                showAlert("Erreur", "Echec de l'enregistrement");
            }
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            statusLabel.setText("Erreur: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #EF5350;");
            e.printStackTrace();
            showAlert("Erreur", e.getMessage());
        }
    }
    
    private String matToBase64(Mat mat) {
        try {
            int totalBytes = (int) (mat.total() * mat.elemSize());
            byte[] buffer = new byte[totalBytes];
            mat.data().get(buffer);
            return java.util.Base64.getEncoder().encodeToString(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void closeCamera() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            try {
                cameraExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cameraExecutor = null;
        }
        
        if (webcam != null) {
            try {
                if (webcam.isOpen()) {
                    webcam.close();
                }
            } catch (Exception e) {
                System.err.println("Erreur fermeture webcam: " + e.getMessage());
            }
            webcam = null;
        }
        cameraStarted = false;
    }
    
    @FXML
    private void goProfile() {
        closeCamera();
        navigateTo("/profile");
    }
    
    @FXML
    private void goToHome() {
        closeCamera();
        navigateTo("/");
    }
    
    private void navigateTo(String path) {
        try {
            SceneManager.getInstance().loadScene(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Mat bufferedImageToMat(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        Mat mat = new Mat(height, width, 16);
        
        byte[] pixels = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        ByteBuffer buf = mat.createBuffer();
        buf.put(pixels);
        
        return mat;
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