package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les opérations métier sur les produits
 */
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService() {
        this.productRepository = new ProductRepository();
    }
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Crée un nouveau produit
     */
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est requis");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix doit être positif ou nul");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("La quantité doit être positive ou nulle");
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Récupère un produit par son ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Récupère tous les produits
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Récupère les produits par catégorie
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    /**
     * Récupère les produits dont le prix est inférieur ou égal au prix donné
     */
    public List<Product> getProductsByMaxPrice(BigDecimal maxPrice) {
        return productRepository.findByPriceLessThanOrEqual(maxPrice);
    }
    
    /**
     * Récupère les produits en stock
     */
    public List<Product> getProductsInStock() {
        return productRepository.findInStock();
    }
    
    /**
     * Met à jour un produit
     */
    public Product updateProduct(Long id, Product product) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            throw new IllegalArgumentException("Produit non trouvé avec l'ID: " + id);
        }
        
        Product productToUpdate = existingProduct.get();
        
        // Mettre à jour les champs non nuls
        if (product.getName() != null && !product.getName().trim().isEmpty()) {
            productToUpdate.setName(product.getName());
        }
        
        if (product.getDescription() != null) {
            productToUpdate.setDescription(product.getDescription());
        }
        
        if (product.getPrice() != null) {
            if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le prix doit être positif ou nul");
            }
            productToUpdate.setPrice(product.getPrice());
        }
        
        if (product.getQuantity() != null) {
            if (product.getQuantity() < 0) {
                throw new IllegalArgumentException("La quantité doit être positive ou nulle");
            }
            productToUpdate.setQuantity(product.getQuantity());
        }
        
        if (product.getCategory() != null) {
            productToUpdate.setCategory(product.getCategory());
        }
        
        return productRepository.update(productToUpdate);
    }
    
    /**
     * Supprime un produit
     */
    public boolean deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return false;
        }
        
        productRepository.deleteById(id);
        return true;
    }
    
    /**
     * Compte le nombre de produits
     */
    public long countProducts() {
        return productRepository.count();
    }
}
