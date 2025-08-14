package com.reg.regis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class WaliTest {

    private Wali wali;

    @BeforeEach
    void setUp() {
        wali = new Wali();
    }

    @Test
    void testWaliCreation() {
        assertNotNull(wali);
    }

    @Test
    void testSettersAndGetters() {
        wali.setJenisWali("Orang Tua");
        wali.setNamaLengkapWali("Jane Doe");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Wali No. 456");
        wali.setNomorTeleponWali("081234567891");
        
        assertEquals("Orang Tua", wali.getJenisWali());
        assertEquals("Jane Doe", wali.getNamaLengkapWali());
        assertEquals("PNS", wali.getPekerjaanWali());
        assertEquals("Jl. Wali No. 456", wali.getAlamatWali());
        assertEquals("081234567891", wali.getNomorTeleponWali());
    }

    @Test
    void testIdSetterGetter() {
        wali.setId(1L);
        assertEquals(1L, wali.getId());
    }
}
