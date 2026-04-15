package com.example.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Gestionnaire centralisé pour les transitions entre scènes.
 * Gère le chargement des fichiers FXML et la navigation.
 */
public class SceneManager {
    private static Stage primaryStage;
    private static final String FXML_PATH = "/com/example/app/views/";
    private static final double WINDOW_WIDTH = 1400;
    private static final double WINDOW_HEIGHT = 900;

    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
    }

    public static void showScene(String fxmlName, String title) {
        try {
            Parent root = loadFXML(fxmlName);
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(SceneManager.class.getResource("/com/example/app/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + fxmlName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Parent loadFXML(String fxmlName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(FXML_PATH + fxmlName + ".fxml"));
        return fxmlLoader.load();
    }

    public static <T> T loadFXMLWithController(String fxmlName, Class<T> controllerClass) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(FXML_PATH + fxmlName + ".fxml"));
        fxmlLoader.load();
        return fxmlLoader.getController();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}

