package com.example.FlawlessRead.repository;

import com.example.FlawlessRead.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findTop6ByOrderByIdDesc();
    List<Book> findAll();
}