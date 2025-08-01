package com.reg.regis.security;

import com.reg.regis.service.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    
    // @Autowired
    // private JwtUtil jwtUtil;
    private final JwtUtil jwtUtil;

    // @Autowired
    // private CustomerUserDetailsService userDetailsService;
    private final CustomerUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        try {
            // Extract token from Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                
                // Validate token format and extract email
                if (jwtUtil.validateToken(token)) {
                    email = jwtUtil.getEmailFromToken(token);
                } else {
                    logger.warn("Invalid JWT token received from IP: {}", getClientIpAddress(request));
                }
            }

            // If we have a valid email and no existing authentication
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    if (userDetails != null) {
                        // Double-check token validity and user match
                        if (jwtUtil.validateToken(token) && email.equals(userDetails.getUsername())) {
                            
                            // Check if account is enabled and not locked
                            if (userDetails.isEnabled() && userDetails.isAccountNonLocked()) {
                                UsernamePasswordAuthenticationToken authToken = 
                                    new UsernamePasswordAuthenticationToken(
                                        userDetails, 
                                        null, 
                                        userDetails.getAuthorities()
                                    );
                                
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                
                                logger.debug("Successfully authenticated user: {}", email);
                            } else {
                                logger.warn("Account disabled or locked for user: {}", email);
                            }
                        } else {
                            logger.warn("Token validation failed for user: {}", email);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Authentication failed for email {}: {}", email, e.getMessage());
                    // Clear any partial authentication
                    SecurityContextHolder.clearContext();
                }
            }
        } catch (Exception e) {
            logger.error("JWT processing error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Skip JWT processing for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip JWT processing for public endpoints
        return "OPTIONS".equals(method) ||
               path.equals("/auth/login") ||
               path.equals("/auth/register") ||
               path.equals("/auth/check-password") ||
               path.equals("/auth/validate-nik") ||
               path.equals("/auth/health") ||
               path.equals("/auth/check-auth") ||
               path.startsWith("/verification/") ||
               path.equals("/actuator/health") ||
               path.equals("/error");
    }

    /**
     * Get real client IP address (handles proxies)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}