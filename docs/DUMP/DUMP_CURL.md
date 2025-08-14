# Quick Login Tests - Copy & Paste Ready

# 1. Health Check
curl -X GET "http://localhost:8080/api/auth/health" \
  -H "Content-Type: application/json"

# 2. Registration (Create Test User)
# 1. Test Register Customer (POST /auth/register)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "namaIbuKandung": "Jane Doe",
    "nomorTelepon": "081234567890",
    "email": "john@example.com",
    "password": "password123",
    "tipeAkun": "Individual",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5-10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1001,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kebayoran Baru",
      "kelurahan": "Melawai",
      "kodePos": "12160"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Budi Hartono",
      "pekerjaanWali": "Pensiunan",
      "alamatWali": "Jl. Sudirman No. 123",
      "nomorTeleponWali": "081298765432"
    }
  }'

# 2. Test Validate NIK (POST /auth/validate-nik)
curl -X POST http://localhost:8080/api/verification/nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap" : "John Doe",
    "tanggalLahir" : "1990-05-15"
  }'

# 3. Test Password Strength (POST /auth/check-password)
curl -X POST http://localhost:8080/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "SecurePass123!"
  }'

# 4. Test Health Check (GET /auth/health)
curl -X GET http://localhost:8080/auth/health

# 5. Test Registration Stats (GET /auth/stats)
curl -X GET http://localhost:8080/auth/stats

# 6. Test Get Profile (GET /auth/profile) - Need cookie from register
curl -X GET http://localhost:8080/auth/profile \
  -H "Cookie: authToken=YOUR_TOKEN_HERE"

# 7. Test Verify Email (POST /auth/verify-email)
curl -X POST http://localhost:8080/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com"
  }'

# 8. Test Register dengan NIK Invalid (Should fail)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User",
    "nik": "123",
    "namaIbuKandung": "Test Mother",
    "nomorTelepon": "081234567890",
    "email": "test@example.com",
    "password": "password123",
    "tipeAkun": "TABUNGAN",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "L",
    "agama": "Islam",
    "statusPernikahan": "Belum Menikah",
    "pekerjaan": "Developer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5000000-10000000",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 123456,
    "alamat": {
      "namaAlamat": "Test Address",
      "provinsi": "Test Province",
      "kota": "Test City",
      "kecamatan": "Test District",
      "kelurahan": "Test Village",
      "kodePos": "12345"
    },
    "wali": {
      "jenisWali": "Orang Tua",
      "namaLengkapWali": "Test Wali",
      "pekerjaanWali": "Test Job",
      "alamatWali": "Test Wali Address",
      "nomorTeleponWali": "081987654321"
    }
  }'

# 9. Test Register dengan Email Duplicate (Should fail)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Another User",
    "nik": "3201012345678902",
    "namaIbuKandung": "Another Mother",
    "nomorTelepon": "081234567891",
    "email": "john.doe@example.com",
    "password": "password123",
    "tipeAkun": "TABUNGAN",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "L",
    "agama": "Islam",
    "statusPernikahan": "Belum Menikah",
    "pekerjaan": "Developer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5000000-10000000",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 123456,
    "alamat": {
      "namaAlamat": "Test Address 2",
      "provinsi": "Test Province 2",
      "kota": "Test City 2",
      "kecamatan": "Test District 2",
      "kelurahan": "Test Village 2",
      "kodePos": "12346"
    },
    "wali": {
      "jenisWali": "Orang Tua",
      "namaLengkapWali": "Test Wali 2",
      "pekerjaanWali": "Test Job 2",
      "alamatWali": "Test Wali Address 2",
      "nomorTeleponWali": "081987654322"
    }
  }'

# 10. Test Register dengan NIK Duplicate (Should fail)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Third User",
    "nik": "3201012345678901",
    "namaIbuKandung": "Third Mother",
    "nomorTelepon": "081234567892",
    "email": "third@example.com",
    "password": "password123",
    "tipeAkun": "TABUNGAN",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "L",
    "agama": "Islam",
    "statusPernikahan": "Belum Menikah",
    "pekerjaan": "Developer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5000000-10000000",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 123456,
    "alamat": {
      "namaAlamat": "Test Address 3",
      "provinsi": "Test Province 3",
      "kota": "Test City 3",
      "kecamatan": "Test District 3",
      "kelurahan": "Test Village 3",
      "kodePos": "12347"
    },
    "wali": {
      "jenisWali": "Orang Tua",
      "namaLengkapWali": "Test Wali 3",
      "pekerjaanWali": "Test Job 3",
      "alamatWali": "Test Wali Address 3",
      "nomorTeleponWali": "081987654323"
    }
  }'

# 3. Login (Valid Credentials)
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "testuser@example.com",
    "password": "TestPassword123!"
  }'

# 4. Login (Invalid Email)
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "wrong@example.com",
    "password": "TestPassword123!"
  }'

# 5. Login (Invalid Password)
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "WrongPassword"
  }'

# 6. Get Current User (with cookie)
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 7. Get Current User (without cookie - should fail)
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json"

# 8. Check Auth Status (with cookie)
curl -X GET "http://localhost:8080/api/auth/check-auth" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 9. Get Customer Profile (with cookie)
curl -X GET "http://localhost:8080/api/auth/profile" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 10. Password Strength Check
curl -X POST "http://localhost:8080/api/auth/check-password" \
  -H "Content-Type: application/json" \
  -d '{
    "password": "StrongPassword123!"
  }'

# 11. Logout
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 12. Try Access After Logout (should fail)
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json" \
  -b cookies.txt