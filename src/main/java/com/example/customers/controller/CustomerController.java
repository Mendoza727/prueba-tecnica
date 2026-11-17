package com.example.customers.controller;

import com.example.customers.dto.CustomerRequest;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
        @Valid @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).
        body(customerService.createCustomer(request));
    }

    @GetMapping
    public List<CustomerResponse> findAllCustomers() {
        return customerService.getAllCustomers();
    }
}