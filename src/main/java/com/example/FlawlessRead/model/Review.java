package com.example.FlawlessRead.model;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BooksRead book;

    @ManyToOne
    private User user;

    private int rating;
    private String comment;

    // Getters e Setters
}
