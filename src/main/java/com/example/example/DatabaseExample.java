package com.example.example;

import com.example.model.Product;
import com.example.model.User;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.util.JPAUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Classe d'exemple démontrant l'utilisation de JPA/Hibernate avec les repositories
 */
public class DatabaseExample {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Exemple d'utilisation de JPA/Hibernate ===\n");
            
            UserRepository userRepository = new UserRepository();
            ProductRepository productRepository = new ProductRepository();
            
            // Exemple 1: Création d'utilisateurs
            System.out.println("1. Création d'utilisateurs:");
            User user1 = new User("alice", "alice@example.com", "Alice", "Dupont");
            User user2 = new User("bob", "bob@example.com", "Bob", "Martin");
            userRepository.save(user1);
            userRepository.save(user2);
            System.out.println("   - Utilisateur créé: " + user1.getUsername());
            System.out.println("   - Utilisateur créé: " + user2.getUsername());
            
            // Exemple 2: Création de produits
            System.out.println("\n2. Création de produits:");
            Product product1 = new Product("Laptop", "Ordinateur portable", 
                                          new BigDecimal("1299.99"), 5, "Electronics");
            Product product2 = new Product("Mouse", "Souris sans fil", 
                                          new BigDecimal("29.99"), 20, "Electronics");
            Product product3 = new Product("T-Shirt", "T-shirt en coton", 
                                          new BigDecimal("19.99"), 50, "Clothing");
            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);
            System.out.println("   - Produit créé: " + product1.getName() + " - " + product1.getPrice() + "€");
            System.out.println("   - Produit créé: " + product2.getName() + " - " + product2.getPrice() + "€");
            System.out.println("   - Produit créé: " + product3.getName() + " - " + product3.getPrice() + "€");
            
            // Exemple 3: Recherche d'utilisateurs
            System.out.println("\n3. Recherche d'utilisateurs:");
            userRepository.findByUsername("alice").ifPresent(user -> 
                System.out.println("   - Utilisateur trouvé: " + user.getFirstName() + " " + user.getLastName()));
            
            // Exemple 4: Recherche de produits
            System.out.println("\n4. Recherche de produits:");
            List<Product> electronics = productRepository.findByCategory("Electronics");
            System.out.println("   - Produits électroniques trouvés: " + electronics.size());
            electronics.forEach(p -> System.out.println("     * " + p.getName() + " - " + p.getPrice() + "€"));
            
            // Exemple 5: Produits en stock
            System.out.println("\n5. Produits en stock:");
            List<Product> inStock = productRepository.findInStock();
            System.out.println("   - Nombre de produits en stock: " + inStock.size());
            
            // Exemple 6: Statistiques
            System.out.println("\n6. Statistiques:");
            System.out.println("   - Nombre total d'utilisateurs: " + userRepository.count());
            System.out.println("   - Nombre total de produits: " + productRepository.count());
            
            // Exemple 7: Mise à jour
            System.out.println("\n7. Mise à jour d'un produit:");
            product1.setPrice(new BigDecimal("1199.99"));
            product1.setQuantity(3);
            Product updatedProduct = productRepository.update(product1);
            System.out.println("   - Prix mis à jour: " + updatedProduct.getPrice() + "€");
            System.out.println("   - Stock mis à jour: " + updatedProduct.getQuantity());
            
            System.out.println("\n=== Fin de l'exemple ===");
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JPAUtil.closeEntityManagerFactory();
        }
    }
}
