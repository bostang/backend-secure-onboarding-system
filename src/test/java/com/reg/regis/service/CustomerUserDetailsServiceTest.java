package com.reg.regis.service;

import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerUserDetailsService customerUserDetailsService;

    @Test
    void loadUserByUsername_ValidEmail_ReturnsUserDetails() {
        // Given
        String email = "test@example.com";
        String password = "hashedPassword123";
        
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail(email);
        mockCustomer.setPassword(password);
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(mockCustomer));

        // When
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        
        verify(customerRepository).findByEmailIgnoreCase(email);
    }

    @Test
    void loadUserByUsername_EmailNotFound_ThrowsUsernameNotFoundException() {
        // Given
        String email = "notfound@example.com";
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customerUserDetailsService.loadUserByUsername(email)
        );
        
        assertEquals("User not found with email: " + email, exception.getMessage());
        verify(customerRepository).findByEmailIgnoreCase(email);
    }

    @Test
    void loadUserByUsername_CaseInsensitiveEmail_Works() {
        // Given
        String email = "TEST@EXAMPLE.COM";
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail("test@example.com");
        mockCustomer.setPassword("password123");
        
        when(customerRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(mockCustomer));

        // When
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(customerRepository).findByEmailIgnoreCase(email);
    }
}