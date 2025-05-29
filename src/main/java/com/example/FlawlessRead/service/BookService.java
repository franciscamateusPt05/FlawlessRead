package com.example.FlawlessRead.service;

import com.example.FlawlessRead.repository.BookNewestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookNewestRepository bookRepository;


}