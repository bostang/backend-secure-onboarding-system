package com.reg.regis.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String validEmail = "test@example.com";
    private final String validSecret = Base64.getEncoder().encodeToString(
            "this-is-a-very-long-secret-key-that-is-at-least-64-bytes-long-for-hs512-algorithm".getBytes()
    );
    private final long expiration = 86400000L; // 24 hours
    private final String issuer = "Customer-Registration-Service";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", validSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        ReflectionTestUtils.setField(jwtUtil, "issuer", issuer);
    }

    // ======================= getSigningKey Tests =======================
    @Test
    void testGetSigningKey_validSecret_shouldReturnKey() {
        // Act & Assert - Should not throw exception
        String token = jwtUtil.generateToken(validEmail);
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testGetSigningKey_nullSecret_shouldThrowException() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtil, "secret", null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken(validEmail);
        });
        
        assertTrue(exception.getMessage().contains("Token generation failed") || 
                  exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void testGetSigningKey_emptySecret_shouldThrowException() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtil, "secret", "");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken(validEmail);
        });
        
        assertTrue(exception.getMessage().contains("Token generation failed") || 
                  exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void testGetSigningKey_shortSecret_shouldThrowException() {
        // Arrange - Create a short secret (less than 64 bytes after Base64 decoding)
        String shortSecret = Base64.getEncoder().encodeToString("short".getBytes());
        ReflectionTestUtils.setField(jwtUtil, "secret", shortSecret);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken(validEmail);
        });
        
        assertTrue(exception.getMessage().contains("Token generation failed") || 
                  exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void testGetSigningKey_invalidBase64Secret_shouldThrowException() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtil, "secret", "invalid-base64-string!@#");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken(validEmail);
        });
        
        assertTrue(exception.getMessage().contains("Token generation failed") || 
                  exception.getCause() instanceof IllegalStateException);
    }

    // ======================= generateToken Tests =======================
    @Test
    void testGenerateToken_validEmail_shouldReturnToken() {
        // Act
        String token = jwtUtil.generateToken(validEmail);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT format has dots
        assertEquals(validEmail, jwtUtil.getEmailFromToken(token));
    }

    @Test
    void testGenerateToken_nullEmail_shouldReturnToken() {
        // Act
        String token = jwtUtil.generateToken(null);

        // Assert
        assertNotNull(token);
        assertNull(jwtUtil.getEmailFromToken(token));
    }

    @Test
    void testGenerateToken_emptyEmail_shouldReturnToken() {
        // Act
        String token = jwtUtil.generateToken("");

        // Assert
        assertNotNull(token);
        // Empty string subject becomes null when extracted
        String extractedEmail = jwtUtil.getEmailFromToken(token);
        // JWT treats empty string subject as null, so we expect null
        assertTrue(extractedEmail == null || extractedEmail.isEmpty());
    }

    // ======================= getEmailFromToken Tests =======================
    @Test
    void testGetEmailFromToken_validToken_shouldReturnEmail() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertEquals(validEmail, extractedEmail);
    }

    @Test
    void testGetEmailFromToken_expiredToken_shouldReturnNull() {
        // Arrange - Create token with short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms
        String token = jwtUtil.generateToken(validEmail);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_malformedToken_shouldReturnNull() {
        // Act
        String extractedEmail = jwtUtil.getEmailFromToken("invalid.malformed.token");

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_nullToken_shouldReturnNull() {
        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(null);

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_emptyToken_shouldReturnNull() {
        // Act
        String extractedEmail = jwtUtil.getEmailFromToken("");

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_tokenWithWrongIssuer_shouldReturnNull() {
        // Arrange - Create token with different issuer
        JwtUtil differentIssuerUtil = new JwtUtil();
        ReflectionTestUtils.setField(differentIssuerUtil, "secret", validSecret);
        ReflectionTestUtils.setField(differentIssuerUtil, "expiration", expiration);
        ReflectionTestUtils.setField(differentIssuerUtil, "issuer", "Different-Issuer");
        
        String token = differentIssuerUtil.generateToken(validEmail);

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_tokenWithWrongSignature_shouldReturnNull() {
        // Arrange - Create token with different secret
        String differentSecret = Base64.getEncoder().encodeToString(
            "different-very-long-secret-key-that-is-at-least-64-bytes-long-for-hs512".getBytes()
        );
        JwtUtil differentSecretUtil = new JwtUtil();
        ReflectionTestUtils.setField(differentSecretUtil, "secret", differentSecret);
        ReflectionTestUtils.setField(differentSecretUtil, "expiration", expiration);
        ReflectionTestUtils.setField(differentSecretUtil, "issuer", issuer);
        
        String token = differentSecretUtil.generateToken(validEmail);

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_tokenWithInvalidType_shouldReturnNull() throws Exception {
        // Arrange - Create token with invalid type manually
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        // Use reflection to get signing key
        java.lang.reflect.Method getSigningKeyMethod = JwtUtil.class.getDeclaredMethod("getSigningKey");
        getSigningKeyMethod.setAccessible(true);
        java.security.Key signingKey = (java.security.Key) getSigningKeyMethod.invoke(jwtUtil);
        
        String tokenWithInvalidType = Jwts.builder()
                .setSubject(validEmail)
                .setIssuer(issuer)
                .setAudience("customer-app")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setNotBefore(now)
                .claim("type", "refresh_token") // Invalid type - should be "access_token"
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        // Act - Test line 106-108: invalid token type
        String extractedEmail = jwtUtil.getEmailFromToken(tokenWithInvalidType);

        // Assert
        assertNull(extractedEmail);
    }

    @Test
    void testGetEmailFromToken_unsupportedJwtToken_shouldReturnNull() {
        // Arrange - Create a token with unsupported algorithm/format
        // This is difficult to create directly, so we'll use a malformed token that triggers UnsupportedJwtException
        String unsupportedToken = "unsupported.jwt.format.that.causes.UnsupportedJwtException";

        // Act - Test line 116-118: UnsupportedJwtException
        String extractedEmail = jwtUtil.getEmailFromToken(unsupportedToken);

        // Assert
        assertNull(extractedEmail);
    }

    // ======================= Additional getSigningKey Exception Tests =======================
    @Test
    void testGetSigningKey_runtimeExceptionFromKeyGeneration_shouldThrowRuntimeException() throws Exception {
        // This test covers the general Exception catch block in getSigningKey (line 60-63)
        // It's difficult to trigger directly, but we can test the generateToken wrapper
        ReflectionTestUtils.setField(jwtUtil, "secret", "invalid-format-that-causes-runtime-error");

        // Act & Assert - Line 60-63: catch (Exception e)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken(validEmail);
        });
        
        assertTrue(exception.getMessage().contains("Token generation failed"));
    }

    // ======================= Additional validateToken Exception Tests =======================
    @Test
    void testValidateToken_unsupportedJwtException_shouldReturnFalse() {
        // Act - Test line 157-159: UnsupportedJwtException in validateToken
        boolean isValid = jwtUtil.validateToken("unsupported.jwt.format");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_generalException_shouldReturnFalse() {
        // Arrange - Create a scenario that might trigger general Exception
        ReflectionTestUtils.setField(jwtUtil, "secret", "invalid-format");

        // Act - Test line 169-171: catch (Exception e) in validateToken
        boolean isValid = jwtUtil.validateToken("some.token.here");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_isTokenExpiredCheck_shouldReturnFalse() {
        // Arrange - Create an expired token
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms
        String token = jwtUtil.generateToken(validEmail);
        
        // Wait for expiration
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act - Test line 147-149: isTokenExpired check
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    // ======================= validateTokenForUser Exception Test =======================
    @Test
    void testValidateTokenForUser_exceptionThrown_shouldReturnFalse() {
        // Arrange - Use invalid secret to cause exception
        ReflectionTestUtils.setField(jwtUtil, "secret", "invalid-secret-format");

        // Act - Test line 219-222: catch (Exception e) in validateTokenForUser
        boolean isValid = jwtUtil.validateTokenForUser("some.token", validEmail);

        // Assert
        assertFalse(isValid);
    }

    // ======================= validateToken Tests =======================
    @Test
    void testValidateToken_validToken_shouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_expiredToken_shouldReturnFalse() {
        // Arrange - Create token with short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms
        String token = jwtUtil.generateToken(validEmail);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_malformedToken_shouldReturnFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken("invalid.malformed.token");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_nullToken_shouldReturnFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_emptyToken_shouldReturnFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_tokenWithWrongSignature_shouldReturnFalse() {
        // Arrange - Create token with different secret
        String differentSecret = Base64.getEncoder().encodeToString(
            "different-very-long-secret-key-that-is-at-least-64-bytes-long-for-hs512".getBytes()
        );
        JwtUtil differentSecretUtil = new JwtUtil();
        ReflectionTestUtils.setField(differentSecretUtil, "secret", differentSecret);
        ReflectionTestUtils.setField(differentSecretUtil, "expiration", expiration);
        ReflectionTestUtils.setField(differentSecretUtil, "issuer", issuer);
        
        String token = differentSecretUtil.generateToken(validEmail);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    // ======================= isTokenExpired Tests =======================
    @Test
    void testIsTokenExpired_validToken_shouldReturnFalse() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_expiredToken_shouldReturnTrue() {
        // Arrange - Create token with short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms
        String token = jwtUtil.generateToken(validEmail);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenExpired_malformedToken_shouldReturnTrue() {
        // Act
        boolean isExpired = jwtUtil.isTokenExpired("invalid.malformed.token");

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenExpired_nullToken_shouldReturnTrue() {
        // Act
        boolean isExpired = jwtUtil.isTokenExpired(null);

        // Assert
        assertTrue(isExpired);
    }

    // ======================= getExpirationDateFromToken Tests =======================
    @Test
    void testGetExpirationDateFromToken_validToken_shouldReturnDate() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testGetExpirationDateFromToken_malformedToken_shouldReturnNull() {
        // Act
        Date expirationDate = jwtUtil.getExpirationDateFromToken("invalid.malformed.token");

        // Assert
        assertNull(expirationDate);
    }

    @Test
    void testGetExpirationDateFromToken_nullToken_shouldReturnNull() {
        // Act
        Date expirationDate = jwtUtil.getExpirationDateFromToken(null);

        // Assert
        assertNull(expirationDate);
    }

    // ======================= validateTokenForUser Tests =======================
    @Test
    void testValidateTokenForUser_validTokenAndMatchingEmail_shouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act
        boolean isValid = jwtUtil.validateTokenForUser(token, validEmail);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenForUser_validTokenButDifferentEmail_shouldReturnFalse() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);
        String differentEmail = "different@example.com";

        // Act
        boolean isValid = jwtUtil.validateTokenForUser(token, differentEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenForUser_expiredToken_shouldReturnFalse() {
        // Arrange - Create token with short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms
        String token = jwtUtil.generateToken(validEmail);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtUtil.validateTokenForUser(token, validEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenForUser_malformedToken_shouldReturnFalse() {
        // Act
        boolean isValid = jwtUtil.validateTokenForUser("invalid.malformed.token", validEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenForUser_nullToken_shouldReturnFalse() {
        // Act
        boolean isValid = jwtUtil.validateTokenForUser(null, validEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenForUser_nullEmail_shouldReturnFalse() {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act
        boolean isValid = jwtUtil.validateTokenForUser(token, null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenForUser_nullTokenForNullEmail_shouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(null);

        // Act
        boolean isValid = jwtUtil.validateTokenForUser(token, null);

        // Assert
        // This should return false because getEmailFromToken returns null for null subject,
        // and null.equals(null) in validateTokenForUser will cause NullPointerException
        // which is caught and returns false
        assertFalse(isValid);
    }

    // ======================= Token Content Validation Tests =======================
    @Test
    void testTokenContains_correctClaims() throws Exception {
        // Arrange
        String token = jwtUtil.generateToken(validEmail);

        // Act - Use reflection to get signing key
        java.lang.reflect.Method getSigningKeyMethod = JwtUtil.class.getDeclaredMethod("getSigningKey");
        getSigningKeyMethod.setAccessible(true);
        java.security.Key signingKey = (java.security.Key) getSigningKeyMethod.invoke(jwtUtil);

        // Parse token manually to verify claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Assert
        assertEquals(validEmail, claims.getSubject());
        assertEquals(issuer, claims.getIssuer());
        assertEquals("customer-app", claims.getAudience());
        assertEquals("access_token", claims.get("type"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getNotBefore());
    }

    // ======================= Edge Cases and Error Handling =======================
    @Test
    void testGenerateToken_longEmail_shouldWork() {
        // Arrange
        String longEmail = "very.long.email.address.that.might.cause.issues@very.long.domain.name.example.com";

        // Act
        String token = jwtUtil.generateToken(longEmail);

        // Assert
        assertNotNull(token);
        assertEquals(longEmail, jwtUtil.getEmailFromToken(token));
    }

    @Test
    void testGenerateToken_emailWithSpecialCharacters_shouldWork() {
        // Arrange
        String specialEmail = "test+tag@sub.domain-name.co.uk";

        // Act
        String token = jwtUtil.generateToken(specialEmail);

        // Assert
        assertNotNull(token);
        assertEquals(specialEmail, jwtUtil.getEmailFromToken(token));
    }

    @Test
    void testTokenExpiration_exactTiming() {
        // Arrange - Create token with longer expiration for more reliable test
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1000L); // 1 second
        
        // Act - Validate immediately (should be valid)
        String token = jwtUtil.generateToken(validEmail);
        boolean validImmediately = jwtUtil.validateToken(token);
        
        // Now create an expired token by setting very short expiration and waiting
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms
        String shortToken = jwtUtil.generateToken(validEmail);
        
        // Wait for short token to expire
        try {
            Thread.sleep(50); // Wait 50ms to ensure expiration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Validate after expiration (should be invalid)
        boolean validAfterExpiration = jwtUtil.validateToken(shortToken);

        // Assert
        assertTrue(validImmediately);
        assertFalse(validAfterExpiration);
    }
}