package com.example.FlawlessRead.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommunityController {

    @GetMapping("/community")
    public String communityPage() {
        // Retorna apenas o nome do template HTML
        return "community";
    }
}
