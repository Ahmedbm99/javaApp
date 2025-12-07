# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR and dependencies
COPY --from=build /app/target/javaApp-repo-1.0.jar app.jar
COPY --from=build /app/target/lib ./lib

EXPOSE 3000

# Run with classpath including lib/*
ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "com.example.RestServer"]
