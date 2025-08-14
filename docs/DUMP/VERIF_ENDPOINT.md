# API Documentation - Verification Service

**Base URL:** `http://localhost:8080/api`

## 📋 Table of Contents

1. [Health Check](#health-check)
2. [NIK Verification](#nik-verification)
3. [Email Verification](#email-verification)
4. [Phone Verification](#phone-verification)
5. [NIK Simple Check](#nik-simple-check)
6. [Get KTP Data](#get-ktp-data)
7. [Statistics](#statistics)
8. [Postman Collection](#postman-collection)
9. [Error Responses](#error-responses)

---

## 🔍 Health Check

### Endpoint
```
GET /verification/health
```

### Description
Check if the verification service is running and available.

### CURL Command
```bash
curl -X GET "http://localhost:8080/api/verification/health" \
  -H "Content-Type: application/json"
```

### Postman Setup
- **Method:** GET
- **URL:** `http://localhost:8080/api/verification/health`
- **Headers:** 
  - `Content-Type: application/json`

### Response Example
```json
{
  "status": "OK",
  "service": "Verification Service (NIK, Email, Phone)",
  "timestamp": 1720689600000,
  "endpoints": {
    "nikVerification": "/verification/nik",
    "emailVerification": "/verification/email",
    "phoneVerification": "/verification/phone",
    "nikCheck": "/verification/nik-check",
    "ktpData": "/verification/ktp-data/{nik}",
    "stats": "/verification/stats"
  }
}
```

---

## 🆔 NIK Verification

### Endpoint
```
POST /verification/nik
```

### Description
Verify NIK (National ID) with full name against Dukcapil database.

### Request Body
```json
{
  "nik": "3175031234567890",
  "namaLengkap": "John Doe"
}
```

### CURL Command
```bash
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe"
  }'
```

### Postman Setup
- **Method:** POST
- **URL:** `http://localhost:8080/api/verification/nik`
- **Headers:** 
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "nik": "3175031234567890",
  "namaLengkap": "John Doe"
}
```

### Success Response (200)
```json
{
  "valid": true,
  "message": "Data NIK dan nama valid sesuai dengan data Dukcapil",
  "ktpData": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "alamat": "Jl. Sudirman No. 123, RT 001/RW 002",
    "kecamatan": "Tanah Abang",
    "kelurahan": "Bendungan Hilir",
    "agama": "Islam",
    "statusPerkawinan": "BELUM KAWIN"
  }
}
```

### Error Response - Name Mismatch (400)
```json
{
  "valid": false,
  "message": "NIK terdaftar namun nama tidak sesuai dengan data Dukcapil"
}
```

### Error Response - NIK Not Found (400)
```json
{
  "valid": false,
  "message": "NIK tidak terdaftar di database Dukcapil"
}
```

### Validation Errors
```json
{
  "valid": false,
  "message": "NIK harus 16 digit"
}
```

---

## 📧 Email Verification

### Endpoint
```
POST /verification/email
```

### Description
Check if email is available for registration (not already registered).

### Request Body
```json
{
  "email": "newuser@example.com"
}
```

### CURL Command
```bash
curl -X POST "http://localhost:8080/api/verification/email" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com"
  }'
```

### Postman Setup
- **Method:** POST
- **URL:** `http://localhost:8080/api/verification/email`
- **Headers:** 
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "email": "newuser@example.com"
}
```

### Success Response - Email Available (200)
```json
{
  "available": true,
  "message": "Email belum terdaftar dan dapat digunakan",
  "data": {}
}
```

### Response - Email Already Registered (200)
```json
{
  "available": false,
  "message": "Email sudah terdaftar di sistem",
  "data": {
    "email": "john.doe@example.com",
    "namaLengkap": "John Doe",
    "emailVerified": true,
    "registeredAt": "2024-07-11T10:30:00"
  }
}
```

### Validation Error Response (400)
```json
{
  "available": false,
  "message": "Format email tidak valid"
}
```

---

## 📱 Phone Verification

### Endpoint
```
POST /verification/phone
```

### Description
Check if phone number is available for registration (not already registered).

### Request Body
```json
{
  "nomorTelepon": "081234567890"
}
```

### CURL Command
```bash
curl -X POST "http://localhost:8080/api/verification/phone" \
  -H "Content-Type: application/json" \
  -d '{
    "nomorTelepon": "081234567890"
  }'
```

### Postman Setup
- **Method:** POST
- **URL:** `http://localhost:8080/api/verification/phone`
- **Headers:** 
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "nomorTelepon": "081234567890"
}
```

### Success Response - Phone Available (200)
```json
{
  "available": true,
  "message": "Nomor telepon belum terdaftar dan dapat digunakan",
  "data": {}
}
```

### Response - Phone Already Registered (200)
```json
{
  "available": false,
  "message": "Nomor telepon sudah terdaftar di sistem",
  "data": {
    "nomorTelepon": "081298765432",
    "namaLengkap": "Jane Smith",
    "email": "jane.smith@example.com",
    "registeredAt": "2024-07-11T09:15:00"
  }
}
```

### Validation Error Response (400)
```json
{
  "available": false,
  "message": "Format nomor telepon tidak valid (contoh: 081234567890)"
}
```

---

## 🔍 NIK Simple Check

### Endpoint
```
POST /verification/nik-check
```

### Description
Simple check if NIK exists in Dukcapil database (without name verification).

### Request Body
```json
{
  "nik": "3175031234567890"
}
```

### CURL Command
```bash
curl -X POST "http://localhost:8080/api/verification/nik-check" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890"
  }'
```

### Postman Setup
- **Method:** POST
- **URL:** `http://localhost:8080/api/verification/nik-check`
- **Headers:** 
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "nik": "3175031234567890"
}
```

### Success Response - NIK Found (200)
```json
{
  "registered": true,
  "message": "NIK terdaftar di database Dukcapil"
}
```

### Response - NIK Not Found (200)
```json
{
  "registered": false,
  "message": "NIK tidak terdaftar di database Dukcapil"
}
```

### Validation Error Response (400)
```json
{
  "registered": false,
  "message": "NIK harus 16 digit"
}
```

---

## 📄 Get KTP Data

### Endpoint
```
GET /verification/ktp-data/{nik}
```

### Description
Get complete KTP data by NIK for testing purposes.

### Path Parameters
- `nik` (string, required): 16-digit National ID number

### CURL Command
```bash
curl -X GET "http://localhost:8080/api/verification/ktp-data/3175031234567890" \
  -H "Content-Type: application/json"
```

### Postman Setup
- **Method:** GET
- **URL:** `http://localhost:8080/api/verification/ktp-data/3175031234567890`
- **Headers:** 
  - `Content-Type: application/json`

### Success Response - Data Found (200)
```json
{
  "found": true,
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "alamat": "Jl. Sudirman No. 123, RT 001/RW 002",
    "kecamatan": "Tanah Abang",
    "kelurahan": "Bendungan Hilir",
    "agama": "Islam",
    "statusPerkawinan": "BELUM KAWIN"
  }
}
```

### Response - Data Not Found (200)
```json
{
  "found": false,
  "message": "Data KTP tidak ditemukan"
}
```

### Validation Error Response (400)
```json
{
  "error": "NIK harus 16 digit"
}
```

---

## 📊 Statistics

### Endpoint
```
GET /verification/stats
```

### Description
Get verification service statistics including customer and KTP data counts.

### CURL Command
```bash
curl -X GET "http://localhost:8080/api/verification/stats" \
  -H "Content-Type: application/json"
```

### Postman Setup
- **Method:** GET
- **URL:** `http://localhost:8080/api/verification/stats`
- **Headers:** 
  - `Content-Type: application/json`

### Success Response (200)
```json
{
  "totalCustomers": 15,
  "verifiedCustomers": 12,
  "verificationRate": 80.0,
  "totalKtpRecords": 17,
  "timestamp": 1720689600000
}
```

---

## 📮 Postman Collection

### Import Instructions
1. Open Postman
2. Click "Import" button
3. Select "Raw text" tab
4. Paste the JSON below
5. Click "Continue" and "Import"

### Postman Collection JSON
```json
{
  "info": {
    "name": "Verification Service API",
    "description": "API collection for NIK, Email, and Phone verification",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/verification/health",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "health"]
        }
      }
    },
    {
      "name": "NIK Verification - Valid",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nik\": \"3175031234567890\",\n  \"namaLengkap\": \"John Doe\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/verification/nik",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "nik"]
        }
      }
    },
    {
      "name": "NIK Verification - Invalid Name",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nik\": \"3175031234567890\",\n  \"namaLengkap\": \"Wrong Name\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/verification/nik",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "nik"]
        }
      }
    },
    {
      "name": "Email Verification - Available",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"newuser@example.com\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/verification/email",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "email"]
        }
      }
    },
    {
      "name": "Phone Verification - Available",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nomorTelepon\": \"081234567890\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/verification/phone",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "phone"]
        }
      }
    },
    {
      "name": "NIK Simple Check",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nik\": \"3175031234567890\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/verification/nik-check",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "nik-check"]
        }
      }
    },
    {
      "name": "Get KTP Data",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/verification/ktp-data/3175031234567890",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "ktp-data", "3175031234567890"]
        }
      }
    },
    {
      "name": "Statistics",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/verification/stats",
          "host": ["{{baseUrl}}"],
          "path": ["verification", "stats"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api",
      "type": "string"
    }
  ]
}
```

### Environment Variables
Create a new environment in Postman with:
- **Variable:** `baseUrl`
- **Value:** `http://localhost:8080/api`

---

## ❌ Error Responses

### Common HTTP Status Codes

| Status Code | Description | Example |
|-------------|-------------|---------|
| 200 | Success | Request processed successfully |
| 400 | Bad Request | Invalid input data or validation errors |
| 404 | Not Found | Endpoint not found |
| 500 | Internal Server Error | Server-side error |

### Standard Error Format
```json
{
  "error": "Error message description",
  "timestamp": 1720689600000,
  "path": "/verification/endpoint"
}
```

### Validation Errors
All endpoints validate input data and return descriptive error messages:

**NIK Validation:**
- Must be exactly 16 digits
- Must contain only numbers
- Required field

**Email Validation:**
- Must be valid email format
- Required field

**Phone Validation:**
- Must start with "08"
- Must be 10-13 digits total
- Required field

---

## 🧪 Test Data

### Available NIK Test Data
Use these NIK numbers for testing:

| NIK | Name | Location | Status |
|-----|------|----------|--------|
| 3175031234567890 | John Doe | Jakarta | Valid |
| 3175032345678901 | Jane Smith | Jakarta | Valid |
| 3175033456789012 | Ahmad Rahman | Bogor | Valid |
| 3175034567890123 | Siti Nurhaliza | Depok | Valid |
| 3175035678901234 | Budi Santoso | Jakarta | Valid |
| 1234567890123456 | Test User One | Jakarta | Valid |
| 1234567890123457 | Test User Two | Bandung | Valid |

### Invalid Test Cases
- NIK: `9999999999999999` (Not found)
- NIK: `12345` (Invalid format - too short)
- NIK: `317503123456789A` (Invalid format - contains letters)
- Email: `invalid-email` (Invalid format)
- Phone: `123456` (Invalid format)

---

## 🚀 Quick Start

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Test health check:**
   ```bash
   curl http://localhost:8080/api/verification/health
   ```

3. **Test NIK verification:**
   ```bash
   curl -X POST http://localhost:8080/api/verification/nik \
     -H "Content-Type: application/json" \
     -d '{"nik":"3175031234567890","namaLengkap":"John Doe"}'
   ```

4. **Import Postman collection** using the JSON provided above

5. **Set environment variable** `baseUrl` to `http://localhost:8080/api`

---

## 📝 Notes

- All endpoints return JSON responses
- Authentication is not required for verification endpoints
- Rate limiting is applied (5 requests per minute by default)
- CORS is enabled for `http://localhost:3000`
- All timestamps are in Unix epoch format (milliseconds)

---

**Version:** 1.0  
**Last Updated:** July 11, 2025  
**Author:** Registration-Absolute API Team