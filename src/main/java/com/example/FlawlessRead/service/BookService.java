package com.example.FlawlessRead.service;

import com.example.FlawlessRead.model.Book;
import com.example.FlawlessRead.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<com.example.FlawlessRead.model.Book> getFeaturedBooks() {
        return bookRepository.findTop6ByOrderByIdDesc();
    }

    public List<com.example.FlawlessRead.model.Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }
}