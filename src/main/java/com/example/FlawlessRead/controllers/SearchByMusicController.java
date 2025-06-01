package com.example.FlawlessRead.controllers;


import com.example.FlawlessRead.model.Book;
import com.example.FlawlessRead.service.SearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/searchByMusic")
public class SearchByMusicController {

    private final SearchService searchService;

    @Autowired
    public SearchByMusicController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public String showSearchPage() {
        return "searchByMusic";
    }

    @PostMapping
    public String searchBooksByMusic(@RequestParam("trackName") String trackName,
                                     @RequestParam("artistName") String artistName,
                                     Model model,
                                     HttpSession session) {
        try {
            String[] ids = searchService.searchTrackAndArtistId(trackName, artistName).block(); // bloqueia o Mono
            if (ids == null) {
                model.addAttribute("error", "Música não encontrada.");
                return "results";
            }
            List<Book> books = searchService.processMusicToBooks(ids[0], ids[1]).block(); // bloqueia também
            model.addAttribute("books", books);
            session.setAttribute("books", books); // salva na sessão
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao processar: " + e.getMessage());
        }
        return "results";
    }




}

