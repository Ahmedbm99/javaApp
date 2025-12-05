package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe App
 */
class AppTest {
    
    @Test
    void testAppExists() {
        App app = new App();
        assertNotNull(app);
    }
}
