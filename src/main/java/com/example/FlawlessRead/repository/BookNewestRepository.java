package com.example.FlawlessRead.repository;

import com.example.FlawlessRead.model.BookNewest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookNewestRepository extends JpaRepository<BookNewest, Long> {
    boolean existsByIsbn(String isbn);

    List<BookNewest> findByGenero(String genero);

}
