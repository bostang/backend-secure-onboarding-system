package com.reg.regis.repository;

import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByEmailIgnoreCase() {
        Customer customer = createTestCustomer();
        customer.setEmail("test@example.com");
        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByEmailIgnoreCase("TEST@EXAMPLE.COM");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        Customer customer = createTestCustomer();
        customer.setEmail("test@example.com");
        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        
        Optional<Customer> notFound = customerRepository.findByEmail("TEST@EXAMPLE.COM");
        assertFalse(notFound.isPresent());
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        Customer customer = createTestCustomer();
        customer.setEmail("test@example.com");
        entityManager.persistAndFlush(customer);

        assertTrue(customerRepository.existsByEmailIgnoreCase("TEST@EXAMPLE.COM"));
        assertFalse(customerRepository.existsByEmailIgnoreCase("notfound@example.com"));
    }

    @Test
    void testExistsByNomorTelepon() {
        Customer customer = createTestCustomer();
        customer.setNomorTelepon("081234567890");
        entityManager.persistAndFlush(customer);

        assertTrue(customerRepository.existsByNomorTelepon("081234567890"));
        assertFalse(customerRepository.existsByNomorTelepon("089876543210"));
    }

    @Test
    void testFindByNik() {
        Customer customer = createTestCustomer();
        customer.setNik("1234567890123456");
        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByNik("1234567890123456");
        assertTrue(found.isPresent());
        assertEquals("1234567890123456", found.get().getNik());
    }

    @Test
    void testExistsByNik() {
        Customer customer = createTestCustomer();
        customer.setNik("1234567890123456");
        entityManager.persistAndFlush(customer);

        assertTrue(customerRepository.existsByNik("1234567890123456"));
        assertFalse(customerRepository.existsByNik("6543210987654321"));
    }

    @Test
    void testCountTotalCustomers() {
        Customer customer1 = createTestCustomer();
        customer1.setEmail("test1@example.com");
        customer1.setNik("1234567890123456");
        customer1.setNomorTelepon("081234567890");
        
        Customer customer2 = createTestCustomer();
        customer2.setEmail("test2@example.com");
        customer2.setNik("6543210987654321");
        customer2.setNomorTelepon("089876543210");
        
        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        Long count = customerRepository.countTotalCustomers();
        assertEquals(2L, count);
    }

    @Test
    void testFindByNikAndEmail() {
        Customer customer = createTestCustomer();
        customer.setNik("1234567890123456");
        customer.setEmail("test@example.com");
        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByNikAndEmail("1234567890123456", "TEST@EXAMPLE.COM");
        assertTrue(found.isPresent());
    }

    @Test
    void testExistsByKodeRekening() {
        Customer customer = createTestCustomer();
        customer.setKodeRekening(12345);
        entityManager.persistAndFlush(customer);

        assertTrue(customerRepository.existsByKodeRekening(12345));
        assertFalse(customerRepository.existsByKodeRekening(99999));
    }

    @Test
    void testFindByKodeRekening() {
        Customer customer = createTestCustomer();
        customer.setKodeRekening(12345);
        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByKodeRekening(12345);
        assertTrue(found.isPresent());
        assertEquals(12345, found.get().getKodeRekening());
    }

    @Test
    void testCountByJenisKartu() {
        Customer customer1 = createTestCustomer();
        customer1.setEmail("test1@example.com");
        customer1.setNik("1234567890123456");
        customer1.setNomorTelepon("081234567890");
        customer1.setJenisKartu("Gold");
        
        Customer customer2 = createTestCustomer();
        customer2.setEmail("test2@example.com");
        customer2.setNik("6543210987654321");
        customer2.setNomorTelepon("089876543210");
        customer2.setJenisKartu("Gold");
        
        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        long count = customerRepository.countByJenisKartu("Gold");
        assertEquals(2, count);
    }

    @Test
    void testFindByJenisKartu() {
        Customer customer = createTestCustomer();
        customer.setJenisKartu("Platinum");
        entityManager.persistAndFlush(customer);

        List<Customer> customers = customerRepository.findByJenisKartu("Platinum");
        assertEquals(1, customers.size());
        assertEquals("Platinum", customers.get(0).getJenisKartu());
    }

    @Test
    void testExistsByNomorKartuDebitVirtual() {
        Customer customer = createTestCustomer();
        customer.setNomorKartuDebitVirtual("1234-5678-9012-3456");
        entityManager.persistAndFlush(customer);

        assertTrue(customerRepository.existsByNomorKartuDebitVirtual("1234-5678-9012-3456"));
        assertFalse(customerRepository.existsByNomorKartuDebitVirtual("9999-8888-7777-6666"));
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setNamaLengkap("Test User");
        customer.setNik("1234567890123456");
        customer.setNamaIbuKandung("Test Mother");
        customer.setNomorTelepon("081234567890");
        customer.setEmail("test@example.com");
        customer.setPassword("password123");
        customer.setTipeAkun("PERSONAL");
        customer.setJenisKartu("Silver");
        customer.setTempatLahir("Jakarta");
        customer.setTanggalLahir(LocalDate.of(1990, 1, 1));
        customer.setJenisKelamin("L");
        customer.setAgama("Islam");
        customer.setStatusPernikahan("Belum Menikah");
        customer.setPekerjaan("Software Engineer");
        customer.setSumberPenghasilan("Gaji");
        customer.setRentangGaji("5-10 juta");
        customer.setTujuanPembuatanRekening("Tabungan");
        
        Alamat alamat = new Alamat();
        alamat.setNamaAlamat("Jl. Test");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta");
        alamat.setKecamatan("Test");
        alamat.setKelurahan("Test");
        alamat.setKodePos("12345");
        customer.setAlamat(alamat);
        
        return customer;
    }
}
