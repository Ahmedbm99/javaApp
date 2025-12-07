package com.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour gérer l'EntityManagerFactory et l'EntityManager.
 */
public final class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;
    private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());

    private JPAUtil() {
        // util class
    }

    /**
     * Initialise Flyway et JPA pour une persistence-unit donnée.
     */
    public static synchronized void init(String persistenceUnitName, String flywayConfigPath) {
        Objects.requireNonNull(persistenceUnitName, "PersistenceUnitName ne doit pas être null");
        Objects.requireNonNull(flywayConfigPath, "flywayConfigPath ne doit pas être null");

        try {
            runFlywayMigrations(flywayConfigPath);
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'initialisation JPA/Flyway", e);
            throw new IllegalStateException("Impossible d'initialiser JPA/Flyway", e);
        }
    }

    /**
     * Exécute les migrations Flyway à partir d'un fichier de configuration.
     */
    private static void runFlywayMigrations(String configFilePath) {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            props.load(fis);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de la configuration Flyway", e);
            throw new IllegalStateException("Impossible de charger le fichier de configuration Flyway", e);
        }

        try {
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
            logger.info(() -> "Migrations Flyway exécutées avec succès sur " + props.getProperty("flyway.url"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Échec des migrations Flyway", e);
            throw new IllegalStateException("Échec des migrations Flyway", e);
        }
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory non initialisée. Appeler JPAUtil.init(...) d'abord.");
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        try {
            return getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Impossible de créer un EntityManager", e);
            throw new IllegalStateException("Erreur lors de la création de l'EntityManager", e);
        }
    }

    public static synchronized void closeEntityManagerFactory() {
        try {
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur lors de la fermeture de l'EntityManagerFactory", e);
        }
    }

    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager == null) {
            return;
        }
        try {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur lors de la fermeture de l'EntityManager", e);
        }
    }
}
