# ===== UPDATED TEST COMMANDS (Fixed URLs) =====

# 1. Health Checks
curl -X GET http://localhost:8080/api/auth/health           # LoginController
curl -X GET http://localhost:8080/api/auth/health   # RegistrationController  
curl -X GET http://localhost:8080/api/verification/health   # VerificationController

# 2. NIK Verification (via Dukcapil Service)
curl -X POST http://localhost:8080/api/verification/nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe"
  }'

# 3. Registration (NEW URL)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "namaIbuKandung": "Jane Doe",
    "nomorTelepon": "081234567890",
    "email": "john.doe@example.com",
    "password": "SecurePass123!",
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

# 4. Login (Unchanged)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }'

# 5. Check Password Strength (NEW URL)
curl -X POST http://localhost:8080/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "SecurePass123!"
  }'

# 6. Validate NIK Format (NEW URL)
curl -X POST http://localhost:8080/api/auth/validate-nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890"
  }'

# 7. Get Current User (Unchanged)
curl -X GET http://localhost:8080/api/auth/me

# 8. Logout (Unchanged)  
curl -X POST http://localhost:8080/api/auth/logout

# 9. Get Statistics (Multiple endpoints)
curl -X GET http://localhost:8080/api/auth/stats     # Registration stats
curl -X GET http://localhost:8080/api/verification/stats     # Verification stats

# 10. Email Verification
curl -X POST http://localhost:8080/api/verification/email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'

# 11. Phone Verification
curl -X POST http://localhost:8080/api/verification/phone \
  -H "Content-Type: application/json" \
  -d '{
    "nomorTelepon": "081234567890"
  }'

echo "=== All endpoints updated to avoid conflicts! ==="