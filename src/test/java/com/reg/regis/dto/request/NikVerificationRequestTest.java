package com.reg.regis.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

class NikVerificationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        NikVerificationRequest request = new NikVerificationRequest();

        // Then
        assertNull(request.getNik());
        assertNull(request.getNamaLengkap());
        assertNull(request.getTanggalLahir());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);

        // When
        NikVerificationRequest request = new NikVerificationRequest(nik, namaLengkap, tanggalLahir);

        // Then
        assertEquals("1234567890123456", request.getNik());
        assertEquals("John Doe", request.getNamaLengkap());
        assertEquals(LocalDate.of(1990, 1, 1), request.getTanggalLahir());
    }

    @Test
    void testParameterizedConstructorWithNullValues() {
        // When
        NikVerificationRequest request = new NikVerificationRequest(null, null, null);

        // Then
        assertNull(request.getNik());
        assertNull(request.getNamaLengkap());
        assertNull(request.getTanggalLahir());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest();

        // When
        request.setNik("9876543210987654");
        request.setNamaLengkap("Jane Smith");
        request.setTanggalLahir(LocalDate.of(1995, 5, 15));

        // Then
        assertEquals("9876543210987654", request.getNik());
        assertEquals("Jane Smith", request.getNamaLengkap());
        assertEquals(LocalDate.of(1995, 5, 15), request.getTanggalLahir());
    }

    @Test
    void testValidationWithValidData() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    // NIK Validation Tests
    @Test
    void testValidationWithNullNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            null, "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK wajib diisi")));
    }

    @Test
    void testValidationWithEmptyNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK wajib diisi")));
    }

    @Test
    void testValidationWithBlankNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "   ", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK wajib diisi")));
    }

    @Test
    void testValidationWithTooShortNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "12345", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK harus 16 digit")));
    }

    @Test
    void testValidationWithTooLongNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "12345678901234567", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK harus 16 digit")));
    }

    @Test
    void testValidationWithNonNumericNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "abcd567890123456", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK hanya boleh berisi angka")));
    }

    @Test
    void testValidationWithSpecialCharactersInNik() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234-567890-12345", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik") && 
            v.getMessage().equals("NIK hanya boleh berisi angka")));
    }

    // Nama Lengkap Validation Tests
    @Test
    void testValidationWithNullNamaLengkap() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", null, LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("namaLengkap") && 
            v.getMessage().equals("Nama lengkap wajib diisi")));
    }

    @Test
    void testValidationWithEmptyNamaLengkap() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("namaLengkap") && 
            v.getMessage().equals("Nama lengkap wajib diisi")));
    }

    @Test
    void testValidationWithTooShortNamaLengkap() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "A", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("namaLengkap") && 
            v.getMessage().equals("Nama lengkap antara 2-100 karakter")));
    }

    @Test
    void testValidationWithTooLongNamaLengkap() {
        // Given
        String longName = "A".repeat(101); // 101 characters
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", longName, LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("namaLengkap") && 
            v.getMessage().equals("Nama lengkap antara 2-100 karakter")));
    }

    @Test
    void testValidationWithValidNamaLengkapMinLength() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "AB", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationWithValidNamaLengkapMaxLength() {
        // Given
        String maxName = "A".repeat(100); // Exactly 100 characters
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", maxName, LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    // Tanggal Lahir Validation Tests
    @Test
    void testValidationWithNullTanggalLahir() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "John Doe", null);

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("tanggalLahir") && 
            v.getMessage().equals("Tanggal lahir wajib diisi")));
    }

    @Test
    void testValidationWithValidTanggalLahir() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "John Doe", LocalDate.of(1985, 12, 25));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    // toString Method Tests
    @Test
    void testToStringWithValidData() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "John Doe", LocalDate.of(1990, 1, 1));

        // When
        String result = request.toString();

        // Then
        String expected = "NikVerificationRequest{nik='1234567890123456', namaLengkap='John Doe', tanggalLahir=1990-01-01}";
        assertEquals(expected, result);
    }

    @Test
    void testToStringWithNullValues() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(null, null, null);

        // When
        String result = request.toString();

        // Then
        String expected = "NikVerificationRequest{nik='null', namaLengkap='null', tanggalLahir=null}";
        assertEquals(expected, result);
    }

    @Test
    void testSettersIndividually() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest();

        // When
        request.setNik("1111222233334444");
        request.setNamaLengkap("Alice Johnson");
        request.setTanggalLahir(LocalDate.of(1992, 8, 20));

        // Then
        assertEquals("1111222233334444", request.getNik());
        assertEquals("Alice Johnson", request.getNamaLengkap());
        assertEquals(LocalDate.of(1992, 8, 20), request.getTanggalLahir());
    }

    @Test
    void testMultipleValidationErrors() {
        // Given - Request with multiple validation errors
        NikVerificationRequest request = new NikVerificationRequest("", "A", null);

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        // Should have at least 3 violations, but may have more due to multiple constraints per field
        assertTrue(violations.size() >= 3, "Should have at least 3 violations, but got: " + violations.size());
        
        // Check that we have violations for each field
        boolean hasNikViolation = violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("nik"));
        boolean hasNamaViolation = violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("namaLengkap"));
        boolean hasTanggalViolation = violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("tanggalLahir"));
            
        assertTrue(hasNikViolation, "Should have NIK violation");
        assertTrue(hasNamaViolation, "Should have nama violation");
        assertTrue(hasTanggalViolation, "Should have tanggal lahir violation");
    }

    @Test
    void testValidationWithSpecialCharactersInName() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "John Doe Jr.", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // Should be valid
    }

    @Test
    void testValidationWithIndonesianName() {
        // Given
        NikVerificationRequest request = new NikVerificationRequest(
            "1234567890123456", "Siti Nurhaliza Binti Abdullah", LocalDate.of(1990, 1, 1));

        // When
        Set<ConstraintViolation<NikVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // Should be valid
    }
}