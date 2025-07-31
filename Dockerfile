# Multi-stage build untuk Secure Onboarding
# Stage 1: Build stage
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml dan download dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Copy environment files dengan default fallback
COPY .env* ./
COPY src/main/resources/model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json* ./src/main/resources/

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR dari build stage
COPY --from=build /app/target/*.jar app.jar

# Copy .env file ke runtime (jika ada)
COPY --from=build /app/.env* ./

# Environment variables (match dengan application.properties)
# ENV DB_URL=jdbc:postgresql://postgres-db:5432/customer_registration
# ENV DB_USERNAME=postgres
# ENV DB_PASSWORD=postgres123
# ENV JWT_SECRET=aB3dF6gH9jK2mN5pQ8rS1tU4vW7xY0zA3bC6dE9fG2hJ5kL8mO1pR4sT7uV0wX3y
# ENV JWT_EXPIRATION=86400000
# ENV SERVER_PORT=8080
# ENV FIREBASE_CONFIG_PATH=model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json
# ENV DUKCAPIL_SERVICE_URL=http://dukcapil-dummy:8081

# Expose port
EXPOSE 8080

# Run aplikasi dengan nama JAR yang fixed
CMD ["java", "-jar", "app.jar"]
