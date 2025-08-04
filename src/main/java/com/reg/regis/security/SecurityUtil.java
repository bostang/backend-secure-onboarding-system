package com.reg.regis.security;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public class SecurityUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // Patterns for input validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9+\\-\\s()]{8,15}$"
    );
    
    private static final Pattern NIK_PATTERN = Pattern.compile(
        "^[0-9]{16}$"
    );
    
    // Private IP ranges for security
    private static final Pattern PRIVATE_IP_PATTERN = Pattern.compile(
        "^(127\\.)|(192\\.168\\.)|(10\\.)|(172\\.1[6-9]\\.)|(172\\.2[0-9]\\.)|(172\\.3[0-1]\\.)|(::1$)|([fF][cCdD])"
    );
    
    /**
     * Get real client IP address (handles proxies and load balancers)
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        // Check X-Forwarded-For header (from load balancers/proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (isValidIp(xForwardedFor)) {
            // Take the first IP if multiple IPs are present
            String[] ips = xForwardedFor.split(",");
            for (String ip : ips) {
                String trimmedIp = ip.trim();
                if (isValidIp(trimmedIp) && !isPrivateIp(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }
        
        // Check X-Real-IP header (from nginx)
        String xRealIp = request.getHeader("X-Real-IP");
        if (isValidIp(xRealIp) && !isPrivateIp(xRealIp)) {
            return xRealIp;
        }
        
        // Check X-Forwarded header
        String xForwarded = request.getHeader("X-Forwarded");
        if (isValidIp(xForwarded)) {
            return xForwarded;
        }
        
        // Fallback to remote address
        String remoteAddr = request.getRemoteAddr();
        return isValidIp(remoteAddr) ? remoteAddr : "unknown";
    }
    
    /**
     * Validate if string is a valid IP address
     */
    private static boolean isValidIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        
        String trimmedIp = ip.trim();
        
        // Basic IPv4 validation
        String[] parts = trimmedIp.split("\\.");
        if (parts.length == 4) {
            try {
                for (String part : parts) {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) {
                        return false;
                    }
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // IPv6 validation (basic)
        return trimmedIp.contains(":") && trimmedIp.length() >= 3;
    }
    
    /**
     * Check if IP is private/internal
     */
    private static boolean isPrivateIp(String ip) {
        return PRIVATE_IP_PATTERN.matcher(ip).find();
    }
    
    /**
     * Get current authenticated user email
     */
    public static String getCurrentUserEmail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            logger.debug("Error getting current user email: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Check if current user is authenticated
     */
    public static boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && 
                   authentication.isAuthenticated() && 
                   !"anonymousUser".equals(authentication.getName());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validate NIK format
     */
    public static boolean isValidNik(String nik) {
        return nik != null && NIK_PATTERN.matcher(nik.trim()).matches();
    }
    
    /**
     * Sanitize string input (remove potential XSS)
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
    
    /**
     * Check if request is from localhost/development
     */
    public static boolean isLocalRequest(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        return "127.0.0.1".equals(clientIp) || 
               "localhost".equals(clientIp) || 
               "::1".equals(clientIp) ||
               isPrivateIp(clientIp);
    }
    
    /**
     * Log security event
     */
    public static void logSecurityEvent(String event, String details, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String currentUser = getCurrentUserEmail();
        
        logger.warn("SECURITY_EVENT: {} | User: {} | IP: {} | Details: {} | UserAgent: {}", 
                   event, currentUser, clientIp, details, userAgent);
    }
    
    /**
     * Validate password strength - Fixed ReDoS vulnerability
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Use simple iteration instead of regex to avoid ReDoS
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                hasLower = true;
            } else if (c >= 'A' && c <= 'Z') {
                hasUpper = true;
            } else if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if ("@$!%*?&".indexOf(c) != -1) {
                hasSpecial = true;
            }
            
            // Early exit if all conditions met
            if (hasLower && hasUpper && hasDigit && hasSpecial) {
                break;
            }
        }
        
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
    
    /**
     * Generate secure random string for tokens
     */
    public static String generateSecureRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(chars.length());
            result.append(chars.charAt(index));
        }
        
        return result.toString();
    }
}