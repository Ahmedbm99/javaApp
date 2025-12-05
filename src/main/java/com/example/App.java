package com.example;

/**
 * Classe principale de l'application
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Bienvenue dans le projet Java Maven!");
        
        Calculator calculator = new Calculator();
        System.out.println("Addition: 5 + 3 = " + calculator.add(5, 3));
        System.out.println("Soustraction: 10 - 4 = " + calculator.subtract(10, 4));
        System.out.println("Multiplication: 6 * 7 = " + calculator.multiply(6, 7));
        System.out.println("Division: 20 / 4 = " + calculator.divide(20, 4));
    }
}
