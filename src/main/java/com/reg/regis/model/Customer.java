package com.reg.regis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama lengkap wajib diisi")
    @Column(name = "nama_lengkap", nullable = false)
    private String namaLengkap;
    
    @NotBlank(message = "NIK wajib diisi")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    @Pattern(regexp = "^[0-9]{16}$", message = "NIK hanya boleh berisi angka")
    @Column(name = "nik", nullable = false, unique = true, length = 16)
    private String nik;
    
    @NotBlank(message = "Nama ibu kandung wajib diisi")
    @Column(name = "nama_ibu_kandung", nullable = false)
    private String namaIbuKandung;
    
    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(regexp = "^08[0-9]{8,11}$", message = "Format nomor telepon tidak valid")
    @Column(name = "nomor_telepon", nullable = false, unique = true)
    private String nomorTelepon;
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Password wajib diisi")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "Tipe akun wajib diisi")
    @Column(name = "tipe_akun", nullable = false)
    private String tipeAkun;
    
    @NotBlank(message = "Jenis kartu wajib diisi")
    @Column(name = "jenis_kartu", nullable = false)
    private String jenisKartu;
    
    @NotBlank(message = "Tempat lahir wajib diisi")
    @Column(name = "tempat_lahir", nullable = false)
    private String tempatLahir;
    
    @NotNull(message = "Tanggal lahir wajib diisi")
    @Column(name = "tanggal_lahir", nullable = false)
    private LocalDate tanggalLahir;
    
    @NotBlank(message = "Jenis kelamin wajib diisi")
    @Column(name = "jenis_kelamin", nullable = false)
    private String jenisKelamin;
    
    @NotBlank(message = "Agama wajib diisi")
    @Column(nullable = false)
    private String agama;
    
    @NotBlank(message = "Status pernikahan wajib diisi")
    @Column(name = "status_pernikahan", nullable = false)
    private String statusPernikahan;
    
    @NotBlank(message = "Pekerjaan wajib diisi")
    @Column(nullable = false)
    private String pekerjaan;
    
    @NotBlank(message = "Sumber penghasilan wajib diisi")
    @Column(name = "sumber_penghasilan", nullable = false)
    private String sumberPenghasilan;
    
    @NotBlank(message = "Rentang gaji wajib diisi")
    @Column(name = "rentang_gaji", nullable = false)
    private String rentangGaji;
    
    @NotBlank(message = "Tujuan pembuatan rekening wajib diisi")
    @Column(name = "tujuan_pembuatan_rekening", nullable = false)
    private String tujuanPembuatanRekening;
    
    @Column(name = "kode_rekening")
    private Integer kodeRekening;

    @Column(name = "nomor_kartu_debit_virtual", unique = true)
    private String nomorKartuDebitVirtual;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // FAILED LOGIN ATTEMPTS
    @JsonIgnore
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0; // Default ke 0

    // ACCOUNT LOCKED UNTIL
    @JsonIgnore
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil; // Kapan akun akan dibuka

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "alamat_id")
    private Alamat alamat;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "wali_id", nullable = true)
    private Wali wali;
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (jenisKartu == null || jenisKartu.isEmpty()) {
            jenisKartu = "Silver";  // Default value
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Customer() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    
    public String getNamaIbuKandung() { return namaIbuKandung; }
    public void setNamaIbuKandung(String namaIbuKandung) { this.namaIbuKandung = namaIbuKandung; }
    
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTipeAkun() { return tipeAkun; }
    public void setTipeAkun(String tipeAkun) { this.tipeAkun = tipeAkun; }
    
    public String getJenisKartu() { return jenisKartu; }
    public void setJenisKartu(String jenisKartu) { this.jenisKartu = jenisKartu; }
    
    public String getTempatLahir() { return tempatLahir; }
    public void setTempatLahir(String tempatLahir) { this.tempatLahir = tempatLahir; }
    
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    
    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    
    public String getAgama() { return agama; }
    public void setAgama(String agama) { this.agama = agama; }
    
    public String getStatusPernikahan() { return statusPernikahan; }
    public void setStatusPernikahan(String statusPernikahan) { this.statusPernikahan = statusPernikahan; }
    
    public String getPekerjaan() { return pekerjaan; }
    public void setPekerjaan(String pekerjaan) { this.pekerjaan = pekerjaan; }
    
    public String getSumberPenghasilan() { return sumberPenghasilan; }
    public void setSumberPenghasilan(String sumberPenghasilan) { this.sumberPenghasilan = sumberPenghasilan; }
    
    public String getRentangGaji() { return rentangGaji; }
    public void setRentangGaji(String rentangGaji) { this.rentangGaji = rentangGaji; }
    
    public String getTujuanPembuatanRekening() { return tujuanPembuatanRekening; }
    public void setTujuanPembuatanRekening(String tujuanPembuatanRekening) { this.tujuanPembuatanRekening = tujuanPembuatanRekening; }
    
    public Integer getKodeRekening() { return kodeRekening; }
    public void setKodeRekening(Integer kodeRekening) { this.kodeRekening = kodeRekening; }
    
    public String getNomorKartuDebitVirtual() {return nomorKartuDebitVirtual;}
    public void setNomorKartuDebitVirtual(String nomorKartuDebitVirtual) {this.nomorKartuDebitVirtual = nomorKartuDebitVirtual;}

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    // helper untuk mengecek apakah akun terkunci
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    public Alamat getAlamat() { return alamat; }
    public void setAlamat(Alamat alamat) { this.alamat = alamat; }
    
    public Wali getWali() { return wali; }
    public void setWali(Wali wali) { this.wali = wali; }
}