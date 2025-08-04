package com.reg.regis.service;

// import com.reg.regis.dto.*;
import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.dto.response.VerificationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final CustomerRepository customerRepository;
    
    private final DukcapilClientService dukcapilClientService;

    /**
     * Verifikasi NIK dengan nama lengkap dan tanggal lahir via Dukcapil Service
     */
    @Transactional(readOnly = true)
    public VerificationResponse verifyNik(NikVerificationRequest request) {
        try {
            System.out.println("🔍 Starting NIK verification with full data: " + request);
            
            // Call Dukcapil Service dengan SEMUA field
            DukcapilResponseDto dukcapilResponse = dukcapilClientService.verifyNikNameAndBirthDate(
                request.getNik(), 
                request.getNamaLengkap(),
                request.getTanggalLahir()
            );
            
            if (dukcapilResponse.isValid()) {
                System.out.println("✅ NIK verification SUCCESS via Dukcapil Service");
                return new VerificationResponse(
                    true, 
                    dukcapilResponse.getMessage(), 
                    dukcapilResponse.getData()
                );
            } else {
                System.out.println("❌ NIK verification FAILED via Dukcapil Service: " + dukcapilResponse.getMessage());
                return new VerificationResponse(
                    false, 
                    dukcapilResponse.getMessage()
                );
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error in NIK verification: " + e.getMessage());
            return new VerificationResponse(
                false, 
                "Terjadi kesalahan saat verifikasi NIK: " + e.getMessage()
            );
        }
    }
    
    /**
     * Verifikasi email (check apakah sudah terdaftar)
     */
    @Transactional(readOnly = true)
    public VerificationResponse verifyEmail(EmailVerificationRequest request) {
        try {
            boolean emailExists = customerRepository.existsByEmailIgnoreCase(request.getEmail());
            
            if (emailExists) {
                Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(request.getEmail());
                if (customerOpt.isPresent()) {
                    Customer customer = customerOpt.get();
                    Map<String, Object> emailData = new HashMap<>();
                    emailData.put("email", customer.getEmail());
                    emailData.put("namaLengkap", customer.getNamaLengkap());
                    // emailData.put("emailVerified", customer.getEmailVerified());
                    emailData.put("registeredAt", customer.getCreatedAt());
                    
                    return new VerificationResponse(
                        false, 
                        "Email sudah terdaftar di sistem", 
                        emailData
                    );
                }
            }
            
            return new VerificationResponse(
                true, 
                "Email belum terdaftar dan dapat digunakan"
            );
            
        } catch (Exception e) {
            return new VerificationResponse(
                false, 
                "Terjadi kesalahan saat verifikasi email: " + e.getMessage()
            );
        }
    }
    
    /**
     * Verifikasi nomor telepon (check apakah sudah terdaftar)
     */
    @Transactional(readOnly = true)
    public VerificationResponse verifyPhone(PhoneVerificationRequest request) {
        try {
            boolean phoneExists = customerRepository.existsByNomorTelepon(request.getNomorTelepon());
            
            if (phoneExists) {
                Optional<Customer> customerOpt = customerRepository.findAll()
                    .stream()
                    .filter(c -> c.getNomorTelepon().equals(request.getNomorTelepon()))
                    .findFirst();
                
                if (customerOpt.isPresent()) {
                    Customer customer = customerOpt.get();
                    Map<String, Object> phoneData = new HashMap<>();
                    phoneData.put("nomorTelepon", customer.getNomorTelepon());
                    phoneData.put("namaLengkap", customer.getNamaLengkap());
                    phoneData.put("email", customer.getEmail());
                    phoneData.put("registeredAt", customer.getCreatedAt());
                    
                    return new VerificationResponse(
                        false, 
                        "Nomor telepon sudah terdaftar di sistem", 
                        phoneData
                    );
                }
            }
            
            return new VerificationResponse(
                true, 
                "Nomor telepon belum terdaftar dan dapat digunakan"
            );
            
        } catch (Exception e) {
            return new VerificationResponse(
                false, 
                "Terjadi kesalahan saat verifikasi nomor telepon: " + e.getMessage()
            );
        }
    }
    
    /**
     * Check apakah NIK terdaftar di Dukcapil (tanpa nama)
     */
    @Transactional(readOnly = true)
    public boolean isNikRegistered(String nik) {
        return dukcapilClientService.isNikExists(nik);
    }
    
    /**
     * Get verification statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getVerificationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Customer stats
        long totalCustomers = customerRepository.countTotalCustomers();
        long verifiedCustomers = 0; //customerRepository.countVerifiedCustomers();
        double verificationRate = totalCustomers > 0 ? 
            (double) verifiedCustomers / totalCustomers * 100 : 0;
        
        // Dukcapil service health
        boolean dukcapilHealthy = dukcapilClientService.isDukcapilServiceHealthy();
        
        stats.put("totalCustomers", totalCustomers);
        stats.put("verifiedCustomers", verifiedCustomers);
        stats.put("verificationRate", Math.round(verificationRate * 100.0) / 100.0);
        stats.put("dukcapilServiceHealthy", dukcapilHealthy);
        stats.put("dukcapilServiceUrl", dukcapilClientService.getDukcapilBaseUrl());
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
}