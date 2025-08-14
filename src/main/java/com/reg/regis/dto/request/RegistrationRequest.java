package com.reg.regis.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegistrationRequest {
    
    @NotBlank(message = "Nama lengkap wajib diisi")
    private String namaLengkap;
    
    @NotBlank(message = "NIK wajib diisi")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    @Pattern(regexp = "^[0-9]{16}$", message = "NIK hanya boleh berisi angka")
    private String nik;
    
    @NotBlank(message = "Nama ibu kandung wajib diisi")
    private String namaIbuKandung;
    
    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(regexp = "^08[0-9]{8,11}$", message = "Format nomor telepon tidak valid")
    private String nomorTelepon;
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;
    
    @NotBlank(message = "Password wajib diisi")
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String password;
    
    @NotBlank(message = "Tipe akun wajib diisi")
    private String tipeAkun;
    
    private String jenisKartu;
    
    @NotBlank(message = "Tempat lahir wajib diisi")
    private String tempatLahir;
    
    @NotNull(message = "Tanggal lahir wajib diisi")
    private LocalDate tanggalLahir;
    
    @NotBlank(message = "Jenis kelamin wajib diisi")
    private String jenisKelamin;
    
    @NotBlank(message = "Agama wajib diisi")
    private String agama;
    
    @NotBlank(message = "Status pernikahan wajib diisi")
    private String statusPernikahan;
    
    @NotBlank(message = "Pekerjaan wajib diisi")
    private String pekerjaan;
    
    @NotBlank(message = "Sumber penghasilan wajib diisi")
    private String sumberPenghasilan;
    
    @NotBlank(message = "Rentang gaji wajib diisi")
    private String rentangGaji;
    
    @NotBlank(message = "Tujuan pembuatan rekening wajib diisi")
    private String tujuanPembuatanRekening;
    
    private Integer kodeRekening;

    private String nomorKartuDebitVirtual;
    
    @Valid
    @NotNull(message = "Alamat wajib diisi")
    private AlamatRequest alamat;
    
    @Valid
    private WaliRequest wali;  
    
    // Constructors
    public RegistrationRequest() {}
    
    // Getters and Setters
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
    public void setJenisKartu(String jenisKartu) { 
        this.jenisKartu = (jenisKartu != null && !jenisKartu.isEmpty()) ? jenisKartu : "Silver"; 
    }

    public String getNomorKartuDebitVirtual() {return nomorKartuDebitVirtual;}
    public void setNomorKartuDebitVirtual(String nomorKartuDebitVirtual) {this.nomorKartuDebitVirtual = nomorKartuDebitVirtual;}
    
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
    
    public AlamatRequest getAlamat() { return alamat; }
    public void setAlamat(AlamatRequest alamat) { this.alamat = alamat; }
    
    public WaliRequest getWali() { return wali; }
    public void setWali(WaliRequest wali) { this.wali = wali; }
    
    // Nested Classes
    public static class AlamatRequest {
        @NotBlank(message = "Nama alamat wajib diisi")
        private String namaAlamat;
        
        @NotBlank(message = "Provinsi wajib diisi")
        private String provinsi;
        
        @NotBlank(message = "Kota wajib diisi")
        private String kota;
        
        @NotBlank(message = "Kecamatan wajib diisi")
        private String kecamatan;
        
        @NotBlank(message = "Kelurahan wajib diisi")
        private String kelurahan;
        
        @NotBlank(message = "Kode pos wajib diisi")
        private String kodePos;
        
        // Getters and Setters
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
    
    // UPDATED: WaliRequest tanpa @NotBlank untuk semua field (karena optional)
    public static class WaliRequest {
        private String jenisWali;
        private String namaLengkapWali;
        private String pekerjaanWali;
        private String alamatWali;
        private String nomorTeleponWali;
        
        // Getters and Setters
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
        
        // Helper method untuk check apakah wali data lengkap
        public boolean isComplete() {
            return jenisWali != null && !jenisWali.trim().isEmpty() &&
                   namaLengkapWali != null && !namaLengkapWali.trim().isEmpty() &&
                   pekerjaanWali != null && !pekerjaanWali.trim().isEmpty() &&
                   alamatWali != null && !alamatWali.trim().isEmpty() &&
                   nomorTeleponWali != null && !nomorTeleponWali.trim().isEmpty();
        }
    }
}