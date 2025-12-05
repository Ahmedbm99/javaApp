# Projet Java Maven

Ce projet est une application Java standard avec Maven, incluant des tests unitaires, JPA/Hibernate pour la gestion de base de données, des contrôleurs REST avec JAX-RS/Jersey, et un Dockerfile pour la containerisation.

## Structure du projet

```
.
├── pom.xml                      # Configuration Maven
├── Dockerfile                   # Configuration Docker
├── .dockerignore               # Fichiers ignorés par Docker
├── .gitignore                  # Fichiers ignorés par Git
├── README.md                   # Ce fichier
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           ├── App.java              # Classe principale
    │   │           ├── Calculator.java       # Classe métier
    │   │           ├── model/
    │   │           │   ├── User.java         # Entité JPA User
    │   │           │   └── Product.java      # Entité JPA Product
    │   │           ├── repository/
    │   │           │   ├── UserRepository.java    # Repository pour User
    │   │           │   └── ProductRepository.java # Repository pour Product
    │   │           ├── service/
    │   │           │   ├── UserService.java       # Service pour User
    │   │           │   └── ProductService.java    # Service pour Product
    │   │           ├── controller/
    │   │           │   ├── UserController.java    # Contrôleur REST pour User
    │   │           │   └── ProductController.java # Contrôleur REST pour Product
    │   │           ├── config/
    │   │           │   └── RestApplication.java   # Configuration REST
    │   │           ├── util/
    │   │           │   └── JPAUtil.java      # Utilitaire JPA
    │   │           ├── example/
    │   │           │   └── DatabaseExample.java # Exemple d'utilisation
    │   │           └── RestServer.java       # Serveur REST
    │   └── resources/
    │       └── META-INF/
    │           └── persistence.xml           # Configuration JPA
    └── test/
        └── java/
            └── com/
                └── example/
                    ├── AppTest.java
                    ├── CalculatorTest.java
                    ├── repository/
                    │   ├── UserRepositoryTest.java
                    │   └── ProductRepositoryTest.java
                    └── controller/
                        ├── UserControllerTest.java
                        └── ProductControllerTest.java
```

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- Docker (optionnel, pour la containerisation)

## Base de données

Le projet utilise H2 (base de données en mémoire) par défaut pour le développement et les tests. Vous pouvez facilement passer à MySQL ou PostgreSQL en modifiant le fichier `src/main/resources/META-INF/persistence.xml`.

### Configuration H2 (par défaut)

La base de données H2 est automatiquement créée en mémoire au démarrage de l'application. Aucune configuration supplémentaire n'est nécessaire.

### Configuration MySQL

1. Décommentez les lignes MySQL dans `persistence.xml`
2. Modifiez l'URL, l'utilisateur et le mot de passe
3. Créez la base de données :

```sql
CREATE DATABASE dbname CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Configuration PostgreSQL

1. Décommentez les lignes PostgreSQL dans `persistence.xml`
2. Modifiez l'URL, l'utilisateur et le mot de passe
3. Créez la base de données :

```sql
CREATE DATABASE dbname;
```

## Compilation et exécution

### Compiler le projet

```bash
mvn clean compile
```

### Exécuter l'application principale

```bash
mvn exec:java -Dexec.mainClass="com.example.App"
```

### Exécuter l'exemple avec la base de données

```bash
mvn exec:java -Dexec.mainClass="com.example.example.DatabaseExample"
```

### Démarrer le serveur REST

```bash
mvn exec:java -Dexec.mainClass="com.example.RestServer"
```

Le serveur REST sera accessible à l'adresse : `http://localhost:5000/api`

Ou après compilation :

```bash
mvn package
java -jar target/java-maven-project-1.0-SNAPSHOT.jar
```

## Tests unitaires

### Exécuter tous les tests

```bash
mvn test
```

### Exécuter un test spécifique

```bash
# Tests de Calculator
mvn test -Dtest=CalculatorTest

# Tests des repositories JPA
mvn test -Dtest=UserRepositoryTest
mvn test -Dtest=ProductRepositoryTest

# Tests des contrôleurs REST
mvn test -Dtest=UserControllerTest
mvn test -Dtest=ProductControllerTest
```

### Générer un rapport de couverture (nécessite un plugin supplémentaire)

```bash
mvn test jacoco:report
```

## Docker

### Construire l'image Docker

```bash
docker build -t java-maven-project .
```

### Exécuter le conteneur

```bash
docker run --rm java-maven-project
```

### Exécuter avec un nom personnalisé

```bash
docker run --name my-java-app --rm java-maven-project
```

## Technologies utilisées

- **Java 17** : Langage de programmation
- **Maven** : Outil de gestion de projet et de build
- **JUnit 5** : Framework de test unitaire
- **JPA (Jakarta Persistence)** : API de persistance Java
- **Hibernate** : Implémentation de JPA
- **JAX-RS (Jakarta REST)** : API pour les services web RESTful
- **Jersey** : Implémentation de JAX-RS
- **Grizzly** : Serveur HTTP pour Jersey
- **Jackson** : Sérialisation/désérialisation JSON
- **Mockito** : Framework de mock pour les tests
- **H2 Database** : Base de données en mémoire (développement/tests)
- **MySQL Connector** : Driver MySQL (optionnel, pour production)
- **PostgreSQL Driver** : Driver PostgreSQL (optionnel, pour production)
- **Docker** : Plateforme de containerisation

## Modèles de données

### User (Utilisateur)

- `id` : Identifiant unique (auto-généré)
- `username` : Nom d'utilisateur (unique)
- `email` : Email (unique)
- `firstName` : Prénom
- `lastName` : Nom de famille
- `createdAt` : Date de création (auto)
- `updatedAt` : Date de mise à jour (auto)

### Product (Produit)

- `id` : Identifiant unique (auto-généré)
- `name` : Nom du produit
- `description` : Description
- `price` : Prix (BigDecimal)
- `quantity` : Quantité en stock
- `category` : Catégorie
- `createdAt` : Date de création (auto)
- `updatedAt` : Date de mise à jour (auto)

## Fonctionnalités

### Calculator

Classe implémentant des opérations mathématiques de base :
- Addition
- Soustraction
- Multiplication
- Division (avec gestion de la division par zéro)

### Services

Les services fournissent la logique métier et la validation :

**UserService :**
- Validation des données utilisateur
- Vérification de l'unicité (username, email)
- Gestion des erreurs métier

**ProductService :**
- Validation des données produit
- Vérification des contraintes métier (prix, quantité)
- Gestion des erreurs métier

### Repositories

Les repositories fournissent des opérations CRUD complètes :

**UserRepository :**
- `save(User)` : Enregistrer un utilisateur
- `findById(Long)` : Trouver par ID
- `findByUsername(String)` : Trouver par username
- `findByEmail(String)` : Trouver par email
- `findAll()` : Récupérer tous les utilisateurs
- `update(User)` : Mettre à jour
- `deleteById(Long)` : Supprimer
- `count()` : Compter

**ProductRepository :**
- `save(Product)` : Enregistrer un produit
- `findById(Long)` : Trouver par ID
- `findAll()` : Récupérer tous les produits
- `findByCategory(String)` : Trouver par catégorie
- `findByPriceLessThanOrEqual(BigDecimal)` : Trouver par prix
- `findInStock()` : Trouver les produits en stock
- `update(Product)` : Mettre à jour
- `deleteById(Long)` : Supprimer
- `count()` : Compter

### API REST

Le projet expose une API REST complète avec les endpoints suivants :

#### Endpoints Utilisateurs (`/api/users`)

- `GET /api/users` : Récupère tous les utilisateurs
- `GET /api/users/{id}` : Récupère un utilisateur par son ID
- `POST /api/users` : Crée un nouvel utilisateur
- `PUT /api/users/{id}` : Met à jour un utilisateur
- `DELETE /api/users/{id}` : Supprime un utilisateur
- `GET /api/users/count` : Compte le nombre d'utilisateurs

#### Endpoints Produits (`/api/products`)

- `GET /api/products` : Récupère tous les produits
- `GET /api/products/{id}` : Récupère un produit par son ID
- `POST /api/products` : Crée un nouveau produit
- `PUT /api/products/{id}` : Met à jour un produit
- `DELETE /api/products/{id}` : Supprime un produit
- `GET /api/products/category/{category}` : Récupère les produits par catégorie
- `GET /api/products/instock` : Récupère les produits en stock
- `GET /api/products/price/{maxPrice}` : Récupère les produits par prix maximum
- `GET /api/products/count` : Compte le nombre de produits

#### Exemples d'utilisation de l'API REST

**Créer un utilisateur :**
```bash
curl -X POST http://localhost:5000/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "firstName": "Alice",
    "lastName": "Dupont"
  }'
```

**Récupérer tous les utilisateurs :**
```bash
curl http://localhost:5000/api/users
```

**Créer un produit :**
```bash
curl -X POST http://localhost:5000/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "Ordinateur portable",
    "price": 999.99,
    "quantity": 10,
    "category": "Electronics"
  }'
```

**Récupérer les produits par catégorie :**
```bash
curl http://localhost:5000/api/products/category/Electronics
```

## Exemple d'utilisation

```java
// Créer un utilisateur
UserRepository userRepo = new UserRepository();
User user = new User("alice", "alice@example.com", "Alice", "Dupont");
userRepo.save(user);

// Créer un produit
ProductRepository productRepo = new ProductRepository();
Product product = new Product("Laptop", "Ordinateur portable", 
                             new BigDecimal("999.99"), 10, "Electronics");
productRepo.save(product);

// Rechercher
Optional<User> foundUser = userRepo.findByUsername("alice");
List<Product> electronics = productRepo.findByCategory("Electronics");
```

## Auteur

Créé avec Maven, JPA/Hibernate et Docker

## Licence

Ce projet est fourni tel quel, à des fins éducatives.