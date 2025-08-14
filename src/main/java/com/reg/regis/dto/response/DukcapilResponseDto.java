package com.reg.regis.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DukcapilResponseDto {
    
    private boolean valid;
    private String message;
    private Map<String, Object> data;
    private String timestamp;
    private String service;
    
    // Constructors
    public DukcapilResponseDto() {}
    
    public DukcapilResponseDto(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    
    @Override
    public String toString() {
        return "DukcapilResponseDto{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}