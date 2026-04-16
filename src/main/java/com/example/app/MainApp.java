package com.example.app;

import com.example.app.utils.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialiser SceneManager AVANT de charger la scène
        SceneManager.getInstance().setPrimaryStage(stage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/monapp/view/index.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/modern-style.css").toExternalForm());

        stage.setTitle("Midgar - Plateforme de Création Fantasy");
        stage.setScene(scene);
        stage.show();

        System.out.println("✅ Application lancée !");
    }

    public static void main(String[] args) {
        launch(args);
    }
}