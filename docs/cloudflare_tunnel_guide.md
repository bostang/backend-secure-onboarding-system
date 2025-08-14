# Setup Cloudflare Tunnel untuk Forward API

## 1. Install Cloudflared

### macOS

```bash
brew install cloudflared
```

### Windows

```bash
# Download dari: https://github.com/cloudflare/cloudflared/releases
# Extract dan add ke PATH
```

### Linux

```bash
# Debian/Ubuntu
curl -L --output cloudflared.deb https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb
sudo dpkg -i cloudflared.deb

# CentOS/RHEL
curl -L --output cloudflared.rpm https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-x86_64.rpm
sudo rpm -i cloudflared.rpm
```

## 2. Quick Start (Tanpa Akun - Temporary)

### Start Spring Boot

```bash
# Terminal 1: Start aplikasi kamu
./mvnw spring-boot:run

# Pastikan jalan di http://localhost:8080
```

### Start Cloudflare Tunnel

```bash
# Terminal 2: Start tunnel (temporary)
cloudflared tunnel --url http://localhost:8080
```

**Output akan seperti ini:**

```log
2024-01-XX T10:00:00Z INF Thank you for trying Cloudflare Tunnel. Doing so, without a Cloudflare account, is a quick way to experiment and try it out. However, be aware that these account-less tunnels have no uptime guarantee. If you intend to use tunnels in production you should use a pre-created named tunnel by following: https://developers.cloudflare.com/cloudflare-one/connections/connect-apps
2024-01-XX T10:00:00Z INF Requesting new quick Tunnel on trycloudflare.com...
2024-01-XX T10:00:00Z INF +--------------------------------------------------------------------------------------------+
2024-01-XX T10:00:00Z INF |  Your quick Tunnel has been created! Visit it at (it may take some time to be reachable):  |
2024-01-XX T10:00:00Z INF |  https://random-name-123.trycloudflare.com                                                |
2024-01-XX T10:00:00Z INF +--------------------------------------------------------------------------------------------+
```

**Public URL kamu:** `https://random-name-123.trycloudflare.com`

## 3. Update CORS di Spring Boot

### Edit application.yml

```yaml
app:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:3001,https://random-name-123.trycloudflare.com
```

### Restart Spring Boot

```bash
# Ctrl+C di terminal Spring Boot
./mvnw spring-boot:run
```

## 4. Test Public URL

### Health Check

```bash
curl https://random-name-123.trycloudflare.com/api/auth/health
```

### Test Registration

```bash
curl -X POST https://random-name-123.trycloudflare.com/api/auth/register \
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
```

## 5. Share ke Tim Frontend

### Base URL untuk React

```javascript
// .env di project React tim FE
REACT_APP_API_URL=https://random-name-123.trycloudflare.com/api
```

### Axios Setup

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'https://random-name-123.trycloudflare.com/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

export default api;
```

---

## 6. Setup dengan Akun Cloudflare (Optional - untuk persistent URL)

### Login Cloudflare

```bash
cloudflared tunnel login
```

### Create Named Tunnel

```bash
# Create tunnel
cloudflared tunnel create customer-registration-api

# Create config file
nano ~/.cloudflared/config.yml
```

### Config File Content

```yaml
tunnel: customer-registration-api
credentials-file: /Users/yourname/.cloudflared/your-tunnel-id.json

ingress:
  - hostname: customer-api.your-domain.com
    service: http://localhost:8080
  - service: http_status:404
```

### Start Named Tunnel

```bash
cloudflared tunnel run customer-registration-api
```

---

## 7. Quick Commands untuk Development

### Start Everything

```bash
# Terminal 1: Spring Boot
./mvnw spring-boot:run

# Terminal 2: Cloudflare Tunnel
cloudflared tunnel --url http://localhost:8080

# Copy URL dari output terminal 2
# Share ke tim FE
```

### Stop Everything

```bash
# Ctrl+C di kedua terminal
```

---

## 8. Advantages Cloudflare Tunnel

✅ **Gratis dan reliable**  
✅ **HTTPS otomatis**  
✅ **No rate limiting untuk development**  
✅ **Global CDN (fast)**  
✅ **Built-in DDoS protection**  
✅ **Temporary URLs tanpa akun**  
✅ **Named tunnels dengan akun**  

---

## 9. Troubleshooting

### Tunnel tidak connect

```bash
# Cek versi cloudflared
cloudflared version

# Update ke versi terbaru
brew upgrade cloudflared  # macOS
```

### CORS error

- Pastikan URL cloudflare ada di `allowed-origins`
- Restart Spring Boot setelah update CORS

### Port sudah digunakan

```bash
# Cek port 8080
lsof -i :8080

# Kill process kalau perlu
kill -9 PID
```

---

## Summary untuk Tim FE

**Base URL:** `https://random-name-123.trycloudflare.com/api`

**Endpoints:**

- `POST /auth/register` - Registrasi customer
- `POST /auth/login` - Login
- `GET /auth/me` - Get current user
- `POST /auth/logout` - Logout
- `GET /auth/health` - Health check

**Important:**

- Pakai `withCredentials: true` di axios
- NIK harus dari database KTP yang valid
- Auth menggunakan HTTP-only cookies

Ready untuk development! 🚀
