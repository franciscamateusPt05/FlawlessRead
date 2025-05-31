package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.Book;
import com.example.FlawlessRead.service.OpenLibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final OpenLibraryService openLibraryService;

    public SearchController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    // 1. Mostrar o formulário de pesquisa
    @GetMapping("/search")
    public String showSearchForm() {
        return "search"; // mostra o formulário (search.html)
    }

    // 2. Tratar a pesquisa com query enviada
    @GetMapping("/search/results")
    public String processSearch(@RequestParam("query") String query, Model model, HttpSession session) {
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/search";
        }

        List<Book> books = openLibraryService.searchBooks(query);
        session.setAttribute("books", books);  // guardar na sessão
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        return "results";
    }



}

