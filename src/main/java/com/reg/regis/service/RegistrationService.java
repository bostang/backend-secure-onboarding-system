package com.reg.regis.service;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.dto.response.RegistrationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import com.reg.regis.model.Wali;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.Map;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class RegistrationService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private DukcapilClientService dukcapilClientService;
    
    // Static random untuk thread safety
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * Generate nomor kartu debit virtual 16 digit dengan format standar kartu
     * Format: xxxx xxxx xxxx xxxx
     */
    private String generateNomorKartuDebitVirtual(String jenisKartu) {
        // Prefix berdasarkan jenis kartu (4 digit pertama)
        String prefix = switch (jenisKartu) {
            case "Silver" -> "4101";      // 4101 xxxx xxxx xxxx
            case "Gold" -> "4102";        // 4102 xxxx xxxx xxxx  
            case "Platinum" -> "4103";    // 4103 xxxx xxxx xxxx
            case "Batik Air" -> "4104";   // 4104 xxxx xxxx xxxx
            case "GPN" -> "4105";         // 4105 xxxx xxxx xxxx
            default -> "4101";            // Default Silver
        };
        
        // Generate 12 digit sisanya (random)
        long random12Digit = (long)(RANDOM.nextDouble() * 1000000000000L);
        String random12DigitStr = String.format("%012d", random12Digit);
        
        // Gabungkan jadi 16 digit
        String cardNumber = prefix + random12DigitStr;
        
        // Format dengan spasi: xxxx xxxx xxxx xxxx
        return formatCardNumber(cardNumber);
    }
    
    /**
     * Format 16 digit menjadi xxxx xxxx xxxx xxxx
     */
    private String formatCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            throw new IllegalArgumentException("Card number must be 16 digits");
        }
        
        return cardNumber.substring(0, 4) + " " + 
               cardNumber.substring(4, 8) + " " + 
               cardNumber.substring(8, 12) + " " + 
               cardNumber.substring(12, 16);
    }
    
    /**
     * Generate nomor kartu debit virtual dengan collision detection
     */
    private String generateUniqueNomorKartuDebitVirtual(String jenisKartu) {
        String nomorKartu = generateNomorKartuDebitVirtual(jenisKartu);
        
        // Check collision dan retry maksimal 5 kali
        int attempts = 0;
        while (customerRepository.existsByNomorKartuDebitVirtual(nomorKartu) && attempts < 5) {
            nomorKartu = generateNomorKartuDebitVirtual(jenisKartu);
            attempts++;
        }
        
        return nomorKartu;
    }
    
    /**
     * Generate kode rekening berdasarkan jenis kartu
     * Format: [Prefix][YYMM][Random4Digit]
     */
    private Integer generateKodeRekening(String jenisKartu) {
        if (jenisKartu == null || jenisKartu.trim().isEmpty()) {
            jenisKartu = "Silver"; // Default
        }
        
        // Prefix berdasarkan jenis kartu
        int prefix = switch (jenisKartu) {
            case "Silver" -> 10;
            case "Gold" -> 20;
            case "Platinum" -> 30;
            case "Batik Air" -> 40;
            default -> 10; // Default Silver
        };
        
        // Year-Month (YYMM format)
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear() % 100; // Last 2 digits of year
        int month = now.getMonthValue();
        int yearMonth = year * 100 + month; // Format: YYMM
        
        // Random 4 digits (1000-9999)
        int random4Digit = 1000 + RANDOM.nextInt(9000);
        
        // Gabungkan: prefix (2) + yearMonth (4) + random (4) = 10 digits
        long accountCode = (long) prefix * 100000000L + (long) yearMonth * 10000L + random4Digit;
        
        return (int) (accountCode % Integer.MAX_VALUE);
    }
    
    /**
     * Generate kode rekening dengan collision detection
     */
    private Integer generateUniqueKodeRekening(String jenisKartu) {
        Integer kodeRekening = generateKodeRekening(jenisKartu);
        
        // Check collision dan retry maksimal 5 kali
        int attempts = 0;
        while (customerRepository.existsByKodeRekening(kodeRekening) && attempts < 5) {
            kodeRekening = generateKodeRekening(jenisKartu);
            attempts++;
        }
        
        // Jika masih collision setelah 5 kali, generate simple random
        if (customerRepository.existsByKodeRekening(kodeRekening)) {
            int prefix = switch (jenisKartu) {
                case "Silver" -> 10;
                case "Gold" -> 20;
                case "Platinum" -> 30;
                case "Batik Air" -> 40;
                default -> 10;
            };
            
            // Simple format: prefix + 6 random digits
            int randomNumber = 100000 + RANDOM.nextInt(900000);
            kodeRekening = prefix * 1000000 + randomNumber;
        }
        
        return kodeRekening;
    }
    
    @Transactional
    public RegistrationResponse registerCustomer(RegistrationRequest request) {
        // 1. CHECK DUKCAPIL SERVICE AVAILABILITY
        if (!dukcapilClientService.isDukcapilServiceHealthy()) {
            throw new RuntimeException("Dukcapil service tidak tersedia. Silakan coba lagi nanti.");
        }
        
        // 2. VALIDASI NIK, NAMA, DAN TANGGAL LAHIR VIA DUKCAPIL SERVICE
        DukcapilResponseDto dukcapilResponse = dukcapilClientService.verifyNikNameAndBirthDate(
            request.getNik(), 
            request.getNamaLengkap(),
            request.getTanggalLahir()
        );
        
        if (!dukcapilResponse.isValid()) {
            throw new RuntimeException("Verifikasi Dukcapil gagal: " + dukcapilResponse.getMessage());
        }
        
        // 3. VALIDASI EMAIL TIDAK BOLEH DUPLIKAT
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email " + request.getEmail() + " sudah terdaftar. Gunakan email lain.");
        }
        
        // 4. VALIDASI NOMOR TELEPON TIDAK BOLEH DUPLIKAT
        if (customerRepository.existsByNomorTelepon(request.getNomorTelepon())) {
            throw new RuntimeException("Nomor telepon " + request.getNomorTelepon() + " sudah terdaftar. Gunakan nomor lain.");
        }
        
        // 5. VALIDASI NIK BELUM PERNAH DIGUNAKAN UNTUK REGISTRASI
        if (customerRepository.existsByNik(request.getNik())) {
            throw new RuntimeException("NIK " + request.getNik() + " sudah pernah digunakan untuk registrasi.");
        }
        
        // 6. SET JENIS KARTU DAN AUTO-GENERATE KODE REKENING
        String jenisKartu = request.getJenisKartu() != null ? request.getJenisKartu() : "Silver";
        
        if (request.getKodeRekening() == null) {
            Integer generatedCode = generateUniqueKodeRekening(jenisKartu);
            request.setKodeRekening(generatedCode);
        }
        
        // 7. AUTO-GENERATE NOMOR KARTU DEBIT VIRTUAL
        String nomorKartuDebitVirtual = generateUniqueNomorKartuDebitVirtual(jenisKartu);
        
        // 8. BUAT CUSTOMER BARU DENGAN DATA DARI DUKCAPIL
        Customer customer = new Customer();
        Map<String, Object> ktpData = dukcapilResponse.getData();
        
        // Data dari KTP Dukcapil (auto-fill)
        if (ktpData != null) {
            customer.setNamaLengkap((String) ktpData.get("namaLengkap"));
            customer.setTempatLahir((String) ktpData.get("tempatLahir"));
            
            // Parse tanggal lahir
            String tanggalLahirStr = (String) ktpData.get("tanggalLahir");
            if (tanggalLahirStr != null) {
                customer.setTanggalLahir(java.time.LocalDate.parse(tanggalLahirStr));
            } else {
                customer.setTanggalLahir(request.getTanggalLahir());
            }
            
            customer.setJenisKelamin((String) ktpData.get("jenisKelamin"));
            customer.setAgama((String) ktpData.get("agama"));
        } else {
            // Fallback ke data dari form jika KTP data tidak ada
            customer.setNamaLengkap(request.getNamaLengkap());
            customer.setTempatLahir(request.getTempatLahir());
            customer.setTanggalLahir(request.getTanggalLahir());
            customer.setJenisKelamin(request.getJenisKelamin());
            customer.setAgama(request.getAgama());
        }
        
        // Data dari form registrasi
        customer.setNik(request.getNik());
        customer.setNamaIbuKandung(request.getNamaIbuKandung());
        customer.setNomorTelepon(request.getNomorTelepon());
        customer.setEmail(request.getEmail().toLowerCase());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setTipeAkun(request.getTipeAkun());
        customer.setStatusPernikahan(request.getStatusPernikahan());
        customer.setPekerjaan(request.getPekerjaan());
        customer.setSumberPenghasilan(request.getSumberPenghasilan());
        customer.setRentangGaji(request.getRentangGaji());
        customer.setTujuanPembuatanRekening(request.getTujuanPembuatanRekening());
        customer.setKodeRekening(request.getKodeRekening()); // Generated code
        customer.setEmailVerified(false);
        
        // Set jenisKartu dan nomor kartu debit virtual
        customer.setJenisKartu(jenisKartu);
        customer.setNomorKartuDebitVirtual(nomorKartuDebitVirtual);
        
        // 9. BUAT ALAMAT
        Alamat alamat = new Alamat();
        alamat.setNamaAlamat(request.getAlamat().getNamaAlamat());
        alamat.setProvinsi(request.getAlamat().getProvinsi());
        alamat.setKota(request.getAlamat().getKota());
        alamat.setKecamatan(request.getAlamat().getKecamatan());
        alamat.setKelurahan(request.getAlamat().getKelurahan());
        alamat.setKodePos(request.getAlamat().getKodePos());
        
        // 10. BUAT WALI (OPTIONAL)
        Wali wali = null;
        if (request.getWali() != null && request.getWali().isComplete()) {
            wali = new Wali();
            wali.setJenisWali(request.getWali().getJenisWali());
            wali.setNamaLengkapWali(request.getWali().getNamaLengkapWali());
            wali.setPekerjaanWali(request.getWali().getPekerjaanWali());
            wali.setAlamatWali(request.getWali().getAlamatWali());
            wali.setNomorTeleponWali(request.getWali().getNomorTeleponWali());
        }
        
        // Set relasi
        customer.setAlamat(alamat);
        customer.setWali(wali);  // Bisa null
        
        Customer savedCustomer = customerRepository.save(customer);
        
        // Return response DTO dengan data yang diminta
        return new RegistrationResponse(
        savedCustomer.getJenisKartu(),
        savedCustomer.getNamaLengkap(),
        String.valueOf(savedCustomer.getKodeRekening()),
        savedCustomer.getTipeAkun(), // ✅ Ini akan jadi jenisTabungan
        savedCustomer.getNomorKartuDebitVirtual()
);
    }
    
    // NEW: Method untuk check kode rekening exists (untuk repository)
    public Optional<Customer> getCustomerByAccountCode(Integer kodeRekening) {
        return customerRepository.findByKodeRekening(kodeRekening);
    }
    
    /**
     * Validasi NIK, nama, dan tanggal lahir via Dukcapil service (untuk preview)
     */
    public DukcapilResponseDto validateNikNameAndBirthDate(String nik, String namaLengkap, java.time.LocalDate tanggalLahir) {
        return dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);
    }
    
    /**
     * Check NIK existence via Dukcapil service
     */
    public boolean checkNikExists(String nik) {
        return dukcapilClientService.isNikExists(nik);
    }
    
    public String authenticateCustomer(String email, String password) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Email atau password salah");
        }
        
        Customer customer = customerOpt.get();
        
        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Email atau password salah");
        }
        
        return jwtUtil.generateToken(customer.getEmail());
    }
    
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email);
    }
    
    public Optional<Customer> getCustomerByNik(String nik) {
        return customerRepository.findByNik(nik);
    }
    
    public boolean validateNikFormat(String nik) {
        if (nik == null || nik.length() != 16) {
            return false;
        }
        
        try {
            Long.parseLong(nik);
            String provinsi = nik.substring(0, 2);
            String kabupaten = nik.substring(2, 4);
            String kecamatan = nik.substring(4, 6);
            
            return !provinsi.equals("00") && !kabupaten.equals("00") && !kecamatan.equals("00");
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Transactional
    public void verifyEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setEmailVerified(true);
            customerRepository.save(customer);
        }
    }
    
    public String checkPasswordStrength(String password) {
        int score = 0;
        
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[@$!%*?&].*")) score++;
        
        return switch (score) {
            case 0, 1, 2 -> "lemah";
            case 3, 4 -> "sedang";
            case 5 -> "kuat";
            default -> "lemah";
        };
    }
    
    public String getEmailFromToken(String token) {
        try {
            return jwtUtil.getEmailFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String generateTokenForEmail(String email) {
        return jwtUtil.generateToken(email);
    }
    
    public static class RegistrationStats {
        private final long totalCustomers;
        private final long verifiedCustomers;
        private final double verificationRate;
        private final boolean dukcapilServiceAvailable;
        private final String dukcapilServiceUrl;
        
        public RegistrationStats(long totalCustomers, long verifiedCustomers, double verificationRate, 
                               boolean dukcapilServiceAvailable, String dukcapilServiceUrl) {
            this.totalCustomers = totalCustomers;
            this.verifiedCustomers = verifiedCustomers;
            this.verificationRate = verificationRate;
            this.dukcapilServiceAvailable = dukcapilServiceAvailable;
            this.dukcapilServiceUrl = dukcapilServiceUrl;
        }
        
        public long getTotalCustomers() { return totalCustomers; }
        public long getVerifiedCustomers() { return verifiedCustomers; }
        public double getVerificationRate() { return verificationRate; }
        public boolean isDukcapilServiceAvailable() { return dukcapilServiceAvailable; }
        public String getDukcapilServiceUrl() { return dukcapilServiceUrl; }
    }    
    
    public RegistrationStats getRegistrationStats() {
        long totalCustomers = customerRepository.countTotalCustomers();
        long verifiedCustomers = customerRepository.countVerifiedCustomers();
        double verificationRate = totalCustomers > 0 ? 
            (double) verifiedCustomers / totalCustomers * 100 : 0;
        boolean dukcapilAvailable = dukcapilClientService.isDukcapilServiceHealthy();
        String dukcapilUrl = dukcapilClientService.getDukcapilBaseUrl();
            
        return new RegistrationStats(totalCustomers, verifiedCustomers, verificationRate, 
                                   dukcapilAvailable, dukcapilUrl);
    }
}