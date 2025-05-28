package com.example.FlawlessRead.model;

import jakarta.persistence.*;

@Entity
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento ManyToOne com User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // FK para User
    private User user;

    private String generoFavorito;
    private String formatoPreferido;
    // outros campos...

    // Getters e setters
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGeneroFavorito() {
        return generoFavorito;
    }

    public void setGeneroFavorito(String generoFavorito) {
        this.generoFavorito = generoFavorito;
    }

    public String getFormatoPreferido() {
        return formatoPreferido;
    }

    public void setFormatoPreferido(String formatoPreferido) {
        this.formatoPreferido = formatoPreferido;
    }
}
