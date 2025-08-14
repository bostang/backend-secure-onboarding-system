package com.reg.regis.controller;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.RegistrationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth") 
@Tag(name = "Registration & Customer API", description = "API untuk registrasi customer dengan verifikasi Dukcapil dan manajemen profil")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class RegistrationController {
    
    @Value("${app.security.cookie.secure:false}")
    private boolean cookieSecure;
    
    @Value("${app.security.cookie.domain:}")
    private String cookieDomain;

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    
    /**
     * Register customer dengan validasi ketat
     * - NIK harus ada di database KTP Dukcapil via HTTP call
     * - Nama harus sesuai dengan KTP
     * - Email tidak boleh duplikat
     * - Nomor HP tidak boleh duplikat
     */
    @Operation(
        summary = "Register Customer Baru",
        description = "Registrasi customer dengan validasi KTP Dukcapil, email unik, nomor HP unik, dan auto-generate kode rekening + kartu debit virtual"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registrasi berhasil",
            content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validasi gagal atau data duplikat"),
        @ApiResponse(responseCode = "500", description = "Kesalahan sistem")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(
        @Parameter(description = "Data registrasi customer lengkap") 
        @Valid @RequestBody RegistrationRequest request, 
        HttpServletResponse response) {
        try {
            RegistrationResponse registrationResponse = registrationService.registerCustomer(request);
            String token = registrationService.authenticateCustomer(request.getEmail(), request.getPassword());
            
            // Set SECURE HTTP-only cookie
            Cookie authCookie = createSecureAuthCookie("authToken", token);
            response.addCookie(authCookie);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Registrasi berhasil! Data Anda telah terverifikasi dengan KTP Dukcapil.");
            responseData.put("data", registrationResponse);
            
            return ResponseEntity.ok(responseData);
            
        } catch (RuntimeException e) {
            // Return specific validation error
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "type", "validation_error"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Terjadi kesalahan sistem: " + e.getMessage(),
                "type", "system_error"
            ));
        }
    }
    
    /**
     * Check password strength
     */
    @Operation(
        summary = "Cek Kekuatan Password", 
        description = "Validasi kekuatan password berdasarkan kriteria keamanan (panjang, karakter khusus, angka, huruf besar/kecil)"
    )
    @ApiResponse(responseCode = "200", description = "Password strength berhasil dievaluasi")
    @PostMapping("/check-password")
    public ResponseEntity<?> checkPasswordStrength(
        @Parameter(description = "Request body dengan field 'password'")
        @RequestBody Map<String, String> request) {
        String password = request.get("password");
        String strength = registrationService.checkPasswordStrength(password);
        
        return ResponseEntity.ok(Map.of("strength", strength));
    }
    
    /**
     * Validate NIK format
     */
    @Operation(
        summary = "Validasi Format dan Ketersediaan NIK", 
        description = "Cek format NIK 16 digit dan apakah NIK sudah terdaftar di sistem"
    )
    @ApiResponse(responseCode = "200", description = "Validasi NIK berhasil")
    @PostMapping("/validate-nik")
    public ResponseEntity<?> validateNik(
        @Parameter(description = "Request body dengan field 'nik'")
        @RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            boolean isValid = registrationService.validateNikFormat(nik);
            boolean isExists = registrationService.getCustomerByNik(nik).isPresent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("exists", isExists);
            response.put("message", isValid ? 
                (isExists ? "NIK sudah terdaftar" : "NIK valid") : 
                "Format NIK tidak valid");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Verify email customer
     */
    @Operation(
        summary = "Verifikasi Email Customer", 
        description = "Mengaktifkan status email verified untuk customer"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Email berhasil diverifikasi")
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
        @Parameter(description = "Request body dengan field 'email'")
        @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            registrationService.verifyEmail(email);
            
            return ResponseEntity.ok(Map.of("message", "Email berhasil diverifikasi"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get registration statistics
     */
    @Operation(
        summary = "Statistik Registrasi", 
        description = "Mendapatkan statistik registrasi dan status koneksi Dukcapil service"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Statistik berhasil diambil")
    @GetMapping("/stats")
    public ResponseEntity<?> getRegistrationStats() {
        return ResponseEntity.ok(registrationService.getRegistrationStats());
    }
    
    /**
     * Health check endpoint
     */
    @Operation(
        summary = "Health Check Service", 
        description = "Status kesehatan aplikasi, koneksi Dukcapil, dan daftar endpoint yang tersedia"
    )
    @ApiResponse(responseCode = "200", description = "Health check berhasil")
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        var stats = registrationService.getRegistrationStats();
        
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Customer Registration Service dengan Dukcapil Integration",
            "timestamp", System.currentTimeMillis(),
            "dukcapilService", Map.of(
                "url", stats.getDukcapilServiceUrl(),
                "available", stats.isDukcapilServiceAvailable()
            ),
            "statistics", Map.of(
                "totalCustomers", stats.getTotalCustomers(),
                "verifiedCustomers", stats.getVerifiedCustomers(),
                "verificationRate", stats.getVerificationRate() + "%"
            ),
            "endpoints", Map.of(
                "register", "POST /auth/register",
                "checkPassword", "POST /auth/check-password",
                "validateNik", "POST /auth/validate-nik",
                "verifyEmail", "POST /auth/verify-email",
                "stats", "GET /auth/stats",
                "health", "GET /auth/health",
                "profile", "GET /auth/profile"
            )
        ));
    }
    
    /**
     * Get customer profile  
     */
    @Operation(
        summary = "Profil Customer", 
        description = "Mendapatkan profil customer berdasarkan JWT token dari cookie atau Authorization header"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profil berhasil diambil"),
        @ApiResponse(responseCode = "401", description = "Token tidak valid atau tidak ada"),
        @ApiResponse(responseCode = "404", description = "Customer tidak ditemukan")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getCustomerProfile(
        @Parameter(description = "JWT Token dari cookie authToken") 
        @CookieValue(value = "authToken", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak ditemukan"));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak valid"));
            }
            
            var customerOpt = registrationService.getCustomerByEmail(email);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                return ResponseEntity.ok(Map.of(
                    "profile", buildCustomerResponse(customer)
                ));
            }
            
            return ResponseEntity.status(404).body(Map.of("error", "Customer tidak ditemukan"));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Gagal mengambil profil"));
        }
    }
    
    /**
     * UPDATED: Helper method untuk build customer response dengan jenisKartu dan nomor kartu debit virtual
     */
    private Map<String, Object> buildCustomerResponse(Customer customer) {
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("id", customer.getId());
        customerData.put("namaLengkap", customer.getNamaLengkap());
        customerData.put("nik", customer.getNik());
        customerData.put("email", customer.getEmail());
        customerData.put("nomorTelepon", customer.getNomorTelepon());
        customerData.put("tipeAkun", customer.getTipeAkun());
        
        // Add jenisKartu field
        try {
            customerData.put("jenisKartu", customer.getJenisKartu() != null ? customer.getJenisKartu() : "Silver");
        } catch (Exception e) {
            customerData.put("jenisKartu", "Silver"); // Default fallback
        }
        
        // Add nomor kartu debit virtual
        try {
            customerData.put("nomorKartuDebitVirtual", customer.getNomorKartuDebitVirtual());
        } catch (Exception e) {
            customerData.put("nomorKartuDebitVirtual", null);
        }
        
        customerData.put("tempatLahir", customer.getTempatLahir());
        customerData.put("tanggalLahir", customer.getTanggalLahir());
        customerData.put("jenisKelamin", customer.getJenisKelamin());
        customerData.put("agama", customer.getAgama());
        customerData.put("statusPernikahan", customer.getStatusPernikahan());
        customerData.put("pekerjaan", customer.getPekerjaan());
        
        // Alamat info
        if (customer.getAlamat() != null) {
            Map<String, Object> alamatData = new HashMap<>();
            alamatData.put("namaAlamat", customer.getAlamat().getNamaAlamat());
            alamatData.put("provinsi", customer.getAlamat().getProvinsi());
            alamatData.put("kota", customer.getAlamat().getKota());
            alamatData.put("kecamatan", customer.getAlamat().getKecamatan());
            alamatData.put("kelurahan", customer.getAlamat().getKelurahan());
            alamatData.put("kodePos", customer.getAlamat().getKodePos());
            customerData.put("alamat", alamatData);
        }
        
        // Wali info (handle optional/null)
        if (customer.getWali() != null) {
            Map<String, Object> waliData = new HashMap<>();
            waliData.put("jenisWali", customer.getWali().getJenisWali());
            waliData.put("namaLengkapWali", customer.getWali().getNamaLengkapWali());
            waliData.put("pekerjaanWali", customer.getWali().getPekerjaanWali());
            waliData.put("alamatWali", customer.getWali().getAlamatWali());
            waliData.put("nomorTeleponWali", customer.getWali().getNomorTeleponWali());
            customerData.put("wali", waliData);
        } else {
            customerData.put("wali", null);
        }
        
        return customerData;
    }
    
    /**
     * Create secure authentication cookie
     */
    private Cookie createSecureAuthCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // Prevent XSS
        cookie.setSecure(cookieSecure); // HTTPS only in production
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        
        // Set domain only if specified in properties
        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }
        
        // SameSite attribute for CSRF protection
        cookie.setAttribute("SameSite", "Strict");
        
        return cookie;
    }
}