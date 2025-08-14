package com.reg.regis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationResponse {
    private String jenisKartu;
    private String namaLengkap;
    private String kodeRekening;
    
    @JsonProperty("jenisTabungan")  // ✅ JSON tetap pakai "jenisTabungan"
    private String tipeAkun;        // ✅ Tapi datanya dari tipeAkun
    
    private String nomorKartuDebitVirtual;

    // Constructor
    public RegistrationResponse(String jenisKartu, String namaLengkap, 
                              String kodeRekening, String tipeAkun,  // ✅ Parameter name harus sama
                              String nomorKartuDebitVirtual) {
        this.jenisKartu = jenisKartu;
        this.namaLengkap = namaLengkap;
        this.kodeRekening = kodeRekening;
        this.tipeAkun = tipeAkun;  // ✅ Assignment harus sama dengan parameter
        this.nomorKartuDebitVirtual = nomorKartuDebitVirtual;
    }

    // Getters
    public String getJenisKartu() { return jenisKartu; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getKodeRekening() { return kodeRekening; }
    public String getTipeAkun() { return tipeAkun; }
    public String getNomorKartuDebitVirtual() { return nomorKartuDebitVirtual; }

    // Setters
    public void setJenisKartu(String jenisKartu) { this.jenisKartu = jenisKartu; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public void setKodeRekening(String kodeRekening) { this.kodeRekening = kodeRekening; }
    public void setTipeAkun(String tipeAkun) { this.tipeAkun = tipeAkun; }
    public void setNomorKartuDebitVirtual(String nomorKartuDebitVirtual) { this.nomorKartuDebitVirtual = nomorKartuDebitVirtual; }
}