package com.reg.regis.controller;

import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import com.reg.regis.dto.response.VerificationResponse;
import com.reg.regis.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/verification")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
@RequiredArgsConstructor
public class VerificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);
    private final VerificationService verificationService;
    
    /**
     * Verifikasi NIK dengan nama lengkap dan tanggal lahir via Dukcapil Service
     */
    @PostMapping("/nik")
    public ResponseEntity<?> verifyNik(@Valid @RequestBody NikVerificationRequest request) {
        try {
            logger.debug("Received NIK verification request for NIK: {}", 
                request.getNik() != null ? request.getNik().substring(0, 4) + "****" : "null");
            
            VerificationResponse response = verificationService.verifyNik(request);
            
            return ResponseEntity.ok(Map.of(
                "valid", response.isValid(),
                "message", response.getMessage(),
                "data", response.getData() != null ? response.getData() : Map.of()
            ));
            
        } catch (Exception e) {
            logger.error("Error in verifyNik: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Terjadi kesalahan saat verifikasi NIK: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Verifikasi email
     */
    @PostMapping("/email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            VerificationResponse response = verificationService.verifyEmail(request);
            
            return ResponseEntity.ok(Map.of(
                "available", response.isValid(),
                "message", response.getMessage(),
                "data", response.getData() != null ? response.getData() : Map.of()
            ));
            
        } catch (Exception e) {
            logger.error("Error in verifyEmail: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                "available", false,
                "message", "Terjadi kesalahan saat verifikasi email: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Verifikasi nomor telepon
     */
    @PostMapping("/phone")
    public ResponseEntity<?> verifyPhone(@Valid @RequestBody PhoneVerificationRequest request) {
        try {
            VerificationResponse response = verificationService.verifyPhone(request);
            
            return ResponseEntity.ok(Map.of(
                "available", response.isValid(),
                "message", response.getMessage(),
                "data", response.getData() != null ? response.getData() : Map.of()
            ));
            
        } catch (Exception e) {
            logger.error("Error in verifyPhone: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                "available", false,
                "message", "Terjadi kesalahan saat verifikasi nomor telepon: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Check NIK tanpa nama (simple check)
     */
    @PostMapping("/nik-check")
    public ResponseEntity<?> checkNik(@RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            
            if (nik == null || nik.length() != 16) {
                return ResponseEntity.badRequest().body(Map.of(
                    "registered", false,
                    "message", "NIK harus 16 digit"
                ));
            }
            
            boolean isRegistered = verificationService.isNikRegistered(nik);
            
            return ResponseEntity.ok(Map.of(
                "registered", isRegistered,
                "message", isRegistered ? 
                    "NIK terdaftar di database Dukcapil" : 
                    "NIK tidak terdaftar di database Dukcapil"
            ));
            
        } catch (Exception e) {
            logger.error("Error in checkNik: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                "registered", false,
                "message", "Terjadi kesalahan saat pengecekan NIK: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get verification statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getVerificationStats() {
        try {
            return ResponseEntity.ok(verificationService.getVerificationStats());
        } catch (Exception e) {
            logger.error("Error in getVerificationStats: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Terjadi kesalahan saat mengambil statistik: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Health check untuk verification service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Verification Service (Enhanced with tanggalLahir)",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "nikVerification", "POST /verification/nik (requires: nik, namaLengkap, tanggalLahir)",
                "emailVerification", "POST /verification/email", 
                "phoneVerification", "POST /verification/phone",
                "nikCheck", "POST /verification/nik-check",
                "stats", "GET /verification/stats"
            )
        ));
    }
}