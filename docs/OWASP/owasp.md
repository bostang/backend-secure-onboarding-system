
# ✅ OWASP Top 10 Mitigations in Spring Boot Project

Dokumentasi ini mencatat penerapan mitigasi OWASP Top 10 di dalam aplikasi Spring Boot yang telah dikembangkan.

---

## 🛡️ A01: Broken Access Control

**Mitigasi:**  
- Telah diterapkan validasi berbasis endpoint.
- Contoh:
  - Endpoint `/auth/**` hanya bisa diakses oleh user
  - JWT access in login with token

---

## 🔐 A02: Cryptographic Failures

**Mitigasi:**  
- Password disimpan menggunakan hash BCrypt (`BCryptPasswordEncoder`).
- HMAC-SHA256 digunakan untuk menandatangani payload menuju layanan Dukcapil.
- Tidak ada penyimpanan data sensitif dalam bentuk plaintext.

---

## 🔍 A03: Injection

**Mitigasi:**  
- Seluruh query menggunakan JPA (`@Query`, `CrudRepository`, `JpaRepository`) untuk menghindari SQL injection.
- Tidak ada penggunaan native SQL.
- Input dari user telah divalidasi menggunakan anotasi `@Valid`, `@NotNull`, dll.

---

## 🧱 A04: Insecure Design

**Mitigasi:**  
- Validasi masukan secara ketat di level DTO dan Entity.
- Implementasi pengecekan manual untuk input abnormal (verifikasi nama & NIK ke Dukcapil).
- Tidak ada kebergantungan pada validasi di client side saja.
- Menghindari *mass assignment* dengan kontrol eksplisit pada field `Customer`.

---

## ⚙️ A05: Security Misconfiguration

**Mitigasi:**  
- Header HTTP dikonfigurasi secara eksplisit:
  - `Strict-Transport-Security`
  - `X-Content-Type-Options`
  - `X-Frame-Options`
- Rate Limiting diterapkan menggunakan filter `RateLimiting.java`.
- Tidak ada profil `dev` terbuka di environment production.
- Tidak expose actuator endpoint sensitif.

---

## 🧪 A06: Vulnerable and Outdated Components

**Mitigasi:**  
- Menggunakan plugin `owasp-dependency-check` di `pom.xml`.
- Sudah dijalankan pengecekan:
  ```
  mvn verify
  ```
- Semua library aman dan tidak ada CVE kritikal yang terdeteksi.

---

## 🧰 A07: Identification and Authentication Failures

**Mitigasi:**  
- Menggunakan JWT untuk otentikasi.
- Token JWT hanya valid dalam durasi tertentu dan memiliki signature HMAC-SHA256.
- Header `Authorization: Bearer <token>` wajib dikirim pada setiap request.
- Tidak ada informasi sensitif dalam payload token.

---

## 🔎 A08: Software and Data Integrity Failures

**Mitigasi:**  
- Tidak ada dynamic code execution.
- Tidak ada object deserialization tanpa kontrol.
- CI/CD pipeline aman tanpa `script injection` atau `eval`.

---

## 🔗 A09: Security Logging and Monitoring Failures

**Mitigasi:**  
- Log semua request sensitif dan error pada sisi server.
- Log kesalahan pada login, verifikasi Dukcapil, dan validasi input.
- Gunakan `SLF4J + Logback` untuk log error dan audit.
- Tidak mencetak `stacktrace` ke user.

---

## 🧭 A10: Server-Side Request Forgery (SSRF)

**Mitigasi:**  
- WebClient hanya mengakses URL internal yang telah dipastikan aman.
- Tidak menerima URL dari input pengguna untuk digunakan dalam request.
- Tidak ada fitur file upload/download atau image parsing dari link.

---

## ✅ Status Implementasi

| OWASP ID | Nama Isu                                | Status   |
|----------|------------------------------------------|----------|
| A01      | Broken Access Control                    | ✅ Done   |
| A02      | Cryptographic Failures                   | ✅ Done   |
| A03      | Injection                                | ✅ Done   |
| A04      | Insecure Design                          | ✅ Done   |
| A05      | Security Misconfiguration                | ✅ Done   |
| A06      | Vulnerable and Outdated Components       | ✅ Done   |
| A07      | Identification and Authentication Failures | ✅ Done |
| A08      | Software and Data Integrity Failures     | ✅ Done   |
| A09      | Security Logging and Monitoring Failures | ✅ Done   |
| A10      | SSRF (Server-Side Request Forgery)       | ✅ Done   |

---

📁 **Disusun oleh:**  
HEUSC – DevSecOps Squad 🚀  
