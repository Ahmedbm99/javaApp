package com.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour gérer l'EntityManagerFactory et l'EntityManager
 */
public class JPAUtil {
    
    private static final String PERSISTENCE_UNIT_NAME = "example-pu-test";
    private static EntityManagerFactory entityManagerFactory;
        private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());
        

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
    Properties props = new Properties();

    try (FileInputStream fis = new FileInputStream("src/main/resources/vars/flyway_test.conf")) {
        props.load(fis);

        Flyway flyway = Flyway.configure()
                .dataSource(
                    props.getProperty("flyway.url"),
                    props.getProperty("flyway.user"),
                    props.getProperty("flyway.password")
                )
                .locations(props.getProperty("flyway.locations"))
                .baselineOnMigrate(Boolean.parseBoolean(props.getProperty("flyway.baselineOnMigrate", "true")))
                .load();

        flyway.migrate();
        logger.info("Migrations Flyway exécutées avec succès");
    } catch (Exception e) {
        logger.severe("Échec des migrations Flyway: " + e.getMessage());
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
