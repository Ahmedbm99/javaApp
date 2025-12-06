package com.example;

import com.example.config.RestApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Serveur REST utilisant Grizzly et Jersey
 */
public class RestServer {
    
    // Base URI pour l'application
    public static final String BASE_URI = "http://localhost:5000/";
    
    /**
     * Démarre le serveur Grizzly HTTP
     */
    public static HttpServer startServer() {
        // Créer la configuration des ressources
        final ResourceConfig rc = ResourceConfig.forApplicationClass(RestApplication.class);
        
        // Ajouter les packages pour la sérialisation JSON
        rc.packages("com.example.controller");
        rc.register(org.glassfish.jersey.jackson.JacksonFeature.class);
        
        // Créer et démarrer une nouvelle instance du serveur Grizzly HTTP
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
    
    /**
     * Méthode principale pour démarrer le serveur
     */
   public static void main(String[] args) {
    try {
        final HttpServer server = startServer();

        System.out.println(String.format(
            "Application REST démarrée avec succès!%n" +
            "Point d'accès: %s",
            BASE_URI + "api"));

        // Garde le serveur actif indéfiniment
        Thread.currentThread().join();

    } catch (InterruptedException e) {
        System.err.println("Le serveur a été interrompu: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
        e.printStackTrace();
    }
}

}
