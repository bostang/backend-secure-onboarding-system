# Stage 1: Build aplikasi menggunakan Maven
# Gunakan image Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory di dalam container build stage
WORKDIR /app

# Copy pom.xml dan mvnw (Maven Wrapper) terlebih dahulu untuk memanfaatkan Docker layer caching.
# Karena Dockerfile ini berada di dalam folder backend-secure-onboarding-system,
# jalur COPY sekarang relatif terhadap folder tersebut (yaitu, dari root folder backend).
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Beri izin eksekusi pada mvnw
RUN chmod +x mvnw

# Copy seluruh source code backend
# Ini akan menyalin semua file dari folder src ke /app/src di dalam container.
COPY src ./src

# Salin file .env ke src/main/resources agar masuk ke classpath JAR
# Ini akan memastikan bahwa file .env dapat ditemukan oleh aplikasi saat runtime
# karena akan dikemas di dalam JAR pada lokasi yang diharapkan oleh classpath.
COPY .env ./src/main/resources/

# Build aplikasi Spring Boot
# Perintah ini akan mengkompilasi kode dan membuat file JAR yang dapat dieksekusi.
# -DskipTests digunakan untuk melewati pengujian selama proses build Docker.
RUN ./mvnw clean package -DskipTests

# Stage 2: Jalankan jar dengan OpenJDK
# Gunakan image OpenJDK yang ringan untuk menjalankan aplikasi.
FROM eclipse-temurin:21-jdk-alpine 
    # Menggunakan alpine untuk ukuran image yang lebih kecil

# Set working directory di dalam container runtime stage
WORKDIR /app

# Copy hasil build (file JAR) dari stage 'build' ke direktori /app di stage ini.
# File JAR yang dihasilkan akan memiliki nama seperti 'secure-onboarding-system-0.0.1-SNAPSHOT.jar'
# atau nama lain sesuai konfigurasi di pom.xml Anda.
# Kita menyalinnya sebagai 'app.jar' untuk konsistensi.
COPY --from=build /app/target/*.jar app.jar

# --- Opsional: Tambahkan baris ini untuk debugging (misalnya, untuk netcat) ---
# eclipse-temurin:21-jdk-alpine adalah berbasis Alpine, jadi gunakan apk
# RUN apk update && apk add netcat-traditional && rm -rf /var/cache/apk/*
# Baris di atas akan menginstal netcat untuk debugging jika diperlukan.
# 'rm -rf /var/cache/apk/*' membersihkan cache apk untuk menjaga ukuran image tetap kecil.
# ----------------------------------------------------------------------

# Tentukan perintah yang akan dijalankan saat container dimulai.
# Ini akan menjalankan aplikasi Spring Boot Anda.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]