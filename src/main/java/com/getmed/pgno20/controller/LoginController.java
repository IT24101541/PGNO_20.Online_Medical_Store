package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Admin;
import com.getmed.pgno20.model.Customer;
import com.getmed.pgno20.service.AdminService;
import com.getmed.pgno20.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AdminService adminService;
    private final CustomerService customerService;

    @Autowired
    public LoginController(AdminService adminService, CustomerService customerService) {
        this.adminService = adminService;
        this.customerService = customerService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("message", null);
        model.addAttribute("error", null);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, @RequestParam String role, HttpSession session, Model model) {
        boolean isAuthenticated = false;

        if ("admin".equalsIgnoreCase(role)) {
            Admin admin = adminService.getAdminByEmail(email);
            if (admin != null && admin.getPassword().equals(password)) {
                isAuthenticated = true;
                session.setAttribute("loggedInAdmin", admin); // Set loggedInAdmin with the Admin object
                session.setAttribute("role", "admin");
                return "redirect:/admin/dashboard";
            }
        } else if ("customer".equalsIgnoreCase(role)) {
            Customer customer = customerService.getCustomerByEmail(email);
            if (customer != null && customer.getPassword().equals(password)) {
                isAuthenticated = true;
                session.setAttribute("loggedInUser", customer.getEmail()); // Keep loggedInUser for customers
                session.setAttribute("role", "customer");
                return "redirect:/customer/dashboard";
            }
        }

        if (!isAuthenticated) {
            model.addAttribute("error", "Invalid email, password, or role.");
            return "login";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}