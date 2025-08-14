package com.reg.regis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "wali")
public class Wali {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "jenis_wali", nullable = false)
    private String jenisWali;
    
    @NotBlank
    @Column(name = "nama_lengkap_wali", nullable = false)
    private String namaLengkapWali;
    
    @NotBlank
    @Column(name = "pekerjaan_wali", nullable = false)
    private String pekerjaanWali;
    
    @NotBlank
    @Column(name = "alamat_wali", nullable = false)
    private String alamatWali;
    
    @NotBlank
    @Pattern(regexp = "^08[0-9]{8,11}$")
    @Column(name = "nomor_telepon_wali", nullable = false)
    private String nomorTeleponWali;
    
    @OneToOne(mappedBy = "wali")
    private Customer customer;
    
    // Constructors
    public Wali() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getJenisWali() { return jenisWali; }
    public void setJenisWali(String jenisWali) { this.jenisWali = jenisWali; }
    
    public String getNamaLengkapWali() { return namaLengkapWali; }
    public void setNamaLengkapWali(String namaLengkapWali) { this.namaLengkapWali = namaLengkapWali; }
    
    public String getPekerjaanWali() { return pekerjaanWali; }
    public void setPekerjaanWali(String pekerjaanWali) { this.pekerjaanWali = pekerjaanWali; }
    
    public String getAlamatWali() { return alamatWali; }
    public void setAlamatWali(String alamatWali) { this.alamatWali = alamatWali; }
    
    public String getNomorTeleponWali() { return nomorTeleponWali; }
    public void setNomorTeleponWali(String nomorTeleponWali) { this.nomorTeleponWali = nomorTeleponWali; }
}