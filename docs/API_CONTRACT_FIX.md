# 📄 API Contract - Customer Registration Service

## Service Information

- **Service Name:** Customer Registration Service dengan Dukcapil Integration
- **Version:** 1.0.0
- **Base URL:** `http://localhost:8080/api`
- **Protocol:** HTTP/HTTPS
- **Content-Type:** `application/json`
- **Authentication:** Cookie-based (HTTP-only cookies)

## 🎯 Service Architecture

3 Controller dengan endpoint yang berbeda:

1. **`/api/auth`** - Authentication Management : Register & Login  
2. **`/api/verification`** - NIK & Data Verification

---

## 📋 API Endpoints

### 1. REGISTRATION MANAGEMENT (`/api/auth`) 📝

#### 🏥 **GET** `/api/auth/health`

Health check untuk registration service

- **Response (200)**

```json
{
  "status": "OK",
  "service": "Customer Registration Service dengan Dukcapil Integration",
  "timestamp": 1721120400000,
  "dukcapilService": {
    "url": "http://localhost:8081",
    "available": true
  },
  "statistics": {
    "totalCustomers": 150,
    "verifiedCustomers": 135,
    "verificationRate": "90%"
  },
  "endpoints": {
    "register": "POST /auth/register",
    "checkPassword": "POST /auth/check-password",
    "validateNik": "POST /auth/validate-nik",
    "verifyEmail": "POST /auth/verify-email",
    "stats": "GET /auth/stats",
    "health": "GET /auth/health",
    "profile": "GET /auth/profile"
  },
  "jenisKartu": {
    "available": ["Silver", "Gold", "Platinum", "Batik Air"],
    "description": "Available card types for customer registration"
    "register": "POST /auth/register",
    "checkPassword": "POST /auth/check-password",
    "validateNik": "POST /auth/validate-nik",
    "verifyEmail": "POST /auth/verify-email",
    "stats": "GET /auth/stats",
    "health": "GET /auth/health",
    "profile": "GET /auth/profile"
  }
}
```

---

#### 📝 **POST** `/api/auth/register` ⭐ **MAIN ENDPOINT**

Registrasi customer baru dengan validasi Dukcapil + Auto Login

- **Request**

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
  "alamat": {
    "namaAlamat": "Jl. Sudirman No. 123",
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
    "alamatWali": "Jl. Sudirman No. 123",
    "nomorTeleponWali": "081298765432"
  }
}
```

##### JenisKartu Field Details

- **Field Name:** `jenisKartu`
- **Type:** String
- **Required:** Yes
- **Valid Values:**
  - `"Silver"` - Basic card level
  - `"Gold"` - Premium card level  
  - `"Platinum"` - Premium plus card level
  - `"Batik Air"` - Airline partnership card
- **Validation:** Must be one of the valid values above
- **Default:** None (field is required)

- **Success Response (200)** + Set-Cookie - **UPDATED**

```json
{
  "success": true,
  "message": "Registrasi berhasil! Data Anda telah terverifikasi dengan KTP Dukcapil.",
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Silver",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
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
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

##### Validation Error (400) - **NEW JenisKartu Errors**

```json
{
  "success": false,
  "error": "Jenis kartu tidak valid. Pilihan yang tersedia: Silver, Gold, Platinum, Batik Air",
  "type": "validation_error"
}
```

**Set-Cookie Header:**

```conf
Set-Cookie: authToken=<jwt-token>; HttpOnly; Path=/; Max-Age=86400; Domain=localhost
```

---

#### 🔑 **POST** `/api/auth/check-password`

Check password strength

- **Request**

```json
{
  "password": "Password123!"
}
```

- **Response (200)**

```json
{
  "strength": "STRONG"
}
```

---

#### ✅ **POST** `/api/auth/validate-nik`

Validasi format NIK

- **Request**

```json
{
  "nik": "3175031234567890"
}
```

- **Response (200)**

```json
{
  "valid": true,
  "exists": false,
  "message": "NIK valid"
}
```

---

#### 📧 **POST** `/api/auth/verify-email`

Verify customer email

- **Request**

```json
{
  "email": "john.doe@example.com"
}
```

- **Response (200)**

```json
{
  "message": "Email berhasil diverifikasi"
}
```

---

#### 📊 **GET** `/api/auth/stats`

Registration statistics - **UPDATED dengan JenisKartu breakdown**

- **Response (200)**

```json
{
  "totalCustomers": 150,
  "verifiedCustomers": 135,
  "verificationRate": 90.0,
  "dukcapilServiceUrl": "http://localhost:8081",
  "dukcapilServiceAvailable": true,
  "jenisKartuBreakdown": {
    "Silver": 45,
    "Gold": 38,
    "Platinum": 32,
    "Batik Air": 35
  },
  "popularCardType": "Silver"
}
```

---

#### 👤 **GET** `/api/auth/profile`

Get customer profile (requires authentication) - **UPDATED**

- **Headers**

```conf
Cookie: authToken=<jwt-token>
```

- **Success Response (200)**

```json
{
  "profile": {
    "id": 1,
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Silver",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
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
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

---

### 2. AUTHENTICATION (`/api/auth`) 🔐

#### 🔐 **POST** `/api/auth/login` ⭐ **MAIN ENDPOINT**

Customer login dengan email dan password

- **Request**

```json
{
  "email": "john.doe@example.com",
  "password": "JohnDoe123!"
}
```

- **Success Response (200)** + Set-Cookie - **UPDATED**

```json
{
  "message": "Login berhasil",
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Silver",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
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
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

##### Failed Response (400)

```json
{
  "error": "Email atau password tidak valid"
}
```

---

#### 👤 **GET** `/api/auth/me`

Get current authenticated user - **UPDATED**

- **Headers**

```conf
Cookie: authToken=<jwt-token>
```

- **Success Response (200)**

```json
{
  "authenticated": true,
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "jenisKartu": "Silver",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
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
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

---

#### 🚪 **POST** `/api/auth/logout`

Logout user (clear cookie)

- **Response (200)** + Clear-Cookie

```json
{
  "message": "Logout berhasil"
}
```

---

#### 🔄 **POST** `/api/auth/refresh-token`

Refresh JWT token

- **Headers**

```conf
Cookie: authToken=<jwt-token>
```

- **Success Response (200)** + Set-Cookie

```json
{
  "message": "Token berhasil diperbarui"
}
```

---

#### ✅ **GET** `/api/auth/check-auth`

Check authentication status

- **Headers**

```conf
Cookie: authToken=<jwt-token>
```

##### Authenticated Response (200)

```json
{
  "authenticated": true,
  "email": "john.doe@example.com",
  "jenisKartu": "Silver"
}
```

##### Not Authenticated Response (200)

```json
{
  "authenticated": false
}
```

---

### 3. VERIFICATION SERVICE (`/api/verification`) 🔍

#### 🏥 **GET** `/api/verification/health`

Health check verification service

- **Response (200)**

```json
{
  "status": "OK",
  "service": "Verification Service (Enhanced with tanggalLahir)",
  "timestamp": 1721120400000,
  "endpoints": {
    "nikVerification": "POST /verification/nik (requires: nik, namaLengkap, tanggalLahir)",
    "emailVerification": "POST /verification/email",
    "phoneVerification": "POST /verification/phone",
    "nikCheck": "POST /verification/nik-check",
    "stats": "GET /verification/stats"
  }
}
```

---

#### 🔍 **POST** `/api/verification/nik` ⭐ **MAIN ENDPOINT**

Verifikasi NIK dengan nama lengkap dan tanggal lahir via Dukcapil Service

- **Request**

```json
{
  "nik": "3175031234567890",
  "namaLengkap": "John Doe",
  "tanggalLahir": "1990-05-15"
}
```

- **Success Response (200)**

```json
{
  "valid": true,
  "message": "NIK dan nama cocok",
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "LAKI_LAKI",
    "alamat": "Jl. Sudirman No. 123, RT 001/RW 002",
    "kecamatan": "Tanah Abang",
    "kelurahan": "Bendungan Hilir",
    "agama": "ISLAM",
    "statusPerkawinan": "BELUM KAWIN"
  }
}
```

##### Failed Response (200)

```json
{
  "valid": false,
  "message": "Data tidak cocok dengan database Dukcapil",
  "data": {}
}
```

---

#### 📧 **POST** `/api/verification/email`

Verifikasi ketersediaan email

- **Request**

```json
{
  "email": "user@example.com"
}
```

- **Available Response (200)**

```json
{
  "available": true,
  "message": "Email tersedia",
  "data": {}
}
```

---

#### 📱 **POST** `/api/verification/phone`

Verifikasi ketersediaan nomor telepon

- **Request**

```json
{
  "nomorTelepon": "081234567890"
}
```

- **Available Response (200)**

```json
{
  "available": true,
  "message": "Nomor telepon tersedia",
  "data": {}
}
```

---

#### 🆔 **POST** `/api/verification/nik-check`

Check NIK tanpa nama (simple check)

- **Request**

```json
{
  "nik": "3175031234567890"
}
```

##### Registered Response (200)

```json
{
  "registered": true,
  "message": "NIK terdaftar di database Dukcapil"
}
```

---

#### 📊 **GET** `/api/verification/stats`

Verification statistics

- **Response (200)**

```json
{
  "totalNikChecks": 105,
  "validNik": 78,
  "invalidNik": 27,
  "emailVerifications": 120,
  "availableEmails": 115,
  "takenEmails": 5,
  "phoneVerifications": 110,
  "availablePhones": 105,
  "takenPhones": 5
}
```

---

### 🗂 Endpoint Summary - **UPDATED**

| Method                      | Endpoint                      | Description                         | Auth Required | JenisKartu    |
| --------------------------- | ----------------------------- | ----------------------------------- | ------------- | ------------- |
| **Registration Management** |                               |                                     |               |               |
| GET                         | `/api/auth/health`            | Health check                        | ❌            | N/A           |
| POST                        | `/api/auth/register`          | Customer registration + auto login  | ❌            | **Required**  |
| POST                        | `/api/auth/check-password`    | Password strength check             | ❌            | N/A           |
| POST                        | `/api/auth/validate-nik`      | NIK format validation               | ❌            | N/A           |
| POST                        | `/api/auth/verify-email`      | Email verification                  | ❌            | N/A           |
| GET                         | `/api/auth/stats`             | Registration statistics             | ❌            | **Breakdown** |
| GET                         | `/api/auth/profile`           | Customer profile                    | ✅            | **Included**  |
| **Authentication**          |                               |                                     |               |               |
| POST                        | `/api/auth/login`             | Customer login                      | ❌            | **Returned**  |
| GET                         | `/api/auth/me`                | Current user info                   | ✅            | **Included**  |
| POST                        | `/api/auth/logout`            | Logout user                         | ✅            | N/A           |
| POST                        | `/api/auth/refresh-token`     | Refresh JWT token                   | ✅            | N/A           |
| GET                         | `/api/auth/check-auth`        | Check auth status                   | ✅            | **Included**  |
| **Verification Service**    |                               |                                     |               |               |
| GET                         | `/api/verification/health`    | Health check                        | ❌            | N/A           |
| POST                        | `/api/verification/nik`       | NIK + name + birthdate verification | ❌            | N/A           |
| POST                        | `/api/verification/email`     | Email availability check            | ❌            | N/A           |
| POST                        | `/api/verification/phone`     | Phone availability check            | ❌            | N/A           |
| POST                        | `/api/verification/nik-check` | NIK existence check                 | ❌            | N/A           |
| GET                         | `/api/verification/stats`     | Verification statistics             | ❌            | N/A           |

---

### 🔧 Configuration & Security

#### Cookie Configuration

- **Name:** `authToken`
- **HttpOnly:** `true` (XSS protection)
- **Secure:** `false` (development) / `true` (production)
- **Path:** `/`
- **Domain:** `localhost` (development)
- **Max-Age:** `86400` seconds (24 hours)

#### JenisKartu Configuration - **NEW**

- **Valid Values:** `["Silver", "Gold", "Platinum", "Batik Air"]`
- **Required:** Yes (for registration)
- **Case Sensitive:** Yes
- **Default Value:** None (must be explicitly provided)
- **Database Column:** `VARCHAR(20) NOT NULL`

#### External Dependencies

- **Dukcapil Service:** `http://localhost:8081` (NIK verification)
- **Database:** PostgreSQL for customer data
- **JWT:** Token-based authentication

---

### 🎯 JenisKartu Business Rules

#### Card Type Descriptions

- **Silver:** Entry-level card with basic features
- **Gold:** Premium card with enhanced benefits
- **Platinum:** Top-tier card with exclusive privileges
- **Batik Air:** Partnership card with airline-specific benefits

#### Card Type Features (Business Logic)

```json
{
  "Silver": {
    "withdrawalLimit": 5000000,
    "transferLimit": 10000000,
    "annualFee": 0,
    "benefits": ["ATM access", "Mobile banking", "Basic insurance"]
  },
  "Gold": {
    "withdrawalLimit": 10000000,
    "transferLimit": 25000000,
    "annualFee": 100000,
    "benefits": ["ATM access", "Mobile banking", "Travel insurance", "Cashback 1%"]
  },
  "Platinum": {
    "withdrawalLimit": 25000000,
    "transferLimit": 100000000,
    "annualFee": 500000,
    "benefits": ["ATM access", "Mobile banking", "Comprehensive insurance", "Cashback 2%", "Airport lounge access"]
  },
  "Batik Air": {
    "withdrawalLimit": 15000000,
    "transferLimit": 50000000,
    "annualFee": 250000,
    "benefits": ["ATM access", "Mobile banking", "Flight discounts", "Miles earning", "Priority check-in"]
  }
}
```

---

### 📝 Sample Registration Requests by Card Type

#### Silver Card Registration

```json
{
  "namaLengkap": "Ahmad Santosa",
  "nik": "3175031234567890",
  "namaIbuKandung": "Siti Santosa",
  "nomorTelepon": "081234567890",
  "email": "ahmad.santosa@example.com",
  "password": "Ahmad123!",
  "tipeAkun": "BNI Taplus",
  "jenisKartu": "Silver",
  "tempatLahir": "Jakarta",
  "tanggalLahir": "1990-05-15",
  "jenisKelamin": "Laki-laki",
  "agama": "Islam",
  "statusPernikahan": "Belum Kawin",
  "pekerjaan": "Karyawan",
  "sumberPenghasilan": "Gaji",
  "rentangGaji": "3-5 juta",
  "tujuanPembuatanRekening": "Tabungan",
  "kodeRekening": 1001
}
```

#### Gold Card Registration

```json
{
  "namaLengkap": "Maria Putri",
  "nik": "3175032345678901",
  "namaIbuKandung": "Elena Putri",
  "nomorTelepon": "081234567891",
  "email": "maria.putri@example.com",
  "password": "Maria123!",
  "tipeAkun": "BNI Taplus Gold",
  "jenisKartu": "Gold",
  "tempatLahir": "Surabaya",
  "tanggalLahir": "1985-08-22",
  "jenisKelamin": "Perempuan",
  "agama": "Kristen",
  "statusPernikahan": "Kawin",
  "pekerjaan": "Manager",
  "sumberPenghasilan": "Gaji",
  "rentangGaji": "10-15 juta",
  "tujuanPembuatanRekening": "Investasi",
  "kodeRekening": 1002
}
```

#### Platinum Card Registration

```json
{
  "namaLengkap": "David Kusuma",
  "nik": "3175033456789012",
  "namaIbuKandung": "Linda Kusuma",
  "nomorTelepon": "081234567892",
  "email": "david.kusuma@example.com",
  "password": "David123!",
  "tipeAkun": "BNI Taplus Platinum",
  "jenisKartu": "Platinum",
  "tempatLahir": "Bandung",
  "tanggalLahir": "1980-12-10",
  "jenisKelamin": "Laki-laki",
  "agama": "Kristen",
  "statusPernikahan": "Kawin",
  "pekerjaan": "Direktur",
  "sumberPenghasilan": "Gaji",
  "rentangGaji": "25+ juta",
  "tujuanPembuatanRekening": "Bisnis",
  "kodeRekening": 1003
}
```

#### Batik Air Card Registration

```json
{
  "namaLengkap": "Sarah Airlines",
  "nik": "3175034567890123",
  "namaIbuKandung": "Rina Airlines",
  "nomorTelepon": "081234567893",
  "email": "sarah.airlines@example.com",
  "password": "Sarah123!",
  "tipeAkun": "BNI Batik Air",
  "jenisKartu": "Batik Air",
  "tempatLahir": "Medan",
  "tanggalLahir": "1992-03-18",
  "jenisKelamin": "Perempuan",
  "agama": "Islam",
  "statusPernikahan": "Belum Kawin",
  "pekerjaan": "Pilot",
  "sumberPenghasilan": "Gaji",
  "rentangGaji": "15-20 juta",
  "tujuanPembuatanRekening": "Travel",
  "kodeRekening": 1004
}
```

---

### ❌ Error Codes & Messages

#### JenisKartu Validation Errors

```json
{
  "INVALID_CARD_TYPE": {
    "code": "REG_001",
    "message": "Jenis kartu tidak valid. Pilihan yang tersedia: Silver, Gold, Platinum, Batik Air",
    "httpStatus": 400
  },
  "MISSING_CARD_TYPE": {
    "code": "REG_002", 
    "message": "Jenis kartu wajib diisi",
    "httpStatus": 400
  },
  "EMPTY_CARD_TYPE": {
    "code": "REG_003",
    "message": "Jenis kartu tidak boleh kosong",
    "httpStatus": 400
  }
}
```

#### General Validation Errors

```json
{
  "EMAIL_ALREADY_EXISTS": {
    "code": "REG_004",
    "message": "Email sudah terdaftar dalam sistem",
    "httpStatus": 400
  },
  "PHONE_ALREADY_EXISTS": {
    "code": "REG_005",
    "message": "Nomor telepon sudah terdaftar dalam sistem", 
    "httpStatus": 400
  },
  "DUKCAPIL_VERIFICATION_FAILED": {
    "code": "REG_006",
    "message": "Data tidak cocok dengan database Dukcapil",
    "httpStatus": 400
  }
}
```

---

### 🔍 Database Schema Updates

#### Customer Table - **UPDATED**

```sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    nik VARCHAR(16) UNIQUE NOT NULL,
    nama_ibu_kandung VARCHAR(100) NOT NULL,
    nomor_telepon VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    tipe_akun VARCHAR(50) NOT NULL,
    jenis_kartu VARCHAR(20) NOT NULL CHECK (jenis_kartu IN ('Silver', 'Gold', 'Platinum', 'Batik Air')),
    tempat_lahir VARCHAR(50),
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(10) CHECK (jenis_kelamin IN ('Laki-laki', 'Perempuan')),
    agama VARCHAR(20),
    status_pernikahan VARCHAR(20),
    pekerjaan VARCHAR(50),
    sumber_penghasilan VARCHAR(50),
    rentang_gaji VARCHAR(20),
    tujuan_pembuatan_rekening VARCHAR(50),
    kode_rekening INTEGER,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Address Table (Embedded)

```sql
ALTER TABLE customers ADD COLUMN alamat_nama_alamat VARCHAR(255);
ALTER TABLE customers ADD COLUMN alamat_provinsi VARCHAR(50);
ALTER TABLE customers ADD COLUMN alamat_kota VARCHAR(50);
ALTER TABLE customers ADD COLUMN alamat_kecamatan VARCHAR(50);
ALTER TABLE customers ADD COLUMN alamat_kelurahan VARCHAR(50);
ALTER TABLE customers ADD COLUMN alamat_kode_pos VARCHAR(10);
```

#### Guardian Table (Embedded)

```sql
ALTER TABLE customers ADD COLUMN wali_jenis_wali VARCHAR(20);
ALTER TABLE customers ADD COLUMN wali_nama_lengkap_wali VARCHAR(100);
ALTER TABLE customers ADD COLUMN wali_pekerjaan_wali VARCHAR(50);
ALTER TABLE customers ADD COLUMN wali_alamat_wali VARCHAR(255);
ALTER TABLE customers ADD COLUMN wali_nomor_telepon_wali VARCHAR(15);
```

#### Indexes for Performance

```sql
CREATE INDEX idx_customers_jenis_kartu ON customers(jenis_kartu);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_nik ON customers(nik);
CREATE INDEX idx_customers_created_at ON customers(created_at);
```

---

### 📊 Analytics & Reporting Endpoints

#### **GET** `/api/auth/analytics/card-distribution`

Get distribution of customers by card type

- **Response (200)**

```json
{
  "cardDistribution": {
    "Silver": {
      "count": 45,
      "percentage": 30.0
    },
    "Gold": {
      "count": 38,
      "percentage": 25.3
    },
    "Platinum": {
      "count": 32,
      "percentage": 21.3
    },
    "Batik Air": {
      "count": 35,
      "percentage": 23.3
    }
  },
  "totalCustomers": 150,
  "mostPopular": "Silver",
  "leastPopular": "Platinum"
}
```

#### **GET** `/api/auth/analytics/monthly-registrations`

Get monthly registration statistics by card type

##### Query Parameters

- `year` (optional): Year to filter (default: current year)
- `cardType` (optional): Filter by specific card type

- **Response (200)**

```json
{
  "year": 2024,
  "monthlyData": [
    {
      "month": "January",
      "Silver": 5,
      "Gold": 3,
      "Platinum": 2,
      "Batik Air": 4,
      "total": 14
    },
    {
      "month": "February", 
      "Silver": 7,
      "Gold": 5,
      "Platinum": 3,
      "Batik Air": 6,
      "total": 21
    }
  ],
  "yearTotal": 150
}
```

---

### 🛡️ Security Considerations

#### JenisKartu Security

- Card type cannot be changed after registration (requires admin intervention)
- Card type determines transaction limits and access levels
- Audit trail maintained for all card type assignments
- Rate limiting applied to registration endpoints

#### Validation Rules

- JenisKartu must be validated server-side
- Client-side validation is supplementary only
- Database constraints enforce valid card types
- Case-sensitive matching required

---

### 🚀 Future Enhancements

#### Planned JenisKartu Features

1. **Card Upgrade System** - Allow customers to upgrade their card type
2. **Dynamic Limits** - Adjust limits based on customer behavior
3. **Seasonal Cards** - Temporary card types for special promotions
4. **Corporate Cards** - Business-specific card types
5. **Student Cards** - Discounted card types for students

#### API Versioning

- Current version: `v1`
- JenisKartu field introduced in: `v1.0.0`
- Backward compatibility maintained for 6 months
- Migration guide available for existing integrations
