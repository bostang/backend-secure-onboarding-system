package com.reg.regis.repository;

import com.reg.regis.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    Optional<Customer> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT c FROM Customer c WHERE c.email = :email") // Ini akan melakukan pencarian case-sensitive
    Optional<Customer> findByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    boolean existsByNomorTelepon(String nomorTelepon);
    
    @Query("SELECT c FROM Customer c WHERE c.nik = :nik")
    Optional<Customer> findByNik(@Param("nik") String nik);
    
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.nik = :nik")
    boolean existsByNik(@Param("nik") String nik);
    
    @Query("SELECT COUNT(c) FROM Customer c")
    Long countTotalCustomers();
    
    @Query("SELECT c FROM Customer c WHERE c.nik = :nik AND LOWER(c.email) = LOWER(:email)")
    Optional<Customer> findByNikAndEmail(@Param("nik") String nik, @Param("email") String email);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= CURRENT_DATE")
    Long countTodayRegistrations();

    boolean existsByKodeRekening(Integer kodeRekening);
    Optional<Customer> findByKodeRekening(Integer kodeRekening);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.jenisKartu = :jenisKartu")
    long countByJenisKartu(@Param("jenisKartu") String jenisKartu);
    
    @Query("SELECT c FROM Customer c WHERE c.jenisKartu = :jenisKartu")
    java.util.List<Customer> findByJenisKartu(@Param("jenisKartu") String jenisKartu);

    boolean existsByNomorKartuDebitVirtual(String nomorKartuDebitVirtual);
}