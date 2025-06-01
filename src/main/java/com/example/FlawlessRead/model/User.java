package com.example.FlawlessRead.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @ManyToMany
    private Set<Book> wantToRead = new HashSet<>();

    @ManyToMany
    private Set<Book> alreadyRead = new HashSet<>();

    // Getters e Setters tradicionais

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // NÃO fazer encriptação aqui — só definir a senha já criptografada
    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Book> getWantToRead() {
        return wantToRead;
    }

    public void setWantToRead(Set<Book> wantToRead) {
        this.wantToRead = wantToRead;
    }

    public Set<Book> getAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(Set<Book> alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

    // Implementações dos métodos de UserDetails abaixo:

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por simplicidade, todos usuários tem ROLE_USER
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // ajustar conforme sua lógica
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ajustar conforme sua lógica
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // ajustar conforme sua lógica
    }

    @Override
    public boolean isEnabled() {
        return true; // ajustar conforme sua lógica
    }
}
