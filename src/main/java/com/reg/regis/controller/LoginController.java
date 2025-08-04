package com.reg.regis.controller;

import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus; // Import HttpStatus

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API", description = "API untuk login, logout, dan manajemen session")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
@Validated
@RequiredArgsConstructor
public class LoginController {
    
    // @Autowired
    // private RegistrationService registrationService;
    private final RegistrationService registrationService;

    @Value("${app.security.cookie.secure:false}")
    private boolean cookieSecure;
    
    @Value("${app.security.cookie.domain:}")
    private String cookieDomain;
    
    /**
     * Customer login with secure cookie-based auth
     */
    @Operation(
        summary = "Login Customer",
        description = "Autentikasi customer dengan email dan password, mengembalikan JWT token dalam secure cookie"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login berhasil"),
        @ApiResponse(responseCode = "400", description = "Email atau password salah"),
        @ApiResponse(responseCode = "429", description = "Too many login attempts")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(
        @Parameter(description = "Data login (email dan password)")
        @Valid @RequestBody LoginRequest loginRequest, 
        HttpServletRequest request, 
        HttpServletResponse response) {
        try {
            // Rate limiting check (implement this later in A04)
            // String clientIp = SecurityUtil.getClientIpAddress(request);
            
            String token = registrationService.authenticateCustomer(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(loginRequest.getEmail());
            
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                // Set SECURE HTTP-only cookie
                Cookie authCookie = createSecureAuthCookie("authToken", token);
                response.addCookie(authCookie);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Login successful");
                responseData.put("customer", buildCustomerResponse(customer));
                responseData.put("token", token); // For Bearer token usage
                
                return ResponseEntity.ok(responseData);
            }
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Authentication failed"
            ));
            
        }catch (BadCredentialsException e) {
            // ** MODIFIKASI UNTUK MAXIMUM LOGIN ATTEMP **
            String errorMessage = e.getMessage();
            HttpStatus status = HttpStatus.UNAUTHORIZED; // Default untuk kredensial salah

            if (errorMessage.contains("terkunci") || errorMessage.contains("Too many failed attempts")) {
                status = HttpStatus.TOO_MANY_REQUESTS; // Gunakan 429 untuk akun terkunci
            }
            return ResponseEntity.status(status).body(Map.of(
                "success", false,
                "error", errorMessage
            ));
            // ** END MODIFIKASI **
        }  
        catch (Exception e) {
            // Log security event (implement in A09)
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Authentication failed"
            ));
        }
        
    }
    
    /**
     * Get current authenticated user
     */
    @Operation(
        summary = "Get Current User",
        description = "Mendapatkan informasi user yang sedang login berdasarkan JWT token"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User data berhasil diambil"),
        @ApiResponse(responseCode = "401", description = "Token tidak valid atau expired")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
        @Parameter(description = "JWT Token dari cookie") 
        @CookieValue(value = "authToken", required = false) String cookieToken,
        @Parameter(description = "JWT Token dari Authorization header")
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractToken(authHeader, cookieToken);

        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }
            
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "customer", buildCustomerResponse(customer)
                ));
            }
            
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
        }
    }
    
    /**
     * Secure logout - clear cookie
     */
    @Operation(
        summary = "Logout",
        description = "Logout user dan hapus secure cookie"
    )
    @ApiResponse(responseCode = "200", description = "Logout berhasil")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the auth cookie securely
        Cookie authCookie = createSecureAuthCookie("authToken", "");
        authCookie.setMaxAge(0); // Expire immediately
        response.addCookie(authCookie);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Logout successful"
        ));
    }
    
    /**
     * Refresh JWT token
     */
    @Operation(
        summary = "Refresh JWT Token",
        description = "Generate JWT token baru berdasarkan token yang masih valid"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token berhasil diperbarui"),
        @ApiResponse(responseCode = "401", description = "Token tidak valid")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
        @Parameter(description = "JWT Token dari cookie")
        @CookieValue(value = "authToken", required = false) String cookieToken,
        @Parameter(description = "JWT Token dari Authorization header")
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        HttpServletResponse response) {
        String token = extractToken(authHeader, cookieToken);

        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Token required"));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }
            
            // Generate new token
            String newToken = registrationService.generateTokenForEmail(email);
            
            // Set new secure cookie
            Cookie authCookie = createSecureAuthCookie("authToken", newToken);
            response.addCookie(authCookie);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Token refreshed",
                "token", newToken
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token refresh failed"));
        }
    }
    
    /**
     * Check authentication status (PUBLIC ENDPOINT)
     */
    @Operation(
        summary = "Check Authentication Status",
        description = "Cek apakah user masih ter-autentikasi (endpoint public)"
    )
    @ApiResponse(responseCode = "200", description = "Status autentikasi")
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuthentication(
        @Parameter(description = "JWT Token dari cookie")
        @CookieValue(value = "authToken", required = false) String cookieToken,
        @Parameter(description = "JWT Token dari Authorization header")
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractToken(authHeader, cookieToken);

        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email != null && registrationService.getCustomerByEmail(email).isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "email", email
                ));
            }
            
            return ResponseEntity.ok(Map.of("authenticated", false));
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
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
    
    /**
     * Extract token from Bearer header or cookie
     */
    private String extractToken(String authHeader, String cookieToken) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return cookieToken;
    }
    
    /**
     * Build secure customer response (no sensitive data)
     */
    private Map<String, Object> buildCustomerResponse(Customer customer) {
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("id", customer.getId());
        customerData.put("namaLengkap", customer.getNamaLengkap());
        customerData.put("email", customer.getEmail());
        customerData.put("nomorTelepon", customer.getNomorTelepon());
        customerData.put("tipeAkun", customer.getTipeAkun());
        // customerData.put("emailVerified", customer.getEmailVerified());
        customerData.put("jenisKartu", customer.getJenisKartu() != null ? customer.getJenisKartu() : "Silver");
        
        // Only include address if present
        if (customer.getAlamat() != null) {
            Map<String, Object> alamatData = new HashMap<>();
            alamatData.put("provinsi", customer.getAlamat().getProvinsi());
            alamatData.put("kota", customer.getAlamat().getKota());
            customerData.put("alamat", alamatData);
        }
        
        return customerData;
    }
    
    /**
     * Login request DTO with validation
     */
    @Schema(description = "Login request dengan email dan password")
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Email customer", example = "john.doe@example.com")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Schema(description = "Password customer", example = "JohnDoe123!")
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}