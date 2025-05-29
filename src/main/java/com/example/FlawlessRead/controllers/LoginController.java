package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.User;
import com.example.FlawlessRead.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // Procura por username ou email
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByEmail(username); // login com email também
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verifica se a password bate certo com a encriptada
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user); // guarda na sessão
                return "redirect:/"; // vai para home
            }
        }

        model.addAttribute("error", "Username ou password inválidos.");
        return "login"; // volta para o login com erro
    }
}

