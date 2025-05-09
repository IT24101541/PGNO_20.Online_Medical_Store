package com.getmed.pgno20.service;

import com.getmed.pgno20.model.Customer;
import com.getmed.pgno20.model.User;
import com.getmed.pgno20.repository.FileBasedCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final FileBasedCustomerRepository customerRepository;

    @Autowired
    public CustomerService(FileBasedCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public boolean deleteCustomer(int id) {
        try {
            customerRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    public void saveUser(User user) {
        if ("customer".equalsIgnoreCase(user.getRole())) {
            // Convert User to Customer and save
            Customer customer = new Customer(
                    user.getName(),
                    user.getAddress(),
                    user.getContactNo(),
                    user.getEmail(),
                    user.getPassword()
            );
            customerRepository.save(customer);
        } else {
            System.err.println("Invalid role for CustomerService: " + user.getRole());
        }
    }

    public void saveUser(Customer customer) {
        customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}