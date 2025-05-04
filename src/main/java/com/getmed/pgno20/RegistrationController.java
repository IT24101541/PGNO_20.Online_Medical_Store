package com.getmed.pgno20;

import com.getmed.pgno20.model.Customer;
import com.getmed.pgno20.model.Admin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;

@Controller
public class RegistrationController {

    private final String customerFilePath = System.getProperty("user.dir") + "/data/customers.txt";
    private final String adminFilePath = System.getProperty("user.dir") + "/data/admins.txt";

    @PostMapping("/register/customer")
    public RedirectView registerCustomer(@ModelAttribute Customer customer) {
        try {
            File file = new File(customerFilePath);
            file.getParentFile().mkdirs(); // Ensure /data directory exists
            file.createNewFile(); // Create a file if it doesn't exist

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(String.format("%s,%s,%s,%s,%s%n",
                        customer.getName(), customer.getAddress(), customer.getContactNo(), customer.getEmail(), customer.getPassword()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RedirectView("/Home.html");
    }

    @PostMapping("/register/admin")
    public RedirectView registerAdmin(@ModelAttribute Admin admin) {
        try {
            File file = new File(adminFilePath);
            file.getParentFile().mkdirs(); // Ensure /data directory exists
            file.createNewFile(); // Create a file if it doesn't exist

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(String.format("%s,%s,%s,%s,%s%n",
                        admin.getName(), admin.getAddress(), admin.getContactNo(), admin.getEmail(), admin.getPassword()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RedirectView("/Home.html");
    }
}
