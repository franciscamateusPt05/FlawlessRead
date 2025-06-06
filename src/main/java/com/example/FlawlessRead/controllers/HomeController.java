package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.Book;
import com.example.FlawlessRead.model.Questionnaire;
import com.example.FlawlessRead.model.User;
import com.example.FlawlessRead.repository.QuestionnaireRepository;
import com.example.FlawlessRead.repository.UserRepository;
import com.example.FlawlessRead.service.BookService;
import com.example.FlawlessRead.service.ReadingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
public class HomeController {

    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final ReadingService readingService;


    public HomeController(QuestionnaireRepository questionnaireRepository,
                          UserRepository userRepository,
                          BookService bookService, ReadingService readingService) {
        this.questionnaireRepository = questionnaireRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.readingService = readingService;
    }

    @GetMapping("/home")
    public String showHome(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();

        Optional<User> user = userRepository.findByUsername(username);

        Questionnaire questionnaire = questionnaireRepository.findByUser(user);

        String generosString = questionnaire.getGenerosPreferidos();
        List<String> generos = generosString == null || generosString.isEmpty()
                ? List.of()
                : List.of(generosString.split(","));

        // Buscar trending (ordenar por relevância) e novidades (ordenar por "new" ou data)
        List<Book> trendingBooks = bookService.fetchBooksByGenres(generos);
        if (trendingBooks.size() > 10 || trendingBooks.size() < 5) {
            trendingBooks = trendingBooks.subList(0, 10);
            model.addAttribute("trendingBooks", trendingBooks);
        }
        else{
            List<Book> trendingBook = bookService.fetchBooksByGenres(new ArrayList<>(List.of(generos.getFirst())));
            if(trendingBook.size() > 10){
                model.addAttribute("trendingBooks", trendingBook.subList(0, 10));
            }
            else{
                model.addAttribute("trendingBooks", trendingBook);
            }
        }


        List<Book> newBooks = bookService.fetchBooksByGenres(generos, "new");
        if (newBooks.size() > 10 || newBooks.size() < 5) {
            newBooks = newBooks.subList(0, 10);
            model.addAttribute("newBooks", newBooks);
        }
        else{
            List<Book> newBook = bookService.fetchBooksByGenres(new ArrayList<>(List.of(generos.getFirst())), "new");
            if (newBook.size() > 10) {
                model.addAttribute("newBooks", newBook.subList(0, 10));
            }
            else{model.addAttribute("newBooks", newBook);}
        }


        List<Book> books = new ArrayList<>(trendingBooks);
        books.addAll(newBooks);
        model.addAttribute("books", books);
        session.setAttribute("books", books);

        model.addAttribute("user", user);
        model.addAttribute("generos", generos);

        return "home";
    }

    @GetMapping("/testAuth")
    @ResponseBody
    public String testAuth(@AuthenticationPrincipal User user) {
        if (user == null) return "Usuário não autenticado";
        return "Usuário autenticado: " + user.getUsername();
    }

    @GetMapping("/stats")
    public String getStats(@AuthenticationPrincipal User user, Model model) throws JsonProcessingException {
        int totalRead = readingService.countBooksReadByUser(user.getId());
        int readingGoal = questionnaireRepository.findByUserId(user.getId())
                .map(Questionnaire::getGoalBook)
                .orElse(20);
        Map<String, Integer> readingHistory = readingService.getReadingHistory(user.getId());
        Map<String, Integer> booksByGenre = readingService.getBooksByGenreByUser(user.getId());


        // Converter para JSON
        ObjectMapper mapper = new ObjectMapper();

        model.addAttribute("totalRead", totalRead);
        model.addAttribute("readingGoal", readingGoal);
        model.addAttribute("readingHistoryJson", mapper.writeValueAsString(readingHistory));
        model.addAttribute("booksByGenreJson", mapper.writeValueAsString(booksByGenre));


        return "stats"; // template Thymeleaf
    }

    @GetMapping("/")
    public String landingPage() {
        return "landingpage";
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // ou página de erro, se preferir
        }

        User user = optionalUser.get();

        // Supondo que ReadingService tenha esses métodos:
        int booksRead = readingService.countBooksReadByUser(user.getId());


        model.addAttribute("user", user);
        model.addAttribute("booksRead", booksRead);


        return "profile";  // nome do template Thymeleaf (profile.html)
    }




}
