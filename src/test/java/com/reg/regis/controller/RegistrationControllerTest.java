package com.reg.regis.controller;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.RegistrationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.model.Wali;
import com.reg.regis.model.Alamat;
import com.reg.regis.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    private RegistrationRequest registrationRequest;
    private Customer customer;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        
        registrationRequest = new RegistrationRequest();
        registrationRequest.setNik("1234567890123456");
        registrationRequest.setNamaLengkap("John Doe");
        registrationRequest.setEmail("john@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setNomorTelepon("081234567890");
        registrationRequest.setTanggalLahir(LocalDate.of(1990, 1, 1));

        RegistrationRequest.AlamatRequest alamat = new RegistrationRequest.AlamatRequest();
        alamat.setNamaAlamat("Jl. Test");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta");
        alamat.setKecamatan("Test");
        alamat.setKelurahan("Test");
        alamat.setKodePos("12345");
        registrationRequest.setAlamat(alamat);

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("john@example.com");
        customer.setNamaLengkap("John Doe");
        customer.setJenisKartu("Silver");
    }

    @Test
    void testRegisterCustomer_Success() {
        RegistrationResponse registrationResponse = new RegistrationResponse(
            "Silver", "John Doe", "12345678", "PERSONAL", "4101 2345 6789 0123"
        );

        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenReturn(registrationResponse);
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenReturn("jwt-token");

        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("success"));
        
        verify(registrationService).registerCustomer(any(RegistrationRequest.class));
        verify(registrationService).authenticateCustomer("john@example.com", "password123");
    }

    @Test
    void testRegisterCustomer_ValidationError() {
        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenThrow(new RuntimeException("Email sudah terdaftar"));

        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("Email sudah terdaftar", body.get("error"));
    }

    @Test
    void testCheckPasswordStrength() {
        when(registrationService.checkPasswordStrength("password123"))
            .thenReturn("sedang");

        Map<String, String> request = Map.of("password", "password123");
        ResponseEntity<?> result = registrationController.checkPasswordStrength(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("sedang", body.get("strength"));
    }

    @Test
    void testValidateNik_Valid() {
        when(registrationService.validateNikFormat("1234567890123456")).thenReturn(true);
        when(registrationService.getCustomerByNik("1234567890123456")).thenReturn(Optional.empty());

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = registrationController.validateNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertFalse((Boolean) body.get("exists"));
    }

    @Test
    void testValidateNik_Invalid() {
        when(registrationService.validateNikFormat("123")).thenReturn(false);

        Map<String, String> request = Map.of("nik", "123");
        ResponseEntity<?> result = registrationController.validateNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("valid"));
        assertEquals("Format NIK tidak valid", body.get("message"));
    }

    @Test
    void testVerifyEmail() {
        doNothing().when(registrationService).verifyEmail("test@example.com");

        Map<String, String> request = Map.of("email", "test@example.com");
        ResponseEntity<?> result = registrationController.verifyEmail(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Email berhasil diverifikasi", body.get("message"));
    }

    @Test
    void testGetRegistrationStats() {
        RegistrationService.RegistrationStats stats = new RegistrationService.RegistrationStats(
            100L, 80L, 80.0, true, "http://localhost:8081"
        );
        when(registrationService.getRegistrationStats()).thenReturn(stats);

        ResponseEntity<?> result = registrationController.getRegistrationStats();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
    }

    @Test
    void testHealthCheck() {
        RegistrationService.RegistrationStats stats = new RegistrationService.RegistrationStats(
            100L, 80L, 80.0, true, "http://localhost:8081"
        );
        when(registrationService.getRegistrationStats()).thenReturn(stats);

        ResponseEntity<?> result = registrationController.healthCheck();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("OK", body.get("status"));
    }

    @Test
    void testGetCustomerProfile_ValidToken() {
        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertNotNull(body.get("profile"));
    }

    @Test
    void testGetCustomerProfile_InvalidToken() {
        when(registrationService.getEmailFromToken("invalid-token")).thenReturn(null);

        ResponseEntity<?> result = registrationController.getCustomerProfile("invalid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Token tidak valid", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_NoToken() {
        ResponseEntity<?> result = registrationController.getCustomerProfile(null);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Token tidak ditemukan", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_WithJenisKartuAndDebitCard() {
        // Mock customer dengan jenisKartu dan nomor kartu debit
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setNamaLengkap("John Doe");
        customer.setJenisKartu("Gold");
        customer.setNomorKartuDebitVirtual("4101 2345 6789 0123");
        customer.setEmail("john@example.com");

        // Mock service calls
        when(registrationService.getEmailFromToken("valid-token"))
            .thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com"))
            .thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        
        assertEquals("Gold", profile.get("jenisKartu"));
        assertEquals("4101 2345 6789 0123", profile.get("nomorKartuDebitVirtual"));
        
        verify(registrationService).getEmailFromToken("valid-token");
        verify(registrationService).getCustomerByEmail("john@example.com");
    }

    @Test
    void testGetCustomerProfile_WithNullJenisKartu() {
        // Mock customer dengan jenisKartu null
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setNamaLengkap("Jane Doe");
        customer.setJenisKartu(null); // null value
        customer.setEmail("jane@example.com");

        when(registrationService.getEmailFromToken("valid-token"))
            .thenReturn("jane@example.com");
        when(registrationService.getCustomerByEmail("jane@example.com"))
            .thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        
        assertEquals("Silver", profile.get("jenisKartu")); // default fallback
    }

    @Test 
    void testGetCustomerProfile_WithWaliData() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("minor@example.com");
        
        // Mock wali object (assume getter exists)
        Wali mockWali = mock(Wali.class);
        when(mockWali.getJenisWali()).thenReturn("AYAH");
        when(mockWali.getNamaLengkapWali()).thenReturn("Bapak Doe");
        when(mockWali.getPekerjaanWali()).thenReturn("PNS");
        customer.setWali(mockWali);

        when(registrationService.getEmailFromToken("valid-token"))
            .thenReturn("minor@example.com");
        when(registrationService.getCustomerByEmail("minor@example.com"))
            .thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        Map<String, Object> wali = (Map<String, Object>) profile.get("wali");
        
        assertNotNull(wali);
        assertEquals("AYAH", wali.get("jenisWali"));
        assertEquals("Bapak Doe", wali.get("namaLengkapWali"));
    }

    @Test
    void testGetCustomerProfile_WithAlamatData() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        
        // Mock alamat
        Alamat mockAlamat = mock(Alamat.class);
        when(mockAlamat.getNamaAlamat()).thenReturn("Jl. Test");
        when(mockAlamat.getProvinsi()).thenReturn("DKI Jakarta");
        when(mockAlamat.getKota()).thenReturn("Jakarta");
        customer.setAlamat(mockAlamat);

        when(registrationService.getEmailFromToken("valid-token"))
            .thenReturn("test@example.com");  
        when(registrationService.getCustomerByEmail("test@example.com"))
            .thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        Map<String, Object> alamat = (Map<String, Object>) profile.get("alamat");
        
        assertNotNull(alamat);
        assertEquals("Jl. Test", alamat.get("namaAlamat"));
        assertEquals("DKI Jakarta", alamat.get("provinsi"));
    }

    @Test
    void testRegisterCustomer_ServiceException() {
        // Test for general service exception (masuk ke RuntimeException catch)
        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenThrow(new IllegalStateException("Service unavailable"));

        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("Service unavailable", body.get("error"));
        assertEquals("validation_error", body.get("type"));
    }

    @Test
    void testValidateNik_Exception() {
        when(registrationService.validateNikFormat("1234567890123456"))
            .thenThrow(new RuntimeException("Service unavailable"));

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = registrationController.validateNik(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Service unavailable", body.get("error"));
    }

    @Test
    void testValidateNik_ExistsInSystem() {
        when(registrationService.validateNikFormat("1234567890123456")).thenReturn(true);
        when(registrationService.getCustomerByNik("1234567890123456")).thenReturn(Optional.of(customer));

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = registrationController.validateNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertTrue((Boolean) body.get("exists"));
        assertEquals("NIK sudah terdaftar", body.get("message"));
    }

    @Test
    void testVerifyEmail_Exception() {
        doThrow(new RuntimeException("Email service error"))
            .when(registrationService).verifyEmail("test@example.com");

        Map<String, String> request = Map.of("email", "test@example.com");
        ResponseEntity<?> result = registrationController.verifyEmail(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Email service error", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_EmptyToken() {
        ResponseEntity<?> result = registrationController.getCustomerProfile("");

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Token tidak ditemukan", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_CustomerNotFound() {
        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Customer tidak ditemukan", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_Exception() {
        when(registrationService.getEmailFromToken("valid-token"))
            .thenThrow(new RuntimeException("Token parsing error"));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Gagal mengambil profil", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_WithExceptionInJenisKartu() {
        // Mock customer yang throw exception saat getJenisKartu()
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1L);
        when(customer.getNamaLengkap()).thenReturn("John Doe");
        when(customer.getEmail()).thenReturn("john@example.com");
        when(customer.getJenisKartu()).thenThrow(new RuntimeException("Field access error"));
        when(customer.getAlamat()).thenReturn(null);
        when(customer.getWali()).thenReturn(null);

        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        
        assertEquals("Silver", profile.get("jenisKartu")); // default fallback
    }

    @Test
    void testGetCustomerProfile_WithExceptionInNomorKartuDebit() {
        // Mock customer yang throw exception saat getNomorKartuDebitVirtual()
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1L);
        when(customer.getNamaLengkap()).thenReturn("John Doe");
        when(customer.getEmail()).thenReturn("john@example.com");
        when(customer.getJenisKartu()).thenReturn("Gold");
        when(customer.getNomorKartuDebitVirtual()).thenThrow(new RuntimeException("Field access error"));
        when(customer.getAlamat()).thenReturn(null);
        when(customer.getWali()).thenReturn(null);

        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        
        assertNull(profile.get("nomorKartuDebitVirtual")); // null fallback
    }

    @Test
    void testCreateSecureAuthCookie_WithCookieDomain() {
        // Setup controller dengan cookieDomain tidak kosong
        ReflectionTestUtils.setField(registrationController, "cookieDomain", "example.com");
        ReflectionTestUtils.setField(registrationController, "cookieSecure", true);

        // Test menggunakan reflection untuk method private
        Object result = ReflectionTestUtils.invokeMethod(
            registrationController, "createSecureAuthCookie", "authToken", "test-token");

        assertNotNull(result);
        assertTrue(result instanceof Cookie);
        Cookie cookie = (Cookie) result;
        assertEquals("authToken", cookie.getName());
        assertEquals("test-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(24 * 60 * 60, cookie.getMaxAge());
    }

    @Test
    void testCreateSecureAuthCookie_WithoutCookieDomain() {
        // Setup controller dengan cookieDomain kosong
        ReflectionTestUtils.setField(registrationController, "cookieDomain", "");
        ReflectionTestUtils.setField(registrationController, "cookieSecure", false);

        // Test menggunakan reflection untuk method private
        Object result = ReflectionTestUtils.invokeMethod(
            registrationController, "createSecureAuthCookie", "authToken", "test-token");

        assertNotNull(result);
        assertTrue(result instanceof Cookie);
        Cookie cookie = (Cookie) result;
        assertEquals("authToken", cookie.getName());
        assertEquals("test-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(24 * 60 * 60, cookie.getMaxAge());
    }

    @Test
    void testCreateSecureAuthCookie_WithNullCookieDomain() {
        // Setup controller dengan cookieDomain null
        ReflectionTestUtils.setField(registrationController, "cookieDomain", null);
        ReflectionTestUtils.setField(registrationController, "cookieSecure", false);

        // Test menggunakan reflection untuk method private
        Object result = ReflectionTestUtils.invokeMethod(
            registrationController, "createSecureAuthCookie", "authToken", "test-token");

        assertNotNull(result);
        assertTrue(result instanceof Cookie);
        Cookie cookie = (Cookie) result;
        assertEquals("authToken", cookie.getName());
        assertEquals("test-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getSecure());
    }

    @Test
    void testGetCustomerProfile_WithCompleteAlamatData() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setJenisKartu("Platinum");
        
        // Mock alamat dengan semua field
        Alamat mockAlamat = mock(Alamat.class);
        when(mockAlamat.getNamaAlamat()).thenReturn("Jl. Sudirman No. 1");
        when(mockAlamat.getProvinsi()).thenReturn("DKI Jakarta");
        when(mockAlamat.getKota()).thenReturn("Jakarta");
        when(mockAlamat.getKecamatan()).thenReturn("Tanah Abang");
        when(mockAlamat.getKelurahan()).thenReturn("Bendungan Hilir");
        when(mockAlamat.getKodePos()).thenReturn("10210");
        customer.setAlamat(mockAlamat);

        when(registrationService.getEmailFromToken("valid-token")).thenReturn("test@example.com");
        when(registrationService.getCustomerByEmail("test@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        Map<String, Object> alamat = (Map<String, Object>) profile.get("alamat");
        
        assertNotNull(alamat);
        assertEquals("Jl. Sudirman No. 1", alamat.get("namaAlamat"));
        assertEquals("DKI Jakarta", alamat.get("provinsi"));
        assertEquals("Jakarta", alamat.get("kota"));
        assertEquals("Tanah Abang", alamat.get("kecamatan"));
        assertEquals("Bendungan Hilir", alamat.get("kelurahan"));
        assertEquals("10210", alamat.get("kodePos"));
    }

    @Test
    void testGetCustomerProfile_WithCompleteWaliData() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("minor@example.com");
        
        // Mock wali dengan semua field
        Wali mockWali = mock(Wali.class);
        when(mockWali.getJenisWali()).thenReturn("IBU");
        when(mockWali.getNamaLengkapWali()).thenReturn("Ibu Jane Doe");
        when(mockWali.getPekerjaanWali()).thenReturn("Guru");
        when(mockWali.getAlamatWali()).thenReturn("Jl. Kebon Jeruk No. 5");
        when(mockWali.getNomorTeleponWali()).thenReturn("081987654321");
        customer.setWali(mockWali);

        when(registrationService.getEmailFromToken("valid-token")).thenReturn("minor@example.com");
        when(registrationService.getCustomerByEmail("minor@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        Map<String, Object> body = (Map<String, Object>) result.getBody();
        Map<String, Object> profile = (Map<String, Object>) body.get("profile");
        Map<String, Object> wali = (Map<String, Object>) profile.get("wali");
        
        assertNotNull(wali);
        assertEquals("IBU", wali.get("jenisWali"));
        assertEquals("Ibu Jane Doe", wali.get("namaLengkapWali"));
        assertEquals("Guru", wali.get("pekerjaanWali"));
        assertEquals("Jl. Kebon Jeruk No. 5", wali.get("alamatWali"));
        assertEquals("081987654321", wali.get("nomorTeleponWali"));
    }

    @Test
    void testRegisterCustomer_RuntimeExceptionSystemError() {
        // Given - Mock RuntimeException dengan message khusus untuk trigger system error
        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenThrow(new RuntimeException("Terjadi kesalahan sistem: Database connection timeout"));

        // When
        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertTrue(((String) body.get("error")).contains("Database connection timeout"));
        assertEquals("validation_error", body.get("type")); // RuntimeException masuk ke validation_error
    }

    @Test
    void testRegisterCustomer_SuccessWithCookieSet() {
        // Given
        RegistrationResponse registrationResponse = new RegistrationResponse(
            "Gold", "Jane Doe", "87654321", "PREMIUM", "4101 9876 5432 1098"
        );
        String token = "jwt-token-67890";

        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenReturn(registrationResponse);
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenReturn(token);

        // When
        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("Registrasi berhasil! Data Anda telah terverifikasi dengan KTP Dukcapil.", body.get("message"));
        assertEquals(registrationResponse, body.get("data"));
        
        // Verify cookie di-set di response
        assertNotNull(response.getCookies());
        assertTrue(response.getCookies().length > 0);
        
        verify(registrationService).registerCustomer(any(RegistrationRequest.class));
        verify(registrationService).authenticateCustomer(registrationRequest.getEmail(), registrationRequest.getPassword());
    }

    @Test
    void testCheckPasswordStrength_WeakPassword() {
        // Given - test untuk password lemah
        when(registrationService.checkPasswordStrength("123"))
            .thenReturn("lemah");

        Map<String, String> request = Map.of("password", "123");
        
        // When
        ResponseEntity<?> result = registrationController.checkPasswordStrength(request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        
        assertNotNull(body);
        assertEquals("lemah", body.get("strength"));
    }

    @Test
    void testCheckPasswordStrength_StrongPassword() {
        // Given - test untuk password kuat
        when(registrationService.checkPasswordStrength("StrongP@ssw0rd123!"))
            .thenReturn("kuat");

        Map<String, String> request = Map.of("password", "StrongP@ssw0rd123!");
        
        // When
        ResponseEntity<?> result = registrationController.checkPasswordStrength(request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        
        assertNotNull(body);
        assertEquals("kuat", body.get("strength"));
    }

    @Test
    void testGetRegistrationStats_SuccessWithDifferentStats() {
        // Given - test dengan stats yang berbeda
        RegistrationService.RegistrationStats stats = new RegistrationService.RegistrationStats(
            250L, 200L, 80.0, false, "http://localhost:8082"
        );
        when(registrationService.getRegistrationStats()).thenReturn(stats);

        // When
        ResponseEntity<?> result = registrationController.getRegistrationStats();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
        
        verify(registrationService).getRegistrationStats();
    }

    @Test
    void testRegisterCustomer_GeneralExceptionFromService() {
        // Given - Mock service method yang throw checked exception atau other exception
        // Kita bisa mock IOException atau exception lain yang bisa terjadi di service layer
        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenAnswer(invocation -> {
                // Simulate service method yang internally catch IOException dan re-throw sebagai Exception
                throw new Exception("Database connection failed");
            });

        // When
        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertTrue(((String) body.get("error")).contains("Database connection failed"));
        assertEquals("system_error", body.get("type"));
    }

    @Test 
    void testRegisterCustomer_AuthenticateCustomerException() {
        // Given - registrasi berhasil tapi authenticate gagal
        RegistrationResponse registrationResponse = new RegistrationResponse(
            "Silver", "John Doe", "12345678", "PERSONAL", "4101 2345 6789 0123"
        );

        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenReturn(registrationResponse);
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenAnswer(invocation -> {
                throw new Exception("Authentication service unavailable");
            });

        // When
        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertTrue(((String) body.get("error")).contains("Authentication service unavailable"));
        assertEquals("system_error", body.get("type"));
    }
}
