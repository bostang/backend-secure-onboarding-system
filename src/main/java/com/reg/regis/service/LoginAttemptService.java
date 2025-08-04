package com.reg.regis.service;

import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 1; // Durasi penguncian

    private final CustomerRepository customerRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Customer recordFailedLoginAttempt(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        if (customerOpt.isEmpty()) {
            // Ini seharusnya tidak terjadi jika dipanggil setelah findByEmailIgnoreCase di authenticateCustomer
            // Namun, untuk keamanan, kita bisa melempar BadCredentialsException atau logged warning.
            throw new BadCredentialsException("Email atau password salah."); // Agar konsisten dengan pesan login gagal
        }

        Customer customer = customerOpt.get();
        customer.setFailedLoginAttempts(customer.getFailedLoginAttempts() + 1);

        if (customer.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            customer.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            customer.setFailedLoginAttempts(0); // Reset attempts setelah dikunci
        }

        return customerRepository.save(customer); // Simpan perubahan di transaksi baru
    }

    public int getMaxLoginAttempts() {
        return MAX_LOGIN_ATTEMPTS;
    }

    public long getLockoutDurationMinutes() {
        return LOCKOUT_DURATION_MINUTES;
    }
}