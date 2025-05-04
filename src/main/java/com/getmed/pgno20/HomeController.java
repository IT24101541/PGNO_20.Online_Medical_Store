package com.getmed.pgno20;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Home"; // This will look for templates/Home.html
    }
}


