package com.example.app.utils;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GoogleAuthServer {
    
    private static final int PORT = 8888;
    private static String authorizationCode = null;
    private static CountDownLatch latch = new CountDownLatch(1);
    private static HttpServer server;
    
    public static String startAndWaitForCode() throws IOException, InterruptedException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/callback", new CallbackHandler());
        server.setExecutor(null);
        server.start();
        
        System.out.println("✅ Serveur OAuth démarré sur http://localhost:" + PORT + "/callback");
        System.out.println("⏳ En attente de la redirection Google...");
        
        // Attendre 5 minutes maximum
        boolean codeReceived = latch.await(5, TimeUnit.MINUTES);
        
        server.stop(0);
        
        if (!codeReceived) {
            throw new IOException("Timeout - Aucun code reçu de Google");
        }
        
        return authorizationCode;
    }
    
    static class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            System.out.println("📥 Callback reçu avec query: " + query);
            
            if (query != null && query.contains("code=")) {
                // Extraire le code
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("code=")) {
                        authorizationCode = param.substring(5);
                        break;
                    }
                }
                
                System.out.println("✅ Code d'autorisation reçu: " + authorizationCode);
                
                // Page de succès
                String response = "<!DOCTYPE html>" +
                    "<html><head><meta charset='UTF-8'>" +
                    "<title>Connexion réussie - Midgar</title>" +
                    "<style>" +
                    "body{background:#0a0c10;color:#fff;text-align:center;padding:50px;font-family:Arial,sans-serif}" +
                    "h1{color:#18E3A4;margin-bottom:20px}" +
                    ".success-icon{font-size:64px;margin-bottom:20px}" +
                    "p{color:#B0B9B6}" +
                    "</style>" +
                    "</head><body>" +
                    "<div class='success-icon'>✅</div>" +
                    "<h1>Connexion réussie !</h1>" +
                    "<p>Vous pouvez fermer cette fenêtre et retourner à l'application.</p>" +
                    "<p style='font-size:12px;margin-top:30px;color:#666'>Fermeture automatique dans 3 secondes...</p>" +
                    "<script>setTimeout(function(){window.close();}, 3000);</script>" +
                    "</body></html>";
                
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
                latch.countDown();
            } else {
                // Page d'erreur
                String response = "<!DOCTYPE html>" +
                    "<html><head><title>Erreur - Midgar</title>" +
                    "<style>body{background:#0a0c10;color:#fff;text-align:center;padding:50px}" +
                    "h1{color:#EF5350}</style></head>" +
                    "<body><h1>❌ Erreur de connexion</h1>" +
                    "<p>Code d'autorisation non trouvé.</p>" +
                    "<p>Veuillez réessayer.</p></body></html>";
                
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}