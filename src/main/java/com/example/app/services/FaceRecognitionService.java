package com.example.app.services;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.javacpp.BytePointer;
import com.example.app.utils.FaceModelLoader;
import com.example.app.dao.UserDAO;
import com.example.app.entities.User;

import java.util.*;

public class FaceRecognitionService {
    
    private boolean initialized = false;
    private UserDAO userDAO;
    private Map<Integer, Mat> registeredFaces = new HashMap<>();
    
    public FaceRecognitionService() {
        initialize();
        userDAO = new UserDAO();
        loadRegisteredFacesFromDatabase();
    }
    
    private void initialize() {
        try {
            if (FaceModelLoader.isModelsLoaded()) {
                initialized = true;
                System.out.println("✓ Service reconnaissance facial initialisé");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }
    
    public boolean isReady() {
        return initialized && FaceModelLoader.isModelsLoaded();
    }
    
    public List<Rect> detectFaces(Mat frame) {
        List<Rect> faces = new ArrayList<>();
        if (!isReady() || frame == null || frame.empty()) return faces;
        
        try {
            Mat grayFrame = new Mat();
            opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);
            opencv_imgproc.equalizeHist(grayFrame, grayFrame);
            
            RectVector faceRects = new RectVector();
            FaceModelLoader.getFaceDetector().detectMultiScale(grayFrame, faceRects);
            
            for (int i = 0; i < faceRects.size(); i++) {
                faces.add(faceRects.get(i));
            }
            
            grayFrame.close();
            faceRects.close();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur détection: " + e.getMessage());
        }
        
        return faces;
    }
    
    public Mat extractFace(Mat frame, Rect faceRect) {
        if (frame == null || faceRect == null) return null;
        
        try {
            Mat face = new Mat(frame, faceRect);
            Mat resized = new Mat();
            opencv_imgproc.resize(face, resized, new Size(50, 50));
            
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(resized, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            Mat equalized = new Mat();
            opencv_imgproc.equalizeHist(gray, equalized);
            
            face.close();
            resized.close();
            gray.close();
            
            return equalized;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur extraction: " + e.getMessage());
            return null;
        }
    }
    
    public String matToBase64(Mat mat) {
        try {
            int totalBytes = (int) (mat.total() * mat.elemSize());
            byte[] buffer = new byte[totalBytes];
            mat.data().get(buffer);
            return Base64.getEncoder().encodeToString(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Mat base64ToMat(String base64, int height, int width, int type) {
        try {
            byte[] buffer = Base64.getDecoder().decode(base64);
            Mat mat = new Mat(height, width, type);
            mat.data().put(buffer);
            return mat;
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur Base64: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("❌ Erreur conversion: " + e.getMessage());
            return null;
        }
    }
    
    public void loadRegisteredFacesFromDatabase() {
        registeredFaces.clear();
        List<User> users = userDAO.getUsersWithFaceEnabled();
        
        System.out.println("📋 Chargement des visages depuis la base...");
        
        for (User user : users) {
            String faceDescriptor = user.getFaceDescriptor();
            if (faceDescriptor != null && !faceDescriptor.isEmpty()) {
                Mat faceMat = base64ToMat(faceDescriptor, 50, 50, 0);
                if (faceMat != null) {
                    registeredFaces.put(user.getId(), faceMat);
                    System.out.println("✓ Chargé visage pour: " + user.getUsername() + " (ID: " + user.getId() + ")");
                } else {
                    System.err.println("⚠️ Visage corrompu pour: " + user.getUsername());
                    userDAO.disableFace(user.getId());
                }
            }
        }
        System.out.println("✓ Total visages chargés: " + registeredFaces.size());
    }
    
    public void refreshFaces() {
        loadRegisteredFacesFromDatabase();
    }
    
    // Méthode de comparaison simplifiée (sans absdiff qui pose problème)
    private double compareFaces(Mat face1, Mat face2) {
        if (face1 == null || face2 == null) return 1.0;
        
        try {
            // S'assurer que les deux images ont la même taille
            if (face1.cols() != face2.cols() || face1.rows() != face2.rows()) {
                Mat temp = new Mat();
                opencv_imgproc.resize(face2, temp, face1.size());
                face2 = temp;
            }
            
            // Calculer la différence pixel par pixel
            double diffSum = 0;
            int total = face1.cols() * face1.rows();
            
            BytePointer ptr1 = face1.ptr(0, 0);
            BytePointer ptr2 = face2.ptr(0, 0);
            
            for (int i = 0; i < total; i++) {
                int val1 = ptr1.get() & 0xFF;
                int val2 = ptr2.get() & 0xFF;
                diffSum += Math.abs(val1 - val2);
                ptr1 = ptr1.position(i + 1);
                ptr2 = ptr2.position(i + 1);
            }
            
            double meanDiff = diffSum / total;
            double distance = meanDiff / 255.0;
            
            System.out.println("  Distance: " + distance);
            return distance;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur comparaison: " + e.getMessage());
            return 1.0;
        }
    }
    
    public int recognizeFace(Mat faceImage) {
        if (!isReady() || faceImage == null) {
            System.out.println("❌ Service non pret ou image nulle");
            return -1;
        }
        
        try {
            Mat extractedFace = extractFace(faceImage, new Rect(0, 0, faceImage.cols(), faceImage.rows()));
            if (extractedFace == null) {
                System.out.println("❌ Extraction du visage echouee");
                return -1;
            }
            
            if (registeredFaces.isEmpty()) {
                System.out.println("⚠️ Aucun visage enregistré dans la base!");
                extractedFace.close();
                return -1;
            }
            
            System.out.println("🔍 " + registeredFaces.size() + " visage(s) enregistré(s)");
            
            int bestMatchId = -1;
            double bestDistance = 0.4;  // Seuil (distance < 0.4 = reconnu)
            
            for (Map.Entry<Integer, Mat> entry : registeredFaces.entrySet()) {
                int userId = entry.getKey();
                Mat registeredFace = entry.getValue();
                double distance = compareFaces(extractedFace, registeredFace);
                
                System.out.println("  📊 User ID " + userId + ": distance = " + distance);
                
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestMatchId = userId;
                    System.out.println("     -> NOUVEAU BEST MATCH!");
                }
            }
            
            extractedFace.close();
            
            if (bestMatchId != -1) {
                System.out.println("✅ RECONNU! User ID: " + bestMatchId + " (distance=" + bestDistance + ")");
                return bestMatchId;
            } else {
                System.out.println("❌ NON RECONNU (meilleure distance=" + bestDistance + ")");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur reconnaissance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public boolean hasFaceEnabled(int userId) {
        return userDAO.isFaceEnabled(userId);
    }
    
    public boolean disableFace(int userId) {
        boolean success = userDAO.disableFace(userId);
        if (success) {
            refreshFaces();
        }
        return success;
    }
}