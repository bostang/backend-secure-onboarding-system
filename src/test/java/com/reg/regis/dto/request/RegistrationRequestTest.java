package com.reg.regis.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationRequestTest {

    // ======================= jenisKartu =======================
    @Test
    void testSetJenisKartu_nullValue_shouldDefaultToSilver() {
        RegistrationRequest request = new RegistrationRequest();
        request.setJenisKartu(null);
        assertEquals("Silver", request.getJenisKartu());
    }

    @Test
    void testSetJenisKartu_emptyValue_shouldDefaultToSilver() {
        RegistrationRequest request = new RegistrationRequest();
        request.setJenisKartu("");
        assertEquals("Silver", request.getJenisKartu());
    }

    @Test
    void testSetJenisKartu_withValidValue_shouldSetCorrectly() {
        RegistrationRequest request = new RegistrationRequest();
        request.setJenisKartu("Gold");
        assertEquals("Gold", request.getJenisKartu());
    }

    // ======================= WaliRequest.isComplete() =======================
    @Test
    void testWaliRequest_isComplete_shouldReturnTrueWhenAllFieldsSet() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertTrue(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenAnyFieldMissing() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ibu");
        wali.setNamaLengkapWali(" ");
        wali.setPekerjaanWali("Ibu Rumah Tangga");
        wali.setAlamatWali("Surabaya");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    // ======================= jenisKartu Edge Cases =======================
    @Test
    void testSetJenisKartu_whitespaceValue_shouldReturnWhitespace() {
        RegistrationRequest request = new RegistrationRequest();
        request.setJenisKartu("   "); // whitespace only
        assertEquals("   ", request.getJenisKartu()); // Code doesn't trim, so it keeps whitespace
    }

    @Test
    void testSetJenisKartu_validValueWithSpaces_shouldSetCorrectly() {
        RegistrationRequest request = new RegistrationRequest();
        request.setJenisKartu("Platinum");
        assertEquals("Platinum", request.getJenisKartu());
    }

    // ======================= WaliRequest.isComplete() Branch Coverage =======================
    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenJenisWaliNull() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali(null); // null
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenJenisWaliEmpty() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali(""); // empty
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenJenisWaliWhitespace() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("   "); // whitespace only
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenNamaLengkapWaliNull() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali(null); // null
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenNamaLengkapWaliEmpty() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali(""); // empty
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenPekerjaanWaliNull() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali(null); // null
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenPekerjaanWaliEmpty() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali(""); // empty
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenPekerjaanWaliWhitespace() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("   "); // whitespace only
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenAlamatWaliNull() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali(null); // null
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenAlamatWaliEmpty() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali(""); // empty
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenAlamatWaliWhitespace() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("   "); // whitespace only
        wali.setNomorTeleponWali("081234567890");

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenNomorTeleponWaliNull() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali(null); // null

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenNomorTeleponWaliEmpty() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali(""); // empty

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenNomorTeleponWaliWhitespace() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("   "); // whitespace only

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenAllFieldsNull() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        // All fields are null by default

        assertFalse(wali.isComplete());
    }

    @Test
    void testWaliRequest_isComplete_shouldReturnFalseWhenAllFieldsEmpty() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("");
        wali.setNamaLengkapWali("");
        wali.setPekerjaanWali("");
        wali.setAlamatWali("");
        wali.setNomorTeleponWali("");

        assertFalse(wali.isComplete());
    }

    // ======================= Constructor and Basic Getters/Setters =======================
    @Test
    void testRegistrationRequest_defaultConstructor() {
        RegistrationRequest request = new RegistrationRequest();
        assertNotNull(request);
        assertNull(request.getNamaLengkap());
        assertNull(request.getJenisKartu());
    }

    @Test
    void testAlamatRequest_basicGettersSetters() {
        RegistrationRequest.AlamatRequest alamat = new RegistrationRequest.AlamatRequest();
        alamat.setNamaAlamat("Jl. Test");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta");
        alamat.setKecamatan("Test");
        alamat.setKelurahan("Test");
        alamat.setKodePos("12345");

        assertEquals("Jl. Test", alamat.getNamaAlamat());
        assertEquals("DKI Jakarta", alamat.getProvinsi());
        assertEquals("Jakarta", alamat.getKota());
        assertEquals("Test", alamat.getKecamatan());
        assertEquals("Test", alamat.getKelurahan());
        assertEquals("12345", alamat.getKodePos());
    }

    @Test
    void testWaliRequest_basicGettersSetters() {
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Budi Santoso");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka 10");
        wali.setNomorTeleponWali("081234567890");

        assertEquals("Ayah", wali.getJenisWali());
        assertEquals("Budi Santoso", wali.getNamaLengkapWali());
        assertEquals("PNS", wali.getPekerjaanWali());
        assertEquals("Jl. Merdeka 10", wali.getAlamatWali());
        assertEquals("081234567890", wali.getNomorTeleponWali());
    }

    // ======================= nomorKartuDebitVirtual =======================
    @Test
    void testGetNomorKartuDebitVirtual_defaultValue_shouldReturnNull() {
        RegistrationRequest request = new RegistrationRequest();
        assertNull(request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_withValidValue_shouldSetCorrectly() {
        RegistrationRequest request = new RegistrationRequest();
        String nomorKartu = "1234567890123456";
        request.setNomorKartuDebitVirtual(nomorKartu);
        assertEquals(nomorKartu, request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_withNull_shouldSetNull() {
        RegistrationRequest request = new RegistrationRequest();
        request.setNomorKartuDebitVirtual(null);
        assertNull(request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_withEmptyString_shouldSetEmptyString() {
        RegistrationRequest request = new RegistrationRequest();
        request.setNomorKartuDebitVirtual("");
        assertEquals("", request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_withWhitespace_shouldSetWhitespace() {
        RegistrationRequest request = new RegistrationRequest();
        String whitespaceValue = "   ";
        request.setNomorKartuDebitVirtual(whitespaceValue);
        assertEquals(whitespaceValue, request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_withSpecialCharacters_shouldSetCorrectly() {
        RegistrationRequest request = new RegistrationRequest();
        String specialValue = "1234-5678-9012-3456";
        request.setNomorKartuDebitVirtual(specialValue);
        assertEquals(specialValue, request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_overwritePreviousValue_shouldUpdateCorrectly() {
        RegistrationRequest request = new RegistrationRequest();
        String firstValue = "1111111111111111";
        String secondValue = "2222222222222222";
        
        request.setNomorKartuDebitVirtual(firstValue);
        assertEquals(firstValue, request.getNomorKartuDebitVirtual());
        
        request.setNomorKartuDebitVirtual(secondValue);
        assertEquals(secondValue, request.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtual_withLongString_shouldSetCorrectly() {
        RegistrationRequest request = new RegistrationRequest();
        String longValue = "12345678901234567890123456789012345678901234567890";
        request.setNomorKartuDebitVirtual(longValue);
        assertEquals(longValue, request.getNomorKartuDebitVirtual());
    }
}
