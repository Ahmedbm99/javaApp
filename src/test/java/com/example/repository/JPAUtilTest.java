package com.example.repository;


import com.example.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JPAUtilTest {

    private static final String PERSISTENCE_UNIT = "example-pu-test";
    private static final String FLYWAY_CONF = "src/main/resources/vars/flyway_test.conf";

    @Test
    @Order(1)
    void testInitDirectly() {
        assertDoesNotThrow(() -> JPAUtil.init(PERSISTENCE_UNIT, FLYWAY_CONF));
    }

    @Test
    @Order(2)
    void testGetEntityManagerFactory() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        assertNotNull(emf);
        assertTrue(emf.isOpen());
    }

    @Test
    @Order(3)
    void testGetAndCloseEntityManager() {
        EntityManager em = JPAUtil.getEntityManager();
        assertNotNull(em);
        assertTrue(em.isOpen());
        JPAUtil.closeEntityManager(em);
        assertFalse(em.isOpen());
    }

    @Test
    @Order(4)
    void testCloseEntityManagerFactory() {
        // Act
        assertDoesNotThrow(() -> JPAUtil.closeEntityManagerFactory());

        // Assert: calling getEntityManagerFactory now should throw
        assertThrows(IllegalStateException.class, JPAUtil::getEntityManagerFactory);
    }
    @Test
    @Order(5)
    void testCloseEntityManagerWithNull() {
        assertDoesNotThrow(() -> JPAUtil.closeEntityManager(null));
    }

}
