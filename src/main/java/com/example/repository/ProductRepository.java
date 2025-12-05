package com.example.repository;

import com.example.model.Product;
import com.example.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour gérer les opérations CRUD sur l'entité Product
 */
public class ProductRepository {
    
    /**
     * Enregistre un nouveau produit
     */
    public Product save(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(product);
            transaction.commit();
            return product;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de l'enregistrement du produit", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve un produit par son ID
     */
    public Optional<Product> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Product product = em.find(Product.class, id);
            return Optional.ofNullable(product);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Récupère tous les produits
     */
    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve les produits par catégorie
     */
    public List<Product> findByCategory(String category) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.category = :category", Product.class);
            query.setParameter("category", category);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve les produits dont le prix est inférieur ou égal à un montant donné
     */
    public List<Product> findByPriceLessThanOrEqual(BigDecimal price) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.price <= :price ORDER BY p.price", Product.class);
            query.setParameter("price", price);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve les produits en stock (quantity > 0)
     */
    public List<Product> findInStock() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.quantity > 0", Product.class);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Met à jour un produit
     */
    public Product update(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Product mergedProduct = em.merge(product);
            transaction.commit();
            return mergedProduct;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à jour du produit", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Supprime un produit par son ID
     */
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression du produit", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Compte le nombre total de produits
     */
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Product p", Long.class);
            return query.getSingleResult();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}
