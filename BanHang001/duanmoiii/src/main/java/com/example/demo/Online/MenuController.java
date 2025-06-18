package com.example.demo.Online;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {
    @GetMapping("/menu")
    public String showMenu() {
        return "menutrangchu/menu"; // Tên file menu.html trong templates
    }
}