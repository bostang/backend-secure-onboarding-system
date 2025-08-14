package com.reg.regis.dto.response;

public class VerificationResponse {
    private boolean valid;
    private String message;
    private Object data;
    
    public VerificationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
        this.data = null;
    }
    
    public VerificationResponse(boolean valid, String message, Object data) {
        this.valid = valid;
        this.message = message;
        this.data = data;
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}