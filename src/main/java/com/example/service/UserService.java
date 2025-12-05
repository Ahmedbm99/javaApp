package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les opérations métier sur les utilisateurs
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService() {
        this.userRepository = new UserRepository();
    }
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Crée un nouvel utilisateur
     */
    public User createUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Le username est requis");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est requis");
        }
        
        // Vérifier si l'username existe déjà
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Le username existe déjà");
        }
        
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("L'email existe déjà");
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Récupère un utilisateur par son ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Récupère un utilisateur par son username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Récupère tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Met à jour un utilisateur
     */
    public User updateUser(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id);
        }
        
        User userToUpdate = existingUser.get();
        
        // Mettre à jour les champs non nuls
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            // Vérifier l'unicité si le username a changé
            if (!user.getUsername().equals(userToUpdate.getUsername())) {
                if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                    throw new IllegalArgumentException("Le username existe déjà");
                }
            }
            userToUpdate.setUsername(user.getUsername());
        }
        
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            // Vérifier l'unicité si l'email a changé
            if (!user.getEmail().equals(userToUpdate.getEmail())) {
                if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("L'email existe déjà");
                }
            }
            userToUpdate.setEmail(user.getEmail());
        }
        
        if (user.getFirstName() != null) {
            userToUpdate.setFirstName(user.getFirstName());
        }
        
        if (user.getLastName() != null) {
            userToUpdate.setLastName(user.getLastName());
        }
        
        return userRepository.update(userToUpdate);
    }
    
    /**
     * Supprime un utilisateur
     */
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return false;
        }
        
        userRepository.deleteById(id);
        return true;
    }
    
    /**
     * Compte le nombre d'utilisateurs
     */
    public long countUsers() {
        return userRepository.count();
    }
}
