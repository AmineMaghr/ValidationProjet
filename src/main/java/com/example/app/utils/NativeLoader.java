// Créez ce fichier : NativeLoader.java
package com.example.app.utils;

import java.io.*;
import java.nio.file.*;

public class NativeLoader {
    
    public static void loadOpenCV() {
        try {
            // Chercher les DLL dans le classpath
            String opencvDll = findDll("opencv_java");
            if (opencvDll != null) {
                System.load(opencvDll);
                System.out.println("✅ OpenCV chargé: " + opencvDll);
            }
            
            // Chercher OpenBLAS
            String openblasDll = findDll("openblas");
            if (openblasDll != null) {
                System.load(openblasDll);
                System.out.println("✅ OpenBLAS chargé: " + openblasDll);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement natives: " + e.getMessage());
        }
    }
    
    private static String findDll(String name) {
        // Chercher dans les ressources
        String resourcePath = "/dll/" + name + ".dll";
        try (InputStream is = NativeLoader.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                File tempFile = File.createTempFile(name, ".dll");
                tempFile.deleteOnExit();
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return tempFile.getAbsolutePath();
            }
        } catch (IOException e) {
            // Ignorer
        }
        
        // Chercher dans java.library.path
        String libraryPath = System.getProperty("java.library.path");
        String[] paths = libraryPath.split(File.pathSeparator);
        for (String path : paths) {
            File dll = new File(path, name + ".dll");
            if (dll.exists()) {
                return dll.getAbsolutePath();
            }
        }
        
        return null;
    }
}