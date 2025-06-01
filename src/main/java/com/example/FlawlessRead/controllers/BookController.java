package com.example.FlawlessRead.controllers;

import com.example.FlawlessRead.model.*;
import com.example.FlawlessRead.repository.ReviewRepository;
import com.example.FlawlessRead.repository.UserBookRepository;
import com.example.FlawlessRead.repository.UserRepository;
import com.example.FlawlessRead.service.BookService;
import com.example.FlawlessRead.service.OpenLibraryService;
import com.example.FlawlessRead.service.ReviewService;
import com.example.FlawlessRead.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/books")
public class BookController {

    private final OpenLibraryService openLibraryService;
    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserService userService; //
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private UserRepository userRepository;



    public BookController(OpenLibraryService openLibraryService, BookService bookService, ReviewService reviewService, UserService userService) {
        this.openLibraryService = openLibraryService;
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/{index}")
    public String getBookDetails(@PathVariable int index, Model model, HttpSession session) {
        List<Book> books = (List<Book>) session.getAttribute("books");

        if (books != null && index >= 0 && index < books.size()) {
            Book selectedBook = books.get(index);

            if (selectedBook.getKey() != null) {
                String descricao = openLibraryService.getBookDescription(selectedBook.getKey());
                model.addAttribute("descricao", descricao);
            } else {
                model.addAttribute("descricao", null);
            }
            if (bookService.findByIsbn(selectedBook.getIsbn()) != null) {
                Book book = bookService.findByIsbn(selectedBook.getIsbn());
                List<Review> reviews = reviewRepository.findByBookId(book.getId());
                model.addAttribute("reviews", reviews);
            }else {
                List<Review> reviews = null;
                model.addAttribute("reviews", reviews);
            }

            model.addAttribute("book", selectedBook);
            model.addAttribute("index", index);
            model.addAttribute("book", selectedBook);
            model.addAttribute("index", index);

            return "book-details";
        } else {
            return "redirect:/search";
        }
    }

    // Endpoints para alterar status do livro para o usuário logado

    @PostMapping("/{index}/wantToRead")
    public ResponseEntity<?> markWantToRead(
            @AuthenticationPrincipal User user,
            @PathVariable Integer index,
            HttpSession session) {

        if (user == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        List<Book> books = (List<Book>) session.getAttribute("books");
        if (books == null || index < 0 || index >= books.size()) {
            return ResponseEntity.badRequest().body("Índice do livro inválido");
        }

        Book sessionBook = books.get(index);
        if (sessionBook.getIsbn() == null) {
            return ResponseEntity.badRequest().body("Livro sem ISBN");
        }

        User sessionUser = userService.findByUsername(user.getUsername());

        Book book = bookService.findByIsbn(sessionBook.getIsbn());
        if (book == null) {
            book = new Book();
            book.setIsbn(sessionBook.getIsbn());
            book.setTitulo(sessionBook.getTitulo());
            book.setAutor(sessionBook.getAutor());
            book.setCapaUrl(sessionBook.getCapaUrl());
            book.setGenero(sessionBook.getGenero());
            book = bookService.save(book);
        }

        if (!sessionUser.getWantToRead().contains(book)) {
            sessionUser.getWantToRead().add(book);
            userService.save(sessionUser);
        }

        return ResponseEntity.ok("Livro marcado como Quero Ler");
    }

    @PostMapping("/{index}/alreadyRead")
    public ResponseEntity<?> markAlreadyRead(
            @AuthenticationPrincipal User user,
            @PathVariable Integer index,
            HttpSession session) {

        if (user == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        List<Book> books = (List<Book>) session.getAttribute("books");
        if (books == null || index < 0 || index >= books.size()) {
            return ResponseEntity.badRequest().body("Índice do livro inválido");
        }

        Book sessionBook = books.get(index);
        if (sessionBook.getIsbn() == null) {
            return ResponseEntity.badRequest().body("Livro sem ISBN");
        }

        Book book = bookService.findByIsbn(sessionBook.getIsbn());
        if (book == null) {
            book = new Book();
            book.setIsbn(sessionBook.getIsbn());
            book.setTitulo(sessionBook.getTitulo());
            book.setAutor(sessionBook.getAutor());
            book.setCapaUrl(sessionBook.getCapaUrl());
            book.setGenero(sessionBook.getGenero());
            book = bookService.save(book);
        }

        User sessionUser = userService.findByUsername(user.getUsername());

        final Long bookId = book.getId();

        boolean alreadyRead = sessionUser.getAlreadyRead().stream()
                .anyMatch(ub -> ub.getBook().getId().equals(bookId));


        if (!alreadyRead) {
            UserBookId id = new UserBookId(sessionUser.getId(), book.getId());

            // Verifique se já existe UserBook com esse ID no banco (pode usar o repositório)
            if (!userBookRepository.existsById(id)) {
                UserBook userBook = new UserBook(id, sessionUser, book, LocalDate.now());
                sessionUser.getAlreadyRead().add(userBook);

                userRepository.save(sessionUser);  // Cascade salva o UserBook
            }
        }

        return ResponseEntity.ok("Livro marcado como já lido");
    }


    @PostMapping("/{index}/review")
    public ResponseEntity<?> addReview(
            @AuthenticationPrincipal User user,
            @PathVariable Integer index,
            @RequestBody Review reviewDTO,
            HttpSession session) {

        if (user == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        List<Book> books = (List<Book>) session.getAttribute("books");
        if (books == null || index < 0 || index >= books.size()) {
            return ResponseEntity.badRequest().body("Índice do livro inválido");
        }

        Book sessionBook = books.get(index);
        if (sessionBook.getIsbn() == null) {
            return ResponseEntity.badRequest().body("Livro sem ISBN");
        }

        Book book = bookService.findByIsbn(sessionBook.getIsbn());
        if (book == null) {
            book = new Book();
            book.setIsbn(sessionBook.getIsbn());
            book.setTitulo(sessionBook.getTitulo());
            book.setAutor(sessionBook.getAutor());
            book.setCapaUrl(sessionBook.getCapaUrl());
            book.setGenero(sessionBook.getGenero());
            book = bookService.save(book);
        }

        User sessionUser = userService.findByUsername(user.getUsername());

        Review review = new Review();
        review.setUser(sessionUser);
        review.setBook(book);
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());

        reviewService.save(review);

        return ResponseEntity.ok("Review adicionada com sucesso");
    }

    // GET para AlreadyRead
    @GetMapping("/alreadyRead")
    public ResponseEntity<Set<UserBook>> getAlreadyRead(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user.getAlreadyRead());
    }

    // GET para WantToRead
    @GetMapping("/wantToRead")
    public ResponseEntity<Set<Book>> getWantToRead(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user.getWantToRead());
    }

}
