package com.reg.regis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PhoneVerificationRequest {
    
    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(regexp = "^08[0-9]{8,11}$", message = "Format nomor telepon tidak valid (contoh: 081234567890)")
    private String nomorTelepon;
    
    // Constructors
    public PhoneVerificationRequest() {}
    
    public PhoneVerificationRequest(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }
    
    // Getters and Setters
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
}