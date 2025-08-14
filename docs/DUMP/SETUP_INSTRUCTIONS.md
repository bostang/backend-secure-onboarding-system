# Setup Instructions - Customer Registration & Dukcapil Services

## Prerequisites
- Java 21
- PostgreSQL 15+
- Maven 3.8+
- IDE (IntelliJ IDEA / VS Code)

## 1. Database Setup

### Install PostgreSQL dan buat databases:
```bash
# Install PostgreSQL (Ubuntu/Debian)
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Login sebagai postgres user
sudo -u postgres psql

# Di PostgreSQL prompt, buat databases:
CREATE DATABASE dukcapil_ktp;
CREATE DATABASE customer_registration;
\q
```

### Setup database schema dan data:
```bash
# Download dan jalankan script database setup
psql -U postgres -f database_setup.sql
```

## 2. Dukcapil Service Setup

### Create project structure:
```
dukcapil-service/
├── src/main/java/com/dukcapil/service/
│   ├── DukcapilServiceApplication.java
│   ├── config/SecurityConfig.java
│   ├── controller/DukcapilController.java
│   ├── dto/
│   ├── model/KtpDukcapil.java
│   ├── repository/KtpDukcapilRepository.java
│   └── service/DukcapilService.java
├── src/main/resources/application.properties
└── pom.xml
```

### Configure application.properties:
```properties
spring.application.name=Dukcapil-KTP-Service
server.port=8081
server.servlet.context-path=/api

spring.datasource.url=jdbc:postgresql://localhost:5432/dukcapil_ktp
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Start Dukcapil Service:
```bash
cd dukcapil-service
mvn clean install
mvn spring-boot:run
```

### Verify Dukcapil Service:
```bash
curl http://localhost:8081/api/dukcapil/health
curl http://localhost:8081/api/dukcapil/stats
```

## 3. Customer Service Setup

### Create project structure:
```
customer-service/
├── src/main/java/com/reg/regis/
│   ├── CustomerServiceApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── DukcapilClientConfig.java
│   │   ├── RateLimitConfig.java
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── VerificationController.java
│   ├── dto/
│   ├── model/
│   │   ├── Customer.java
│   │   ├── Alamat.java
│   │   └── Wali.java
│   ├── repository/CustomerRepository.java
│   ├── security/JwtUtil.java
│   └── service/
│       ├── RegistrationService.java
│       ├── VerificationService.java
│       └── DukcapilClientService.java
├── src/main/resources/application.properties
└── pom.xml
```

### Configure application.properties:
```properties
spring.application.name=Customer-Registration-Service
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:postgresql://localhost:5432/customer_registration
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate

app.jwt.secret=mySecretKey123456789mySecretKey123456789
app.jwt.expiration=86400000

app.dukcapil.base-url=http://localhost:8081/api/dukcapil
app.dukcapil.verify-nik-endpoint=/verify-nik
app.dukcapil.check-nik-endpoint=/check-nik
app.dukcapil.timeout=10000
```

### Start Customer Service:
```bash
cd customer-service
mvn clean install
mvn spring-boot:run
```

### Verify Customer Service:
```bash
curl http://localhost:8080/api/auth/health
curl http://localhost:8080/api/verification/health
```

## 4. Testing Integration

### Test NIK Verification:
```bash
curl -X POST http://localhost:8080/api/verification/nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe"
  }'
```

### Test Customer Registration:
```bash
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
```

### Test Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }'
```

## 5. Service URLs

- **Dukcapil Service**: http://localhost:8081/api/dukcapil
- **Customer Service**: http://localhost:8080/api

### Key Endpoints:

**Dukcapil Service:**
- GET `/dukcapil/health` - Health check
- POST `/dukcapil/verify-nik` - Verify NIK and name
- POST `/dukcapil/check-nik` - Check NIK existence
- GET `/dukcapil/stats` - Service statistics

**Customer Service:**
- POST `/auth/register` - Customer registration
- POST `/auth/login` - Customer login
- GET `/auth/health` - Health check
- POST `/verification/nik` - NIK verification
- POST `/verification/email` - Email verification
- POST `/verification/phone` - Phone verification

## 6. Available Test Data (NIK from Dukcapil Database)

```
3175031234567890 - John Doe
3175032345678901 - Jane Smith
3175033456789012 - Ahmad Rahman
3175034567890123 - Siti Nurhaliza
3175035678901234 - Budi Santoso
1234567890123456 - Test User One
1234567890123457 - Test User Two
```

**IMPORTANT**: Nama harus PERSIS sama dengan yang ada di database KTP Dukcapil!

## 7. Troubleshooting

### Common Issues:

1. **Connection Refused ke Dukcapil Service**
   - Pastikan Dukcapil Service berjalan di port 8081
   - Check dengan: `curl http://localhost:8081/api/dukcapil/health`

2. **Database Connection Error**
   - Pastikan PostgreSQL berjalan
   - Verify credentials di application.properties
   - Check database exists: `psql -U postgres -l`

3. **NIK Not Found**
   - Pastikan NIK ada di database dukcapil_ktp
   - Check dengan: `psql -U postgres -d dukcapil_ktp -c "SELECT nik, nama_lengkap FROM ktp_dukcapil LIMIT 10;"`

4. **Name Mismatch**
   - Nama harus PERSIS sama dengan di database KTP
   - Case sensitive dan spasi matters

5. **Port Already in Use**
   - Kill process: `sudo lsof -t -i:8080 | xargs kill -9`
   - Or change port di application.properties

## 8. Development Tips

- Use `mvn spring-boot:run` untuk development
- Enable debug logs: `logging.level.com.reg.regis=DEBUG`
- Use Postman/Insomnia untuk testing API
- Monitor logs untuk debugging service communication
- Test dengan different NIK combinations dari database

## 9. Production Considerations

- Change JWT secret di production
- Enable HTTPS (secure=true untuk cookies)
- Set proper CORS origins
- Enable rate limiting
- Add proper logging dan monitoring
- Use environment-specific configurations
- Implement proper error handling
- Add API documentation (