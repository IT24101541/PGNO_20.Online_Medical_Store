package com.getmed.pgno20.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage() {
        return "home";  // home.html
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";  // contact.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";  // register.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // login.html
    }

    @GetMapping("/admin_home")
    public String adminHomePage() {
        return "admin_home"; // This should match admin_home.html in templates
    }

    @GetMapping("/customer_home")
    public String customerHomePage() {
        return "customer_home"; // This should match customer_home.html in templates
    }

}
