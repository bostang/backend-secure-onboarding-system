package com.reg.regis.service;

import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    @Test
    void recordFailedLoginAttempt_FirstAttempt_IncrementsCounter() {
        // Given
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFailedLoginAttempts(0);
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(customer));
        when(customerRepository.save(customer))
                .thenReturn(customer);

        // When
        Customer result = loginAttemptService.recordFailedLoginAttempt(email);

        // Then
        assertEquals(1, result.getFailedLoginAttempts());
        assertNull(result.getAccountLockedUntil());
        verify(customerRepository).findByEmailIgnoreCase(email);
        verify(customerRepository).save(customer);
    }

    @Test
    void recordFailedLoginAttempt_MaxAttemptsReached_LocksAccount() {
        // Given
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFailedLoginAttempts(4); // 4 + 1 = 5 (max attempts)
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(customer));
        when(customerRepository.save(customer))
                .thenReturn(customer);

        // When
        Customer result = loginAttemptService.recordFailedLoginAttempt(email);

        // Then
        assertEquals(0, result.getFailedLoginAttempts()); // Reset after lock
        assertNotNull(result.getAccountLockedUntil());
        assertTrue(result.getAccountLockedUntil().isAfter(LocalDateTime.now()));
        verify(customerRepository).save(customer);
    }

    @Test
    void recordFailedLoginAttempt_EmailNotFound_ThrowsBadCredentials() {
        // Given
        String email = "notfound@example.com";
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.empty());

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> loginAttemptService.recordFailedLoginAttempt(email)
        );
        
        assertEquals("Email atau password salah.", exception.getMessage());
        verify(customerRepository).findByEmailIgnoreCase(email);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getMaxLoginAttempts_ReturnsCorrectValue() {
        // When
        int maxAttempts = loginAttemptService.getMaxLoginAttempts();

        // Then
        assertEquals(5, maxAttempts);
    }

    @Test
    void getLockoutDurationMinutes_ReturnsCorrectValue() {
        // When
        long duration = loginAttemptService.getLockoutDurationMinutes();

        // Then
        assertEquals(1, duration);
    }

    @Test
    void recordFailedLoginAttempt_MultipleAttempts_CountsCorrectly() {
        // Given
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFailedLoginAttempts(2);
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(customer));
        when(customerRepository.save(customer))
                .thenReturn(customer);

        // When
        Customer result = loginAttemptService.recordFailedLoginAttempt(email);

        // Then
        assertEquals(3, result.getFailedLoginAttempts());
        assertNull(result.getAccountLockedUntil());
        verify(customerRepository).save(customer);
    }
}