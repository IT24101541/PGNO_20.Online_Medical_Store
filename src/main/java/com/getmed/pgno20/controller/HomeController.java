package com.getmed.pgno20.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/homepage")
    public String homePage() {
        return "home";  // home.html
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";  // contact.html
    }

    @GetMapping("/admin_home")
    public String adminHomePage() {
        return "admin_dashboard"; // This should match admin_home.html in templates
    }

    @GetMapping("/customer_home")
    public String customerHomePage() {
        return "customer_home"; // This should match customer_home.html in templates
    }

}
