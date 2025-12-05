package com.example.repository;

import com.example.model.User;
import com.example.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour gérer les opérations CRUD sur l'entité User
 */
public class UserRepository {
    
    /**
     * Enregistre un nouvel utilisateur
     */
    public User save(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve un utilisateur par son ID
     */
    public Optional<User> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, id);
            return Optional.ofNullable(user);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve un utilisateur par son username
     */
    public Optional<User> findByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            List<User> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Trouve un utilisateur par son email
     */
    public Optional<User> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            List<User> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Récupère tous les utilisateurs
     */
    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Met à jour un utilisateur
     */
    public User update(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            User mergedUser = em.merge(user);
            transaction.commit();
            return mergedUser;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Supprime un utilisateur par son ID
     */
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Compte le nombre total d'utilisateurs
     */
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u", Long.class);
            return query.getSingleResult();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}
