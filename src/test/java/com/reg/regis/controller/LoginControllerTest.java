package com.reg.regis.controller;

import com.reg.regis.model.Alamat;
import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private RegistrationService registrationService;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController(registrationService);
        ReflectionTestUtils.setField(loginController, "cookieSecure", false);
        ReflectionTestUtils.setField(loginController, "cookieDomain", "");
    }

    @Test
    void testLoginCustomer_Success() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String token = "jwt-token";
        
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail(email);
        customer.setNamaLengkap("Test User");

        when(registrationService.authenticateCustomer(email, password)).thenReturn(token);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("Login successful", body.get("message"));
        assertEquals(token, body.get("token"));
        verify(response).addCookie(any());
    }

    @Test
    void testLoginCustomer_BadCredentials() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Invalid credentials", body.get("error"));
    }

    @Test
    void testLoginCustomer_AccountLocked() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new BadCredentialsException("Akun terkunci karena terlalu banyak percobaan login"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
    }

    @Test
    void testGetCurrentUser_Success() {
        // Given
        String token = "valid-token";
        String email = "test@example.com";
        
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setNamaLengkap("Test User");

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("authenticated"));
    }

    @Test
    void testGetCurrentUser_InvalidToken() {
        // Given
        String invalidToken = "invalid-token";

        when(registrationService.getEmailFromToken(invalidToken)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser(invalidToken, null);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Invalid token", body.get("error"));
    }

    @Test
    void testLogout_Success() {
        // When
        ResponseEntity<?> responseEntity = loginController.logout(response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("Logout successful", body.get("message"));
        verify(response).addCookie(any());
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        String oldToken = "old-token";
        String newToken = "new-token";
        String email = "test@example.com";

        when(registrationService.getEmailFromToken(oldToken)).thenReturn(email);
        when(registrationService.generateTokenForEmail(email)).thenReturn(newToken);

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(oldToken, null, response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(newToken, body.get("token"));
        verify(response).addCookie(any());
    }

    @Test
    void testCheckAuthentication_Authenticated() {
        // Given
        String token = "valid-token";
        String email = "test@example.com";
        Customer customer = new Customer();

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("authenticated"));
        assertEquals(email, body.get("email"));
    }

    @Test
    void testCheckAuthentication_NotAuthenticated() {
        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(null, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testBuildCustomerResponse_WithAddress() {
        // Given
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setNamaLengkap("John Doe");
        customer.setEmail("john@example.com");
        customer.setNomorTelepon("08123456789");
        customer.setTipeAkun("Premium");
        customer.setJenisKartu("Gold");
        
        // Mock alamat
        Alamat alamat = new Alamat();
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta Selatan");
        customer.setAlamat(alamat);

        // When - using reflection to call private method
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) ReflectionTestUtils.invokeMethod(
            loginController, "buildCustomerResponse", customer);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("id"));
        assertEquals("John Doe", result.get("namaLengkap"));
        assertEquals("john@example.com", result.get("email"));
        assertEquals("08123456789", result.get("nomorTelepon"));
        assertEquals("Premium", result.get("tipeAkun"));
        assertEquals("Gold", result.get("jenisKartu"));
        
        // Verify alamat
        assertNotNull(result.get("alamat"));
        @SuppressWarnings("unchecked")
        Map<String, Object> alamatData = (Map<String, Object>) result.get("alamat");
        assertEquals("DKI Jakarta", alamatData.get("provinsi"));
        assertEquals("Jakarta Selatan", alamatData.get("kota"));
    }

    @Test
    void testBuildCustomerResponse_WithoutAddress() {
        // Given
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setNamaLengkap("Jane Doe");
        customer.setEmail("jane@example.com");
        customer.setNomorTelepon("08987654321");
        customer.setTipeAkun("Basic");
        customer.setJenisKartu(null); // Test default "Silver"
        customer.setAlamat(null); // No address

        // When - using reflection to call private method
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) ReflectionTestUtils.invokeMethod(
            loginController, "buildCustomerResponse", customer);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.get("id"));
        assertEquals("Jane Doe", result.get("namaLengkap"));
        assertEquals("jane@example.com", result.get("email"));
        assertEquals("08987654321", result.get("nomorTelepon"));
        assertEquals("Basic", result.get("tipeAkun"));
        assertEquals("Silver", result.get("jenisKartu")); // Default value
        
        // Verify no alamat included
        assertNull(result.get("alamat"));
    }

    @Test
    void testLoginCustomer_CustomerNotFound() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String token = "jwt-token";

        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(registrationService.authenticateCustomer(email, password)).thenReturn(token);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();

        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Authentication failed", body.get("error"));
    }


    @Test
    void testLoginCustomer_GeneralException() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new RuntimeException("Database connection error"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Authentication failed", body.get("error"));
    }

    @Test
    void testGetCurrentUser_NoToken() {
        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser(null, null);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Authentication required", body.get("error"));
    }

    @Test
    void testGetCurrentUser_EmptyToken() {
        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser("", null);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Authentication required", body.get("error"));
    }

    @Test
    void testGetCurrentUser_CustomerNotFound() {
        // Given
        String token = "valid-token";
        String email = "test@example.com";

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser(token, null);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("User not found", body.get("error"));
    }

    @Test
    void testGetCurrentUser_Exception() {
        // Given
        String token = "valid-token";

        when(registrationService.getEmailFromToken(token))
            .thenThrow(new RuntimeException("Token parsing error"));

        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser(token, null);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Authentication failed", body.get("error"));
    }

    @Test
    void testGetCurrentUser_WithAuthHeader() {
        // Given
        String token = "valid-token";
        String authHeader = "Bearer " + token;
        String email = "test@example.com";
        
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setNamaLengkap("Test User");

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<?> responseEntity = loginController.getCurrentUser(null, authHeader);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("authenticated"));
    }

    @Test
    void testRefreshToken_NoToken() {
        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(null, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Token required", body.get("error"));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Given
        String invalidToken = "invalid-token";

        when(registrationService.getEmailFromToken(invalidToken)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(invalidToken, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Invalid token", body.get("error"));
    }

    @Test
    void testRefreshToken_Exception() {
        // Given
        String token = "valid-token";

        when(registrationService.getEmailFromToken(token))
            .thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(token, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Token refresh failed", body.get("error"));
    }

    @Test
    void testCheckAuthentication_WithAuthHeader() {
        // Given
        String token = "valid-token";
        String authHeader = "Bearer " + token;
        String email = "test@example.com";
        Customer customer = new Customer();

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(null, authHeader);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("authenticated"));
        assertEquals(email, body.get("email"));
    }

    @Test
    void testCheckAuthentication_InvalidToken() {
        // Given
        String token = "invalid-token";

        when(registrationService.getEmailFromToken(token)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testCheckAuthentication_CustomerNotFound() {
        // Given
        String token = "valid-token";
        String email = "test@example.com";

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testCheckAuthentication_Exception() {
        // Given
        String token = "valid-token";

        when(registrationService.getEmailFromToken(token))
            .thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();

        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }


    @Test
    void testExtractToken_FromBearerHeader() {
        // Given
        String token = "test-token";
        String authHeader = "Bearer " + token;
        String cookieToken = "cookie-token";
        
        // When - using reflection to test private method
        String result = (String) ReflectionTestUtils.invokeMethod(
            loginController, "extractToken", authHeader, cookieToken);

        // Then
        assertEquals(token, result);
    }

    @Test
    void testExtractToken_FromCookie() {
        // Given
        String authHeader = null;
        String cookieToken = "cookie-token";
        
        // When
        String result = (String) ReflectionTestUtils.invokeMethod(
            loginController, "extractToken", authHeader, cookieToken);

        // Then
        assertEquals(cookieToken, result);
    }

    @Test
    void testExtractToken_InvalidBearerHeader() {
        // Given
        String authHeader = "InvalidHeader test-token";
        String cookieToken = "cookie-token";
        
        // When
        String result = (String) ReflectionTestUtils.invokeMethod(
            loginController, "extractToken", authHeader, cookieToken);

        // Then
        assertEquals(cookieToken, result);
    }

    // Tambahkan import ini di bagian atas file:
    // import jakarta.servlet.http.Cookie;

    @Test
    void testCreateSecureAuthCookie_WithDomain() {
        // Given
        ReflectionTestUtils.setField(loginController, "cookieDomain", "example.com");
        String name = "authToken";
        String value = "token123";

        // When - using reflection to call private method
        Cookie result = (Cookie) ReflectionTestUtils.invokeMethod(
            loginController, "createSecureAuthCookie", name, value);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(value, result.getValue());
        assertTrue(result.isHttpOnly());
        assertEquals("example.com", result.getDomain());
        assertEquals("/", result.getPath());
        assertEquals(24 * 60 * 60, result.getMaxAge()); // 24 hours
    }

    @Test
    void testCreateSecureAuthCookie_WithoutDomain() {
        // Given - cookieDomain is empty (already set in setUp)
        String name = "authToken";
        String value = "token123";

        // When
        Cookie result = (Cookie) ReflectionTestUtils.invokeMethod(
            loginController, "createSecureAuthCookie", name, value);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(value, result.getValue());
        assertTrue(result.isHttpOnly());
        assertNull(result.getDomain()); // Domain not set
        assertEquals("/", result.getPath());
        assertEquals(24 * 60 * 60, result.getMaxAge());
    }

    @Test
    void testCreateSecureAuthCookie_NullDomain() {
        // Given
        ReflectionTestUtils.setField(loginController, "cookieDomain", null);
        String name = "authToken";
        String value = "token123";

        // When
        Cookie result = (Cookie) ReflectionTestUtils.invokeMethod(
            loginController, "createSecureAuthCookie", name, value);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(value, result.getValue());
        assertTrue(result.isHttpOnly());
        assertNull(result.getDomain()); // Domain not set when null
    }

    @Test
    void testCreateSecureAuthCookie_EmptyDomain() {
        // Given
        ReflectionTestUtils.setField(loginController, "cookieDomain", "");
        String name = "authToken";
        String value = "token123";

        // When
        Cookie result = (Cookie) ReflectionTestUtils.invokeMethod(
            loginController, "createSecureAuthCookie", name, value);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(value, result.getValue());
        assertTrue(result.isHttpOnly());
        assertNull(result.getDomain()); // Domain not set when empty
    }

    @Test
    void testRefreshToken_TokenNullOrEmpty_Null() {
        // Given - token is null
        String token = null;

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(token, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Token required", body.get("error"));
    }

    @Test
    void testRefreshToken_TokenNullOrEmpty_Empty() {
        // Given - token is empty
        String token = "";

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(token, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Token required", body.get("error"));
    }

    @Test
    void testRefreshToken_EmailNull() {
        // Given
        String token = "valid-token";
        
        // Mock getEmailFromToken to return null
        when(registrationService.getEmailFromToken(token)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(token, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Invalid token", body.get("error"));
    }

    @Test
    void testRefreshToken_SuccessPath() {
        // Given
        String oldToken = "old-token";
        String newToken = "new-token";
        String email = "test@example.com";

        when(registrationService.getEmailFromToken(oldToken)).thenReturn(email);
        when(registrationService.generateTokenForEmail(email)).thenReturn(newToken);

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(oldToken, null, response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("Token refreshed", body.get("message"));
        assertEquals(newToken, body.get("token"));
        
        // Verify cookie was set
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void testRefreshToken_ExceptionHandling() {
        // Given
        String token = "valid-token";
        
        // Mock to throw exception
        when(registrationService.getEmailFromToken(token))
            .thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<?> responseEntity = loginController.refreshToken(token, null, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertEquals("Token refresh failed", body.get("error"));
    }

    @Test
    void testCheckAuthentication_TokenNullOrEmpty_Null() {
        // Given - token is null (extractToken returns null)
        
        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(null, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testCheckAuthentication_TokenNullOrEmpty_Empty() {
        // Given - token is empty string
        
        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication("", null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testCheckAuthentication_EmailNotNullAndCustomerExists() {
        // Given
        String token = "valid-token";
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertTrue((Boolean) body.get("authenticated"));
        assertEquals(email, body.get("email"));
    }

    @Test
    void testCheckAuthentication_EmailNullOrCustomerNotExists_EmailNull() {
        // Given
        String token = "invalid-token";

        when(registrationService.getEmailFromToken(token)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testCheckAuthentication_EmailNullOrCustomerNotExists_CustomerNotFound() {
        // Given
        String token = "valid-token";
        String email = "test@example.com";

        when(registrationService.getEmailFromToken(token)).thenReturn(email);
        when(registrationService.getCustomerByEmail(email)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }

    @Test
    void testCheckAuthentication_ExceptionHandling() {
        // Given
        String token = "valid-token";

        when(registrationService.getEmailFromToken(token))
            .thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<?> responseEntity = loginController.checkAuthentication(token, null);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();

        assertNotNull(body);
        assertFalse((Boolean) body.get("authenticated"));
    }


    @Test
    void testLoginCustomer_BadCredentials_WithTerkunci() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Mock BadCredentialsException dengan message yang mengandung "terkunci"
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new BadCredentialsException("Akun terkunci karena terlalu banyak percobaan login"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Akun terkunci karena terlalu banyak percobaan login", body.get("error"));
    }

    @Test
    void testLoginCustomer_BadCredentials_WithTooManyFailedAttempts() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Mock BadCredentialsException dengan message yang mengandung "Too many failed attempts"
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new BadCredentialsException("Too many failed attempts"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Too many failed attempts", body.get("error"));
    }

    @Test
    void testLoginCustomer_BadCredentials_WithoutSpecialMessage() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Mock BadCredentialsException dengan message biasa (tidak mengandung "terkunci" atau "Too many failed attempts")
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new BadCredentialsException("Invalid email or password"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Invalid email or password", body.get("error"));
    }

    @Test
    void testLoginCustomer_GeneralException_DatabaseError() {
        // Given
        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Mock general Exception (bukan BadCredentialsException)
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenThrow(new RuntimeException("Database connection error"));

        // When
        ResponseEntity<?> responseEntity = loginController.loginCustomer(loginRequest, request, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Authentication failed", body.get("error"));
    }
}