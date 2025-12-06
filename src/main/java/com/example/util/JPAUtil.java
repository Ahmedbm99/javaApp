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

    private static EntityManagerFactory entityManagerFactory;
    private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());

    /**
     * Initialise Flyway et JPA pour une persistence-unit donnée.
     */
    public static void init(String persistenceUnitName, String flywayConfigPath) {
        runFlywayMigrations(flywayConfigPath);
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        logger.info("EntityManagerFactory initialisée pour " + persistenceUnitName);
    }

    /**
     * Exécute les migrations Flyway à partir d'un fichier de configuration
     */
    private static void runFlywayMigrations(String configFilePath) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
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
            logger.info("Migrations Flyway exécutées avec succès sur " + props.getProperty("flyway.url"));
        } catch (Exception e) {
            logger.severe("Échec des migrations Flyway: " + e.getMessage());
            throw new RuntimeException("Échec des migrations Flyway", e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory non initialisée. Appeler JPAUtil.init(...) d'abord.");
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
