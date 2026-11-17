package com.example.customers.service;

import com.example.customers.dto.CustomerRequest;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

   @Transactional
   public CustomerResponse createCustomer(CustomerRequest request) {
    if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    Customer customer = Customer.builder()
        .name(request.getName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
    .build();
    Customer saved = customerRepository.save(customer);
    return toResponse(saved);
   }

   @Transactional(readOnly = true)
   public List<CustomerResponse> getAllCustomers() {
    return customerRepository.findAll().stream().map(
        this::toResponse
    ).toList();
   }

   private CustomerResponse toResponse(Customer customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        Date.from(customer.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()),
        Date.from(customer.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant())
    );
   }
}