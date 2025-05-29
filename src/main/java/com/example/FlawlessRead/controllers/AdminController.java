package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.service.BookFetcherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BookFetcherService bookFetcherService;

    public AdminController(BookFetcherService bookFetcherService) {
        this.bookFetcherService = bookFetcherService;
    }

    @GetMapping("/fetch-books")
    public String fetchBooks() {
        bookFetcherService.fetchBooksDaily();
        return "Livros buscados com sucesso!";
    }
}
