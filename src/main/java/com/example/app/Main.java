package com.example.app;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.app.utils.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            SceneManager.initialize(primaryStage);
            SceneManager.showScene("common/login", "Midgar - Connexion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
