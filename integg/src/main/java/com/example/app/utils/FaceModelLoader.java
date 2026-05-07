// src/main/java/com/example/app/utils/FaceModelLoader.java
package com.example.app.utils;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_objdetect;
import java.io.File;
import java.io.InputStream;
import java.nio.file.*;

public class FaceModelLoader {
    
    private static boolean modelsLoaded = false;
    private static CascadeClassifier faceDetector = null;
    
    static {
        try {
            Loader.load(opencv_objdetect.class);
            loadFaceDetector();
            modelsLoaded = true;
            System.out.println("✅ OpenCV chargé");
        } catch (Exception e) {
            System.err.println("❌ Erreur OpenCV: " + e.getMessage());
            modelsLoaded = false;
        }
    }
    
    private static void loadFaceDetector() {
        try {
            // Chercher le fichier dans resources
            String[] paths = {
                "src/main/resources/haarcascade_frontalface_default.xml",
                "haarcascade_frontalface_default.xml"
            };
            
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    faceDetector = new CascadeClassifier(file.getAbsolutePath());
                    if (!faceDetector.empty()) {
                        System.out.println("✅ Détecteur chargé: " + path);
                        return;
                    }
                }
            }
            
            // Extraire depuis les ressources
            InputStream is = FaceModelLoader.class.getResourceAsStream("/haarcascade_frontalface_default.xml");
            if (is != null) {
                Path tempFile = Files.createTempFile("cascade_", ".xml");
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
                faceDetector = new CascadeClassifier(tempFile.toString());
                System.out.println("✅ Détecteur chargé depuis ressources");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    public static boolean isModelsLoaded() {
        return modelsLoaded && faceDetector != null && !faceDetector.empty();
    }
    
    public static CascadeClassifier getFaceDetector() {
        return faceDetector;
    }
}