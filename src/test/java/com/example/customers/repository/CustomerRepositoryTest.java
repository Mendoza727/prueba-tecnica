package com.example.customers.repository;

import com.example.customers.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void save_and_existsByEmailIgnoreCase() {
        Customer customer = Customer.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer saved = customerRepository.save(customer);

        assertThat(saved.getId()).isNotNull();
        assertThat(customerRepository.existsByEmailIgnoreCase("JOHN.DOE@EXAMPLE.COM")).isTrue();
        assertThat(customerRepository.findAll()).hasSize(1);
    }
}
