package com.example.config;

import com.example.controller.ProductController;
import com.example.controller.UserController;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe d'application REST pour configurer JAX-RS
 */
@ApplicationPath("/")
public class RestApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Enregistrer les contrôleurs
        classes.add(UserController.class);
        classes.add(ProductController.class);
        
        return classes;
    }
}
