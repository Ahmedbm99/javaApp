package com.example;

/**
 * Classe Calculator pour effectuer des opérations mathématiques de base
 */
public class Calculator {
    
    /**
     * Additionne deux nombres
     * @param a Premier nombre
     * @param b Deuxième nombre
     * @return La somme de a et b
     */
    public int add(int a, int b) {
        return a + b;
    }
    
    /**
     * Soustrait deux nombres
     * @param a Premier nombre
     * @param b Deuxième nombre
     * @return La différence de a et b
     */
    public int subtract(int a, int b) {
        return a - b;
    }
    
    /**
     * Multiplie deux nombres
     * @param a Premier nombre
     * @param b Deuxième nombre
     * @return Le produit de a et b
     */
    public int multiply(int a, int b) {
        return a * b;
    }
    
    /**
     * Divise deux nombres
     * @param a Dividende
     * @param b Diviseur
     * @return Le quotient de a et b
     * @throws IllegalArgumentException si b est égal à 0
     */
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division par zéro n'est pas autorisée");
        }
        return (double) a / b;
    }
}
