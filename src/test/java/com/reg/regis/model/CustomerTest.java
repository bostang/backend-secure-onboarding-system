package com.reg.regis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals(0, customer.getFailedLoginAttempts());
    }

    @Test
    void testSettersAndGetters() {
        customer.setNamaLengkap("John Doe");
        customer.setNik("1234567890123456");
        customer.setEmail("john@test.com");
        customer.setNomorTelepon("081234567890");
        
        assertEquals("John Doe", customer.getNamaLengkap());
        assertEquals("1234567890123456", customer.getNik());
        assertEquals("john@test.com", customer.getEmail());
        assertEquals("081234567890", customer.getNomorTelepon());
    }

    @Test
    void testPrePersist() {
        customer.prePersist();
        
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
        assertEquals("Silver", customer.getJenisKartu());
    }

    @Test
    void testPreUpdate() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        customer.preUpdate();
        
        assertTrue(customer.getUpdatedAt().isAfter(before));
    }

    @Test
    void testIsAccountLocked_NotLocked() {
        assertFalse(customer.isAccountLocked());
    }

    @Test
    void testIsAccountLocked_Locked() {
        customer.setAccountLockedUntil(LocalDateTime.now().plusHours(1));
        assertTrue(customer.isAccountLocked());
    }

    @Test
    void testIsAccountLocked_ExpiredLock() {
        customer.setAccountLockedUntil(LocalDateTime.now().minusHours(1));
        assertFalse(customer.isAccountLocked());
    }

    @Test
    void testRelationships() {
        Alamat alamat = new Alamat();
        Wali wali = new Wali();
        
        customer.setAlamat(alamat);
        customer.setWali(wali);
        
        assertEquals(alamat, customer.getAlamat());
        assertEquals(wali, customer.getWali());
    }

    @Test
    void testGetNamaIbuKandung() {
        // Test getter method
        customer.setNamaIbuKandung("Siti Nurhaliza");
        assertEquals("Siti Nurhaliza", customer.getNamaIbuKandung());
    }

    @Test
    void testSetNamaIbuKandung() {
        // Test setter method
        String namaIbu = "Dewi Sartika";
        customer.setNamaIbuKandung(namaIbu);
        assertEquals(namaIbu, customer.getNamaIbuKandung());
    }

    @Test
    void testNamaIbuKandungNull() {
        // Test dengan nilai null
        customer.setNamaIbuKandung(null);
        assertNull(customer.getNamaIbuKandung());
    }

    @Test
    void testNamaIbuKandungEmpty() {
        // Test dengan string kosong
        customer.setNamaIbuKandung("");
        assertEquals("", customer.getNamaIbuKandung());
    }

    @Test
    void testGetSumberPenghasilanMethod() {
        customer.setSumberPenghasilan("Gaji Pokok");
        assertEquals("Gaji Pokok", customer.getSumberPenghasilan());
    }

    @Test
    void testGetRentangGajiMethod() {
        customer.setRentangGaji("5-10 juta");
        assertEquals("5-10 juta", customer.getRentangGaji());
    }

    @Test
    void testGetTujuanPembuatanRekeningMethod() {
        customer.setTujuanPembuatanRekening("Menabung");
        assertEquals("Menabung", customer.getTujuanPembuatanRekening());
    }

    @Test
    void testSetUpdatedAtMethod() {
        LocalDateTime waktu = LocalDateTime.now();
        customer.setUpdatedAt(waktu);
        assertEquals(waktu, customer.getUpdatedAt());
    }

    @Test
    void testSumberPenghasilanNull() {
        customer.setSumberPenghasilan(null);
        assertNull(customer.getSumberPenghasilan());
    }

    @Test
    void testRentangGajiEmpty() {
        customer.setRentangGaji("");
        assertEquals("", customer.getRentangGaji());
    }

    @Test
    void testTujuanPembuatanRekeningSpaces() {
        customer.setTujuanPembuatanRekening("  Investasi  ");
        assertEquals("  Investasi  ", customer.getTujuanPembuatanRekening());
    }

    @Test
    void testPrePersist_JenisKartuNull() {
        // Test ketika jenisKartu null
        customer.setJenisKartu(null);
        customer.prePersist();
        
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
        assertEquals("Silver", customer.getJenisKartu());
    }

    @Test
    void testPrePersist_JenisKartuEmpty() {
        // Test ketika jenisKartu empty string
        customer.setJenisKartu("");
        customer.prePersist();
        
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
        assertEquals("Silver", customer.getJenisKartu());
    }

    @Test
    void testPrePersist_JenisKartuAlreadySet() {
        // Test ketika jenisKartu sudah ada nilai
        customer.setJenisKartu("Gold");
        customer.prePersist();
        
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
        assertEquals("Gold", customer.getJenisKartu()); // Tidak berubah
    }

    @Test
    void testPrePersist_JenisKartuWhitespace() {
        // Test ketika jenisKartu hanya whitespace
        customer.setJenisKartu("   ");
        customer.prePersist();
        
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
        assertEquals("   ", customer.getJenisKartu()); // Whitespace tidak dianggap empty
    }
}
