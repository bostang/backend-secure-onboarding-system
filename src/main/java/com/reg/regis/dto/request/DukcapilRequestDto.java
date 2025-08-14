package com.reg.regis.dto.request;

public class DukcapilRequestDto {
    
    private String nik;
    private String namaLengkap;
    
    // Constructors
    public DukcapilRequestDto() {}
    
    public DukcapilRequestDto(String nik, String namaLengkap) {
        this.nik = nik;
        this.namaLengkap = namaLengkap;
    }
    
    // Getters and Setters
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    @Override
    public String toString() {
        return "DukcapilRequestDto{" +
                "nik='" + nik + '\'' +
                ", namaLengkap='" + namaLengkap + '\'' +
                '}';
    }
}