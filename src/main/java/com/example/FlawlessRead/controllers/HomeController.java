package com.example.FlawlessRead.controllers;


import com.example.FlawlessRead.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }





}
