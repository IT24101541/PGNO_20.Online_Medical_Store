package com.getmedplus.controller;

import com.getmedplus.model.User;
import com.getmedplus.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    private final FileService fileService = new FileService();

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password, Model model) {
        User user = new User(name, email, password);
        try {
            fileService.saveUser(user.toFileString());
            model.addAttribute("message", "User registered successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "register";
    }

    @GetMapping("/users")
    public String getUsers(Model model) throws IOException {
        List<String> users = fileService.readUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
}
