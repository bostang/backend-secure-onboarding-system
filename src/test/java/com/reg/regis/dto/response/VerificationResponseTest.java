package com.reg.regis.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class VerificationResponseTest {

    @Test
    void testConstructorWithTwoParameters() {
        // Given
        boolean valid = true;
        String message = "Verification successful";

        // When
        VerificationResponse response = new VerificationResponse(valid, message);

        // Then
        assertTrue(response.isValid());
        assertEquals("Verification successful", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testConstructorWithThreeParameters() {
        // Given
        boolean valid = false;
        String message = "Verification failed";
        Map<String, Object> data = new HashMap<>();
        data.put("errorCode", "ERR001");
        data.put("details", "Invalid format");

        // When
        VerificationResponse response = new VerificationResponse(valid, message, data);

        // Then
        assertFalse(response.isValid());
        assertEquals("Verification failed", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(data, response.getData());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Initial message");

        // Test setters and getters
        response.setValid(false);
        response.setMessage("Updated message");
        response.setData("Some data");

        // Then
        assertFalse(response.isValid());
        assertEquals("Updated message", response.getMessage());
        assertEquals("Some data", response.getData());
    }

    @Test
    void testSetValidTrue() {
        // Given
        VerificationResponse response = new VerificationResponse(false, "Test");

        // When
        response.setValid(true);

        // Then
        assertTrue(response.isValid());
    }

    @Test
    void testSetValidFalse() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Test");

        // When
        response.setValid(false);

        // Then
        assertFalse(response.isValid());
    }

    @Test
    void testSetMessage() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Original");

        // When
        response.setMessage("New message");

        // Then
        assertEquals("New message", response.getMessage());
    }

    @Test
    void testSetMessageNull() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Original");

        // When
        response.setMessage(null);

        // Then
        assertNull(response.getMessage());
    }

    @Test
    void testSetData() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Test");

        // When
        Object testData = "Test data";
        response.setData(testData);

        // Then
        assertEquals("Test data", response.getData());
    }

    @Test
    void testSetDataNull() {
        // Given
        VerificationResponse response = new VerificationResponse(true, "Test", "Initial data");

        // When
        response.setData(null);

        // Then
        assertNull(response.getData());
    }

    @Test
    void testDataWithDifferentTypes() {
        // Test with different data types
        VerificationResponse response = new VerificationResponse(true, "Test");

        // Test with String
        response.setData("String data");
        assertEquals("String data", response.getData());

        // Test with Integer
        response.setData(123);
        assertEquals(123, response.getData());

        // Test with Map
        Map<String, String> mapData = new HashMap<>();
        mapData.put("key", "value");
        response.setData(mapData);
        assertEquals(mapData, response.getData());
    }

    @Test
    void testConstructorWithNullMessage() {
        // Given
        boolean valid = true;
        String message = null;

        // When
        VerificationResponse response = new VerificationResponse(valid, message);

        // Then
        assertTrue(response.isValid());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testConstructorWithNullData() {
        // Given
        boolean valid = true;
        String message = "Test message";
        Object data = null;

        // When
        VerificationResponse response = new VerificationResponse(valid, message, data);

        // Then
        assertTrue(response.isValid());
        assertEquals("Test message", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testMultipleSettersChaining() {
        // Given
        VerificationResponse response = new VerificationResponse(false, "Initial");

        // When - multiple setter calls
        response.setValid(true);
        response.setMessage("Updated");
        response.setData("New data");

        // Then
        assertTrue(response.isValid());
        assertEquals("Updated", response.getMessage());
        assertEquals("New data", response.getData());
    }
}