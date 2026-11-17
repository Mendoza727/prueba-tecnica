package com.example.customers.repository;

import com.example.customers.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerRepository {
    private final List<Customer> customers = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        customers.add(customer);
        return customer;
    }

    public Optional<Customer> findById(Long id) {
        return customers.stream()
            .filter(customer -> customer.getId().equals(id))
            .findFirst();
    }

    public List<Customer> findAll() {
        return List.copyOf(customers);
    }

    public boolean existsByEmail(String email) {
        return customers.stream().anyMatch(customer ->
        customer.getEmail().equalsIgnoreCase(email));
    }

    public void deleteById(Long id) {
        customers.removeIf(customer -> customer.getId().equals(id));
    }
}