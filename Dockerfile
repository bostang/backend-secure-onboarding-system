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

# Hapus baris ini: COPY .env* ./
# File .env hanya untuk pengembangan lokal dan tidak boleh di-bundle ke dalam image produksi.
# Variabel lingkungan akan disuntikkan oleh Kubernetes.

# Copy Firebase service account key (pastikan ini aman dan tidak di-commit ke repo)
# Hanya copy jika file ini benar-benar dibutuhkan di dalam JAR saat runtime.
# Jika hanya untuk konfigurasi, lebih baik di-mount sebagai Secret Kubernetes.
# Namun, jika Spring Boot membaca file ini dari classpath, maka harus ada di sini.
COPY src/main/resources/model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json ./src/main/resources/

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR dari build stage
COPY --from=build /app/target/*.jar app.jar

# Hapus baris ini: COPY --from=build /app/.env* ./
# Hapus baris ini: ENV $(cat .env | xargs)
# Variabel lingkungan akan disuntikkan oleh Kubernetes, bukan dari file .env di dalam image.

# Expose port
EXPOSE 8080

# Run aplikasi dengan nama JAR yang fixed
# Pastikan semua variabel lingkungan yang dibutuhkan oleh application.properties
# (seperti DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, JWT_EXPIRATION,
# SERVER_PORT, FIREBASE_CONFIG_PATH, BACKEND_BASE_URL, DUKCAPIL_SERVICE_URL,
# dan FRONTEND_URL) disuntikkan oleh Kubernetes Deployment YAML.
CMD ["java", "-jar", "app.jar"]


### Dockerfile for Secure Onboarding System untuk deployment local ###

# # Multi-stage build untuk Secure Onboarding
# # Stage 1: Build stage
# FROM maven:3.9.8-eclipse-temurin-21 AS build
# WORKDIR /app

# # Copy pom.xml dan download dependencies
# COPY pom.xml .
# COPY mvnw .
# COPY .mvn .mvn
# RUN ./mvnw dependency:go-offline

# # Copy source code
# COPY src ./src

# # Copy environment files dengan default fallback
# COPY .env* ./
# COPY src/main/resources/model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json* ./src/main/resources/

# # Build aplikasi
# RUN ./mvnw clean package -DskipTests

# # Stage 2: Runtime stage
# FROM eclipse-temurin:21-jre
# WORKDIR /app

# # Copy JAR dari build stage
# COPY --from=build /app/target/*.jar app.jar

# # Copy .env file ke runtime (jika ada)
# COPY --from=build /app/.env* ./

# # Environment variables (match dengan application.properties)
# # ENV DB_URL=jdbc:postgresql://postgres-db:5432/customer_registration
# # ENV DB_USERNAME=postgres
# # ENV DB_PASSWORD=postgres123
# # ENV JWT_SECRET=aB3dF6gH9jK2mN5pQ8rS1tU4vW7xY0zA3bC6dE9fG2hJ5kL8mO1pR4sT7uV0wX3y
# # ENV JWT_EXPIRATION=86400000
# # ENV SERVER_PORT=8080
# # ENV FIREBASE_CONFIG_PATH=model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json

# # inject environment variables from .env file
# ENV $(cat .env | xargs)

# # Expose port
# EXPOSE 8080

# # Run aplikasi dengan nama JAR yang fixed
# CMD ["java", "-jar", "app.jar"]
