# Multi-stage build untuk Secure Onboarding
# Stage 1: Build stage
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Declare a build argument for the Firebase service account key file.
# This allows the filename to be passed at build time.
ARG FIREBASE_SA_CRED_FILE

# Copy pom.xml and download dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Copy the Firebase service account key file using the build argument.
# The asterisk is removed since the exact filename is now specified.
COPY src/main/resources/${FIREBASE_SA_CRED_FILE} ./src/main/resources/

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Re-declare the build argument to make it available in this stage.
ARG FIREBASE_SA_CRED_FILE

# Copy JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the Firebase service account key file from the build stage to the runtime image.
COPY --from=build /app/src/main/resources/${FIREBASE_SA_CRED_FILE} ./src/main/resources/

# Set the environment variable FIREBASE_SA_CRED to the file's path.
# The application should be configured to read this environment variable
# to locate the credentials file at runtime.
ENV FIREBASE_SA_CRED=src/main/resources/${FIREBASE_SA_CRED_FILE}
# Original ENV variable for context, removed to avoid redundancy
# ENV FIREBASE_CONFIG_PATH=model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json

# Expose port
EXPOSE 8080

# Run aplikasi
CMD ["java", "-jar", "app.jar"]

##########################################

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
# # ENV DUKCAPIL_SERVICE_URL=http://dukcapil-dummy:8081

# # Expose port
# EXPOSE 8080

# # Run aplikasi dengan nama JAR yang fixed
# CMD ["java", "-jar", "app.jar"]
