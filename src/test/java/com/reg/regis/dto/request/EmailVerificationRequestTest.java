package com.reg.regis.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

class EmailVerificationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        EmailVerificationRequest request = new EmailVerificationRequest();

        // Then
        assertNull(request.getEmail());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String email = "test@example.com";

        // When
        EmailVerificationRequest request = new EmailVerificationRequest(email);

        // Then
        assertEquals("test@example.com", request.getEmail());
    }

    @Test
    void testParameterizedConstructorWithNull() {
        // When
        EmailVerificationRequest request = new EmailVerificationRequest(null);

        // Then
        assertNull(request.getEmail());
    }

    @Test
    void testGetterAndSetter() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest();

        // When
        request.setEmail("user@domain.com");

        // Then
        assertEquals("user@domain.com", request.getEmail());
    }

    @Test
    void testSetEmailNull() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("test@example.com");

        // When
        request.setEmail(null);

        // Then
        assertNull(request.getEmail());
    }

    @Test
    void testValidationWithValidEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("valid@example.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationWithInvalidEmailFormat() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("invalid-email");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<EmailVerificationRequest> violation = violations.iterator().next();
        assertEquals("Format email tidak valid", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void testValidationWithNullEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest(null);

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<EmailVerificationRequest> violation = violations.iterator().next();
        assertEquals("Email wajib diisi", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void testValidationWithEmptyEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<EmailVerificationRequest> violation = violations.iterator().next();
        assertEquals("Email wajib diisi", violation.getMessage());
    }

    @Test
    void testValidationWithBlankEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("   ");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        // Blank string may trigger both @NotBlank and @Email violations
        assertTrue(violations.size() >= 1);
        
        boolean hasNotBlankViolation = violations.stream().anyMatch(v -> 
            v.getMessage().equals("Email wajib diisi"));
        assertTrue(hasNotBlankViolation);
    }

    @Test
    void testValidationWithInvalidEmailMissingAtSymbol() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("testexample.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<EmailVerificationRequest> violation = violations.iterator().next();
        assertEquals("Format email tidak valid", violation.getMessage());
    }

    @Test
    void testValidationWithInvalidEmailMissingDomain() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("test@");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<EmailVerificationRequest> violation = violations.iterator().next();
        assertEquals("Format email tidak valid", violation.getMessage());
    }

    @Test
    void testValidationWithInvalidEmailMissingLocalPart() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("@example.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<EmailVerificationRequest> violation = violations.iterator().next();
        assertEquals("Format email tidak valid", violation.getMessage());
    }

    @Test
    void testValidationWithMultipleDotsInEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("test.user@example.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // This should be valid
    }

    @Test
    void testValidationWithNumbersInEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("user123@example456.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // This should be valid
    }

    @Test
    void testValidationWithSubdomain() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("user@mail.example.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // This should be valid
    }

    @Test
    void testValidationWithSpecialCharactersInEmail() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("user+tag@example.com");

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // This should be valid
    }

    @Test
    void testSetEmailAfterConstruction() {
        // Given
        EmailVerificationRequest request = new EmailVerificationRequest("old@example.com");

        // When
        request.setEmail("new@example.com");

        // Then
        assertEquals("new@example.com", request.getEmail());
        
        // Validate the new email
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationWithLongEmail() {
        // Given
        String longEmail = "verylongusernamethatmightbeconsideredunusualbutisvalid@verylongdomainthatisalsovalid.com";
        EmailVerificationRequest request = new EmailVerificationRequest(longEmail);

        // When
        Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty()); // This should be valid
    }

    @Test
    void testValidationWithCommonEmailProviders() {
        // Test with common email providers
        String[] validEmails = {
            "user@gmail.com",
            "user@yahoo.com", 
            "user@hotmail.com",
            "user@outlook.com",
            "user@company.co.id"
        };

        for (String email : validEmails) {
            EmailVerificationRequest request = new EmailVerificationRequest(email);
            Set<ConstraintViolation<EmailVerificationRequest>> violations = validator.validate(request);
            
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }
}