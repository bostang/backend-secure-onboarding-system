#!/bin/bash

# --- 1. Membuat file .env ---
echo "Membuat file .env..."
cat << EOF > .env
DB_URL=jdbc:postgresql://localhost:5432/customer_registration
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=90385881f4876e643cdf5fa2b28c1494469133ddf0e6aee2784eeab3f4f342e82b51ea0c
JWT_EXPIRATION=86400000
SERVER_PORT=8080
EOF
echo ".env berhasil dibuat."

# --- 2. Menjalankan skrip SQL ---
# Pastikan PostgreSQL client (psql) sudah terinstall dan berjalan.
# Ganti 'your_db_user' jika username database kamu bukan 'postgres'
# Ganti 'your_db_password' jika password database kamu bukan 'password'
# Penting: Pastikan database 'customer_registration' sudah ada atau buat secara manual
# sebelum menjalankan skrip ini, atau tambahkan langkah pembuatan database di sini.

echo "Menjalankan skrip SQL: database_setup_FIXED.sql"
# Menggunakan PGPASSWORD untuk menghindari prompt password
PGPASSWORD=password psql -h localhost -U postgres -f ./sql/database_setup_FIXED.sql

if [ $? -eq 0 ]; then
    echo "database_setup_FIXED.sql berhasil dijalankan."
else
    echo "Gagal menjalankan database_setup_FIXED.sql. Pastikan PostgreSQL berjalan dan kredensial benar."
    exit 1
fi

echo "Menjalankan skrip SQL: clean_db.sql"
PGPASSWORD=password psql -h localhost -U postgres -f ./sql/clean_db.sql

if [ $? -eq 0 ]; then
    echo "clean_db.sql berhasil dijalankan."
else
    echo "Gagal menjalankan clean_db.sql. Pastikan PostgreSQL berjalan dan kredensial benar."
    exit 1
fi

# --- 3. Menjalankan aplikasi Spring Boot ---
echo "Menjalankan aplikasi Spring Boot dengan ./mvnw spring-boot:run"
# Penting: Pastikan kamu berada di direktori root proyekmu saat menjalankan skrip ini
./mvnw spring-boot:run

# Tambahan: Untuk menjalankan Spring Boot di background, kamu bisa gunakan `nohup` dan `&`:
# nohup ./mvnw spring-boot:run > app.log 2>&1 &
# echo "Aplikasi Spring Boot sedang berjalan di background. Lihat app.log untuk output."
# Jika kamu menjalankan di background, kamu perlu cara lain untuk mematikan aplikasi,
# misalnya dengan mencari PID dan membunuhnya: kill $(lsof -t -i:8080)