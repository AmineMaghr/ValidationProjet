package com.example.app;

import com.example.app.utils.SceneManager;
import com.example.app.utils.TokenReceiver;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.getInstance().setPrimaryStage(stage);
        
        // Démarrer le serveur HTTP pour écouter le token
        TokenReceiver.startServer();
        
        SceneManager.getInstance().loadScene("/");
        System.out.println("✅ Application lancée !");
    }
    
    @Override
    public void stop() {
        TokenReceiver.stopServer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}