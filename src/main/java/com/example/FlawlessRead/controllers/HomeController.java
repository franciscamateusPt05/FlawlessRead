package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.BookNewest;
import com.example.FlawlessRead.model.BookTrending;
import com.example.FlawlessRead.model.User;
import com.example.FlawlessRead.repository.BookNewestRepository;
import com.example.FlawlessRead.repository.BookTrendingRepository;
import com.example.FlawlessRead.repository.QuestionnaireRepository;
import com.example.FlawlessRead.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final QuestionnaireRepository questionnaireRepository;
    private final BookTrendingRepository bookTrendingRepository;
    private final UserRepository userRepository;
    private final BookNewestRepository bookNewestRepository;

    public HomeController(QuestionnaireRepository questionnaireRepository,  BookTrendingRepository bookTrendingRepository, UserRepository userRepository, BookNewestRepository bookNewestRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.bookTrendingRepository = bookTrendingRepository;
        this.userRepository = userRepository;
        this.bookNewestRepository = bookNewestRepository;
    }

    @GetMapping("/")
    public String showHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Usa userDetails para identificar o user autenticado
        if (userDetails == null) {
            return "redirect:/login";  // fallback (não deveria chegar aqui porque o Security trata disso)
        }

        String username = userDetails.getUsername();

        // Busca o user e dados para o modelo, por exemplo o questionário
        // Exemplo de código, adapta ao teu repositório
        Optional<User> user = userRepository.findByUsername(username) ;
        var questionnaire = questionnaireRepository.findByUser(user);
        String genero = questionnaire.getGeneroFavorito();

        List<BookTrending> trendingBooks = (List<BookTrending>) bookTrendingRepository.findByGenero(genero);
        List<BookNewest> newBooks = (List<BookNewest>) bookNewestRepository.findByGenero(genero);



        model.addAttribute("user", user);
        model.addAttribute("trendingBooks", trendingBooks);
        model.addAttribute("newBooks", newBooks);
        model.addAttribute("genero", genero);

        return "home";
    }
}
