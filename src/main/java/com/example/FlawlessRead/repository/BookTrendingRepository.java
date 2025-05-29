package com.example.FlawlessRead.repository;

import com.example.FlawlessRead.model.BookNewest;
import com.example.FlawlessRead.model.BookTrending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookTrendingRepository extends JpaRepository<BookTrending, Long> {
    boolean existsByIsbn(String isbn);


    List<BookTrending> findByGenero(String genero);
}
