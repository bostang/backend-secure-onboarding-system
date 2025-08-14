package com.reg.regis.dto.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DukcapilRequestDtoTest {

    @Test
    void testDefaultConstructor() {
        // When
        DukcapilRequestDto dto = new DukcapilRequestDto();

        // Then
        assertNull(dto.getNik());
        assertNull(dto.getNamaLengkap());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";

        // When
        DukcapilRequestDto dto = new DukcapilRequestDto(nik, namaLengkap);

        // Then
        assertEquals("1234567890123456", dto.getNik());
        assertEquals("John Doe", dto.getNamaLengkap());
    }

    @Test
    void testParameterizedConstructorWithNullValues() {
        // Given
        String nik = null;
        String namaLengkap = null;

        // When
        DukcapilRequestDto dto = new DukcapilRequestDto(nik, namaLengkap);

        // Then
        assertNull(dto.getNik());
        assertNull(dto.getNamaLengkap());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto();

        // When
        dto.setNik("9876543210987654");
        dto.setNamaLengkap("Jane Smith");

        // Then
        assertEquals("9876543210987654", dto.getNik());
        assertEquals("Jane Smith", dto.getNamaLengkap());
    }

    @Test
    void testSetNik() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto();

        // When
        dto.setNik("1111222233334444");

        // Then
        assertEquals("1111222233334444", dto.getNik());
    }

    @Test
    void testSetNikNull() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto("1234567890123456", "Test");

        // When
        dto.setNik(null);

        // Then
        assertNull(dto.getNik());
    }

    @Test
    void testSetNamaLengkap() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto();

        // When
        dto.setNamaLengkap("Alice Johnson");

        // Then
        assertEquals("Alice Johnson", dto.getNamaLengkap());
    }

    @Test
    void testSetNamaLengkapNull() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto("1234567890123456", "Original Name");

        // When
        dto.setNamaLengkap(null);

        // Then
        assertNull(dto.getNamaLengkap());
    }

    @Test
    void testToStringWithValues() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        DukcapilRequestDto dto = new DukcapilRequestDto(nik, namaLengkap);

        // When
        String result = dto.toString();

        // Then
        String expected = "DukcapilRequestDto{nik='1234567890123456', namaLengkap='John Doe'}";
        assertEquals(expected, result);
    }

    @Test
    void testToStringWithNullValues() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto(null, null);

        // When
        String result = dto.toString();

        // Then
        String expected = "DukcapilRequestDto{nik='null', namaLengkap='null'}";
        assertEquals(expected, result);
    }

    @Test
    void testToStringWithEmptyValues() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto("", "");

        // When
        String result = dto.toString();

        // Then
        String expected = "DukcapilRequestDto{nik='', namaLengkap=''}";
        assertEquals(expected, result);
    }

    @Test
    void testToStringAfterSetters() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto();
        dto.setNik("9999888877776666");
        dto.setNamaLengkap("Bob Wilson");

        // When
        String result = dto.toString();

        // Then
        String expected = "DukcapilRequestDto{nik='9999888877776666', namaLengkap='Bob Wilson'}";
        assertEquals(expected, result);
    }

    @Test
    void testEmptyStringValues() {
        // Given
        String nik = "";
        String namaLengkap = "";

        // When
        DukcapilRequestDto dto = new DukcapilRequestDto(nik, namaLengkap);

        // Then
        assertEquals("", dto.getNik());
        assertEquals("", dto.getNamaLengkap());
    }

    @Test
    void testWhitespaceValues() {
        // Given
        String nik = "   ";
        String namaLengkap = "   ";

        // When
        DukcapilRequestDto dto = new DukcapilRequestDto(nik, namaLengkap);

        // Then
        assertEquals("   ", dto.getNik());
        assertEquals("   ", dto.getNamaLengkap());
    }

    @Test
    void testMultipleSettersChaining() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto("old", "old name");

        // When - multiple setter calls
        dto.setNik("new123456789");
        dto.setNamaLengkap("New Name");

        // Then
        assertEquals("new123456789", dto.getNik());
        assertEquals("New Name", dto.getNamaLengkap());
    }

    @Test
    void testLongNikValue() {
        // Given
        String longNik = "1234567890123456789012345"; // Very long NIK
        DukcapilRequestDto dto = new DukcapilRequestDto();

        // When
        dto.setNik(longNik);

        // Then
        assertEquals(longNik, dto.getNik());
    }

    @Test
    void testSpecialCharactersInName() {
        // Given
        String namaLengkap = "John Doe Jr. - Smith & Co.";
        DukcapilRequestDto dto = new DukcapilRequestDto();

        // When
        dto.setNamaLengkap(namaLengkap);

        // Then
        assertEquals("John Doe Jr. - Smith & Co.", dto.getNamaLengkap());
    }

    @Test
    void testConstructorAndSettersCombination() {
        // Given
        DukcapilRequestDto dto = new DukcapilRequestDto("initial123", "Initial Name");

        // When - modify using setters
        dto.setNik("modified456");
        dto.setNamaLengkap("Modified Name");

        // Then
        assertEquals("modified456", dto.getNik());
        assertEquals("Modified Name", dto.getNamaLengkap());
        
        // Test toString after modification
        String expected = "DukcapilRequestDto{nik='modified456', namaLengkap='Modified Name'}";
        assertEquals(expected, dto.toString());
    }
}