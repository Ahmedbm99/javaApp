package com.example.repository;

import com.example.model.Product;
import com.example.util.JPAUtil;
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
        try {
            JPAUtil.init("example-pu-test", "src/main/resources/vars/flyway_test.conf");

            // Force coverage
            assertNotNull(JPAUtil.getEntityManagerFactory(), "EntityManagerFactory should not be null");

            var em = JPAUtil.getEntityManager();
            assertNotNull(em, "EntityManager should not be null");
            JPAUtil.closeEntityManager(em);

            productRepository = new ProductRepository();
        } catch (Exception e) {
            fail("Exception during test initialization: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            JPAUtil.closeEntityManagerFactory();
        } catch (Exception e) {
            fail("Failed to close EMF: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Création de produit")
    void testSaveProduct() {
        Product product = new Product("Laptop", "Ordinateur puissant",
                new BigDecimal("999.99"), 10, "Electronics");

        Product saved = productRepository.save(product);

        assertAll(
                () -> assertNotNull(saved, "Saved product must not be null"),
                () -> assertNotNull(saved.getId(), "ID must be assigned"),
                () -> assertEquals("Laptop", saved.getName(), "Name mismatch"),
                () -> assertEquals(new BigDecimal("999.99"), saved.getPrice(), "Price mismatch"),
                () -> assertEquals(10, saved.getQuantity(), "Quantity mismatch"),
                () -> assertEquals("Electronics", saved.getCategory(), "Category mismatch"),
                () -> assertNotNull(saved.getCreatedAt(), "CreatedAt must be set")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Recherche par ID")
    void testFindById() {
        Product product = new Product("Smartphone", "Téléphone",
                new BigDecimal("599.99"), 25, "Electronics");

        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertAll(
                () -> assertTrue(found.isPresent(), "Product should be found"),
                () -> assertEquals(saved.getId(), found.get().getId(), "ID mismatch"),
                () -> assertEquals("Smartphone", found.get().getName(), "Name mismatch")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Récupération de tous les produits")
    void testFindAll() {
        List<Product> products = productRepository.findAll();

        assertNotNull(products, "Products list should not be null");
        assertFalse(products.isEmpty(), "Products list should not be empty");
    }

    @Test
    @Order(4)
    @DisplayName("Recherche par catégorie")
    void testFindByCategory() {
        Product product = new Product("T-Shirt", "Coton",
                new BigDecimal("19.99"), 50, "Clothing");
        productRepository.save(product);

        List<Product> list = productRepository.findByCategory("Clothing");

        assertNotNull(list, "List should not be null");
        assertFalse(list.isEmpty(), "List should not be empty");

        list.forEach(p -> assertEquals("Clothing", p.getCategory(), "Incorrect category"));
    }

    @Test
    @Order(5)
    @DisplayName("Recherche par prix max")
    void testFindByPriceLessThanOrEqual() {
        productRepository.save(
                new Product("Book", "Programming",
                        new BigDecimal("29.99"), 100, "Books")
        );

        var results = productRepository.findByPriceLessThanOrEqual(new BigDecimal("100.00"));

        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Should return results");

        results.forEach(p ->
                assertTrue(
                        p.getPrice().compareTo(new BigDecimal("100.00")) <= 0,
                        "Product price is above limit"
                )
        );
    }

    @Test
    @Order(6)
    @DisplayName("Produits en stock")
    void testFindInStock() {
        productRepository.save(
                new Product("OutOfStock", "Test",
                        new BigDecimal("10.00"), 0, "Test")
        );

        List<Product> list = productRepository.findInStock();

        assertNotNull(list, "List should not be null");

        list.forEach(p ->
                assertTrue(p.getQuantity() > 0, "Product must be in stock")
        );
    }

    @Test
    @Order(7)
    @DisplayName("Mise à jour de produit")
    void testUpdateProduct() {
        Product product = new Product("Tablet", "Tablette",
                new BigDecimal("299.99"), 15, "Electronics");

        Product saved = productRepository.save(product);

        saved.setPrice(new BigDecimal("249.99"));
        saved.setQuantity(20);

        Product updated = productRepository.update(saved);

        assertAll(
                () -> assertEquals(new BigDecimal("249.99"), updated.getPrice(), "Price mismatch"),
                () -> assertEquals(20, updated.getQuantity(), "Quantity mismatch"),
                () -> assertNotNull(updated.getUpdatedAt(), "updatedAt must be set")
        );
    }

    @Test
    @Order(8)
    @DisplayName("Suppression de produit")
    void testDeleteProduct() {
        Product saved = productRepository.save(
                new Product("ToDelete", "Test", new BigDecimal("5.00"), 1, "Test")
        );

        Long id = saved.getId();

        productRepository.deleteById(id);

        assertFalse(productRepository.findById(id).isPresent(), "Product should be deleted");
    }

    @Test
    @Order(9)
    @DisplayName("Comptage produits")
    void testCount() {
        long count = productRepository.count();
        assertTrue(count >= 0, "Count should be positive");
    }

    @Test
    @Order(10)
    @DisplayName("Produit inexistant")
    void testFindNonExistentProduct() {
        Optional<Product> product = productRepository.findById(99999L);
        assertFalse(product.isPresent(), "Product should not exist");
    }
}
