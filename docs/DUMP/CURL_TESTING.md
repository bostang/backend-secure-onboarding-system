# Customer Registration API Test Script

> Bash script untuk menguji berbagai endpoint dalam sistem Secure Onboarding.  
> Jalankan di terminal menggunakan: `bash test.sh`

📝 Important Notes:

Cookie Handling: Commands menggunakan -c cookies.txt untuk save cookies dan -b cookies.txt untuk load cookies
JSON Formatting: Pakai | jq '.' untuk pretty-print JSON (install jq jika belum ada)
Test Data: Email testuser@example.com dengan password TestPassword123!
Cleanup: Script otomatis hapus cookie files di akhir

## 🔧 Environment

- Base URL: `http://localhost:8081/api/auth`

## 📜 Script

### 1. HEALTH CHECK

```bash
echo "1. Testing Health Check..."
curl -X GET http://localhost:8081/api/auth/health
echo -e "\n"
```

### 2. REGISTRATION TEST (Valid Data)

```bash
echo "2. Testing Customer Registration (Valid Data)..."
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}' \
  -c cookies.txt
echo -e "\n"
```

### 3. LOGIN TEST

```bash
echo "3. Testing Customer Login..."
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{...}' \
  -c cookies.txt
echo -e "\n"
```

### 4. GET CURRENT USER

```bash
echo "4. Testing Get Current User..."
curl -X GET http://localhost:8081/api/auth/me \
  -b cookies.txt
echo -e "\n"
```

### 5. CHECK AUTHENTICATION

```bash
echo "5. Testing Check Authentication..."
curl -X GET http://localhost:8081/api/auth/check-auth \
  -b cookies.txt
echo -e "\n"
```

### 6. PASSWORD STRENGTH CHECK

```bash
echo "6. Testing Password Strength Check..."
curl -X POST http://localhost:8081/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 7. REGISTRATION STATS

```bash
echo "7. Testing Registration Statistics..."
curl -X GET http://localhost:8081/api/auth/stats
echo -e "\n"
```

### 8. VERIFY EMAIL

```bash
echo "8. Testing Email Verification..."
curl -X POST http://localhost:8081/api/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 9. REFRESH TOKEN

```bash
echo "9. Testing Refresh Token..."
curl -X POST http://localhost:8081/api/auth/refresh-token \
  -b cookies.txt \
  -c cookies.txt
echo -e "\n"
```

### 10. LOGOUT

```bash
echo "10. Testing Logout..."
curl -X POST http://localhost:8081/api/auth/logout \
  -b cookies.txt
echo -e "\n"
```

## === ERROR TESTING ===

### 11. DUPLICATE EMAIL TEST

```bash
echo "11. Testing Duplicate Email Registration..."
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 12. INVALID EMAIL FORMAT TEST

```bash
echo "12. Testing Invalid Email Format..."
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 13. WEAK PASSWORD TEST

```bash
echo "13. Testing Weak Password..."
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 14. INVALID PHONE FORMAT TEST

```bash
echo "14. Testing Invalid Phone Format..."
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 15. LOGIN WITH WRONG PASSWORD

```bash
echo "15. Testing Login with Wrong Password..."
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 16. LOGIN WITH NON-EXISTENT EMAIL

```bash
echo "16. Testing Login with Non-existent Email..."
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{...}'
echo -e "\n"
```

### 17. ACCESS PROTECTED ENDPOINT WITHOUT TOKEN

```bash
echo "17. Testing Access Protected Endpoint Without Token..."
curl -X GET http://localhost:8081/api/auth/me
echo -e "\n"
```
