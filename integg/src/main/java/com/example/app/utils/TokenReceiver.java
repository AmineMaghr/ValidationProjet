package com.example.app.utils;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import javafx.application.Platform;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class TokenReceiver {
    
    private static HttpServer server;
    
    public static void startServer() {
        // Try ports 8080–8089 in case one is already taken
        for (int port = 8080; port <= 8089; port++) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/reset", new ResetHandler());
                server.setExecutor(null);
                server.start();
                System.out.println("🖧 Serveur HTTP démarré sur http://localhost:" + port);
                return;
            } catch (java.net.BindException e) {
                System.out.println("Port " + port + " occupé, essai suivant…");
            } catch (IOException e) {
                System.err.println("Impossible de démarrer le serveur HTTP: " + e.getMessage());
                return;
            }
        }
        System.err.println("⚠️ Aucun port disponible entre 8080-8089, serveur de token désactivé.");
    }
    
    static class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String response;
            
            if (query != null && query.contains("token=")) {
                // ✅ AJOUT DE .trim() pour enlever les espaces invisibles
                String token = query.substring(query.indexOf("token=") + 6).trim();
                // Nettoyer le token
                int ampIndex = token.indexOf("&");
                if (ampIndex > 0) {
                    token = token.substring(0, ampIndex);
                }
                
                final String finalToken = token;
                System.out.println("🔑 Token reçu: '" + finalToken + "'");
                System.out.println("🔑 Longueur du token: " + finalToken.length());
                
                response = "<!DOCTYPE html>\n" +
                    "<html><head><meta charset='UTF-8'></head>\n" +
                    "<body style='font-family: Arial; background: #0a0c10; color: #fff; text-align: center; padding: 50px;'>\n" +
                    "<div style='background: #11161c; border-radius: 20px; padding: 30px; border: 1px solid #18E3A4; max-width: 500px; margin: 0 auto;'>\n" +
                    "<h1 style='color: #18E3A4;'>✅ Token reçu !</h1>\n" +
                    "<p>Vous pouvez retourner à l'application Midgar.</p>\n" +
                    "<p style='font-size: 12px; color: #666;'>Fermeture automatique dans 5 secondes...</p>\n" +
                    "<script>setTimeout(function(){window.close();}, 5000);</script>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
                
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
                // Rediriger l'application vers la page reset-password
                Platform.runLater(() -> {
                    SceneManager.getInstance().loadScene("/reset-password", finalToken);
                });
                
            } else {
                response = "<html><body><h1>Erreur : Token non trouvé</h1></body></html>";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }
}