package com.example.repository;

import com.example.model.Product;
import com.example.util.JPAUtil;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ProductRepository
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryTest {
    
    private static ProductRepository productRepository;
    
    @BeforeAll
    static void setUp() {
        productRepository = new ProductRepository();
    }
    
    @AfterAll
    static void tearDown() {
        JPAUtil.closeEntityManagerFactory();
    }

    @Test
    @Order(1)
    @DisplayName("Test de création d'un produit")
    void testSaveProduct() {
        Product product = new Product("Laptop", "Ordinateur portable haute performance", 
                                     new BigDecimal("999.99"), 10, "Electronics");
        Product savedProduct = productRepository.save(product);
        
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals("Laptop", savedProduct.getName());
        assertEquals(new BigDecimal("999.99"), savedProduct.getPrice());
        assertEquals(10, savedProduct.getQuantity());
        assertEquals("Electronics", savedProduct.getCategory());
        assertNotNull(savedProduct.getCreatedAt());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test de recherche d'un produit par ID")
    void testFindById() {
        Product product = new Product("Smartphone", "Téléphone intelligent", 
                                     new BigDecimal("599.99"), 25, "Electronics");
        Product savedProduct = productRepository.save(product);
        
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
        assertEquals("Smartphone", foundProduct.get().getName());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test de récupération de tous les produits")
    void testFindAll() {
        List<Product> products = productRepository.findAll();
        
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertTrue(products.size() >= 2); // Au moins les 2 produits créés précédemment
    }
    
    @Test
    @Order(4)
    @DisplayName("Test de recherche de produits par catégorie")
    void testFindByCategory() {
        Product product = new Product("T-Shirt", "T-shirt en coton", 
                                     new BigDecimal("19.99"), 50, "Clothing");
        productRepository.save(product);
        
        List<Product> clothingProducts = productRepository.findByCategory("Clothing");
        
        assertNotNull(clothingProducts);
        assertFalse(clothingProducts.isEmpty());
        assertTrue(clothingProducts.stream().allMatch(p -> "Clothing".equals(p.getCategory())));
    }
    
    @Test
    @Order(5)
    @DisplayName("Test de recherche de produits par prix maximum")
    void testFindByPriceLessThanOrEqual() {
        Product cheapProduct = new Product("Book", "Livre de programmation", 
                                          new BigDecimal("29.99"), 100, "Books");
        productRepository.save(cheapProduct);
        
        List<Product> affordableProducts = productRepository.findByPriceLessThanOrEqual(new BigDecimal("100.00"));
        
        assertNotNull(affordableProducts);
        assertFalse(affordableProducts.isEmpty());
        assertTrue(affordableProducts.stream()
                .allMatch(p -> p.getPrice().compareTo(new BigDecimal("100.00")) <= 0));
    }
    
    @Test
    @Order(6)
    @DisplayName("Test de recherche de produits en stock")
    void testFindInStock() {
        Product outOfStockProduct = new Product("OutOfStock", "Produit épuisé", 
                                                new BigDecimal("10.00"), 0, "Test");
        productRepository.save(outOfStockProduct);
        
        List<Product> inStockProducts = productRepository.findInStock();
        
        assertNotNull(inStockProducts);
        assertTrue(inStockProducts.stream().allMatch(p -> p.getQuantity() > 0));
    }
    
    @Test
    @Order(7)
    @DisplayName("Test de mise à jour d'un produit")
    void testUpdateProduct() {
        Product product = new Product("Tablet", "Tablette", 
                                     new BigDecimal("299.99"), 15, "Electronics");
        Product savedProduct = productRepository.save(product);
        
        savedProduct.setPrice(new BigDecimal("249.99"));
        savedProduct.setQuantity(20);
        Product updatedProduct = productRepository.update(savedProduct);
        
        assertEquals(new BigDecimal("249.99"), updatedProduct.getPrice());
        assertEquals(20, updatedProduct.getQuantity());
        assertNotNull(updatedProduct.getUpdatedAt());
    }
    
    @Test
    @Order(8)
    @DisplayName("Test de suppression d'un produit")
    void testDeleteProduct() {
        Product product = new Product("ToDelete", "Produit à supprimer", 
                                     new BigDecimal("5.00"), 1, "Test");
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        
        productRepository.deleteById(productId);
        
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }
    
    @Test
    @Order(9)
    @DisplayName("Test de comptage des produits")
    void testCount() {
        long count = productRepository.count();
        assertTrue(count >= 0);
    }
    
    @Test
    @Order(10)
    @DisplayName("Test de recherche d'un produit inexistant")
    void testFindNonExistentProduct() {
        Optional<Product> product = productRepository.findById(99999L);
        assertFalse(product.isPresent());
    }
}
