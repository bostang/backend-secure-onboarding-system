package com.reg.regis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AlamatTest {

    private Alamat alamat;

    @BeforeEach
    void setUp() {
        alamat = new Alamat();
    }

    @Test
    void testAlamatCreation() {
        assertNotNull(alamat);
    }

    @Test
    void testSettersAndGetters() {
        alamat.setNamaAlamat("Jl. Test No. 123");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta Selatan");
        alamat.setKecamatan("Kebayoran Baru");
        alamat.setKelurahan("Senayan");
        alamat.setKodePos("12190");
        
        assertEquals("Jl. Test No. 123", alamat.getNamaAlamat());
        assertEquals("DKI Jakarta", alamat.getProvinsi());
        assertEquals("Jakarta Selatan", alamat.getKota());
        assertEquals("Kebayoran Baru", alamat.getKecamatan());
        assertEquals("Senayan", alamat.getKelurahan());
        assertEquals("12190", alamat.getKodePos());
    }

    @Test
    void testIdSetterGetter() {
        alamat.setId(1L);
        assertEquals(1L, alamat.getId());
    }
}
