package com.getmed.pgno20.repository;

import com.getmed.pgno20.model.Customer;
import java.util.List;

public interface CustomerRepository {
    Customer findByEmail(String email);
    void save(Customer customer);
    void deleteById(int id);
    List<Customer> findAll(); // Added method
}