package com.reg.regis.service;

import com.reg.regis.dto.response.DukcapilResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DukcapilClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DukcapilClientService dukcapilClientService;

    @Test
    void verifyNikNameAndBirthDate_Success_ReturnsValidResponse() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");

        DukcapilResponseDto mockResponse = new DukcapilResponseDto(true, "Valid");
        ResponseEntity<DukcapilResponseDto> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class)))
                .thenReturn(responseEntity);

        // When
        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertTrue(result.isValid());
        assertEquals("Valid", result.getMessage());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class));
    }

    @Test
    void verifyNikNameAndBirthDate_ServiceUnavailable_ReturnsErrorResponse() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        // When
        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("tidak dapat diakses"));
    }

    @Test
    void verifyNikNameAndBirthDate_ClientError_ReturnsErrorResponse() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // When
        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Error validasi"));
    }

    @Test
    void isNikExists_Success_ReturnsTrue() {
        // Given
        String nik = "1234567890123456";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("exists", true);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "checkNikEndpoint", "/check");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isNikExists(nik);

        // Then
        assertTrue(result);
    }

    @Test
    void isNikExists_NotFound_ReturnsFalse() {
        // Given
        String nik = "1234567890123456";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("exists", false);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "checkNikEndpoint", "/check");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isNikExists(nik);

        // Then
        assertFalse(result);
    }

    @Test
    void isDukcapilServiceHealthy_Healthy_ReturnsTrue() {
        // Given
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("status", "OK");

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(healthResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isDukcapilServiceHealthy();

        // Then
        assertTrue(result);
    }

    @Test
    void isDukcapilServiceHealthy_Unhealthy_ReturnsFalse() {
        // Given
        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("Service down"));

        // When
        boolean result = dukcapilClientService.isDukcapilServiceHealthy();

        // Then
        assertFalse(result);
    }

    // BRANCH COVERAGE IMPROVEMENTS - Test missing scenarios

    @Test
    void verifyNikNameAndBirthDate_ServerError_ReturnsErrorResponse() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class)))
                .thenThrow(new org.springframework.web.client.HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

        // When
        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("error internal"));
    }

    @Test
    void verifyNikNameAndBirthDate_UnexpectedException_ReturnsErrorResponse() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Terjadi kesalahan"));
    }

    @Test
    void verifyNikNameAndBirthDate_NullResponseBody_ReturnsErrorResponse() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");

        ResponseEntity<DukcapilResponseDto> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DukcapilResponseDto.class)))
                .thenReturn(responseEntity);

        // When
        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Tidak ada response"));
    }

    @Test
    void isNikExists_ExceptionThrown_ReturnsFalse() {
        // Given
        String nik = "1234567890123456";

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "checkNikEndpoint", "/check");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("Connection failed"));

        // When
        boolean result = dukcapilClientService.isNikExists(nik);

        // Then
        assertFalse(result);
    }

    @Test
    void isNikExists_EmptyResponseBody_ReturnsFalse() {
        // Given
        String nik = "1234567890123456";

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "checkNikEndpoint", "/check");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isNikExists(nik);

        // Then
        assertFalse(result);
    }

    @Test
    void isNikExists_ResponseWithoutExistsKey_ReturnsFalse() {
        // Given
        String nik = "1234567890123456";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "ok"); // No "exists" key

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(dukcapilClientService, "checkNikEndpoint", "/check");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isNikExists(nik);

        // Then
        assertFalse(result);
    }

    @Test
    void isDukcapilServiceHealthy_NullResponseBody_ReturnsFalse() {
        // Given
        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isDukcapilServiceHealthy();

        // Then
        assertFalse(result);
    }

    @Test
    void isDukcapilServiceHealthy_WrongStatusValue_ReturnsFalse() {
        // Given
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("status", "DOWN"); // Not "OK"

        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8080");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(healthResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        boolean result = dukcapilClientService.isDukcapilServiceHealthy();

        // Then
        assertFalse(result);
    }

    @Test
    void getDukcapilBaseUrl_ReturnsConfiguredUrl() {
        // Given
        String expectedUrl = "http://localhost:8080";
        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", expectedUrl);

        // When
        String result = dukcapilClientService.getDukcapilBaseUrl();

        // Then
        assertEquals(expectedUrl, result);
    }
}