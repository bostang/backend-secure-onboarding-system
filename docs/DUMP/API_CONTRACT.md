# 📄 API Contract - Secure Onboarding System

**Base URL:** `http://localhost:8081/api/auth`

---

## 🩺 1. Health Check

**GET** `/health`

* **Description:** Mengecek status server.
* **Response:** `200 OK`

**contoh Pengujian**:

```bash
curl -X GET http://localhost:8081/api/auth/health
```

---

## 📝 2. Register Customer

**POST** `/register`

* **Description:** Registrasi akun baru nasabah.
* **Request Body:**

```json
{
  "namaLengkap": "string",
  "namaIbuKandung": "string",
  "nomorTelepon": "string",
  "email": "string",
  "password": "string",
  "tipeAkun": "string",
  "tempatLahir": "string",
  "tanggalLahir": "YYYY-MM-DD",
  "jenisKelamin": "Laki-laki | Perempuan",
  "agama": "string",
  "statusPernikahan": "string",
  "pekerjaan": "string",
  "sumberPenghasilan": "string",
  "rentangGaji": "string",
  "tujuanPembuatanRekening": "string",
  "kodeRekening": integer,
  "alamat": {
    "namaAlamat": "string",
    "provinsi": "string",
    "kota": "string",
    "kecamatan": "string",
    "kelurahan": "string",
    "kodePos": "string"
  },
  "wali": {
    "jenisWali": "Ayah | Ibu",
    "namaLengkapWali": "string",
    "pekerjaanWali": "string",
    "alamatWali": "string",
    "nomorTeleponWali": "string"
  }
}
```

* **Response:** `201 Created` | `400 Bad Request` | `409 Conflict`

**contoh Pengujian**:

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Bostang Pejompongan",
    "namaIbuKandung": "Siti Aminah",
    "nomorTelepon": "089651524900",
    "email": "bostang.p@example.com",
    "password": "Password123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-07-10",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan Swasta",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": ">Rp5 - 10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1023,
    "alamat": {
      "namaAlamat": "Jl. Melati No. 10",
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
      "alamatWali": "Jl. Melati No. 10",
      "nomorTeleponWali": "081298765432"
    }
  }' \
  -c cookies.txt
```

---

## 🔐 3. Login

**POST** `/login`

* **Description:** Login user dengan email dan password.
* **Request Body:**

```json
{
  "email": "string",
  "password": "string"
}
```

* **Response:** `200 OK` + Set-Cookie (Session) | `401 Unauthorized`

**contoh Pengujian**:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com",
    "password": "Password123!"
  }' \
  -c cookies.txt
```

---

## 👤 4. Get Current User

**GET** `/me`

* **Description:** Mendapatkan profil user yang sedang login.
* **Headers:** Cookie dari login response.
* **Response:** `200 OK` | `401 Unauthorized`

**contoh Pengujian**:

```bash
curl -X GET http://localhost:8081/api/auth/me \
  -b cookies.txt
```

---

## ✅ 5. Check Authentication Status

**GET** `/check-auth`

* **Description:** Memeriksa apakah user telah terautentikasi.
* **Headers:** Cookie dari login response.
* **Response:** `200 OK` | `401 Unauthorized`

**contoh Pengujian**:

```bash
curl -X GET http://localhost:8081/api/auth/check-auth \
  -b cookies.txt
```

---

## 🔑 6. Check Password Strength

**POST** `/check-password`

* **Description:** Mengevaluasi kekuatan password.
* **Request Body:**

```json
{
  "password": "string"
}
```

* **Response:** `200 OK`
* **Response Body (Contoh):**

```json
{
  "strength": "STRONG" // atau WEAK
}
```

**contoh Pengujian**:

```bash
curl -X POST http://localhost:8081/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "Password123!"
  }'
```

---

## 📧 7. Verify Email

**POST** `/verify-email`

* **Description:** Mengirim ulang verifikasi email.
* **Request Body:**

```json
{
  "email": "string"
}
```

* **Response:** `200 OK` | `400 Bad Request`

**contoh Pengujian**:

```bash
curl -X POST http://localhost:8081/api/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com"
  }'
```

---

## 📊 8. Registration Statistics

**GET** `/stats`

* **Description:** Mendapatkan statistik jumlah pendaftar, dsb.
* **Response:** `200 OK`
* **Response Body (Contoh):**

```json
{
  "totalRegistrations": 123,
  "uniqueEmails": 110
}
```

**contoh Pengujian**:

```bash
curl -X GET http://localhost:8081/api/auth/stats
```

---

## 🔁 9. Refresh Token

**POST** `/refresh-token`

* **Description:** Memperbarui session login melalui refresh token (via cookie).
* **Headers:** Cookie dari login response.
* **Response:** `200 OK` + Cookie Baru | `401 Unauthorized`

**contoh Pengujian**:

```bash
curl -X POST http://localhost:8081/api/auth/refresh-token \
  -b cookies.txt \
  -c cookies.txt
```

---

## 🚪 10. Logout

**POST** `/logout`

* **Description:** Logout dari sesi login aktif.
* **Headers:** Cookie dari login response.
* **Response:** `200 OK`

**contoh Pengujian**:

```bash
curl -X POST http://localhost:8081/api/auth/logout \
  -b cookies.txt
```

---

## 🗂️ Summary

| Method | Endpoint          | Description                  | Auth Required |
| ------ | ----------------- | ---------------------------- | ------------- |
| GET    | `/health`         | Health check                 | ❌             |
| POST   | `/register`       | Registrasi user              | ❌             |
| POST   | `/login`          | Login user                   | ❌             |
| GET    | `/me`             | Get current user info        | ✅             |
| GET    | `/check-auth`     | Check login status           | ✅             |
| POST   | `/check-password` | Cek kekuatan password        | ❌             |
| POST   | `/verify-email`   | Kirim ulang verifikasi email | ❌             |
| GET    | `/stats`          | Statistik pendaftaran        | ❌             |
| POST   | `/refresh-token`  | Perbarui session login       | ✅             |
| POST   | `/logout`         | Logout                       | ✅             |

---
