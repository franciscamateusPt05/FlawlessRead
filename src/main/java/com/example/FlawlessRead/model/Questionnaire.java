package com.example.FlawlessRead.model;

import jakarta.persistence.*;

@Entity
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    // Guarda os géneros preferidos separados por vírgula (simplificação)
    private String generosPreferidos;

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

    public String getGenerosPreferidos() {
        return generosPreferidos;
    }

    public void setGenerosPreferidos(String generosPreferidos) {
        this.generosPreferidos = generosPreferidos;
    }
}
