# Stage 1: Build avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml (pour bénéficier du cache Docker)
COPY pom.xml .

# Télécharger les dépendances (mise en cache si pom.xml n'a pas changé)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Construire le projet (skip tests pour accélérer le build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime avec JRE seulement
FROM eclipse-temurin:17-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/javaApp-repo-1.0.jar app.jar

# Exposer un port si nécessaire (optionnel)
# EXPOSE 5000

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]