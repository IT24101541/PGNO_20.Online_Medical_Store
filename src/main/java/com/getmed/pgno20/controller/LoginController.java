package com.getmed.pgno20.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("message", message);
        return "login"; // this refers to login.html in templates
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String role,
                        Model model) {

        String filename = "customer".equalsIgnoreCase(role) ? "customers.txt" : "admins.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[3].equals(email) && parts[4].equals(password)) {
                    return "customer".equalsIgnoreCase(role) ? "customer_home" : "admin_home";
                }
            }
        } catch (IOException e) {
            model.addAttribute("message", "Error reading file");
            return "login";
        }

        model.addAttribute("message", "Invalid email or password");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String role,
                        @RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        String resourcePath = role.equals("admin") ? "data/admins.txt" : "data/customers.txt";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
             Scanner scanner = new Scanner(inputStream)) {

            while (scanner.hasNextLine()) {
                String[] userData = scanner.nextLine().split(",");
                if (userData.length >= 5 && userData[3].equals(email) && userData[4].equals(password)) {
                    // Successful login
                    return role.equals("admin") ? "redirect:/admin_home" : "redirect:/customer_home";
                }
            }

            model.addAttribute("error", "Invalid email or password.");
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", "Unable to read user data.");
            return "login";
        }
    }


}
