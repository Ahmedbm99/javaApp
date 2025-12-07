package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserController
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    
    private UserService userService;
    
    private UserController userController;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("johndoe", "john.doe@example.com", "John", "Doe");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("Test GET /api/users - Récupérer tous les utilisateurs")
    void testGetAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);
        
        // Act
        Response response = userController.getAllUsers();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(userService, times(1)).getAllUsers();
    }
    
    @Test
    @DisplayName("Test GET /api/users/{id} - Récupérer un utilisateur existant")
    void testGetUserById_Success() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        Response response = userController.getUserById(1L);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(testUser, response.getEntity());
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    @DisplayName("Test GET /api/users/{id} - Utilisateur non trouvé")
    void testGetUserById_NotFound() {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());
        
        // Act
        Response response = userController.getUserById(999L);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userService, times(1)).getUserById(999L);
    }
    
    @Test
    @DisplayName("Test POST /api/users - Créer un utilisateur")
    void testCreateUser_Success() {
        // Arrange
        User newUser = new User("janedoe", "jane.doe@example.com", "Jane", "Doe");
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        
        // Act
        Response response = userController.createUser(newUser);
        
        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(testUser, response.getEntity());
        verify(userService, times(1)).createUser(newUser);
    }
    
    @Test
    @DisplayName("Test POST /api/users - Erreur de validation")
    void testCreateUser_BadRequest() {
        // Arrange
        User invalidUser = new User("", "invalid-email", null, null);
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Le username est requis"));
        
        // Act
        Response response = userController.createUser(invalidUser);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(userService, times(1)).createUser(invalidUser);
    }
    
    @Test
    @DisplayName("Test PUT /api/users/{id} - Mettre à jour un utilisateur")
    void testUpdateUser_Success() {
        // Arrange
        User updatedUser = new User("johndoe", "john.updated@example.com", "John", "Updated");
        updatedUser.setId(1L);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);
        
        // Act
        Response response = userController.updateUser(1L, updatedUser);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(updatedUser, response.getEntity());
        verify(userService, times(1)).updateUser(1L, updatedUser);
    }
    
    @Test
    @DisplayName("Test PUT /api/users/{id} - Utilisateur non trouvé")
    void testUpdateUser_NotFound() {
        // Arrange
        User updateData = new User("johndoe", "john@example.com", "John", "Doe");
        when(userService.updateUser(anyLong(), any(User.class)))
                .thenThrow(new IllegalArgumentException("Utilisateur non trouvé avec l'ID: 999"));
        
        // Act
        Response response = userController.updateUser(999L, updateData);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(userService, times(1)).updateUser(999L, updateData);
    }
    
    @Test
    @DisplayName("Test DELETE /api/users/{id} - Supprimer un utilisateur")
    void testDeleteUser_Success() {
        // Arrange
        when(userService.deleteUser(1L)).thenReturn(true);
        
        // Act
        Response response = userController.deleteUser(1L);
        
        // Assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(userService, times(1)).deleteUser(1L);
    }
    
    @Test
    @DisplayName("Test DELETE /api/users/{id} - Utilisateur non trouvé")
    void testDeleteUser_NotFound() {
        // Arrange
        when(userService.deleteUser(999L)).thenReturn(false);
        
        // Act
        Response response = userController.deleteUser(999L);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userService, times(1)).deleteUser(999L);
    }
    
    @Test
    @DisplayName("Test GET /api/users/count - Compter les utilisateurs")
    void testCountUsers() {
        // Arrange
        when(userService.countUsers()).thenReturn(5L);
        
        // Act
        Response response = userController.countUsers();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(userService, times(1)).countUsers();
    }
}
