package com.reg.regis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "alamat")
public class Alamat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "nama_alamat", nullable = false)
    private String namaAlamat;
    
    @NotBlank
    @Column(nullable = false)
    private String provinsi;
    
    @NotBlank
    @Column(nullable = false)
    private String kota;
    
    @NotBlank
    @Column(nullable = false)
    private String kecamatan;
    
    @NotBlank
    @Column(nullable = false)
    private String kelurahan;
    
    @NotBlank
    @Column(name = "kode_pos", nullable = false)
    private String kodePos;
    
    @OneToOne(mappedBy = "alamat")
    private Customer customer;
    
    // Constructors
    public Alamat() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNamaAlamat() { return namaAlamat; }
    public void setNamaAlamat(String namaAlamat) { this.namaAlamat = namaAlamat; }
    
    public String getProvinsi() { return provinsi; }
    public void setProvinsi(String provinsi) { this.provinsi = provinsi; }
    
    public String getKota() { return kota; }
    public void setKota(String kota) { this.kota = kota; }
    
    public String getKecamatan() { return kecamatan; }
    public void setKecamatan(String kecamatan) { this.kecamatan = kecamatan; }
    
    public String getKelurahan() { return kelurahan; }
    public void setKelurahan(String kelurahan) { this.kelurahan = kelurahan; }
    
    public String getKodePos() { return kodePos; }
    public void setKodePos(String kodePos) { this.kodePos = kodePos; }
}
