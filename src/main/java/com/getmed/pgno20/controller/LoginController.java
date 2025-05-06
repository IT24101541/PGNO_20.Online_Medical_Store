package com.getmed.pgno20.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Scanner;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("message", message);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String role,
                        @RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        String resourcePath = "data/" + (role.equalsIgnoreCase("admin") ? "admins.txt" : "customers.txt");

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
             Scanner scanner = new Scanner(inputStream)) {

            while (scanner.hasNextLine()) {
                String[] userData = scanner.nextLine().split(",");
                if (userData.length >= 5 && userData[3].equals(email) && userData[4].equals(password)) {
                    // ✅ Redirect to controller-mapped URL
                    return role.equalsIgnoreCase("admin") ? "redirect:/admin_home" : "redirect:/customer_home";
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
