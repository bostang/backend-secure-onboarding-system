package com.reg.regis.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

class PhoneVerificationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        PhoneVerificationRequest request = new PhoneVerificationRequest();

        // Then
        assertNull(request.getNomorTelepon());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String nomorTelepon = "081234567890";

        // When
        PhoneVerificationRequest request = new PhoneVerificationRequest(nomorTelepon);

        // Then
        assertEquals("081234567890", request.getNomorTelepon());
    }

    @Test
    void testParameterizedConstructorWithNull() {
        // When
        PhoneVerificationRequest request = new PhoneVerificationRequest(null);

        // Then
        assertNull(request.getNomorTelepon());
    }

    @Test
    void testGetterAndSetter() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest();

        // When
        request.setNomorTelepon("085987654321");

        // Then
        assertEquals("085987654321", request.getNomorTelepon());
    }

    @Test
    void testSetNomorTeleponNull() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest("081234567890");

        // When
        request.setNomorTelepon(null);

        // Then
        assertNull(request.getNomorTelepon());
    }

    @Test
    void testValidationWithValidPhoneNumber() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest("081234567890");

        // When
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    // @NotBlank Validation Tests
    @Test
    void testValidationWithNullPhoneNumber() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest(null);

        // When
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<PhoneVerificationRequest> violation = violations.iterator().next();
        assertEquals("Nomor telepon wajib diisi", violation.getMessage());
        assertEquals("nomorTelepon", violation.getPropertyPath().toString());
    }

    @Test
    void testValidationWithEmptyPhoneNumber() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest("");

        // When
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        // Empty string triggers both @NotBlank and @Pattern violations
        assertTrue(violations.size() >= 1);
        
        boolean hasNotBlankViolation = violations.stream().anyMatch(v -> 
            v.getMessage().equals("Nomor telepon wajib diisi"));
        assertTrue(hasNotBlankViolation);
    }

    @Test
    void testValidationWithBlankPhoneNumber() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest("   ");

        // When
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        // Blank string triggers both @NotBlank and @Pattern violations
        assertTrue(violations.size() >= 1);
        
        boolean hasNotBlankViolation = violations.stream().anyMatch(v -> 
            v.getMessage().equals("Nomor telepon wajib diisi"));
        assertTrue(hasNotBlankViolation);
    }

    // @Pattern Validation Tests - Valid Cases
    @Test
    void testValidationWithValidTelkomselNumber() {
        // Given - Telkomsel prefix 0811, 0812, 0813, 0821, 0822, 0823, 0852, 0853
        String[] validNumbers = {
            "08111234567", "081123456789", "0811234567890", // Max 13 digits
            "08121234567", "082112345678", "085212345678", "085312345678"
        };

        for (String phoneNumber : validNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertTrue(violations.isEmpty(), "Phone number should be valid: " + phoneNumber);
        }
    }

    @Test
    void testValidationWithValidXLNumber() {
        // Given - XL prefix 0817, 0818, 0819, 0859, 0877, 0878
        String[] validNumbers = {
            "08171234567", "081812345678", "081912345678", 
            "085912345678", "087712345678", "087812345678"
        };

        for (String phoneNumber : validNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertTrue(violations.isEmpty(), "Phone number should be valid: " + phoneNumber);
        }
    }

    @Test
    void testValidationWithValidIndosatNumber() {
        // Given - Indosat prefix 0814, 0815, 0816, 0855, 0856, 0857, 0858
        String[] validNumbers = {
            "08141234567", "081512345678", "081612345678",
            "085512345678", "085612345678", "085712345678", "085812345678"
        };

        for (String phoneNumber : validNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertTrue(violations.isEmpty(), "Phone number should be valid: " + phoneNumber);
        }
    }

    @Test
    void testValidationWithValidSmartfrenNumber() {
        // Given - Smartfren prefix 0881, 0882, 0883, 0884, 0885, 0886, 0887, 0888
        String[] validNumbers = {
            "08811234567", "088212345678", "088312345678",
            "088412345678", "088512345678", "088612345678", "088712345678", "088812345678"
        };

        for (String phoneNumber : validNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertTrue(violations.isEmpty(), "Phone number should be valid: " + phoneNumber);
        }
    }

    @Test
    void testValidationWithMinLength() {
        // Given - Minimum length: 08 + 8 digits = 10 total
        PhoneVerificationRequest request = new PhoneVerificationRequest("0812345678");

        // When
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationWithMaxLength() {
        // Given - Maximum length: 08 + 11 digits = 13 total
        PhoneVerificationRequest request = new PhoneVerificationRequest("0812345678901");

        // When
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    // @Pattern Validation Tests - Invalid Cases
    @Test
    void testValidationWithInvalidPrefix() {
        // Given - Not starting with 08
        String[] invalidNumbers = {
            "071234567890", // starts with 07
            "091234567890", // starts with 09
            "621234567890", // starts with +62 format without +
            "81234567890"   // missing leading 0
        };

        for (String phoneNumber : invalidNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertFalse(violations.isEmpty(), "Phone number should be invalid: " + phoneNumber);
            assertTrue(violations.stream().anyMatch(v -> 
                v.getMessage().equals("Format nomor telepon tidak valid (contoh: 081234567890)")));
        }
    }

    @Test
    void testValidationWithTooShortNumber() {
        // Given - Less than minimum length (08 + 8 digits)
        String[] tooShortNumbers = {
            "081234567",   // 9 digits total (too short)
            "08123456",    // 8 digits total (too short)
            "0812345"      // 7 digits total (too short)
        };

        for (String phoneNumber : tooShortNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertFalse(violations.isEmpty(), "Phone number should be invalid (too short): " + phoneNumber);
            assertTrue(violations.stream().anyMatch(v -> 
                v.getMessage().equals("Format nomor telepon tidak valid (contoh: 081234567890)")));
        }
    }

    @Test
    void testValidationWithTooLongNumber() {
        // Given - More than maximum length (08 + 11 digits)
        String[] tooLongNumbers = {
            "0812345678901234", // 16 digits total (too long)
            "08123456789012345" // 17 digits total (too long)
        };

        for (String phoneNumber : tooLongNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertFalse(violations.isEmpty(), "Phone number should be invalid (too long): " + phoneNumber);
            assertTrue(violations.stream().anyMatch(v -> 
                v.getMessage().equals("Format nomor telepon tidak valid (contoh: 081234567890)")));
        }
    }

    @Test
    void testValidationWithNonNumericCharacters() {
        // Given - Contains non-numeric characters
        String[] invalidNumbers = {
            "081abc567890",     // contains letters
            "081-234-567890",   // contains hyphens
            "081 234 567890",   // contains spaces
            "081.234.567890",   // contains dots
            "081+234567890",    // contains plus sign
            "081(234)567890"    // contains parentheses
        };

        for (String phoneNumber : invalidNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertFalse(violations.isEmpty(), "Phone number should be invalid (non-numeric): " + phoneNumber);
            assertTrue(violations.stream().anyMatch(v -> 
                v.getMessage().equals("Format nomor telepon tidak valid (contoh: 081234567890)")));
        }
    }

    @Test
    void testValidationWithInternationalFormat() {
        // Given - International format (+62)
        String[] internationalNumbers = {
            "+6281234567890",
            "6281234567890",
            "0062812345678"
        };

        for (String phoneNumber : internationalNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            assertFalse(violations.isEmpty(), "International format should be invalid: " + phoneNumber);
            assertTrue(violations.stream().anyMatch(v -> 
                v.getMessage().equals("Format nomor telepon tidak valid (contoh: 081234567890)")));
        }
    }

    @Test
    void testValidationWithInvalidThirdDigit() {
        // Given - Invalid third digit (not valid operator codes)
        String[] invalidNumbers = {
            "080123456789", // 080 is not valid
            "089123456789", // 089 is not valid  
            "087012345678", // 0870 is not common prefix
            "085012345678"  // 0850 is not common prefix
        };

        for (String phoneNumber : invalidNumbers) {
            PhoneVerificationRequest request = new PhoneVerificationRequest(phoneNumber);
            Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
            
            // Note: The current regex ^08[0-9]{8,11}$ actually allows any digit after 08
            // So these would be valid according to the current pattern
            // If you want stricter validation, the pattern needs to be updated
            assertTrue(violations.isEmpty(), "Current pattern allows any digit after 08: " + phoneNumber);
        }
    }

    @Test
    void testSetNomorTeleponAfterConstruction() {
        // Given
        PhoneVerificationRequest request = new PhoneVerificationRequest("081111111111");

        // When
        request.setNomorTelepon("085222222222");

        // Then
        assertEquals("085222222222", request.getNomorTelepon());
        
        // Validate the new phone number
        Set<ConstraintViolation<PhoneVerificationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationWithEdgeCaseLengths() {
        // Test exactly at boundaries
        PhoneVerificationRequest minRequest = new PhoneVerificationRequest("0812345678"); // 10 digits (min)
        PhoneVerificationRequest maxRequest = new PhoneVerificationRequest("0812345678901"); // 13 digits (max)

        Set<ConstraintViolation<PhoneVerificationRequest>> minViolations = validator.validate(minRequest);
        Set<ConstraintViolation<PhoneVerificationRequest>> maxViolations = validator.validate(maxRequest);

        assertTrue(minViolations.isEmpty(), "Minimum length should be valid");
        assertTrue(maxViolations.isEmpty(), "Maximum length should be valid");
    }
}