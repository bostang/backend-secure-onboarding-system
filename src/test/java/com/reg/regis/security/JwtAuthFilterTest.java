package com.reg.regis.security;

import com.reg.regis.service.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomerUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private UserDetails userDetails;
    private final String validToken = "valid.jwt.token";
    private final String validEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        
        userDetails = User.builder()
                .username(validEmail)
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    // ======================= Successful Authentication Tests =======================
    @Test
    void testDoFilterInternal_validTokenAndUser_shouldAuthenticateSuccessfully() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(validEmail)).thenReturn(userDetails);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_validTokenButUserDisabled_shouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        UserDetails disabledUser = User.builder()
                .username(validEmail)
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .disabled(true)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(validEmail)).thenReturn(disabledUser);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_validTokenButAccountLocked_shouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        UserDetails lockedUser = User.builder()
                .username(validEmail)
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountLocked(true)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(validEmail)).thenReturn(lockedUser);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    // ======================= Invalid Token Tests =======================
    @Test
    void testDoFilterInternal_noAuthorizationHeader_shouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_authHeaderWithoutBearer_shouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic " + validToken);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_invalidToken_shouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(false);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).getEmailFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_validTokenButEmailMismatch_shouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        String differentEmail = "different@example.com";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(differentEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(differentEmail)).thenReturn(userDetails); // userDetails has validEmail

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_tokenValidationFailsOnDoubleCheck_shouldNotAuthenticate() throws ServletException, IOException {
        // Arrange - Test line 63: double-check token validation fails
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken))
            .thenReturn(true)  // First validation passes (line 46)
            .thenReturn(false); // Second validation fails (line 63)
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(validEmail)).thenReturn(userDetails);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(2)).validateToken(validToken); // Called twice
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    // ======================= Exception Handling Tests =======================
    @Test
    void testDoFilterInternal_userDetailsServiceThrowsException_shouldClearContext() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(validEmail)).thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_jwtUtilThrowsException_shouldClearContext() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenThrow(new RuntimeException("JWT processing error"));

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    // ======================= Existing Authentication Tests =======================
    @Test
    void testDoFilterInternal_existingAuthentication_shouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    // ======================= shouldNotFilter Tests =======================
    @Test
    void testShouldNotFilter_optionsMethod_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_loginEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/login");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_registerEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/register");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_checkPasswordEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/check-password");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_validateNikEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/validate-nik");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_healthEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/auth/health");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_checkAuthEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/auth/check-auth");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_verificationEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/verification/email");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_actuatorHealthEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_errorEndpoint_shouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/error");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testShouldNotFilter_protectedEndpoint_shouldReturnFalse() throws ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertFalse(result);
    }

    // ======================= getClientIpAddress Tests =======================
    @Test
    void testGetClientIpAddress_withXForwardedFor_shouldReturnFirstIp() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");

        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthFilter.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(jwtAuthFilter, request);

        // Assert
        assertEquals("192.168.1.1", result);
    }

    @Test
    void testGetClientIpAddress_withXRealIp_shouldReturnXRealIp() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("192.168.1.100");

        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthFilter.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(jwtAuthFilter, request);

        // Assert
        assertEquals("192.168.1.100", result);
    }

    @Test
    void testGetClientIpAddress_noProxyHeaders_shouldReturnRemoteAddr() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthFilter.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(jwtAuthFilter, request);

        // Assert
        assertEquals("127.0.0.1", result);
    }

    @Test
    void testGetClientIpAddress_emptyXForwardedFor_shouldUseXRealIp() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getHeader("X-Real-IP")).thenReturn("192.168.1.200");

        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthFilter.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(jwtAuthFilter, request);

        // Assert
        assertEquals("192.168.1.200", result);
    }

    @Test
    void testGetClientIpAddress_emptyXRealIp_shouldUseRemoteAddr() throws Exception {
        // Arrange - Test line 131: when xRealIp is empty, should fallback to getRemoteAddr()
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(""); // empty string
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthFilter.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(jwtAuthFilter, request);

        // Assert
        assertEquals("127.0.0.1", result);
    }

    // ======================= Edge Cases =======================
    @Test
    void testDoFilterInternal_bearerTokenWithoutSpace_shouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_userDetailsIsNull_shouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(validEmail);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(validEmail)).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }
}