# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (offline mode)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build project and create  JAR
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime with JRE only
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy  JAR from build stage
COPY --from=build /app/target/javaApp-repo-1.0.jar app.jar
# Créer le dossier config
RUN mkdir -p /app/config

# Copier le fichier flyway.conf depuis Ansible
COPY ansible/vars/flyway.conf /app/config/flyway.conf
# Expose port (your app port)
EXPOSE 3000

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
