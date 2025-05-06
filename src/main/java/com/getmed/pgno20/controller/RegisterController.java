package com.getmed.pgno20.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;// import the file class
import java.io.FileWriter;// writing the file
import java.io.IOException;// handle errors

@Controller
public class RegisterController {

    private final String dataDir;{
        dataDir = "data";
    }

    public RegisterController() {
        // Ensure data directory exists
        new File(dataDir).mkdirs();
    }

    @GetMapping("/customer_register")
    public String showCustomerRegisterForm() {
        return "customer_register";
    }

    @PostMapping("/customer_register")
    public String registerCustomer(@RequestParam String name,
                                   @RequestParam String address,
                                   @RequestParam String contactNo,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   Model model) {
        try (FileWriter fw = new FileWriter(dataDir + "/customers.txt", true)) {
            fw.write(name + "," + address + "," + contactNo + "," + email + "," + password + "\n");
            return "redirect:/login?message=Registered+Successfully.+Please+log+in+using+your+email+and+password.";
        } catch (IOException e) {
            model.addAttribute("message", "Error writing to customer file");
            return "customer_register";
        }
    }

    @GetMapping("/admin_register")
    public String showAdminRegisterForm() {
        return "admin_register";
    }

    @PostMapping("/admin_register")
    public String registerAdmin(@RequestParam String name,
                                @RequestParam String address,
                                @RequestParam String contactNo,
                                @RequestParam String email,
                                @RequestParam String password,
                                Model model) {
        try (FileWriter fw = new FileWriter(dataDir + "/admins.txt", true)) {
            fw.write(name + "," + address + "," + contactNo + "," + email + "," + password + "\n");
            return "redirect:/login?message=Registered+Successfully.+Please+log+in+using+your+email+and+password.";
        } catch (IOException e) {
            model.addAttribute("message", "Error writing to admin file");
            return "admin_register";
        }
    }
}
