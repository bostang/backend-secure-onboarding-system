# 1. Health Check
curl -X GET http://localhost:8080/api/auth/health

# 2. REGISTRASI BERHASIL - dengan NIK yang ADA di database KTP
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

# 3. REGISTRASI BERHASIL - dengan NIK lain yang ADA di database KTP
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Ahmad Rahman",
    "nik": "3175033456789012",
    "namaIbuKandung": "Siti Rahman",
    "nomorTelepon": "081234567891",
    "email": "ahmad@example.com",
    "password": "password123",
    "tipeAkun": "Individual",
    "tempatLahir": "Bogor",
    "tanggalLahir": "1985-12-10",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Manager",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "10-15 juta",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 1002,
    "alamat": {
      "namaAlamat": "Jl. Thamrin No. 789",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Menteng",
      "kelurahan": "Gondangdia",
      "kodePos": "10110"
    },
    "wali": {
      "jenisWali": "Istri",
      "namaLengkapWali": "Fatimah Rahman",
      "pekerjaanWali": "Guru",
      "alamatWali": "Jl. Thamrin No. 789",
      "nomorTeleponWali": "081298765433"
    }
  }'

# 4. REGISTRASI GAGAL - NIK tidak ada di database KTP
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Invalid User",
    "nik": "9999999999999999",
    "namaIbuKandung": "Invalid Mother",
    "nomorTelepon": "081234567999",
    "email": "invalid@example.com",
    "password": "password123",
    "tipeAkun": "Individual",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Test",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5-10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1001,
    "alamat": {
      "namaAlamat": "Jl. Test",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta",
      "kecamatan": "Test",
      "kelurahan": "Test",
      "kodePos": "12345"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Wali",
      "pekerjaanWali": "Test",
      "alamatWali": "Jl. Test",
      "nomorTeleponWali": "081298765999"
    }
  }'

# 5. REGISTRASI GAGAL - nama tidak sesuai KTP
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Nama Salah",
    "nik": "3175033456789012",
    "namaIbuKandung": "Test Mother",
    "nomorTelepon": "081234567998",
    "email": "wrong@example.com",
    "password": "password123",
    "tipeAkun": "Individual",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Test",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5-10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1001,
    "alamat": {
      "namaAlamat": "Jl. Test",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta",
      "kecamatan": "Test",
      "kelurahan": "Test",
      "kodePos": "12345"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Wali",
      "pekerjaanWali": "Test",
      "alamatWali": "Jl. Test",
      "nomorTeleponWali": "081298765998"
    }
  }'

# 6. REGISTRASI GAGAL - email duplikat (gunakan email yang sudah ada)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Jane Smith",
    "nik": "3175032345678901",
    "namaIbuKandung": "Mary Smith",
    "nomorTelepon": "081234567892",
    "email": "john@example.com",
    "password": "password123",
    "tipeAkun": "Individual",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-08-22",
    "jenisKelamin": "Perempuan",
    "agama": "Kristen",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Designer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "8-12 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1003,
    "alamat": {
      "namaAlamat": "Jl. Gatot Subroto No. 456",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Setiabudi",
      "kelurahan": "Kuningan Timur",
      "kodePos": "12950"
    },
    "wali": {
      "jenisWali": "Suami",
      "namaLengkapWali": "Robert Smith",
      "pekerjaanWali": "Engineer",
      "alamatWali": "Jl. Gatot Subroto No. 456",
      "nomorTeleponWali": "081298765434"
    }
  }'

# 7. REGISTRASI GAGAL - nomor telepon duplikat
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Jane Smith",
    "nik": "3175032345678901",
    "namaIbuKandung": "Mary Smith",
    "nomorTelepon": "081234567890",
    "email": "jane@example.com",
    "password": "password123",
    "tipeAkun": "Individual",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-08-22",
    "jenisKelamin": "Perempuan",
    "agama": "Kristen",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Designer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "8-12 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1003,
    "alamat": {
      "namaAlamat": "Jl. Gatot Subroto No. 456",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Setiabudi",
      "kelurahan": "Kuningan Timur",
      "kodePos": "12950"
    },
    "wali": {
      "jenisWali": "Suami",
      "namaLengkapWali": "Robert Smith",
      "pekerjaanWali": "Engineer",
      "alamatWali": "Jl. Gatot Subroto No. 456",
      "nomorTeleponWali": "081298765434"
    }
  }'

# 8. Login setelah registrasi berhasil
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# 9. Get stats
curl -X GET http://localhost:8080/api/auth/stats

# 10. Check password strength
curl -X POST http://localhost:8080/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "password123"
  }'

echo ""
echo "=== NIK YANG BISA DIPAKAI (dari database KTP) ==="
echo "3175031234567890 - John Doe"
echo "3175032345678901 - Jane Smith"  
echo "3175033456789012 - Ahmad Rahman"
echo "3175034567890123 - Siti Nurhaliza"
echo "3175035678901234 - Budi Santoso"
echo "3174011234567890 - Maria Gonzales"
echo "3174012345678901 - Rudi Hermawan"
echo "3175071234567890 - Dewi Sartika"
echo "3175072345678901 - Andi Wijaya"
echo "3174051234567890 - Linda Kusuma"
echo ""
echo "NAMA HARUS PERSIS SAMA DENGAN YANG ADA DI DATABASE KTP!"