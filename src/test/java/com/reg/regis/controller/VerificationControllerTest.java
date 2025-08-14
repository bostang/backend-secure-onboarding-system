package com.reg.regis.controller;

import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import com.reg.regis.dto.response.VerificationResponse;
import com.reg.regis.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class VerificationControllerTest {

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private VerificationController verificationController;

    private NikVerificationRequest nikRequest;
    private EmailVerificationRequest emailRequest;
    private PhoneVerificationRequest phoneRequest;

    @BeforeEach
    void setUp() {
        nikRequest = new NikVerificationRequest();
        nikRequest.setNik("1234567890123456");
        nikRequest.setNamaLengkap("John Doe");
        nikRequest.setTanggalLahir(LocalDate.of(1990, 1, 1));

        emailRequest = new EmailVerificationRequest();
        emailRequest.setEmail("test@example.com");

        phoneRequest = new PhoneVerificationRequest();
        phoneRequest.setNomorTelepon("081234567890");
    }

    @Test
    void testVerifyNik_Success() {
        VerificationResponse response = new VerificationResponse(true, "NIK valid");
        when(verificationService.verifyNik(any(NikVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyNik(nikRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertEquals("NIK valid", body.get("message"));
    }

    @Test
    void testVerifyNik_Failed() {
        VerificationResponse response = new VerificationResponse(false, "NIK tidak valid");
        when(verificationService.verifyNik(any(NikVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyNik(nikRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("valid"));
        assertEquals("NIK tidak valid", body.get("message"));
    }

    @Test
    void testVerifyEmail_Available() {
        VerificationResponse response = new VerificationResponse(true, "Email tersedia");
        when(verificationService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyEmail(emailRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Email tersedia", body.get("message"));
    }

    @Test
    void testVerifyPhone_Available() {
        VerificationResponse response = new VerificationResponse(true, "Nomor telepon tersedia");
        when(verificationService.verifyPhone(any(PhoneVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyPhone(phoneRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Nomor telepon tersedia", body.get("message"));
    }

    @Test
    void testCheckNik_Valid() {
        when(verificationService.isNikRegistered("1234567890123456")).thenReturn(true);

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = verificationController.checkNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("registered"));
    }

    @Test
    void testCheckNik_Invalid() {
        Map<String, String> request = Map.of("nik", "123");
        ResponseEntity<?> result = verificationController.checkNik(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testGetVerificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", 100L);
        stats.put("verifiedCustomers", 80L);
        stats.put("dukcapilServiceHealthy", true);

        when(verificationService.getVerificationStats()).thenReturn(stats);

        ResponseEntity<?> result = verificationController.getVerificationStats();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<?> result = verificationController.healthCheck();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("OK", body.get("status"));
        assertNotNull(body.get("service"));
        assertNotNull(body.get("endpoints"));
    }

    @Test
    void testVerifyNik_Exception() {
        when(verificationService.verifyNik(any(NikVerificationRequest.class)))
            .thenThrow(new RuntimeException("Service error"));

        ResponseEntity<?> result = verificationController.verifyNik(nikRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("valid"));
        assertTrue(((String) body.get("message")).contains("Service error"));
    }

    @Test
    void testVerifyEmail_Exception() {
        when(verificationService.verifyEmail(any(EmailVerificationRequest.class)))
            .thenThrow(new RuntimeException("Email service error"));

        ResponseEntity<?> result = verificationController.verifyEmail(emailRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("available"));
        assertTrue(((String) body.get("message")).contains("Email service error"));
    }

    @Test
    void testVerifyPhone_Exception() {
        when(verificationService.verifyPhone(any(PhoneVerificationRequest.class)))
            .thenThrow(new RuntimeException("Phone service error"));

        ResponseEntity<?> result = verificationController.verifyPhone(phoneRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("available"));
        assertTrue(((String) body.get("message")).contains("Phone service error"));
    }

    @Test
    void testCheckNik_NullNik() {
        Map<String, String> request = Map.of();

        ResponseEntity<?> result = verificationController.checkNik(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testCheckNik_ServiceException() {
        when(verificationService.isNikRegistered("1234567890123456"))
            .thenThrow(new RuntimeException("Database error"));

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = verificationController.checkNik(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertTrue(((String) body.get("message")).contains("Database error"));
    }

    @Test
    void testGetVerificationStats_Exception() {
        when(verificationService.getVerificationStats())
            .thenThrow(new RuntimeException("Stats error"));

        ResponseEntity<?> result = verificationController.getVerificationStats();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue(((String) body.get("error")).contains("Stats error"));
    }

    @Test
    void testVerifyNik_WithData() {
        Map<String, Object> responseData = Map.of("additionalInfo", "test");
        VerificationResponse response = new VerificationResponse(true, "NIK valid", responseData);
        
        when(verificationService.verifyNik(any(NikVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyNik(nikRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertNotNull(body.get("data"));
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertEquals("test", data.get("additionalInfo"));
    }

    @Test
    void testVerifyEmail_WithNullData() {
        VerificationResponse response = new VerificationResponse(true, "Email tersedia", null);
        when(verificationService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyEmail(emailRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertNotNull(body.get("data"));
        assertTrue(((Map<String, Object>) body.get("data")).isEmpty());
    }

    @Test
    void testVerifyEmail_WithDataNotNull() {
        // Given
        Map<String, Object> responseData = Map.of("emailProvider", "gmail", "domain", "gmail.com");
        VerificationResponse response = new VerificationResponse(true, "Email tersedia", responseData);
        when(verificationService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        // When
        ResponseEntity<?> result = verificationController.verifyEmail(emailRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Email tersedia", body.get("message"));
        assertNotNull(body.get("data"));
        
        // Verify data is the actual responseData (not Map.of())
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertEquals("gmail", data.get("emailProvider"));
        assertEquals("gmail.com", data.get("domain"));
    }

    @Test
    void testVerifyEmail_WithDataNull() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Email tersedia", null);
        when(verificationService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        // When
        ResponseEntity<?> result = verificationController.verifyEmail(emailRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Email tersedia", body.get("message"));
        
        // Verify data is Map.of() when responseData is null
        assertNotNull(body.get("data"));
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.isEmpty()); // Map.of() returns empty map
    }

    @Test
    void testVerifyPhone_WithDataNotNull() {
        // Given
        Map<String, Object> responseData = Map.of("carrier", "Telkomsel", "region", "Jakarta");
        VerificationResponse response = new VerificationResponse(true, "Nomor telepon tersedia", responseData);
        when(verificationService.verifyPhone(any(PhoneVerificationRequest.class))).thenReturn(response);

        // When
        ResponseEntity<?> result = verificationController.verifyPhone(phoneRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Nomor telepon tersedia", body.get("message"));
        assertNotNull(body.get("data"));
        
        // Verify data is the actual responseData (not Map.of())
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertEquals("Telkomsel", data.get("carrier"));
        assertEquals("Jakarta", data.get("region"));
    }

    @Test
    void testVerifyPhone_WithDataNull() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Nomor telepon tersedia", null);
        when(verificationService.verifyPhone(any(PhoneVerificationRequest.class))).thenReturn(response);

        // When
        ResponseEntity<?> result = verificationController.verifyPhone(phoneRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Nomor telepon tersedia", body.get("message"));
        
        // Verify data is Map.of() when responseData is null
        assertNotNull(body.get("data"));
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.isEmpty()); // Map.of() returns empty map
    }

    @Test
    void testCheckNik_ValidAndRegistered() {
        // Given
        when(verificationService.isNikRegistered("1234567890123456")).thenReturn(true);

        Map<String, String> request = Map.of("nik", "1234567890123456");
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("registered"));
        assertEquals("NIK terdaftar di database Dukcapil", body.get("message"));
    }

    @Test
    void testCheckNik_ValidButNotRegistered() {
        // Given
        when(verificationService.isNikRegistered("1234567890123456")).thenReturn(false);

        Map<String, String> request = Map.of("nik", "1234567890123456");
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK tidak terdaftar di database Dukcapil", body.get("message"));
    }

    @Test
    void testCheckNik_NullNik_MissingKey() {
        // Given - request tanpa key "nik"
        Map<String, String> request = Map.of();
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testCheckNik_EmptyNik() {
        // Given - NIK empty string
        Map<String, String> request = Map.of("nik", "");
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testCheckNik_InvalidLength_TooShort() {
        // Given - NIK kurang dari 16 digit
        Map<String, String> request = Map.of("nik", "12345");
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testCheckNik_InvalidLength_TooLong() {
        // Given - NIK lebih dari 16 digit
        Map<String, String> request = Map.of("nik", "12345678901234567");
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testCheckNik_ServiceException_DatabaseError() {
        // Given
        when(verificationService.isNikRegistered("1234567890123456"))
            .thenThrow(new RuntimeException("Database connection error"));

        Map<String, String> request = Map.of("nik", "1234567890123456");
        
        // When
        ResponseEntity<?> result = verificationController.checkNik(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertTrue(((String) body.get("message")).contains("Database connection error"));
    }
}
