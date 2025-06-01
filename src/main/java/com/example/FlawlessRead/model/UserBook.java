package com.example.FlawlessRead.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.logging.Formatter;

@Entity
public class UserBook {

    @EmbeddedId
    private UserBookId id = new UserBookId();

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("bookId")
    private Book book;

    private LocalDate dataAdicionado; // ou dataLeitura

    // construtores, getters e setters


    public UserBook(UserBookId id, User user, Book book, LocalDate dataAdicionado) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.dataAdicionado = dataAdicionado;
    }

    public UserBook() {

    }

    public UserBookId getId() {
        return id;
    }

    public void setId(UserBookId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getDataAdicionado() {
        return dataAdicionado;
    }

    public void setDataAdicionado(LocalDate dataAdicionado) {
        this.dataAdicionado = dataAdicionado;
    }


}

