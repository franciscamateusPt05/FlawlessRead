package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.Book;
import com.example.FlawlessRead.service.OpenLibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final OpenLibraryService openLibraryService;

    public BookController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/{index}")
    public String getBookDetails(@PathVariable int index, Model model, HttpSession session) {
        List<Book> books = (List<Book>) session.getAttribute("books");

        if (books != null && index >= 0 && index < books.size()) {
            Book selectedBook = books.get(index);

            // Buscar a descrição da API, usando a chave do livro
            if (selectedBook.getKey() != null) {
                String descricao = openLibraryService.getBookDescription(selectedBook.getKey());
                model.addAttribute("descricao", descricao);

            }
            else{
                model.addAttribute("descricao", null);
            }

            model.addAttribute("book", selectedBook);

            return "book-details";
        } else {
            return "redirect:/search";
        }
    }




}
