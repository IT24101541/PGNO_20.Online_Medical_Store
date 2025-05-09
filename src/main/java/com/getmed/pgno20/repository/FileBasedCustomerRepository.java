package com.getmed.pgno20.repository;

import com.getmed.pgno20.model.Customer;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileBasedCustomerRepository implements CustomerRepository {

    private static final String CUSTOMER_FILE_PATH = "src/main/resources/data/customers.txt";
    private List<Customer> customers;

    public FileBasedCustomerRepository() {
        this.customers = loadCustomersFromFile();
    }

    @Override
    public Customer findByEmail(String email) {
        this.customers = loadCustomersFromFile();
        return customers.stream().filter(c -> c.getEmail().equals(email)).findFirst().orElse(null);
    }

    @Override
    public void save(Customer customer) {
        // Reload customers to ensure we have the latest data
        this.customers = loadCustomersFromFile();

        // Log current customers and their IDs
        System.out.println("Current customers before save: " + customers.size());
        customers.forEach(c -> System.out.println("Customer ID: " + c.getId() + ", Email: " + c.getEmail()));

        // Remove existing customer with the same email (if any)
        customers.removeIf(c -> c.getEmail().equals(customer.getEmail()));

        // Assign a new ID if the customer doesn't have one
        if (customer.getId() == 0) {
            int maxId = customers.stream().mapToInt(Customer::getId).max().orElse(0);
            int newId = maxId + 1;
            customer.setId(newId);
            System.out.println("Assigned new ID: " + newId + " to customer with email: " + customer.getEmail());
        }

        // Add the new customer
        customers.add(customer);

        // Log updated customers
        System.out.println("Current customers after save: " + customers.size());
        customers.forEach(c -> System.out.println("Customer ID: " + c.getId() + ", Email: " + c.getEmail()));

        // Save the updated list to the file
        saveCustomersToFile();
    }

    @Override
    public void deleteById(int id) {
        this.customers = loadCustomersFromFile();
        customers.removeIf(c -> c.getId() == id);
        saveCustomersToFile();
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(loadCustomersFromFile()); // Return a new list to avoid modifying the cached list
    }

    private List<Customer> loadCustomersFromFile() {
        List<Customer> loadedCustomers = new ArrayList<>();
        File file = new File(CUSTOMER_FILE_PATH);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
            return loadedCustomers;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) { // Expect 6 fields: id,name,address,contactNo,email,password
                    try {
                        int id = Integer.parseInt(data[0].trim());
                        Customer customer = new Customer();
                        customer.setId(id); // Set ID first
                        customer.setName(data[1].trim());
                        customer.setAddress(data[2].trim());
                        customer.setContactNo(data[3].trim());
                        customer.setEmail(data[4].trim());
                        customer.setPassword(data[5].trim());
                        loadedCustomers.add(customer);
                        System.out.println("Loaded customer with ID: " + id + ", Email: " + customer.getEmail());
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing customer ID: " + data[0] + ". Skipping line.");
                    }
                } else {
                    System.err.println("Invalid customer data format: " + line + ". Expected 6 fields, found " + data.length);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading customer data: " + e.getMessage());
        }
        return loadedCustomers;
    }

    private void saveCustomersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE_PATH))) {
            for (Customer customer : customers) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s%n",
                        customer.getId(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getContactNo(),
                        customer.getEmail(),
                        customer.getPassword()));
            }
        } catch (IOException e) {
            System.err.println("Error writing customer data: " + e.getMessage());
        }
    }
}