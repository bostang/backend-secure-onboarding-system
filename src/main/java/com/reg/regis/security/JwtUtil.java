package com.reg.regis.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders; // Import this for Base64 decoding
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${app.jwt.secret}")
    private String secret; // This should be a Base64 encoded string of at least 64 bytes
    
    @Value("${app.jwt.expiration}")
    private long expiration;
    
    @Value("${spring.application.name:Customer-Registration-Service}")
    private String issuer;
    
    private static final String AUDIENCE = "customer-app";
    
    /**
     * Retrieves the signing key for JWT.
     * The 'secret' property is expected to be a Base64 encoded string.
     * It must decode to at least 64 bytes (512 bits) for HS512.
     */
    private Key getSigningKey() {
        if (secret == null || secret.isEmpty()) {
            logger.error("JWT secret is null or empty. Please configure 'app.jwt.secret' in application.properties.");
            throw new IllegalStateException("JWT secret cannot be empty. Please configure 'app.jwt.secret' with a Base64 encoded key.");
        }
        
        try {
            // Decode the Base64 secret string into a byte array.
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            
            // HS512 requires a key size of at least 512 bits (64 bytes).
            if (keyBytes.length < 64) {
                logger.error("JWT secret (after Base64 decoding) is {} bytes, but HS512 requires at least 64 bytes (512 bits). " +
                             "Please generate a stronger key for 'app.jwt.secret'.", keyBytes.length);
                throw new IllegalStateException("JWT secret is too short for HS512. It must be a Base64 encoded string representing at least 64 bytes.");
            }
            
            // Create an HMAC-SHA key from the decoded bytes.
            return Keys.hmacShaKeyFor(keyBytes);
            
        } catch (IllegalArgumentException e) {
            // This catches errors if the 'secret' string is not a valid Base64 format.
            logger.error("Invalid Base64 string provided for 'app.jwt.secret'. Please ensure it's a valid Base64 encoded key: {}", e.getMessage());
            throw new IllegalStateException("JWT secret is not a valid Base64 string. " +
                                           "Please ensure 'app.jwt.secret' is a Base64 encoded key.", e);
        } catch (Exception e) {
            // Catch any other unexpected errors during key generation.
            logger.error("An unexpected error occurred while generating the signing key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT signing key.", e);
        }
    }
    
    /**
     * Generate JWT token with enhanced security
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        try {
            return Jwts.builder()
                    .setSubject(email)
                    .setIssuer(issuer)
                    .setAudience(AUDIENCE)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .setNotBefore(now) // Token not valid before current time
                    .claim("type", "access_token")
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Use HS512 for better security
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token for email: {}", email, e);
            throw new RuntimeException("Token generation failed", e);
        }
    }
    
    /**
     * Extract email from token with comprehensive validation
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .requireIssuer(issuer)
                    .requireAudience(AUDIENCE)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // Additional validation
            String tokenType = claims.get("type", String.class);
            if (!"access_token".equals(tokenType)) {
                logger.warn("Invalid token type: {}", tokenType);
                return null;
            }
            
            return claims.getSubject();
            
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            return null;
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token: {}", e.getMessage());
            return null;
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty or invalid: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error extracting email from JWT: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Comprehensive token validation
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .requireIssuer(issuer)
                    .requireAudience(AUDIENCE)
                    .build()
                    .parseClaimsJws(token);
            
            // Additional checks
            if (isTokenExpired(token)) {
                logger.debug("Token is expired");
                return false;
            }
            
            return true;
            
        } catch (ExpiredJwtException e) {
            logger.debug("JWT token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty or invalid: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error validating JWT: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration().before(new Date());
            
        } catch (Exception e) {
            logger.debug("Error checking token expiration (token might be invalid or malformed): {}", e.getMessage());
            return true; // Treat any parsing error as expired or invalid
        }
    }
    
    /**
     * Get token expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration();
            
        } catch (Exception e) {
            logger.error("Error getting expiration date from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate token for specific user
     */
    public boolean validateTokenForUser(String token, String email) {
        try {
            String tokenEmail = getEmailFromToken(token);
            return email.equals(tokenEmail) && validateToken(token);
        } catch (Exception e) {
            logger.warn("Error validating token for user {}: {}", email, e.getMessage());
            return false;
        }
    }
}
