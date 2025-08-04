# 🚀 cURL & Postman Guide untuk Customer Registration Service

## 📋 **Base URL:** `http://localhost:8080/api`

---

## 🔐 CUSTOMER REGISTRATION SERVICE - QUICK TESTS (Real Data)

### 📝 **cURL Commands**

#### 1. Health Checks

```bash
echo "📊 Health Checks:"
curl -X GET "http://localhost:8080/api/auth/health" | jq
curl -X GET "http://localhost:8080/api/verification/health" | jq
```

#### 2. NIK Verification Tests (Real Dukcapil Data)

```bash
echo "🔍 Testing John Doe (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tanggalLahir": "1990-05-15"
  }' | jq

echo "🔍 Testing Jane Smith (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175032345678901",
    "namaLengkap": "Jane Smith",
    "tanggalLahir": "1995-08-22"
  }' | jq

echo "🔍 Testing Ahmad Rahman (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175033456789012",
    "namaLengkap": "Ahmad Rahman",
    "tanggalLahir": "1985-12-10"
  }' | jq

echo "🔍 Testing Test User One (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "1234567890123456",
    "namaLengkap": "Test User One",
    "tanggalLahir": "1995-01-01"
  }' | jq

echo "❌ Testing Invalid Name:"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "Wrong Name",
    "tanggalLahir": "1990-05-15"
  }' | jq
```

#### 3. NIK Check (Simple)

```bash
echo "🔍 NIK Check Tests:"
curl -X POST "http://localhost:8080/api/verification/nik-check" \
  -H "Content-Type: application/json" \
  -d '{"nik": "3175031234567890"}' | jq

curl -X POST "http://localhost:8080/api/verification/nik-check" \
  -H "Content-Type: application/json" \
  -d '{"nik": "1234567890123456"}' | jq
```

#### 4. Email & Phone Verification

```bash
echo "📧 Email Verification Tests:"
curl -X POST "http://localhost:8080/api/verification/email" \
  -H "Content-Type: application/json" \
  -d '{"email": "newuser@example.com"}' | jq

curl -X POST "http://localhost:8080/api/verification/email" \
  -H "Content-Type: application/json" \
  -d '{"email": "existing@example.com"}' | jq

echo "📱 Phone Verification Tests:"
curl -X POST "http://localhost:8080/api/verification/phone" \
  -H "Content-Type: application/json" \
  -d '{"nomorTelepon": "081999888777"}' | jq

curl -X POST "http://localhost:8080/api/verification/phone" \
  -H "Content-Type: application/json" \
  -d '{"nomorTelepon": "081234567890"}' | jq
```

#### 5. Validation Tests

```bash
echo "✅ NIK Format Validation:"
curl -X POST "http://localhost:8080/api/auth/validate-nik" \
curl -X POST "http://localhost:8080/api/auth/validate-nik" \
  -H "Content-Type: application/json" \
  -d '{"nik": "3175031234567890"}' | jq

echo "🔒 Password Strength Check:"
curl -X POST "http://localhost:8080/api/auth/check-password" \
curl -X POST "http://localhost:8080/api/auth/check-password" \
  -H "Content-Type: application/json" \
  -d '{"password": "JohnDoe123!"}' | jq
```

#### 6. Customer Registration (Main Test) - **UPDATED dengan JenisKartu**

```bash
echo "👤 Registration Test - John Doe (Silver):"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "namaIbuKandung": "Mary Doe",
    "nomorTelepon": "081234567890",
    "email": "john.doe@example.com",
    "password": "JohnDoe123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Gold",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5-10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123, RT 001/RW 002",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Tanah Abang",
      "kelurahan": "Bendungan Hilir",
      "kodePos": "10210"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Robert Doe",
      "pekerjaanWali": "Pensiunan",
      "alamatWali": "Jl. Sudirman No. 123, RT 001/RW 002",
      "nomorTeleponWali": "081298765432"
    }
  }' | jq

echo "👤 Registration Test - Jane Smith (Gold):"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "Jane Smith",
    "nik": "3175032345678901",
    "namaIbuKandung": "Anna Smith",
    "nomorTelepon": "081234567891",
    "email": "jane.smith@example.com",
    "password": "JaneSmith123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Gold",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-08-22",
    "jenisKelamin": "Perempuan",
    "agama": "Kristen",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Marketing Manager",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "10-15 juta",
    "tujuanPembuatanRekening": "Investasi",
    "alamat": {
      "namaAlamat": "Jl. Gatot Subroto No. 456, RT 003/RW 004",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Setiabudi",
      "kelurahan": "Kuningan Timur",
      "kodePos": "12950"
    },
    "wali": {
      "jenisWali": "Suami",
      "namaLengkapWali": "Michael Smith",
      "pekerjaanWali": "Konsultan",
      "alamatWali": "Jl. Gatot Subroto No. 456, RT 003/RW 004",
      "nomorTeleponWali": "081298765433"
    }
  }' | jq

echo "👤 Registration Test - Foreign Customer (Platinum):"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "Robert Wilson",
    "nik": "X12345678901234",
    "namaIbuKandung": "Susan Wilson",
    "nomorTelepon": "081234567892",
    "email": "robert.wilson@example.com",
    "password": "RobertWilson123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Platinum",
    "tempatLahir": "New York",
    "tanggalLahir": "1988-03-12",
    "jenisKelamin": "Laki-laki",
    "agama": "Kristen",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Consultant",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "15-20 juta",
    "tujuanPembuatanRekening": "Bisnis",
    "kodeRekening": 1003,
    "alamat": {
      "namaAlamat": "Jl. Thamrin No. 789, RT 005/RW 006",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Menteng",
      "kelurahan": "Menteng",
      "kodePos": "10310"
    },
    "wali": {
      "jenisWali": "Sponsor",
      "namaLengkapWali": "PT. Global Solutions",
      "pekerjaanWali": "Perusahaan",
      "alamatWali": "Jl. Thamrin No. 789, RT 005/RW 006",
      "nomorTeleponWali": "021-12345678"
    }
  }' | jq

echo "👤 Registration Test - Batik Air Customer:"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "Akiko Tanaka",
    "nik": "K31750312345678",
    "namaIbuKandung": "Yuki Tanaka",
    "nomorTelepon": "081234567893",
    "email": "akiko.tanaka@example.com",
    "password": "AkikoTanaka123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Batik Air",
    "tempatLahir": "Tokyo",
    "tanggalLahir": "1992-07-20",
    "jenisKelamin": "Perempuan",
    "agama": "Buddha",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Teacher",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "8-12 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1004,
    "alamat": {
      "namaAlamat": "Jl. Kemang Raya No. 101, RT 007/RW 008",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kemang",
      "kelurahan": "Kemang Timur",
      "kodePos": "12560"
    },
    "wali": {
      "jenisWali": "Suami",
      "namaLengkapWali": "Budi Santoso",
      "pekerjaanWali": "Engineer",
      "alamatWali": "Jl. Kemang Raya No. 101, RT 007/RW 008",
      "nomorTeleponWali": "081298765434"
    }
  }' | jq
```

#### 7. Profile Access (Auto-login test)

```bash
echo "👤 John Profile (using auto-login cookie):"
curl -X GET "http://localhost:8080/api/auth/profile" \
  -b cookies.txt | jq

echo "👤 Jane Profile (using auto-login cookie):"
curl -X GET "http://localhost:8080/api/auth/profile" \
echo "👤 Profile Access Tests:"
curl -X GET "http://localhost:8080/api/auth/profile" \
  -b cookies.txt | jq
```

#### 8. Login Tests

```bash
echo "🔐 Login Tests:"
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "john.doe@example.com",
    "password": "JohnDoe123!"
  }' | jq

curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "robert.wilson@example.com", 
    "password": "RobertWilson123!"
  }' | jq

curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "akiko.tanaka@example.com", 
    "password": "AkikoTanaka123!"
  }' | jq
```

#### 9. Auth Tests

```bash
echo "👤 Get Current User (Auth/Me):"
curl -X GET "http://localhost:8080/api/auth/me" \
  -b cookies.txt | jq

echo "✅ Check Auth Status:"
curl -X GET "http://localhost:8080/api/auth/check-auth" \
  -b cookies.txt | jq

echo "🔄 Refresh Token:"
curl -X POST "http://localhost:8080/api/auth/refresh-token" \
  -b cookies.txt \
  -c refresh_cookies.txt | jq

echo "🚪 Logout:"
curl -X POST "http://localhost:8080/api/auth/logout" \
  -b cookies.txt | jq
```

#### 10. Statistics

```bash
echo "📊 Statistics:"
curl -X GET "http://localhost:8080/api/auth/stats" | jq
curl -X GET "http://localhost:8080/api/auth/stats" | jq
curl -X GET "http://localhost:8080/api/verification/stats" | jq
```

---

## 📱 **Postman Collection**

### 🔧 **Environment Variables**

```json
{
  "name": "Customer Registration Environment",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api",
      "enabled": true
    },
    {
      "key": "auth_token",
      "value": "",
      "enabled": true
    }
  ]
}
```

### 📋 **Collection Structure**

#### **1. Health Checks**

- **GET** `{{base_url}}/auth/health`
- **GET** `{{base_url}}/auth/health`
- **GET** `{{base_url}}/auth/health`
- **GET** `{{base_url}}/verification/health`

#### **2. Registration - **UPDATED dengan JenisKartu**

##### **Register Customer (KTP)**

- **Method:** POST
- **URL:** `{{base_url}}/auth/register`
- **URL:** `{{base_url}}/auth/register`
- **Headers:**

  ```conf
  Content-Type: application/json
  ```

- **Body (raw JSON):**

  ```json
  {
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "namaIbuKandung": "Mary Doe",
    "nomorTelepon": "081234567890",
    "email": "john.doe@example.com",
    "password": "JohnDoe123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Silver",
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
      "namaAlamat": "Jl. Sudirman No. 123, RT 001/RW 002",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Tanah Abang",
      "kelurahan": "Bendungan Hilir",
      "kodePos": "10210"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Robert Doe",
      "pekerjaanWali": "Pensiunan",
      "alamatWali": "Jl. Sudirman No. 123, RT 001/RW 002",
      "nomorTeleponWali": "081298765432"
    }
  }
  ```

##### **Register Foreign Customer (Passport)**

- **Method:** POST
- **URL:** `{{base_url}}/auth/register`
- **Body (raw JSON):**

  ```json
  {
    "namaLengkap": "Robert Wilson",
    "nik": "X12345678901234",
    "namaIbuKandung": "Susan Wilson",
    "nomorTelepon": "081234567892",
    "email": "robert.wilson@example.com",
    "password": "RobertWilson123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Platinum",
    "tempatLahir": "New York",
    "tanggalLahir": "1988-03-12",
    "jenisKelamin": "Laki-laki",
    "agama": "Kristen",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Consultant",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "15-20 juta",
    "tujuanPembuatanRekening": "Bisnis",
    "kodeRekening": 1003,
    "alamat": {
      "namaAlamat": "Jl. Thamrin No. 789, RT 005/RW 006",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Menteng",
      "kelurahan": "Menteng",
      "kodePos": "10310"
    },
    "wali": {
      "jenisWali": "Sponsor",
      "namaLengkapWali": "PT. Global Solutions",
      "pekerjaanWali": "Perusahaan",
      "alamatWali": "Jl. Thamrin No. 789, RT 005/RW 006",
      "nomorTeleponWali": "021-12345678"
    }
  }
  ```

##### **Register KITAS Holder**

- **Method:** POST
- **URL:** `{{base_url}}/auth/register`
- **Body (raw JSON):**

  ```json
  {
    "namaLengkap": "Akiko Tanaka",
    "nik": "K31750312345678",
    "namaIbuKandung": "Yuki Tanaka",
    "nomorTelepon": "081234567893",
    "email": "akiko.tanaka@example.com",
    "password": "AkikoTanaka123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Batik Air",
    "tempatLahir": "Tokyo",
    "tanggalLahir": "1992-07-20",
    "jenisKelamin": "Perempuan",
    "agama": "Buddha",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Teacher",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "8-12 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1004,
    "alamat": {
      "namaAlamat": "Jl. Kemang Raya No. 101, RT 007/RW 008",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kemang",
      "kelurahan": "Kemang Timur",
      "kodePos": "12560"
    },
    "wali": {
      "jenisWali": "Suami",
      "namaLengkapWali": "Budi Santoso",
      "pekerjaanWali": "Engineer",
      "alamatWali": "Jl. Kemang Raya No. 101, RT 007/RW 008",
      "nomorTeleponWali": "081298765434"
    }
  }
  ```

### 🔧 **Postman Pre-request Scripts**

#### **For Registration (Generate Random Data dengan JenisKartu)**

```javascript
// Generate random email untuk testing
const timestamp = Date.now();
const randomEmail = `test.user.${timestamp}@example.com`;
pm.environment.set("test_email", randomEmail);

// Generate random phone number
const randomPhone = `0812${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`;
pm.environment.set("test_phone", randomPhone);

// Generate random account code
const randomAccountCode = Math.floor(Math.random() * 9000) + 1000;
pm.environment.set("account_code", randomAccountCode);

// Set random jenis kartu
const jenisKartuOptions = ["Silver", "Gold", "Platinum", "Batik Air"];
const randomJenisKartu = jenisKartuOptions[Math.floor(Math.random() * jenisKartuOptions.length)];
pm.environment.set("jenis_kartu", randomJenisKartu);

console.log("Generated email:", randomEmail);
console.log("Generated phone:", randomPhone);
console.log("Generated account code:", randomAccountCode);
console.log("Generated jenis kartu:", randomJenisKartu);
```

### 🔧 **Postman Tests Scripts**

#### **For Registration Endpoint (dengan JenisKartu)**

```javascript
// Test response status
pm.test("Registration successful", function () {
    pm.response.to.have.status(200);
});

// Test response structure
pm.test("Response has success field", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('success');
    pm.expect(responseJson.success).to.be.true;
});

// Test customer data
pm.test("Response contains customer data", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('customer');
    pm.expect(responseJson.customer).to.have.property('email');
    pm.expect(responseJson.customer).to.have.property('namaLengkap');
    pm.expect(responseJson.customer).to.have.property('jenisKartu');
});

// Test jenis kartu validation
pm.test("Jenis kartu is valid", function () {
    const responseJson = pm.response.json();
    const validJenisKartu = ["Silver", "Gold", "Platinum", "Batik Air"];
    pm.expect(validJenisKartu).to.include(responseJson.customer.jenisKartu);
});

// Check auto-login cookie
pm.test("Auth cookie is set", function () {
    const authCookie = pm.cookies.get("authToken");
    pm.expect(authCookie).to.not.be.undefined;
    pm.environment.set("auth_token", authCookie);
    console.log("Auth token saved:", authCookie);
});
```

---

## 🧪 **Error Test Scenarios dengan JenisKartu**

### ❌ **Validation Error Tests**

```bash
#!/bin/bash
echo "❌ VALIDATION ERROR TESTS untuk JenisKartu"

## 1. Invalid JenisKartu
echo "Testing Invalid JenisKartu:"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User",
    "nik": "3175031234567890",
    "email": "test.invalid@example.com",
    "password": "Test123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "INVALID_CARD",
    "tanggalLahir": "1990-01-01"
  }' | jq

## 2. Missing JenisKartu
echo "Testing Missing JenisKartu:"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User",
    "nik": "3175031234567890",
    "email": "test.missing@example.com",
    "password": "Test123!",
    "tipeAkun": "BNI Taplus",
    "tanggalLahir": "1990-01-01"
  }' | jq

## 3. Empty JenisKartu
echo "Testing Empty JenisKartu:"
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User",
    "nik": "3175031234567890",
    "email": "test.empty@example.com",
    "password": "Test123!",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "",
    "tanggalLahir": "1990-01-01"
  }' | jq

echo "❌ Validation error tests completed!"
```

### ✅ **Complete Happy Path dengan Semua JenisKartu**

```bash
#!/bin/bash
echo "🚀 COMPLETE
```