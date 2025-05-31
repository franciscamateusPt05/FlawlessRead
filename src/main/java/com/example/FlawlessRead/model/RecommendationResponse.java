
package com.example.FlawlessRead.model;

import java.util.List;
import java.util.Map;

public class RecommendationResponse {
    private List<String> genres;
    private List<Book> booksByGenre;

    // Getters e Setters
    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    public List<Book> getBooksByGenre() {
        return booksByGenre;
    }
    public void setBooksByGenre(List<Book> booksByGenre) {
        this.booksByGenre = booksByGenre;
    }
}

