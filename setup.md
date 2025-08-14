# Docker Setup Guide - Spring Boot Applications

## Overview
Panduan lengkap untuk setup aplikasi Spring Boot menggunakan Docker:
- **Secure Onboarding** (Main Application) - Port 8080
- **Dukcapil Dummy Service** - Port 8081  
- **PostgreSQL Database** - Port 5432

## Prerequisites
- Docker installed
- Java 21 JDK (untuk development)
- Maven (mvnw included in project)

## Project Structure

### Secure Onboarding (Main App)
```
src/main/
├── java/com/reg/regis/
│   ├── RegistrationAbsoluteApplication.java
│   ├── controller/, config/, dto/, model/, repository/, security/, service/
├── resources/
│   ├── application.properties
│   ├── application.yml
│   └── model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json
├── .env
├── pom.xml
└── Dockerfile
```

### Dukcapil Dummy Service
```
src/main/
├── java/com/dukcapil/service/
│   ├── DukcapilServiceApplication.java
│   ├── controller/, config/, dto/, model/, repository/, service/
├── resources/
│   └── application.properties
├── pom.xml
└── Dockerfile
```

## Setup Instructions

### 1. Setup PostgreSQL Container

```bash
# Pull PostgreSQL image
docker pull postgres:15

# Run PostgreSQL container
docker run --name postgres-db \
  -e POSTGRES_PASSWORD=postgres123 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=customer_registration \
  -p 5432:5432 \
  -d postgres:15

# Verify PostgreSQL is running
docker ps
```

### 2. Create Required Databases

#### Create Both Databases
```bash
# Access PostgreSQL container
docker exec -it postgres-db psql -U postgres

# Create customer_registration database (if not exists)
CREATE DATABASE customer_registration;

# Create dukcapil_ktp database
CREATE DATABASE dukcapil_ktp;

# List all databases to verify
\l

# Exit PostgreSQL
\q
```

**Alternative: One-liner commands**
```bash
# Create databases using single commands
docker exec postgres-db psql -U postgres -c "CREATE DATABASE customer_registration;"
docker exec postgres-db psql -U postgres -c "CREATE DATABASE dukcapil_ktp;"

# Verify databases created
docker exec postgres-db psql -U postgres -c "\l"
```

### 3. Setup Database dengan SQL Files

#### Prepare SQL Files
Pastikan SQL files sudah ada di folder `sql/`:
- `sql/database_fix.sql` - Setup dukcapil database dengan sample data
- `sql/database_setup_FIXED.sql` - Clean customer registration database

#### Execute SQL Files ke Container

**Method 1: Copy dan Execute**
```bash
# Copy SQL files ke container
docker cp sql/database_fix.sql postgres-db:/tmp/
docker cp sql/database_setup_FIXED.sql postgres-db:/tmp/
docker cp sql/clean_db.sql postgres-db:/tmp/

# Execute dukcapil setup (creates tables + sample data)
docker exec postgres-db psql -U postgres -d dukcapil_ktp -f /tmp/database_fix.sql

# Execute customer registration cleanup
docker exec postgres-db psql -U postgres -f /tmp/database_setup_FIXED.sql
```

**Method 2: Direct Execute (One-liner)**
```bash
# Direct execute tanpa copy file
cat sql/database_fix.sql | docker exec -i postgres-db psql -U postgres -d dukcapil_ktp
cat sql/database_setup_FIXED.sql | docker exec -i postgres-db psql -U postgres
```

**Method 3: Mount Volume (untuk development)**
```bash
# Stop existing container
docker stop postgres-db
docker rm postgres-db

# Run dengan mounted SQL folder
docker run --name postgres-db \
  -e POSTGRES_PASSWORD=postgres123 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=customer_registration \
  -v $(pwd)/sql:/sql \
  -p 5432:5432 \
  -d postgres:15

# Create dukcapil database
docker exec postgres-db psql -U postgres -c "CREATE DATABASE dukcapil_ktp;"

# Execute SQL files
docker exec postgres-db psql -U postgres -d dukcapil_ktp -f /sql/database_fix.sql
docker exec postgres-db psql -U postgres -f /sql/database_setup_FIXED.sql
```

#### Verify Database Setup
```bash
# Check dukcapil database
docker exec postgres-db psql -U postgres -d dukcapil_ktp -c "\dt"
docker exec postgres-db psql -U postgres -d dukcapil_ktp -c "SELECT COUNT(*) FROM ktp_dukcapil;"
docker exec postgres-db psql -U postgres -d dukcapil_ktp -c "SELECT nik, nama_lengkap FROM ktp_dukcapil LIMIT 3;"

# Check customer registration database  
docker exec postgres-db psql -U postgres -d customer_registration -c "\dt"
docker exec postgres-db psql -U postgres -d customer_registration -c "SELECT COUNT(*) FROM customers;"

# List all databases
docker exec postgres-db psql -U postgres -c "\l"
```

#### Expected Results
- **dukcapil_ktp**: Table `ktp_dukcapil` with 7 sample records
- **customer_registration**: Clean tables (customers, alamat, wali) ready for use

### 4. Prepare Application Files

#### Secure Onboarding
Pastikan file ini ada di root folder secure onboarding:
- `.env` file dengan config
- `model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json`
- `Dockerfile`

#### Dukcapil Dummy Service  
Pastikan file ini ada di root folder dukcapil dummy:
- `Dockerfile`

### 5. Build Docker Images

```bash
# Build Secure Onboarding
cd /path/to/secure-onboarding
docker build -t secure-onboarding:latest .

# Build Dukcapil Dummy Service
cd /path/to/dukcapil-dummy
docker build -t dukcapil-dummy:latest .

# Verify images created
docker images
```

### 6. Run Applications

```bash
1. Buat network dulu:
docker network create myapp-network

2. Run PostgreSQL database:
docker run -d \
  --name postgres-db \
  --network myapp-network \
  -p 5432:5432 \
  -e POSTGRES_DB=customer_registration \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres123 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15

3. Run Dukcapil service:
docker run -d \
  --name dukcapil-dummy \
  --network myapp-network \
  -p 8081:8081 \
  dukcapil-service

4. Run Secure Onboarding (registration service):
docker run -d \
  --name secure-onboarding \
  --network myapp-network \
  -p 8080:8080 \
  secure-onboarding
```

## Environment Variables

### Secure Onboarding (.env file)
```env
DB_URL=jdbc:postgresql://localhost:5432/customer_registration
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=aB3dF6gH9jK2mN5pQ8rS1tU4vW7xY0zA3bC6dE9fG2hJ5kL8mO1pR4sT7uV0wX3y
JWT_EXPIRATION=86400000
SERVER_PORT=8080
FIREBASE_CONFIG_PATH="model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json"
DUKCAPIL_SERVICE_URL=http://dukcapil-dummy:8081
```

### Docker Environment (Override)
```dockerfile
# Secure Onboarding
ENV DB_URL=jdbc:postgresql://postgres:5432/customer_registration
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres123

# Dukcapil Dummy Service  
ENV DATABASE_URL=jdbc:postgresql://postgres:5432/dukcapil_ktp
ENV DATABASE_USERNAME=postgres
ENV DATABASE_PASSWORD=postgres123
```

## Testing

### Check Application Status
```bash
# Check container logs
docker logs registration-app
docker logs dukcapil-service
docker logs postgres-db

# Test endpoints
curl http://localhost:8080/health  # Registration App
curl http://localhost:8081/health  # Dukcapil Service
```

### Database Connection Test
```bash
# Connect to PostgreSQL
docker exec -it postgres-db psql -U postgres

# Check databases
\l

# Connect to specific database
\c customer_registration
\c dukcapil_ktp

# List tables (after app startup)
\dt
```

## Troubleshooting

### Common Issues

#### Container won't start
```bash
# Check container status
docker ps -a

# Check logs for errors
docker logs <container-name>

# Remove and recreate
docker rm <container-name>
docker run ...
```

#### Database connection failed
```bash
# Verify PostgreSQL is running
docker ps | grep postgres

# Check network connectivity
docker exec registration-app ping postgres

# Verify environment variables
docker exec registration-app env | grep DB
```

#### Port already in use
```bash
# Check what's using the port
lsof -i :8080
lsof -i :8081
lsof -i :5432

# Kill process or use different port
docker run -p 8082:8080 ...
```

### Useful Commands

```bash
# Stop all containers
docker stop postgres-db registration-app dukcapil-service

# Remove all containers
docker rm postgres-db registration-app dukcapil-service

# Remove images
docker rmi registration-app dukcapil-service postgres:15

# Clean up everything
docker system prune -a
```

## Security Notes (DevSecOps)

### Development vs Production
- **Development**: Hard-coded passwords OK
- **Production**: Use Docker secrets or env files
- **Never commit**: `.env` files with real credentials

### Best Practices
```bash
# Production deployment
docker run -d \
  --name secure-onboarding \
  -p 8080:8080 \
  --env-file /secure/path/.env \
  --link postgres-db:postgres \
  secure-onboarding:latest
```

### Database Security
```bash
# Create dedicated user (production)
docker exec -it postgres-db psql -U postgres
CREATE USER app_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE customer_registration TO app_user;
```

## Next Steps
- Setup Docker Compose untuk automation
- Add health checks
- Setup monitoring dan logging
- Implement database migrations
- Add backup strategies