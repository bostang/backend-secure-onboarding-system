package com.reg.regis.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationResponseTest {

    @Test
    void testConstructorWithAllParameters() {
        // Given
        String jenisKartu = "Gold";
        String namaLengkap = "John Doe";
        String kodeRekening = "1234567890";
        String tipeAkun = "Tabungan";
        String nomorKartuDebitVirtual = "4102 1234 5678 9012";

        // When
        RegistrationResponse response = new RegistrationResponse(
            jenisKartu, namaLengkap, kodeRekening, tipeAkun, nomorKartuDebitVirtual);

        // Then
        assertEquals("Gold", response.getJenisKartu());
        assertEquals("John Doe", response.getNamaLengkap());
        assertEquals("1234567890", response.getKodeRekening());
        assertEquals("Tabungan", response.getTipeAkun());
        assertEquals("4102 1234 5678 9012", response.getNomorKartuDebitVirtual());
    }

    @Test
    void testConstructorWithNullValues() {
        // Given
        String jenisKartu = null;
        String namaLengkap = null;
        String kodeRekening = null;
        String tipeAkun = null;
        String nomorKartuDebitVirtual = null;

        // When
        RegistrationResponse response = new RegistrationResponse(
            jenisKartu, namaLengkap, kodeRekening, tipeAkun, nomorKartuDebitVirtual);

        // Then
        assertNull(response.getJenisKartu());
        assertNull(response.getNamaLengkap());
        assertNull(response.getKodeRekening());
        assertNull(response.getTipeAkun());
        assertNull(response.getNomorKartuDebitVirtual());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Jane Doe", "0987654321", "Giro", "4101 9876 5432 1098");

        // Test setters
        response.setJenisKartu("Platinum");
        response.setNamaLengkap("Jane Smith");
        response.setKodeRekening("1111222233");
        response.setTipeAkun("Deposito");
        response.setNomorKartuDebitVirtual("4103 1111 2222 3333");

        // Then - test getters
        assertEquals("Platinum", response.getJenisKartu());
        assertEquals("Jane Smith", response.getNamaLengkap());
        assertEquals("1111222233", response.getKodeRekening());
        assertEquals("Deposito", response.getTipeAkun());
        assertEquals("4103 1111 2222 3333", response.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetJenisKartu() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setJenisKartu("Gold");

        // Then
        assertEquals("Gold", response.getJenisKartu());
    }

    @Test
    void testSetJenisKartuNull() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setJenisKartu(null);

        // Then
        assertNull(response.getJenisKartu());
    }

    @Test
    void testSetNamaLengkap() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Old Name", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setNamaLengkap("New Name");

        // Then
        assertEquals("New Name", response.getNamaLengkap());
    }

    @Test
    void testSetNamaLengkapNull() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Old Name", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setNamaLengkap(null);

        // Then
        assertNull(response.getNamaLengkap());
    }

    @Test
    void testSetKodeRekening() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setKodeRekening("999888777");

        // Then
        assertEquals("999888777", response.getKodeRekening());
    }

    @Test
    void testSetKodeRekeningNull() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setKodeRekening(null);

        // Then
        assertNull(response.getKodeRekening());
    }

    @Test
    void testSetTipeAkun() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setTipeAkun("Giro");

        // Then
        assertEquals("Giro", response.getTipeAkun());
    }

    @Test
    void testSetTipeAkunNull() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setTipeAkun(null);

        // Then
        assertNull(response.getTipeAkun());
    }

    @Test
    void testSetNomorKartuDebitVirtual() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setNomorKartuDebitVirtual("4102 9999 8888 7777");

        // Then
        assertEquals("4102 9999 8888 7777", response.getNomorKartuDebitVirtual());
    }

    @Test
    void testSetNomorKartuDebitVirtualNull() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Test", "123", "Tabungan", "4101 1234 5678 9012");

        // When
        response.setNomorKartuDebitVirtual(null);

        // Then
        assertNull(response.getNomorKartuDebitVirtual());
    }

    @Test
    void testMultipleSettersChaining() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "Silver", "Original", "000", "Tabungan", "4101 0000 0000 0000");

        // When - multiple setter calls
        response.setJenisKartu("Platinum");
        response.setNamaLengkap("Updated Name");
        response.setKodeRekening("111222333");
        response.setTipeAkun("Deposito");
        response.setNomorKartuDebitVirtual("4103 1111 2222 3333");

        // Then
        assertEquals("Platinum", response.getJenisKartu());
        assertEquals("Updated Name", response.getNamaLengkap());
        assertEquals("111222333", response.getKodeRekening());
        assertEquals("Deposito", response.getTipeAkun());
        assertEquals("4103 1111 2222 3333", response.getNomorKartuDebitVirtual());
    }

    @Test
    void testDifferentCardTypes() {
        // Test different jenis kartu values
        String[] cardTypes = {"Silver", "Gold", "Platinum", "Batik Air", "GPN"};
        
        for (String cardType : cardTypes) {
            RegistrationResponse response = new RegistrationResponse(
                cardType, "Test User", "123456", "Tabungan", "4101 1234 5678 9012");
            
            assertEquals(cardType, response.getJenisKartu());
        }
    }

    @Test
    void testDifferentAccountTypes() {
        // Test different tipe akun values
        String[] accountTypes = {"Tabungan", "Giro", "Deposito"};
        
        for (String accountType : accountTypes) {
            RegistrationResponse response = new RegistrationResponse(
                "Silver", "Test User", "123456", accountType, "4101 1234 5678 9012");
            
            assertEquals(accountType, response.getTipeAkun());
        }
    }

    @Test
    void testEmptyStringValues() {
        // Given
        RegistrationResponse response = new RegistrationResponse(
            "", "", "", "", "");

        // Then
        assertEquals("", response.getJenisKartu());
        assertEquals("", response.getNamaLengkap());
        assertEquals("", response.getKodeRekening());
        assertEquals("", response.getTipeAkun());
        assertEquals("", response.getNomorKartuDebitVirtual());
    }
}