package com.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;

/**
 * Classe utilitaire pour gérer l'EntityManagerFactory et l'EntityManager
 */
public class JPAUtil {
    
    private static final String PERSISTENCE_UNIT_NAME = "example-pu";
    private static EntityManagerFactory entityManagerFactory;
    
    static {
        try {
            // Exécuter les migrations Flyway avant d'initialiser JPA
            runFlywayMigrations();
            
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Exécute les migrations Flyway
     */
    private static void runFlywayMigrations() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource("jdbc:postgresql://192.168.220.8:5432/appdb", "admin", "admin")
                    .locations("filesystem:src/main/resources/db/migration")
                    .load();
            flyway.migrate();
            System.out.println("Migrations Flyway exécutées avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution des migrations Flyway: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Échec des migrations Flyway", e);
        }
    }
    
    /**
     * Obtient une instance d'EntityManagerFactory
     */
public static EntityManagerFactory getEntityManagerFactory() {
    if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }
    return entityManagerFactory;
}

    /**
     * Obtient une instance d'EntityManager
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Ferme l'EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
    
    /**
     * Ferme l'EntityManager
     */
    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
