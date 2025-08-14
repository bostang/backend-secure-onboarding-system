package com.reg.regis.dto.response;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DukcapilResponseDtoTest {

    @Test
    void testAllFieldsAndToString() {
        DukcapilResponseDto dto = new DukcapilResponseDto();

        // Set values
        dto.setValid(true);
        dto.setMessage("Success");

        Map<String, Object> mockData = new HashMap<>();
        mockData.put("name", "John Doe");
        mockData.put("nik", "1234567890123456");
        dto.setData(mockData);

        dto.setTimestamp("2025-07-25T14:00:00Z");
        dto.setService("dukcapil-check");

        // Assertions
        assertTrue(dto.isValid());
        assertEquals("Success", dto.getMessage());
        assertEquals(mockData, dto.getData());
        assertEquals("2025-07-25T14:00:00Z", dto.getTimestamp());
        assertEquals("dukcapil-check", dto.getService());

        // toString check (optional, for coverage)
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("valid=true"));
        assertTrue(toString.contains("message='Success'"));
        assertTrue(toString.contains("name=John Doe"));
    }

    @Test
    void testConstructorWithValidAndMessage() {
        DukcapilResponseDto dto = new DukcapilResponseDto(true, "Verified");

        assertTrue(dto.isValid());
        assertEquals("Verified", dto.getMessage());
    }
}
