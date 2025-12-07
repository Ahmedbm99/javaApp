package com.example.repository;

import com.example.model.User;
import com.example.util.JPAUtil;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour UserRepository
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    
    private static UserRepository userRepository;
    
    @BeforeAll
    static void setUp() {
        JPAUtil.init("example-pu-test", "src/main/resources/vars/flyway_test.conf");
        userRepository = new UserRepository();
    }



    @AfterAll
    static void tearDown() {
        JPAUtil.closeEntityManagerFactory();
    }

    @Test
    @Order(1)
    @DisplayName("Test de création d'un utilisateur")
    void testSaveUser() {
        User user = new User("johndoe", "john.doe@example.com", "John", "Doe");
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("johndoe", savedUser.getUsername());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertNotNull(savedUser.getCreatedAt());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test de recherche d'un utilisateur par ID")
    void testFindById() {
        User user = new User("janedoe", "jane.doe@example.com", "Jane", "Doe");
        User savedUser = userRepository.save(user);
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("janedoe", foundUser.get().getUsername());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test de recherche d'un utilisateur par username")
    void testFindByUsername() {
        User user = new User("bobsmith", "bob.smith@example.com");
        userRepository.save(user);
        
        Optional<User> foundUser = userRepository.findByUsername("bobsmith");
        
        assertTrue(foundUser.isPresent());
        assertEquals("bobsmith", foundUser.get().getUsername());
        assertEquals("bob.smith@example.com", foundUser.get().getEmail());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test de recherche d'un utilisateur par email")
    void testFindByEmail() {
        User user = new User("alicewonder", "alice.wonder@example.com", "Alice", "Wonder");
        userRepository.save(user);
        
        Optional<User> foundUser = userRepository.findByEmail("alice.wonder@example.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals("alicewonder", foundUser.get().getUsername());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test de récupération de tous les utilisateurs")
    void testFindAll() {
        List<User> users = userRepository.findAll();
        
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 4); // Au moins les 4 utilisateurs créés dans les tests précédents
    }
    
    @Test
    @Order(6)
    @DisplayName("Test de mise à jour d'un utilisateur")
    void testUpdateUser() {
        User user = new User("updateuser", "update@example.com", "Update", "User");
        User savedUser = userRepository.save(user);
        
        savedUser.setFirstName("Updated");
        savedUser.setLastName("Name");
        User updatedUser = userRepository.update(savedUser);
        
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertNotNull(updatedUser.getUpdatedAt());
    }
    
    @Test
    @Order(7)
    @DisplayName("Test de suppression d'un utilisateur")
    void testDeleteUser() {
        User user = new User("todelete", "delete@example.com");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        userRepository.deleteById(userId);
        
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }
    
    @Test
    @Order(8)
    @DisplayName("Test de comptage des utilisateurs")
    void testCount() {
        long count = userRepository.count();
        assertTrue(count >= 0);
    }
    
    @Test
    @Order(9)
    @DisplayName("Test de recherche d'un utilisateur inexistant")
    void testFindNonExistentUser() {
        Optional<User> user = userRepository.findById(99999L);
        assertFalse(user.isPresent());
    }
}
