package com.example;

import com.example.config.RestApplication;
import com.example.util.JPAUtil;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Logger;

/**
 * Serveur REST utilisant Grizzly et Jersey
 */
public class RestServer {
    
    private static final Logger logger = Logger.getLogger(RestServer.class.getName());
    private static HttpServer server;

    /**
     * Démarre le serveur Grizzly HTTP
     */
    public static HttpServer startServer() {
        // Initialiser JPA pour la production avec la persistence-unit "example-pu"
        try {
            logger.info("Initialisation de JPA pour la production...");
            // Le fichier est dans src/main/resources/vars/flyway.conf, donc accessible via classpath comme "vars/flyway.conf"
            JPAUtil.init("example-pu", "vars/flyway.conf");
            logger.info("JPA initialisé avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'initialisation de JPA: " + e.getMessage());
            throw new RuntimeException("Impossible de démarrer le serveur: échec de l'initialisation JPA", e);
        }
        
        // Créer la configuration des ressources
        final ResourceConfig rc = ResourceConfig.forApplicationClass(RestApplication.class);
        
        // Ajouter les packages pour la sérialisation JSON
        rc.packages("com.example.controller");
        rc.register(org.glassfish.jersey.jackson.JacksonFeature.class);
        
        // Créer et démarrer une nouvelle instance du serveur Grizzly HTTP
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:3000/"), rc);
        
        // Ajouter un shutdown hook pour nettoyer les ressources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Arrêt du serveur...");
            if (server != null) {
                server.shutdownNow();
            }
            JPAUtil.closeEntityManagerFactory();
            logger.info("Serveur arrêté");
        }));
        
        return server;
    }
    
    /**
     * Méthode principale pour démarrer le serveur
     */
    public static void main(String[] args) {
        try {
            startServer();
            logger.info("Serveur démarré sur http://0.0.0.0:3000/");
            
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Serveur interrompu");
        } catch (Exception e) {
            logger.severe("Erreur fatale lors du démarrage du serveur: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
