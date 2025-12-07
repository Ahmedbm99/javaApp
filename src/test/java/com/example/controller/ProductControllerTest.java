package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ProductController
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    
    private ProductService productService;
    
    private ProductController productController;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product("Laptop", "Ordinateur portable", 
                                 new BigDecimal("999.99"), 10, "Electronics");
        testProduct.setId(1L);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("Test GET /api/products - Récupérer tous les produits")
    void testGetAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);
        
        // Act
        Response response = productController.getAllProducts();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(productService, times(1)).getAllProducts();
    }
    
    @Test
    @DisplayName("Test GET /api/products/{id} - Récupérer un produit existant")
    void testGetProductById_Success() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        Response response = productController.getProductById(1L);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(testProduct, response.getEntity());
        verify(productService, times(1)).getProductById(1L);
    }
    
    @Test
    @DisplayName("Test GET /api/products/{id} - Produit non trouvé")
    void testGetProductById_NotFound() {
        // Arrange
        when(productService.getProductById(999L)).thenReturn(Optional.empty());
        
        // Act
        Response response = productController.getProductById(999L);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(productService, times(1)).getProductById(999L);
    }
    
    @Test
    @DisplayName("Test POST /api/products - Créer un produit")
    void testCreateProduct_Success() {
        // Arrange
        Product newProduct = new Product("Mouse", "Souris", 
                                        new BigDecimal("29.99"), 20, "Electronics");
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Response response = productController.createProduct(newProduct);
        
        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(testProduct, response.getEntity());
        verify(productService, times(1)).createProduct(newProduct);
    }
    
    @Test
    @DisplayName("Test POST /api/products - Erreur de validation")
    void testCreateProduct_BadRequest() {
        // Arrange
        Product invalidProduct = new Product("", null, new BigDecimal("-10"), -5, null);
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Le nom du produit est requis"));
        
        // Act
        Response response = productController.createProduct(invalidProduct);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(productService, times(1)).createProduct(invalidProduct);
    }
    
    @Test
    @DisplayName("Test PUT /api/products/{id} - Mettre à jour un produit")
    void testUpdateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product("Laptop Pro", "Ordinateur portable Pro", 
                                            new BigDecimal("1299.99"), 5, "Electronics");
        updatedProduct.setId(1L);
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);
        
        // Act
        Response response = productController.updateProduct(1L, updatedProduct);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(updatedProduct, response.getEntity());
        verify(productService, times(1)).updateProduct(1L, updatedProduct);
    }
    
    @Test
    @DisplayName("Test PUT /api/products/{id} - Produit non trouvé")
    void testUpdateProduct_NotFound() {
        // Arrange
        Product updateData = new Product("Product", "Description", 
                                        new BigDecimal("100"), 10, "Category");
        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Produit non trouvé avec l'ID: 999"));
        
        // Act
        Response response = productController.updateProduct(999L, updateData);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(productService, times(1)).updateProduct(999L, updateData);
    }
    
    @Test
    @DisplayName("Test DELETE /api/products/{id} - Supprimer un produit")
    void testDeleteProduct_Success() {
        // Arrange
        when(productService.deleteProduct(1L)).thenReturn(true);
        
        // Act
        Response response = productController.deleteProduct(1L);
        
        // Assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(productService, times(1)).deleteProduct(1L);
    }
    
    @Test
    @DisplayName("Test DELETE /api/products/{id} - Produit non trouvé")
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productService.deleteProduct(999L)).thenReturn(false);
        
        // Act
        Response response = productController.deleteProduct(999L);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(productService, times(1)).deleteProduct(999L);
    }
    
    @Test
    @DisplayName("Test GET /api/products/category/{category} - Produits par catégorie")
    void testGetProductsByCategory() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("Electronics")).thenReturn(products);
        
        // Act
        Response response = productController.getProductsByCategory("Electronics");
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(productService, times(1)).getProductsByCategory("Electronics");
    }
    
    @Test
    @DisplayName("Test GET /api/products/instock - Produits en stock")
    void testGetProductsInStock() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsInStock()).thenReturn(products);
        
        // Act
        Response response = productController.getProductsInStock();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(productService, times(1)).getProductsInStock();
    }
    
    @Test
    @DisplayName("Test GET /api/products/price/{maxPrice} - Produits par prix maximum")
    void testGetProductsByMaxPrice() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        BigDecimal maxPrice = new BigDecimal("1000.00");
        when(productService.getProductsByMaxPrice(maxPrice)).thenReturn(products);
        
        // Act
        Response response = productController.getProductsByMaxPrice(maxPrice);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(productService, times(1)).getProductsByMaxPrice(maxPrice);
    }
    
    @Test
    @DisplayName("Test GET /api/products/count - Compter les produits")
    void testCountProducts() {
        // Arrange
        when(productService.countProducts()).thenReturn(10L);
        
        // Act
        Response response = productController.countProducts();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(productService, times(1)).countProducts();
    }
}
