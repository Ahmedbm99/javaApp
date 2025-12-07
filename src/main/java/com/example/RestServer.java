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
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:3000/"), rc);
    }
    
    /**
     * Méthode principale pour démarrer le serveur
     */
   public static void main(String[] args) {
   
        startServer();


try {
    Thread.currentThread().join();
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
}

}
